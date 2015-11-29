package geoguard.geoguard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.provider.Settings.Secure;


import java.util.List;
import java.util.UUID;


public class MainScreen extends Activity implements View.OnClickListener {

    Button btnSettings;
    Button btnHomeBase;
    Button btnLocalPass;
    Button btnMobilePass;
    Button btnInsert;
    Button btnLocation; // @GeoLocation
    private Tracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btnHomeBase = (Button) findViewById(R.id.btnHomeBase);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnLocalPass = (Button) findViewById(R.id.btnLocalPass);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnLocation = (Button) findViewById(R.id.btnLocation); // @GeoLocation

        btnHomeBase.setOnClickListener(this);
        btnLocalPass.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnLocation.setOnClickListener(this); // @GeoLocation

        SharedPreferences settings = getSharedPreferences("settings" , MODE_PRIVATE);
        gps = new Tracker(MainScreen.this);
        int radius = settings.getInt("radius", 0);
        System.out.println("RAD = " + radius);
        String homeLat = settings.getString("latitude", "");
        String homeLong = settings.getString("longitude", "");
        String homeLoc = "";
        System.out.println("lat is " + homeLat);
        System.out.println("long is " + homeLong);
        if( !(homeLat.equals("") || homeLong.equals(""))) {
            homeLoc = homeLat + " " + homeLong;
        }
        if (gps.canGetLocation()){
            if(!homeLoc.equals("")) {
                if (!(showCurrLoc(homeLoc, getLocation(), radius))) {
                    btnHomeBase.setVisibility(View.GONE);
                }
            }
            else{
                btnHomeBase.setVisibility(View.GONE);
            }
        }
        else {
            btnHomeBase.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        //disable going back
        moveTaskToBack(true);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnHomeBase:
                Intent home = new Intent(this, HomeBase.class);
                startActivity(home);
                break;
            case R.id.btnSettings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                break;
            case R.id.btnLocalPass:
                Intent local = new Intent(this, LocalPasswords.class);
                startActivity(local);
                break;
            case R.id.btnInsert:
                Intent insert = new Intent(this, Insert.class);
                startActivity(insert);
                break;
            case R.id.btnLocation: // @GeoLocation
                Intent location = new Intent(this, Location.class);
                startActivity(location);
                break;
            default:
                break;
        }
    }

    private double getDist(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372.8; // radius of Earth in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        System.out.println("DIST = " + (R * c * 1000));
        return R * c * 1000; // returns in meters
    }
    private String getLocation() {
        String locString = null;
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            locString = Double.toString(latitude);
            locString += " " + Double.toString(longitude);
            /*Toast.makeText(
                    getApplicationContext(),
                    "Your Location is -\nLat: " + latitude + "\nLong: "
                            + longitude, Toast.LENGTH_SHORT).show();*/
        } else {
            // Display alert to turn on GPS
            gps.showSettingsAlert();
        }
        return locString;
    }

    private boolean showCurrLoc(String there, String here,double radius) {
        if( there.equals("") || here.equals("")) {
            return false;
        }
        android.location.Location a = gps.getLocation();
        double thereLat = Double.parseDouble(there.substring(0, there.indexOf(" ")).trim());
        double  hereLat = Double.parseDouble(here.substring(0, here.indexOf(" ")).trim());
        double thereLong = Double.parseDouble(there.substring(there.indexOf(" ")).trim());
        double  hereLong = Double.parseDouble(here.substring (here.indexOf(" ")).trim());
        if( getDist(thereLat,thereLong,hereLat,hereLong) >= 0 && getDist(thereLat,thereLong,hereLat,hereLong) <= radius) {
            return true;
        }
        return false;
    }
}
