package com.android.nana.find.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/9/26.
 */

public class Moment {

    private Mine mine;
    private ArrayList<Moments> moments;

    public ArrayList<Moments> getMoments() {
        return moments;
    }

    public void setMoments(ArrayList<Moments> moments) {
        this.moments = moments;
    }

    public Mine getMine() {
        return mine;
    }

    public void setMine(Mine mine) {
        this.mine = mine;
    }

    public class Mine {
        private String id;
        private String username;
        private String avatar;
        private String status;
        private String backgroundImage;

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
        private String lat;
        private String lng;
        private String address;
        private String showBound;

        public String getSpread_url() {
            return spread_url;
        }

        public void setSpread_url(String spread_url) {
            this.spread_url = spread_url;
        }

        private String spread_url;

        public String getSpread_url_type() {
            return spread_url_type;
        }

        public void setSpread_url_type(String spread_url_type) {
            this.spread_url_type = spread_url_type;
        }

        private String spread_url_type;
        private ArrayList<Comments> comments;


        public ArrayList<Comments> getComments() {
            return comments;
        }

        public void setComments(ArrayList<Comments> comments) {
            this.comments = comments;
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
        private String juli;
        private User user;
        private Mine mine;
        private String time;
        private String type;
        private ArrayList<Pictures> userArticlePictures;
        private String pictureNum;
        private String laudCount;
        private String laudResult;
        private String collectioned;
        private String shareUrl;
        private String shareLogo;
        private String shareTitle;
        private List<Article> tag_list;
        private List<UserListBean> user_list;

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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getJuli() {
            return juli;
        }

        public void setJuli(String juli) {
            this.juli = juli;
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

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Mine getMine() {
            return mine;
        }

        public void setMine(Mine mine) {
            this.mine = mine;
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

        public ArrayList<Pictures> getUserArticlePictures() {
            return userArticlePictures;
        }

        public void setUserArticlePictures(ArrayList<Pictures> userArticlePictures) {
            this.userArticlePictures = userArticlePictures;
        }

        public String getPictureNum() {
            return pictureNum;
        }

        public void setPictureNum(String pictureNum) {
            this.pictureNum = pictureNum;
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

        private String shareDesc;
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

    public static class UserListBean {
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

    public class Comments {
        private String uid;
        private String content;
        private String to_uid;
        private String ctime;

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

        public CommentsUser getUser() {
            return user;
        }

        public void setUser(CommentsUser user) {
            this.user = user;
        }

        private CommentsUser user;
    }

    public class CommentsUser {
        private String id;
        private String avatar;

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

        private String username;
    }
}
