package com.efsoft.hangmedia.model;

public class JadwalProgram {

    private String pembahasan, ustadz, jam;
    private boolean tv_live;

    public JadwalProgram(){
    }

    public JadwalProgram(String pembahasan, String ustadz, String jam, boolean tv_live){
        this.jam = jam;
        this.pembahasan = pembahasan;
        this.ustadz = ustadz;
        this.tv_live = tv_live;
    }

    public String getJam() {
        return jam;
    }

    public String getPembahasan() {
        return pembahasan;
    }

    public String getUstadz() {
        return ustadz;
    }

    public boolean getIsTvLive() {
        return tv_live;
    }
}
