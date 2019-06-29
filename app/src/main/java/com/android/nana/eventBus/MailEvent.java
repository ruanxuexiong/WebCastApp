package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/9/4.
 */

public class MailEvent {

    public String count;
    public String avatar;

    public MailEvent(String count, String avatar) {
        this.count = count;
        this.avatar = avatar;
    }

    public MailEvent() {

    }
}
