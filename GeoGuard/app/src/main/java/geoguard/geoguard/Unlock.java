package geoguard.geoguard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;


public class Unlock extends Activity{

    Button btnEnter;
    EditText password;
    EditText username;
    TextView btnSignup;
    byte[] masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        btnEnter = (Button) findViewById(R.id.btnEnter);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        btnSignup = (TextView) findViewById(R.id.signup);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUserName(v))
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


    private boolean checkUserName(View v) {

        /*
        final String id = username.getText().toString();
        Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
        final ParseObject tableName = new ParseObject("Users");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("deviceID", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() == 1) {
                        Log.d("userID", "Retrieved " + idList.size() + " deviceIDs");
                        if(!getSharedPreferences("settings",MODE_PRIVATE).getString("userId","").equals(id))
                            updateUserProfile(idList.get(0));
                    } else {
                        Toast.makeText(getApplicationContext(),"No Profile Found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        if(getSharedPreferences("settings",MODE_PRIVATE).getString("userId","").equals(""))
            return false;
            return true;
            */
        if (getSharedPreferences("settings", MODE_PRIVATE).getString("userId", "").equals("")) {
            Toast.makeText(getApplicationContext(), "No Profile Found", Toast.LENGTH_LONG).show();
            return false;
        } else if (getSharedPreferences("settings", MODE_PRIVATE).getString("userId", "").equals(username.getText().toString())) {
            return true;
        }
        Toast.makeText(getApplicationContext(), "Wrong profile", Toast.LENGTH_LONG).show();
        return true;
    }

    private void updateUserProfile(ParseObject profile){
        String id = (String) profile.get("userId");
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
            //new String(pass, "UTF-8");
            //masterKey = ende.masterKeyGenerate(Base64.decode(pass, Base64.DEFAULT), getApplicationContext());
            masterKey = ende.masterKeyGenerate(pass.getBytes("UTF-8"), getApplicationContext());

            byte[] decrypted = ende.decryptBytes(masterKey, getApplicationContext(), buff);
            if (Arrays.equals(decrypted, "password".getBytes("UTF-8"))) {
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(getBaseContext(), "invalid password", Toast.LENGTH_LONG).show();
            }
        } catch(FileNotFoundException e){
            Toast.makeText(getBaseContext(), "No Profile Found" , Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(getBaseContext(), "Invalid Password" , Toast.LENGTH_LONG).show();
        }

    }

}
