package com.example.steve.poke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by steve on 15/05/2017.
 */
public class NetworkChange extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean pokeOnNetworkChange = prefs.getBoolean("netchange_switch", false);

        if(pokeOnNetworkChange) {
            // Create a new poker object
            Poker poker = new Poker(context);

            // Send a poke but don't be noisy about it!
            poker.sendPoke(false, false);
        }
    }
}
