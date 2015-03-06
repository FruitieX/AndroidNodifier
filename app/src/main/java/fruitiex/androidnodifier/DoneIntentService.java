package fruitiex.androidnodifier;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DoneIntentService extends IntentService {
    public DoneIntentService() {
        super("doneIntentService");
    }
    @Override
    public void onHandleIntent(Intent intent) {
        String uid = intent.getStringExtra("uid");

        Log.i("nodifier", "doneIntent() tapped, using uid " + uid);

        // dismiss notification
        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(uid.hashCode());

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid", uid);
            new DoHttpPost().execute("setRead", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DoneBroadcastReceiver.completeWakefulIntent(intent);
    }
}
