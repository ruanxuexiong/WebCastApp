package com.android.nana.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by THINK on 2017/6/28.
 */

public class PositionViewPager extends ViewPager {


    public PositionViewPager(Context context) {
        super(context);
    }

    public PositionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void scrollTo(int x, int y) {
        if (getAdapter() == null || x > getWidth() * (getAdapter().getCount() - 2)) {
            return;
        }
        super.scrollTo(x, y);
    }
}
