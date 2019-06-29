package com.android.nana.base;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.common.pay.wx.Utils;
import com.android.common.utils.AppManager;
import com.android.nana.R;
import com.android.nana.util.ToastUtils;

import java.util.Locale;

import static android.view.FrameMetrics.ANIMATION_DURATION;

/**
 * Created by lenovo on 2017/8/29.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected String TAG;
    protected Context mContext;
    protected ProgressDialog mProgressDialog;
    protected Toolbar mToolbar;
    protected TextView mTvTitle, mAction1, mAction2, mIvBack;
    private Window window;
    private int statusBarColor;
    private static final String STATUS_BAR_COLOR = "statusBarColor";
    private Animator actionModeInAnimator, actionModeOutAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        mContext = this;

        // 设置不能横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        //Activity管理
        AppManager.getAppManager().addActivity(this);

        switchLanguage(Locale.getDefault());//设置国际化语言 --Locale.getDefault() 默认系统字体
    }


    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        init();
        initAnimator();
    }

    public void init() {
        initView();
        bindEvent();
    }

    /**
     * 初始化页面
     */
    public abstract void initView();

    /**
     * 绑定事件
     */
    public abstract void bindEvent();

    /**
     * 设置电池条颜色
     */
    private void initAnimator() {
        window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = window.getStatusBarColor();
            int actionModeStatusBarColor = ContextCompat.getColor(this,
                    R.color.view_toolbar);
            int startColor = Color.argb(
                    0,
                    Color.red(actionModeStatusBarColor),
                    Color.green(actionModeStatusBarColor),
                    Color.blue(actionModeStatusBarColor)
            );
            actionModeInAnimator = ObjectAnimator.ofObject(
                    window,
                    STATUS_BAR_COLOR,
                    new ArgbEvaluator(),
                    startColor,
                    actionModeStatusBarColor
            );
            actionModeOutAnimator = ObjectAnimator.ofObject(
                    window,
                    STATUS_BAR_COLOR,
                    new ArgbEvaluator(),
                    actionModeStatusBarColor,
                    startColor
            );
            actionModeInAnimator.setDuration(ANIMATION_DURATION);
            actionModeOutAnimator.setDuration(ANIMATION_DURATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionModeOutAnimator.cancel();
            actionModeInAnimator.setDuration(ANIMATION_DURATION).start();
            Utils.setLightStatusBar(window.getDecorView());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(statusBarColor);
            if (actionModeInAnimator != null) {
                actionModeInAnimator.cancel();
            }
            if (actionModeOutAnimator != null) {
                actionModeOutAnimator.start();
            }
            Utils.clearLightStatusBar(window.getDecorView());
        }
        AppManager.getAppManager().removeActivity(this);
    }

    protected void showProgressDialog() {
        showProgressDialog(null, getString(R.string.loading));
    }

    protected void showProgressDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        if (title != null && !title.isEmpty()) {
            mProgressDialog.setTitle(title);
        }
        if (message != null && !message.isEmpty()) {
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showToastShort(String s) {
        ToastUtils.showToast(s, ToastUtils.LENGTH_SHORT);
    }

    protected void showToastLong(String s) {
        ToastUtils.showToast(s, ToastUtils.LENGTH_LONG);
    }

    protected void setTitle(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        mAction1 = (TextView) mToolbar.findViewById(R.id.toolbar_right_1);
        mAction2 = (TextView) mToolbar.findViewById(R.id.toolbar_right_2);
        mIvBack = (TextView) mToolbar.findViewById(R.id.iv_toolbar_back);

        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
        View back = findViewById(R.id.iv_toolbar_back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected void hideToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    protected void showToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideBackIcon() {
        if (mIvBack != null) {
            mIvBack.setVisibility(View.GONE);
        }
    }

    protected void setAction1(String action) {
        if (mAction1 != null) {
            mAction1.setText(action);
            mAction1.setVisibility(View.VISIBLE);
            mAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction1Click();
                }
            });
        }
    }

    protected void setAction1Icon(int resId) {
        if (mAction1 != null) {
            mAction1.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mAction1.setCompoundDrawables(drawable, null, null, null);
            mAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction1Click();
                }
            });
        }
    }

    protected void hideAction1() {
        if (mAction1 != null) {
            mAction1.setVisibility(View.GONE);
        }
    }

    protected void showAction1() {
        if (mAction1 != null) {
            mAction1.setVisibility(View.VISIBLE);
            mAction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction1Click();
                }
            });
        }
    }

    protected void onAction1Click() {

    }

    protected void setAction2(String action) {
        if (mAction2 != null) {
            mAction2.setText(action);
            mAction2.setVisibility(View.VISIBLE);
            mAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction2Click();
                }
            });
        }
    }

    protected void setAction2Icon(int resId) {
        if (mAction2 != null) {
            mAction2.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mAction2.setCompoundDrawables(drawable, null, null, null);
            mAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction2Click();
                }
            });
        }
    }

    protected void hideAction2() {
        if (mAction2 != null) {
            mAction2.setVisibility(View.GONE);
        }
    }

    protected void showAction2() {
        if (mAction2 != null) {
            mAction2.setVisibility(View.VISIBLE);
            mAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAction2Click();
                }
            });
        }
    }

    protected void onAction2Click() {

    }

    //设置多语言
    protected void switchLanguage(Locale locale) {
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
