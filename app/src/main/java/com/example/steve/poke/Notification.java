package com.example.steve.poke;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by steve on 15/05/2017.
 */

public class Notification {

    private Context context;

    public Notification(Context context)
    {
        this.context = context;
    }

    public void modifyNotification(boolean showNotification)
    {
        final int notificationId = 69;

        // Get a reference to the notification manager
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Decide whether to show or hide the notification
        if(showNotification)
        {
            // Show notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle("Poke")
                            .setContentText("Currently poking...")
                            .setOngoing(true)
                            .setPriority(-2);


            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(context, SettingsActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(SettingsActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);



            // mId allows you to update the notification later on.
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
        else
        {
            // Just cancel the notification
            mNotificationManager.cancel(notificationId);
        }
    }
}
