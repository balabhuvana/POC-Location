package casperon.com.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bala on 11/9/16.
 */
public class MyReceiver extends BroadcastReceiver {

    public MyReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.networkTask();
    }
}