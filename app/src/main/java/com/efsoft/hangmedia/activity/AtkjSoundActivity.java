package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.notification.AtkjNotifReceiver;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AtkjSoundActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private ImageButton btn_play_pause;
    private AppCompatSeekBar seekBar;
    private TextView timer;

    private MediaPlayer mediaPlayer;
    MediaSessionCompat mediaSession;
    private int mediaFileLength, realtimeLength;
    final Handler handler = new Handler();

    private NotificationManager notificationManager;
    String url, jud;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atkj_sound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSession = new MediaSessionCompat(this, "tag");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("INTENT_NAME"));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Typeface oSR = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        Typeface oSSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

        Intent intent = getIntent();
        url = intent.getStringExtra("URL_KEY");
        jud = intent.getStringExtra("JUDUL_KEY");

        TextView judul = findViewById(R.id.txtJudul);
        judul.setTypeface(oSSB);
        judul.setText(jud);

        timer = findViewById(R.id.txtSoundTimer);
        timer.setTypeface(oSR);

        seekBar = findViewById(R.id.seekBarAtkj);
        seekBar.setMax(99);
        seekBar.setOnTouchListener((v, event) -> {
            if (mediaPlayer.isPlaying()) {
                AppCompatSeekBar seekBar = (AppCompatSeekBar) v;
                int playPosition = (mediaFileLength / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
            }
            return false;
        });

//        ImageButton btnDownload = findViewById(R.id.btnDownloadAtkj);
//        btnDownload.setOnClickListener(v -> downloadFile("http://tiviapi.online/hang_atkj_api/upload_atkj/", url));

        btn_play_pause = findViewById(R.id.btnPlayPauseAtkj);
        btn_play_pause.setOnClickListener(v -> {
            final ProgressDialog mDialog = new ProgressDialog(AtkjSoundActivity.this);
            playPlayer(jud, url, mDialog);
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(AtkjSoundActivity.this);

    }

    private void updateSeekBar() {
        seekBar.setProgress((int) ((float) mediaPlayer.getCurrentPosition() / mediaFileLength * 100));
        if (mediaPlayer.isPlaying()) {
            Runnable updater = () -> {
                updateSeekBar();

                int timeElapsed = mediaPlayer.getCurrentPosition();

                //set time remaining in minutes and seconds
                int timeRemaining = realtimeLength - timeElapsed;

                timer.setText(String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            };
            handler.postDelayed(updater, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_play_pause.setImageResource(R.drawable.ic_play);
    }

    public void showNotificationLatest(String judul){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("29", "Rekaman ATKJ", NotificationManager.IMPORTANCE_HIGH);
            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(false);
            serviceChannel.setSound(null, null);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManager.createNotificationChannel(serviceChannel);
        }

        Intent intent = new Intent(this, AtkjSoundActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 3, intent, 0);

        Intent broadIntent1 = new Intent(this, AtkjNotifReceiver.class);
        broadIntent1.putExtra("ACTION_KEY", "play");
        PendingIntent playIntent = PendingIntent.getBroadcast(this,4, broadIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadIntent2 = new Intent(this, AtkjNotifReceiver.class);
        broadIntent2.putExtra("ACTION_KEY", "pause");
        PendingIntent pauseIntent = PendingIntent.getBroadcast(this,5, broadIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadIntent3 = new Intent(this, AtkjNotifReceiver.class);
        broadIntent3.putExtra("ACTION_KEY", "stop");
        PendingIntent stopIntent = PendingIntent.getBroadcast(this,6, broadIntent3, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hang_fm_logo);

        Notification notification = new NotificationCompat.Builder(this, "29")
                .setSmallIcon(R.drawable.exo_notification_small_icon)
                .setContentTitle("Anda Tanya Kami Jawab")
                .setContentText(judul)
                .setLargeIcon(bitmap)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.exo_icon_play, "Play", playIntent)
                .addAction(R.drawable.exo_icon_pause, "Pause", pauseIntent)
                .addAction(R.drawable.exo_icon_stop, "Stop", stopIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        //startForegroundService(29, notification); //notification id
        notificationManager.notify(29, notification);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedAction = intent.getStringExtra("ACTION");

            switch (receivedAction) {
                case "StopMediaPlayer":
                    mediaPlayer.stop();
                    break;
                case "PauseMediaPlayer":
                    mediaPlayer.pause();
                    break;
                case "PlayMediaPlayer":
                    playPlayer(jud, url);
                    break;
            }
        }
    };

    public void downloadFile(String hostUrl, String filename)
    {
        int count;
        try {
            URL url = new URL(hostUrl + filename);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conexion.getContentLength();

            // downlod the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath());

            byte[] data = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                //publishProgress((int)(total*100/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            Toast.makeText(AtkjSoundActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();

        } catch (Exception e) { e.printStackTrace();}
    }

    private void playPlayer(String jud, String url, ProgressDialog mDialog){

        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                mDialog.setMessage("Memutar Rekaman ATKJ");
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    mediaPlayer.setDataSource(strings[0]);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Log.d("AtkjSoundActivity", "doInBackground: " + e.getMessage());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                mediaFileLength = mediaPlayer.getDuration();
                realtimeLength = mediaFileLength;

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    btn_play_pause.setImageResource(R.drawable.ic_pause);
                    showNotificationLatest(jud);
                } else {
                    mediaPlayer.pause();
                    btn_play_pause.setImageResource(R.drawable.ic_play);
                }

                updateSeekBar();
                mDialog.dismiss();
            }
        };

        mp3Play.execute("http://tiviapi.online/hang_atkj_api/upload_atkj/" + url);

    }

    private void playPlayer(String jud, String url){

        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    mediaPlayer.setDataSource(strings[0]);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Log.d("AtkjSoundActivity", "doInBackground: " + e.getMessage());
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                mediaFileLength = mediaPlayer.getDuration();
                realtimeLength = mediaFileLength;

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    btn_play_pause.setImageResource(R.drawable.ic_pause);
                    showNotificationLatest(jud);
                } else {
                    mediaPlayer.pause();
                    btn_play_pause.setImageResource(R.drawable.ic_play);
                }

                updateSeekBar();
            }
        };

        mp3Play.execute("http://tiviapi.online/hang_atkj_api/upload_atkj/" + url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        moveTaskToBack(true);
    }
}