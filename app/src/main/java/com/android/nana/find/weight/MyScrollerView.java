package com.android.nana.find.weight;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ScrollView;

public class MyScrollerView extends ScrollView {

    private float start_Y, last_Y;

    public MyScrollerView(Context context) {
        super(context);
    }

    public MyScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
            float Y = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    last_Y = Y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    start_Y = last_Y;
                    last_Y = Y;
                    float moveY = last_Y - start_Y;
                    if (isTop() && moveY > 0)
                        requestDisallowInterceptTouchEvent(false);
                    else if (isBottom() && moveY < 0)
                       requestDisallowInterceptTouchEvent(false);
                    else
                        requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private boolean isTop() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    private boolean isBottom() {
        int scrollY = getScrollY();
        int height = getHeight();
        if (scrollY + height == getChildAt(0).getHeight()) {
            return true;
        }
        return false;
    }

}
