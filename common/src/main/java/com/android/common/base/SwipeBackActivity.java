package com.android.common.base;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.aitangba.swipeback.SwipeBackHelper;

/**
 * Created by THINK on 2017/7/20.
 */

public class SwipeBackActivity extends AppCompatActivity implements SwipeBackHelper.SlideBackManager {
    private static final String TAG = "SwipeBackActivity";

    private SwipeBackHelper mSwipeBackHelper;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mSwipeBackHelper == null) {
            mSwipeBackHelper = new SwipeBackHelper(this);
        }
        return mSwipeBackHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public Activity getSlideActivity() {
        return this;
    }

    @Override
    public boolean supportSlideBack() {
        return true;
    }

    @Override
    public boolean canBeSlideBack() {
        return true;
    }

    @Override
    public void finish() {
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.finishSwipeImmediately();
            mSwipeBackHelper = null;
        }
        super.finish();
    }
}
