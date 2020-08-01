package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.adapter.CatListAdapter;
import com.efsoft.hangmedia.hangtv.item.ItemPlayList;
import com.efsoft.hangmedia.hangtv.util.API;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.IsRTL;
import com.efsoft.hangmedia.hangtv.util.ItemOffsetDecoration;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SearchActivity extends AppCompatActivity {

    ArrayList<ItemPlayList> mListItem;
    public RecyclerView recyclerView;
    CatListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Search;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        IsRTL.ifSupported(SearchActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_search));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        JsonUtils.setStatusBarGradiant(SearchActivity.this);

        Intent intent = getIntent();
        Search = intent.getStringExtra("search");
        mListItem = new ArrayList<>();
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(SearchActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "search_playlist");
        jsObj.addProperty("search_keyword", Search);
        if (JsonUtils.isNetworkAvailable(SearchActivity.this)) {
            new getCategory(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getCategory extends AsyncTask<String, Void, String> {

        String base64;

        private getCategory(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemPlayList objItem = new ItemPlayList();
                        objItem.setId(objJson.getInt(Constant.PLAYLIST_ID));
                        objItem.setPlayListName(objJson.getString(Constant.PLAYLIST_TITLE));
                        objItem.setImage(objJson.getString(Constant.PLAYLIST_IMAGE));
                        objItem.setPlayListUrl(objJson.getString(Constant.PLAYLIST_URL));
                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {
        adapter = new CatListAdapter(SearchActivity.this, mListItem, true);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
