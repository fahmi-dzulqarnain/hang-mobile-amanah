package com.efsoft.hangmedia.hangtv.util;

import com.efsoft.hangmedia.BuildConfig;

import java.io.Serializable;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String SERVER_URL = BuildConfig.SERVER_URL;

    public static final String API_URL = SERVER_URL + "api.php";

    public static final String IMAGE_PATH = SERVER_URL + "images/";

    public static final String ARRAY_NAME = "YOUTUBE_PLAYLIST";

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_CID = "cid";
    public static final String CATEGORY_IMAGE = "category_image";

    public static final String PLAYLIST_ID = "pid";
    public static final String PLAYLIST_TITLE = "playlist_name";
    public static final String PLAYLIST_URL = "playlist_unique_id";
    public static final String PLAYLIST_IMAGE = "playlist_image";
    public static final String PLAYLIST_CAT_NAME = "category_name";


    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_author";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_description";
    public static final String APP_PRIVACY_POLICY = "app_privacy_policy";
    public static final String APP_PACKAGE_NAME = "package_name";

    public static final String HOME_CAT_ARRAY = "category_slider";
    public static final String HOME_SLIDER_ARRAY = "playlist_slider";
    public static final String HOME_FEATURED_ARRAY = "featured_channels";
    public static final String HOME_VIDEO_ARRAY = "videos";

    public static final String VIDEO_ID = "vid";
    public static final String VIDEO_NAME = "video_title";
    public static final String VIDEO_IMAGE = "video_image";
    public static final String VIDEO_URL = "video_url_id";

    public static final String YTPLAYURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&playlistId=";
    public static final String YTAPIKEY = "&key=";
    public static String PLAY_NAME_TITLE;

    //public static int AD_COUNT = 0;
    //public static int AD_COUNT_SHOW;

    //public static boolean isBanner = false, isInterstitial = false;
    //public static String adMobBannerId, adMobInterstitialId, adMobPublisherId;


}
