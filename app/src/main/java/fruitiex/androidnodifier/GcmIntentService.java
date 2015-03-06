package fruitiex.androidnodifier;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    static final String TAG = "nodifier";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                String method = extras.getString("method", null);
                if(method == null) {
                    return;
                }

                if(method.equals("newNotification")) {
                    String source = extras.getString("source", "Nodifier");
                    String context = extras.getString("context", "");
                    String text = extras.getString("text", "");
                    String uid = extras.getString("uid", "");
                    sendNotification(source, context, text, uid);
                } else if(method.equals("markAs")) {
                    String read = extras.getString("read", "true");
                    String uid = extras.getString("uid", "");
                    if(read.equals("false")) {
                        // marking as unread is same as creating new notification
                        String source = extras.getString("source", "Nodifier");
                        String context = extras.getString("context", "");
                        String text = extras.getString("text", "");
                        sendNotification(source, context, text, uid);
                    } else {
                        // dismiss notification if it was marked as read
                        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.cancel(uid.hashCode());
                    }
                } else {
                    Log.e(TAG, "unknown notification method");
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String source, String context, String text, String uid) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Intent doneIntent = new Intent(this, DoneIntentService.class);
        doneIntent.putExtra("uid", uid);
        Log.i(TAG, "storing uid " + uid);
        PendingIntent pendingIntentDone = PendingIntent.getService(this, uid.hashCode(), doneIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(source + (context.length() != 0 ? " (" + context + ")" : ""))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .addAction(android.R.drawable.checkbox_on_background, "Done", pendingIntentDone)
                        .setContentText(text);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(uid.hashCode(), mBuilder.build());
    }
}
