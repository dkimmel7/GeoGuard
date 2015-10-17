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
import java.util.HashMap;



public class Insert extends ActionBarActivity implements View.OnClickListener {
    Button bEnter;
    EditText editKey, editValue;


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
                    storePasswordNoLoc(editKey.getText().toString(), editValue.getText().toString());
                    System.out.println("Value = ");
                    System.out.println(String.valueOf(editValue.getText()));
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
    private HashMap<String, String> noLocData = new HashMap<>();
    private HashMap<String, String> locData = new HashMap<String, String>();
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
    public void storePasswordNoLoc(String key, String value) {
        noLocData.put(key, value);
        if(noLocData == null) {
            System.out.println("NOLOCDATA IS NULL"); return;
        } else if(noLocData.isEmpty()) {
            System.out.println("NOLOCDATA IS EMPTY"); return;
        }

        try {
            File tempFile = getFilesDir();
            System.out.println(tempFile);
            FileOutputStream noLocStream = new FileOutputStream(tempFile+filenameNoLoc,false);
            ObjectOutputStream objNoLoc = new ObjectOutputStream(noLocStream);
            objNoLoc.writeObject(noLocData);
            objNoLoc.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.toString());
        }
    }
    public void printKeysNoLoc() {
        try {
            File tempFile = getFilesDir();
            FileInputStream noLoc = new FileInputStream(tempFile+filenameNoLoc);
            ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
            try {
                HashMap<String, String> tempMap = (HashMap<String, String>) oNoLoc.readObject();
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
        //System.out.println(noLocData.keySet());
    }
    //Add geotagged password to other hashmap
    public void storePasswordLoc(String key, String value, boolean overWrite) {
        locData.put(key, value);
        try {
            objectOutputStreamLoc.writeObject(locData);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
    }
    //Retrieve the password associated with the given key
    // and returns geotagged password if locTagged is true
    public String retrievePassword(String key, boolean locTagged) {
        if(locTagged) {
            try {
                File tempFile = getFilesDir();
                FileInputStream loc = new FileInputStream(tempFile+filename);
                ObjectInputStream oLoc = new ObjectInputStream(loc);
                HashMap<String, String> tempMap = (HashMap<String,String>) oLoc.readObject();
                loc.close();
                oLoc.close();
                return tempMap.get(key);
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }
        else {
            try {

                File tempFile = getFilesDir();
                FileInputStream noLoc = new FileInputStream(tempFile+filenameNoLoc);
                ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
                HashMap<String, String> tempMap = (HashMap<String, String>) oNoLoc.readObject();
                noLoc.close();
                oNoLoc.close();
                return tempMap.get(key);
            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }
    //Removes an entry from the specified hashmap
    public void removePassword (String key, boolean locTagged) {
        if(locTagged) {
            try {
                File tempFile = getFilesDir();
                FileInputStream loc = new FileInputStream(tempFile+filenameNoLoc);
                ObjectInputStream oLoc = new ObjectInputStream(loc);
                HashMap<String, String> tempMap = (HashMap<String, String>) oLoc.readObject();
                loc.close();
                oLoc.close();
                locData.remove(key);
                tempMap.remove(key);
                objectOutputStreamLoc.writeObject(tempMap);

            } catch (Exception e) {
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }
        else {
            try {
                File tempFile = getFilesDir();
                FileInputStream noLoc = new FileInputStream(tempFile+filenameNoLoc);
                ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
                HashMap<String, String> tempMap = (HashMap<String, String>) oNoLoc.readObject();
                noLoc.close();
                oNoLoc.close();
                noLocData.remove(key);
                tempMap.remove(key);
                objectOutputStreamNoLoc.writeObject(tempMap);
            } catch (Exception e) {
                //System.err.println(e.toString());
                e.printStackTrace();
            }
        }
    }
}


