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
    // Restart service in 30 second intervals
    private static final long REPEAT = 1000 * 30;

    /* Called on boot. Sets an alarm that will call a receiver
     * in certain set timed intervals.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Service", "Alarm Set");

        // Set up Alarm service
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent for receiver class; starts receiver initiating main service (GPS, notification)
        Intent i = new Intent(context, NotifyReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT); // maybe update current

        // Alarm initiates 5 seconds after initial boot
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis()); // added
        cal.add(Calendar.SECOND, 5);

        // Schedules exact repeat of alarm
        service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT, pending); //cal.getTimeInMillis()


        // Optimizes energy consumption; schedules alarm with inexact trigger req
        // service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT, pending);

    }
}
