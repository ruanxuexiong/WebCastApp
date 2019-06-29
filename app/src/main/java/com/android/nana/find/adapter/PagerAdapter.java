package com.android.nana.find.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.android.nana.find.FindPhotoWallFragment;
import com.android.nana.find.FollowFragment;
import com.android.nana.find.NearbyFragment;
import com.android.nana.find.NewFindActivity;

/**
 * Created by lenovo on 2018/9/26.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context mContext;
    private NewFindActivity mFindFragment;
    private FollowFragment mFollowFragment;
    private NearbyFragment mNearbyFragment;

    public PagerAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
        tabTitles = new String[]{"附近", "关注", "发现"};
        mNearbyFragment = NearbyFragment.newInstance();
        mFollowFragment = FollowFragment.newInstance();
        mFindFragment = NewFindActivity.newInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (mNearbyFragment == null) {
                    mNearbyFragment = NearbyFragment.newInstance();
                }
                return mNearbyFragment;
            case 1:
                if (mFollowFragment == null) {
                    mFollowFragment = FollowFragment.newInstance();

                }
                return mFollowFragment;
            case 2:
                if (mFindFragment == null) {
                    mFindFragment = NewFindActivity.newInstance();
                }
                return mFindFragment;
            default:
                if (mNearbyFragment == null) {
                    mNearbyFragment = NearbyFragment.newInstance();
                }
                return mNearbyFragment;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }
}
