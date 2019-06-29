package com.android.nana.mail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.GroupFinisEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by lenovo on 2017/9/20.
 */

public class GroupNoticeActivity extends BaseActivity implements View.OnClickListener {


    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight2Tv;
    private EditText mAreaEt;

    private Conversation.ConversationType mConversationType;
    private String sGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mConversationType = Conversation.ConversationType.setValue(intent.getIntExtra("conversationType", 0));
        sGroupId = getIntent().getStringExtra("GroupId");
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_group_notice);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAreaEt = findViewById(R.id.edit_area);
    }

    @Override
    protected void init() {
        mTitleTv.setText("群公告");
        mRight2Tv.setText("确定");
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void setListener() {
        mRight2Tv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:

                if (TextUtils.isEmpty(mAreaEt.getText().toString())) {
                    ToastUtils.showToast("群公告不能为空");
                    return;
                }

                TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + mAreaEt.getText().toString());
                MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
                textMessage.setMentionedInfo(mentionedInfo);

                RongIM.getInstance().sendMessage(Message.obtain(sGroupId, mConversationType, textMessage), null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });

                finish();
                EventBus.getDefault().post(new GroupFinisEvent());
                break;
            default:
                break;
        }
    }
}
