package com.android.nana.material;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/1.
 */

public class AlbumBean {

    private String id;
    private String userId;
    private String title;
    private String content;
    private String addTime;
    private String username;
    private String avatar;
    private String time;
    private ArrayList<Pictures> userArticlePictures;
    private String collectioned;
    private String laudResult;

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

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Pictures> getUserArticlePictures() {
        return userArticlePictures;
    }

    public void setUserArticlePictures(ArrayList<Pictures> userArticlePictures) {
        this.userArticlePictures = userArticlePictures;
    }

    public String getCollectioned() {
        return collectioned;
    }

    public void setCollectioned(String collectioned) {
        this.collectioned = collectioned;
    }

    public String getLaudResult() {
        return laudResult;
    }

    public void setLaudResult(String laudResult) {
        this.laudResult = laudResult;
    }

    public String getLaudCount() {
        return laudCount;
    }

    public void setLaudCount(String laudCount) {
        this.laudCount = laudCount;
    }

    private String laudCount;

    public class Pictures {
        private String id;
        private String userArticleId;
        private String path;

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
    }
}
