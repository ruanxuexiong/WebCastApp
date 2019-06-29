package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class ContactNotificationMessageData {

    /**
     * sourceUserNickname : 赵哈哈
     * version : 1456282826213
     */

    private String sourceUserNickname;
    private long version;

    public void setSourceUserNickname(String sourceUserNickname) {
        this.sourceUserNickname = sourceUserNickname;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getSourceUserNickname() {
        return sourceUserNickname;
    }

    public long getVersion() {
        return version;
    }
}
