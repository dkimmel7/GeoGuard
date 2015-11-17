package geoguard.geoguard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log; // For testing background boot
import android.widget.Toast;

/**
 * Created by dnalex on 11/16/2015.
 */
public class TestService extends Service {

    Notification note;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        super.onCreate();
        //loc.check();
        note = new Notification();
        note.push();
        Log.d("Service111111", "App started@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Toast.makeText(getApplicationContext(), "Service Working", Toast.LENGTH_LONG).show();
        Log.d("Service222222", "App started@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        note = new Notification();
        note.push();
        return super.onStartCommand(intent, flags, startID);


    }
}