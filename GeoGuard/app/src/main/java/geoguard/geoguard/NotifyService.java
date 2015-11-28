package geoguard.geoguard;

import android.app.NotificationManager;             // for notification
import android.app.PendingIntent;                   // for notification
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;   // for notification

import android.app.Service;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

    // Database
    LocalDB db = new LocalDB(NotifyService.this);

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
        if (WITHIN_RANGE) {
            notification(); // Commented out to avoid clutter of msgs when testing on master
        }

        // Keeps the service running in the background
        return super.onStartCommand(intent, flags, startID); //START_STICKY;
    }


    /* Check current coordinates to tagged GeoLocation */
    public void gpsCheck() {
        gps = new Tracker(NotifyService.this);

        // Passwords within range unknown from start of iteration
        WITHIN_RANGE = false;
        double DB_LONGITUDE;
        double DB_LATITUDE;

        // Recounts passwords found within range of location tagged
        int recount = 0;
        // For all locations tagged in database, entry[0] is location
        ArrayList<String[]> entries = new ArrayList<>();
        entries = db.getAllPasswords();

        // Get current locations
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Query all locations in database
            for(String[] entry : entries) {
                double loc[] = stringToDoublePair(entry[0]);
                DB_LATITUDE = loc[0];
                DB_LONGITUDE = loc[1];

                Log.d("Entry Check:", entry[0]);

                // Check if current location is within radius of tagged location
                if (gps.radius(DB_LATITUDE, DB_LONGITUDE) <= METERS_SET) {
                    recount++; // Increment count for password found
                }
            }

            // Check if the number of passwords found in current location changed
            // No new location found, no need to update notification
            if (recount == NOTIFY_COUNT) {
                WITHIN_RANGE = false;   // Flag false evades notification issuing

                // New password found - increasing NOTIFY_COUNT
                // Or User left area -  decreasing NOTIFY_COUNT
            } else {
                NOTIFY_COUNT = recount;
                WITHIN_RANGE = true;    // Flag true initiates notification issue/mod
            }

            // Display alert to turn on GPS // Put this on main screen and login screen
        } else {
            gps.showSettingsAlert();
        }
    }

    /* Extracts the latitude and longitude of the input string which should be of the form "lat long"
     * Saves the two doubles in a double array with two elements and returns it, returns null if
     * parseDouble fails
     */
    private double[] stringToDoublePair(String input) {
        double[] output = null;
        try {
            output = new double[2];
            output[0] = Double.parseDouble(input.substring(0, input.indexOf(" ")));
            output[1] = Double.parseDouble(input.substring(input.indexOf(" ")));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    /* Creates/Updates notification with certain attributes */
    public void notification() {
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
