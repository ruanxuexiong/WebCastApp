package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/23.
 */

public class JoinGroupRequest {

    private String groupId;

    public JoinGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
