package geoguard.geoguard;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Settings extends ActionBarActivity {

    Button btnUsernameChange;
    Button btnPasswordChange;
    Button btnRadiusChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnUsernameChange = (Button) findViewById(R.id.btnUsernameChange);
        btnPasswordChange = (Button) findViewById(R.id.btnPasswordChange);
        btnRadiusChange = (Button) findViewById(R.id.btnRadius);

        btnUsernameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername(v);
            }
        });

        btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });

        btnRadiusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRadius(v);
            }
        });
    }

    private void changeUsername(View v){

    }

    private void changePassword(View v){

    }

    private void changeRadius(View v){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
