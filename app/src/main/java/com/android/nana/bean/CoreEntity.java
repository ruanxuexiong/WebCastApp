package com.android.nana.bean;

/**
 * Created by lenovo on 2017/8/21.
 */

public class CoreEntity {

    private String id;
    private String title;
    private String picture;
    private String organizer;
    private String num;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getIsEncryte() {
        return isEncryte;
    }

    public void setIsEncryte(String isEncryte) {
        this.isEncryte = isEncryte;
    }

    private String isEncryte;
}
