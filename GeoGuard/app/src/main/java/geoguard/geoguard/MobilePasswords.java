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
    private String filename = "";
    private HashMap<String, TreeMap<String,String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_passwords);
        LocalDB database = new LocalDB(MobilePasswords.this);
        filename = database.getFilename();
        createFile();
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
