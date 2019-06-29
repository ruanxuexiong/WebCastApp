package com.android.nana.inquiry;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/4/29.
 */

public class EvaluateBean {


    private String zx_comment;
    private EvaluateBean.ZxUser zxUser;
    private ArrayList<String> options;
    private EvaluateNew evaluate_new;


    public String getZx_comment() {
        return zx_comment;
    }

    public void setZx_comment(String zx_comment) {
        this.zx_comment = zx_comment;
    }

    public ZxUser getZxUser() {
        return zxUser;
    }

    public void setZxUser(ZxUser zxUser) {
        this.zxUser = zxUser;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public EvaluateNew getEvaluate_new() {
        return evaluate_new;
    }

    public void setEvaluate_new(EvaluateNew evaluate_new) {
        this.evaluate_new = evaluate_new;
    }


    public class ZxUser {
        private String id;
        private String avatar;
        private String username;
        private String status;
        private String zx_time;

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

        public String getZx_time() {
            return zx_time;
        }

        public void setZx_time(String zx_time) {
            this.zx_time = zx_time;
        }

        public String getZx_money() {
            return zx_money;
        }

        public void setZx_money(String zx_money) {
            this.zx_money = zx_money;
        }

        private String zx_money;
    }

    public class EvaluateNew {
        private String id;
        private String directId;
        private String zxUid;
        private String stars;
        private String reviews;
        private String addTime;
        private String updateTime;
        private ArrayList<String> reviews_select;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDirectId() {
            return directId;
        }

        public void setDirectId(String directId) {
            this.directId = directId;
        }

        public String getZxUid() {
            return zxUid;
        }

        public void setZxUid(String zxUid) {
            this.zxUid = zxUid;
        }

        public String getStars() {
            return stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }

        public String getReviews() {
            return reviews;
        }

        public void setReviews(String reviews) {
            this.reviews = reviews;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public ArrayList<String> getReviews_select() {
            return reviews_select;
        }

        public void setReviews_select(ArrayList<String> reviews_select) {
            this.reviews_select = reviews_select;
        }


    }
}
