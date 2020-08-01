package com.efsoft.hangmedia.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.efsoft.hangmedia.database.AlarmDatabase;
import com.efsoft.hangmedia.notification.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class PrayTimeService extends Service {

    PendingIntent pendingIntent;
    AlarmManager[] alarmManagers = new AlarmManager[5];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            switch (Objects.requireNonNull(intent.getAction())){
                case "KIRIM_NOTIF":
                    String shubuh, dhuhur, ashar, maghrib, isya;
                    shubuh = intent.getStringExtra("SHUBUH_TIME");
                    dhuhur = intent.getStringExtra("DHUHUR_TIME");
                    ashar = intent.getStringExtra("ASHAR_TIME");
                    maghrib = intent.getStringExtra("MAGHRIB_TIME");
                    isya = intent.getStringExtra("ISYA_TIME");

                    AlarmDatabase database = new AlarmDatabase(this);
                    Cursor check = database.getPrayTime("ALARM_SHUBUH");
                    check.moveToFirst();

                    if (check.getCount() == 0){
                        database.insertJadwal("ALARM_SHUBUH", shubuh);
                        database.insertJadwal("ALARM_DHUHUR", dhuhur);
                        database.insertJadwal("ALARM_ASHAR", ashar);
                        database.insertJadwal("ALARM_MAGHRIB", maghrib);
                        database.insertJadwal("ALARM_ISYA", isya);
                    } else {
                        database.updateJadwal("ALARM_SHUBUH", shubuh);
                        database.updateJadwal("ALARM_DHUHUR", dhuhur);
                        database.updateJadwal("ALARM_ASHAR", ashar);
                        database.updateJadwal("ALARM_MAGHRIB", maghrib);
                        database.updateJadwal("ALARM_ISYA", isya);
                    }

                    Cursor cS, cD, cA, cM, cI;
                    cS = database.getPrayTime("ALARM_SHUBUH"); cS.moveToFirst();
                    cD = database.getPrayTime("ALARM_DHUHUR"); cD.moveToFirst();
                    cA = database.getPrayTime("ALARM_ASHAR"); cA.moveToFirst();
                    cM = database.getPrayTime("ALARM_MAGHRIB"); cM.moveToFirst();
                    cI = database.getPrayTime("ALARM_ISYA"); cI.moveToFirst();

                    setMultipleAlarms(cS.getString(0),
                            cD.getString(0),
                            cA.getString(0),
                            cM.getString(0),
                            cI.getString(0));
                    break;
                case "HAPUS_NOTIF":
                    cancelMultipleAlarms();
                    break;
                case "DARI_RECEIVER":
                    AlarmDatabase database2 = new AlarmDatabase(this);
                    Cursor cS2, cD2, cA2, cM2, cI2;
                    cS2 = database2.getPrayTime("ALARM_SHUBUH"); cS2.moveToFirst();
                    cD2 = database2.getPrayTime("ALARM_DHUHUR"); cD2.moveToFirst();
                    cA2 = database2.getPrayTime("ALARM_ASHAR"); cA2.moveToFirst();
                    cM2 = database2.getPrayTime("ALARM_MAGHRIB"); cM2.moveToFirst();
                    cI2 = database2.getPrayTime("ALARM_ISYA"); cI2.moveToFirst();

                    setMultipleAlarms(cS2.getString(0),
                            cD2.getString(0),
                            cA2.getString(0),
                            cM2.getString(0),
                            cI2.getString(0));
                    break;
            }

            Log.d("PrayTimeService", "onStartCommand: AlarmResetted");

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    public void setMultipleAlarms(String shubuh, String dhuhr, String asr, String maghrib, String isha){

        String[] s, d, a, m, is;
        s = shubuh.split(":");
        d = dhuhr.split(":"); a = asr.split(":");
        m = maghrib.split(":"); is = isha.split(":");

        List<Integer> hour = new ArrayList<>();
        hour.add(Integer.valueOf(s[0]));
        hour.add(Integer.valueOf(d[0]));
        hour.add(Integer.valueOf(a[0]));
        hour.add(Integer.valueOf(m[0]));
        hour.add(Integer.valueOf(is[0]));

        List<Integer> minutes = new ArrayList<>();
        minutes.add(Integer.valueOf(s[1]));
        minutes.add(Integer.valueOf(d[1]));
        minutes.add(Integer.valueOf(a[1]));
        minutes.add(Integer.valueOf(m[1]));
        minutes.add(Integer.valueOf(is[1]));

        List<String> strings = new ArrayList<>();
        strings.add("Shubuh");
        strings.add("Dzuhur");
        strings.add("Ashar");
        strings.add("Maghrib");
        strings.add("Isya");

    /*
      our alarmManager array size will be that minutes list size
    */
        Intent[] intents = new Intent[alarmManagers.length];

        for(int i = 0; i < alarmManagers.length; i++){
            intents[i] = new Intent(getApplicationContext(), AlarmReceiver.class);
            intents[i].putExtra("NAMASHALAT", strings.get(i));
            intents[i].putExtra("CODE", i);
      /*
        Here is very important,when we set one alarm, pending intent id becomes zero
        but if we want set multiple alarms pending intent id has to be unique so i counter
        is enough to be unique for PendingIntent
      */
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intents[i], PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            if (strings.get(i).equals("Shubuh")){
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hour.get(i));
                calendar.set(Calendar.MINUTE, minutes.get(i));
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, hour.get(i));
                calendar.set(Calendar.MINUTE, minutes.get(i));
            }

            alarmManagers[i] = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
            if (System.currentTimeMillis() < calendar.getTimeInMillis()){

                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    Objects.requireNonNull(alarmManagers[i]).setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d("PRAYTIME_SERVICE", "setMultipleAlarms: Alarm " + i + " are set");
                } else {
                    Objects.requireNonNull(alarmManagers[i]).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d("PRAYTIME_SERVICE", "setMultipleAlarms: Alarm " + i + " are set");
                }
            }
        }
    }

    private void cancelMultipleAlarms(){
        if(alarmManagers.length > 0){
            for (AlarmManager manager : alarmManagers) {
                manager.cancel(pendingIntent);
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
