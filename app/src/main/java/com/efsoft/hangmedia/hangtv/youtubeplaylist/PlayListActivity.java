package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.adapter.PlayListAdapter;
import com.efsoft.hangmedia.hangtv.item.PlayListItem;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.IsRTL;
import com.efsoft.hangmedia.hangtv.util.ItemOffsetDecoration;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.efsoft.hangmedia.hangtv.util.OnLoadMoreListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PlayListActivity extends AppCompatActivity {

    ArrayList<PlayListItem> mListItem;
    public RecyclerView recyclerView;
    PlayListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id, Name;
    String nextPaegToken, prevPageToken;
    boolean isLoadMore = false, isFirst = true, isFromNotification = false;
    LinearLayoutManager lLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        IsRTL.ifSupported(PlayListActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        JsonUtils.setStatusBarGradiant(PlayListActivity.this);

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        Name = intent.getStringExtra("name");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }
        setTitle(Name);

        mListItem = new ArrayList<>();
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        lLayout = new LinearLayoutManager(PlayListActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(lLayout);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(PlayListActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(PlayListActivity.this)) {
            new getPlayList().execute(Constant.YTPLAYURL + Id + Constant.YTAPIKEY + getResources().getString(R.string.youtube_api_key));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getPlayList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isFirst)
                showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return readPlayList(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (isFirst)
                showProgress(false);
            try {
                JSONObject mainJson = new JSONObject(result);
                if (mainJson.has("nextPageToken")) {
                    nextPaegToken = mainJson.getString("nextPageToken");
                    isLoadMore = true;
                } else {
                    isLoadMore = false;
                }

                if (mainJson.has("prevPageToken")) {
                    prevPageToken = mainJson.getString("prevPageToken");
                }

                JSONObject pageInfo = mainJson.getJSONObject("pageInfo");
                String totalResults = pageInfo.getString("totalResults");
                String resultsPerPage = pageInfo.getString("resultsPerPage");

                JSONArray jsonArray = mainJson.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PlayListItem item = new PlayListItem();

                    JSONObject snippet = jsonObject.getJSONObject("snippet");
                    String title = snippet.getString("title");
                    String description = snippet.getString("description");

                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject medium = thumbnails.getJSONObject("medium");
                    String url = medium.getString("url");

                    JSONObject id = snippet.getJSONObject("resourceId");
                    String videoId = id.getString("videoId");

                    item.setPlaylistId(videoId);
                    item.setPlaylistName(title);
                    item.setPlaylistUserName(description);
                    item.setPlaylistImageurl(url);
                    mListItem.add(item);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isFirst) {
                displayData();
            } else {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }

        }

    }

    private void displayData() {
        adapter = new PlayListAdapter(PlayListActivity.this, mListItem, recyclerView, Id);
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(() -> {
            if (isLoadMore) {
                mListItem.add(null);
                recyclerView.post(() -> adapter.notifyItemInserted(mListItem.size() - 1));
                new Handler().postDelayed(() -> {
                    mListItem.remove(mListItem.size() - 1);
                    adapter.notifyItemRemoved(mListItem.size());
                    adapter.notifyDataSetChanged();
                    if (JsonUtils.isNetworkAvailable(PlayListActivity.this)) {
                        isFirst = false;
                        new getPlayList().execute(Constant.YTPLAYURL + Id + Constant.YTAPIKEY + getResources().getString(R.string.youtube_api_key) + "&pageToken=" + nextPaegToken);
                    }

                }, 1200);
            } else {
                Log.d("PlayListActivity", "displayData: No More Videos");
            }

        });

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
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(PlayListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    public String readPlayList(String Url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(PlayListActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
