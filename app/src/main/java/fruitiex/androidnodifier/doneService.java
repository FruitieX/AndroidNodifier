package fruitiex.androidnodifier;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rasse on 3/5/15.
 */
public class doneService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String uid = intent.getStringExtra("uid");

        Log.i("nodifier", "doneIntent() tapped, using uid " + uid);

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid", uid);
            new doHttpPost().execute("setRead", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
