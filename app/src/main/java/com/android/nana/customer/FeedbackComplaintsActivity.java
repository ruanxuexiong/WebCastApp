package com.android.nana.customer;

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

/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class FeedbackComplaintsActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtCommit;

    private EditText mEtText;
    
    @Override
    protected void bindViews() {

        setContentView(R.layout.feedback_complaints);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        
        mTxtCommit = (TextView) findViewById(R.id.feedback_complaints_txt_commit);
        
        mEtText = (EditText) findViewById(R.id.feedback_complaints_et_text);

    }

    @Override
    protected void init() {

        mTxtTitle.setText("我要反馈");

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = mEtText.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    UIHelper.showToast(FeedbackComplaintsActivity.this, "提交内容不能为空");
                    return;
                }

                UIHelper.showToast(FeedbackComplaintsActivity.this, "您所反馈的内容已提交，我们会尽快为您解决");

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(FeedbackComplaintsActivity.this);

        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResult = new ResultRequestModel(successJson);
            if (mResult.mIsSuccess) {

                FeedbackComplaintsActivity.this.finish();

            } else {
                UIHelper.showToast(FeedbackComplaintsActivity.this, mResult.mMessage);
            }
            UIHelper.hideOnLoadingDialog();

        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.hideOnLoadingDialog();

        }
    };
    
}
