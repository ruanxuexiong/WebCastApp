package com.android.nana.eventBus;

/**
 * Created by lenovo on 2018/7/30.
 * 添加公司名称
 */

public class WorkCompanyNameEvent {
    public final String name;

    public WorkCompanyNameEvent(String name) {
        this.name = name;
    }
}
