package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class RegisterRequest {


    private String nickname;

    private String password;

    private String verification_token;

    public RegisterRequest(String nickname, String password, String verification_token) {
        this.nickname = nickname;
        this.password = password;
        this.verification_token = verification_token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerification_token() {
        return verification_token;
    }

    public void setVerification_token(String verification_token) {
        this.verification_token = verification_token;
    }
}
