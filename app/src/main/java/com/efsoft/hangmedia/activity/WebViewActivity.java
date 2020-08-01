package com.efsoft.hangmedia.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.efsoft.hangmedia.helper.CustomWebViewClient;
import com.efsoft.hangmedia.R;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Dialog dialog = new Dialog(WebViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_progress);

        ProgressBar progressBar = dialog.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String URL = intent.getStringExtra("URL_KEY");
        final String title = intent.getStringExtra("TITLE_KEY");

        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
        TextView toolbarTitle = findViewById(R.id.txtToolbarKajian);
        toolbarTitle.setTypeface(fontRegular);
        toolbarTitle.setText(title);

        ImageView btnBack = findViewById(R.id.btnBackWebView);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(WebViewActivity.this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        ImageView btnDonate = findViewById(R.id.btnDonateWebView);
        btnDonate.setOnClickListener(v -> {
            if (isInternetOn()) {
                Intent intent1 = new Intent(WebViewActivity.this, WebViewActivity.class);
                intent1.putExtra("URL_KEY", "https://donate.hangmobile.site");
                intent1.putExtra("TITLE_KEY", "Infaq Shadaqah");
                startActivity(intent1);
                finish();
            } else {
                noInternetDialog();
            }
        });

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new CustomWebViewClient(dialog));

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            String newsUrl = appLinkData.toString();
            webView.loadUrl(newsUrl);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            toolbarTitle.setTypeface(fontRegular);
            toolbarTitle.setText("Artikel Hang Mobile");
        } else {
            webView.loadUrl(URL);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
            startActivity(new Intent(WebViewActivity.this, HomeActivity.class));
            overridePendingTransition(0,0);
            finish();
        }
    }


    private void noInternetDialog(){
        final Dialog dialog = new Dialog(WebViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_internet);

        Button buttonOK = dialog.findViewById(R.id.btnOKE);
        buttonOK.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public boolean isInternetOn(){
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = Objects.requireNonNull(conMgr).getActiveNetworkInfo();
        return netinfo != null;
    }
}
