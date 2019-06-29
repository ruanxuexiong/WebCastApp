package com.android.nana.eventBus;


import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/5/22.
 */

public class UpdateWantMessageEvent {
    public Message message;

    public UpdateWantMessageEvent(Message message) {
        this.message = message;
    }
}
