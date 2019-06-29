package com.android.nana.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.eventBus.WelcomeEvent;
import com.android.nana.main.MainActivity;
import com.android.nana.model.LoginModel;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.OverrideEditText;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by THINK on 2017/7/18.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private OverrideEditText mAccoutTv, mPwdTv;
    private ImageButton mBack;
    private TextView mForgetTv, mLoginTv;
    private StateButton mLoginBut;
    //用来判断显示明文或者密码（同时修改显示的图片）
    private boolean isHidden = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void findViewById() {
        mBack = findViewById(R.id.common_btn_back);
        mPwdTv = findViewById(R.id.login_pwd);
        mForgetTv = findViewById(R.id.forget_tv);
        mLoginBut = findViewById(R.id.btn_login);
        mAccoutTv = findViewById(R.id.login_account);
        mLoginTv = findViewById(R.id.tv_code_login);
    }

    @Override
    protected void init() {
        if (null != SharedPreferencesUtils.getParameter(LoginActivity.this, "mobile", "") && !"".equals(SharedPreferencesUtils.getParameter(LoginActivity.this, "mobile", ""))) {
            String mobile = (String) SharedPreferencesUtils.getParameter(LoginActivity.this, "mobile", "");
            mAccoutTv.setText(mobile);
            mAccoutTv.requestFocus();//有内容光标显示在后面
        }
    }

    @Override
    protected void setListener() {
        mForgetTv.setOnClickListener(this);
        mLoginBut.setOnClickListener(this);
        mLoginTv.setOnClickListener(this);
        mBack.setOnClickListener(this);

        mAccoutTv.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                mAccoutTv.setText("");
            }
        });

        mPwdTv.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                if (isHidden) {
                    //设置EditText文本为可见的
                    mPwdTv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPwdTv.setRightDrawable(getResources().getDrawable(R.drawable.show_new_selected));
                } else {
                    //设置EditText文本为隐藏的
                    mPwdTv.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPwdTv.setRightDrawable(getResources().getDrawable(R.drawable.hide_normal));
                }
                isHidden = !isHidden;
                mPwdTv.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = mPwdTv.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                LoginActivity.this.finish();
                break;
            case R.id.forget_tv:
                startActivity(new Intent(LoginActivity.this, RetrievalPwdActivity.class));
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_code_login://短信登录
                finish();
                break;
        }
    }

    private void login() {

        final String account = mAccoutTv.getText().toString();
        final String pass = mPwdTv.getText().toString();
        if (account.equals("")) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }
        if (pass.equals("")) {
            ToastUtils.showToast("密码不能为空");
            return;
        }
        final String registrationId = JPushInterface.getRegistrationID(LoginActivity.this);
        String time = getTime();
        String appSignature = MD5.MD5Hash(time + "&" + account + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");
        LoginDbHelper.doLogin(registrationId, account, MD5.MD5Hash("Ui8c7UTCMKbQDD2vuB" + pass), time, appSignature, new IOAuthCallBack() {
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
                        dismissProgressDialog();
                        LoginModel.doLogin(LoginActivity.this, successJson, pass);
                        LoginActivity.this.finish();
                        EventBus.getDefault().post(new WelcomeEvent());//关闭欢迎页
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    } else {
                        dismissProgressDialog();
                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }

}
