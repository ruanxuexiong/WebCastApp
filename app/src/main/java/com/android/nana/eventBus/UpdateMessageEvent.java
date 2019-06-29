package com.android.nana.eventBus;

import com.android.nana.material.WantBean;

import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/5/22.
 */

public class UpdateMessageEvent {

    public Message message;
    public WantBean wantBean;

    public UpdateMessageEvent(Message message,WantBean wantBean) {
        this.message = message;
    }
}
