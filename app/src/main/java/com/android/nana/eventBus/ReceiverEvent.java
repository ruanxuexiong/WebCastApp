package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/10/20.
 */

public class ReceiverEvent {

    public String msg;
    public String title;

    public ReceiverEvent(String msg, String title) {
        this.msg = msg;
        this.title = title;
    }
}
