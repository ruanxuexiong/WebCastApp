package com.android.nana.login;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;
import com.android.nana.util.Constant;
import com.android.nana.util.ToastUtils;

import cn.jpush.android.api.JPushInterface;

public class LoginFragment extends BaseRequestFragment {

    private EditText mEtAccount, mEtPass;
    private TextView mTxtForget, mTxtLogin;

    @Override
    protected int getLayoutId() {

        return R.layout.login;
    }

    @Override
    protected void findViewById() {

        mEtAccount = (EditText) findViewById(R.id.login_edit_account);
        mEtPass = (EditText) findViewById(R.id.login_edit_pass);
        mTxtForget = (TextView) findViewById(R.id.login_txt_forget);
        mTxtLogin = (TextView) findViewById(R.id.login_txt_login);

    }

    @Override
    protected void init() {

        Log.e("支付宝====", Constant.Payment.AliPay.RsaPrivate);
    }

    @Override
    protected void setListener() {

        mTxtLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                login();

            }
        });

        mTxtForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(getActivity(), ForgetPassActivity.class);
                intent.putExtra("IsUpdatePassword", Constant.UpdatePassword.UpdateLoginPass);
                startActivity(intent);

            }
        });
    }

    private void login() {

        final String account = mEtAccount.getText().toString();
        final String pass = mEtPass.getText().toString();

        if (account.equals("")) {
            ToastUtils.showToast("手机号码不能为空");
            return;
        }

        if (pass.equals("")) {
            ToastUtils.showToast("密码不能为空");
            return;
        }

        String registrationId = JPushInterface.getRegistrationID(getActivity());

 /*       LoginDbHelper.login(account, pass, "MOBILE", registrationId, new IOAuthCallBack() {

            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(getActivity());
            }

            @Override
            public void getSuccess(String successJson) {
                SharedPreferencesUtils.setParameter(getActivity(), "isSuccess", "register");
                LoginModel.doLogin(getActivity(), successJson, pass);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                UIHelper.hideOnLoadingDialog();
            }

            @Override
            public void getFailue(String failueJson) {

                UIHelper.showToast(getActivity(), "登录失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();

            }
        });*/
    }

}
