package com.android.nana.login;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.helper.UIHelper;
import com.android.nana.R;
import com.android.nana.builder.PhoneValidateCodeBuilder;
import com.android.nana.builder.PhoneValidateCodeBuilder.PhoneValidateCodeListener;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends BaseRequestFragment {

    private EditText mEtPhone, mEtPass, mEtVerificationCode, mRealnameET;
    private TextView mTxtGetCode, mTxtRegister;

    private PhoneValidateCodeBuilder mValidateCodeBuilder;

    @Override
    protected int getLayoutId() {

        return R.layout.register;
    }

    @Override
    protected void findViewById() {

        mEtPhone = (EditText) findViewById(R.id.register_edit_phone);
        mEtPass = (EditText) findViewById(R.id.register_edit_pass);
        mRealnameET = (EditText) findViewById(R.id.register_edit_nickname);
        mEtVerificationCode = (EditText) findViewById(R.id.register_edit_verification_code);

        mTxtGetCode = (TextView) findViewById(R.id.register_txt_get_code);
        mTxtRegister = (TextView) findViewById(R.id.register_txt_register);
    }

    @Override
    protected void init() {

        mValidateCodeBuilder = new PhoneValidateCodeBuilder(getActivity(), mEtPhone, mTxtGetCode);

    }

    @Override
    protected void setListener() {

        mTxtGetCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mValidateCodeBuilder.verificationFormat("手机号不能为空", "手机号格式不正确");

                mValidateCodeBuilder.getCardByPhone("REGISTER", new PhoneValidateCodeListener() {

                    @Override
                    public void success(String result) {

                        try {
                            JSONObject jsonobject = new JSONObject(result);
                            JSONObject jsonResult = new JSONObject(jsonobject.getString("result"));

                            if (jsonResult.getString("state").equals("0")) {
                                mValidateCodeBuilder.countDown(59);
                                UIHelper.showToast(getActivity(), jsonResult.getString("description"));
                            } else {
                                ToastUtils.showToast(jsonResult.getString("description"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failue(String failue) {

                        UIHelper.showToast(getActivity(), "获取验证码失败，请稍后重试");
                    }
                });
            }
        });

        mTxtRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String phone = mEtPhone.getText().toString().trim();
                String param = null;

                if (!"".equals(SharedPreferencesUtils.getParameter(getActivity(), "param", "")) && null != SharedPreferencesUtils.getParameter(getActivity(), "param", "")) {
                    param = (String) SharedPreferencesUtils.getParameter(getActivity(), "param", "");
                }


                if (TextUtils.isEmpty(phone)) {
                    UIHelper.showToast(getActivity(), "手机号不能为空");
                    return;
                }
                String verificationCode = mEtVerificationCode.getText().toString().trim();
                if (TextUtils.isEmpty(verificationCode)) {
                    UIHelper.showToast(getActivity(), "验证码不能为空");
                    return;
                }
                final String pass = mEtPass.getText().toString().trim();
                if (TextUtils.isEmpty(pass)) {
                    UIHelper.showToast(getActivity(), "密码不能为空");
                    return;
                }
                String name = mRealnameET.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast("真实姓名不能为空");
                    return;
                }

              /*  LoginDbHelper.register(phone, pass, verificationCode, name, param, new IOAuthCallBack() {

                    @Override
                    public void onStartRequest() {

                        UIHelper.showOnLoadingDialog(getActivity());
                    }

                    @Override
                    public void getSuccess(String successJson) {
                        LoginModel.doLogin(getActivity(), successJson, pass);
                        if (new ResultRequestModel(successJson).mIsSuccess) {
                            SharedPreferencesUtils.setParameter(getActivity(), "isSuccess", "register");
                            startActivity(new Intent(getActivity(), UserInfoActivity.class));
                            // getActivity().finish();
                        }
                        UIHelper.hideOnLoadingDialog();
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        UIHelper.showToast(getActivity(), "注册失败，请稍后重试");
                        UIHelper.hideOnLoadingDialog();
                    }
                });*/
            }
        });
    }

}
