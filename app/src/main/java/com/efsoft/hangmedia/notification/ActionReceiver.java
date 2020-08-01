package com.efsoft.hangmedia.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.efsoft.hangmedia.player.RadioPlayer;
import com.efsoft.hangmedia.background.ForegroundService;
import com.efsoft.hangmedia.helper.Constants;

import static android.os.Build.VERSION_CODES.O;

public class ActionReceiver extends BroadcastReceiver {

    NotificationManagerCompat notificationManager;
    MediaSessionCompat mediaSession;

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("ACTION_KEY");
        notificationManager = NotificationManagerCompat.from(context);
        mediaSession = new MediaSessionCompat(context, "tag");

        switch (message) {
            case "play":
                RadioPlayer.playMediaPlayer();
                Log.d("Action Receiver", "onReceive: Playing");
                break;
            case "pause":
                RadioPlayer.pauseMediaPlayer();
                Log.d("Action Receiver", "onReceive: Pausing");
                break;
            case "stop":
                if (!RadioPlayer.isWorking()){
                    RadioPlayer.stopMediaPlayer();
                }
                if (Build.VERSION.SDK_INT < O){
                    NotificationPanel.notificationCancel();
                }

                Intent stopIntent = new Intent(context, ForegroundService.class);
                stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                if (Build.VERSION.SDK_INT >= O) {
                    context.startForegroundService(stopIntent);
                } else {
                    context.startService(stopIntent);
                }
                notificationManager.cancel(13);
                Log.d("Action Receiver", "onReceive: Stoppping");
                break;
        }
    }
}
