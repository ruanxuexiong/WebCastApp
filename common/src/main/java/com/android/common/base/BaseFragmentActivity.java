package com.android.common.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.common.BaseApplication;
import com.android.common.utils.AppManager;
import com.lidroid.xutils.ViewUtils;


public abstract class BaseFragmentActivity extends SwipeBackActivity {

    protected BaseApplication mBaseApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mBaseApplication = (BaseApplication) getApplication();
        super.onCreate(savedInstanceState);

        bindViews();
        ViewUtils.inject(this);

        locationData();
        findViewById();
        initFragments();
        init();
        setListener();
        AppManager.getAppManager().addActivity(this);
    }

    protected abstract void bindViews();

    protected abstract void locationData();

    protected abstract void findViewById();

    protected abstract void init();

    protected abstract void initFragments();

    protected abstract void setListener();

}
