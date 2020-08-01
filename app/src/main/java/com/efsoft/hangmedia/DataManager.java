package com.efsoft.hangmedia;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

import com.efsoft.hangmedia.radio.RadioList;
import com.efsoft.hangmedia.radio.RadioListElement;
import com.efsoft.hangmedia.R;

public class DataManager {

    private Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    public void createRadioListForRadioScreen(TextView radioListName, TextView radioListLocation) {
        ArrayList<RadioListElement> radioArray = new ArrayList<RadioListElement>();
        radioArray.add(new RadioListElement(context, context.getResources().getString(R.string.radio_name), context.getResources().getString(R.string.radio_location), context.getResources().getString(R.string.radio_url)));
        RadioList radioList = new RadioList(context, radioArray);
        radioList.addRadioStations(radioListName, radioListLocation);
    }
}