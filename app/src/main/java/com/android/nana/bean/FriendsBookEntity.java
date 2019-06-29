package com.android.nana.bean;

import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by lenovo on 2017/9/11.
 */

public class FriendsBookEntity implements Comparable {

    private String id;
    private String avatar;
    private String user_nicename;
    private String username;
    private String status;
    private String uname;
    private String isFriend;//是否是好友
    private String friend;//是否好友
    public int top;
    public long time;

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }


    public boolean isChcked() {
        return chcked;
    }

    public void setChcked(boolean chcked) {
        this.chcked = chcked;
    }

    private boolean chcked;

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }


    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    private String sortLetters;

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

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public WorkHistorys getWorkHistorys() {
        return workHistorys;
    }

    public void setWorkHistorys(WorkHistorys workHistorys) {
        this.workHistorys = workHistorys;
    }

    private WorkHistorys workHistorys;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull Object another) {

        if (another == null || !(another instanceof FriendsBookEntity)) {
            return -1;
        }
        FriendsBookEntity otherSession = (FriendsBookEntity) another;
        /**置顶判断 ArrayAdapter是按照升序从上到下排序的，就是默认的自然排序
         * 如果是相等的情况下返回0，包括都置顶或者都不置顶，返回0的情况下要
         * 再做判断，拿它们置顶时间进行判断
         * 如果是不相等的情况下，otherSession是置顶的，则当前session是非置顶的，应该在otherSession下面，所以返回1
         *  同样，session是置顶的，则当前otherSession是非置顶的，应该在otherSession上面，所以返回-1
         * */
        int result = 0 - (top - otherSession.getTop());
        if (result == 0) {
            result = 0 - compareToTime(time, otherSession.getTime());
        }
        return result;
    }


    public static int compareToTime(long lhs, long rhs) {
        Calendar cLhs = Calendar.getInstance();
        Calendar cRhs = Calendar.getInstance();
        cLhs.setTimeInMillis(lhs);
        cRhs.setTimeInMillis(rhs);
        return cLhs.compareTo(cRhs);
    }


    public class WorkHistorys {
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
