package geoguard.geoguard;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by monca on 11/23/2015.
 */
public class LocalDB {
    private HashMap<String, TreeMap<String,String>> data = null;
    final private String filename = "passwordData";
    final private String nonGeoTaggedString = "";
    private Context context = null;

    LocalDB(Context context) {
        this.context = context;
        openFile();
    }
    private void openFile() {
        HashMap<String,TreeMap<String, String>> data = null;
        try {
            FileInputStream savedData = context.openFileInput(filename);
            ObjectInputStream objStr = new ObjectInputStream(savedData);
            data = (HashMap<String, TreeMap<String, String>>) objStr.readObject();
            savedData.close();
            objStr.close();
            this.data = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void delete(String location, String name) {
    }
    public ArrayList<String[]> getAllPasswords() {
        ArrayList<String[]> output = new ArrayList<>();
        String entryData[] = new String[3];
        //entry.getKey is the Location and entry.getValue is the TreeMap containing all the
        //Name, Password pairs geotagged to the Location
        for(final Map.Entry<String, TreeMap<String,String>> entry : data.entrySet()) {
            if (entry.getKey().equals("")) {
                continue;
            }
            final TreeMap<String, String> hashEntry = entry.getValue();
            //Iterates through all the Name, Value pairs tagged to the Location entry.getKey()
            for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                System.out.println("Location = " + entry.getKey() + " name = " + treeEntry.getKey() + " password = " + treeEntry.getValue());
                //entryData[0] will get the location which was tagged to this password
                entryData[0] = entry.getKey();
                //entryData[1] will get the name which was associated with the password, aka password name
                entryData[1] = treeEntry.getKey();
                //entryData[2] will get the password
                entryData[2] = treeEntry.getValue();
                //output is an ArrayList of String arrays, each array contains a Location, Name, and Password
                output.add(entryData);
            }
        }
        return output;
    }
    private double getDist(double lat1, double lon1, double lat2, double lon2) {
        //Haversine formula to get distance on a sphere
        double R = 6372.8; // radius of Earth in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c * 1000; // returns in meters
    }
    public boolean hasLocation(String location) {
        if(data.containsKey(location)) {
            return true;
        }
        return false;
    }
    public boolean hasName(String name) {

        //Iterates through all entries in the Hash Map
        for(Map.Entry<String, TreeMap<String, String>> entry: data.entrySet()) {
            TreeMap<String, String> tree = entry.getValue();
            for(Map.Entry<String, String> treeEntry: tree.entrySet()) {
                if(treeEntry.getKey().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    public ArrayList<String[]> nearbyPasswords(double latitude, double longitude) {
        String entry[] = new String[3];
        ArrayList<String[]> output = new ArrayList<>();

        //return output;
        return null;
    }
    public String getFilename() {
        return filename;
    }
    public void storePassword(String location, String name, String password) {
        File file = context.getFilesDir();
        //If the file which contains the data exists, read it into
        //data. Else, initialize data to a new HashMap

        try {
            FileInputStream inputStream = context.openFileInput(filename);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            data = (HashMap<String, TreeMap<String, String>>) objectStream.readObject();
            inputStream.close();
            objectStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(! (data == null)) {
            System.out.println("file exists");
        } else {
            System.err.println("File does NOT exist");
            data = new HashMap<>();
        }
        if(data == null) {
            System.out.println("DATA IS NULL"); return;
        } else if(data.isEmpty()) {
            System.out.println("DATA IS EMPTY");
        }
        if(location.equals(nonGeoTaggedString)) {
            System.out.println("location == \"\"");
            if(data.containsKey(nonGeoTaggedString)) {
                System.out.println("data containsKey noLocString");
                data.get(nonGeoTaggedString).put(name, password);
            } else {
                System.out.println("data DOES NOT containkey location");
                TreeMap<String, String> temp = new TreeMap<>();
                temp.put(name, password);
                data.put(nonGeoTaggedString, temp);
            }
        } else {
            System.out.println("location is not \"\"");
            TreeMap<String, String> pair;
            if(data.containsKey(location)) {
                pair = data.get(location);
            } else {
                pair = new TreeMap<>();
            }
            pair.put(name, password);
            data.put(location, pair);
        }

        try {
            File tempFile = context.getFilesDir();
            //Creates a new fileOutputStream and sets append to false which means
            // it overwrites the file
            FileOutputStream inputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(inputStream);
            objectStream.writeObject(data);
            objectStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
