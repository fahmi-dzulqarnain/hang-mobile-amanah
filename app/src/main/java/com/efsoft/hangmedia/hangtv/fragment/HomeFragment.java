package com.efsoft.hangmedia.hangtv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.hangtv.adapter.CategoryHomeAdapter;
import com.efsoft.hangmedia.hangtv.adapter.HomePlayListAdapter;
import com.efsoft.hangmedia.hangtv.adapter.HomeVideoAdapter;
import com.efsoft.hangmedia.hangtv.item.ItemCategory;
import com.efsoft.hangmedia.hangtv.item.ItemPlayList;
import com.efsoft.hangmedia.hangtv.item.ItemVideo;
import com.efsoft.hangmedia.hangtv.util.API;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.EnchantedViewPager;
import com.efsoft.hangmedia.hangtv.util.ItemOffsetDecoration;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.efsoft.hangmedia.hangtv.util.PopUpAds;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.MainActivity;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.MoreActivity;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.PlayListActivity;
import com.efsoft.hangmedia.hangtv.youtubeplaylist.SearchActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    Button btnLatest, btnFeatured, btnVideo;
    RecyclerView mLatestView, mFeaturedView, mVideoView;
    HomePlayListAdapter mFeaturedAdapter;
    ArrayList<ItemPlayList> mFeaturedList;
    ArrayList<ItemCategory> mLatestList;
    EnchantedViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;
    ArrayList<ItemPlayList> mSliderList;
    CircleIndicator circleIndicator;
    int currentCount = 0;
    CategoryHomeAdapter categoryHomeAdapter;
    ArrayList<ItemVideo> itemVideos;
    HomeVideoAdapter homeVideoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        btnLatest = rootView.findViewById(R.id.btn_latest);
        btnFeatured = rootView.findViewById(R.id.btn_featured);
        btnVideo = rootView.findViewById(R.id.btn_featuredv);
        mLatestView = rootView.findViewById(R.id.rv_latest);
        mFeaturedView = rootView.findViewById(R.id.rv_featured);
        mVideoView = rootView.findViewById(R.id.rv_featuredv);

        mLatestList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();
        mSliderList = new ArrayList<>();
        itemVideos = new ArrayList<>();

        mLatestView.setHasFixedSize(false);
        mLatestView.setNestedScrollingEnabled(false);
        mLatestView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        mLatestView.addItemDecoration(itemDecoration);

        mFeaturedView.setHasFixedSize(false);
        mFeaturedView.setNestedScrollingEnabled(false);
        mFeaturedView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mFeaturedView.addItemDecoration(itemDecoration);

        mVideoView.setHasFixedSize(false);
        mVideoView.setNestedScrollingEnabled(false);
        mVideoView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mVideoView.addItemDecoration(itemDecoration);

        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        mScrollView = rootView.findViewById(R.id.scrollView);
        mViewPager = rootView.findViewById(R.id.viewPager);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_home");
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new Home(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        RelativeLayout btnLiveStream = rootView.findViewById(R.id.btnLiveStream);
        btnLiveStream.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/c/HangTV/live"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.google.android.youtube");

            Intent check = getActivity().getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");

            if (check != null)
                startActivity(intent);
        });


        NewDatabase alarmDatabase = new NewDatabase(getActivity());

        Cursor language = alarmDatabase.getPrayTime("language");
        language.moveToFirst();

        TextView txtLiveStream = rootView.findViewById(R.id.txtLiveTV);

        if(language.getCount() != 0){
            String bahasa = language.getString(0);
            if (bahasa.equals("id")){
                txtLiveStream.setText(getString(R.string.live_stream_info));
            } else if (bahasa.equals("en")){
                txtLiveStream.setText(getString(R.string.live_stream_info_en));
            }
        }

        btnLatest.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).highLightNavigation(1);
            String categoryName = getString(R.string.menu_category);
            FragmentManager fm = getFragmentManager();
            CategoryFragment f1 = new CategoryFragment();
            assert fm != null;
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.Container, f1, categoryName);
            ft.commit();
            ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
        });

        btnFeatured.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MoreActivity.class);
            startActivity(intent);
        });

        btnVideo.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).highLightNavigation(3);
            String categoryName = getString(R.string.menu_video);
            FragmentManager fm = getFragmentManager();
            VideoFragment f1 = new VideoFragment();
            assert fm != null;
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.Container, f1, categoryName);
            ft.commit();
            ((MainActivity) requireActivity()).setToolbarTitle(categoryName);
        });

        mViewPager.useScale();
        mViewPager.removeAlpha();
        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class Home extends AsyncTask<String, Void, String> {

        String base64;

        private Home(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray jsonLatestSlider = jsonArray.getJSONArray(Constant.HOME_SLIDER_ARRAY);
                    JSONObject objJsonSl;
                    for (int i = 0; i < jsonLatestSlider.length(); i++) {
                        objJsonSl = jsonLatestSlider.getJSONObject(i);
                        ItemPlayList objItem = new ItemPlayList();
                        objItem.setId(objJsonSl.getInt(Constant.PLAYLIST_ID));
                        objItem.setPlayListName(objJsonSl.getString(Constant.PLAYLIST_TITLE));
                        objItem.setImage(objJsonSl.getString(Constant.PLAYLIST_IMAGE));
                        objItem.setPlayListUrl(objJsonSl.getString(Constant.PLAYLIST_URL));
                        objItem.setPlayCatName(objJsonSl.getString(Constant.PLAYLIST_CAT_NAME));
                        mSliderList.add(objItem);
                    }

                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_CAT_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJson = jsonLatest.getJSONObject(i);
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryId(objJson.getInt(Constant.CATEGORY_CID));
                        objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                        objItem.setCategoryImage(objJson.getString(Constant.CATEGORY_IMAGE));
                        mLatestList.add(objItem);
                    }

                    JSONArray jsonFeatured = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonFeature;
                    for (int i = 0; i < jsonFeatured.length(); i++) {
                        objJsonFeature = jsonFeatured.getJSONObject(i);
                        ItemPlayList objItem = new ItemPlayList();
                        objItem.setId(objJsonFeature.getInt(Constant.PLAYLIST_ID));
                        objItem.setPlayListName(objJsonFeature.getString(Constant.PLAYLIST_TITLE));
                        objItem.setImage(objJsonFeature.getString(Constant.PLAYLIST_IMAGE));
                        objItem.setPlayListUrl(objJsonFeature.getString(Constant.PLAYLIST_URL));
                        objItem.setPlayCatName(objJsonFeature.getString(Constant.PLAYLIST_CAT_NAME));
                        mFeaturedList.add(objItem);
                    }

                    JSONArray jsonVideo = jsonArray.getJSONArray(Constant.HOME_VIDEO_ARRAY);
                    JSONObject objJsonVideo;
                    for (int i = 0; i < jsonVideo.length(); i++) {
                        objJsonVideo = jsonVideo.getJSONObject(i);
                        ItemVideo objItem = new ItemVideo();
                        objItem.setVideoId(objJsonVideo.getString(Constant.VIDEO_ID));
                        objItem.setVideoName(objJsonVideo.getString(Constant.VIDEO_NAME));
                        objItem.setVideoImage(objJsonVideo.getString(Constant.VIDEO_IMAGE));
                        objItem.setVideoUrl(objJsonVideo.getString(Constant.VIDEO_URL));
                        itemVideos.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        categoryHomeAdapter = new CategoryHomeAdapter(getActivity(), mLatestList);
        mLatestView.setAdapter(categoryHomeAdapter);

        mFeaturedAdapter = new HomePlayListAdapter(getActivity(), mFeaturedList, false);
        mFeaturedView.setAdapter(mFeaturedAdapter);

        homeVideoAdapter = new HomeVideoAdapter(getActivity(), itemVideos);
        mVideoView.setAdapter(homeVideoAdapter);

        if (!mSliderList.isEmpty()) {
            mAdapter = new CustomViewPagerAdapter();
            mViewPager.setAdapter(mAdapter);
            circleIndicator.setViewPager(mViewPager);
            autoPlay(mViewPager);
        }

    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        private CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = requireActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mSliderList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_item_slider, container, false);
            assert imageLayout != null;
            ImageView image = imageLayout.findViewById(R.id.image);
            TextView text_title = imageLayout.findViewById(R.id.text);
            RelativeLayout lytParent = imageLayout.findViewById(R.id.rootLayout);
            TextView text_cat = imageLayout.findViewById(R.id.text_cat);

            final ItemPlayList itemSlider = mSliderList.get(position);

            text_title.setText(itemSlider.getPlayListName());
            text_cat.setText(itemSlider.getPlayCatName());
            Picasso.get().load(itemSlider.getImage()).into(image);
            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

            lytParent.setOnClickListener(v -> {
                Constant.PLAY_NAME_TITLE = itemSlider.getPlayCatName();
                PopUpAds.ShowInterstitialAds(requireActivity(), itemSlider.getPlayListUrl(), itemSlider.getPlayListName());

            });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(() -> {
            try {
                if (mAdapter != null && viewPager.getAdapter().getCount() > 0) {
                    int position = currentCount % mAdapter.getCount();
                    currentCount++;
                    viewPager.setCurrentItem(position);
                    autoPlay(viewPager);
                }
            } catch (Exception e) {
                Log.e("TAG", "auto scroll pager error.", e);
            }
        }, 2500);
    }

//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_main, menu);
//
//        final SearchView searchView = (SearchView) menu.findItem(R.id.search)
//                .getActionView();
//
//        final MenuItem searchMenuItem = menu.findItem(R.id.search);
//        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
//            // TODO Auto-generated method stub
//            if (!hasFocus) {
//                searchMenuItem.collapseActionView();
//                searchView.setQuery("", false);
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(requireActivity(), SearchActivity.class);
//                intent.putExtra("search", query);
//                startActivity(intent);
//                searchView.clearFocus();
//                return false;
//            }
//        });
//    }
}