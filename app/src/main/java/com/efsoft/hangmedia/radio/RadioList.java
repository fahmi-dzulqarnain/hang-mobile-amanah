package com.efsoft.hangmedia.radio;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.efsoft.hangmedia.player.RadioPlayer;

public class RadioList extends LinearLayout {

    private static ArrayList<RadioListElement> radioList;
    private static RadioPlayer mpt = new RadioPlayer();


    public RadioList(Context context, ArrayList<RadioListElement> radioList) {
        super(context);
        RadioList.radioList = radioList;
    }

    public static void resetOldSelectedRadio() {
        for (final RadioListElement rle : radioList) {
            if (rle.isPlayBol())
                rle.setElementDefault();
        }
    }

    public static void nextOrPreviousRadioStation(int action, final TextView mainRadioLocation, final TextView mainRadioName) {
        int index = -1;
        for (final RadioListElement rle : radioList) {
            if (rle.isPlayBol())
                index = radioList.indexOf(rle);
        }
        switch (action) {
            case 1:
                index = index + 1;
                if (index < radioList.size()) {
                    resetOldSelectedRadio();
                    radioList.get(index).touchUP();
                    mainRadioLocation.setText(radioList.get(index).getFrequency());
                    mainRadioName.setText(radioList.get(index).getName());
                    mpt.play(radioList.get(index));
                }
                break;
            case -1:
                index = index - 1;
                if (index >= 0) {
                    resetOldSelectedRadio();
                    radioList.get(index).touchUP();
                    mainRadioLocation.setText(radioList.get(index).getName());
                    mainRadioName.setText(radioList.get(index).getFrequency());
                    mpt.play(radioList.get(index));
                }
                break;

            case 0:
                if (index != -1)
                    mpt.play(radioList.get(index));
                break;
        }
    }

    public static void listeningReset(final TextView mainRadioLocation){
        int index = -1;
        for (final RadioListElement rle : radioList) {
            if (rle.getName().contentEquals(mainRadioLocation.getText()))
                index = radioList.indexOf(rle);
        }
        radioList.get(index).touchUP();
    }

    public void addRadioStations(final TextView mainRadioLocation, final TextView mainRadioName) {
        listeningReset(mainRadioLocation);
    }
}

