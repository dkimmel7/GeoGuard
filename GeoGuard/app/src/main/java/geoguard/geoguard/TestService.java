package geoguard.geoguard;

import android.app.NotificationManager; // for notification
import android.app.PendingIntent; // for notification
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat; // for notifications
import android.util.Log; // For testing background boot
import android.widget.Toast;

/**
 * Created by dnalex on 11/16/2015.
 */
public class TestService extends Service {

    // Notification
    NotificationCompat.Builder notification;
    private static final int uniqueID = 123456;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        super.onCreate();

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        push();
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

        push();
        return super.onStartCommand(intent, flags, startID);
    }

    public void push() {
        // Build the notification
        notification.setSmallIcon(R.drawable.notification_template_icon_bg);
        notification.setTicker("This is a ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Here is the title.");
        notification.setContentText("I am the body text of your notification");

        Intent intent = new Intent(this, Location.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        // Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }


}
