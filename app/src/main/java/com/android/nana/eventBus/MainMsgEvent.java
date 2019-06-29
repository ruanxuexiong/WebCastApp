package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/8/15.
 */

public class MainMsgEvent {
    public final int msg;

    public MainMsgEvent(int msg) {
        this.msg = msg;
    }

    public MainMsgEvent(int num, int nub) {
        this.msg = num + nub;
    }
}
