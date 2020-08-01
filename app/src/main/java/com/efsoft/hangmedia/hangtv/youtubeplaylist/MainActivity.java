package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.efsoft.hangmedia.R;
import com.efsoft.hangmedia.activity.HomeActivity;
import com.efsoft.hangmedia.database.NewDatabase;
import com.efsoft.hangmedia.hangtv.fragment.CategoryFragment;
import com.efsoft.hangmedia.hangtv.fragment.FavouriteFragment;
import com.efsoft.hangmedia.hangtv.fragment.HomeFragment;
import com.efsoft.hangmedia.hangtv.fragment.LatestFragment;
import com.efsoft.hangmedia.hangtv.fragment.VideoFragment;
import com.efsoft.hangmedia.hangtv.util.API;
import com.efsoft.hangmedia.hangtv.util.Constant;
import com.efsoft.hangmedia.hangtv.util.IsRTL;
import com.efsoft.hangmedia.hangtv.util.JsonUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    MyApplication MyApp;
    NavigationView navigationView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsRTL.ifSupported(MainActivity.this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);
        JsonUtils.setStatusBarGradiant(MainActivity.this);

        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_app_details");
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new MyTaskConsent(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();

        NewDatabase alarmDatabase = new NewDatabase(this);

        Cursor language = alarmDatabase.getPrayTime("language");
        language.moveToFirst();

        String home, cate, news, vide, favo;

        home = getString(R.string.menu_home);
        cate = getString(R.string.menu_category);
        news = getString(R.string.menu_latest);
        vide = getString(R.string.menu_video);
        favo = getString(R.string.menu_favourite);

        if(language.getCount() != 0){
            String bahasa = language.getString(0);
            if (bahasa.equals("id")){
                home = getString(R.string.menu_home);
                cate = getString(R.string.menu_category);
                news = getString(R.string.menu_latest);
                vide = getString(R.string.menu_video);
                favo = getString(R.string.menu_favourite);
            } else if (bahasa.equals("en")){
                home = getString(R.string.menu_home_en);
                cate = getString(R.string.menu_category_en);
                news = getString(R.string.menu_latest_en);
                vide = getString(R.string.menu_video_en);
                favo = getString(R.string.menu_favourite_en);
            }
        }

        String finalHome = home;
        String finalCate = cate;
        String finalNews = news;
        String finalVide = vide;
        String finalFavo = favo;
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.menu_go_home:
                    toolbar.setTitle(finalHome);
                    HomeFragment homeFragment1 = new HomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.Container, homeFragment1).commit();
                    return true;
                case R.id.menu_go_category:
                    toolbar.setTitle(finalCate);
                    CategoryFragment currentCategory = new CategoryFragment();
                    fragmentManager.beginTransaction().replace(R.id.Container, currentCategory).commit();
                    return true;
                case R.id.menu_go_latest:
                    toolbar.setTitle(finalNews);
                    LatestFragment latestFragment = new LatestFragment();
                    fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                    return true;
                case R.id.menu_go_video:
                    toolbar.setTitle(finalVide);
                    VideoFragment videoFragment = new VideoFragment();
                    fragmentManager.beginTransaction().replace(R.id.Container, videoFragment).commit();
                    return true;
                case R.id.menu_go_favourite:
                    toolbar.setTitle(finalFavo);
                    FavouriteFragment favouriteFragment = new FavouriteFragment();
                    fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                    return true;
                default:
                    return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//
//        final MenuItem searchMenuItem = menu.findItem(R.id.search);
//        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//
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
//            public boolean onQueryTextSubmit(String arg0) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
//                intent.putExtra("search", arg0);
//                startActivity(intent);
//                searchView.clearFocus();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String arg0) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });
//
//       return super.onCreateOptionsMenu(menu);
//    }

    public void highLightNavigation(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);

    }
    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskConsent extends AsyncTask<String, Void, String> {

        String base64;

        MyTaskConsent(String base64) {
            this.base64 = base64;
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

    }
}
