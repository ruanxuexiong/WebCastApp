package com.android.nana.friend;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.base.BaseActivity;
import com.android.nana.eventBus.RemarksEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/12/11.
 */

public class RemarksAddressActivity extends BaseActivity {
    private TextView mTitleTv, mRightTv, mBackTv;
    private EditText mRemarksEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remarks_address);
    }

    @Override
    public void initView() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mRemarksEt = findViewById(R.id.et_remarks);

        mTitleTv.setText("备注地址");
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);

        showSoftInputFromWindow(this,mRemarksEt);
    }

    @Override
    public void bindEvent() {
        mRightTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_2:
                preservation();
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    private void preservation() {
        String address = mRemarksEt.getText().toString().trim();
        if (TextUtils.isEmpty(address)){
            ToastUtils.showToast("请输入备注地址");
            return;
        }
        EventBus.getDefault().post(new RemarksEvent(address));
        RemarksAddressActivity.this.finish();
    }

    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
