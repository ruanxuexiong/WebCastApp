package com.android.nana.main;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/27.
 * 视频通话实体
 */

public class TimeBean implements Serializable {

    private String isFree;
    private String timeLeft;
    private String charge;
    private String type;
    private String pageId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }


    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }


}
