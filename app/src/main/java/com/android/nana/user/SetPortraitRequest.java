package com.android.nana.user;

/**
 * Created by lenovo on 2017/9/8.
 */

public class SetPortraitRequest {

    private String portraitUri;


    public SetPortraitRequest(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }
}
