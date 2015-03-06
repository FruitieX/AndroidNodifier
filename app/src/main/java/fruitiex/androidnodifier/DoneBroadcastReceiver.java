package fruitiex.androidnodifier;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class DoneBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent service = new Intent(context, DoneIntentService.class);
        startWakefulService(context, service);
    }
}
