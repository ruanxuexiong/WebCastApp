package com.android.nana.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.nana.R;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by lenovo on 2018/12/10.
 */

public class VideoMessageItemFragment extends DialogFragment {

    private LinearLayout mDelLl;
    private int msgId;

    public static VideoMessageItemFragment newInstance(int msgId) {
        VideoMessageItemFragment fragment = new VideoMessageItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("msgId", msgId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgId = getArguments().getInt("msgId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_dialog, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDelLl = view.findViewById(R.id.ll_del);
        mDelLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().deleteMessages(new int[]{msgId}, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            VideoMessageItemFragment.this.dismiss();
                            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        });
    }
}
