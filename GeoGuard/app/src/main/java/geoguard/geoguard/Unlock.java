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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;


public class Unlock extends Activity{

    Button btnEnter;
    EditText password;
    TextView btnSignup;
    byte[] masterKey;

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
                intent.putExtra("unlockContext", "test");
                startActivity(intent);
            }
        });

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
    public void onBackPressed(){
        moveTaskToBack(true);
    }



    /*
    if the password equals what is stored launch main activity
    otherwise popup that its invalid
     */
    private void checkPassword(View v) {
        try {
            FileInputStream inputStream = openFileInput("passwordCheck");
            int buffSize = (int) inputStream.getChannel().size();
            byte[] buff = new byte[buffSize];
            int reader = 0;
            while (reader != -1) {
                reader = inputStream.read(buff);
            }
            inputStream.close();
            String pass = password.getText().toString();
            encryptDecrypt ende = new encryptDecrypt();
            masterKey = ende.masterKeyGenerate(Base64.decode(pass, Base64.DEFAULT), getApplicationContext());
            System.out.println("masterkey:" + Arrays.toString(masterKey));
            byte[] decrypted = ende.decryptBytes(masterKey, getApplicationContext(), buff);
            if (Arrays.equals(decrypted, Base64.decode("password", Base64.DEFAULT))) {
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(getBaseContext(), "invalid password", Toast.LENGTH_LONG).show();
            }
        } catch(FileNotFoundException e){
            Toast.makeText(getBaseContext(), "Please Create Profile" , Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(getBaseContext(), "Invalid Password" , Toast.LENGTH_LONG).show();
        }

    }

}
