package fruitiex.androidnodifier;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rasse on 3/5/15.
 */
public class DoneIntentService extends IntentService {
    public DoneIntentService() {
        super("doneIntentService");
    }
    @Override
    public void onHandleIntent(Intent intent) {
        String uid = intent.getStringExtra("uid");

        Log.i("nodifier", "doneIntent() tapped, using uid " + uid);

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
