package com.valvrare.littlekai.valvraretranslation.model;

/**
 * Created by Kai on 12/28/2016.
 */

public class ImageModel {
//    private String name;
    private String path;
    private String url;
    private String chapterName;
    private String novelName;

    public ImageModel(String chapterName, String novelName, String path, String url) {
//        this.name = name;
        this.path = path;
        this.url = url;
        this.chapterName = chapterName;
        this.novelName = novelName;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }
}
