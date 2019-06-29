package com.android.nana.customer;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.StringHelper;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class EmailActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtCommit;

    private EditText mEtText;
    private String mText;

    @Override
    protected void bindViews() {
        setContentView(R.layout.email);
    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtCommit = findViewById(R.id.email_txt_commit);
        mEtText = findViewById(R.id.email_et_text);
    }

    @Override
    protected void init() {
        mIbBack.setVisibility(View.VISIBLE);
        mTxtTitle.setText("设置邮箱");

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mText = mEtText.getText().toString().trim();
                if (TextUtils.isEmpty(mText)) {
                    UIHelper.showToast(EmailActivity.this, "提交内容不能为空");
                    return;
                }
                if (!StringHelper.checkEmail(mText)) {
                    UIHelper.showToast(EmailActivity.this, "邮箱格式不正确");
                    return;
                }

                CustomerDbHelper.setEmail(BaseApplication.getInstance().getCustomerId(EmailActivity.this), mText, mIOAuthCallBack);

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(EmailActivity.this);

        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResult = new ResultRequestModel(successJson);
            if (mResult.mIsSuccess) {

                UserInfo userInfo = (UserInfo) SharedPreferencesUtils.getObject(EmailActivity.this, "userInfo", UserInfo.class);
                userInfo.setUser_email(mText);
                SharedPreferencesUtils.saveObject(EmailActivity.this, "userInfo", userInfo);
                EmailActivity.this.finish();

            } else {
                UIHelper.showToast(EmailActivity.this, mResult.mMessage);
            }
            UIHelper.hideOnLoadingDialog();

        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.hideOnLoadingDialog();

        }
    };

}
