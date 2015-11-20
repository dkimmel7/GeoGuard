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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;


public class SignUp extends Activity implements View.OnClickListener{

    Button btnEnter;
    EditText password;
    EditText confirm;
    EditText username;
    byte[] masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnEnter = (Button) findViewById(R.id.btnEnter);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirm);
        username = (EditText) findViewById(R.id.username);

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
                if(checkUserName(v))
                    checkPassword(v);
                break;
            default:
                break;
        }
    }

    /*
    takes the username and checks if its the local and if not updates the salt and encoded file
    returns false if found existing username
    todo check database
     */
    private boolean checkUserName(View v){

        final byte[] salt= encryptDecrypt.saltGenerate(getApplicationContext());
        final String id = username.getText().toString();

        /*
        Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
        final ParseObject tableName = new ParseObject("Users");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("deviceID", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() > 0) {
                        Log.d("userID", "Retrieved " + idList.size() + " deviceIDs");
                        Toast.makeText(getApplicationContext(),"User Name taken", Toast.LENGTH_LONG).show();
                    } else {
                        tableName.put("userID", id);
                        tableName.put("salt", salt);
                        tableName.saveInBackground();
                        */
                        SharedPreferences.Editor settings = getSharedPreferences("settings" , MODE_PRIVATE).edit();
                        settings.putString("userId",id);
                        settings.commit();
                        /*
                        }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        */
        if (id.isEmpty() || id.length() < 3) {
            username.setError("must be at least three characters");
            return false;
        }else{
            username.setError(null);
        }

            if(getSharedPreferences("settings",MODE_PRIVATE).getString("userId","").equals(""))
            return false;
        return true;
    }

    /*
    This takes the two password fields and compares them and checks they meet basic requirements
    todo check that strings are valid (alpha numeric)
     */
    private void checkPassword(View v){
        //do they match?
        SharedPreferences.Editor settings = getSharedPreferences("settings" , MODE_PRIVATE).edit();
        settings.putInt("radius",10);
        settings.commit();

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

    private void storePassword(String pass){
        encryptDecrypt encryptDecryptor = new encryptDecrypt();
        try{
            //masterKey = encryptDecryptor.masterKeyGenerate(Base64.decode(pass, Base64.DEFAULT), getApplicationContext());
            masterKey = encryptDecryptor.masterKeyGenerate(pass.getBytes("UTF-8"), getApplicationContext());
            //SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences("salt",MODE_PRIVATE).edit();
//            byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), Base64.decode("password", Base64.DEFAULT));
            byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), "password".getBytes("UTF-8"));

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
