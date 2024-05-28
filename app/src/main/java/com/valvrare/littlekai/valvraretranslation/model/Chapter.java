package com.valvrare.littlekai.valvraretranslation.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Kai on 9/24/2016.
 */
public class Chapter implements Parcelable {
    private int orderNo;
    private String novelName;
    private String name;
    private String url;
    private String second_url;
    private String novelUrl;
    private String novelImageUrl;
    private boolean isRead;
    private boolean isFav;
    private boolean isDown;
    private String chapterList;
    private float lastYHeight;
    private float lastZoom;
    private int lastY;
    private String date;
    private String time;
    private String second_name;
    private String insertedChapterDay;
    private ArrayList<String> path;
    private String img;
    private boolean isChecked = false;
    private boolean isEnableDownload = true;
    private int posState = 0;

    public int getPosState() {
        return posState;
    }

    public void setPosState(int posState) {
        this.posState = posState;
    }

    public boolean isEnableDownload() {
        return isEnableDownload;
    }

    public void setEnableDownload(boolean enableDownload) {
        isEnableDownload = enableDownload;
    }

    public String getSecond_url() {
        return second_url;
    }

    public void setSecond_url(String second_url) {
        this.second_url = second_url;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public Chapter(String n, String novelN, boolean read) {
        name = n;
        novelName = novelN;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String n, String u, String novelN, String date, boolean read) {
        name = n;
        url = u;
        insertedChapterDay = date;
        novelName = novelN;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String n, String u, String t, String d, String a) {
        name = n;
        url = u;
        time = t;
        date = d;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String n, String novelN, String t, String d) {
        name = n;
        novelName = novelN;
        time = t;
        date = d;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String n, String u) {
        name = n;
        url = u;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String n, String u, String i) {
        name = n;
        second_name = n;
        url = u;
        img = i;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public Chapter(String chapterN, String novelN, int lstY, float lastHeight) {
        name = chapterN;
        novelName = novelN;
        lastY = lstY;
        lastYHeight = lastHeight;
        isDown = false;
        isRead = false;
        isFav = false;
    }

    public String getInsertedChapterDay() {
        return insertedChapterDay;
    }

    public void setInsertedChapterDay(String insertedChapterDay) {
        this.insertedChapterDay = insertedChapterDay;
    }

    public float getLastZoom() {
        return lastZoom;
    }

    public void setLastZoom(float lastZoom) {
        this.lastZoom = lastZoom;
    }

    public String getChapterList() {
        return chapterList;
    }

    public void setChapterList(String chapterList) {
        this.chapterList = chapterList;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getNovelUrl() {
        return novelUrl;
    }

    public void setNovelUrl(String novelUrl) {
        this.novelUrl = novelUrl;
    }

    public String getNovelImageUrl() {
        return novelImageUrl;
    }

    public void setNovelImageUrl(String novelImageUrl) {
        this.novelImageUrl = novelImageUrl;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public void setRead(boolean read) {
        isRead = read;
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

    public void setPath(ArrayList<String> p) {
        path = p;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setIsFav(boolean isFav) {
        this.isFav = isFav;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public float getLastYHeight() {
        return lastYHeight;
    }

    public void setLastYHeight(float lastYHeight) {
        this.lastYHeight = lastYHeight;
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    protected Chapter(Parcel in) {
        orderNo = in.readInt();
//        lastY = in.readInt();
//        lastYHeight = in.readFloat();
        novelName = in.readString();
        name = in.readString();
        novelImageUrl = in.readString();
        novelUrl = in.readString();
        url = in.readString();
//        date = in.readString();
//        time = in.readString();
        second_name = in.readString();
//        insertedChapterDay = in.readString();
//        chapterList = in.readString();
//        second_url = in.readString();
//        img = in.readString();
        isRead = in.readByte() != 0x00;
        isFav = in.readByte() != 0x00;
        isDown = in.readByte() != 0x00;
        isEnableDownload = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            path = new ArrayList<String>();
            in.readList(path, String.class.getClassLoader());
        } else {
            path = null;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderNo);
//        dest.writeInt(lastY);
//        dest.writeFloat(lastYHeight);
        dest.writeString(novelName);
        dest.writeString(name);
        dest.writeString(novelImageUrl);
        dest.writeString(novelUrl);
        dest.writeString(url);
//        dest.writeString(date);
//        dest.writeString(time);
        dest.writeString(second_name);
//        dest.writeString(insertedChapterDay);
//        dest.writeString(chapterList);
//        dest.writeString(second_url);
//        dest.writeString(img);
        dest.writeByte((byte) (isRead ? 0x01 : 0x00));
        dest.writeByte((byte) (isFav ? 0x01 : 0x00));
        dest.writeByte((byte) (isDown ? 0x01 : 0x00));
        dest.writeByte((byte) (isEnableDownload ? 0x01 : 0x00));
        if (path == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(path);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}