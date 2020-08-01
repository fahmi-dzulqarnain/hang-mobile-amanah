package com.efsoft.hangmedia.hangtv.util;

import android.content.Context;
import android.content.Intent;

import com.efsoft.hangmedia.hangtv.youtubeplaylist.CategoryItemActivity;

public class PopUpAdsCat {

    public static void ShowInterstitialAds(final Context mContext, final String Id, final String Name) {
        Intent intent = new Intent(mContext, CategoryItemActivity.class);
        intent.putExtra("Id", Id);
        intent.putExtra("name", Name);
        mContext.startActivity(intent);
    }
}
