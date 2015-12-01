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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by monca on 11/23/2015.
 */
public class LocalDB {
    private HashMap<String, TreeMap<String,String>> data = null;
    final private String filename = "passwordData";
    final private String nonGeoTaggedString = "";
    private Context context = null;
    private byte[] key;

    LocalDB(Context context, byte[] key) {
        this.context = context;
        this.key = key;
        openFile();
    }
    //Loads the file saved in filename and puts it in data if it can, data will be null otherwise
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

    private void updateOnlineDB(){
        FileInputStream inputStream;
        final byte[] buff;
        try{
            inputStream = context.openFileInput(filename);
            int buffSize = (int)inputStream.getChannel().size();
            buff = new byte[buffSize];
            int reader = inputStream.read(buff);
            while(reader != -1){
                reader = inputStream.read(buff);
            }
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("userID", context.getSharedPreferences("settings", context.MODE_PRIVATE).getString("userID", ""));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() == 1) {
                        try{
                            idList.get(0).put(filename, encryptDecrypt.encryptBytes(key, context, buff));
                            idList.get(0).saveInBackground();
                        }catch (Exception f){
                            f.printStackTrace();
                        }
                    } else {
                        System.err.println("Something went wrong");
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        encryptDecrypt.encryptDecryptFile(filename, false, key, context);
    }

    //Returns an ArrayList of String arrays, each array contains the location, name, and password
    //at array[0],array[1],and array[2], respectively.
    //To use, call the function and save the result to a string array,
    // e.g. String array[] = getAllPasswords();
    public ArrayList<String[]> getAllTaggedPasswords() {
        ArrayList<String[]> output = new ArrayList<>();
        boolean hasEntry = false;
        //entry.getKey is the Location and entry.getValue is the TreeMap containing all the
        //Name, Password pairs geotagged to the Location
        for(final Map.Entry<String, TreeMap<String,String>> entry : data.entrySet()) {
            if (entry.getKey().equals("") || entry.getKey().equals("Home Base")) {
                continue;
            }
            final TreeMap<String, String> hashEntry = entry.getValue();
            //Iterates through all the Name, Value pairs tagged to the Location entry.getKey()
            for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                String entryData[] = new String[3];
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
            hasEntry = true;
        }

        // Reveals database as empty for notification background service
        if (!hasEntry) { output = null; }

        return output;
    }
    //Returns an ArrayList of String arrays which contain 2 elements, the password name in index 0
    //and the password in index 1. Only 2 indices in arrays since these are non-geotagged
    public ArrayList<String[]> getAllNonTaggedPasswords() {
        ArrayList<String[]> output = new ArrayList<>();
        for(final Map.Entry<String, TreeMap<String,String>> entry : data.entrySet()) {
            if (entry.getKey().equals("")) {
                final TreeMap<String, String> hashEntry = entry.getValue();
                //Iterates through all the Name, Value pairs tagged to the Location entry.getKey()
                for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                    String entryData[] = new String[2];
                    System.out.println("Location = " + entry.getKey() + " name = " + treeEntry.getKey() + " password = " + treeEntry.getValue());
                    //entryData[0] will get the name which was associated with the password, aka password name
                    entryData[0] = treeEntry.getKey();
                    //entryData[1] will get the password
                    entryData[1] = treeEntry.getValue();
                    //output is an ArrayList of String arrays, each array contains a Location, Name, and Password
                    output.add(entryData);
                }
            } else continue;
        }
        return output;
    }
    public String retrievePassword(String location, String name) {
        if(data == null) {
            System.err.println("DATA IS NULL IN RETRIEVEPASSWORD");
            return null;
        }
        TreeMap<String, String> hashEntry = data.get(location);
        if(hashEntry == null) {
            return null;
        }
        return hashEntry.get(name);


    }
    //Returns the distance between two locations using 2 lat+long pairs using the Haversine formula
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
    //Returns true if the HashMap contains the location which will be one of the HashMap's keys
    //Retrusn false otherwise
    public boolean hasLocation(String location) {
        if(data == null) {
            return false;
        }
        if(data.containsKey(location)) {
            return true;
        }
        return false;
    }
    //Returns true if any TreeMap in the HashMap contains the name
    //Iterates through the HashMap entries which are location+TreeMap pairs
    //Then Iterates through the TreeMap which contains name+password pairs
    public boolean hasName(String name) {
        if(data == null) {
            return false;
        }
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
    //Extracts the latitude and longitude of the input string which should be of the form "lat long"
    //Saves the two doubles in a double array with two elements and returns it, returns null if
    //parseDouble fails
    private double[] stringToDoublePair(String input) {
        double[] output = null;
        try {
            output = new double[2];
            output[0] = Double.parseDouble(input.substring(0, input.indexOf(" ")));
            output[1] = Double.parseDouble(input.substring(input.indexOf(" ")));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    //Returns an ArrayList of String arrays with all the locations, names, and passwords within
    //the search radius of the location given as latitude and longitude.
    public ArrayList<String[]> nearbyPasswords(double latitude, double longitude) {
        SharedPreferences settings = context.getSharedPreferences("settings" , context.MODE_PRIVATE);
        int radius = settings.getInt("radius", 0);
        ArrayList<String[]> output = new ArrayList<>();
        for (Map.Entry<String, TreeMap<String, String>> entry : data.entrySet()) {
            double loc[] = stringToDoublePair(entry.getKey());
            double taggedLat = loc[0];
            double taggedLong = loc[1];
            if (getDist(taggedLat, taggedLong, latitude, longitude) <= radius && getDist(taggedLat, taggedLong, latitude, longitude) >= 0) {
                TreeMap<String, String> tree = entry.getValue();
                for (Map.Entry<String, String> treeEntry : tree.entrySet()) {
                    String treeData[] = new String[3];
                    treeData[0] = entry.getKey();
                    treeData[1] = treeEntry.getKey();
                    treeData[2] = treeEntry.getValue();
                    output.add(treeData);
                }
            }

        }
        return output;
    }
    public HashMap<String, TreeMap<String, String>> returnData() {
        return data;
    }
    //Returns the name of the file to which the local database is saved
    public String getFilename() {
        return filename;
    }

    //Takes location and name to remove and removes the name+password pair from the TreeMap,
    //if the TreeMap is empty after deletion, delete the location+TreeMap pair from the HashMap
    public void delete(String location, String name) {
        if(data == null) {
            return;
        }
        TreeMap<String, String> hashEntry = data.get(location);
        if(hashEntry == null) {
            return;
        }
        hashEntry.remove(name);
        if(hashEntry.isEmpty()) {
            data.remove(location);
        }
        //Stores data HashMap to file specified by filename
        try {
            //Creates a new fileOutputStream and sets append to false which means
            // it overwrites the file
            FileOutputStream inputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(inputStream);
            objectStream.writeObject(data);
            updateOnlineDB();
            objectStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //returns a string in the form "latitude longitude" which has the latitude and longitude of the
    //home base for the signed in profile
    public String getHomeBaseCoords() {
        TreeMap<String, String> treeEntry = data.get("Home Base");
        String homeBaseCoords = treeEntry.get("Home Base");
        return homeBaseCoords;
    }
    //Stores the password using location as a key in the HashMap and name as a key in the TreeMap and
    //the password as the value associated to the TreeMap key
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
        //nonGeoTaggedString is the string which will be a key in the hashMap and the TreeMap
        //stored there will contain all non-geotagged names+passwords
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

        //Stores data HashMap to file specified by filename
        try {
            //Creates a new fileOutputStream and sets append to false which means
            // it overwrites the file
            FileOutputStream inputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(inputStream);
            objectStream.writeObject(data);
            updateOnlineDB();
            objectStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
