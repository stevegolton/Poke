package com.example.steve.poke;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity{

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private PokePreferenceChangeListener sBindPreferenceSummaryToValueListener;
    private Poker poker;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // Adds the preferences and builds the interface
        addPreferencesFromResource(R.xml.pref_general);

        // Create a listener for changes to preferences, passing ourselves as a reference
        poker = new Poker(this);
        alarm = new Alarm(this);
        sBindPreferenceSummaryToValueListener = new PokePreferenceChangeListener();

        // Update the text preferences summary fields from their values as per the android standard
        setPreferenceSummaryToValue(findPreference("server_text"));
        setPreferenceSummaryToValue(findPreference("port_text"));
        setPreferenceSummaryToValue(findPreference("frequency_text"));

        // Listen to changes from all preferences
        findPreference("enable_switch").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("boot_switch").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("server_text").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("port_text").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("frequency_text").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("wifi_switch").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("netchange_switch").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        findPreference("notif_switch").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Listen for when the test button is pressed
        Preference button = findPreference("test_button");

        button.setOnPreferenceClickListener(sBindPreferenceSummaryToValueListener);

        // Register for connectivity changes
        // TODO: Sometimes crashes?? Not sure why? Perhaps the new NetworkChange() object is getting GC'ed before this has a chance to trigger? I feel like the registerEvent handler should take a class rather than an object...
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChange(), filter);

        reloadSettings(this);
    }

    public static void reloadSettings(Context context)
    {
        // Start alarm and reload settings and things
        // Get a handle on the shared preference manager
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = prefs.getBoolean("enable_switch", false);
        int interval = Integer.parseInt(prefs.getString("frequency_text", "60"));
        boolean notificationEnabled = prefs.getBoolean("notif_switch", false);

        Alarm alarm = new Alarm(context);
        alarm.updateAlarm(enabled, interval);

        Notification notification = new Notification(context);
        notification.modifyNotification(enabled && notificationEnabled);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void setPreferenceSummaryToValue(Preference preference) {

        String value = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");

        preference.setSummary(value);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
