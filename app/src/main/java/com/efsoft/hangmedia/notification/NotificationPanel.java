package com.efsoft.hangmedia.notification;

import com.efsoft.hangmedia.radio.MainScreen;
import com.efsoft.hangmedia.radio.RadioStreamActivity;
import com.efsoft.hangmedia.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationPanel {

    private static NotificationManager nManager;
    private static Context parent;
    private static final String CHANNEL_ID = "channelid";
    private int SDK_VERSION = Build.VERSION.SDK_INT;

    public NotificationPanel(Context parent) {
        NotificationPanel.parent = parent;
    }

    public void showNotification(String radioName, boolean status) {

        Intent intent = new Intent(parent, RadioStreamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(parent, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap image = BitmapFactory.decodeResource(parent.getResources(), R.drawable.hang_fm_logo);

        if (SDK_VERSION >= Build.VERSION_CODES.O && status){

            NotificationHelper helper = new NotificationHelper(parent);

            Notification.Builder builder = helper.getChannelnotification(radioName, pendingIntent);
            Notification noti = builder.setOngoing(true).build();
            noti.flags |= Notification.FLAG_NO_CLEAR;
            helper.getManager().notify(1, noti);

        } else if (SDK_VERSION < Build.VERSION_CODES.LOLLIPOP && status){

            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(parent, CHANNEL_ID)
                    .setContentTitle(radioName)
                    .setContentText(MainScreen.getRadioListName().getText().toString())
                    .setSmallIcon(R.drawable.ic_stat_transmission4)
                    .setLargeIcon(image)
                    .setContentIntent(pendingIntent)
                    .setWhen(0);

            nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification noti = nBuilder.build();
            noti.flags |= Notification.FLAG_NO_CLEAR;

            nManager.notify(1, noti);
        }
    }

    public static void notificationCancel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            NotificationHelper helper = new NotificationHelper(parent);
            helper.getManager().cancel(1);

        } else {

            nManager.cancel(1);

        }
    }
}