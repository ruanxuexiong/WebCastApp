package com.android.nana.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.eventBus.WelcomeEvent;
import com.android.nana.main.MainActivity;
import com.android.nana.model.LoginModel;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CountdownButton;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lenovo on 2017/8/3.
 */

public class LoginCodeActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private TextView mLoginPwdTv, mNotCodeTv;
    private ImageButton mBackBtn;
    private StateButton mLoginBtn;
    private CountdownButton mSendBtn;
    private EditText mPhoneEt, mCodeEt;
    private AlertView mAlertView;
    private boolean isCode = false;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_code_login);
    }

    @Override
    protected void findViewById() {
        mLoginPwdTv = findViewById(R.id.pwd_login_tv);
        mNotCodeTv = findViewById(R.id.not_code_tv);
        mBackBtn = findViewById(R.id.common_btn_back);
        mLoginBtn = findViewById(R.id.btn_login);
        mPhoneEt = findViewById(R.id.et_phone);
        mCodeEt = findViewById(R.id.et_code);
        mSendBtn = findViewById(R.id.register_btn_send);
    }

    @Override
    protected void init() {

        if (null != SharedPreferencesUtils.getParameter(LoginCodeActivity.this, "mobile", "") && !"".equals(SharedPreferencesUtils.getParameter(LoginCodeActivity.this, "mobile", ""))) {
            String mobile = (String) SharedPreferencesUtils.getParameter(LoginCodeActivity.this, "mobile", "");
            mPhoneEt.setText(mobile);
            mPhoneEt.requestFocus();//有内容光标显示在后面
        }
    }

    @Override
    protected void setListener() {
        mLoginPwdTv.setOnClickListener(this);
        mNotCodeTv.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.register_btn_send:
                sendCode();
                break;
            case R.id.pwd_login_tv:
                startActivity(new Intent(LoginCodeActivity.this, LoginActivity.class));
                LoginCodeActivity.this.finish();
                break;
            case R.id.not_code_tv:
                showDialogs();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }


    public void sendCode() {//获取验证码
        String phone = mPhoneEt.getText().toString().trim();
        if ("".equals(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (phone.length() == 11) {
            LoginDbHelper.sendLoginCode(phone, new IOAuthCallBack() {
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
                            mSendBtn.start();
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


    private void login() {

        final String phone = mPhoneEt.getText().toString();
        final String code = mCodeEt.getText().toString();

        if (phone.equals("")) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (code.equals("")) {
            ToastUtils.showToast("验证码不能为空");
            return;
        }
        final String registrationId = JPushInterface.getRegistrationID(LoginCodeActivity.this);//极光id
        String time = getTime();
        String appSignature = MD5.MD5Hash(time + "&" + phone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

        LoginDbHelper.doLoginCode(phone, code, registrationId, time, appSignature, new IOAuthCallBack() {
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
                        LoginModel.doLogin(LoginCodeActivity.this, successJson,"");
                        EventBus.getDefault().post(new WelcomeEvent());//关闭欢迎页
                        startActivity(new Intent(LoginCodeActivity.this, MainActivity.class));
                        LoginCodeActivity.this.finish();
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

    private void showDialogs() {
        isCode = true;
        mAlertView = new AlertView("提示", "验证码有效期为3分钟，请您耐心等待", "继续等待", new String[]{"联系客服"}, null, this, AlertView.Style.Alert, this).setCancelable(true);
        mAlertView.show();
    }

    private void showCustomer() {
        isCode = false;
        DialogHelper.loginAlert(LoginCodeActivity.this, "呼叫", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                call("400-616-5126");
            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

            }
        });
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0 && isCode) {
            mAlertView.dismiss();
            showCustomer();
        }
    }

    private void call(String phone) {//调用拨号界面


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
}
