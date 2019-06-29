package com.android.nana.main;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.android.nana.R;
import com.android.nana.activity.BasePagerAdapter;
import com.android.nana.activity.NoScrollViewPager;
import com.android.nana.base.CheckPermissionsActivity;
import com.android.nana.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

/**
 * Created by lenovo on 2017/8/29.
 */

public class MainPageActivity extends CheckPermissionsActivity {


    private CheckBox checkBoxSan, checkBoxMail, checkBoxFind, checkBoxMe;
    private View san, mail, find, me;
    private BasePagerAdapter mPagerAdapter;
    private NoScrollViewPager mViewPager;

    private int selectedItem = 0;
    private long lastPressBackTime = 0;
    private int PRESS_BACK_STEP = 1000;
    private long mLastClickTime = 0;
    private int CLICK_STEP = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
       // setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedItem = mViewPager.getCurrentItem();
        setSelecteIcon();
    }

  /*  private void setAdapter() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String[] titles = new String[]{"三分钟", "通讯录", "发现", "我"};

        Fragment[] fragments = new Fragment[]{
                HomeFragment.newInstance(),
                MailFragment.newInstance(),
                FindFragment.newInstance(),
                MainMeFragment.newInstance()
        };

        mPagerAdapter = new BasePagerAdapter(fragmentManager, titles, fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(true);
        mViewPager.setOffscreenPageLimit(3);
    }*/

    private void setSelecteIcon() {
        Drawable drawableHome = mContext.getResources().getDrawable(R.drawable.icon_san_unselect);
        drawableHome.setBounds(0, 0, drawableHome.getMinimumWidth(), drawableHome.getMinimumHeight());
        checkBoxSan.setCompoundDrawables(null, drawableHome, null, null);
        checkBoxSan.setTextColor(getResources().getColor(R.color.grey_99));

        Drawable drawableContacts = mContext.getResources().getDrawable(R.drawable.icon_mail_unselect);
        drawableContacts.setBounds(0, 0, drawableContacts.getMinimumWidth(), drawableContacts.getMinimumHeight());
        checkBoxMail.setCompoundDrawables(null, drawableContacts, null, null);
        checkBoxMail.setTextColor(getResources().getColor(R.color.grey_99));

        Drawable drawableFind = mContext.getResources().getDrawable(R.drawable.ic_find_unselect);
        drawableFind.setBounds(0, 0, drawableFind.getMinimumWidth(), drawableFind.getMinimumHeight());
        checkBoxFind.setCompoundDrawables(null, drawableFind, null, null);
        checkBoxFind.setTextColor(getResources().getColor(R.color.grey_99));

        Drawable drawableMe = mContext.getResources().getDrawable(R.drawable.icon_new_me_unselect);
        drawableMe.setBounds(0, 0, drawableMe.getMinimumWidth(), drawableMe.getMinimumHeight());
        checkBoxMe.setCompoundDrawables(null, drawableMe, null, null);
        checkBoxMe.setTextColor(getResources().getColor(R.color.grey_99));

        switch (selectedItem) {
            case 0:
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_san);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                checkBoxSan.setCompoundDrawables(null, drawable, null, null);
                checkBoxSan.setTextColor(getResources().getColor(R.color.green));
                break;

            case 1:
                Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.icon_mail);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                checkBoxMail.setCompoundDrawables(null, drawable1, null, null);
                checkBoxMail.setTextColor(getResources().getColor(R.color.green));
                break;

            case 2:
                Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.ic_find);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                checkBoxFind.setCompoundDrawables(null, drawable2, null, null);
                checkBoxFind.setTextColor(getResources().getColor(R.color.green));
                break;

            case 3:
                Drawable drawable3 = mContext.getResources().getDrawable(R.drawable.icon_new_me);
                drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                checkBoxMe.setCompoundDrawables(null, drawable3, null, null);
                checkBoxMe.setTextColor(getResources().getColor(R.color.green));
                break;
        }

    }


    @Override
    public void initView() {
        checkBoxSan = (CheckBox) findViewById(R.id.checkBox_san);
        checkBoxMail = (CheckBox) findViewById(R.id.checkBox_mail);
        checkBoxFind = (CheckBox) findViewById(R.id.checkBox_find);
        checkBoxMe = (CheckBox) findViewById(R.id.checkBox_me);

        san = findViewById(R.id.navigation_san);
        mail = findViewById(R.id.navigation_mail);
        find = findViewById(R.id.navigation_find);
        me = findViewById(R.id.navigation_me);

        mViewPager = (NoScrollViewPager) findViewById(R.id.container);
    }

    @Override
    public void bindEvent() {
        san.setOnClickListener(this);
        mail.setOnClickListener(this);
        find.setOnClickListener(this);
        me.setOnClickListener(this);
        san.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_san:
                if (selectedItem == 0) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickHomepage());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 0;
                setSelecteIcon();
                break;

            case R.id.navigation_mail:
                if (selectedItem == 1) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //RxBus.getInstance().post(new EventDoubleClickContacts());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 1;
                setSelecteIcon();
                break;

            case R.id.navigation_find:
                if (selectedItem == 2) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 2;
                setSelecteIcon();
                break;

            case R.id.navigation_me:
                if (selectedItem == 3) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 3;
                setSelecteIcon();
                break;
            default:
                break;
        }

        mViewPager.setCurrentItem(selectedItem);
    }


    @Override
    public void onBackPressed() {
        long c = Calendar.getInstance().getTimeInMillis();
        if (c - lastPressBackTime < PRESS_BACK_STEP) {
            super.onBackPressed();
            MainPageActivity.this.finish();
            MobclickAgent.onKillProcess(this);
            android.os.Process.killProcess(android.os.Process.myPid());
            startActivity(new Intent(MainPageActivity.this, com.android.nana.auth.WelcomeActivity.class));
        } else {
            ToastUtils.showToast("再按一次退出程序！");
            lastPressBackTime = c;
        }
    }
}
