package com.android.nana.red;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.SettingRedUrlEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2019/1/2.
 */

public class SettingRedUrlActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv, mRightTv, mBackTv;
    private EditText mUrlEt;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_setting_red_url);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mUrlEt = findViewById(R.id.et_url);

        mTitleTv.setText("添加链接");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);

        showSoftInputFromWindow(this,mUrlEt);
    }

    private void showSoftInputFromWindow(SettingRedUrlActivity settingRedUrlActivity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        settingRedUrlActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void init() {
        mRightTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void setListener() {
        mRightTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_2:
                String mUrlStr = mUrlEt.getText().toString().trim();
                if (TextUtils.isEmpty(mUrlStr)) {
                    ToastUtils.showToast("请输入地址");
                    return;
                }
                EventBus.getDefault().post(new SettingRedUrlEvent(mUrlStr));
                this.finish();
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
