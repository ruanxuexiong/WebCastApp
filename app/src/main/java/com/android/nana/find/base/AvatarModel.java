package com.android.nana.find.base;

/**
 * Created by lenovo on 2018/11/1.
 */

public class AvatarModel {

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int iconRes;

    public AvatarModel(int iconRes) {
        this.iconRes = iconRes;
    }
}
