package com.efsoft.hangmedia.hangtv.youtubeplaylist;

import android.os.Bundle;
import android.view.Window;

import com.efsoft.hangmedia.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class YtPlayActivity extends YouTubeFailureRecoveryActivity {


    private String PId;
    YouTubePlayer player;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yt_play);
        Bundle b = getIntent().getExtras();
        PId = b.getString("PId");
        position = b.getInt("position");
        YouTubePlayerView youTubeView = findViewById(R.id.youtube_view);
        youTubeView
                .initialize(
                        getString(R.string.youtube_api_key),
                        YtPlayActivity.this);


    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        // TODO Auto-generated method stub
        if (!wasRestored) {
            this.player = player;
            player.loadPlaylist(PId, position, 0);
        }
    }

    @Override
    protected Provider getYouTubePlayerProvider() {
        // TODO Auto-generated method stub
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }
}
