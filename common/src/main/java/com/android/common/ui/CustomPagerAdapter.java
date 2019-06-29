package com.android.common.ui;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class CustomPagerAdapter extends PagerAdapter {
    
    private List<View> mViews;
    private SlideShowView.OnSlideShowViewClickListener mOnSlideShowViewClickListener;

    public CustomPagerAdapter(List<View> views, SlideShowView.OnSlideShowViewClickListener l) {
        mOnSlideShowViewClickListener = l;
        mViews = views;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        if (position <= mViews.size()-1) {
            ((ChildViewPagerExtend)container).removeView(mViews.get(position));
        }
    }

    @Override
    public Object instantiateItem(View container, final int position) {
        ((ChildViewPagerExtend)container).removeView(mViews.get(position));
        ((ChildViewPagerExtend)container).addView(mViews.get(position), 0);
        ((ChildViewPagerExtend)container).setOnSingleTouchListener(new ChildViewPagerExtend.OnSingleTouchListener() {
            @Override
            public void onSingleTouch() {
                if (mOnSlideShowViewClickListener != null) {
                    mOnSlideShowViewClickListener.jump(position);
                }
            }
        });
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
