package com.efsoft.hangmedia.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.efsoft.hangmedia.player.RadioPlayer;

import java.util.Objects;

public class TelephonyManagerReceiver extends BroadcastReceiver {

    public static boolean message = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
                if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.PHONE_STATE")) {
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                    if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                        if (RadioPlayer.isStarted()) {
                            message = true;
                        }
                    }

                    if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                        if (RadioPlayer.isStarted()) {
                            message = true;
                            RadioPlayer.stopMediaPlayer();
                        }
                    }

                    if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                        if (RadioPlayer.isStarted()) {
                            message = true;
                            RadioPlayer.stopMediaPlayer();
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
