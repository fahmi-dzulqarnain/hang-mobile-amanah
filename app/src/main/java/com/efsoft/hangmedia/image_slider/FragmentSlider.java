package com.efsoft.hangmedia.image_slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.efsoft.hangmedia.R;

import java.util.Objects;

public class FragmentSlider extends Fragment {

    private static final String ARG_PARAM1 = "params";

    private String imageUrl;
    private int imageResource;

    public FragmentSlider(){}

    public static FragmentSlider newInstance(String params) {
        FragmentSlider fragmentSlider = new FragmentSlider();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, params);
        fragmentSlider.setArguments(args);
        return fragmentSlider;
    }

    public static FragmentSlider newInstance(int params) {
        FragmentSlider fragmentSlider = new FragmentSlider();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, params);
        fragmentSlider.setArguments(args);
        return fragmentSlider;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM1) != null){
                imageUrl = getArguments().getString(ARG_PARAM1);
            } else {
                imageResource = getArguments().getInt(ARG_PARAM1);
            }
        }

        View view = inflater.inflate(R.layout.fragment_slider_item, container, false);
        ImageView img = view.findViewById(R.id.imgSlider);

        if (imageUrl != null){
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(imageUrl)
                    .placeholder(R.drawable.studio_2)
                    .into(img);
        } else {
            img.setImageResource(imageResource);
        }

        return view;
    }
}
