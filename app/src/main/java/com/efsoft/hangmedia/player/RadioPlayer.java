package com.efsoft.hangmedia.player;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.efsoft.hangmedia.IcyStreamMeta;
import com.efsoft.hangmedia.background.ForegroundService;
import com.efsoft.hangmedia.background.TelephonyManagerReceiver;
import com.efsoft.hangmedia.helper.Constants;
import com.efsoft.hangmedia.radio.RadioListElement;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class RadioPlayer {
    public static boolean isStarted = false;
    private static String trackTitle = "";
    private static String radioName = "";
    private static Context context;
    private static boolean isWorking = true;
    private RadioListElement radioListElement;
    private Timer timer = new Timer();
    private boolean timerIndicator = false;

    public static boolean isWorking() {
        return isWorking;
    }

    public static void setIsWorking(boolean isWorking) {
        RadioPlayer.isWorking = isWorking;
    }

    public static String getRadioName() {
        return radioName;
    }

    public static String getTrackTitle() {
        return trackTitle;
    }

    public static boolean isStarted() {
        return isStarted;
    }

    public static void stopMediaPlayer() {
        isStarted = false;
        Intent stopIntent = new Intent(context, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_PLAYER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(stopIntent);
        } else {
            context.startService(stopIntent);
        }
    }

    public static void pauseMediaPlayer() {
        Intent pauseIntent = new Intent(context, ForegroundService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSEFOREGROUND_PLAYER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(pauseIntent);
        } else {
            context.startService(pauseIntent);
        }
    }

    public static void playMediaPlayer() {
        Intent playIntent = new Intent(context, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.PLAYFOREGROUND_PLAYER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(playIntent);
        } else {
            context.startService(playIntent);
        }
    }

    public void play(RadioListElement rle) {
        startThread();
        TelephonyManagerReceiver.message = false;
        isWorking = true;
        isStarted = true;
        radioListElement = rle;
        context = radioListElement.getContext();
        Intent startIntent = new Intent(context, ForegroundService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        context.startService(startIntent);
        radioListElement.getName();
        radioName = radioListElement.getName();
    }

    private void startThread() {
        if (!timerIndicator) {
            timerIndicator = true;
            timer.schedule(new TimerTask() {
                public void run() {
                    if (isStarted) {
                        URL url;
                        try {
                            url = new URL(radioListElement.getUrl());
                            IcyStreamMeta icy = new IcyStreamMeta(url);
                            if (icy.getArtist().length() > 0 && icy.getTitle().length() > 0) {
                                String title = icy.getArtist() + " - " + icy.getTitle();
                                trackTitle = new String(title.getBytes("ISO-8859-1"), "UTF-8");
                            } else {
                                String title = icy.getArtist() + "" + icy.getTitle();
                                trackTitle = new String(title.getBytes("ISO-8859-1"), "UTF-8");
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }, 0, 1000);
        }
    }
}
