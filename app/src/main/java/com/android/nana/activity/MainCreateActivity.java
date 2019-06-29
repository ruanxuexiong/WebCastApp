package com.android.nana.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.bean.CheckPermissionsActivity;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.widget.StateButton;

import java.util.Calendar;

/**
 * Created by lenovo on 2017/8/19.
 */

public class MainCreateActivity extends CheckPermissionsActivity implements View.OnClickListener {

    private CheckBox checkBoxHot, checkBoxMe;
    private View hot, create, me;
    private BasePagerAdapter mPagerAdapter;
    private NoScrollViewPager mViewPager;

    private int selectedItem = 0;
    private long mLastClickTime = 0;
    private int CLICK_STEP = 200;
    private String mStrStatus;//是否是认证用户
    private View mPerfectView;
    private TextView mPerfectContentTv;
    private StateButton mPerfectBtn;
    private ImageButton mCloseBtn;
    private Dialog mPerfectDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStrStatus =(String) SharedPreferencesUtils.getParameter(this, "status", "");
        if (null != getIntent().getStringExtra("mail")) {
            selectedItem = 1;
            setSelecteIcon();
            mViewPager.setCurrentItem(selectedItem);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_main_create);
    }


    @Override
    protected void findViewById() {
        checkBoxHot = findViewById(R.id.checkBox_hot);
        checkBoxMe = findViewById(R.id.checkBox_me);

        hot = findViewById(R.id.navigation_hot);
        create = findViewById(R.id.navigation_create);
        me = findViewById(R.id.navigation_me);

        mViewPager = findViewById(R.id.container);

        mPerfectView = LayoutInflater.from(this).inflate(R.layout.home_perfect_dialog, null);
        mPerfectBtn = mPerfectView.findViewById(R.id.btn_perfect);
        mPerfectContentTv = mPerfectView.findViewById(R.id.tv_content);
        mCloseBtn = mPerfectView.findViewById(R.id.close_iv);
        mPerfectDialog = new AlertDialog.Builder(this).create();
        mPerfectDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void init() {
        setAdapter();
    }

    @Override
    protected void setListener() {
        hot.setOnClickListener(this);
        create.setOnClickListener(this);
        me.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
        hot.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedItem = mViewPager.getCurrentItem();
        setSelecteIcon();
    }


    private void setAdapter() {
        FragmentManager fragmentManager = MainCreateActivity.this.getSupportFragmentManager();
        String[] titles = new String[]{"活动群组", "我的群组"};

        Fragment[] fragments = new Fragment[]{
                HotFragment.newInstance(),
                MeFragment.newInstance()
        };

        mPagerAdapter = new BasePagerAdapter(fragmentManager, titles, fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setNoScroll(true);
        mViewPager.setOffscreenPageLimit(1);
    }

    private void setSelecteIcon() {
        Drawable drawableHome = getResources().getDrawable(R.drawable.ic_new_group_unselect);
        drawableHome.setBounds(0, 0, drawableHome.getMinimumWidth(), drawableHome.getMinimumHeight());
        checkBoxHot.setCompoundDrawables(null, drawableHome, null, null);
        checkBoxHot.setTextColor(getResources().getColor(R.color.grey_99));

        Drawable drawableContacts = getResources().getDrawable(R.drawable.ic_me_unselect);
        drawableContacts.setBounds(0, 0, drawableContacts.getMinimumWidth(), drawableContacts.getMinimumHeight());
        checkBoxMe.setCompoundDrawables(null, drawableContacts, null, null);
        checkBoxMe.setTextColor(getResources().getColor(R.color.grey_99));

        switch (selectedItem) {
            case 0:
                Drawable drawable = getResources().getDrawable(R.drawable.ic_new_group);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                checkBoxHot.setCompoundDrawables(null, drawable, null, null);
                checkBoxHot.setTextColor(getResources().getColor(R.color.create_activity));
                break;

            case 1:
                Drawable drawable1 = getResources().getDrawable(R.drawable.ic_me);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                checkBoxMe.setCompoundDrawables(null, drawable1, null, null);
                checkBoxMe.setTextColor(getResources().getColor(R.color.create_activity));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigation_hot:
                if (selectedItem == 0) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //更新数据
                        //  EventBus.getDefault().post(new MessageEvent("logout"));
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 0;
                setSelecteIcon();
                break;
            case R.id.navigation_create:
                if (null != mStrStatus && mStrStatus.equals("1")) {
                    startActivity(new Intent(MainCreateActivity.this, CreateActivity.class));
                } else {
                    perfect();
                }

                break;
            case R.id.navigation_me:
                if (selectedItem == 1) {
                    long c = Calendar.getInstance().getTimeInMillis();
                    if (c - mLastClickTime < CLICK_STEP) {
                        //  RxBus.getInstance().post(new EventDoubleClickMe());
                    }
                    mLastClickTime = c;
                } else {
                    mLastClickTime = 0;
                }
                selectedItem = 1;
                setSelecteIcon();
                break;
            case R.id.close_iv:
                mPerfectDialog.dismiss();
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(selectedItem);
    }

    private void perfect() {//待完善资料
        mPerfectContentTv.setText("您还不是认证用户，暂时无法\n    创建群组，请提交资料申请认证！");
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setText("马上认证");
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainCreateActivity.this, IDAuthenticationActivity.class));
            }
        });
    }


}
