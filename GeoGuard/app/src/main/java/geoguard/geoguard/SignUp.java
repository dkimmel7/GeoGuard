package geoguard.geoguard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class SignUp extends Activity implements View.OnClickListener{

    Button btnEnter;
    EditText password;
    EditText confirm;
    EditText username;
    byte[] masterKey;
    ParseObject userprofile;

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
                if(checkFields(v))
                    checkUserName(v);
                break;
            default:
                break;
        }
    }


    /*
    Checks for valid fields
    returns false on failure
     */
    private boolean checkFields(View v){
        final String id = username.getText().toString();
        String pattern = "^[a-zA-Z0-9!@]*$";
        if (id.isEmpty() || id.length() < 3 || id.length() > 31 || !id.matches(pattern)) {
            username.setError("must be:\n at least 3 characters \n less than 31 characters \n alpha numeric ");
            return false;
        }
        username.setError(null);
        return checkPassword();
    }

    /*
    Checks network connection
    true on success
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    takes the username and checks if its the local and if not updates the salt and encoded file
    returns if found existing username
    if it is unique sets all appropriate info and sends to the server
     */
    private void checkUserName(View v){
        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "No Network Connection\nCannot Create Profile", Toast.LENGTH_SHORT).show();
            return;
        }
            final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String id = username.getText().toString();
        final byte[] salt = encryptDecrypt.saltGenerate(getApplicationContext());
        final ParseObject tableName = new ParseObject("Users");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("userID", id);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() > 0) {
                        Log.d("userID", "Retrieved " + idList.size() + " deviceIDs");
                        username.setError("username taken");
                    } else {
                        username.setError(null);
                        userprofile = tableName;
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        if(userprofile != null){
                            //SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                            userprofile.put("userID", id);
                            userprofile.put("salt", salt);
                            userprofile.put("radius", 10);
                            userprofile.put("passwordData",("").getBytes());
                            userprofile.saveInBackground();

                            SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                            settings.putString("userID", id);
                            settings.putInt("radius", 10);
                            settings.commit();
                            storePassword();

                            FileOutputStream outputStream;
                            try {
                                outputStream = openFileOutput("passwordData", Context.MODE_PRIVATE);
                                outputStream.write("".getBytes("UTF-8"));
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(SignUp.this, MainScreen.class);
                            startActivity(intent);

                        }
                        progressDialog.dismiss();
                    }
                }, 1500);


    }

    /*
    This takes the two password fields and compares them and checks they meet basic requirements
    todo check that strings are valid (alpha numeric)
     */
    private boolean checkPassword(){
        //do they match?
        String pattern = "^[a-zA-Z0-9!@]*$";
        String pass1 = password.getText().toString();
        String pass2 = confirm.getText().toString();
        if (pass1.isEmpty() || pass1.length() < 3 || pass1.length() > 31 || !pass1.matches(pattern)) {
            password.setError("must be:\n at least 3 characters \n less than 31 characters \n alpha numeric ");
            return false;
        } else if (pass1.equals(pass2)) {
            password.setError(null);
            return true;
        } else {
            password.setError("Passwords Must Match");
            return false;
        }
    }

    /*
    precondition: has already checked username and stored the parse object
    will encode password based on salt and user entered password and store the encoded string
     */
    private void storePassword(){
        String pass=password.getText().toString();
        encryptDecrypt encryptDecryptor = new encryptDecrypt();
        try{
            masterKey = encryptDecryptor.masterKeyGenerate(pass.getBytes("UTF-8"), getApplicationContext());
            byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), "password".getBytes("UTF-8"));
            FileOutputStream outputStream = openFileOutput("passwordCheck", MODE_PRIVATE);
            outputStream.write(password);
            outputStream.close();
            userprofile.put("passcode", password);
            userprofile.saveInBackground();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
