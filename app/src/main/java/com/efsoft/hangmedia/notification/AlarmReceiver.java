package com.efsoft.hangmedia.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.efsoft.hangmedia.activity.HomeActivity;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.activity.JadwalShalatActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String prayName = intent.getStringExtra("NAMASHALAT");
        int code = intent.getExtras().getInt("CODE");

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent forNotif = new Intent(context, JadwalShalatActivity.class);
        PendingIntent forNotifs = TaskStackBuilder.create(context).addNextIntentWithParentStack(forNotif).getPendingIntent(17, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotificationLatest(context, prayName, forNotifs, String.valueOf(code));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(context, String.valueOf(code))
                    .setContentTitle("Shalat " + prayName)
                    .setContentText("Saatnya Shalat " + prayName)
                    .setSmallIcon(R.drawable.hang_fm_logo)
                    .setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(forNotifs)
                    .setChannelId("24")
                    .setShowWhen(true)
                    .setVibrate(new long[]{100,200,300,400,500,400,300,200,400})
                    .build();

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT){
                if (manager != null) {
                    manager.notify(code, notification);
                }
            }
        }
    }

    public void showNotificationLatest(Context context, String shalat, PendingIntent forNotif, String code){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(code, "Waktu Shalat", NotificationManager.IMPORTANCE_HIGH);
            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(false);
            serviceChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            manager.createNotificationChannel(serviceChannel);
        }

        Notification notification = new NotificationCompat.Builder(context, code)
                .setSmallIcon(R.drawable.hang_fm_logo)
                .setContentTitle("Waktunya Shalat")
                .setContentText("Saatnya Shalat " + shalat)
                .setChannelId(code)
                .setContentIntent(forNotif)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        manager.notify(Integer.parseInt(code), notification);
    }
}
