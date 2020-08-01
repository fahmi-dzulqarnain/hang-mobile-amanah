package com.efsoft.hangmedia.retrofit;

import com.google.gson.annotations.SerializedName;

public class TableProgram {
    @SerializedName("nama_program")
    private String nama_program;
    @SerializedName("pembahasan")
    private String pembahasan;
    @SerializedName("ustadz")
    private String ustadz;
    @SerializedName("jam")
    private String jam;
    @SerializedName("image_url")
    private String image_url;

    public TableProgram(String nama_program, String pembahasan, String ustadz, String jam, String image_url) {
        this.nama_program = nama_program;
        this.pembahasan = pembahasan;
        this.ustadz = ustadz;
        this.jam = jam;
        this.image_url = image_url;
    }

    public String getNama_program() {
        return nama_program;
    }

    public String getPembahasan() {
        return pembahasan;
    }

    public String getUstadz() {
        return ustadz;
    }

    public String getJam() {
        return jam;
    }

    public String getImage_url() {
        return image_url;
    }
}