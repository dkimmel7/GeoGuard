package geoguard.geoguard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeMap;
//import geoguard.geoguard.LinkedList;
// Class which handles Insert screen, Uses a hash map which uses TreeMaps its values and either
//the location as its key or noLocString for passwords which are not geotagged
//noLocString could be changed so that the non-geotagged passwords can be grouped together


public class Insert extends AppCompatActivity implements View.OnClickListener {
    Button bEnter;
    EditText editKey, editValue;
    TextView textList;
    CheckBox checkbox;
    Tracker gps;
    boolean canGetLoc = false;

    final private String noLocString = "";
    private String locString = "1289347 123847";
    private String filename = "";
    private FileOutputStream outputStreamLoc;
    private FileOutputStream outputStreamNoLoc;
    private ObjectOutputStream objectOutputStreamLoc;
    private ObjectOutputStream objectOutputStreamNoLoc;
    private HashMap<String, TreeMap<String,String>> noLocData;
    private LocalDB database;
    byte[] masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initOutputStreams();
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            masterKey = extras.getByteArray("masterKey");
        }
        database= new LocalDB(Insert.this,masterKey);
        gps = new Tracker(Insert.this);
        LocalDB database = new LocalDB(Insert.this,masterKey);
        filename = database.getFilename();
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        textList = (TextView) findViewById(R.id.textList);
        //loadText();
        editKey = (EditText) findViewById(R.id.editKey);
        editValue = (EditText) findViewById(R.id.editValue);
        bEnter = (Button) findViewById(R.id.bEnter);
        editKey.getText();
        bEnter.setOnClickListener(this);
    }
    //No longer used I think.
    /*private void loadText() {
        HashMap<String, TreeMap<String,String>> temp = null;
        try {
            FileInputStream noLoc = openFileInput(filename);
            ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
            temp = (HashMap<String, TreeMap<String, String>>) oNoLoc.readObject();
            noLoc.close();
            oNoLoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = "";
        if(temp != null) {
            TreeMap<String, String> tempT = temp.get("");
            if(tempT != null) s = tempT.toString();
        }
        textList.setMovementMethod(new ScrollingMovementMethod());
        textList.setText("");} */
    private void locationStuff() {
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            locString = Double.toString(latitude);
            locString += " " + Double.toString(longitude);
            canGetLoc = true;
            if(checkbox.isChecked()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Your Location is -\nLat: " + latitude + "\nLong: "
                                + longitude, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Display alert to turn on GPS
            canGetLoc = false;
            gps.showSettingsAlert();
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bEnter:
                locationStuff();
                Toast.makeText(getBaseContext(), "password entered", Toast.LENGTH_SHORT).show();
                System.out.println("Key = ");
                //String tempo = textList.getText().toString();
                System.out.println(String.valueOf(editKey.getText()));
                System.out.println("Value = ");
                System.out.println(String.valueOf(editValue.getText()));
                //System.out.println(tempo);
                if(checkbox.isChecked()) {
                    if(canGetLoc == false) {
                        break;
                    }
                    storePassword(locString, editKey.getText().toString(), editValue.getText().toString());
                    System.out.print("checkbox is checked \n");
                    System.out.println("locString = " +locString);

                    // Destroy Notification Service to reload database,
                    // NotifyService will auto recreate itself via onCreate
                    Intent notify = new Intent(this, NotifyService.class);
                    this.stopService(notify);

                } else {
                    storePassword(noLocString, editKey.getText().toString(), editValue.getText().toString());
                    System.out.print("checkbox is NOT checked \n");
                    System.out.println("noLocString = " + noLocString);

                }

                //editKey and editValue are cleared after button click
                editKey.setText("");
                editValue.setText("");
                // }
                break;
        }
    }




    //This is the function to add a password which is not geo tagged
    public void storePassword(String location,String key, String value) {
        database.storePassword(location, key, value);
        //If the file which contains the data exists, read it into
        //noLocData. Else, initialize noLocData to a new HashMap
        /*
            try {
                FileInputStream noLoc = openFileInput(filename);
                ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
                    noLocData = (HashMap<String, TreeMap<String, String>>) oNoLoc.readObject();
                    noLoc.close();
                    oNoLoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        if(! (noLocData == null)) {
            System.out.println("file exists:120");
        } else {
            System.err.println("File does NOT exist:133");
            noLocData = new HashMap<>();
        }
        if(noLocData == null) {
            System.out.println("NOLOCDATA IS NULL"); return;
        } else if(noLocData.isEmpty()) {
            System.out.println("NOLOCDATA IS EMPTY");
        }
        if(location.equals(noLocString)) {
            System.out.println("location == \"\"");
            if(noLocData.containsKey(noLocString)) {
                System.out.println("noLocData containsKey noLocString");
                noLocData.get(noLocString).put(key, value);
            } else {
                System.out.println("noLocData DOES NOT containkey location");
                TreeMap<String, String> temp = new TreeMap<>();
                temp.put(key, value);
                noLocData.put(noLocString, temp);
            }
        } else {
            System.out.println("location is not \"\"");
            TreeMap<String, String> pair;
            if(noLocData.containsKey(location)) {
                pair = noLocData.get(location);
            } else {
                pair = new TreeMap<>();
            }
            pair.put(key, value);
            noLocData.put(location, pair);
        }
        try {
            File tempFile = getFilesDir();
            //Creates a new fileOutputStream and sets append to false which means
            // it overwrites the file
            FileOutputStream noLocStream = openFileOutput(filename, MODE_PRIVATE);
            //FileOutputStream noLocStream = new FileOutputStream(tempFile+filenameNoLoc,false);
            ObjectOutputStream objNoLoc = new ObjectOutputStream(noLocStream);
            objNoLoc.writeObject(noLocData);
            objNoLoc.close();
            noLocStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.toString());
        }*/
    }
    public void printKeysNoLoc() {
        try {
            File tempFile = getFilesDir();
            FileInputStream noLoc = openFileInput(filename);
            ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
            try {
                HashMap<String, TreeMap<String, String>> tempMap = (HashMap<String, TreeMap<String, String>>) oNoLoc.readObject();
                noLoc.close();
                oNoLoc.close();
                System.out.println(tempMap.toString());
            }catch(Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
    //Retrieve the password associated with the given key
    // and returns geotagged password if locTagged is true
    public String retrievePassword(String location, String name) {
        return database.retrievePassword(location, name);
    }
    //Removes an entry from the specified hashmap

    public void removePassword (String location, String key) {
        database.delete(location,key);
    }

}