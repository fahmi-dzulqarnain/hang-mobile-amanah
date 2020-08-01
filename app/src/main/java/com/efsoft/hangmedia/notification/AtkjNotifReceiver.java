package com.efsoft.hangmedia.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.efsoft.hangmedia.activity.AtkjSoundActivity;


public class AtkjNotifReceiver extends BroadcastReceiver {

    NotificationManagerCompat notificationManager;
    MediaSessionCompat mediaSession;

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("ACTION_KEY");
        notificationManager = NotificationManagerCompat.from(context);
        mediaSession = new MediaSessionCompat(context, "tag");
        AtkjSoundActivity player = new AtkjSoundActivity();

        switch (message) {
            case "play":
                playMediaPlayer(context);
                Log.d("Action Receiver", "onReceive: Playing");
                break;
            case "pause":
                pauseMediaPlayer(context);
                Log.d("Action Receiver", "onReceive: Pausing");
                break;
            case "stop":
                stopMediaPlayer(context);
                notificationManager.cancel(29);
                Log.d("Action Receiver", "onReceive: Stoppping");
                break;
        }
    }

    public static void stopMediaPlayer(Context context) {
        Intent intent = new Intent("INTENT_NAME").putExtra("ACTION", "StopMediaPlayer");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void pauseMediaPlayer(Context context) {
        Intent intent = new Intent("INTENT_NAME").putExtra("ACTION", "PauseMediaPlayer");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void playMediaPlayer(Context context) {
        Intent intent = new Intent("INTENT_NAME").putExtra("ACTION", "PlayMediaPlayer");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
