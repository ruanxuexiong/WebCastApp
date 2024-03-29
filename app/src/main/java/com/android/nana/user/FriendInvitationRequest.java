package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class FriendInvitationRequest {

    private String friendId;
    private String message;

    public FriendInvitationRequest(String userid, String addFrinedMessage) {
        this.message = addFrinedMessage;
        this.friendId = userid;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
