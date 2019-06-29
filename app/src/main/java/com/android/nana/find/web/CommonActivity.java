package com.android.nana.find.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by lenovo on 2019/1/24.
 */

public class CommonActivity extends BaseActivity {

    private String url;
    private FragmentManager mFragmentManager;
    private SuperWebX5Fragment mSuperWebX5Fragment;
    private FragmentTransaction ft;
    private Bundle mBundle = null;


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_webview_x5);
    }

    @Override
    protected void findViewById() {
    }

    @Override
    protected void init() {
        url = getIntent().getStringExtra("url");
        if (url.contains("http://") || url.contains("https://")) {
            url = getIntent().getStringExtra("url");
        } else {
            url = "http://" + url;
        }
        mFragmentManager = this.getSupportFragmentManager();
        ft = mFragmentManager.beginTransaction();
        ft.add(R.id.container_framelayout, mSuperWebX5Fragment = SlidingFragment.getInstance(mBundle = new Bundle()), SlidingFragment.class.getName());
        mBundle.putString(SuperWebX5Fragment.URL_KEY, url);
        ft.commit();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSuperWebX5Fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SuperWebX5Fragment mAgentWebX5Fragment = this.mSuperWebX5Fragment;
        if (mAgentWebX5Fragment != null) {
            FragmentKeyDown mFragmentKeyDown = mAgentWebX5Fragment;
            if (mFragmentKeyDown.onFragmentKeyDown(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
