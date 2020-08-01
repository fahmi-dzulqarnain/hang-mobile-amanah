package com.efsoft.hangmedia.model;

public class Program1 {

    private String namaProgram, time, live;
    private int image;

    public Program1(){
    }

    public Program1(String namaProgram, String time, String live, int image){
        this.namaProgram = namaProgram;
        this.time = time;
        this.live = live;
        this.image = image;
    }

    public String getNamaProgram() {
        return namaProgram;
    }

    public void setNamaProgram(String namaProgram) {
        this.namaProgram = namaProgram;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
