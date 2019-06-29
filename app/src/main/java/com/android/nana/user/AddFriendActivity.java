package com.android.nana.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/9/11.
 */

public class AddFriendActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mRight2Tv;
    private RoundImageView mAvatar;
    private TextView mNameTv, mDescribeTv,mTitleTv;
    private ImageView mDentyIv;
    private EditText mMsgEt;
    private String mUid, mName, mInfo, mLogo, mThisId, mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_add_friend);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRight2Tv = findViewById(R.id.toolbar_right_2);

        mNameTv = findViewById(R.id.tv_name);
        mTitleTv = findViewById(R.id.tv_title);
        mAvatar = findViewById(R.id.iv_picture);
        mMsgEt = findViewById(R.id.et_msg);
        mDescribeTv = findViewById(R.id.tv_describe);
        mDentyIv = findViewById(R.id.iv_identy);
    }

    @Override
    protected void init() {

        mRight2Tv.setCompoundDrawables(null, null, null, null);
        mRight2Tv.setText("发送");
        mBackTv.setText("取消");
        mBackTv.setTextSize(16);
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);

        mBackTv.setTextColor(getResources().getColor(R.color.white));
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        mBackTv.setCompoundDrawables(null, null, null, null);

        if (null != getIntent().getStringExtra("uid")) {
            mUid = getIntent().getStringExtra("uid");
            mName = getIntent().getStringExtra("userName");
            mInfo = getIntent().getStringExtra("info");
            mLogo = getIntent().getStringExtra("logo");
            mStatus = getIntent().getStringExtra("status");
            mThisId = getIntent().getStringExtra("thisUserId");

            mTitleTv.setText(mName);
            mNameTv.setText(mName);
            if ("".equals(mInfo)){
                mDescribeTv.setText("暂无简介");
            }else {
                mDescribeTv.setText(mInfo);
            }

            if (null != mStatus && mStatus.equals("1")) {
                mDentyIv.setVisibility(View.VISIBLE);
            } else {
                mDentyIv.setVisibility(View.GONE);
            }
            ImgLoaderManager.getInstance().showImageView(mLogo, mAvatar);
        }else {
            mNameTv.setText("添加好友");
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                //发送好友
                sendMsg();
                break;
        }
    }

    private void sendMsg() {
        String msg = mMsgEt.getText().toString().trim();

        CustomerDbHelper.addFriend(mThisId, mUid, msg, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EventBus.getDefault().post(new MessageEvent(""));
                        AddFriendActivity.this.finish();
                        ToastUtils.showToast(result.getString("description"));
                    } else {
                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }
}
