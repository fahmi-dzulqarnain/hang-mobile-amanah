package com.efsoft.hangmedia.model;

public class AtkjSound {

    private String judul, tanggal, url;

    public AtkjSound(){
    }

    public AtkjSound(String judul, String tanggal, String url){
        this.judul = judul;
        this.tanggal = tanggal;
        this.url = url;
    }

    public String getJudul() {
        return judul;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getUrl() {
        return url;
    }
}
