package geoguard.geoguard;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


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
                //Intent local = new Intent(this, LocalPasswords.class);
                //startActivity(local);
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
