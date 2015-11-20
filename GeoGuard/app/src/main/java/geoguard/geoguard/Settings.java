package geoguard.geoguard;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileOutputStream;


public class Settings extends ActionBarActivity {

    Button btnUsernameChange;
    Button btnPasswordChange;
    Button btnRadiusChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnUsernameChange = (Button) findViewById(R.id.btnUsernameChange);
        btnPasswordChange = (Button) findViewById(R.id.btnPasswordChange);
        btnRadiusChange = (Button) findViewById(R.id.btnRadius);

        btnUsernameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername(v);
            }
        });

        btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });

        btnRadiusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRadius(v);
            }
        });
    }

    //todo check database
    private void changeUsername(View v){
        final Dialog usrDialog = new Dialog(this);
        usrDialog.setTitle("Change Username:");
        usrDialog.setContentView(R.layout.change_username);

        final EditText usernameText = (EditText)usrDialog.findViewById(R.id.usernameChange);
        Button btnenter = (Button)usrDialog.findViewById(R.id.btnEnter);
        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = usernameText.getText().toString();
                if (id.isEmpty() || id.length() < 3) {
                    usernameText.setError("must be at least three characters");
                } else {

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
                        tableName.saveInBackground();
                        */
                    SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                    settings.putString("userId", id);
                    settings.commit();
                        /*
                        }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        */

                    usernameText.setError(null);
                    usrDialog.dismiss();
                }
            }
        });

        usrDialog.show();

    }

    private void changePassword(View v){
        final Dialog usrDialog = new Dialog(this);
        usrDialog.setTitle("Change Password:");
        usrDialog.setContentView(R.layout.change_password);

        final EditText password1 = (EditText)usrDialog.findViewById(R.id.PasswordChange1);
        final EditText password2 = (EditText)usrDialog.findViewById(R.id.PasswordChange2);
        Button btnenter = (Button)usrDialog.findViewById(R.id.btnEnter);
        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = password1.getText().toString();
                String pass2 = password2.getText().toString();
                if (pass1.equals("")) {
                    password2.setError("Invalid Password");
                } else if (!pass1.equals(pass2)) {
                    password2.setError("Passwords Must Match");
                } else {
                    storePassword(pass1);
                    password1.setError(null);
                    usrDialog.dismiss();
                }

            }
        });
        usrDialog.show();
    }

    private void storePassword(String pass){
        encryptDecrypt encryptDecryptor = new encryptDecrypt();
        try{
            byte[] masterKey = encryptDecryptor.masterKeyGenerate(Base64.decode(pass, Base64.DEFAULT), getApplicationContext());
            byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), Base64.decode("password", Base64.DEFAULT));
            FileOutputStream outputStream = openFileOutput("passwordCheck", MODE_PRIVATE);
            outputStream.write(password);
            outputStream.close();
            Toast.makeText(getApplicationContext(),"Password Saved", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeRadius(View v){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
}
