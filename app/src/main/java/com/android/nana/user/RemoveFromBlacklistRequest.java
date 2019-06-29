package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/14.
 */

public class RemoveFromBlacklistRequest {


    private String friendId;

    public RemoveFromBlacklistRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
