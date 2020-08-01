package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.image_slider.FragmentSlider;
import com.efsoft.hangmedia.image_slider.SliderIndicator;
import com.efsoft.hangmedia.image_slider.SliderPagerAdapter;
import com.efsoft.hangmedia.image_slider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class CompanyProfileActivity extends AppCompatActivity {

    private SliderView sliderView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        TextView txtTitle1, txtTitle2, txtContent1, txtContent2, txtContinueRead, txtHangMedia, txtGaleri, txtMediaDakwah;

        txtTitle1 = findViewById(R.id.txtProfileTitle1);
        txtTitle2 = findViewById(R.id.txtProfileTitle2);
        txtContent1 = findViewById(R.id.txtContent1);
        txtContent2 = findViewById(R.id.txtContent2);
        txtContinueRead = findViewById(R.id.txtProfileClick);
        txtHangMedia = findViewById(R.id.txtHangMedia);
        txtGaleri = findViewById(R.id.txtGaleri);
        txtMediaDakwah = findViewById(R.id.txtMediaDakwahIslam);

        Typeface oSSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        Typeface oSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface oSR = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");

        txtTitle1.setTypeface(oSSB);
        txtTitle2.setTypeface(oSSB);
        txtContent1.setTypeface(oSR);
        txtContent2.setTypeface(oSR);
        txtContinueRead.setTypeface(oSR);
        txtHangMedia.setTypeface(oSB);
        txtGaleri.setTypeface(oSB);
        txtMediaDakwah.setTypeface(oSR);

        txtContinueRead.setOnClickListener(v -> {
            txtContinueRead.setVisibility(View.GONE);
            txtContent2.setVisibility(View.VISIBLE);
        });

        sliderView = findViewById(R.id.sliderView);
        linearLayout = findViewById(R.id.galleryContainer);
        setupSlider();

    }

    private void setupSlider() {
        sliderView.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentSlider.newInstance(R.drawable.studio_2));
        fragments.add(FragmentSlider.newInstance(R.drawable.studio_3));
        fragments.add(FragmentSlider.newInstance(R.drawable.mushalla));
        fragments.add(FragmentSlider.newInstance(R.drawable.shalat_mushalla));
        fragments.add(FragmentSlider.newInstance(R.drawable.production));

        SliderPagerAdapter adapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        sliderView.setAdapter(adapter);
        SliderIndicator indicator = new SliderIndicator(this, linearLayout, sliderView, R.drawable.indicator_circle);
        indicator.setPageCount(fragments.size());
        indicator.show();
    }
}