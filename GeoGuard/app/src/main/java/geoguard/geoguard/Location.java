package geoguard.geoguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Location extends AppCompatActivity {

    Button btnShowLocation;

    Tracker gps;

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
                                    + longitude + "\nradius" + gps.radius(36.975952,-122.05534399999999), Toast.LENGTH_LONG).show();
                } else {
                    // Display alert to turn on GPS
                    gps.showSettingsAlert();
                }
            }
        });

    }
}
