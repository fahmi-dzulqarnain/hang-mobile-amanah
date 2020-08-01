package com.efsoft.hangmedia.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.efsoft.hangmedia.helper.Constants;
import com.efsoft.hangmedia.notification.ActionReceiver;
import com.efsoft.hangmedia.radio.MainScreen;
import com.efsoft.hangmedia.player.RadioPlayer;
import com.efsoft.hangmedia.radio.RadioStreamActivity;
import com.efsoft.hangmedia.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

import java.util.Objects;

public class ForegroundService extends Service {

    public SimpleExoPlayer player;
    public MultiPlayer multiPlayer;

    NotificationManager notificationManager;
    MediaSessionCompat mediaSession;

    public ForegroundService(){ }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSession = new MediaSessionCompat(this, "tag");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Constants.ACTION.STARTFOREGROUND_ACTION:
                    play(this.getString(R.string.radio_url));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        showNotification();
                    } else {
                        showNotificationLatest();
                    }
                    break;
                case Constants.ACTION.PAUSEFOREGROUND_PLAYER:
                    player.setVolume(0f);
                    break;
                case Constants.ACTION.PLAYFOREGROUND_PLAYER:
                    player.setVolume(1f);
                    break;
                case Constants.ACTION.STOPFOREGROUND_PLAYER:
                    stopMediaPlayer();
                    stopSelf();
                    break;
                case Constants.ACTION.STOPFOREGROUND_ACTION:
                    stopMediaPlayer();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationManager.cancel(13);
                        stopForeground(true);
                    } else {
                        closeNotification();
                        stopForeground(true);
                    }
                    stopSelf();
                    break;
            }

        } catch (Exception e){
            Log.d("ForegroundService", "onStartCommand: " + e.getMessage());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void play(final String url){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "streamradio"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
        player.prepare(audioSource);
        player.setPlayWhenReady(true);
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                if(isLoading){
                    RadioStreamActivity.startBufferingAnimation();
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState==3){
                    RadioStreamActivity.stopBufferingAnimation();
                    RadioPlayer.isStarted = true;
                }else{
                    RadioPlayer.isStarted = false;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        try {
                            multiPlayer.stop();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        RadioStreamActivity.newNotification(RadioPlayer.getRadioName(), true);
                        multiPlayer = new MultiPlayer(new PlayerCallback() {

                            @Override
                            public void playerStopped(int arg0) {
                                RadioPlayer.isStarted = false;
                            }

                            @Override
                            public void playerStarted() {
                                RadioPlayer.isStarted = true;
                                try {
                                    RadioStreamActivity.stopBufferingAnimation();
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }

                            @Override
                            public void playerPCMFeedBuffer(boolean arg0, int arg1, int arg2) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void playerMetadata(String arg0, String arg1) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void playerException(Throwable arg0) {
                                // TODO Auto-generated method stub
                                RadioPlayer.setIsWorking(false);
                                try {
                                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                                        RadioStreamActivity.stopBufferingAnimation();
                                        RadioPlayer.setIsWorking(false);

                                    } else {
                                        RadioStreamActivity.stopBufferingAnimation();
                                        RadioPlayer.setIsWorking(false);
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }

                            }

                            @Override
                            public void playerAudioTrackCreated(AudioTrack arg0) {
                                // TODO Auto-generated method stub

                            }
                        }, 750, 700);
                        multiPlayer.playAsync(url);

                        try {
                            java.net.URL.setURLStreamHandlerFactory(protocol -> {
                                if ("icy".equals(protocol))
                                    return new com.spoledge.aacdecoder.IcyURLStreamHandler();
                                return new com.spoledge.aacdecoder.IcyURLStreamHandler();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        RadioPlayer.setIsWorking(false);
                        try {
                            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = cm.getActiveNetworkInfo();
                            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                                RadioStreamActivity.stopBufferingAnimation();
                                RadioPlayer.setIsWorking(false);

                            } else {
                                RadioStreamActivity.stopBufferingAnimation();
                                RadioPlayer.setIsWorking(false);
                            }
                        } catch (Exception e2) {
                            // TODO: handle exception
                        }
                    }
                } else {
                    RadioPlayer.setIsWorking(false);
                    try {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = cm.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                            RadioStreamActivity.stopBufferingAnimation();
                            RadioPlayer.setIsWorking(false);
                        } else {
                            RadioStreamActivity.stopBufferingAnimation();
                            RadioPlayer.setIsWorking(false);
                        }
                    } catch (Exception e2) {
                        // TODO: handle exception
                    }
                }
            }

        });
    }

    public void showNotification() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Intent intent = new Intent(this, RadioStreamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, "1")
                    .setContentTitle(this.getString(R.string.radio_name))
                    .setContentText(MainScreen.getRadioListName().getText().toString())
                    .setSmallIcon(R.drawable.ic_stat_transmission4)
                    .setContentIntent(pendingIntent)
                    .setWhen(0)
                    .setOngoing(true);

            nBuilder.setContentIntent(pendingIntent);
            Notification noti = nBuilder.build();
            noti.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, noti); //notification id*/
        } else {
            Intent intent = new Intent(this, RadioStreamActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 3, intent, 0);

            Intent broadIntent1 = new Intent(this, ActionReceiver.class);
            broadIntent1.putExtra("ACTION_KEY", "play");
            PendingIntent playIntent = PendingIntent.getBroadcast(this,4, broadIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadIntent2 = new Intent(this, ActionReceiver.class);
            broadIntent2.putExtra("ACTION_KEY", "pause");
            PendingIntent pauseIntent = PendingIntent.getBroadcast(this,5, broadIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent broadIntent3 = new Intent(this, ActionReceiver.class);
            broadIntent3.putExtra("ACTION_KEY", "stop");
            PendingIntent stopIntent = PendingIntent.getBroadcast(this,6, broadIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hang_fm_logo);

            Notification notification = new NotificationCompat.Builder(this, "13")
                    .setSmallIcon(R.drawable.exo_notification_small_icon)
                    .setContentTitle("Streaming Radio")
                    .setContentText("Hang 106 FM")
                    .setLargeIcon(bitmap)
                    .setContentIntent(contentIntent)
                    .addAction(R.drawable.exo_icon_play, "Play", playIntent)
                    .addAction(R.drawable.exo_icon_pause, "Pause", pauseIntent)
                    .addAction(R.drawable.exo_icon_stop, "Stop", stopIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSession.getSessionToken()))
                    .setSubText("LIVE")
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            startForeground(13, notification); //notification id
            notificationManager.notify(13, notification);
        }
    }

    public void showNotificationLatest(){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("13", "Streaming Radio", NotificationManager.IMPORTANCE_HIGH);
            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(false);
            serviceChannel.setSound(null, null);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager.createNotificationChannel(serviceChannel);
        }

        Intent intent = new Intent(this, RadioStreamActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 3, intent, 0);

        Intent broadIntent1 = new Intent(this, ActionReceiver.class);
        broadIntent1.putExtra("ACTION_KEY", "play");
        PendingIntent playIntent = PendingIntent.getBroadcast(this,4, broadIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadIntent2 = new Intent(this, ActionReceiver.class);
        broadIntent2.putExtra("ACTION_KEY", "pause");
        PendingIntent pauseIntent = PendingIntent.getBroadcast(this,5, broadIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadIntent3 = new Intent(this, ActionReceiver.class);
        broadIntent3.putExtra("ACTION_KEY", "stop");
        PendingIntent stopIntent = PendingIntent.getBroadcast(this,6, broadIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hang_fm_logo);

        Notification notification = new NotificationCompat.Builder(this, "13")
                .setSmallIcon(R.drawable.exo_notification_small_icon)
                .setContentTitle("Streaming Radio")
                .setContentText("Hang 106 FM")
                .setLargeIcon(bitmap)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.exo_icon_play, "Play", playIntent)
                .addAction(R.drawable.exo_icon_pause, "Pause", pauseIntent)
                .addAction(R.drawable.exo_icon_stop, "Stop", stopIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setSubText("LIVE")
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        startForeground(13, notification); //notification id
        notificationManager.notify(13, notification);
    }

    public void closeNotification(){
        NotificationManager nManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (nManager != null) {
            nManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        }
    }

    public void stopMediaPlayer() {
        RadioPlayer.isStarted = false;
        try {
            player.stop();
        }catch (Exception e){
            e.getMessage();
        }
        try {
            multiPlayer.stop();
        }catch (Exception e){
            e.getMessage();
        }
    }
}