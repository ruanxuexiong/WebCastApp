package com.android.nana.customer.myincome;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.builder.PhoneValidateCodeBuilder;
import com.android.nana.util.SharedPreferencesUtils;


/**
 * Created by Cristina on 2017/3/23.
 */
public class MyIncomeVerifyIdentityActivity extends BaseActivity {

    private TextView mTxtTitle;
    private ImageButton mIbBack;

    private EditText mEtVerify,mEtPass;
    private TextView mTxtPhone,mTxtGetCode,mTxtNext;

    private String mName,mCart,mBankCartId,mUserName;

    private PhoneValidateCodeBuilder mValidateCodeBuilder;

    @Override
    protected void bindViews() {

        mName = getIntent().getStringExtra("Name");
        mCart = getIntent().getStringExtra("Cart");
        mBankCartId = getIntent().getStringExtra("BankCartId");
        mUserName = (String) SharedPreferencesUtils.getParameter(this, "userName", "");
        setContentView(R.layout.verify_identity);

    }

    @Override
    protected void findViewById() {
        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("身份验证");

        mTxtPhone = (TextView) findViewById(R.id.withdraw_txt_withdraw_info);
        mTxtGetCode = (TextView) findViewById(R.id.verify_identity_txt_get_code);
        mEtVerify = (EditText) findViewById(R.id.verify_identity_et_verify);
        mEtPass = (EditText) findViewById(R.id.verify_identity_et_pass);

        mTxtNext = (TextView) findViewById(R.id.verify_identity_txt_ok);
    }

    @Override
    protected void init() {

        StringBuffer sb = new StringBuffer();
        for (int i=0;i<mUserName.length();i++){
            if (i>2 && i<7) {
                sb.append("*");
            } else {
                sb.append(mUserName.charAt(i));
            }
        }

        mTxtPhone.setText("将发送验证码到手机"+sb.toString());

        mValidateCodeBuilder = new PhoneValidateCodeBuilder(this, mUserName, mTxtGetCode);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mTxtGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mValidateCodeBuilder.verificationFormat("手机号不能为空", "手机号格式不正确");
                mValidateCodeBuilder.countDown(59);
                mValidateCodeBuilder.getCardByPhone("OTHER", new PhoneValidateCodeBuilder.PhoneValidateCodeListener() {

                    @Override
                    public void success(String result) {

                        ResultRequestModel mResultDetailModel = new ResultRequestModel(result);
                        if (mResultDetailModel.mIsSuccess) {

                        }
                        UIHelper.showToast(MyIncomeVerifyIdentityActivity.this, mResultDetailModel.mMessage);
                    }

                    @Override
                    public void failue(String failue) {

                        UIHelper.showToast(MyIncomeVerifyIdentityActivity.this, failue);
                    }
                });
            }
        });

        mTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next();

            }
        });

    }

    private void next(){

        String verify = mEtVerify.getText().toString().trim();
        if (TextUtils.isEmpty(verify)) {
            showToast("验证码不能为空");
            return;
        }
        String pass = mEtPass.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            showToast("密码不能为空");
            return;
        }

//        MyIncomeHelper.addBankCard(BaseApplication.getInstance().getCustomerId(this),
//                mName, verify, mCart, mBankCartId, pass, mIOAuthCallBack);
    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(MyIncomeVerifyIdentityActivity.this);
        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                MyIncomeVerifyIdentityActivity.this.finish();
            }
            UIHelper.showToast(MyIncomeVerifyIdentityActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(MyIncomeVerifyIdentityActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };
    
}
