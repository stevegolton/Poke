package com.example.steve.poke;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
public class PokePreferenceChangeListener implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        // Get the value as a string
        String stringValue = value.toString();

        // Update the summary field if this is a list preference
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        }

        // Update the summary field if this is a text preference
        if (preference instanceof EditTextPreference) {
            preference.setSummary(stringValue);
        }

        // Get our context
        Context context = preference.getContext();

        // Get a handle on the shared preference manager
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (preference.getKey().equals("enable_switch")) {

            boolean enabled = (boolean)value;

            // Show the correct toast
            if (enabled) {
                Toast.makeText(context, "Turning recurring poke on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Turning recurring poke off", Toast.LENGTH_SHORT).show();
            }

            // Find out our interval from the previous settings
            int interval = Integer.parseInt(prefs.getString("frequency_text", "60"));
            boolean notificationEnabled = prefs.getBoolean("notif_switch", false);

            Alarm alarm = new Alarm(context);
            alarm.updateAlarm(enabled, interval);

            Notification notification = new Notification(context);
            notification.modifyNotification(notificationEnabled && enabled);

        } else if (preference.getKey().equals("frequency_text")) {

            boolean enabled = prefs.getBoolean("enable_switch", false);
            int interval = Integer.parseInt((String)value);

            Toast.makeText(context, "Changing poke interval to " + interval + " seconds", Toast.LENGTH_SHORT).show();

            Alarm alarm = new Alarm(context);
            alarm.updateAlarm(enabled, interval);

        } else if (preference.getKey().equals("notif_switch")) {
            boolean enabled = prefs.getBoolean("enable_switch", false);
            boolean notificationEnabled = (boolean)value;

            // Update the notification
            Notification notification = new Notification(context);
            notification.modifyNotification(notificationEnabled && enabled);

        } else if (preference.getKey().equals("server_text")) {
            Toast.makeText(context, "Server hostname set to " + (String)value, Toast.LENGTH_SHORT).show();

        } else if (preference.getKey().equals("port_text")) {
            Toast.makeText(context, "Server port set to " + (String)value, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        Context context = preference.getContext();

        Poker poker = new Poker(context);

        // Fire off a test if the test button is pressed
        if(preference.getKey().equals("test_button")) {
            poker.sendPoke(true, true);
        }
        return true;
    }
}
