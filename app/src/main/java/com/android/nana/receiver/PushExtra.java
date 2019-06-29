package com.android.nana.receiver;

/**
 * Created by lenovo on 2017/11/11.
 */

public class PushExtra {
    private String count;
    private String avatar;
    private String type;
    private String status;
    private String uid;
    private String openVcNameKey;
    private String talkId;
    private String id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOpenVcNameKey() {
        return openVcNameKey;
    }

    public void setOpenVcNameKey(String openVcNameKey) {
        this.openVcNameKey = openVcNameKey;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
