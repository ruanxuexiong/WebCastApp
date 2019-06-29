package com.android.nana.wanted;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * Created by THINK on 2017/6/28.
 */

public class Position implements Serializable {

    private String id;
    private String parentId;
    private String name;
    private String ctime;
    private String type;
    private String orderId;

    public JSONArray getStatud() {
        return statud;
    }

    public void setStatud(JSONArray statud) {
        this.statud = statud;
    }

    private JSONArray statud;

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



}
