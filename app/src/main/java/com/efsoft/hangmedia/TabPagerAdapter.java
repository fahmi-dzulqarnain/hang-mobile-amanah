package com.efsoft.hangmedia;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.efsoft.hangmedia.radio.MainScreen;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private MainScreen mainScreen = new MainScreen();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        return mainScreen;
    }

    @Override
    public int getCount() {
        return 1; //No of Tabs
    }
}
