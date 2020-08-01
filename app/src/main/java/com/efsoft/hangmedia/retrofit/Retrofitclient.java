package com.efsoft.hangmedia.retrofit;

import com.efsoft.hangmedia.api.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Retrofitclient {
    private static final String BASE_URL = "http://tiviapi.online/";
    private static Retrofitclient mInstance;
    private Retrofit retrofit;

    private Retrofitclient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized Retrofitclient getmInstance(){
        if(mInstance == null) {
            mInstance = new Retrofitclient();
        }
        return mInstance;
    }

    public ApiService getApi(){
        return retrofit.create(ApiService.class);
    }
}
