package com.android.nana.friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/10/30.
 */

public class AlbumDetailsEntity {

    private String id;
    private String userId;
    private String title;
    private String content;
    private String addTime;
    private String comment_count;
    private String repost_count;
    private User user;
    private String type;
    private String time;
    private ArrayList<Pictures> userArticlePictures;

    private List<Article> tag_list;
    private List<UserListBean> user_list;
    private String laudCount;
    private String laudResult;
    private String collectioned;
    private ArrayList<String> laudAvatar;
    private String shareUrl;
    private String shareLogo;
    private String shareTitle;
    private String shareDesc;

    public List<Article> getTag_list() {
        return tag_list;
    }

    public void setTag_list(List<Article> tag_list) {
        this.tag_list = tag_list;
    }

    public List<UserListBean> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<UserListBean> user_list) {
        this.user_list = user_list;
    }

    public String getSpread_url() {
        return spread_url;
    }

    public String getSpread_url_type() {
        return spread_url_type;
    }

    public void setSpread_url_type(String spread_url_type) {
        this.spread_url_type = spread_url_type;
    }

    private String spread_url_type;

    public void setSpread_url(String spread_url) {
        this.spread_url = spread_url;
    }

    private String spread_url;

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    private String view_count;
    private String lat;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
    private String juli;
    private String showBound;

    public String getJuli() {
        return juli;
    }

    public void setJuli(String juli) {
        this.juli = juli;
    }

    public String getShowBound() {
        return showBound;
    }

    public void setShowBound(String showBound) {
        this.showBound = showBound;
    }

    public String getBoundCount() {
        return boundCount;
    }

    public void setBoundCount(String boundCount) {
        this.boundCount = boundCount;
    }

    private String boundCount;

    public String getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(String isReceived) {
        this.isReceived = isReceived;
    }

    private String isReceived;
    private ArrayList<Comments> comments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareLogo() {
        return shareLogo;
    }

    public void setShareLogo(String shareLogo) {
        this.shareLogo = shareLogo;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDesc() {
        return shareDesc;
    }

    public void setShareDesc(String shareDesc) {
        this.shareDesc = shareDesc;
    }


    public String getLaudCount() {
        return laudCount;
    }

    public void setLaudCount(String laudCount) {
        this.laudCount = laudCount;
    }

    public String getLaudResult() {
        return laudResult;
    }

    public void setLaudResult(String laudResult) {
        this.laudResult = laudResult;
    }

    public String getCollectioned() {
        return collectioned;
    }

    public void setCollectioned(String collectioned) {
        this.collectioned = collectioned;
    }


    public ArrayList<Pictures> getUserArticlePictures() {
        return userArticlePictures;
    }

    public void setUserArticlePictures(ArrayList<Pictures> userArticlePictures) {
        this.userArticlePictures = userArticlePictures;
    }


    public ArrayList<String> getLaudAvatar() {
        return laudAvatar;
    }

    public void setLaudAvatar(ArrayList<String> laudAvatar) {
        this.laudAvatar = laudAvatar;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }


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


    public class User {
        private String id;
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
    public  class UserListBean {
        /**
         * touid :
         * uname :
         */

        private String touid;
        private String uname;

        public String getTouid() {
            return touid;
        }

        public void setTouid(String touid) {
            this.touid = touid;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }
    }

    public class Article {
        private String tag_id;
        private String tag_name;

        public String getTag_id() {
            return tag_id;
        }

        public void setTag_id(String tag_id) {
            this.tag_id = tag_id;
        }

        public String getTag_name() {
            return tag_name;
        }

        public void setTag_name(String tag_name) {
            this.tag_name = tag_name;
        }
    }
    public class Pictures {
        private String id;
        private String userArticleId;
        private String picture_url;
        private String path;
        private String width;

        public String getPicture_url() {
            return picture_url;
        }

        public void setPicture_url(String picture_url) {
            this.picture_url = picture_url;
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

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        private String percent;
    }


    public class Comments {//评论
        private String comment_id;
        private String articleId;
        private String app_uid;
        private String uid;
        private String content;
        private String to_comment_id;
        private String parentId;
        private String to_uid;
        private String ctime;
        private String is_del;
        private String isread;
        private String to_avatar;
        private String uname;
        private String status;
        private String avatar;
        private String date;
        private ArrayList<Child> child;

        public ArrayList<Child> getChild() {
            return child;
        }

        public void setChild(ArrayList<Child> child) {
            this.child = child;
        }

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public String getApp_uid() {
            return app_uid;
        }

        public void setApp_uid(String app_uid) {
            this.app_uid = app_uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTo_comment_id() {
            return to_comment_id;
        }

        public void setTo_comment_id(String to_comment_id) {
            this.to_comment_id = to_comment_id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getTo_uid() {
            return to_uid;
        }

        public void setTo_uid(String to_uid) {
            this.to_uid = to_uid;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getIs_del() {
            return is_del;
        }

        public void setIs_del(String is_del) {
            this.is_del = is_del;
        }

        public String getIsread() {
            return isread;
        }

        public void setIsread(String isread) {
            this.isread = isread;
        }

        public String getTo_avatar() {
            return to_avatar;
        }

        public void setTo_avatar(String to_avatar) {
            this.to_avatar = to_avatar;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

    public class Child {//二级评论
        private String comment_id;
        private String articleId;
        private String app_uid;
        private String uid;
        private String content;
        private String to_comment_id;
        private String parentId;
        private String to_uid;
        private String ctime;
        private String is_del;
        private String isread;
        private String to_avatar;
        private String uname;
        private String status;
        private String avatar;

        public String getTo_uname() {
            return to_uname;
        }

        public void setTo_uname(String to_uname) {
            this.to_uname = to_uname;
        }

        private String to_uname;

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public String getApp_uid() {
            return app_uid;
        }

        public void setApp_uid(String app_uid) {
            this.app_uid = app_uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTo_comment_id() {
            return to_comment_id;
        }

        public void setTo_comment_id(String to_comment_id) {
            this.to_comment_id = to_comment_id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getTo_uid() {
            return to_uid;
        }

        public void setTo_uid(String to_uid) {
            this.to_uid = to_uid;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getIs_del() {
            return is_del;
        }

        public void setIs_del(String is_del) {
            this.is_del = is_del;
        }

        public String getIsread() {
            return isread;
        }

        public void setIsread(String isread) {
            this.isread = isread;
        }

        public String getTo_avatar() {
            return to_avatar;
        }

        public void setTo_avatar(String to_avatar) {
            this.to_avatar = to_avatar;
        }

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        private String date;
    }


}
