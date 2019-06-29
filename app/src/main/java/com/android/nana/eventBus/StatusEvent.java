package com.android.nana.eventBus;

/**
 * Created by lenovo on 2017/10/12.
 * 是否为认证用户
 */

public class StatusEvent {

    public String status;

    public StatusEvent(String status) {
        this.status = status;
    }
}
