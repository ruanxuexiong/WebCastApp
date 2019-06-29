package com.android.nana.friend;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/13.
 */

public class FriendSearchEntity {

    private ArrayList<Moments> moments;

    public ArrayList<Moments> getMoments() {
        return moments;
    }

    public void setMoments(ArrayList<Moments> moments) {
        this.moments = moments;
    }

    public class Moments {
        private String id;
        private String userId;
        private String title;
        private String content;
        private String content_text;
        private String addTime;
        private String id_del;
        private String comment_count;
        private String repost_count;
        private String view_count;
        private String art;
        private String sort;
        private User user;
        private String time;
        private String type;
        private ArrayList<UserArticlePictures> userArticlePictures;
        private String videoHeight;

        public String getVideoHeight() {
            return videoHeight;
        }

        public void setVideoHeight(String videoHeight) {
            this.videoHeight = videoHeight;
        }

        public String getVideoWidth() {
            return videoWidth;
        }

        public void setVideoWidth(String videoWidth) {
            this.videoWidth = videoWidth;
        }

        private String videoWidth;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent_text() {
            return content_text;
        }

        public void setContent_text(String content_text) {
            this.content_text = content_text;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getId_del() {
            return id_del;
        }

        public void setId_del(String id_del) {
            this.id_del = id_del;
        }

        public String getComment_count() {
            return comment_count;
        }

        public void setComment_count(String comment_count) {
            this.comment_count = comment_count;
        }

        public String getRepost_count() {
            return repost_count;
        }

        public void setRepost_count(String repost_count) {
            this.repost_count = repost_count;
        }

        public String getView_count() {
            return view_count;
        }

        public void setView_count(String view_count) {
            this.view_count = view_count;
        }

        public String getArt() {
            return art;
        }

        public void setArt(String art) {
            this.art = art;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ArrayList<UserArticlePictures> getUserArticlePictures() {
            return userArticlePictures;
        }

        public void setUserArticlePictures(ArrayList<UserArticlePictures> userArticlePictures) {
            this.userArticlePictures = userArticlePictures;
        }

        public String getPictureNum() {
            return pictureNum;
        }

        public void setPictureNum(String pictureNum) {
            this.pictureNum = pictureNum;
        }

        private String pictureNum;

    }

    public class User {
        private String id;
        private String user_nicename;
        private String username;
        private String avatar;
        private String status;
        private String uname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public String getBackgroundImage() {
            return backgroundImage;
        }

        public void setBackgroundImage(String backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        private String backgroundImage;
    }

    public class UserArticlePictures {
        private String id;
        private String userArticleId;
        private String path;
        private String picture_url;
        private String percent;
        private String width;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserArticleId() {
            return userArticleId;
        }

        public void setUserArticleId(String userArticleId) {
            this.userArticleId = userArticleId;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPicture_url() {
            return picture_url;
        }

        public void setPicture_url(String picture_url) {
            this.picture_url = picture_url;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        private String height;
    }
}
