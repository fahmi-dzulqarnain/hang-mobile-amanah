package com.efsoft.hangmedia.hangtv.item;

public class PlayListItem {

    private String PlaylistId;
    private String PlaylistName;
    private String PlaylistUserName;
    private String PlaylistImageUrl;
    private String PlaylistCatName;

    public String getPlaylistCatName() {
        return PlaylistCatName;
    }

    public void setPlaylistCatName(String playlistCatName) {
        PlaylistCatName = playlistCatName;
    }



    public String getPlaylistId() {
        return PlaylistId;
    }

    public void setPlaylistId(String Playlistid) {
        this.PlaylistId = Playlistid;
    }


    public String getPlaylistName() {
        return PlaylistName;
    }

    public void setPlaylistName(String Playlistname) {
        this.PlaylistName = Playlistname;
    }

    public String getPlaylistImageurl() {
        return PlaylistImageUrl;
    }

    public void setPlaylistImageurl(String Playlistimage) {
        this.PlaylistImageUrl = Playlistimage;
    }

    public String getPlaylistUserName() {
        return PlaylistUserName;
    }

    public void setPlaylistUserName(String Playlistusername) {
        this.PlaylistUserName = Playlistusername;
    }

}
