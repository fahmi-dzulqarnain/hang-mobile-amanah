package com.efsoft.hangmedia.model;

public class Program2 {

    private String pembahasan, ustadz, jam, image_url;

    public Program2(){
    }

    public Program2(String pembahasan, String ustadz, String jam, String image_url){
        this.jam = jam;
        this.pembahasan = pembahasan;
        this.ustadz = ustadz;
        this.image_url = image_url;
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

    public String getImage_url() {
        return image_url;
    }
}
