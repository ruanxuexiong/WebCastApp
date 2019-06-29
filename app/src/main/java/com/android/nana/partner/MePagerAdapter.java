package com.android.nana.partner;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by THINK on 2017/7/8.
 */

public class MePagerAdapter extends FragmentPagerAdapter {


    private static final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;
    private MeProfitFragment profitFragment;
    private MeNumberFragment numberFragment;
    private MeInviteFragment inviteFragment;
    private String mUid, mMsg, mUrl;

    public MePagerAdapter(FragmentManager fm, Context context, String mUid, String mMsg, String mUrl) {
        super(fm);
        this.context = context;
        this.mUid = mUid;
        this.mMsg = mMsg;
        this.mUrl = mUrl;
        tabTitles = new String[]{"推荐收益排行", "推荐人数排行", "我推荐的人"};
        profitFragment = MeProfitFragment.newInstance(this.context, this.mUid, mMsg, mUrl);
        numberFragment = MeNumberFragment.newInstance(this.context, this.mUid, mMsg, mUrl);
        inviteFragment = MeInviteFragment.newInstance(this.context, this.mUid, mMsg, mUrl);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (profitFragment == null) {
                    profitFragment = MeProfitFragment.newInstance(context, mUid, mMsg, mUrl);
                }
                return profitFragment;
            case 1:
                if (numberFragment == null) {
                    numberFragment = MeNumberFragment.newInstance(context, mUid, mMsg, mUrl);
                }
                return numberFragment;
            case 2:
                if (inviteFragment == null) {
                    inviteFragment = MeInviteFragment.newInstance(context, mUid, mMsg, mUrl);
                }
                return inviteFragment;
            default:
                if (profitFragment == null) {
                    profitFragment = MeProfitFragment.newInstance(context, mUid, mMsg, mUrl);
                }
                return profitFragment;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
