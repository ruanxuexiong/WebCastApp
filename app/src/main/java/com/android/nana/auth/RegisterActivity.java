package com.android.nana.auth;

import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.eventBus.WelcomeEvent;
import com.android.nana.main.MainActivity;
import com.android.nana.model.LoginModel;
import com.android.nana.util.MD5;
import com.android.nana.util.ToastUtils;
import com.android.nana.webview.JumpWebViewActivity;
import com.android.nana.widget.CountdownButton;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by THINK on 2017/7/18.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private TextView mProtocolTv;
    private RadioButton mAgreeBtn;
    private StateButton mResetBtn;
    private boolean isChecked = true;
    private CountdownButton mCodtBtn;
    private ImageButton mBack;

    private EditText mPhoneEt, mCodeEt, mPwdEt, mNameEt;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void findViewById() {
        mAgreeBtn =  findViewById(R.id.radio_agree);
        mResetBtn =  findViewById(R.id.btn_reset);
        mProtocolTv =  findViewById(R.id.tv_protocol);
        mProtocolTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mNameEt =  findViewById(R.id.et_name);
        mPhoneEt =  findViewById(R.id.et_phone);
        mCodeEt =  findViewById(R.id.et_code);
        mPwdEt =  findViewById(R.id.et_pwd);

        mBack =  findViewById(R.id.common_btn_back);
        mCodtBtn =  findViewById(R.id.register_btn_send);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {
        mResetBtn.setOnClickListener(this);
        mCodtBtn.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mAgreeBtn.setOnClickListener(this);
        mProtocolTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_agree:
                if (isChecked) {
                    isChecked = false;
                    mAgreeBtn.setChecked(false);
                    mResetBtn.setTextColor(getResources().getColor(R.color.white));
                    mResetBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_dd));
                    mResetBtn.setClickable(false);
                } else {
                    isChecked = true;
                    mAgreeBtn.setChecked(true);
                    mResetBtn.setTextColor(getResources().getColor(R.color.white));
                    mResetBtn.setNormalBackgroundColor(getResources().getColor(R.color.right));
                    mResetBtn.setClickable(true);
                }
                break;
            case R.id.tv_protocol:
                startJumpActivity(JumpWebViewActivity.class, "用户协议", "5");
                break;
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.btn_reset:
                rest();
                break;
            case R.id.register_btn_send:
                sendCode();
                break;
        }
    }

    private void rest() {//注册

        String phone = mPhoneEt.getText().toString().trim();
        String code = mCodeEt.getText().toString().trim();
        final String pwd = mPwdEt.getText().toString().trim();
        final String name = mNameEt.getText().toString().trim();

        if (mPwdEt.getText().length() < 6 ) {
            ToastUtils.showToast("密码大于等于6位");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast("验证码不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast("密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("真实姓名不能为空");
            return;
        }

        String time = getTime();
        String appSignature = MD5.MD5Hash(time + "&" + phone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");
        LoginDbHelper.doRegister(phone, code, name, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pwd), time, appSignature, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中请稍后...");
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                LoginModel.doLogin(RegisterActivity.this, successJson, pwd);
                if (new ResultRequestModel(successJson).mIsSuccess) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            JSONObject user = new JSONObject(data.getString("user"));
                            EventBus.getDefault().post(new WelcomeEvent());//关闭欢迎页
                            LoginModel.doLoginThird(RegisterActivity.this, successJson, "");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("mUid", user.getString("id"));
                            startActivity(intent);
                            RegisterActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {
        Intent intent = new Intent(RegisterActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

    public void sendCode() {//获取验证码
        String phone = mPhoneEt.getText().toString().trim();
        if ("".equals(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (phone.length() == 11) {
            LoginDbHelper.sendMessage(phone, "REGISTER", new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                    showProgressDialog("", "正在加载...");
                }

                @Override
                public void getSuccess(String successJson) {
                    dismissProgressDialog();
                    try {
                        JSONObject jsonobject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonobject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ToastUtils.showToast(result.getString("description"));
                            mCodtBtn.start();
                        } else {
                            ToastUtils.showToast(result.getString("description"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void getFailue(String failueJson) {
                    ToastUtils.showToast("获取验证码失败，请稍后重试");
                    dismissProgressDialog();
                }
            });
        } else {
            ToastUtils.showToast("请检查手机号码是否正确！");
        }
    }

    public String getTime() {

        long time = System.currentTimeMillis() / 1000;

        String str = String.valueOf(time);

        return str;

    }
}
