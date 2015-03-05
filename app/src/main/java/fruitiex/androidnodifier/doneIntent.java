package fruitiex.androidnodifier;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by rasse on 3/5/15.
 */
public class doneIntent extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = savedInstanceState.getString("uid");


    }
}
