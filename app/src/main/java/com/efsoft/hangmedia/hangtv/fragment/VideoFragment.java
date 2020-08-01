package com.efsoft.hangmedia.hangtv.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.hangtv.adapter.VideoAdapter;
import com.efsoft.hangmedia.hangtv.item.ItemVideo;
import com.efsoft.hangmedia.hangtv.util.API;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.ItemOffsetDecoration;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class VideoFragment extends Fragment {

    ArrayList<ItemVideo> mListItem;
    public RecyclerView recyclerView;
    VideoAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 1));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_videos");
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getCategory(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }
        setHasOptionsMenu(true);
        return rootView;
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
                        ItemVideo objItem = new ItemVideo();
                        objItem.setVideoId(objJson.getString(Constant.VIDEO_ID));
                        objItem.setVideoName(objJson.getString(Constant.VIDEO_NAME));
                        objItem.setVideoImage(objJson.getString(Constant.VIDEO_IMAGE));
                        objItem.setVideoUrl(objJson.getString(Constant.VIDEO_URL));
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
        adapter = new VideoAdapter(getActivity(), mListItem);
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

//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_main, menu);
//
//        final SearchView searchView = (SearchView) menu.findItem(R.id.search)
//                .getActionView();
//
//        final MenuItem searchMenuItem = menu.findItem(R.id.search);
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                // TODO Auto-generated method stub
//                if (!hasFocus) {
//                    searchMenuItem.collapseActionView();
//                    searchView.setQuery("", false);
//                }
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//
//                if (adapter != null) {
//                    adapter.filter(newText);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//
//                return false;
//            }
//        });
//
//    }
}
