package com.valvrare.littlekai.valvraretranslation.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Novel implements Parcelable {

    private String novelName;
    private String url;
    private String image;
    private String second_image;
    private String latestChapName;
    private String catelogies;
    private boolean isFav;
    private int view;
    private String updateDate;
    private double rate;
    private int rateCount;
    private Chapter chapter;
    private String date;
    private String time;
    private int daoChapters;
    private String summary;
    private boolean notify;
    private byte[] img_file;
    private String type;

private String chapterList;
    public Novel() {
    }
    public Novel(String novelName, String url, String image) {
        this.novelName = novelName;
        this.url = url;
        this.image = image;
    }
    public Novel(String novelName, String url, String image, int daoChapters) {
        this.novelName = novelName;
        this.url = url;
        this.image = image;
        this.daoChapters = daoChapters;
    }

    public Novel(String novelName, String url, String image, Chapter chapter) {
        this.novelName = novelName;
        this.url = url;
        this.image = image;
        this.chapter = chapter;
        isFav = false;
    }

    public Novel(String n, String u, String i, String l, String lu) {
        novelName = n;
        url = u;
        image = i;
        latestChapName = l;
        catelogies = lu;
        rate = 0.0;
        rateCount = 0;
        view = 0;
        updateDate = "";
        isFav = false;
    }

    public String getSecond_image() {
        return second_image;
    }

    public void setSecond_image(String second_image) {
        this.second_image = second_image;
    }


    public int getDaoChapters() {
        return daoChapters;
    }

    public void setDaoChapters(int daoChapters) {
        this.daoChapters = daoChapters;
    }


    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLatestChapName() {
        return latestChapName;
    }

    public void setLatestChapName(String latestChapName) {
        this.latestChapName = latestChapName;
    }

    public String getCatelogies() {
        return catelogies;
    }

    public void setCatelogies(String catelogies) {
        this.catelogies = catelogies;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public String getChapterList() {
        return chapterList;
    }

    public void setChapterList(String chapterList) {
        this.chapterList = chapterList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getImg_file() {
        return img_file;
    }

    public void setImg_file(byte[] img_file) {
        this.img_file = img_file;
    }

    protected Novel(Parcel in) {
        second_image = in.readString();
        type = in.readString();
        chapterList = in.readString();
        novelName = in.readString();
        url = in.readString();
        image = in.readString();
        summary = in.readString();
        latestChapName = in.readString();
        catelogies = in.readString();
        updateDate = in.readString();
        rate = in.readDouble();
        rateCount = in.readInt();
        view = in.readInt();
        isFav = in.readByte() != 0x00;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(second_image);
        dest.writeString(type);
        dest.writeString(chapterList);
        dest.writeString(novelName);
        dest.writeString(url);
        dest.writeString(image);
        dest.writeString(summary);
        dest.writeString(latestChapName);
        dest.writeString(catelogies);
        dest.writeString(updateDate);
        dest.writeDouble(rate);
        dest.writeInt(rateCount);
        dest.writeInt(view);
        dest.writeByte((byte) (isFav ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Novel> CREATOR = new Parcelable.Creator<Novel>() {
        @Override
        public Novel createFromParcel(Parcel in) {
            return new Novel(in);
        }

        @Override
        public Novel[] newArray(int size) {
            return new Novel[size];
        }
    };

}