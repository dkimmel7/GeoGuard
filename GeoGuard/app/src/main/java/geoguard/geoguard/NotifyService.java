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
    private int METERS_SET;
    private int NOTIFY_COUNT = 0;
    private boolean IN_RANGE_CHANGE;

    // Database
    LocalDB db;

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

       // Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();  // Debugging
        Log.d("Service", "Created");                                          // Debugging

        db = new LocalDB(getApplicationContext(),null);
        SharedPreferences settings = getSharedPreferences("settings" , MODE_PRIVATE);
        int radius = settings.getInt("radius", 0);
        METERS_SET = radius;

        Log.d("Radius:", String.valueOf(METERS_SET));

        // Builds single notification
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        super.onCreate();
    }

    /* System calls this when service is removed. Should not be called directly.
     * Resources used by service are freed when it is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d("Service", "Destroyed");
        super.onDestroy();
    }

    /* Called every time a client starts the service using startService(Intent, intent)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        // Debugging
       // Toast.makeText(getApplicationContext(), "Service Working", Toast.LENGTH_LONG).show();
       // Log.d("Service", "Working"); // Debugging

        // Check if user is within radius of tagged location
        gpsCheck();

        // Issue Notification if new password found or if left area
        if (IN_RANGE_CHANGE) {
            notification();
        }

        // Keeps the service running in the background
        return START_STICKY; //super.onStartCommand(intent, flags, startID);
    }


    /* Check current coordinates to tagged GeoLocation */
    public void gpsCheck() {
        gps = new Tracker(NotifyService.this);

        // Passwords within range unknown from start of iteration
        IN_RANGE_CHANGE = false;
        double DB_LONGITUDE;
        double DB_LATITUDE;

        // Recounts passwords found within range of location tagged
        int recount = 0;
        // For all locations tagged in database, entry[0] is location
        ArrayList<String[]> entries = new ArrayList<>();
        entries = db.getAllTaggedPasswords();

        // Check if entries are null, and if notifications decreased to 0 or stayed at 0
        // Saves needless GPS tracking when database is empty
        if(entries == null) {
            // Database is still empty
            if (recount == NOTIFY_COUNT) {
                IN_RANGE_CHANGE = false;
                return;
            } else {
                // Geotagged password was removed from local DB
                IN_RANGE_CHANGE = true;
                NOTIFY_COUNT = recount;
                return;
            }
        }

        // Get current locations
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Makes sure there is no initial crash when checking radius
            if(latitude == 0.0 && longitude == 0.0) {
                return;
            }
            
            // Query all locations in database
            for(String[] entry : entries) {
                double loc[] = stringToDoublePair(entry[0]);
                DB_LATITUDE = loc[0];
                DB_LONGITUDE = loc[1];

                // Log.d("Entry Check:", entry[0]);
                // Log.d("GPS Location:", String.valueOf(gps.getLatitude()) + ", " + String.valueOf(gps.getLongitude()));

                // Check if current location is within radius of tagged location
                if (gps.radius(DB_LATITUDE,DB_LONGITUDE) <= METERS_SET) {

                /*    Toast.makeText(
                            getApplicationContext(),
                            "Your Location is -\nLat: " + gps.getLatitude() + "\nLong: "
                                    + gps.getLongitude() + "\nRadius: " + gps.radius(DB_LATITUDE,DB_LONGITUDE), Toast.LENGTH_LONG).show();
                 */
                    recount++; // Increment count for password found
                }
            }

            // Check if the number of passwords found in current location changed
            // No new location found, no need to update notification
            if (recount == NOTIFY_COUNT) {
                IN_RANGE_CHANGE = false;   // Flag false evades notification issuing

                // New password found - increasing NOTIFY_COUNT
                // Or User left area -  decreasing NOTIFY_COUNT
            } else {
                NOTIFY_COUNT = recount;
                IN_RANGE_CHANGE = true;    // Flag true initiates notification issue/mod
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
            notification.setSmallIcon(R.drawable.ic_geoguard); // Default: notification_template_icon_bg
            notification.setTicker("GeoGuard Password Located");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("GeoGuard Password Found at Location");

            if(NOTIFY_COUNT == 1) {
                notification.setContentText("You have "
                        + NOTIFY_COUNT + " password found within "
                        + METERS_SET + " meters of your location");
            } else {
                notification.setContentText("You have "
                        + NOTIFY_COUNT + " passwords found within "
                        + METERS_SET + " meters of your location");
            }

            Intent intent = new Intent(this, Unlock.class);
            intent.putExtra("firstTime", false);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            // Builds notification and issues it
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(uniqueID, notification.build());
        } else {
            // Cancel notification if notification un-clicked and radius is left
            // NOTIFY_COUNT will be 0
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(uniqueID);

        }
    }

}
