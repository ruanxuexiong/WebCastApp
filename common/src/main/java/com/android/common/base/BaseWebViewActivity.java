package com.android.common.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.R;

public abstract class BaseWebViewActivity extends BaseActivity {

    private TextView mIbBack;
    private WebView mWebView;
    private TextView mTxtTitle;
    private ProgressBar mProgressBar;
    public String mUrl, mTitle;


    public abstract String getErrorDescription();

    public abstract void setCookier(String url);

    public abstract boolean onShouldOverrideUrlLoading(WebView view, String url);



    public WebView getWebView() {
        return mWebView;
    }

    @Override
    protected void bindViews() {

        setContentView(R.layout.base_webview);

        mUrl = getIntent().getStringExtra("Url");
        mTitle = getIntent().getStringExtra("Title");
    }

    @Override
    protected void findViewById() {
        mIbBack = (TextView) findViewById(R.id.base_webview_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.base_webview_txt_title);
        mWebView = (WebView) findViewById(R.id.base_webview);


        mProgressBar = (ProgressBar) findViewById(R.id.base_webview_progress_bar);

        initWebViewSettings();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {

        // 设置可以访问文件
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

    }

    @Override
    protected void locationData() {

    }

    @Override
    protected void init() {

        if (mTitle != null && mTitle.length() > 0) {
            mTxtTitle.setText(mTitle);
        }

        setCookier(mUrl);

        mWebView.loadUrl(mUrl);
    }

    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseWebViewActivity.this.finish();

            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                setCookier(url);

                return onShouldOverrideUrlLoading(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                Toast.makeText(BaseWebViewActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                view.loadData(getErrorDescription(), "text/html; charset=UTF-8", null);

                mTxtTitle.setText("网络异常");
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                mProgressBar.setVisibility(View.GONE);

                if (mTitle != null && mTitle.length() > 0) {
                    mTxtTitle.setText(mTitle);
                } else {
                    mTxtTitle.setText(view.getTitle());
                }
                super.onPageFinished(view, url);
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
