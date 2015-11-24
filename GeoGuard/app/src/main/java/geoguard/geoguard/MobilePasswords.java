package geoguard.geoguard;

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


public class MobilePasswords extends ActionBarActivity {
    private String filename = "loc";
    private HashMap<String, TreeMap<String,String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_passwords);
        createFile();
        if(data != null) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
            TreeMap<String, String> tree = data.get("");
            int i = 0;
            if(tree != null)
                for(final Map.Entry<String, String> entry : tree.entrySet()) {
                System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());

                Button myButton = new Button(this);
                myButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                myButton.setId(i);
                myButton.setText(entry.getKey());
                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.out.println("BUTTON " + entry.getKey() + "WAS CLICKED");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MobilePasswords.this);
                        builder.setMessage("Password: " + entry.getValue() + "\ncopy to clipboard?").setCancelable(true);
                        builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getBaseContext(), "Copied to clipboard(not implemented)", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getBaseContext(), "Password removed(not implemented)", Toast.LENGTH_LONG).show();

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
        } else System.out.println("data is null");
    }
    private void createFile() {
        data = null;
        try {
            FileInputStream noLoc = openFileInput(filename);
            ObjectInputStream oNoLoc = new ObjectInputStream(noLoc);
            data = (HashMap<String, TreeMap<String, String>>) oNoLoc.readObject();
            noLoc.close();
            oNoLoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mobile_passwords, menu);
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
