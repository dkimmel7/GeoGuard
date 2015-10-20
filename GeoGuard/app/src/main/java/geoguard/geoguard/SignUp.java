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
                checkPassword(v);
                break;
            default:
                break;
        }
    }

    /*
    This takes the two password fields and compares them and checks they meet basic requirements
    todo check that strings are valid (alpha numeric)
     */
    public void checkPassword(View v){
        //do they match?
        SharedPreferences userId = getApplicationContext().getSharedPreferences("userId", MODE_PRIVATE);
        String existingPass = userId.getString("password", null);

        if(existingPass==(null) || existingPass.equals("")) {
            String pass1 = password.getText().toString();
            String pass2 = confirm.getText().toString();
            if (pass1.equals("")) {
                Toast.makeText(getBaseContext(), "Invalid Password", Toast.LENGTH_LONG).show();
            } else if (!pass1.equals(pass2)) {
                Toast.makeText(getBaseContext(), "Passwords Must Match", Toast.LENGTH_LONG).show();
            } else {
                storePassword(pass1);
                startMain(v);
            }
        }else{
            Toast.makeText(getBaseContext(), "profile already created", Toast.LENGTH_LONG).show();
        }
    }

    //todo Make encrypted password
    private void storePassword(String pass){
        SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences("userId",MODE_PRIVATE).edit();
        edit.putString("password", pass);
        edit.commit();
        Toast.makeText(getApplicationContext(),"Password Saved", Toast.LENGTH_LONG).show();
    }

    public void startMain(View view){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
