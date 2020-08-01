package com.efsoft.hangmedia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.azan.Azan;
import com.azan.AzanTimes;
import com.azan.Method;
import com.azan.astrologicalCalc.SimpleDate;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.api.ApiService;
import com.efsoft.hangmedia.api.ApiUrl;
import com.efsoft.hangmedia.background.PrayTimeService;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.model.JadwalModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JadwalShalatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    FloatingActionButton btnRefresh;
    RelativeLayout btnManualJadwal;
    Switch btnOffNotif;
    TextView txtLocation, txtDate, txtShubuh, txtShuruq, txtDzuhr, txtAsr, txtMaghreeb, txtIsha, txtElapsed, txtPrayUpcoming;
    ImageView illustration, transparent;
    String shubuh, shuruq, dhuhr, asr, maghrib, isha, praycoming;
    NewDatabase alarmDatabase;
    ProgressBar progressBar;
    private boolean isNotification;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_shalat);

        methodRequiresTwoPermission();

        alarmDatabase = new NewDatabase(this);
        Cursor isNotificationOn = alarmDatabase.getSetting("NOTIF");

        txtLocation = findViewById(R.id.txtLocationField);
        txtDate = findViewById(R.id.txtDateField);
        txtShubuh = findViewById(R.id.txtFajrField);
        txtShuruq = findViewById(R.id.txtShuruqField);
        txtDzuhr = findViewById(R.id.txtDhuhrField);
        txtAsr = findViewById(R.id.txtAsrField);
        txtMaghreeb = findViewById(R.id.txtMaghreebField);
        txtIsha = findViewById(R.id.txtIshaField);

        txtElapsed = findViewById(R.id.txtElapsedTime);
        txtPrayUpcoming = findViewById(R.id.txtPrayUpcoming);

        btnRefresh = findViewById(R.id.btnRefreshJadwal);
        btnManualJadwal = findViewById(R.id.btnJadwalManual);
        btnOffNotif = findViewById(R.id.btnTurnOffNotif);
        illustration = findViewById(R.id.imgShalatIllustration);
        transparent = findViewById(R.id.imgTransparent);

        progressBar = findViewById(R.id.progressJadwalShalat);
        progressBar.setVisibility(View.GONE);

        btnRefresh.setOnClickListener(v -> getAllPrayData());
        btnManualJadwal.setOnClickListener(v -> dialogGetLocation());
        btnOffNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked){
                cancelMultipleAlarms();
                isNotification = false;
                alarmDatabase.updateSetting("NOTIF", "false");
            } else {
                sendNotificationService();
                isNotification = true;
                alarmDatabase.updateSetting("NOTIF", "true");
            }
        });

        if (isNotificationOn == null){
            alarmDatabase.insertPengaturan("NOTIF", "true");
            isNotification = true;
        }
        else if (isNotificationOn.getCount() != 0){
            isNotificationOn.moveToFirst();
            generateButtonNotif(isNotificationOn.getString(0));
        } else {
            isNotification = true;
        }

        new Handler().postDelayed(this::updateDatabaseLotLangLoc, 1000);
    }

    private void generateButtonNotif(String bool){
        if (bool.equals("true")){
            btnOffNotif.setChecked(true);
            isNotification = true;
            sendNotificationService();
        } else {
            btnOffNotif.setChecked(false);
            isNotification = false;
            cancelMultipleAlarms();
        }
    }

    private void dialogFirstTime(){
        final Dialog dialog = new Dialog(JadwalShalatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_stream);
        dialog.setCanceledOnTouchOutside(false);

        TextView txt1, txt2;
        txt1 = dialog.findViewById(R.id.txtNoInt1);
        txt2 = dialog.findViewById(R.id.txtNoInt2);

        txt1.setText(R.string.default_location);
        txt2.setText(R.string.location_permission);

        Button btn = dialog.findViewById(R.id.btnOKEnoStream);
        btn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private String getCloseTime(){

        String result = "";
        String newTime;
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {

            Date currentTimeDate = sdf.parse(currentTime);
            Date shubhTime, dhuhrTime, asrTime, maghribTime, ishaTime;
            shubhTime = sdf.parse(shubuh);
            dhuhrTime = sdf.parse(dhuhr);
            asrTime = sdf.parse(asr);
            maghribTime = sdf.parse(maghrib);
            ishaTime = sdf.parse(isha);

            if (shubhTime.compareTo(currentTimeDate) > 0){
                if (isTwentyMinutesGreater(ishaTime, currentTimeDate)){

                    if (Integer.parseInt(currentTime.split(":")[0]) <= Integer.parseInt(shubuh.split(":")[0])){
                        if(Integer.parseInt(currentTime.split(":")[1]) <= Integer.parseInt(shubuh.split(":")[1])){
                            result = generateCloseTime(shubuh, currentTime);
                            newTime = shubuh;
                            praycoming = "Shubuh " + newTime;
                            illustration.setImageResource(R.drawable.shubuh_background);
                            transparent.setVisibility(View.GONE);
                        } else {
                            result = getFewMinutes(ishaTime, shubuh, currentTimeDate,
                                    "Shubuh " + shubuh, R.drawable.shubuh_background, false,
                                    "Isya " + isha, R.drawable.isha_background, false);
                        }
                    } else {
                        result = getFewMinutes(ishaTime, shubuh, currentTimeDate,
                                "Shubuh " + shubuh, R.drawable.shubuh_background, false,
                                "Isya " + isha, R.drawable.isha_background, false);
                    }

                } else {
                    result = generateCloseTime(shubuh);
                    newTime = shubuh;
                    praycoming = "Shubuh " + newTime;
                    illustration.setImageResource(R.drawable.shubuh_background);
                    transparent.setVisibility(View.GONE);
                }
            } else if (dhuhrTime.compareTo(currentTimeDate) > 0){
                if (isTwentyMinutesGreater(shubhTime, currentTimeDate)){
                    result = getFewMinutes(shubhTime, dhuhr, currentTimeDate,
                            "Dzuhur " + dhuhr, R.drawable.dzuhur_background, true,
                            "Shubuh " + shubuh, R.drawable.shubuh_background, false);
                } else {
                    result = generateCloseTime(dhuhr);
                    newTime = dhuhr;
                    praycoming = "Dzuhur " + newTime;
                    illustration.setImageResource(R.drawable.dzuhur_background);
                    transparent.setVisibility(View.VISIBLE);
                }
            } else if (asrTime.compareTo(currentTimeDate) > 0){
                if (isTwentyMinutesGreater(dhuhrTime, currentTimeDate)){
                    result = getFewMinutes(dhuhrTime, asr, currentTimeDate,
                            "Ashar " + asr, R.drawable.ashar_background, true,
                            "Dzuhur " + dhuhr, R.drawable.dzuhur_background, true);
                } else {
                    result = generateCloseTime(asr);
                    newTime = asr;
                    praycoming = "Ashar " + newTime;
                    illustration.setImageResource(R.drawable.ashar_background);
                    transparent.setVisibility(View.VISIBLE);
                }
            } else if (maghribTime.compareTo(currentTimeDate) > 0){
                if (isTwentyMinutesGreater(asrTime, currentTimeDate)){
                    result = getFewMinutes(asrTime, maghrib, currentTimeDate,
                            "Maghrib " + maghrib, R.drawable.maghrib_background, true,
                            "Ashar " + asr, R.drawable.ashar_background, true);
                } else {
                    result = generateCloseTime(maghrib);
                    newTime = maghrib;
                    praycoming = "Maghrib " + newTime;
                    illustration.setImageResource(R.drawable.maghrib_background);
                    transparent.setVisibility(View.VISIBLE);
                }
            } else if (ishaTime.compareTo(currentTimeDate) > 0){
                if (isTwentyMinutesGreater(maghribTime, currentTimeDate)){
                    result = getFewMinutes(maghribTime, isha, currentTimeDate,
                            "Isya " + isha, R.drawable.isha_background, false,
                            "Maghrib " + maghrib, R.drawable.maghrib_background, true);
                } else {
                    result = generateCloseTime(isha);
                    newTime = isha;
                    praycoming = "Isya " + newTime;
                    illustration.setImageResource(R.drawable.isha_background);
                    transparent.setVisibility(View.GONE);
                }
            } else {
                if (isTwentyMinutesGreater(ishaTime, currentTimeDate)){
                    result = getFewMinutes(ishaTime, shubuh, currentTimeDate,
                            "Shubuh " + shubuh, R.drawable.shubuh_background, false,
                            "Isya " + isha, R.drawable.isha_background, false);
                } else {
                    result = generateCloseTime(shubuh);
                    newTime = shubuh;
                    praycoming = "Shubuh " + newTime;
                    illustration.setImageResource(R.drawable.shubuh_background);
                    transparent.setVisibility(View.GONE);
                }
            }

        } catch (ParseException ignored) { }

        return result;
    }

    private void cancelMultipleAlarms(){
        Intent intent = new Intent(JadwalShalatActivity.this, PrayTimeService.class);
        intent.setAction("HAPUS_NOTIF");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private String getFewMinutes(Date pastTime, String newTime, Date now,
                                 String praycoming2, int imageResource, boolean transBack,
                                 String praycoming1, int imageResource1, boolean transback1){
        String result = "";
        try {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - pastTime.getTime());
            if (minutes <= 20){
                result = minutes + " menit yang lalu";
                praycoming = praycoming1;
                illustration.setImageResource(imageResource1);
                if (transback1)
                    transparent.setVisibility(View.VISIBLE);
                else
                    transparent.setVisibility(View.GONE);
            } else {
                result = generateCloseTime(newTime);
                praycoming = praycoming2;
                illustration.setImageResource(imageResource);
                if (transBack)
                    transparent.setVisibility(View.VISIBLE);
                else
                    transparent.setVisibility(View.GONE);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    private boolean isTwentyMinutesGreater(Date pastTime, Date now){
        boolean result = false;
        try {
            long millis = now.getTime() - pastTime.getTime();
            int compare = (int) ((millis / 60000) % 60);
            result = compare <= 20;

        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    private String generateCloseTime(String newTime, String currentTime){
        Calendar cP = Calendar.getInstance();
        String formatted;
        String[] sub = newTime.split(":");

        cP.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sub[0]));
        cP.set(Calendar.MINUTE, Integer.parseInt(sub[1]));

        if (newTime.equals(shubuh)){
            cP.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sub[0]));
            cP.set(Calendar.MINUTE, Integer.parseInt(sub[1]));

            DateTime end = new DateTime(cP.getTimeInMillis());
            Duration duration = new Interval(new Instant(), end).toDuration();
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(" Jam ")
                    .appendMinutes()
                    .appendSuffix(" Menit")
                    .appendSuffix(" Lagi")
                    .toFormatter();

            Period period = duration.toPeriod();
            Period timePeriod = period.normalizedStandard(PeriodType.time());
            formatted = formatter.print(timePeriod);
        } else {
            DateTime end = new DateTime(cP.getTimeInMillis());
            Duration duration = new Interval(new Instant(), end).toDuration();
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(" Jam ")
                    .appendMinutes()
                    .appendSuffix(" Menit Lagi")
                    .toFormatter();

            Period period = duration.toPeriod();
            Period timePeriod = period.normalizedStandard(PeriodType.time());
            formatted = formatter.print(timePeriod);
        }

        return formatted;
    }

    private String generateCloseTime(String newTime){
        Calendar cP = Calendar.getInstance();
        String formatted;
        String[] sub = newTime.split(":");

        cP.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sub[0]));
        cP.set(Calendar.MINUTE, Integer.parseInt(sub[1]));

        if (newTime.equals(shubuh)){

            cP.add(Calendar.DATE, 1);
            cP.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sub[0]));
            cP.set(Calendar.MINUTE, Integer.parseInt(sub[1]));

            DateTime end = new DateTime(cP.getTimeInMillis());
            Duration duration = new Interval(new Instant(), end).toDuration();
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(" Jam ")
                    .appendMinutes()
                    .appendSuffix(" Menit Lagi")
                    .toFormatter();

            Period period = duration.toPeriod();
            Period timePeriod = period.normalizedStandard(PeriodType.time());
            formatted = formatter.print(timePeriod);
        } else {
            DateTime end = new DateTime(cP.getTimeInMillis());
            Duration duration = new Interval(new Instant(), end).toDuration();
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(" Jam ")
                    .appendMinutes()
                    .appendSuffix(" Menit Lagi")
                    .toFormatter();

            Period period = duration.toPeriod();
            Period timePeriod = period.normalizedStandard(PeriodType.time());
            formatted = formatter.print(timePeriod);
        }

        return formatted;
    }

    private void sendNotificationService(){
        Intent intent = new Intent(JadwalShalatActivity.this, PrayTimeService.class);
        intent.putExtra("SHUBUH_TIME", shubuh);
        intent.putExtra("DHUHUR_TIME", dhuhr);
        intent.putExtra("ASHAR_TIME", asr);
        intent.putExtra("MAGHRIB_TIME", maghrib);
        intent.putExtra("ISYA_TIME", isha);
        intent.setAction("KIRIM_NOTIF");
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @AfterPermissionGranted(1000)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getCurrentLocation();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask_again),
                    1000, perms);
        }
    }

    private void getCurrentLocation() {
        // GET CURRENT LOCATION
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocation.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    // Display in Toast
                    Toast.makeText(JadwalShalatActivity.this, "Lat : " + location.getLatitude() + " Long : " + location.getLongitude(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getAllPrayData() {
        if (longitude != 0)
            generatePrayTimeToday(latitude, longitude);
    }

    private void getAllPrayData(double GMT) {
        if (longitude != 0)
            generatePrayTimeToday(latitude, longitude, GMT);
    }

    private void generatePrayTimeToday(double latitude, double longitude) {
        progressBar.setVisibility(View.VISIBLE);
        SimpleDateFormat date = new SimpleDateFormat("Z", Locale.getDefault());
        String localTime = date.format(new Date());
        String finalGMT = localTime.substring(0, 3) + "." + localTime.substring(3);

        SimpleDate today = new SimpleDate(new GregorianCalendar());
        com.azan.astrologicalCalc.Location location = new com.azan.astrologicalCalc.Location(latitude, longitude, Double.parseDouble(finalGMT), 0);
        Azan azan = new Azan(location, Method.Companion.getEGYPT_SURVEY());
        AzanTimes athan = azan.getPrayerTimes(today);

        shubuh = athan.fajr().getHour() + ":" + timeformat(athan.fajr().getMinute() + 2);
        dhuhr = athan.thuhr().getHour() + ":" + timeformat(athan.thuhr().getMinute() + 2);
        shuruq = athan.shuruq().getHour() + ":" + timeformat(athan.shuruq().getMinute());
        asr = athan.assr().getHour() + ":" + timeformat(athan.assr().getMinute() + 2);
        maghrib = athan.maghrib().getHour() + ":" + timeformat(athan.maghrib().getMinute() + 2);
        isha = athan.ishaa().getHour() + ":" + timeformat(athan.ishaa().getMinute() + 2);

        String closeTime = getCloseTime();
        String now = "Sekarang";

        if (closeTime.equals(""))
            txtElapsed.setText(now);
        else
            txtElapsed.setText(closeTime);

        txtPrayUpcoming.setText(praycoming);

        txtElapsed.setText(closeTime);
        txtPrayUpcoming.setText(praycoming);

        if (isNotification)
            sendNotificationService();

        Cursor cek = alarmDatabase.getSetting("city");
        cek.moveToFirst();

        if (cek.getCount() == 0)
            alarmDatabase.insertPengaturan("city", hereLocation(latitude, longitude));
        else
            alarmDatabase.updateSetting("city", hereLocation(latitude, longitude));

        txtLocation.setText(hereLocation(latitude, longitude));
        txtDate.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));
        txtShubuh.setText(shubuh);
        txtShuruq.setText(shuruq);
        txtDzuhr.setText(dhuhr);
        txtAsr.setText(asr);
        txtMaghreeb.setText(maghrib);
        txtIsha.setText(isha);
        progressBar.setVisibility(View.GONE);
    }

    private void generatePrayTimeToday(double latitude, double longitude, double GMT) {
        progressBar.setVisibility(View.VISIBLE);
        int dst;
        boolean dstBool = TimeZone.getDefault().inDaylightTime(new Date());
        if (dstBool)
            dst = 1;
        else
            dst = 0;

        SimpleDate today = new SimpleDate(new GregorianCalendar());
        com.azan.astrologicalCalc.Location location = new com.azan.astrologicalCalc.Location(latitude, longitude, GMT, dst);
        Azan azan = new Azan(location, Method.Companion.getEGYPT_SURVEY());
        AzanTimes athan = azan.getPrayerTimes(today);

        shubuh = athan.fajr().getHour() + ":" + timeformat(athan.fajr().getMinute() + 2);
        shuruq = athan.shuruq().getHour() + ":" + timeformat(athan.shuruq().getMinute());
        dhuhr = athan.thuhr().getHour() + ":" + timeformat(athan.thuhr().getMinute() + 2);
        asr = athan.assr().getHour() + ":" + timeformat(athan.assr().getMinute() + 2);
        maghrib = athan.maghrib().getHour() + ":" + timeformat(athan.maghrib().getMinute() + 2);
        isha = athan.ishaa().getHour() + ":" + timeformat(athan.ishaa().getMinute() + 2);

        String closeTime = getCloseTime();
        String now = "Sekarang";

        if (closeTime.equals("")) {
            txtElapsed.setText(now);
            txtPrayUpcoming.setText(praycoming);
        } else {
            txtElapsed.setText(closeTime);
            txtPrayUpcoming.setText(praycoming);
        }

        txtElapsed.setText(closeTime);
        txtPrayUpcoming.setText(praycoming);

        if (isNotification) {
            sendNotificationService();
        }

        Cursor cek = alarmDatabase.getSetting("city");
        cek.moveToFirst();

        if (cek.getCount() == 0)
            alarmDatabase.insertPengaturan("city", hereLocation(latitude, longitude));
        else
            alarmDatabase.updateSetting("city", hereLocation(latitude, longitude));

        txtLocation.setText(hereLocation(latitude, longitude));
        txtDate.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));
        txtShubuh.setText(shubuh);
        txtShuruq.setText(shuruq);
        txtDzuhr.setText(dhuhr);
        txtAsr.setText(asr);
        txtMaghreeb.setText(maghrib);
        txtIsha.setText(isha);
        progressBar.setVisibility(View.GONE);
    }

    private void dialogGetLocation() {
        final Dialog dialog = new Dialog(JadwalShalatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_location);

        RelativeLayout auto, batam, singapore, johor, kualaLumpur;
        auto = dialog.findViewById(R.id.relativeAuto);
        batam = dialog.findViewById(R.id.relativeBatam);
        singapore = dialog.findViewById(R.id.relativeSingapore);
        johor = dialog.findViewById(R.id.relativeJohorBahru);
        kualaLumpur = dialog.findViewById(R.id.relativeKualaLumpur);

        auto.setOnClickListener(v -> {
            methodRequiresTwoPermission();
            getCurrentLocation();
            updateDatabaseLotLangLoc();
            recreate();
            dialog.dismiss();
        });

        batam.setOnClickListener(v -> {
            latitude = 1.082828;
            longitude = 104.030457;
            updateDatabaseLotLangLoc(+07.00);
            recreate();
            dialog.dismiss();
        });

        singapore.setOnClickListener(v -> {
            latitude = 1.290270;
            longitude = 103.851959;
            updateDatabaseLotLangLoc(+08.00);
            recreate();
            dialog.dismiss();
        });

        johor.setOnClickListener(v -> {
            latitude = 1.4655;
            longitude = 103.7578;
            updateDatabaseLotLangLoc(+08.00);
            recreate();
            dialog.dismiss();
        });

        kualaLumpur.setOnClickListener(v -> {
            latitude = 3.140853;
            longitude = 101.693207;
            updateDatabaseLotLangLoc(+08.00);
            recreate();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateDatabaseLotLangLoc() {
        try {
            if (alarmDatabase.getSetting("latitude").getCount() == 0) {
                if (latitude == 0) {
                    dialogFirstTime();
                    latitude = 1.082828;
                    longitude = 104.030457;
                }

                alarmDatabase.insertPengaturan("latitude", String.valueOf(latitude));
                alarmDatabase.insertPengaturan("longitude", String.valueOf(longitude));

                getAllPrayData();
            } else {
                if (latitude == 0){
                    Cursor latitude_, longitude_;
                    latitude_ = Objects.requireNonNull(alarmDatabase.getSetting("latitude"));
                    longitude_ = Objects.requireNonNull(alarmDatabase.getSetting("longitude"));
                    latitude_.moveToFirst();
                    longitude_.moveToFirst();

                    if (latitude_.getCount() != 0) {
                        latitude = Double.parseDouble(latitude_.getString(0));
                        longitude = Double.parseDouble(longitude_.getString(0));

                        if (latitude != 0) {
                            getAllPrayData();
                        }
                    } else {
                        alarmDatabase.updateSetting("latitude", String.valueOf(latitude));
                        alarmDatabase.updateSetting("longitude", String.valueOf(longitude));

                        getAllPrayData();
                    }

                } else {
                    alarmDatabase.updateSetting("latitude", String.valueOf(latitude));
                    alarmDatabase.updateSetting("longitude", String.valueOf(longitude));

                    getAllPrayData();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this,  "onCreate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabaseLotLangLoc(double GMT) {
        try {
            if (alarmDatabase.getSetting("latitude").getCount() == 0) {
                if (latitude == 0) {
                    dialogFirstTime();
                    latitude = 1.082828;
                    longitude = 104.030457;
                }

                alarmDatabase.insertPengaturan("latitude", String.valueOf(latitude));
                alarmDatabase.insertPengaturan("longitude", String.valueOf(longitude));

                getAllPrayData(GMT);
            } else {
                if (latitude == 0) {
                    Cursor latitude_, longitude_;
                    latitude_ = Objects.requireNonNull(alarmDatabase.getSetting("latitude"));
                    longitude_ = Objects.requireNonNull(alarmDatabase.getSetting("longitude"));
                    latitude_.moveToFirst();
                    longitude_.moveToFirst();

                    if (latitude_.getCount() != 0) {
                        latitude = Double.parseDouble(latitude_.getString(0));
                        longitude = Double.parseDouble(longitude_.getString(0));
                        if (latitude != 0) {
                            getAllPrayData(GMT);
                        }
                    } else {
                        alarmDatabase.updateSetting("latitude", String.valueOf(latitude));
                        alarmDatabase.updateSetting("longitude", String.valueOf(longitude));

                        getAllPrayData(GMT);
                    }

                } else {
                    alarmDatabase.updateSetting("latitude", String.valueOf(latitude));
                    alarmDatabase.updateSetting("longitude", String.valueOf(longitude));

                    getAllPrayData(GMT);
                }
            }
        } catch (Exception e) {
            Log.d("RadioStreamActivity", "onCreate: " + e.getMessage());
        }
    }

    private String timeformat(int minute){
        String fixs;
        switch (minute) {
            case 1: fixs = "01"; break;
            case 2: fixs = "02"; break;
            case 3: fixs = "03"; break;
            case 4: fixs = "04"; break;
            case 5: fixs = "05"; break;
            case 6: fixs = "06"; break;
            case 7: fixs = "07"; break;
            case 8: fixs = "08"; break;
            case 9: fixs = "09"; break;
            default: fixs = String.valueOf(minute); break;
        }

        return fixs;
    }

    private String hereLocation(double lat, double lon){
        String kota = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> adresses;

        try{
            adresses = geocoder.getFromLocation(lat, lon , 10);
            if (adresses.size() > 0){
                for (Address adr: adresses){
                    if (adr.getLocality() != null && adr.getLocality().length() > 0){
                        kota = adr.getLocality();
                        break;
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return kota;
    }
}