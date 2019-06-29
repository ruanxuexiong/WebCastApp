package com.android.nana.auth;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CountdownButton;
import com.android.nana.widget.StateButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by THINK on 2017/7/19.
 */

public class RetrievalPwdActivity extends BaseActivity implements View.OnClickListener {


    private ImageButton mBack;
    private TextView mTitleTv;

    private EditText mPhoneET, mPwdET, mCodeET;
    private CountdownButton mCodeBtn;
    private StateButton resetBtn;


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_retrieval);
    }

    @Override
    protected void findViewById() {
        mBack =  findViewById(R.id.common_btn_back);
        mTitleTv =  findViewById(R.id.common_txt_title);
        resetBtn =  findViewById(R.id.btn_reset);
        mCodeBtn =  findViewById(R.id.register_btn_send);

        mPhoneET =  findViewById(R.id.et_phone);
        mCodeET =  findViewById(R.id.et_code);
        mPwdET =  findViewById(R.id.et_pwd);
    }

    @Override
    protected void init() {
        mTitleTv.setText("找回密码");
    }

    @Override
    protected void setListener() {
        mBack.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        mCodeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.btn_reset:
                reset();
                break;
            case R.id.register_btn_send:
                sendCode();
                break;
        }
    }

    private void reset() {
        String phone = mPhoneET.getText().toString().trim();
        String pwd = mPwdET.getText().toString().trim();
        String code = mCodeET.getText().toString().trim();

        if (pwd.length() < 6) {
            ToastUtils.showToast("密码不能小于6位");
        }

        LoginDbHelper.resetPassword(phone, pwd, code, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中请稍后...");
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        RetrievalPwdActivity.this.finish();
                    } else {
                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("重置失败，请稍后重试!");
                dismissProgressDialog();
            }
        });
    }

    private void sendCode() {//获取验证码
        String phone = mPhoneET.getText().toString().trim();

        if ("".equals(phone)) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (phone.length() == 11) {

            LoginDbHelper.sendMessage(phone, "FORGET_PASSWORD", new IOAuthCallBack() {
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
                            mCodeBtn.start();
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
}
