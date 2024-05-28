package com.valvrare.littlekai.valvraretranslation.model;

/**
 * Created by Kai on 8/4/2016.
 */
public class OptionMenu {
    private int imgId;
    private String title;

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public OptionMenu(String title, String subTitle, int imgId) {
        super();
        this.imgId = imgId;
        this.title = title;
        this.subTitle = subTitle;
    }

    private String subTitle;

    public OptionMenu(int imgId, String title) {
        this.imgId = imgId;
        this.title = title;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
