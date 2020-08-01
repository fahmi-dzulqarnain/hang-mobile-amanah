package com.efsoft.hangmedia.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.efsoft.hangmedia.activity.JadwalShalatActivity;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intents = new Intent(context, PrayTimeService.class);
        intents.setAction("DARI_RECEIVER");
        context.startService(intents);
        Toast.makeText(context, "Notifikasi Shalat Dimulai Ulang", Toast.LENGTH_SHORT).show();
        Log.d("AlarmBroadcast", "onReceive: AlarmResend");
    }
}
