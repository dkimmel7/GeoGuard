package geoguard.geoguard;

import android.app.NotificationManager;             // for notification
import android.app.PendingIntent;                   // for notification
import android.content.Intent;
import android.support.v4.app.NotificationCompat;   // for notification

import android.app.Service;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dnalex on 11/16/2015.
 */
public class NotifyService extends Service {

    // Notification
    NotificationCompat.Builder notification;
    private static final int uniqueID = 123456;


    @Override
    public IBinder onBind(Intent intent) {
        // throw new UnsupportedOperationException("Not implemented");
        return null;
    }

    /* Called when the Service object is instantiated or when service is created
     * Events in this method occur only once
     */
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();  // Debugging
        Log.d("Service", "Created");                                        // Debugging
        super.onCreate();

        // Builds single notification
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }

    /* System calls this when service is removed. Should not be called directly.
     * Resources used by service are freed when it is destroyed.
     */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();    // Debugging
        super.onDestroy();
    }

    /* Called every time a client starts the service using startService(Intent, intent)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Debugging
        Toast.makeText(getApplicationContext(), "Service Working", Toast.LENGTH_LONG).show();
        Log.d("Service", "Working");

        // Issue Notification
        notification();

        // Keeps the service running in the background
        return START_STICKY;
    }


    /* Creates notification with certain attributes */
    public void notification() {
        // Build the notification
        notification.setSmallIcon(R.drawable.notification_template_icon_bg);
        notification.setTicker("This is a ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Thisi is the title.");
        notification.setContentText("Body text of notification");

        Intent intent = new Intent(this, Location.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        // Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

}
