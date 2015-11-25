package geoguard.geoguard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileOutputStream;
import java.util.List;


public class Settings extends AppCompatActivity {

    Button btnUsernameChange;
    Button btnPasswordChange;
    Button btnRadiusChange;
    Button btnSetHomeBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnUsernameChange = (Button) findViewById(R.id.btnUsernameChange);
        btnPasswordChange = (Button) findViewById(R.id.btnPasswordChange);
        btnRadiusChange = (Button) findViewById(R.id.btnRadius);
        btnSetHomeBase = (Button) findViewById(R.id.btnHomebaseSet);

        btnUsernameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        btnPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnRadiusChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRadius();
            }
        });

        btnSetHomeBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHomeBase();
            }
        });

    }



    //todo check database
    private void changeUsername() {
        final Dialog usrDialog = new Dialog(this);
        usrDialog.setTitle("Change Username:");
        usrDialog.setContentView(R.layout.change_username);
        final TextView text = (TextView)usrDialog.findViewById(R.id.text);
        text.setText("Current Username: " + getSharedPreferences("settings", MODE_PRIVATE).getString("userID", ""));

        final EditText usernameText = (EditText) usrDialog.findViewById(R.id.usernameChange);
        Button btnenter = (Button) usrDialog.findViewById(R.id.btnEnter);
        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newId = usernameText.getText().toString();
                if (newId.isEmpty() || newId.length() < 3) {
                    usernameText.setError("must be at least three characters");
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                    query.whereEqualTo("userID", newId);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(final List<ParseObject> idList, ParseException e) {
                            if (e == null) {
                                if (idList.size() > 0) {
                                    usernameText.setError("User Name Taken");
                                } else {
                                    usernameText.setError(null);

                                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Users");
                                    query2.whereEqualTo("userID", getSharedPreferences("settings", MODE_PRIVATE).getString("userID", ""));
                                    query2.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> idList2, ParseException e) {
                                            if (e == null) {
                                                if (idList2.size() == 1) {
                                                    idList2.get(0).put("userID", newId);
                                                    SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                                                    settings.putString("userID", newId);
                                                    settings.commit();
                                                    idList2.get(0).saveInBackground();
                                                } else {
                                                    System.err.println("Something went wrong");
                                                }
                                            } else {
                                                Log.d("score", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                    usrDialog.dismiss();
                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                }
            }
        });
        usrDialog.show();
    }

    private void changePassword(){
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
            byte[] masterKey = encryptDecryptor.masterKeyGenerate(pass.getBytes("UTF-8"), getApplicationContext());
            final byte[] password = encryptDecryptor.encryptBytes(masterKey, getApplicationContext(), "password".getBytes("UTF-8"));

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
            query.whereEqualTo("userID", getSharedPreferences("settings", MODE_PRIVATE).getString("userID", ""));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> idList, ParseException e) {
                    if (e == null) {
                        if (idList.size() == 1) {
                            idList.get(0).put("passcode", password);
                            idList.get(0).saveInBackground();
                        } else {
                            System.err.println("Something went wrong");
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
            FileOutputStream outputStream = openFileOutput("passwordCheck", MODE_PRIVATE);
            outputStream.write(password);
            outputStream.close();

            Toast.makeText(getApplicationContext(),"Password Saved", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeRadius(){
        final Dialog usrDialog = new Dialog(this);
        usrDialog.setTitle("Change Radius:");
        usrDialog.setContentView(R.layout.change_radius);

        Button btnSmall = (Button)usrDialog.findViewById(R.id.btnSmall);
        Button btnMedium = (Button)usrDialog.findViewById(R.id.btnMedium);
        Button btnLarge = (Button)usrDialog.findViewById(R.id.btnLarge);

        btnSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor settings = getSharedPreferences("settings" , MODE_PRIVATE).edit();
                saveRadius(15);
                settings.commit();
                usrDialog.dismiss();
            }
        });
        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRadius(30);
                usrDialog.dismiss();
            }
        });
        btnLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRadius(60);
                usrDialog.dismiss();
            }
        });

        usrDialog.show();
    }

    private void saveRadius(final int val){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("userID", getSharedPreferences("settings", MODE_PRIVATE).getString("userID", ""));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> idList, ParseException e) {
                if (e == null) {
                    if (idList.size() == 1) {
                        idList.get(0).put("radius", val);
                        SharedPreferences.Editor settings = getSharedPreferences("settings" , MODE_PRIVATE).edit();
                        settings.putInt("radius", val);
                        settings.commit();
                        idList.get(0).saveInBackground();
                    } else {
                        System.err.println("Something went wrong");
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void setHomeBase(){
        final Dialog usrDialog = new Dialog(this);
        usrDialog.setTitle("Set HomeBase:");
        usrDialog.setContentView(R.layout.set_homebase);
        Button btnSet = (Button)usrDialog.findViewById(R.id.btnSet);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker gps = new Tracker(getApplicationContext());
                if (gps.canGetLocation()) {
                    final double latitude = gps.getLatitude();
                    final double longitude = gps.getLongitude();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
                    query.whereEqualTo("userID", getSharedPreferences("settings", MODE_PRIVATE).getString("userID", ""));
                    query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> idList, ParseException e) {
                        if (e == null) {
                            if (idList.size() == 1) {
                                idList.get(0).put("homeLatitude", latitude);
                                idList.get(0).put("homeLongitude", longitude);
                                SharedPreferences.Editor settings = getSharedPreferences("settings", MODE_PRIVATE).edit();
                                //System.out.println("SETTINGS latitude = " + latitude + " longitude = " + longitude);
                                settings.putString("latitude", Double.toString(latitude));
                                settings.putString("longitude", Double.toString(longitude));
                                //settings.putLong("latitudeAsLong", Double.doubleToRawLongBits(latitude));
                                //settings.putLong("longitudeAsLong",  Double.doubleToRawLongBits(longitude));
                                settings.commit();
                                idList.get(0).saveInBackground();
                            } else {
                                System.err.println("Something went wrong");
                            }
                        } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
                usrDialog.dismiss();
            }
        });
        usrDialog.show();
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
