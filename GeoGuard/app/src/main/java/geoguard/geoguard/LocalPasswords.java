package geoguard.geoguard;

/**
 * Created by monca on 11/10/2015.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class LocalPasswords extends ActionBarActivity {
    final private String filename = "loc";
    private HashMap<String, TreeMap<String,String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_passwords);
        data = createFile(filename);
        if(data != null) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
            TreeMap<String, String> tree = data.get("");
            int i = 0;
            for(final Map.Entry<String, TreeMap<String,String>> entry : data.entrySet()) {
                if (entry.getKey().equals("")) {
                    System.out.println("Continue");
                    continue;
                }
                final TreeMap<String, String> hashEntry = entry.getValue();
                for (final Map.Entry<String, String> treeEntry : hashEntry.entrySet()) {
                    System.out.println("Location = " + entry.getKey() + " name = " + treeEntry.getKey() + " value = " + treeEntry.getValue());

                    Button myButton = new Button(this);
                    myButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    myButton.setId(i);
                    myButton.setText(treeEntry.getKey());
                    myButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            System.out.println("BUTTON " + treeEntry.getKey() + "WAS CLICKED");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LocalPasswords.this);
                            builder.setMessage("Location: \n" + entry.getKey() + "\n" + "Password: " + treeEntry.getValue() + "\ncopy to clipboard?").setCancelable(true);
                            builder.setPositiveButton("Copy to clipboard", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getBaseContext(), "Copied to clipboard(not implemented)", Toast.LENGTH_LONG).show();
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

