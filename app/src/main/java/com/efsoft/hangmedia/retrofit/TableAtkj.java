package com.efsoft.hangmedia.retrofit;

import com.google.gson.annotations.SerializedName;

public class TableAtkj {
    @SerializedName("judul_atkj")
    private String judul;
    @SerializedName("tgl_atkj")
    private String tanggal;
    @SerializedName("file_audio")
    private String sound_url;

    public TableAtkj(String judul, String tanggal, String sound_url) {
        this.judul = judul;
        this.tanggal = tanggal;
        this.sound_url = sound_url;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJudul() {
        return judul;
    }

    public String getSound_url() {
        return sound_url;
    }
}