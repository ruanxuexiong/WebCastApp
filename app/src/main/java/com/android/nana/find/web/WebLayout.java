package com.android.nana.find.web;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.nana.R;
import com.fanneng.android.web.IWebLayout;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by lenovo on 2019/1/24.
 */

public class WebLayout implements IWebLayout {


    private Activity mActivity;
    private final SlidingLayout slidingLayout;
    private WebView mWebView = null;

    public WebLayout(Activity activity) {
        this.mActivity = activity;
        slidingLayout = (SlidingLayout) LayoutInflater.from(activity).inflate(R.layout.fragment_sliding_web, null);
        mWebView = slidingLayout.findViewById(R.id.webView);
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return slidingLayout;
    }

    @Nullable
    @Override
    public WebView getWeb() {
        return mWebView;
    }

}
