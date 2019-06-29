package com.android.common.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.android.common.BaseApplication;
import com.android.common.helper.UIHelper;
import com.android.common.utils.AppManager;
import com.umeng.analytics.MobclickAgent;


public abstract class BaseActivity extends FragmentActivity {

    protected BaseApplication mBaseApplication;
    protected ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);

        mBaseApplication = (BaseApplication) getApplication();

        bindViews();
        findViewById();
        locationData();
        initOther();
        init();
        setListener();
    }

    protected void showProgressDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        if (title != null && !title.isEmpty()) {
            mProgressDialog.setTitle(title);
        }
        if (message != null && !message.isEmpty()) {
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected OnClickListener mBackPullListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    protected void populateData() {

    }


    /***
     * 绑定页面的layout
     */
    protected abstract void bindViews();

    /***
     * 为每一个控件赋值
     */
    protected abstract void findViewById();

    /***
     * 加载其他页面的intent传递过来的值，可以放到bindviews中执行
     */
    protected void locationData() {
    }

    /**
     * 加载启动数据
     */
    protected abstract void init();

    /***
     * 加载出去启动数据中的其他的数据
     */
    protected void initOther() {
    }

    /***
     * 绑定控件的事件。
     */
    protected abstract void setListener();

    protected void showToast(String message) {

        UIHelper.showToast(this, message);

    }

    public boolean checkLogin() {
        return mBaseApplication.checkLogin(this);
    }

    public void loadWebView(WebView webView, String body) {
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.loadData(body, "text/html; charset=UTF-8", "UTF-8");
    }

    /**
     * 友盟统计ActivityName
     *
     * @return ActivityName
     */
    protected String analyticsActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(analyticsActivityName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(analyticsActivityName());
        MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {

        AppManager.getAppManager().removeActivity(this);

        super.finish();
    }



}
