package com.android.nana.eventBus;

import java.util.ArrayList;

import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/6/20.
 */

public class MessageArrayEvent {

    public ArrayList<Message> messages;

    public MessageArrayEvent(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
