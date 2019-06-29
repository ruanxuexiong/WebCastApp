package com.android.nana.eventBus;

import com.android.nana.user.VideoMessage;
import com.android.nana.user.VideoMessageItemProvider;

import io.rong.imkit.model.UIMessage;

/**
 * Created by lenovo on 2018/11/27.
 */

public class SendVideoMsgEvent {

    public VideoMessageItemProvider.ViewHolder viewHolder;
    public VideoMessage videoMessage;
    public UIMessage uiMessage;
    public SendVideoMsgEvent(VideoMessageItemProvider.ViewHolder viewHolder,VideoMessage videoMessage,UIMessage uiMessage){
        this.viewHolder = viewHolder;
        this.videoMessage = videoMessage;
        this.uiMessage = uiMessage;
    }

}
