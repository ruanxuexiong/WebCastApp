package com.android.nana.red.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.nana.eventBus.RedEvent;
import com.android.nana.red.OrdinaryFragment;
import com.android.nana.red.SpellingFragment;

/**
 * Created by lenovo on 2018/11/5.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 2;
    private String tabTitles[];
    private Context mContext;
    private SpellingFragment mSpellingFragment;
    private OrdinaryFragment mOrdinaryFragment;
    private RedEvent mRed;

    public PagerAdapter(FragmentManager fm, Context mContext, RedEvent mRed) {
        super(fm);
        this.mContext = mContext;
        this.mRed = mRed;
        tabTitles = new String[]{"普通红包", "拼手气红包"};
        mSpellingFragment = new SpellingFragment().newInstance(mRed);
        mOrdinaryFragment = new OrdinaryFragment().newInstance();

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (mOrdinaryFragment == null) {
                    mOrdinaryFragment = OrdinaryFragment.newInstance();
                }
                return mOrdinaryFragment;
            case 1:
                if (mSpellingFragment == null) {
                    mSpellingFragment = SpellingFragment.newInstance(mRed);
                }
                return mSpellingFragment;

            default:
                if (mOrdinaryFragment == null) {
                    mOrdinaryFragment = OrdinaryFragment.newInstance();
                }
                return mOrdinaryFragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
