package geoguard.geoguard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        //code for getting a unique device id
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        final String deviceId = deviceUuid.toString();
        Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
        //allows for data to be stored in parse
       // ParseObject testObject = new ParseObject("TestObject");
       // testObject.put("foo", deviceId);
       // testObject.saveInBackground();
        final ParseObject tableName = new ParseObject("Users");
        //tableName.put("username", "user");
        //tableName.saveInBackground();// because of final?
       // tableName.put("columnOne", "string"); //string
       // tableName.put("columnTwo", 12); //integer
        //tableName.put("columnThree", x); //variable
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("deviceID", deviceId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() > 0)
                        Log.d("deviceID", "Retrieved " + idList.size() + " deviceIDs");
                    else {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> theSize, ParseException e) {
                                if (e == null) {
                                    tableName.put("username", "user" + theSize.size());
                                    tableName.saveInBackground();
                                } else {
                                    Log.d("score", "Error: " + e.getMessage());
                                }
                            }
                        });
                        tableName.put("deviceID", deviceId);

                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });



        btnHomeBase = (Button) findViewById(R.id.btnHomeBase);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnLocalPass = (Button) findViewById(R.id.btnLocalPass);
        btnMobilePass = (Button) findViewById(R.id.btnMobilePass);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnLocation = (Button) findViewById(R.id.btnLocation); // @GeoLocation

        btnHomeBase.setOnClickListener(this);
        btnLocalPass.setOnClickListener(this);
        btnMobilePass.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnLocation.setOnClickListener(this); // @GeoLocation

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
            case R.id.btnMobilePass:
                Intent mobile = new Intent(this, MobilePasswords.class);
                startActivity(mobile);
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
}
