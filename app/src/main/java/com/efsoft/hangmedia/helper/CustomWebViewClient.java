package com.efsoft.hangmedia.helper;

import android.app.Dialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {

    private Dialog dialog;

    public CustomWebViewClient(Dialog dialog) {
        this.dialog = dialog;
        dialog.show();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
        dialog.dismiss();
    }
}
