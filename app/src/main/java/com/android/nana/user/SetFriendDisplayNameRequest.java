package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class SetFriendDisplayNameRequest {


    private String friendId;
    private String displayName;

    public SetFriendDisplayNameRequest(String friendId, String displayName) {
        this.friendId = friendId;
        this.displayName = displayName;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
