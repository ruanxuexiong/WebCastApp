package com.android.nana.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.nana.R;
import com.android.nana.main.HomeRecordFragment;
import com.android.nana.material.WhoSeeFragment;
import com.android.nana.util.Constant;

/**
 * Created by lenovo on 2017/8/29.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;
    private HomeRecordFragment recordFragment;
    private WhoSeeFragment seeFragment;
    private String mid;

    public HomePagerAdapter(FragmentManager fm, Context context, String mid) {
        super(fm);
        this.context = context;
        this.mid = mid;
        tabTitles = new String[]{context.getString(R.string.me_record), context.getString(R.string.me_see), context.getString(R.string.me_see_who)};
        recordFragment = HomeRecordFragment.newInstance();
        seeFragment = WhoSeeFragment.newInstance(Constant.Appointment.Other_me, mid);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (recordFragment == null) {
                    recordFragment = HomeRecordFragment.newInstance();
                }
                return recordFragment;
            case 1:
                if (seeFragment == null) {
                    seeFragment = WhoSeeFragment.newInstance(Constant.Appointment.Other_me, mid);
                }
                return seeFragment;

            default:
                if (recordFragment == null) {
                    recordFragment = HomeRecordFragment.newInstance();
                }
                return recordFragment;
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
