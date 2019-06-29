package com.android.nana.customer.myincome;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.nana.R;
import com.android.nana.customer.UpdatePassActivity;
import com.android.nana.login.ForgetPassActivity;
import com.android.nana.util.Constant;
import com.android.nana.util.SharedPreferencesUtils;

/**
 * Created by lenovo on 2018/7/11.
 */

public class PaySetActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private LinearLayout mUpPwdLl;
    private LinearLayout mPayRetrieveLl;
    private LinearLayout mPaySetLl;
    private String mPayPassword;
    private LinearLayout mShowPayLl;
    private TextView mTitleTv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_pay_set);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mUpPwdLl = findViewById(R.id.ll_up_pwd);
        mPaySetLl = findViewById(R.id.ll_pay_set);
        mPayRetrieveLl = findViewById(R.id.ll_pay_retrieve);
        mShowPayLl = findViewById(R.id.ll_show_pay);
        mTitleTv = findViewById(R.id.tv_title);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mPayPassword = (String) SharedPreferencesUtils.getParameter(this, "payPassword", "");
        if (TextUtils.isEmpty(mPayPassword)) {
            mShowPayLl.setVisibility(View.GONE);
            mPaySetLl.setVisibility(View.VISIBLE);
        }
        mTitleTv.setText("提现设置");
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mUpPwdLl.setOnClickListener(this);
        mPaySetLl.setOnClickListener(this);
        mPayRetrieveLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_up_pwd:
                // 修改提现密码
                Intent intent = new Intent(PaySetActivity.this, UpdatePassActivity.class);
                intent.putExtra("IsUpdatePayPassword", true);
                startActivity(intent);
                break;
            case R.id.ll_pay_retrieve:
                // 找回提现密码
                Intent i = new Intent(PaySetActivity.this, ForgetPassActivity.class);
                i.putExtra("IsUpdatePassword", Constant.UpdatePassword.UpdateWithdrawPass);
                startActivity(i);
                break;
            case R.id.ll_pay_set:
                // 设置提现密码
                String mobile = (String) SharedPreferencesUtils.getParameter(PaySetActivity.this, "mobile", "");
                if (TextUtils.isEmpty(mobile)) {
                    DialogHelper.customAlert(PaySetActivity.this, "提示", "检测到您未绑定手机号，确定要前去绑定吗?", new DialogHelper.OnAlertConfirmClick() {
                        @Override
                        public void OnClick(String content) {

                        }

                        @Override
                        public void OnClick() {

                            Intent intent = new Intent(PaySetActivity.this, ForgetPassActivity.class);
                            intent.putExtra("IsUpdatePassword", Constant.UpdatePassword.BindPhoneNumber);
                            startActivity(intent);

                        }
                    }, null);
                    return;
                }
                Intent intent1 = new Intent(PaySetActivity.this, ForgetPassActivity.class);
                intent1.putExtra("IsUpdatePassword", Constant.UpdatePassword.SetUpWithdrawPass);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
