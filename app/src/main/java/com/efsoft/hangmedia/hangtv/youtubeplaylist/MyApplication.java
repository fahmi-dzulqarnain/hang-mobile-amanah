package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.efsoft.hangmedia.R;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    public MyApplication() {
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(new ViewPump.Builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build())
                ).build());

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            JSONObject data = result.notification.payload.additionalData;
            String customKey;
            String isExternalLink, channelName;
            if (data != null) {
                customKey = data.optString("playlist_id", null);
                channelName = data.optString("playlist_name", null);
                isExternalLink = data.optString("external_link", null);
                if (customKey != null) {
                    if (!customKey.equals("0")) {
                        Intent intent = new Intent(MyApplication.this, PlayListActivity.class);
                        intent.putExtra("Id", customKey);
                        intent.putExtra("name", channelName);
                        intent.putExtra("isNotification", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        if (!isExternalLink.equals("false")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(isExternalLink));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MyApplication.this, SplashTvActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }
        }
    }
}