package geoguard.geoguard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Unlock extends Activity {

    Button btnEnter;
    EditText password;
    TextView btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        btnEnter = (Button) findViewById(R.id.btnEnter);
        password = (EditText) findViewById(R.id.password);
        btnSignup = (TextView) findViewById(R.id.signup);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword(v);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

    }

 /*   @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnEnter:
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                break;
            case R.id.signup:
                Intent intent2 = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unlock, menu);
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
        //disable going back (doesnt work)
        //todo look into this
        moveTaskToBack(true);
    }

    /*
    if the password equals what is stored launch main activity
    otherwise popup that its invalid
     */
    //todo make things private
    public void checkPassword(View v){
        String pass1= password.getText().toString();

        SharedPreferences userId = getApplicationContext().getSharedPreferences("userId", MODE_PRIVATE);
        String pass2 = userId.getString("password", "");

        if( pass2.equals("")) {
            Toast.makeText(getBaseContext(), "no password found" , Toast.LENGTH_LONG).show();
        }else if(!pass1.equals(pass2)){
            Toast.makeText(getBaseContext(), "invalid password" , Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
        }
    }

}
