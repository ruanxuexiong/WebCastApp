package com.android.nana.bean;

/**
 * Created by lenovo on 2017/9/19.
 */

public class GroupsEntity {

    private String groupId;
    private String name;
    private String picture;
    private String cuid;
    private String ctime;

    public boolean isChcked() {
        return chcked;
    }

    public void setChcked(boolean chcked) {
        this.chcked = chcked;
    }

    private boolean chcked;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getGonggao() {
        return gonggao;
    }

    public void setGonggao(String gonggao) {
        this.gonggao = gonggao;
    }

    private String gonggao;
}
