package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/11/17.
 * 朋友圈推送
 */

public class FriendsEvent {

    public String count;
    public String avatar;

    public FriendsEvent(String count, String avatar) {
        this.count = count;
        this.avatar = avatar;
    }
}
