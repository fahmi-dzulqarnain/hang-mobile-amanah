package com.efsoft.hangmedia.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AtkjResponse {
    @SerializedName("data_atkj")
    public List<TableAtkj> data;

    public AtkjResponse(List<TableAtkj> data){
        this.data = data;
    }

    public List<TableAtkj> getDataAtkj() {
        return data;
    }
}
