package com.google.developer.colorvalue.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.developer.colorvalue.MainActivity;
import com.google.developer.colorvalue.R;

/**
 * Created by clinton on 3/17/18.
 */

public class NotificationUtils {

    private static final int STUDY_REMINDER_NOTIFICATION_ID = 3000;
    private static final int STUDY_REMINDER_PENDING_INTENT_ID = 3100;
    private static final String PREF_IS_FIRST_NOTIFICATION = "is-first-notification";


    /**
     * Creates, designs and displays the notification to the user
     * @param context The context of the application
     */
    public static void displayStudyReminderNotification(Context context){


        String notificationChannelId = context.getString(R.string.notification_channel_id);
        String notificationChannelName = context.getString(R.string.notification_channel_name);
        String title =null;
        String content = null;


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager==null){
            return;
        }

        /*  Create a channel for Android Oreo devices   */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel studyReminderChannel = new NotificationChannel(
                    notificationChannelId,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(studyReminderChannel);
        }

        /*  Build the notification  */
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,notificationChannelId)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.ic_dialog_info)
                        .setLargeIcon(getLargeIcon(context))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true);

            /*  Notifications turned on before. Time to set the reminder */
            title = context.getString(R.string.time_to_practice);
            content = context.getString(R.string.it_is_time_to_practice);
            notificationBuilder.setContentIntent(contentIntent(context));

        /*  Set the title and the content   */
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);



        /*  Set the priority to high for devices prior to Oreo and greater than 22*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        /* Notify the user  */
        notificationManager.notify(STUDY_REMINDER_NOTIFICATION_ID,notificationBuilder.build());
    }

    /**
     * Creates and returns a pending intent to open the main activity.
     * @param context  The context of the application
     * @return  Returns the created pending intent
     */
    private static PendingIntent contentIntent(Context context) {

        /*  Create the intent   */
        Intent startActivityIntent = new Intent(context, MainActivity.class);

        /*  Get the pending intent and return it */
        return PendingIntent.getActivity(
                context,
                STUDY_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Creates and returns a large icon for the study notification
     * @param context The context from where it is called
     * @return Returns a bitmap containing the large icon
     */
    private static Bitmap getLargeIcon(Context context) {

        /* Get reference of resources using the context */
        Resources res = context.getResources();

        /*  Return the decoded Bitmap   */
        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
    }


}
