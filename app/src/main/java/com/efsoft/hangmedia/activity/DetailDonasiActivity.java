package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.image_slider.FragmentSlider;
import com.efsoft.hangmedia.image_slider.SliderIndicator;
import com.efsoft.hangmedia.image_slider.SliderPagerAdapter;
import com.efsoft.hangmedia.image_slider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class DetailDonasiActivity extends AppCompatActivity {

    private SliderView sliderView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_donasi);

        NewDatabase alarmDatabase = new NewDatabase(this);

        TextView txtActionBar, txtContentBar;
        txtActionBar = findViewById(R.id.txtInfaqShadaqah);
        txtContentBar = findViewById(R.id.txtKeteranganInfaq);

        Typeface mSSB, mR;
        mSSB = Typeface.createFromAsset(getAssets(), "Montserrat-SemiBold.ttf");
        mR = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        txtActionBar.setTypeface(mSSB);
        txtContentBar.setTypeface(mR);

        RelativeLayout actionBar = findViewById(R.id.toobarDonasi);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)actionBar.getLayoutParams();
        relativeParams.setMargins(0, getStatusBarHeight(), 0, 0);
        actionBar.setLayoutParams(relativeParams);

        TextView txtDonasiUntukTitle, txtPeruntukanDonasi1, txtPeruntukanDonasi2, txtPeruntukanDonasi3;
        txtDonasiUntukTitle = findViewById(R.id.txtDonasiUntukTitle);
        txtPeruntukanDonasi1 = findViewById(R.id.txtPeruntukanDonasi1);
        txtPeruntukanDonasi2 = findViewById(R.id.txtPeruntukanDonasi2);
        txtPeruntukanDonasi3 = findViewById(R.id.txtPeruntukanDonasi3);

        Button btnBni, btnMandiri;
        btnBni = findViewById(R.id.btnSalinBni);
        btnMandiri = findViewById(R.id.btnSalinMandiri);

        String disalin = "No. Rek disalin";

        try {
            Cursor language = alarmDatabase.getPrayTime("language");
            language.moveToFirst();

            if(language.getCount() != 0){
                String bahasa = language.getString(0);
                if (bahasa.equals("id")){
                    txtActionBar.setText(R.string.investasikan_harta);
                    txtContentBar.setText(R.string.investasikan_harta_desc);
                    txtDonasiUntukTitle.setText(R.string.donasi_utk_title);
                    txtPeruntukanDonasi1.setText(R.string.peruntukan_donasi_1);
                    txtPeruntukanDonasi2.setText(R.string.peruntukan_donasi_2);
                    txtPeruntukanDonasi3.setText(R.string.peruntukan_donasi_3);
                    btnBni.setText(R.string.salin);
                    btnMandiri.setText(R.string.salin);
                } else if (bahasa.equals("en")){
                    txtActionBar.setText(R.string.investasikan_harta_en);
                    txtContentBar.setText(R.string.investasikan_harta_desc_en);
                    txtDonasiUntukTitle.setText(R.string.donasi_utk_title_en);
                    txtPeruntukanDonasi1.setText(R.string.peruntukan_donasi_1_en);
                    txtPeruntukanDonasi2.setText(R.string.peruntukan_donasi_2_en);
                    txtPeruntukanDonasi3.setText(R.string.peruntukan_donasi_3_en);
                    btnBni.setText(R.string.salin_en);
                    btnMandiri.setText(R.string.salin_en);
                    disalin = getResources().getString(R.string.copied);
                }
            }

        } catch (Exception e){
            Log.d("DetailDonasiActivity", "onCreate: " + e.getMessage());
        }

        String finalDisalin = disalin;
        btnBni.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "6060111106");
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DetailDonasiActivity.this, finalDisalin, Toast.LENGTH_SHORT).show();
            }
        });

        btnMandiri.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "7999966106");
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DetailDonasiActivity.this, finalDisalin, Toast.LENGTH_SHORT).show();
            }
        });

        sliderView = findViewById(R.id.sliderViewDonasi);
        linearLayout = findViewById(R.id.galleryContainerDonasi);
        setupSlider();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupSlider() {
        sliderView.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentSlider.newInstance(R.drawable.donor_darah_1));
        fragments.add(FragmentSlider.newInstance(R.drawable.donor_darah_2));
        fragments.add(FragmentSlider.newInstance(R.drawable.studio_2));
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