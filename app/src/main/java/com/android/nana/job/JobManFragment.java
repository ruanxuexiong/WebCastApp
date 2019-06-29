package com.android.nana.job;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;
import com.android.nana.job.meresume.MeResumeActivity;

/**
 * Created by lenovo on 2018/3/7.
 */

public class JobManFragment extends BaseRequestFragment implements View.OnClickListener {


    private RelativeLayout mRecruitRl;
    private RelativeLayout mIntentionRl;
    private RelativeLayout mFollowRl;

    private LinearLayout mMsgNubll;
    private LinearLayout mInterestNumll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_job;
    }

    @Override
    protected void findViewById() {
        mRecruitRl = (RelativeLayout) findViewById(R.id.rl_recruit);
        mIntentionRl = (RelativeLayout) findViewById(R.id.rl_intention);
        mFollowRl = (RelativeLayout) findViewById(R.id.rl_follow);

        mMsgNubll = (LinearLayout) findViewById(R.id.ll_msg_num);
        mInterestNumll = (LinearLayout) findViewById(R.id.ll_interest_num);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {
        mRecruitRl.setOnClickListener(this);
        mIntentionRl.setOnClickListener(this);
        mFollowRl.setOnClickListener(this);

        mMsgNubll.setOnClickListener(this);
        mInterestNumll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_recruit:
                startActivity(new Intent(getActivity(), MeResumeActivity.class));
                break;
            case R.id.rl_intention:
                break;
            case R.id.rl_follow:
                break;
            case R.id.ll_msg_num:
                break;
            case R.id.ll_interest_num:
                break;
            default:
                break;
        }
    }
}
