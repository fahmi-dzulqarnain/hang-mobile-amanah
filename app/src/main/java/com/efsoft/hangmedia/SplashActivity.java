package com.efsoft.hangmedia;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.efsoft.hangmedia.activity.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        TextView txtHang, txtVersion;
        txtHang = findViewById(R.id.txtHangLoading);
        txtVersion = findViewById(R.id.txtVersionLoading);

        Typeface oSSB = Typeface.createFromAsset(getAssets(),"Montserrat-SemiBold.ttf");
        Typeface oSR = Typeface.createFromAsset(getAssets(),"Montserrat-Regular.ttf");

        txtHang.setTypeface(oSSB);
        txtVersion.setTypeface(oSR);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        },2000);
    }
}
