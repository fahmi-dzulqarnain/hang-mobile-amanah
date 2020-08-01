package com.efsoft.hangmedia.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.efsoft.hangmedia.radio.MainScreen;
import com.efsoft.hangmedia.R;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "channelid";
    private static final String CHANNEL_NAME = "Notification Channel";
    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel serviceChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            serviceChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(false);
            serviceChannel.setSound(null, null);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(serviceChannel);
        }
    }

    public NotificationManager getManager() {
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.hang_fm_logo);

    @SuppressLint("NewApi")
    public Notification.Builder getChannelnotification(String radioName, PendingIntent pendingIntent){

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(radioName)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentText(MainScreen.getRadioListName().getText().toString())
                .setSmallIcon(R.drawable.ic_stat_transmission4)
                .setLargeIcon(image)
                .setContentIntent(pendingIntent)
                .setWhen(0)
                .setChannelId(CHANNEL_ID);
    }
}