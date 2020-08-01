package com.efsoft.hangmedia.api;

import com.efsoft.hangmedia.model.JadwalModel;
import com.efsoft.hangmedia.retrofit.AtkjResponse;
import com.efsoft.hangmedia.retrofit.ProgramResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<JadwalModel> getJadwal(@Url String url);

    @GET("hang_program_api/index.php")
    Call<ProgramResponse> getProgram();

    @GET("hang_atkj_api/index.php")
    Call<AtkjResponse> getAtkj();

}
