package com.android.nana.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CountdownButton;
import com.android.nana.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/8/18.
 */

public class LoginPhoneActivity extends BaseActivity implements View.OnClickListener {


    private ImageButton mBack;
    private TextView mTitleTv;

    private EditText mPhoneET, mCodeET;
    private StateButton mResetBtn;
    private CountdownButton mCodtBtn;

    private String type;
    private String registrationId;
    private String grantString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("type")) {
            type = getIntent().getStringExtra("type");
            registrationId = getIntent().getStringExtra("registrationId");
            grantString = getIntent().getStringExtra("grantString");
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.btn_reset:
                binding();
                break;
            case R.id.register_btn_send:
                sendCode();
                break;
        }
    }

    private void binding() {//绑定手机号码
        String phone = mPhoneET.getText().toString().trim();
        String code = mCodeET.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast("验证码不能为空");
            return;
        }

        String time = getTime();
        String appSignature = MD5.MD5Hash(time + "&" + phone + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

        showProgressDialog("", "加载中...");

        LoginDbHelper.registerOauth(phone, code, type, registrationId, grantString, time, appSignature, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("1")) {
                        dismissProgressDialog();
                        ToastUtils.showToast(result.getString("description"));
                        LoginModel.doLoginThird(LoginPhoneActivity.this, successJson, "");
                        LoginPhoneActivity.this.finish();
                        EventBus.getDefault().post(new WelcomeEvent());//关闭欢迎页
                        startActivity(new Intent(LoginPhoneActivity.this, MainActivity.class));
                    } else if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        JSONObject data = new JSONObject(jsonobject.getString("data"));
                        JSONObject user = new JSONObject(data.getString("user"));
                        LoginModel.doLoginThird(LoginPhoneActivity.this, successJson, "");
                        Intent intent = new Intent(LoginPhoneActivity.this, MainActivity.class);
                      //  intent.putExtra("name", user.getString("username"));
                        intent.putExtra("mUid", user.getString("id"));

                        startActivity(intent);
                        EventBus.getDefault().post(new WelcomeEvent());//关闭欢迎页
                        LoginPhoneActivity.this.finish();
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

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_login_phone);
    }

    @Override
    protected void findViewById() {
        mBack = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);

        mPhoneET = findViewById(R.id.et_phone);
        mCodeET = findViewById(R.id.et_code);
        mResetBtn = findViewById(R.id.btn_reset);

        mCodtBtn = findViewById(R.id.register_btn_send);
    }

    private void sendCode() {//获取验证码
        String phone = mPhoneET.getText().toString().trim();
        if ("".equals(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (phone.length() == 11) {
            LoginDbHelper.sendMessage(phone, new IOAuthCallBack() {
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

    @Override
    protected void init() {
        mTitleTv.setText("绑定手机号码");
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);
        mCodtBtn.setOnClickListener(this);
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }
}
