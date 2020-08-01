package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.hangtv.util.API;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashTvActivity extends AppCompatActivity {

    MyApplication App;
    String str_package;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        App = MyApplication.getInstance();
        API api = new API();

        ImageView gif = findViewById(R.id.imgGif);
        TextView wait = findViewById(R.id.txtWait);

        Glide.with(SplashTvActivity.this)
                .load(R.drawable.load_video_gif_compp)
                .into(gif);

        Typeface oSSB = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
        wait.setTypeface(oSSB);

        NewDatabase alarmDatabase = new NewDatabase(this);

        Cursor language = alarmDatabase.getPrayTime("language");
        language.moveToFirst();

        if(language.getCount() != 0){
            String bahasa = language.getString(0);
            if (bahasa.equals("id")){
                wait.setText(R.string.splash_adverb);
            } else if (bahasa.equals("en")){
                wait.setText(R.string.splash_adverb_en);
            }
        }

        new Handler().postDelayed(() -> {
            JsonObject jsObj = new JsonObject();// = (JsonObject) new Gson().toJsonTree(new API());

            jsObj.addProperty("salt", api.getSalts());
            jsObj.addProperty("sign", api.getSigns());
            jsObj.addProperty("method_name", "get_app_details");

            if (JsonUtils.isNetworkAvailable(SplashTvActivity.this)) {
                new MyTaskDev(API.toBase64(jsObj.toString())).execute(Constant.API_URL);

                //showToast(jsObj.toString());
            } else {
                showToast(getString(R.string.nodata));
            }
        }, 2500);

    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskDev extends AsyncTask<String, Void, String> {

        String base64;

        private MyTaskDev(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
                dialogUpdateNow();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has("status")) {
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(SplashTvActivity.this);
                            dialog.setTitle(getString(R.string.dialog_error))
                                    .setMessage(getString(R.string.restart_msg))
                                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> dialog1.dismiss());

                            dialog.setCancelable(false);
                            dialog.show();

                            showToast(objJson.getString("status") + " Mohon segera laporkan ke WA +62 812 7500 3934");
                            dialogUpdateNow();

                        } else {
                            str_package = objJson.getString(Constant.APP_PACKAGE_NAME);

                            if (str_package.equals(getPackageName())) {

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
//                                final AlertDialog.Builder dialog = new AlertDialog.Builder(SplashTvActivity.this);
//                                dialog.setTitle(getString(R.string.dialog_error))
//                                        .setMessage(getString(R.string.license_msg))
//                                        .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
//                                            dialog1.dismiss();
//                                        });
//
//                                dialog.setCancelable(false);
//                                dialog.show();
                                dialogUpdateNow();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                }
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SplashTvActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        boolean mIsBackButtonPressed = true;
        super.onBackPressed();
    }

    private void dialogUpdateNow(){
        final Dialog dialog = new Dialog(SplashTvActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_stream);
        dialog.setCanceledOnTouchOutside(false);

        TextView txt1, txt2;
        txt1 = dialog.findViewById(R.id.txtNoInt1);
        txt2 = dialog.findViewById(R.id.txtNoInt2);

        txt1.setText(R.string.update_tv);
        txt2.setText(R.string.update_tv_content);

        Button btn = dialog.findViewById(R.id.btnOKEnoStream);
        btn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
