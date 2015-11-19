package geoguard.geoguard;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
//import geoguard.geoguard.LinkedList;
// Class which handles Insert screen, Uses a hash map which uses TreeMaps its values and either
//the location as its key or noLocString for passwords which are not geotagged
//noLocString could be changed so that the non-geotagged passwords can be grouped together


public class Insert extends ActionBarActivity implements View.OnClickListener {
    Button bEnter;
    EditText editKey, editValue;
    TextView textList;
    CheckBox checkbox;
    Tracker gps;

    final String noLocString = "";
    String locString = "1289347 123847";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // initOutputStreams();
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        gps = new Tracker(Insert.this);
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        textList = (TextView) findViewById(R.id.textList);
        loadText();
        editKey = (EditText) findViewById(R.id.editKey);
        editValue = (EditText) findViewById(R.id.editValue);
        bEnter = (Button) findViewById(R.id.bEnter);
        editKey.getText();
        bEnter.setOnClickListener(this);
    }
    private void loadText() {
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
            s = tempT.toString();
        }
        textList.setMovementMethod(new ScrollingMovementMethod());
        textList.setText("");}
    private void locationStuff() {
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            locString = Double.toString(latitude);
            locString += " " + Double.toString(longitude);

            Toast.makeText(
                    getApplicationContext(),
                    "Your Location is -\nLat: " + latitude + "\nLong: "
                            + longitude, Toast.LENGTH_SHORT).show();
        } else {
            // Display alert to turn on GPS
            gps.showSettingsAlert();
        }
    }
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bEnter:
                //if(editKey.getText().toString().equals("TEST")) {
                //    printKeysNoLoc();
                //} else {
                    locationStuff();
                    Toast.makeText(getBaseContext(), "password entered", Toast.LENGTH_SHORT).show();
                    System.out.println("Key = ");
                    String tempo = textList.getText().toString();
                    System.out.println(String.valueOf(editKey.getText()));
                    System.out.println("Value = ");
                    System.out.println(String.valueOf(editValue.getText()));
                    /*textList.append("Key = ");
                    textList.append(String.valueOf(editKey.getText()));
                    textList.append(" Value = ");
                    textList.append(String.valueOf(editValue.getText()));
                    textList.append("\n");*/
                    System.out.println(tempo);
                    //textList.setText(tempo);
                    if(checkbox.isChecked()) {
                        storePassword(locString, editKey.getText().toString(), editValue.getText().toString());
                        System.out.print("checkbox is checked \n");
                        System.out.println("locString = " +locString);
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


    private FileOutputStream outputStreamLoc;
    private FileOutputStream outputStreamNoLoc;
    private ObjectOutputStream objectOutputStreamLoc;
    private ObjectOutputStream objectOutputStreamNoLoc;
    private String filename = "loc";
    private HashMap<String, TreeMap<String,String>> noLocData;
    /*
    public void initOutputStreams() {

        try {
            outputStreamLoc = openFileOutput(filename, Context.MODE_PRIVATE);
            if (outputStreamNoLoc == null) {
                System.out.println("OUTPUTSTREAMNOLOC EQUALS NULL");
            }
            outputStreamNoLoc = openFileOutput(filenameNoLoc,Context.MODE_PRIVATE);
            objectOutputStreamNoLoc = new ObjectOutputStream(outputStreamNoLoc);
            objectOutputStreamLoc = new ObjectOutputStream(outputStreamLoc);
        } catch(Exception e) {
            e.printStackTrace();
            // System.err.print(e.getMessage());
        }
        return;
    }*//*
    public void close() {
        try {
            objectOutputStreamNoLoc.close();
            objectOutputStreamLoc.close();
            outputStreamLoc.close();
            outputStreamNoLoc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }

    }*/
    //This is the function to add a password which is not geo tagged
    public void storePassword(String location,String key, String value) {
        File file = getFilesDir();
        //If the file which contains the data exists, read it into
        //noLocData. Else, initialize noLocData to a new HashMap

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
            //System.out.println(tempFile+" this is tempFile");
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
        }
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
    public String retrievePassword(String key, boolean locTagged) {
        if(noLocData == null) {
            System.err.println("NOLOCDATA IS NULL IN RETRIEVEPASSWORD");
            return null;
        }
            try {
                File tempFile = getFilesDir();
                FileInputStream loc = openFileInput(filename);
                ObjectInputStream oLoc = new ObjectInputStream(loc);
                HashMap<String, TreeMap<String, String>> tempMap = (HashMap<String,TreeMap<String, String>>) oLoc.readObject();
                loc.close();
                oLoc.close();
                //return here TODO;
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }


        return null;
    }
    //Removes an entry from the specified hashmap

    public void removePassword (String location, String key) {
            try {
                File tempFile = getFilesDir();
                FileInputStream loc =  openFileInput(filename);
                ObjectInputStream oLoc = new ObjectInputStream(loc);
                HashMap<String, TreeMap<String, String>> tempMap = (HashMap<String, TreeMap<String, String>>) oLoc.readObject();
                loc.close();
                oLoc.close();
                if(location.equals(noLocString)) {
                    tempMap.get(noLocString).remove(key);
                } else tempMap.remove(location);
                objectOutputStreamLoc.writeObject(tempMap);

            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }

    }



