package geoguard.geoguard;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignUp extends Activity implements View.OnClickListener{

    Button btnEnter;
    EditText password;
    EditText confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnEnter = (Button) findViewById(R.id.btnEnter);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirm);

        btnEnter.setOnClickListener(this);
    }

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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEnter:
                storePassword(v);
                break;
            case R.id.btnSettings:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        //disable going back (doesnt work)
        //todo look into this
        moveTaskToBack(true);
    }

    public void storePassword(View v){
        //do they match?
        String pass1= password.getText().toString();
        String pass2= confirm.getText().toString();
        if(pass1.equals("")){
            Toast.makeText(getBaseContext(), "Invalid Password", Toast.LENGTH_LONG).show();
        }else if(!pass1.equals(pass2)){
            Toast.makeText(getBaseContext(), "Passwords Must Match", Toast.LENGTH_LONG).show();
        }else{
            startMain(v);
        }
    }

    public void startMain(View view){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
