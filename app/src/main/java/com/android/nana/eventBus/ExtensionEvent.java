package com.android.nana.eventBus;

/**
 * Created by lenovo on 2019/1/16.
 */

public class ExtensionEvent {
    public String text;
    public String type;

    public ExtensionEvent(String text, String type) {
        this.text = text;
        this.type = type;
    }
}
