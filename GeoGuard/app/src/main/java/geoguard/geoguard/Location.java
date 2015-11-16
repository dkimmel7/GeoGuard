package geoguard.geoguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


// For notification
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
// import android.support.v7.app.AppCompatActivity;
// import android.os.Bundle;
// import android.view.View;

public class Location extends AppCompatActivity {

    Button btnShowLocation;
    Tracker gps;

    // Notification
    NotificationCompat.Builder notification;
    private static final int uniqueID = 123456;
    Button btnShowNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Create button (object) to push for geolocation
        btnShowLocation = (Button) findViewById(R.id.show_location);
        // setOnClockListener method (event listener) - detects clicks
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            // Button Click [for location]
            @Override
            public void onClick(View v) {
                gps = new Tracker(Location.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();


                    Toast.makeText(
                            getApplicationContext(),
                            "Your Location is -\nLat: " + latitude + "\nLong: "
                                    + longitude + "\nradius" + gps.radius(36.975952, -122.05534399999999), Toast.LENGTH_LONG).show();
                } else {
                    // Display alert to turn on GPS
                    gps.showSettingsAlert();
                }
            }
        });

        // Notification
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }

    public void notifyButtonClicked(View view) {
        // Build the notification
        notification.setSmallIcon(R.drawable.notification_template_icon_bg);
        notification.setTicker("This is a ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Here is the title.");
        notification.setContentText("I am the body text of your notification");

        Intent intent = new Intent(this, Location.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        // Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

}
