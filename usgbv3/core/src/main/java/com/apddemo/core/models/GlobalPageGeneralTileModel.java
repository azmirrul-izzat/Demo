package com.apddemo.core.models;

public class GlobalPageGeneralTileModel {
    public String getTiletitle() {
        return tiletitle;
    }

    public void setTiletitle(String tiletitle) {
        this.tiletitle = tiletitle;
    }

    public String getTiledescription() {
        return tiledescription;
    }

    public void setTiledescription(String tiledescription) {
        this.tiledescription = tiledescription;
    }

    public String getTilelink() {
        return tilelink;
    }

    public void setTilelink(String tilelink) {
        this.tilelink = tilelink;
    }

    public String tiletitle;
    public String tiledescription;
    public String tilelink;

    public String getBgtileimage() {
        return bgtileimage;
    }

    public void setBgtileimage(String bgtileimage) {
        this.bgtileimage = bgtileimage;
    }

    public String bgtileimage;
}
