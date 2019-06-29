package com.android.nana.customer;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class UpdatePassActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;

    private EditText mEtOldPass, mEtmNewpass, mEtRePass;
    private TextView mTxtUpdate;
    private UserInfo mUserInfo;
    private String mUserPass;
    private String mNewpass;
    private String mPhone;

    private boolean mIsUpdatePayPassword;
    private String md5Str = "Ui8c7UTCMKbQDD2vuB";

    @Override
    protected void bindViews() {
        mIsUpdatePayPassword = getIntent().getBooleanExtra("IsUpdatePayPassword", false);
        setContentView(R.layout.update_pass);
    }

    @Override
    protected void findViewById() {
        if (null != getIntent().getStringExtra("phone")) {
            mPhone = getIntent().getStringExtra("phone");
        }
        mIbBack =  findViewById(R.id.iv_toolbar_back);
        mTxtTitle =  findViewById(R.id.tv_title);

        mEtOldPass =  findViewById(R.id.update_pass_edit_old_pass);
        mEtmNewpass =  findViewById(R.id.update_pass_edit_new_pass);
        mEtRePass =  findViewById(R.id.update_pass_edit_re_pass);

        mTxtUpdate = (TextView) findViewById(R.id.update_pass_txt_update);

    }

    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        if (mIsUpdatePayPassword) {
            mTxtTitle.setText("修改提现密码");
        } else {
            mTxtTitle.setText("修改密码");
        }

        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(this, "userInfo", UserInfo.class);
        mUserPass = (String) SharedPreferencesUtils.getParameter(this, "userPass", "");
    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassActivity.this.finish();
            }
        });

        mTxtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update();

            }
        });

    }

    private void update() {

        String oldpass = mEtOldPass.getText().toString().trim();
        if (TextUtils.isEmpty(oldpass)) {
            showToast("旧密码不能为空");
            return;
        }
        if (!mIsUpdatePayPassword && !mUserPass.equals(oldpass)) {
            showToast("旧密码不正确");
            return;
        }
        mNewpass = mEtmNewpass.getText().toString().trim();
        if (TextUtils.isEmpty(mNewpass)) {
            showToast("新密码不能为空");
            return;
        }
        if (mNewpass.length() < 6) {
            showToast("新密码不能少于6位");
            return;
        }
        final String repass = mEtRePass.getText().toString().trim();
        if (TextUtils.isEmpty(repass)) {
            showToast("确认密码不能为空");
            return;
        }
        if (!repass.equals(mNewpass)) {
            showToast("两次密码不一致");
            return;
        }

        if (mIsUpdatePayPassword) {//修改提现密码
            String time = getTime();
            String appSignature = MD5.MD5Hash(time + "&" + mUserInfo.getMobile() + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");
            MyIncomeHelper.updatePayPwd(mUserInfo.getId(), mUserInfo.getMobile(), MD5.MD5Hash(md5Str + oldpass), MD5.MD5Hash(md5Str + mNewpass), MD5.MD5Hash(md5Str + repass), time, appSignature, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                    showProgressDialog("", "加载中...");
                }

                @Override
                public void getSuccess(String successJson) {

                    dismissProgressDialog();
                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    if (mResultDetailModel.mIsSuccess) {
                        if (mIsUpdatePayPassword) {

                        } else {
                            SharedPreferencesUtils.setParameter(UpdatePassActivity.this, "userPass", mNewpass);
                        }
                        //设置提现密码提示
                        SharedPreferencesUtils.setParameter(UpdatePassActivity.this, "payPassword", "payPassword");
                        UpdatePassActivity.this.finish();
                    }

                    UIHelper.showToast(UpdatePassActivity.this, mResultDetailModel.mMessage);
                    UIHelper.hideOnLoadingDialog();
                }

                @Override
                public void getFailue(String failueJson) {
                    dismissProgressDialog();
                }
            });

        } else {
            String time = getTime();
            String appSignature = MD5.MD5Hash(time + "&" + mPhone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

            LoginDbHelper.updatePwd(mUserInfo.getId(), MD5.MD5Hash(md5Str + oldpass), MD5.MD5Hash(md5Str + mNewpass), MD5.MD5Hash(md5Str + repass), mPhone, time, appSignature, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                    showProgressDialog("", "加载中...");
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            if (mIsUpdatePayPassword) {

                            } else {
                                ToastUtils.showToast(result.getString("description"));
                                SharedPreferencesUtils.setParameter(UpdatePassActivity.this, "userPass", mNewpass);
                                UpdatePassActivity.this.finish();
                            }
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

    public String getTime() {

        long time = System.currentTimeMillis() / 1000;

        String str = String.valueOf(time);

        return str;

    }
}
