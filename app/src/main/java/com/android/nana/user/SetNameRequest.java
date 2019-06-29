package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class SetNameRequest {

    private String nickname;

    public SetNameRequest(String nickname) {
        this.nickname = nickname;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
