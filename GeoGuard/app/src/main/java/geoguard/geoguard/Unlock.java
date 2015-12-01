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

    byte[] masterKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean firstTime = extras.getBoolean("firstTime", true);
            if (firstTime) {
                getIntent().putExtra("firstTime", false);
                Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
            }
        }else{
            getIntent().putExtra("firstTime", false);
            Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
        }


        final Button btnEnter = (Button) findViewById(R.id.btnEnter);
        final TextView btnSignup = (TextView) findViewById(R.id.signup);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserName();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(getApplicationContext(), SignUp.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"No Network Connection\nCannot Make Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
    checks valid format
    checks to see if username matches last one used and if not updates local files

     */
    private void checkUserName() {
        final EditText username = (EditText) findViewById(R.id.username);
        final String id = username.getText().toString();
        String pattern = "^[a-zA-Z0-9!@]*$";
        if (id.isEmpty() || id.length() < 3 || id.length() > 31 || !id.matches(pattern)) {
            username.setError("must be:\n at least 3 characters \n less than 31 characters \n alpha numeric ");
            return;
        }
        if(getSharedPreferences("settings", MODE_PRIVATE).getString("userID", "-1").equals(id)){
            checkPassword();
            return;
        }
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"No Network Connection\nCannot Fetch Profile", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("userID", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() == 1) {
                        username.setError(null);
                        Log.d("userID", "Retrieved " + idList.size() + " deviceIDs");
                        if (!getSharedPreferences("settings", MODE_PRIVATE).getString("userID", "-1").equals(id)) {
                            System.out.println("Updating Profile");
                            updateUserProfile(idList.get(0));
                            System.out.println("Profile Updated");
                        }
                    } else {
                        username.setError("no user found");
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess
                        // depending on success
                        if (username.getError() != null) {
                            progressDialog.dismiss();
                            return;
                        }
                        checkPassword();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }



    /*
      precondition: profile already has been created with the required fields
      updates userID, radius, salt, passwordCheck, and files
    */
    private void updateUserProfile(ParseObject profile) {
        SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
        settings.putInt("radius", (int) profile.get("radius"));
        settings.putString("userID", profile.getString("userID"));
        if(profile.containsKey("latitude")){
            settings.putString("latitude", profile.getString("latitude"));
            settings.putString("longitude", profile.getString("longitude"));
        }else{
            settings.remove("latitude");
            settings.remove("longitude");
        }
        settings.commit();
        try{
            FileOutputStream outputStream = openFileOutput("saltFile", MODE_PRIVATE);
            outputStream.write(profile.getBytes("salt"));
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            FileOutputStream outputStream = openFileOutput("passwordCheck", MODE_PRIVATE);
            outputStream.write(profile.getBytes("passcode"));
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(profile.containsKey("passwordData")) {
            FileOutputStream outputStream;
            try {
                System.out.println("decrypting data");
                outputStream = openFileOutput("passwordData", Context.MODE_PRIVATE);
                outputStream.write(encryptDecrypt.decryptBytes(encryptDecrypt.masterKeyGenerate(((EditText) findViewById(R.id.password)).getText().toString().getBytes("UTF-8"), getApplicationContext()), getApplicationContext(), profile.getBytes("passwordData")));
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput("passwordData", Context.MODE_PRIVATE);
                outputStream.write("".getBytes("UTF-8"));
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /*
    if the password equals what is stored launch main activity
    otherwise popup that its invalid

    will throw exceptions on a bad decryption
     */
    private void checkPassword() {
        EditText password = (EditText) findViewById(R.id.password);

        String pass = password.getText().toString();
        String pattern = "^[a-zA-Z0-9!@]*$";
        if (pass.isEmpty() || pass.length() < 3 || pass.length() > 31 || !pass.matches(pattern)) {
            password.setError("must be:\n at least 3 characters \n less than 31 characters \n alpha numeric ");
            return;
        }

        try {
            FileInputStream inputStream = openFileInput("passwordCheck");
            int buffSize = (int) inputStream.getChannel().size();
            byte[] buff = new byte[buffSize];
            int reader = 0;
            while (reader != -1) {
                reader = inputStream.read(buff);
            }
            inputStream.close();
            encryptDecrypt ende = new encryptDecrypt();
            masterKey = ende.masterKeyGenerate(pass.getBytes("UTF-8"), getApplicationContext());

            byte[] decrypted = ende.decryptBytes(masterKey, getApplicationContext(), buff);
            if (Arrays.equals(decrypted, "password".getBytes("UTF-8"))) {
                password.setError(null);
                Intent intent = new Intent(this, MainScreen.class);
                intent.putExtra("masterKey", masterKey);
                startActivity(intent);
            } else {
                password.setError("Invalid password");
            }
        }catch(Exception e){
            password.setError("Invalid password");
        }

    }

}
