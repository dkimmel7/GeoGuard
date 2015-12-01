package geoguard.geoguard;

/**
 * Created by monca on 11/10/2015.
 */

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;
import android.location.Location;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LocalPasswords extends AppCompatActivity implements View.OnClickListener {

    private Tracker gps;
    private HashMap<String, TreeMap<String,String>> data;
    LocalDB database = null;
    int radius = 0;
    private String filename = "";
    Button bReload;
    byte[] masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
         final int interval = 5000; //5 seconds
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            masterKey = extras.getByteArray("masterKey");
        }
        database = new LocalDB(LocalPasswords.this, masterKey);
        filename =  database.getFilename();
        final SharedPreferences settings = getSharedPreferences("settings" , MODE_PRIVATE);
        final ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        setContentView(R.layout.activity_local_passwords);
        bReload = (Button) findViewById(R.id.bReload);
        bReload.setOnClickListener(this);
        radius = settings.getInt("radius", 0);
        System.out.println("RADIUS = " + radius);
        gps = new Tracker(LocalPasswords.this);
        data = database.returnData();
        final LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        final LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll2);
        updatePassSections();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bReload:
                updatePassSections();
                break;
        }
    }
    //Updates both sections of the password screen
    private void updatePassSections() {
        final LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        final LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll2);
        final SharedPreferences settings = getSharedPreferences("settings" , MODE_PRIVATE);
        final ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clearTagged(ll);
        clearNonTagged(ll2);
        database.returnData();
        if(data != null) {
            TreeMap<String, String> tree = data.get("");
            int i = 0;
            for(final Map.Entry<String, TreeMap<String,String>> entry : data.entrySet()) {

                //if location = Home Base then don't do anything with it since it stores Home Base
                //location
                if(entry.getKey().equals("Home Base")) {
                    continue;
                }
                //if the key = "" then it is the non-geotagged passwords TreeMap
                else if (entry.getKey().equals("")) {
                    final TreeMap<String, String> hashEntry = entry.getValue();
                    for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                        final Button myButton = new Button(this);
                        myButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        myButton.setId(i);
                        myButton.setText(treeEntry.getKey());
                        myButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                System.out.println("BUTTON " + treeEntry.getKey() + "WAS CLICKED");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LocalPasswords.this);
                                builder.setMessage(Html.fromHtml("Password: " + "<b>" + treeEntry.getValue() + "</b>" + "<br>" + "\nCopy to Clipboard?")).setCancelable(true);
                                builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ClipData clip = ClipData.newPlainText(treeEntry.getKey(), treeEntry.getValue());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getBaseContext(), "Copied to clipboard", Toast.LENGTH_LONG).show();
                                    }
                                });
                                builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(database != null) {
                                            Toast.makeText(getBaseContext(), "Password removed", Toast.LENGTH_LONG).show();
                                            database.delete(entry.getKey(), treeEntry.getKey());
                                            ll2.removeView(myButton);
                                        } else
                                            Toast.makeText(getBaseContext(), "Unable to remove password", Toast.LENGTH_LONG).show();

                                    }
                                });

                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                        ll2.addView(myButton);
                        ++i;
                    }
                    continue;
                } else if(true){
                    String currLocation = getLocation();
                    if(currLocation == null) {
                        Toast.makeText(getApplicationContext(),"Please turn on your GPS", Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    if (!(showCurrLoc(entry.getKey(), getLocation(), radius))) {
                        System.out.println("not showCurrLoc on loc = " + entry.getKey());
                        continue;
                    }
                }
                final TreeMap<String, String> hashEntry = entry.getValue();
                for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                    System.out.println("Location = " + entry.getKey() + " name = " + treeEntry.getKey() + " value = " + treeEntry.getValue());

                    final Button myButton = new Button(this);
                    myButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    myButton.setId(i);
                    myButton.setText(treeEntry.getKey());
                    myButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            System.out.println("BUTTON " + treeEntry.getKey() + "WAS CLICKED");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LocalPasswords.this);
                            builder.setMessage(Html.fromHtml("Location: \n" + entry.getKey() + "\n" + "Password: " + "<b>" + treeEntry.getValue() + "</b>" + "<br>" + "\nCopy to Clipboard?")).setCancelable(true);
                            builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ClipData clip = ClipData.newPlainText(treeEntry.getKey(), treeEntry.getValue());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(getBaseContext(), "Copied to clipboard", Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (database != null) {
                                        Toast.makeText(getBaseContext(), "Password removed", Toast.LENGTH_LONG).show();
                                        database.delete(entry.getKey(), treeEntry.getKey());
                                        ll.removeView(myButton);

                                        // Destroy Notification Service to reload database,
                                        // NotifyService will auto recreate itself via onCreate
                                        Intent notify = new Intent(LocalPasswords.this, NotifyService.class);
                                        LocalPasswords.this.stopService(notify);

                                    } else Toast.makeText(getBaseContext(), "Unable to remove password", Toast.LENGTH_LONG).show();

                                }
                            });

                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                    ll.addView(myButton);
                    ++i;
                }
            }
        } else System.out.println("data is null");
    }
    //Clears buttons from tagged section of password screen if there are any
    private void clearTagged(LinearLayout ll) {
        if(((LinearLayout) ll).getChildCount() > 0){
            ll.removeAllViews();
        }
    }
    //Clears buttons from non-tagged section of password screen if there are any
    private void clearNonTagged(LinearLayout ll2) {
        if(((LinearLayout) ll2).getChildCount() > 0){
            ll2.removeAllViews();
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
        Location a = gps.getLocation();
        double thereLat = Double.parseDouble(there.substring(0, there.indexOf(" ")).trim());
        double  hereLat = Double.parseDouble(here.substring(0, here.indexOf(" ")).trim());
        double thereLong = Double.parseDouble(there.substring(there.indexOf(" ")).trim());
        double  hereLong = Double.parseDouble(here.substring (here.indexOf(" ")).trim());
        if( getDist(thereLat,thereLong,hereLat,hereLong) >= 0 && getDist(thereLat,thereLong,hereLat,hereLong) <= radius) {
            return true;
        }
        return false;
    }
    private HashMap<String,TreeMap<String, String>> createFile(String filename) {
        HashMap<String,TreeMap<String, String>> data = null;
        try {
            FileInputStream savedData = openFileInput(filename);
            ObjectInputStream objStr = new ObjectInputStream(savedData);
            data = (HashMap<String, TreeMap<String, String>>) objStr.readObject();
            savedData.close();
            objStr.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_passwords, menu);
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
}

