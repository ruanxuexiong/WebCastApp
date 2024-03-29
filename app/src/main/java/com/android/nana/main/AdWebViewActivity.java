package com.android.nana.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by lenovo on 2017/12/19.
 * 广告
 */

public class AdWebViewActivity extends BaseActivity implements View.OnClickListener {

    private TextView mIbBack;
    private WebView mWebView;
    private TextView mTxtTitle, mShareTv;
    private ProgressBar mProgressBar;

    private String mTitleStr;
    private String mUrlStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.android.common.R.id.base_webview_btn_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void bindViews() {
        setContentView(com.android.common.R.layout.base_webview);
    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(com.android.common.R.id.base_webview_btn_back);
        mTxtTitle = findViewById(com.android.common.R.id.base_webview_txt_title);
        mWebView = findViewById(com.android.common.R.id.base_webview);

        mShareTv = findViewById(R.id.base_webview_txt_right_text);

        mShareTv.setVisibility(View.GONE);
        mProgressBar = findViewById(com.android.common.R.id.base_webview_progress_bar);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void init() {
        showProgressDialog("", "加载中...");
        if (null != getIntent().getStringExtra("title")) {
            mUrlStr = getIntent().getStringExtra("url");
        }

        if (mUrlStr.contains("http://") || mUrlStr.contains("https://")) {
            mWebView.loadUrl(mUrlStr);
        } else {
            mWebView.loadUrl("http://" + mUrlStr);
        }

        mWebView.getSettings().setDomStorageEnabled(true);
        // 设置支持javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 启动缓存
        mWebView.getSettings().setAppCacheEnabled(true);
        // 设置缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }


        });

       mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTxtTitle.setText(title);
            }
        });

        new Handler().postDelayed(new Runnable() { // 延迟0.3秒，让体验更好
            @Override
            public void run() {
                dismissProgressDialog();
            }
        }, 500);
    }

    @Override
    protected void setListener() {
        mIbBack.setOnClickListener(this);
    }

    // 覆盖onKeydown 添加处理WebView 界面内返回事件处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
