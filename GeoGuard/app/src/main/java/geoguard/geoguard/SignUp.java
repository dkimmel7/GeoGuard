package geoguard.geoguard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;


public class SignUp extends Activity implements View.OnClickListener{

    Button btnEnter;
    EditText password;
    EditText confirm;
    byte[] masterKey;
    Context unlockContext;

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
    private void checkPassword(View v){
        //do they match?
        try{
            openFileInput("saltFile");
            Toast.makeText(getBaseContext(), "profile already created", Toast.LENGTH_LONG).show();
        }catch (Exception e){
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
        }
    }

    //todo Make encrypted password
    private void storePassword(String pass){
        encryptDecrypt encryptDecryptor = new encryptDecrypt();
        try{
            masterKey = encryptDecryptor.masterKeyGenerate(Base64.decode(pass, Base64.DEFAULT), getApplicationContext());
            //SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences("salt",MODE_PRIVATE).edit();
            byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), Base64.decode("password", Base64.DEFAULT));
            FileOutputStream outputStream = openFileOutput("passwordCheck", MODE_PRIVATE);
            outputStream.write(password);
            outputStream.close();
            Toast.makeText(getApplicationContext(),"Password Saved", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startMain(View view){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
