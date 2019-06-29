package com.android.nana.login;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.builder.PhoneValidateCodeBuilder;
import com.android.nana.customer.UpdatePassActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.model.LoginModel;
import com.android.nana.util.Constant;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class ForgetPassActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;

    private EditText mEtPhone, mEtPass, mEtVerificationCode;
    private TextView mTxtGetCode, mTxtForget;
    private LinearLayout mLlNewPass;

    private PhoneValidateCodeBuilder mValidateCodeBuilder;

    private String mType;
    private int mIsUpdatePassword;
    private UserInfo mUserInfo;

    @Override
    protected void bindViews() {
        mIsUpdatePassword = getIntent().getIntExtra("IsUpdatePassword", 1);
        setContentView(R.layout.forget_pass);
    }

    @Override
    protected void findViewById() {

        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);

        mEtPhone = findViewById(R.id.forget_pass_edit_phone);
        mEtPass = findViewById(R.id.forget_pass_edit_pass);
        mEtVerificationCode = findViewById(R.id.forget_pass_edit_verification_code);

        mTxtGetCode = findViewById(R.id.forget_pass_txt_get_code);
        mTxtForget = findViewById(R.id.forget_pass_txt_update);

        mLlNewPass = findViewById(R.id.forget_pass_ll_newpass);

        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(ForgetPassActivity.this, "userInfo", UserInfo.class);
        mEtPhone.setText(mUserInfo.getMobile());
    }

    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        if (mIsUpdatePassword == Constant.UpdatePassword.UpdateLoginPass) {
            mTxtTitle.setText("找回登录密码");
            mType = "FORGET_PASSWORD";
        } else if (mIsUpdatePassword == Constant.UpdatePassword.UpdateWithdrawPass) {
            mTxtTitle.setText("找回提现密码");
            mType = "OTHER";
        } else if (mIsUpdatePassword == Constant.UpdatePassword.SetUpWithdrawPass) {
            mTxtTitle.setText("设置提现密码");
            mType = "OTHER";
        } else if (mIsUpdatePassword == Constant.UpdatePassword.BindPhoneNumber) {
            mTxtTitle.setText("绑定手机号");
            mType = "OTHER";
            mLlNewPass.setVisibility(View.GONE);
            mTxtForget.setText("绑定");
        }

        mValidateCodeBuilder = new PhoneValidateCodeBuilder(ForgetPassActivity.this, mEtPhone, mTxtGetCode);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassActivity.this.finish();
            }
        });

        mTxtGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // if (Utils.isMobileExact(mEtPhone.getText().toString())) {
                mValidateCodeBuilder.verificationFormat("手机号不能为空", "手机号格式不正确");
                mValidateCodeBuilder.countDown(59);
                mValidateCodeBuilder.getCardByPhone(mType, new PhoneValidateCodeBuilder.PhoneValidateCodeListener() {

                    @Override
                    public void success(String result) {

                        ResultRequestModel mResultDetailModel = new ResultRequestModel(result);
                        UIHelper.showToast(ForgetPassActivity.this, mResultDetailModel.mMessage);
                        SharedPreferencesUtils.setParameter(ForgetPassActivity.this, "payPassword", "payPassword");
                    }

                    @Override
                    public void failue(String failue) {

                        UIHelper.showToast(ForgetPassActivity.this, failue);
                    }
                });
            }
        });

        mTxtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mEtPhone.getText().toString().trim();
                String verificationCode = mEtVerificationCode.getText().toString().trim();
                String pass = mEtPass.getText().toString().trim();

                if (mIsUpdatePassword == Constant.UpdatePassword.UpdateLoginPass) {
                    LoginDbHelper.resetPassword(phone, pass, verificationCode, mIOAuthCallBack);
                } else if (mIsUpdatePassword == Constant.UpdatePassword.UpdateWithdrawPass) {//找回提现密码

                    String time = getTime();
                    String appSignature = MD5.MD5Hash(time + "&" + phone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

                    MyIncomeHelper.doPayPassword(phone, verificationCode, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass), time, appSignature, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                            UIHelper.showOnLoadingDialog(ForgetPassActivity.this);
                        }

                        @Override
                        public void getSuccess(String successJson) {

                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            if (mResultDetailModel.mIsSuccess) {
                                //修改密码
                                SharedPreferencesUtils.setParameter(ForgetPassActivity.this, "payPassword", "updatePassword");
                                ForgetPassActivity.this.finish();
                                LoginModel.deLogin(ForgetPassActivity.this);
                            }
                            UIHelper.showToast(ForgetPassActivity.this, mResultDetailModel.mMessage);
                            UIHelper.hideOnLoadingDialog();
                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });


                } else if (mIsUpdatePassword == Constant.UpdatePassword.SetUpWithdrawPass) {//设置提现密码
                    String time = getTime();
                    String appSignature = MD5.MD5Hash(time + "&" + phone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

                    MyIncomeHelper.doPayPassword(phone, verificationCode, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass), time, appSignature, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                            UIHelper.showOnLoadingDialog(ForgetPassActivity.this);
                        }

                        @Override
                        public void getSuccess(String successJson) {

                            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                            if (mResultDetailModel.mIsSuccess) {
                                ForgetPassActivity.this.finish();
                                LoginModel.deLogin(ForgetPassActivity.this);
                                SharedPreferencesUtils.setParameter(ForgetPassActivity.this, "payPassword", "updatePassword");
                            }
                            UIHelper.showToast(ForgetPassActivity.this, mResultDetailModel.mMessage);
                            UIHelper.hideOnLoadingDialog();
                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });


                } else if (mIsUpdatePassword == Constant.UpdatePassword.BindPhoneNumber) {
                    CustomerDbHelper.changeMobile(BaseApplication.getInstance().getCustomerId(ForgetPassActivity.this),
                            phone, verificationCode, mIOAuthCallBack);
                }

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(ForgetPassActivity.this);
        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
            if (mResultDetailModel.mIsSuccess) {
                ForgetPassActivity.this.finish();

                LoginModel.deLogin(ForgetPassActivity.this);

            }
            UIHelper.showToast(ForgetPassActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();

        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(ForgetPassActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();
        }
    };

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }
}
