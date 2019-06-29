package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class UserInfoBean {

    private String id;
    private String name;
    private String portraitUri;

    public UserInfoBean() {
    }

    public UserInfoBean(String id) {
        this.id = id;
    }

    public UserInfoBean(String id, String name, String portraitUri) {
        this.id = id;
        this.name = name;
        this.portraitUri = portraitUri;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String uri) {
        this.portraitUri = uri;
    }
}
