package com.android.nana.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/2.
 */

public class ZanEntity {

    private ArrayList<ZanEntity.Users> users;
    private String num;


    public ArrayList<ZanEntity.Users> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ZanEntity.Users> users) {
        this.users = users;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public class Users {
        private String id;
        private String avatar;
        private String username;
        private String user_nicename;
        private String status;
        private String uname;

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

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public Work getWorkHistorys() {
            return workHistorys;
        }

        public void setWorkHistorys(Work workHistorys) {
            this.workHistorys = workHistorys;
        }

        private Work workHistorys;

    }

    public class Work {
        private String id;
        private String userId;
        private String name;
        private String position;
        private String introduce;
        private String beginTime;
        private String endTime;
        private String addTime;
        private String picture;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getBegin() {
            return begin;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        private String begin;
    }
}
