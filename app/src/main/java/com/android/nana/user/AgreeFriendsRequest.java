package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class AgreeFriendsRequest {

    private String friendId;

    public AgreeFriendsRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
