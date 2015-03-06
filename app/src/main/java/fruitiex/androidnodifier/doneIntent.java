package fruitiex.androidnodifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rasse on 3/5/15.
 */
public class doneIntent extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();
        String uid = intent.getStringExtra("uid");

        Log.i("nodifier", "doneIntent() tapped, using uid " + uid);

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid", uid);
            new doHttpPost().execute("setRead", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
