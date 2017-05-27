package com.example.steve.poke;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by steve on 30/04/2017.
 */

public class Alarm extends BroadcastReceiver {

    private Context context;

    Alarm()
    {
        // Nothing to do
    }

    Alarm(Context context)
    {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a new poker object
        Poker poker = new Poker(context);

        // Send a poke but don't be noisy about it!
        // Be noisy for debug!
        poker.sendPoke(false, false);
    }

    public void updateAlarm(boolean enabled, int interval)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        // Cancel the alarm
        am.cancel(pi);

        // If alarm is enabled, restart
        if(enabled) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * interval, pi); // Millisec * Second * Minute
        }
    }
}