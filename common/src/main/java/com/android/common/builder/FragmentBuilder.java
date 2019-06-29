package com.android.common.builder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.common.BaseApplication;
import com.android.common.base.BaseRequestFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentBuilder {

    private FragmentActivity activity;
    private FragmentManager mFragmentManager;
    private int tabContentId;
    private int tabRadioGroupId;
    private int tabRadioButtonLayoutId;
    private List<ViewHolder> mViewHolders;
    private RadioGroup mRadioGroup;
    private int mCurrectIndex;

    public FragmentBuilder(FragmentActivity activity, int tabContentId, int tabRadioGroupId, int tabRadioButtonLayoutId) {
        this.activity = activity;
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.tabContentId = tabContentId;
        this.tabRadioGroupId = tabRadioGroupId;
        this.tabRadioButtonLayoutId = tabRadioButtonLayoutId;
        this.mViewHolders = new ArrayList<ViewHolder>();
    }

    public FragmentBuilder(FragmentActivity activity, int tabContentId) {
        this.activity = activity;
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.tabContentId = tabContentId;
        this.mViewHolders = new ArrayList<ViewHolder>();
    }

    public FragmentBuilder(FragmentManager fragmentManager, int tabContentId, RadioGroup radioGroup) {
        this.mFragmentManager = fragmentManager;
        this.tabContentId = tabContentId;
        this.mRadioGroup = radioGroup;
        this.mViewHolders = new ArrayList<ViewHolder>();
    }

    public FragmentBuilder registerFragement(String title, BaseRequestFragment framage) {

        mViewHolders.add(new ViewHolder(title, framage));
        return this;
    }

    public void build() {

        build(0);
    }

    public void build(int currectIndex) {

        mRadioGroup = (RadioGroup) activity.findViewById(tabRadioGroupId);

        int screenWidth = BaseApplication.getInstance().getScreenWidth(this.activity);
        int width = screenWidth / mViewHolders.size();

        for (int i = 0; i < mViewHolders.size(); i++) {
            RadioButton button = (RadioButton) LayoutInflater.from(activity).inflate(tabRadioButtonLayoutId, null);
            button.setText(mViewHolders.get(i).mTitle);
            button.setTag(i);
            button.setWidth(width);
            button.setId(i);
            mRadioGroup.addView(button);
        }

        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkId) {
                switchFragment(checkId);
            }
        });
        mCurrectIndex = currectIndex;
        ((RadioButton) mRadioGroup.getChildAt(currectIndex)).setChecked(true);
    }

    public void builder() {

        switchFragment(0);
    }

    /**
     * 切换Fragment
     *
     * @param index
     */
    public void switchFragment(int index) {

        for (int i = 0; i < mViewHolders.size(); i++) {
            Fragment fragment = mViewHolders.get(i).mFramage;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (i == index) {
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.add(tabContentId, fragment);
                }
                switchTab(i); // 显示目标tab
                ft.commit();
            }
        }
    }

    public void loadFragment(int index) {
        for (int i = 0; i < mViewHolders.size(); i++) {
            Fragment fragment = mViewHolders.get(i).mFramage;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (i == index) {
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.replace(tabContentId, fragment);
                }
                switchTab(i); // 显示目标tab
                ft.commit();
            }
        }
    }

    /**
     * 切换tab
     *
     * @param idx
     */
    private void switchTab(int idx) {
        for (int i = 0; i < mViewHolders.size(); i++) {
            Fragment fragment = mViewHolders.get(i).mFramage;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
    }

    public Fragment getCurrentFragment() {
        return mViewHolders.get(mCurrectIndex).mFramage;
    }


    public class ViewHolder {

        public ViewHolder(String title, BaseRequestFragment fragement) {
            mTitle = title;
            mFramage = fragement;
        }

        public String mTitle;
        public BaseRequestFragment mFramage;
    }

}