package com.efsoft.hangmedia.radio;

import com.efsoft.hangmedia.player.RadioPlayer;
import com.efsoft.hangmedia.R;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainScreen extends Fragment {

    private static ImageView loading;
    protected static FrameLayout mainView;
    private static ImageView btnPlayRadio;

    public static TextView getRadioListName() {
        return radioListName;
    }

    public static void setRadioListNameColor(int c) {
        radioListName.setTextColor(c);
    }

    private static TextView radioListName;

    public static ImageView getBtnPlayRadio() {
        return btnPlayRadio;
    }

    public static ImageView getLoadingImage() {
        return loading;
    }

    public static void setRadioListName(String test) {
        radioListName.setText(test);
    }

    public static void setBtnPlayRadioImage(int resource){
        btnPlayRadio.setImageResource(resource);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View android = inflater.inflate(R.layout.main_main, container, false);
        loading = android.findViewById(R.id.loading);
        radioListName = android.findViewById(R.id.mainRadioLocation);
        btnPlayRadio = android.findViewById(R.id.btnPlayRadio);
        btnPlayRadio.setImageResource(R.drawable.ic_play);

        Typeface oSSBItc = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-SemiBold.ttf");
        radioListName.setTypeface(oSSBItc);
        LinearLayout share = android.findViewById(R.id.shareImage);
        share.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url));
            startActivity(Intent.createChooser(intent, "Share with"));
        });

        return android;
    }
}
