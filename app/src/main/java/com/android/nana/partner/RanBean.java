package com.android.nana.partner;

import java.util.List;

/**
 * Created by THINK on 2017/7/8.
 */

public class RanBean {


    /**
     * id : 16715
     * recUid : 7100
     * inviteUid : 7209
     * time : 1554087178
     * balance : 178.180
     * No : 1
     * user_nicename :
     * username : 康宣鹏test
     * avatar : http://zbnana.com/data/upload/avatar/5c9def2cef4cf.jpg
     * tow_level_list : [{"avatar":"http://zbnana.com/data/upload/avatar/5c9e10c6bdc27.jpg"},{"avatar":"http://zbnana.com/data/upload/avatar/5c9e3a6dc9f17.jpg"},{"avatar":"http://zbnana.com/data/upload/avatar/5c9c97a16d8fa.jpg"}]
     * tow_level_total : 3
     * tow_level_money : 21.551
     */

    private String id;
    private String recUid;
    private String inviteUid;
    private String time;
    private String balance;
    private int No;
    private String user_nicename;
    private String username;
    private String avatar;
    private int tow_level_total;
    private String tow_level_money;
    private List<TowLevelListBean> tow_level_list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecUid() {
        return recUid;
    }

    public void setRecUid(String recUid) {
        this.recUid = recUid;
    }

    public String getInviteUid() {
        return inviteUid;
    }

    public void setInviteUid(String inviteUid) {
        this.inviteUid = inviteUid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int No) {
        this.No = No;
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

    public int getTow_level_total() {
        return tow_level_total;
    }

    public void setTow_level_total(int tow_level_total) {
        this.tow_level_total = tow_level_total;
    }

    public String getTow_level_money() {
        return tow_level_money;
    }

    public void setTow_level_money(String tow_level_money) {
        this.tow_level_money = tow_level_money;
    }

    public List<TowLevelListBean> getTow_level_list() {
        return tow_level_list;
    }

    public void setTow_level_list(List<TowLevelListBean> tow_level_list) {
        this.tow_level_list = tow_level_list;
    }

    public static class TowLevelListBean {
        /**
         * avatar : http://zbnana.com/data/upload/avatar/5c9e10c6bdc27.jpg
         */

        private String avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
