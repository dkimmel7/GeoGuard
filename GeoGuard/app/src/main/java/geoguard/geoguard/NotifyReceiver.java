package geoguard.geoguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dnalex on 11/16/2015.
 */
public class NotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Start service on boot up
        Intent service = new Intent(context, NotifyService.class);

        Log.d("Service", "Notification Service Started");

        context.startService(service);

    }
}
