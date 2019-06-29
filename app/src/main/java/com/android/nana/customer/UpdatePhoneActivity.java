package com.android.nana.customer;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.builder.PhoneValidateCodeBuilder;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.model.LoginModel;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class UpdatePhoneActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;

    private EditText mEtPhone,mEtVerificationCode;
    private TextView mTxtGetCode,mTxtCommit;

    private PhoneValidateCodeBuilder mValidateCodeBuilder;
    
    @Override
    protected void bindViews() {
        setContentView(R.layout.update_phone);
    }

    @Override
    protected void findViewById() {
        mIbBack =  findViewById(R.id.iv_toolbar_back);
        mTxtTitle =  findViewById(R.id.tv_title);
        mEtPhone =  findViewById(R.id.update_phone_edit_phone);
        mEtVerificationCode =  findViewById(R.id.update_phone_edit_verification_code);
        mTxtGetCode =  findViewById(R.id.update_phone_txt_get_code);
        mTxtCommit =  findViewById(R.id.update_phone_txt_update);
    }

    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        mTxtTitle.setText("更换手机号码");
        mValidateCodeBuilder = new PhoneValidateCodeBuilder(UpdatePhoneActivity.this, mEtPhone, mTxtGetCode);
    }

    @Override
    protected void setListener() {
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePhoneActivity.this.finish();
            }
        });
        mTxtGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                mValidateCodeBuilder.verificationFormat("手机号不能为空", "手机号格式不正确");
                mValidateCodeBuilder.countDown(59);
                mValidateCodeBuilder.getCardByPhone("OTHER", new PhoneValidateCodeBuilder.PhoneValidateCodeListener() {

                    @Override
                    public void success(String result) {

                        ResultRequestModel mResultDetailModel = new ResultRequestModel(result);
                        UIHelper.showToast(UpdatePhoneActivity.this, mResultDetailModel.mMessage);
                    }

                    @Override
                    public void failue(String failue) {

                        UIHelper.showToast(UpdatePhoneActivity.this, failue);
                    }
                });
            }
        });

        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mEtPhone.getText().toString().trim();
                String verificationCode = mEtVerificationCode.getText().toString().trim();

                LoginDbHelper.changeMobile(BaseApplication.getInstance().getCustomerId(UpdatePhoneActivity.this),phone,verificationCode,mIOAuthCallBack);

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack(){

        @Override
        public void onStartRequest() {
            UIHelper.showOnLoadingDialog(UpdatePhoneActivity.this);
        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
            if (mResultDetailModel.mIsSuccess) {
                UpdatePhoneActivity.this.finish();

                LoginModel.deLogin(UpdatePhoneActivity.this);

            }
            UIHelper.showToast(UpdatePhoneActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.hideOnLoadingDialog();
            UIHelper.showToast(UpdatePhoneActivity.this,"获取数据失败，请稍后重试");
        }
    };
    
}
