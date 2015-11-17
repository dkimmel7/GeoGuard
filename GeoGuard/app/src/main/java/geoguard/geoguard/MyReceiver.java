package geoguard.geoguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log; // Checking

/**
 * Created by dnalex on 11/16/2015.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start service on boot up
        Intent service = new Intent(context, TestService.class);
        context.startService(service);
        Log.d("Service", "Service started");

        // Start App on Boot start up
        Intent App = new Intent(context, Location.class);
        context.startService(App);

    }
}
