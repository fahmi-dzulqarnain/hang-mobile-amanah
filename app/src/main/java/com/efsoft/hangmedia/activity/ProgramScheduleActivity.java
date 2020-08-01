package com.efsoft.hangmedia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.adapter.JadwalPagerAdapter;
import com.efsoft.hangmedia.fragment.AhadFragment;
import com.efsoft.hangmedia.fragment.JumatFragment;
import com.efsoft.hangmedia.fragment.KamisFragment;
import com.efsoft.hangmedia.fragment.RabuFragment;
import com.efsoft.hangmedia.fragment.SabtuFragment;
import com.efsoft.hangmedia.fragment.SelasaFragment;
import com.efsoft.hangmedia.fragment.SeninFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class ProgramScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_schedule);

        TabLayout tabLayout = findViewById(R.id.tabJadwalProgram);
        ViewPager viewPager = findViewById(R.id.viewPagerJadwal);
        TextView txtActionBar = findViewById(R.id.txtJadwalAppBar);
        Typeface oSSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        txtActionBar.setTypeface(oSSB);

        JadwalPagerAdapter adapter = new JadwalPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new SeninFragment(this), "Sen");
        adapter.addFragment(new SelasaFragment(this), "Sel");
        adapter.addFragment(new RabuFragment(this), "Rab");
        adapter.addFragment(new KamisFragment(this), "Kam");
        adapter.addFragment(new JumatFragment(this), "Jum");
        adapter.addFragment(new SabtuFragment(this), "Sab");
        adapter.addFragment(new AhadFragment(this), "Aha");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
