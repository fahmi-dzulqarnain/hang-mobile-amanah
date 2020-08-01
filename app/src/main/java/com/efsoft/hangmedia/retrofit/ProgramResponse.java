package com.efsoft.hangmedia.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProgramResponse {
    @SerializedName("data")
    public List<TableProgram> data;

    public ProgramResponse(List<TableProgram> data){
        this.data = data;
    }

    public List<TableProgram> getData() {
        return data;
    }
}
