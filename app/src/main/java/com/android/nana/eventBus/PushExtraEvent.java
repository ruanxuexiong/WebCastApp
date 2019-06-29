package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/11/11.
 */

public class PushExtraEvent {

    public String count;
    public String avatar;

    public PushExtraEvent(String count, String avatar) {
        this.count = count;
        this.avatar = avatar;
    }
}
