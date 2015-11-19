package geoguard.geoguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by dnalex on 11/18/2015.
 */

public class AlarmReceiver extends BroadcastReceiver {
    // Restart service in 10 second intervals
    private static final long REPEAT_TIME = 1000 * 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Service", "Alarm Set");

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MyReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // start 5 seconds after boot completed
        cal.add(Calendar.SECOND, 5);
        // fetch every 10 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);

        // service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
        // REPEAT_TIME, pending);
    }
}
