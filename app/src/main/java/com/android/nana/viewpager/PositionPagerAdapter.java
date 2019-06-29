package com.android.nana.viewpager;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by THINK on 2017/6/28.
 */

public class PositionPagerAdapter extends PagerAdapter {


    private List<View> viewList;

    public PositionPagerAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (viewList != null) {
            return viewList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return (arg0 == arg1);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        // TODO Auto-generated method stub
        ((PositionViewPager) container).addView(viewList.get(position), 0);

        return viewList.get(position);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        // TODO Auto-generated method stub
        ((PositionViewPager) container).removeView(viewList.get(position));
    }

    @Override
    public float getPageWidth(int position) {
        Log.d("d", "pagewidth = " + super.getPageWidth(position));
        return 0.5f;
    }
}
