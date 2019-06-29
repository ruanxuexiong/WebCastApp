package com.android.nana.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.nana.R;
import com.android.nana.eventBus.AudioPluginEvent;
import com.android.nana.eventBus.HindFragment;
import com.android.nana.eventBus.MoneyEvent;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;

/**
 * Created by Qin on 2017/9/8.
 */

public class ConversationFragmentEx extends ConversationFragment {

    private String uid;
    private boolean isGroup;
    private ImageView mVoiceToggleIv;
    private String mIsTanchang;
    private String mMoney;
    private String mEnough;

    public static ConversationFragmentEx newInstance(String uid, boolean isGroup, String mIsTanchang, String mMoney, String mEnough) {
        ConversationFragmentEx fragmentEx = new ConversationFragmentEx();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putBoolean("isGroup", isGroup);
        bundle.putString("mIsTanchang", mIsTanchang);
        bundle.putString("mMoney", mMoney);
        bundle.putString("mEnough", mEnough);
        fragmentEx.setArguments(bundle);
        return fragmentEx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("uid");
        isGroup = getArguments().getBoolean("isGroup");
        mIsTanchang = getArguments().getString("mIsTanchang");
        mMoney = getArguments().getString("mMoney");
        mEnough = getArguments().getString("mEnough");

        if (!EventBus.getDefault().isRegistered(ConversationFragmentEx.this)) {
            EventBus.getDefault().register(ConversationFragmentEx.this);
        }

    }

    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }



    @Override
    public void onSendToggleClick(View v, String text) {
        super.onSendToggleClick(v, text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //   mVoiceToggleIv = findViewById(getView(), R.id.rc_voice_toggle);
        // mVoiceToggleIv.setOnLongClickListener(new ButtonLongClick());
        //mVoiceToggleIv.setOnTouchListener(new ButtonLongClick());

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return super.onKey(v, keyCode, event);
    }

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return new CustomMessageListAdapter(context);
    }

    private class ButtonLongClick implements View.OnLongClickListener, View.OnTouchListener {

        @Override
        public boolean onLongClick(View v) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ChatFragment dialog = ChatFragment.newInstance(uid, "show", isGroup, mIsTanchang, mMoney, mEnough);
            dialog.show(fm, "dialog");
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.rc_voice_toggle) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    EventBus.getDefault().post(new HindFragment());
                }
            }
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMoney(MoneyEvent event) {
        mIsTanchang = event.isHide;
        mEnough = event.isMoney;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onAudioPlugin(AudioPluginEvent event) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChatFragment dialog = ChatFragment.newInstance(uid, isGroup, mIsTanchang, mMoney, mEnough);
        dialog.show(fm, "dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ConversationFragmentEx.this);
    }
}
