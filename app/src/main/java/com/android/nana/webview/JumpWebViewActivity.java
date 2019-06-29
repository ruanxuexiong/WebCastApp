package com.android.nana.webview;

import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;


/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class JumpWebViewActivity extends BaseActivity {

    private TextView mIbBack;
    private WebView mWebView;
    private TextView mTxtTitle, mShareTv;
    private ProgressBar mProgressBar;


    private String mTermId, mTitle;

    @Override
    protected void bindViews() {
        mTitle = getIntent().getStringExtra("Title");
        mTermId = getIntent().getStringExtra("TermId");
        setContentView(com.android.common.R.layout.base_webview);

    }

    @Override
    protected void findViewById() {

        mIbBack =  findViewById(com.android.common.R.id.base_webview_btn_back);
        mTxtTitle =  findViewById(com.android.common.R.id.base_webview_txt_title);
        mWebView =  findViewById(com.android.common.R.id.base_webview);

        mShareTv =  findViewById(R.id.base_webview_txt_right_text);

        mShareTv.setVisibility(View.GONE);
        mProgressBar =  findViewById(com.android.common.R.id.base_webview_progress_bar);
        mProgressBar.setVisibility(View.GONE);

        mTxtTitle.setText(mTitle);

    }

    @Override
    protected void init() {

        CustomerDbHelper.article(mTermId, mIOAuthCallBack);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(JumpWebViewActivity.this);

        }

        @Override
        public void getSuccess(String successJson) {
            ResultRequestModel mResult = new ResultRequestModel(successJson);
            if (mResult.mIsSuccess) {
                mWebView.loadDataWithBaseURL("", JSONUtil.get(mResult.mJsonData, "post_content", ""), "text/html", "utf-8", "");
            } else {
                UIHelper.showToast(JumpWebViewActivity.this, mResult.mMessage);
            }
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.hideOnLoadingDialog();

        }
    };

}
