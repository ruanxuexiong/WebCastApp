package com.android.nana.eventBus;

import android.view.View;

import com.android.nana.user.VoiceMessageItemProvider;

import io.rong.imkit.model.UIMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Qin on 2018/9/20.
 */

public class SendMsgEvent {

    public View view;
    public VoiceMessage voiceMessage;
    public UIMessage uiMessage;
    public VoiceMessageItemProvider.ViewHolder viewHolder;

    public SendMsgEvent(View view,VoiceMessage voiceMessage, UIMessage uiMessage,VoiceMessageItemProvider.ViewHolder viewHolder){
        this.view = view;
        this.voiceMessage = voiceMessage;
        this.uiMessage = uiMessage;
        this.viewHolder = viewHolder;
    }
}
