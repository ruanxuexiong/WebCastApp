package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class BlackList {


    /** Not-null value. */
    private String userId;
    private String status;
    private Long timestamp;

    public BlackList() {
    }

    public BlackList(String userId) {
        this.userId = userId;
    }

    public BlackList(String userId, String status, Long timestamp) {
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
