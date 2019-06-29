package com.android.nana.material;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 2018/4/24.
 */

public class WantBean implements Serializable {

    private String id;
    private String userId;
    private String thisUserId;
    private String orderId;
    private String UserStatus;
    private String time;
    private String addTime;
    private String old;
    private String status;
    private WantBean.User zx_user;
    private String zx_comment;
    private String zx_time;
    private String talkId;
    private ArrayList<String> message;
    private String zx_charge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThisUserId() {
        return thisUserId;
    }

    public void setThisUserId(String thisUserId) {
        this.thisUserId = thisUserId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getZx_user() {
        return zx_user;
    }

    public void setZx_user(User zx_user) {
        this.zx_user = zx_user;
    }

    public String getZx_comment() {
        return zx_comment;
    }

    public void setZx_comment(String zx_comment) {
        this.zx_comment = zx_comment;
    }

    public String getZx_time() {
        return zx_time;
    }

    public void setZx_time(String zx_time) {
        this.zx_time = zx_time;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

    public String getZx_charge() {
        return zx_charge;
    }

    public void setZx_charge(String zx_charge) {
        this.zx_charge = zx_charge;
    }


    public class User implements Serializable{
        private String id;
        private String avatar;
        private String username;
        private String status;
        private String position;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        private String company;
    }
}
