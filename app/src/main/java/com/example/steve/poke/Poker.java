package com.example.steve.poke;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by steve on 30/04/2017.
 */

public class Poker {

    private Context context;
    private Alarm alarm;

    Poker(Context context)
    {
        this.context = context;
    }

    public void sendPoke(final boolean noisy, boolean overrideWifiOnly)
    {
        // Set server and port settings from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Get values from shared preference manager
        String hostname = prefs.getString("server_text", "localhost");
        int port = Integer.parseInt(prefs.getString("port_text", "80"));

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean isOnWiFi = isConnected && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);

        boolean onlyOnWifi = prefs.getBoolean("wifi_switch", false);

        if(!onlyOnWifi || isOnWiFi || overrideWifiOnly) {

            // Instantiate the RequestQueue.
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(context);

            // Build the url from the code
            String url = "http://" + hostname + ":" + Integer.toString(port);

            //if(noisy) Toast.makeText(context, url, Toast.LENGTH_SHORT).show();

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (noisy)
                                Toast.makeText(context, "Poke sent OK", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (noisy)
                        Toast.makeText(context, "Error while sending poke " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the queue
            queue.add(stringRequest);
        }
        else
        {
            // Don't do anything here!

            //Toast.makeText(context, "I wanted to send a poke, but the wifi was off master said and " +
            //        "we are only allowed to send when wifi is on!!", Toast.LENGTH_SHORT).show();
        }
    }
}
