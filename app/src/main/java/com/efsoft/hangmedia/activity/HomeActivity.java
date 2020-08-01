package com.efsoft.hangmedia.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.efsoft.hangmedia.database.AlarmDatabase;
import com.efsoft.hangmedia.database.AtkjSoundDatabase;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.MyApplication;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.SplashTvActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.efsoft.hangmedia.radio.RadioStreamActivity;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.retrofit.AtkjResponse;
import com.efsoft.hangmedia.retrofit.Retrofitclient;
import com.efsoft.hangmedia.retrofit.TableAtkj;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class HomeActivity extends AppCompatActivity {

    MyApplication App;
    AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 13;
    NewDatabase alarmDatabase;
    private Boolean isFirstTime;
    public static String lang = "";
    AtkjSoundDatabase atkjDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        App = MyApplication.getInstance();
        alarmDatabase = new NewDatabase(HomeActivity.this);
        atkjDatabase = new AtkjSoundDatabase(HomeActivity.this);

        Typeface oSSBItc = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");

        TextView txtHang, txtHangTVc, txtHangFMc, txtKajian, txtKajianC, txtATKJ, txtATKJc, txtJadwal, txtJadwalC,
                txtDonate, txtDonateC, txtTimetable, txtTimetableC, txtSocialMedia, txtBantuan, txtWaku, txtProfile, txtProfilec;

        txtHang = findViewById(R.id.txtAhlanWaSahlan);
        txtHangTVc = findViewById(R.id.txtHangTvContent);
        txtHangFMc = findViewById(R.id.txtHangFMc);
//        txtKajian = findViewById(R.id.txtKajian);
//        txtKajianC = findViewById(R.id.txtKajianc);
        txtATKJ = findViewById(R.id.txtATKJWA);
        txtATKJc = findViewById(R.id.txtATKJWAc);
        txtJadwal = findViewById(R.id.txtJadwal);
        txtJadwalC = findViewById(R.id.txtJadwalc);
        txtDonate = findViewById(R.id.txtDonates);
        txtDonateC = findViewById(R.id.txtDonatesC);
        txtTimetable = findViewById(R.id.txtTimetable);
        txtTimetableC = findViewById(R.id.txtTimetableC);
        txtSocialMedia = findViewById(R.id.txtSocialMedia);
//        txtBantuan = findViewById(R.id.txtBantuan);
//        txtWaku = findViewById(R.id.txtWaKu);
        txtProfile = findViewById(R.id.txtProfile);
        txtProfilec = findViewById(R.id.txtProfilec);

        txtHang.setTypeface(oSSBItc);

        lang = "id";
        Cursor language, donate;

        try {
            if (isFirstTime()){
                donateDialog();
                alarmDatabase.insertJadwal("language", "id");
                alarmDatabase.insertJadwal("donate", "1");
            }

            language = alarmDatabase.getPrayTime("language");
            language.moveToFirst();

            donate = alarmDatabase.getPrayTime("donate");
            donate.moveToFirst();

            if (language.getCount() == 0){
                alarmDatabase.insertJadwal("language", "id");
            }

            if (donate.getCount() == 0){
                alarmDatabase.insertJadwal("donate", "1");
            }

            if(language.getCount() != 0){
                String bahasa = language.getString(0);
                if (bahasa.equals("id")){
                    lang = "id";
                    txtHangTVc.setText(R.string.hang_tv_description);
                    txtHangFMc.setText(R.string.hang_fm_description);
//                    txtKajian.setText(R.string.hang_artikel);
//                    txtKajianC.setText(R.string.hang_artikel_description);
                    txtATKJ.setText(R.string.atkj_file);
                    txtATKJc.setText(R.string.atkj_file_description);
                    txtJadwal.setText(R.string.jadwal_shalat);
                    txtJadwalC.setText(R.string.jadwal_shalat_description);
                    txtDonate.setText(R.string.donasi);
                    txtDonateC.setText(R.string.donasi_description);
                    txtTimetable.setText(R.string.timetable);
                    txtTimetableC.setText(R.string.timetable_desctiption);
                    txtProfile.setText(R.string.profile);
                    txtProfilec.setText(R.string.profile_description);
                    txtSocialMedia.setText(R.string.ikuti_kami_di);
//                    txtBantuan.setText(R.string.memiliki_masalah_saran_untuk_aplikasi);
//                    txtWaku.setText(R.string.call_us);
                } else if (bahasa.equals("en")){
                    lang = "en";
                    txtHangTVc.setText(R.string.hang_tv_description_en);
                    txtHangFMc.setText(R.string.hang_fm_description_en);
//                    txtKajian.setText(R.string.hang_artikel_en);
//                    txtKajianC.setText(R.string.hang_artikel_description_en);
                    txtATKJ.setText(R.string.atkj_file_en);
                    txtATKJc.setText(R.string.atkj_file_description);
                    txtJadwal.setText(R.string.jadwal_shalat_en);
                    txtJadwalC.setText(R.string.jadwal_shalat_description_en);
                    txtDonate.setText(R.string.donasi_en);
                    txtDonateC.setText(R.string.donasi_description_en);
                    txtTimetable.setText(R.string.timetable_en);
                    txtTimetableC.setText(R.string.timetable_desctiption_en);
                    txtProfile.setText(R.string.profile_en);
                    txtProfilec.setText(R.string.profile_description_en);
                    txtSocialMedia.setText(R.string.ikuti_kami_di_en);
//                    txtBantuan.setText(R.string.memiliki_masalah_saran_untuk_aplikasi_en);
//                    txtWaku.setText(R.string.call_us_en);
                }

                RelativeLayout btnBahasa = findViewById(R.id.btnBahasa);

                btnBahasa.setOnClickListener(v ->{
                    if (bahasa.equals("id")){
                        alarmDatabase.updateJadwal("language", "en");
                        recreate();
                    } else if (bahasa.equals("en")){
                        alarmDatabase.updateJadwal("language", "id");
                        recreate();
                    }
                });
            }

            if (donate.getCount() != 0){
                int count = Integer.parseInt(donate.getString(0));
                count += 1;
                alarmDatabase.updateJadwal("donate", String.valueOf(count));

                if (count == 8){
                    donateDialog();
                    alarmDatabase.updateJadwal("donate", "1");
                }
            }

        } catch (Exception e){
            Toast.makeText(this, "Ada yang salah dengan pengaturan Bahasa, Aplikasi akan dimulai ulang", Toast.LENGTH_SHORT).show();
            recreate();
        }

        getAtkjData();

        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        RelativeLayout btnRadio, btnTelevision, btnKajian, btnATKJ, btnJadwalShalat, btnDonate,
                btnTimetable, /* btnSetelan, */ btnFacebook, btnInstagram, btnWAku, btnProfile;

        btnRadio = findViewById(R.id.btnRadio_);
        btnTelevision = findViewById(R.id.btnTelevision);
//        btnKajian = findViewById(R.id.btnKajian);
        btnATKJ = findViewById(R.id.btnATKJ);
        btnJadwalShalat = findViewById(R.id.btnJadwalShalat);
        btnDonate = findViewById(R.id.btnDonasi);
        btnTimetable = findViewById(R.id.btnProgramHang);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
//        btnWAku = findViewById(R.id.btnWaKu);
        btnProfile = findViewById(R.id.btnProfile);
//        CardView actionBar = findViewById(R.id.toolbarId);

//        ScrollView mainRelative = findViewById(R.id.mainRelative);

//        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)actionBar.getLayoutParams();
//        relativeParams.setMargins(0, getStatusBarHeight(), 0, 0);
//        actionBar.setLayoutParams(relativeParams);

//        RelativeLayout.LayoutParams relativeParamsMain = (RelativeLayout.LayoutParams)mainRelative.getLayoutParams();
//        relativeParamsMain.setMargins(0, getStatusBarHeight() + 51, 0, 0);
//        mainRelative.setLayoutParams(relativeParamsMain);

        btnRadio.setOnClickListener(v -> {
            if (isInternetOn()){
                startActivity(new Intent(HomeActivity.this, RadioStreamActivity.class));
                overridePendingTransition(0,0);
                finish();
            } else {
                noInternetDialog();
            }
        });

        btnTelevision.setOnClickListener(v -> {
            if (isInternetOn()){
                startActivity(new Intent(HomeActivity.this, SplashTvActivity.class));
                overridePendingTransition(0,0);
                finish();
            } else {
                noInternetDialog();
            }
        });

//        btnKajian.setOnClickListener(v -> {
//            if (isInternetOn()){
//                Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
//                intent.putExtra("URL_KEY", "https://today.hangmobile.site");
//                intent.putExtra("TITLE_KEY", "Kajian Harian");
//                startActivity(intent);
//                finish();
//            } else {
//                noInternetDialog();
//            }
//        });

//        btnATKJ.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
//                Uri.parse(
//                        "https://api.whatsapp.com/send?phone=+6281901010106&text="
//                ))));

        btnATKJ.setOnClickListener(v -> {
            if (isInternetOn()){
                startActivity(new Intent(this, AtkjListActivity.class));
            } else {
                noInternetDialog();
            }
        });

        btnProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CompanyProfileActivity.class)));

        btnJadwalShalat.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, JadwalShalatActivity.class)));

        btnDonate.setOnClickListener(v ->{
            if (isInternetOn()){
                Intent intent = new Intent(HomeActivity.this, DetailDonasiActivity.class);
                //intent.putExtra("URL_KEY", "https://donate.hangmobile.site");
                //intent.putExtra("TITLE_KEY", "Donasi");
                startActivity(intent);
            } else {
                noInternetDialog();
            }
        });

        btnTimetable.setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, ProgramHomeActivity.class))
        );

        btnFacebook.setOnClickListener(v -> {
            if (isInternetOn()){
                Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                intent.putExtra("URL_KEY", "https://m.facebook.com/RadioHang106FM/");
                intent.putExtra("TITLE_KEY", "Facebook Hang 106 FM");
                startActivity(intent);
                finish();
            } else {
                noInternetDialog();
            }
        });

        btnInstagram.setOnClickListener(v -> {
            if (isInternetOn()){
                Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                intent.putExtra("URL_KEY", "https://www.instagram.com/hangmedia.co.id/");
                intent.putExtra("TITLE_KEY", "Instagram hangmedia");
                startActivity(intent);
                finish();
            } else {
                noInternetDialog();
            }
        });

//        btnWAku.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
//                Uri.parse(
//                        "https://api.whatsapp.com/send?phone=+6281275003934&text="
//                ))));
    }

    private void noInternetDialog(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_internet);

        Button buttonOK = dialog.findViewById(R.id.btnOKE);
        buttonOK.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public boolean isInternetOn(){
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = Objects.requireNonNull(conMgr).getActiveNetworkInfo();
        return netinfo != null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("UpdateLog", "onActivityResult: Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private void ExitApp() {
        new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit_msg))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//                    Intent intent = new Intent("com.android.ServiceStopped");
//                    sendBroadcast(intent);
                    finish();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .show();
    }

    private boolean isFirstTime(){
        if (isFirstTime == null){
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", MODE_PRIVATE);
            isFirstTime = mPreferences.getBoolean("isFirstTime", true);

            if (isFirstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("isFirstTime", false);
                editor.apply();
            }
        }
        return isFirstTime;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void donateDialog(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_donate);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtContinue, txtTitleDialog, txtDescDialog;
        Button btnYes, btnNo;
        btnYes = dialog.findViewById(R.id.btnYesDonate);
        btnNo = dialog.findViewById(R.id.btnNoDonate);
        txtContinue = dialog.findViewById(R.id.txtLanjutkan);
        txtTitleDialog = dialog.findViewById(R.id.txtDonateTitle);
        txtDescDialog = dialog.findViewById(R.id.txtDonateDesc);

        View.OnClickListener onClickListener = v -> startActivity(new Intent(HomeActivity.this, DetailDonasiActivity.class));

        try {
            Cursor language = alarmDatabase.getPrayTime("language");
            language.moveToFirst();

            if(language.getCount() != 0){
                String bahasa = language.getString(0);
                if (bahasa.equals("id")){
                    txtContinue.setText(R.string.continue_reading);
                    txtDescDialog.setText(R.string.donate_dialog);
                    txtTitleDialog.setText(R.string.donate_dialog_title);
                    btnYes.setText(R.string.donate_yes);
                    btnNo.setText(R.string.donate_no);
                } else if (bahasa.equals("en")){
                    txtContinue.setText(R.string.continue_reading_en);
                    txtDescDialog.setText(R.string.donate_dialog_en);
                    txtTitleDialog.setText(R.string.donate_dialog_title_en);
                    btnYes.setText(R.string.donate_yes_en);
                    btnNo.setText(R.string.donate_no_en);
                }
            }

        } catch (Exception e){
            Log.d("DetailDonasiActivity", "onCreate: " + e.getMessage());
        }

        txtContinue.setOnClickListener(onClickListener);
        btnYes.setOnClickListener(onClickListener);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getAtkjData(){

        Call<AtkjResponse> call = Retrofitclient.getmInstance().getApi().getAtkj();
        call.enqueue(new Callback<AtkjResponse>() {

            @Override
            public void onResponse(@NonNull Call<AtkjResponse> call, @NonNull Response<AtkjResponse> response) {
                AtkjResponse atkjResponse = response.body();

                if (atkjResponse != null){
                    if (atkjResponse.getDataAtkj().size() != 0){
                        List<TableAtkj> data = atkjResponse.getDataAtkj();

                        atkjDatabase.deleteAtkj();

                        for (int k = 0; k < atkjResponse.getDataAtkj().size(); k++) {
                            atkjDatabase.insertAtkj(data.get(k).getJudul(), data.get(k).getTanggal()
                                    , data.get(k).getSound_url());
                        }

                        Log.d("AtkjDownload", "onResponse: " + "ATKJ data downloaded");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AtkjResponse> call, @NonNull Throwable t) {
                Log.d("AtkjDownload", "onResponse: " + "ATKJ data download failure");
            }
        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        Intent intent = new Intent("com.android.ServiceStopped");
//        sendBroadcast(intent);
//    }
}