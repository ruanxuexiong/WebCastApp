package com.android.nana.wanted;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/6/29.
 */

public class Status {
    private String id;
    private String parentId;
    private String name;
    private String ctime;
    private String type;
    private String orderId;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Three> getThree() {
        return three;
    }

    public void setThree(ArrayList<Three> three) {
        this.three = three;
    }

    private ArrayList<Three> three;
}
