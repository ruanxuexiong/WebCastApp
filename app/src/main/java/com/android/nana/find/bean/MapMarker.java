package com.android.nana.find.bean;

/**
 * Created by lenovo on 2018/9/30.
 */

public class MapMarker {


    private String count;
    private String messageNum;

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    private Users users;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(String messageNum) {
        this.messageNum = messageNum;
    }

    private Articles articles;

    public Articles getArticles() {
        return articles;
    }

    public void setArticles(Articles articles) {
        this.articles = articles;
    }

    public class Articles {
        private String id;
        private String lat;
        private String lng;
        private String juli;
        private String noticeFirendMsg;

        public String getNoticeFirendMsg() {
            return noticeFirendMsg;
        }

        public void setNoticeFirendMsg(String noticeFirendMsg) {
            this.noticeFirendMsg = noticeFirendMsg;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getJuli() {
            return juli;
        }

        public void setJuli(String juli) {
            this.juli = juli;
        }
    }

    public class Users {
        private String _id;
        private String _location;
        private String _name;
        private String _address;
        private String uid;
        private String sex;
        private String userName;
        private String avatar;
        private String _createtime;
        private String _updatetime;
        private String _province;
        private String _city;
        private String _district;
        private String _distance;
        private String lat;
        private String show_avatar;
        private String inform_status;
        private String inform_msg;
        private String notice_msg;


        public String getInform_status() {
            return inform_status;
        }

        public void setInform_status(String inform_status) {
            this.inform_status = inform_status;
        }

        public String getInform_msg() {
            return inform_msg;
        }

        public void setInform_msg(String inform_msg) {
            this.inform_msg = inform_msg;
        }

        public String getNotice_msg() {
            return notice_msg;
        }

        public void setNotice_msg(String notice_msg) {
            this.notice_msg = notice_msg;
        }

        public String getShow_avatar() {
            return show_avatar;
        }

        public void setShow_avatar(String show_avatar) {
            this.show_avatar = show_avatar;
        }

        public String getShow_desc() {
            return show_desc;
        }

        public void setShow_desc(String show_desc) {
            this.show_desc = show_desc;
        }

        private String show_desc;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        private String lng;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String get_location() {
            return _location;
        }

        public void set_location(String _location) {
            this._location = _location;
        }

        public String get_name() {
            return _name;
        }

        public void set_name(String _name) {
            this._name = _name;
        }

        public String get_address() {
            return _address;
        }

        public void set_address(String _address) {
            this._address = _address;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String get_createtime() {
            return _createtime;
        }

        public void set_createtime(String _createtime) {
            this._createtime = _createtime;
        }

        public String get_updatetime() {
            return _updatetime;
        }

        public void set_updatetime(String _updatetime) {
            this._updatetime = _updatetime;
        }

        public String get_province() {
            return _province;
        }

        public void set_province(String _province) {
            this._province = _province;
        }

        public String get_city() {
            return _city;
        }

        public void set_city(String _city) {
            this._city = _city;
        }

        public String get_district() {
            return _district;
        }

        public void set_district(String _district) {
            this._district = _district;
        }

        public String get_distance() {
            return _distance;
        }

        public void set_distance(String _distance) {
            this._distance = _distance;
        }

    }

}
