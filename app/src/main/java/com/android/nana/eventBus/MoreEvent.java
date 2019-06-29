package com.android.nana.eventBus;

/**
 * Created by lenovo on 2018/1/2.
 */

public class MoreEvent {
    public String mOneId;//一级ID
    public String mTwoId;//二级ID
    public String mTitle;//标题
    public String mContent;//内容

    public MoreEvent(String mOneId, String mTwoId, String mContent,String mTitle) {
        this.mOneId = mOneId;
        this.mTwoId = mTwoId;
        this.mContent = mContent;
        this.mTitle = mTitle;
    }
}
