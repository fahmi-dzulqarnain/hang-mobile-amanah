package com.efsoft.hangmedia.image_slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "SliderPagerAdapter";

    List<Fragment> fragments = new ArrayList<>();

    public SliderPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments){
        super(fragmentManager);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        int index = position % fragments.size();
        if (fragments.get(index).getArguments().getString("params") != null) {
            return FragmentSlider.newInstance(fragments.get(index).getArguments().getString("params"));
        } else {
            return FragmentSlider.newInstance(fragments.get(index).getArguments().getInt("params"));
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

}
