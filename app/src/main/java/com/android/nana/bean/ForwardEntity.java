package com.android.nana.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/17.
 * 转发搜索
 */

public class ForwardEntity {


    private ArrayList<Friends> friends;
    private ArrayList<Groups> groups;


    public ArrayList<Friends> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friends> friends) {
        this.friends = friends;
    }

    public ArrayList<Groups> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Groups> groups) {
        this.groups = groups;
    }


    public class Friends {

        private String id;
        private String avatar;
        private String user_nicename;
        private String username;
        private String status;
        private String backgroundImage;

        public boolean isChcked() {
            return chcked;
        }

        public void setChcked(boolean chcked) {
            this.chcked = chcked;
        }

        private boolean chcked;

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

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
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

        public String getBackgroundImage() {
            return backgroundImage;
        }

        public void setBackgroundImage(String backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        private String uname;
    }

    public class Groups {
        private String groupId;
        private String name;
        private String picture;
        private String cuid;

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

        private String ctime;
    }

}
