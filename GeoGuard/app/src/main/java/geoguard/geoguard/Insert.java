package geoguard.geoguard;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    final String noLocString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // initOutputStreams();
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        editKey = (EditText) findViewById(R.id.editKey);
        editValue = (EditText) findViewById(R.id.editValue);
        bEnter = (Button) findViewById(R.id.bEnter);
        editKey.getText();
        bEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bEnter:
                if(editKey.getText().toString().equals("TEST")) {
                    printKeysNoLoc();
                } else {
                    System.out.println("Key = ");
                    System.out.println(String.valueOf(editKey.getText()));
                    System.out.println("Value = ");
                    System.out.println(String.valueOf(editValue.getText()));
                    storePassword("", editKey.getText().toString(), editValue.getText().toString());
                    //editKey and editValue are cleared after button click
                    editKey.setText("");
                    editValue.setText("");
                }
                break;
        }
    }


    private FileOutputStream outputStreamLoc;
    private FileOutputStream outputStreamNoLoc;
    private ObjectOutputStream objectOutputStreamLoc;
    private ObjectOutputStream objectOutputStreamNoLoc;
    private String filename = "loc";
    private String filenameNoLoc = "noLoc";
    private HashMap<String, TreeMap<String,String>> noLocData = new HashMap<>();
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
    }
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

    }
    //This is the function to add a password which is not geo tagged
    public void storePassword(String location,String key, String value) {
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
            TreeMap<String, String> pair = new TreeMap<>();
            pair.put(key, value);
            noLocData.put(location, pair);
        }

        try {
            File tempFile = getFilesDir();
            System.out.println(tempFile+" this is tempFile");
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
            //FileInputStream noLoc = new FileInputStream(tempFile+filenameNoLoc);
            FileInputStream noLoc = openFileInput(filename);
            ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
            try {
                HashMap<String, TreeMap<String, String>> tempMap = (HashMap<String, TreeMap<String, String>>) oNoLoc.readObject();
                noLoc.close();
                oNoLoc.close();
                //System.out.println(tempMap.toString());
                //if(!(tempMap.get("") == null)) {
                    System.out.println(tempMap.toString());
                //} else System.out.println("tempMap is empty");
            }catch(Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        //System.out.println(noLocData.keySet());
    }
    //Retrieve the password associated with the given key
    // and returns geotagged password if locTagged is true
    public String retrievePassword(String key, boolean locTagged) {

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



