package geoguard.geoguard;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class Tracker extends Service implements LocationListener{

    // Class allows access to application-specific resources, app-level operations, etc.
    private final Context context;

    // Allow access to system location services
    protected LocationManager locationManager;
    // Variables for checking if GPS and/or Network Provider is on
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    // Data class of geographic location
    Location location;
    // Variable for geolocation coordinates
    double latitude;
    double longitude;

    /* Controls frequency of location updates
     * Application will only receive updates when location changed via distance and time passed
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters
    private static final long MIN_TIME_BW_UPDATES = 1 * 60 * 1000; // 1 minute (in ms)

    public Tracker(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // Checks GPS and Network Provider. Provide boolean value to variable
            // isProviderEnabled returns status of provider
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                return null; // user should be prompted to enable gps in location class
            } else {
                // If network provider OR/AND GPS is enabled, application can get location
                this.canGetLocation = true;

                // Network Provider is enabled on device for application
                if (isNetworkEnabled) {
                    // Request for location be be updated periodically via provider
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    // Get last known updated location from provider, if needed immediately
                    // If provider disabled for long - possibly out-of-date
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        // Location was found, get the geolocation
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                // GPS is enabled on device for application
                // Methods similar to location via Network Provider
                if(isGPSEnabled) {
                    if(location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if(locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    public void stopUsingGPS() {
        if(locationManager != null) {
            locationManager.removeUpdates(Tracker.this);
        }
    }

    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    // Gets the meters between the current location to set location
    public float radius(double latitude, double longitude) {
        Location curr = getLocation();
        Location pin = new Location ("pin");
        pin.setLatitude(latitude);
        pin.setLongitude(longitude);

        float distance = curr.distanceTo(pin);
        return distance;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    // If GPS is not enabled, this will prompt user to enable
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            // User wants to go to settings
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // User clicks cancel
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    // Generated methods, modify if needed
    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}


