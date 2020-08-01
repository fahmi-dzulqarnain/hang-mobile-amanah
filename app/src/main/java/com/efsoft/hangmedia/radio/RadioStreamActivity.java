package com.efsoft.hangmedia.radio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efsoft.hangmedia.activity.HomeActivity;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.helper.Constants;
import com.efsoft.hangmedia.DataManager;
import com.efsoft.hangmedia.background.ForegroundService;
import com.efsoft.hangmedia.background.HeadsetReceiver;
import com.efsoft.hangmedia.LoadingAnimation;
import com.efsoft.hangmedia.player.RadioPlayer;
import com.efsoft.hangmedia.notification.NotificationPanel;
import com.efsoft.hangmedia.TabPagerAdapter;
import com.efsoft.hangmedia.background.TelephonyManagerReceiver;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import java.util.Objects;

import com.efsoft.hangmedia.R;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.O;
import static com.efsoft.hangmedia.radio.MainScreen.getBtnPlayRadio;
import static com.efsoft.hangmedia.radio.MainScreen.mainView;

public class RadioStreamActivity extends FragmentActivity {

    private HeadsetReceiver headsetReceiver;
    private static DataManager dataManager;
    private static ViewPager viewPager;
    private static ImageView bufferingIndicator, speaker;
    private static LoadingAnimation bufferingAnimation;
    private static AudioManager audioManager;
    private static TextView radioListLocation;
    private static TextView radioListName;
    private static boolean first = true;
    private static NotificationPanel nPanel;
    private boolean runOnce = true;
    private LinearLayout volumeLayout, volumeButton;
    private int volumeStore;
    protected static boolean notificationWhile = true;
    private boolean active = true;
    private String last = "";

    public static String title = "";
    private static boolean exit = false;
    public static int page = 1;
    public static int pos = 0;
    private String lang;

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        notificationWhile = true;
        if(exit){
            RadioPlayer.setIsWorking(false);
            notificationWhile = true;
            trackDetection();
            last = "";
        }
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (!RadioPlayer.isWorking()){
            if (Build.VERSION.SDK_INT < O)
            NotificationPanel.notificationCancel();
        }
        exit = true;
        super.onDestroy();
    }

    public static void newNotification(String notText, boolean status){
        nPanel.showNotification(notText, status);
    }

    public static void radioListRefresh() {
        dataManager.createRadioListForRadioScreen(radioListName, radioListLocation);
    }

    public static void startBufferingAnimation() {
        bufferingIndicator = MainScreen.getLoadingImage();
        bufferingAnimation = new LoadingAnimation(bufferingIndicator);
        bufferingAnimation.startAnimation();
    }

    public static void stopBufferingAnimation() {
        bufferingIndicator = MainScreen.getLoadingImage();
        bufferingAnimation.clearAnimation();
    }

    public static void play(String location) {
        try {
            exit = false;
            if (first) {
                radioListLocation.setText(location);
                title = location;
                RadioList.nextOrPreviousRadioStation(1, radioListLocation, radioListName);
                if (!RadioPlayer.isStarted()) {
                    RadioList.nextOrPreviousRadioStation(0, radioListLocation, radioListName);
                }
                first = false;
                getBtnPlayRadio().setImageResource(R.drawable.ic_pause);
            } else {
                if (RadioPlayer.isStarted()) {
                    getBtnPlayRadio().setImageResource(R.drawable.ic_play);
                    RadioPlayer.stopMediaPlayer();
                } else if (LoadingAnimation.hasEnded()) {
                    try {
                        RadioList.nextOrPreviousRadioStation(0, radioListLocation, radioListName);
                        getBtnPlayRadio().setImageResource(R.drawable.ic_pause);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_radio_stream);
        permissions();
        NewDatabase alarmDatabase = new NewDatabase(this);
        //getBtnPlayRadio().setImageResource(R.drawable.ic_play);

        try {
            Cursor language = alarmDatabase.getPrayTime("language");
            language.moveToFirst();

            if (language.getCount() != 0) {
                String bahasa = language.getString(0);
                if (bahasa.equals("id")) {
                    lang = "id";
                } else if (bahasa.equals("en")) {
                    lang = "en";
                }
            }

        } catch (Exception e) {
            Log.d("RadioStreamActivity", "onCreate: " + e.getMessage());
        }

        headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        registerReceiver();

        dataManager = new DataManager(this);
        Typeface oSSBItc = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        TextView radioTitle = findViewById(R.id.radioTitle);
        radioTitle.setTypeface(oSSBItc);

        ImageView plus = findViewById(R.id.plus);
        plus.setOnClickListener(view -> {
            notificationWhile = false;
            if (!RadioPlayer.isWorking()) {
                RadioPlayer.stopMediaPlayer();
            }
            if (Build.VERSION.SDK_INT < O) {
                NotificationPanel.notificationCancel();
            }
            exit = true;

            Intent stopIntent = new Intent(getApplicationContext(), ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(stopIntent);
            } else {
                getApplicationContext().startService(stopIntent);
            }

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Intent intent2 = new Intent(RadioStreamActivity.this, HomeActivity.class);
            startActivity(intent2);
            finish();
        });

        TabPagerAdapter tabPageAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(tabPageAdapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() == 0) {
                startActivity(new Intent(RadioStreamActivity.this, HomeActivity.class));
                finish();
            }
        });

        speaker = findViewById(R.id.speaker);
        speaker.setOnTouchListener((v, event) -> {
            if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeStore, volumeStore);
                defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
            } else {
                volumeStore = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
            }
            return false;
        });

        volumeLayout = findViewById(R.id.linearLayout_t);
        volumeButton = findViewById(R.id.button_t);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // ATTENTION: This was auto-generated to handle app links.
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        else if (keyCode == KeyEvent.KEYCODE_HOME){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (runOnce) {
            mainView = findViewById(R.id.mainView);
            radioListName = findViewById(R.id.mainRadioName);
            radioListLocation = findViewById(R.id.mainRadioLocation);

            if(title.length() == 0){
                if (lang.equals("id")){
                    MainScreen.setRadioListName(getResources().getString(R.string.welcome_small));
                } else if (lang.equals("en")){
                    MainScreen.setRadioListName(getResources().getString(R.string.welcome_small_en));
                }
            } else {
                MainScreen.setRadioListName(title);
            }

            radioListRefresh();
            volumeBarReaction(volumeLayout, volumeButton, audioManager);
            connectionDialog(isOnline());
            trackDetection();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                nPanel = new NotificationPanel(this);
                nPanel.showNotification(this.getResources().getString(R.string.radio_name), true);
            }

            runOnce = false;

            if(!RadioPlayer.isStarted()){
                MainScreen.setBtnPlayRadioImage(R.drawable.ic_play);
                if(Boolean.parseBoolean(this.getResources().getString(R.string.autostart_true_or_false))){
                    if (lang.equals("id")){
                        MainScreen.setBtnPlayRadioImage(R.drawable.ic_pause);
                        play(this.getResources().getString(R.string.radio_location));
                    } else if (lang.equals("en")){
                        play(this.getResources().getString(R.string.radio_location_en));
                    }
                }
            }
        }
        defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
    }

    public void defaultVolumeBarPosition(AudioManager audioManager, LinearLayout volumeLayout, LinearLayout volumeButton) {
        float actual = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float endPoint = volumeLayout.getWidth() - volumeButton.getWidth();
        volumeButton.setX((endPoint / max * actual));
        if (volumeButton.getX() == 0)
            speaker.setImageResource(R.drawable.volume_muted);
        else speaker.setImageResource(R.drawable.volume_on);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void volumeBarReaction(final LinearLayout volumeLayout, final LinearLayout volumeButton, final AudioManager audioManager) {

        volumeLayout.setOnTouchListener((view, motionEvent) -> {
            float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float endPoint = volumeLayout.getWidth() - volumeButton.getWidth();
            volumeButton.setX(motionEvent.getX() - volumeButton.getWidth() / 2);

            if (volumeButton.getX() >= 0) {
                float pos = volumeButton.getX() / (endPoint / max);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) pos, 0);
            }
            if (volumeButton.getX() >= endPoint) {
                volumeButton.setX(endPoint);
            }
            if (volumeButton.getX() <= 0) {
                volumeButton.setX(0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                speaker.setImageResource(R.drawable.volume_muted);
            } else speaker.setImageResource(R.drawable.volume_on);
            return true;
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null){
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public void connectionDialog(boolean isOnline) {
        if (!isOnline) {
            final Dialog dialog = new Dialog(RadioStreamActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.without_internet);

            Typeface oSSBItc = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

            TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
            TextView dialogText = dialog.findViewById(R.id.dialogQuestion);
            TextView dialogButton = dialog.findViewById(R.id.retry);

            dialogTitle.setTypeface(oSSBItc);
            dialogText.setTypeface(oSSBItc);
            dialogButton.setTypeface(oSSBItc);

            LinearLayout mainLayout = findViewById(R.id.mainLayout);
            Objects.requireNonNull(dialog.getWindow()).setLayout((int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.35));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button retryBtn = dialog.getWindow().findViewById(R.id.retry);
            retryBtn.setOnClickListener(view -> {
                if (!isOnline()) {
                    dialog.dismiss();
                    dialog.show();
                } else {
                    dialog.dismiss();
                }
            });
            dialog.setOnCancelListener(dialogInterface -> {
                if (!isOnline()) {
                    dialog.dismiss();
                    dialog.show();
                }
            });
            dialog.show();
        }
    }

    public void trackDetection() {
           new Thread() {
            @Override
            public void run() {
                while (notificationWhile) {
                    runOnUiThread(() -> {
                        if(!TelephonyManagerReceiver.message){
                            if(RadioPlayer.isWorking()){
                                if (!RadioPlayer.getTrackTitle().trim().equals("")) {
                                    title = RadioPlayer.getTrackTitle().trim();
                                    if(title.contains("~+") || title.contains("ad|")){
                                        title = "COMMERCIAL BREAK";
                                    }
                                    if (title.length() > 0 && title.charAt(title.length()-1)=='-') {
                                        title = title.substring(0, title.length()-1).trim();
                                    }

                                    String[] parts = title.split("\\(");
                                    if(parts.length == 2){
                                        MainScreen.setRadioListName(parts[0] + "\n(" + parts[1]);
                                        title = parts[0] +"\n("+parts[1];
                                    }
                                    else
                                        MainScreen.setRadioListName(title);
                                    if(!last.equals(title)) {
                                        if (Build.VERSION.SDK_INT < LOLLIPOP){
                                            RadioStreamActivity.newNotification(getResources().getString(R.string.radio_name), active);
                                        }
                                        last = title;
                                    }
                                } else if (RadioPlayer.getTrackTitle().trim().equals("") && RadioPlayer.isStarted() && !radioListLocation.getText().toString().equals(getResources().getString(R.string.radio_location)) ||
                                           RadioPlayer.getTrackTitle().trim().equals("") && RadioPlayer.isStarted() && !radioListLocation.getText().toString().equals(getResources().getString(R.string.radio_location_en))) {

                                    if (lang.equals("id")){
                                        title = getResources().getString(R.string.radio_location);
                                    } else if (lang.equals("en")){
                                        title = getResources().getString(R.string.radio_location_en);
                                    }

                                    MainScreen.setRadioListName(title);
                                    if (Build.VERSION.SDK_INT < LOLLIPOP){
                                        RadioStreamActivity.newNotification(getResources().getString(R.string.radio_name), active);
                                    }                                    }
                            } else {
                                if (!exit) {
                                    if (lang.equals("id")){
                                        title = getResources().getString(R.string.radio_offline);
                                    } else if (lang.equals("en")){
                                        title = getResources().getString(R.string.radio_offline_en);
                                    }

                                    MainScreen.setRadioListName(title);
                                    if (Build.VERSION.SDK_INT < LOLLIPOP){
                                        RadioStreamActivity.newNotification(getResources().getString(R.string.radio_name), active);
                                    }                                    } else {
                                    MainScreen.setRadioListName(title);
                                    if (Build.VERSION.SDK_INT < LOLLIPOP){
                                        RadioStreamActivity.newNotification(getResources().getString(R.string.radio_name), active);
                                    }                                    }
                            }
                        } else {
                            if (lang.equals("id")){
                                MainScreen.setRadioListName(getResources().getString(R.string.resume));
                            } else if (lang.equals("en")){
                                MainScreen.setRadioListName(getResources().getString(R.string.resume_en));
                            }

                            if (Build.VERSION.SDK_INT < LOLLIPOP){
                                RadioStreamActivity.newNotification(getResources().getString(R.string.radio_name), active);
                            }                            }
                    });

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(exit){
                    System.exit(1);
                }
            }
        }.start();
    }

    public void permissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
    }

    public void registerReceiver(){
        headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

}