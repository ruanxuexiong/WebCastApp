package com.android.nana.job;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;
import com.android.nana.job.tool.SimpleCardFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/6.
 */

public class InteractFragment extends BaseRequestFragment implements OnTabSelectListener {


    private final String[] mTitles = {
            "对我感兴趣", "看过我", "新职位"
    };
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_interact;
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void init() {
        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }

        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        vp.setAdapter(mAdapter);
        SlidingTabLayout tabLayout_2 = (SlidingTabLayout) findViewById(R.id.tab_view);
        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);
        tabLayout_2.showDot(2);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


}
