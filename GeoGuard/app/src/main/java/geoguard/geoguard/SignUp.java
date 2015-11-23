package geoguard.geoguard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

        Parse.initialize(this, "FAnQXaYIH3v9tMOzMG6buNMOnpDPwZZybELUFBmr", "hwOkh0Z11ZNskikNFsERhPDPT1wzdLj1SX9z5wZP");
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
        if (id.isEmpty() || id.length() < 3) {
            username.setError("must be at least three characters");
            return false;
        }
        username.setError(null);
        return checkPassword();
    }


    /*
    takes the username and checks if its the local and if not updates the salt and encoded file
    returns false if found existing username
    todo check database
     */
    private void checkUserName(View v){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final byte[] salt = encryptDecrypt.saltGenerate(getApplicationContext());
        final String id = username.getText().toString();
        final ParseObject tableName = new ParseObject("Users");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("userID", id);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() > 0) {
                        Log.d("userID", "Retrieved " + idList.size() + " deviceIDs");
                        Toast.makeText(getApplicationContext(), "User Name taken", Toast.LENGTH_LONG).show();
                    } else {
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
                        if(userprofile == null){
                            System.out.println("FALSEEEE");
                            return;
                        }else{
                            //SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                            userprofile.put("userID", id);
                            userprofile.put("salt", salt);
                            userprofile.put("radius", 10);
                            userprofile.saveInBackground();

                            SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                            settings.putString("userID", id);
                            settings.putInt("radius", 10);
                            settings.commit();
                            storePassword();
                            Intent intent = new Intent(SignUp.this, MainScreen.class);
                            startActivity(intent);

                        }
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    /*
    This takes the two password fields and compares them and checks they meet basic requirements
    todo check that strings are valid (alpha numeric)
     */
    private boolean checkPassword(){
        //do they match?
        String pass1 = password.getText().toString();
        String pass2 = confirm.getText().toString();
        if (pass1.isEmpty() || pass1.length() < 3) {
            password.setError("must be at least 3 characters");
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
            Toast.makeText(getApplicationContext(),"Password Saved", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startMain(){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }
}
