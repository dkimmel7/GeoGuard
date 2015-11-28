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

    // GPS
    Tracker gps;
    private static final int METERS_SET = 50;
    private int NOTIFY_COUNT = 0;
    private boolean WITHIN_RANGE = false;
    private double DB_LONG = 36.975952;
    private double DB_LAT = -122.05534399999999;

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

        // Check if user is within radius of tagged location
        gpsCheck();

        // Issue Notification if new password found or if left area
        if (WITHIN_RANGE == true) {
            notification(); // Commented out to avoid clutter of msgs when testing on master
        }

        // Keeps the service running in the background
        return super.onStartCommand(intent, flags, startID); //START_STICKY;
    }


    /* Check current coordinates to tagged GeoLocation */
    public void gpsCheck() {
        gps = new Tracker(NotifyService.this);

        // Recounts passwords found within range of location tagged
        int recount = 0;

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            WITHIN_RANGE = false; // Redundant, but used again in case

            // ** DB_LAT and DB_LAT - ALL TAGGED LOCATIONS
            // ** WILL END UP AS FOR-LOOP **
            // Check if current location is within password tagged location
            if (gps.radius(DB_LAT, DB_LONG) <= METERS_SET) { // Will need to do this for multiple tagged coordinates in db
                recount++;
            }
            // Check if the number of passwords found in current location changed
            if (recount == NOTIFY_COUNT) {
                // No new location found, no need to update notification
                WITHIN_RANGE = false;
            } else {
                // New password found, or user left area so password count decreases
                NOTIFY_COUNT = recount;
                WITHIN_RANGE = true;
            }
        } else {
            // Display alert to turn on GPS // Put this on main screen and login screen
            gps.showSettingsAlert();
        }
    }


    /* Creates/Updates notification with certain attributes */
    public void notification() {

        boolean cancelNotification = false; // Cancel notification if area has been left

        if(NOTIFY_COUNT > 0) {
            // Build the notification
            notification.setSmallIcon(R.drawable.notification_template_icon_bg);
            notification.setTicker("Ticker");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("GeoGuard Password Found at Location");
            notification.setContentText("You have" + NOTIFY_COUNT + "Password Found Within" + METERS_SET + "Meters of your Location");

            Intent intent = new Intent(this, Location.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            // Builds notification and issues it
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(uniqueID, notification.build());
        } else {
            // Cancel notification if notification unclicked and radius is left
            // NOTIFY_COUNT should be 0
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(uniqueID);

        }
    }

}
