package com.android.nana.partner;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;

/**
 * Created by THINK on 2017/7/8.
 * 合伙人主页
 */

public class PartnerBaseActivity extends BaseActivity implements View.OnClickListener {

    private String mUid;
    private ImageButton mBackBtn;
    private TextView mPlanTv, mMetv;

    private FragmentTransaction mFragmetTrans;
    private FragmentManager mFragmentManager;

    private PlanFragment mPlanFragment;
    private MeFragment mMeFragment;


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_partner_base);
    }

    @Override
    protected void findViewById() {
        mPlanTv = findViewById(R.id.plan_tv);
        mMetv = findViewById(R.id.me_tv);

        mBackBtn = findViewById(R.id.common_btn_back);
    }

    @Override
    protected void init() {
        mFragmentManager = getSupportFragmentManager();
        mFragmetTrans = mFragmentManager.beginTransaction();

        if (null != getIntent().getStringExtra("mid")) {
            mUid = getIntent().getStringExtra("mid");
        }

        mPlanFragment = new PlanFragment().newInstance(mUid);
        mMeFragment = new MeFragment().newInstance(mUid);

        Drawable drawable = getResources().getDrawable(R.drawable.sel_partner_selected);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mPlanTv.setTextColor(getResources().getColor(R.color.white));
        mPlanTv.setBackgroundDrawable(drawable);

        mFragmetTrans.replace(R.id.view_layout, mPlanFragment).commit();
    }

    @Override
    protected void setListener() {
        mPlanTv.setOnClickListener(this);
        mMetv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.plan_tv:

                Drawable drawable = getResources().getDrawable(R.drawable.sel_partner_selected);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mPlanTv.setTextColor(getResources().getColor(R.color.white));
                mPlanTv.setBackgroundDrawable(drawable);

                Drawable drawable3 = getResources().getDrawable(R.drawable.bg_me_sel_partner);
                drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                mMetv.setBackgroundDrawable(drawable3);
                mMetv.setTextColor(getResources().getColor(R.color.bg_light_red));

                FragmentTransaction mPlanTransaction1 = mFragmentManager.beginTransaction();
                mPlanTransaction1.replace(R.id.view_layout, mPlanFragment).commit();
                break;
            case R.id.me_tv:

                Drawable drawable1 = getResources().getDrawable(R.drawable.sel_selected);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                mMetv.setTextColor(getResources().getColor(R.color.white));
                mMetv.setBackgroundDrawable(drawable1);


                Drawable drawable2 = getResources().getDrawable(R.drawable.sel_partner);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                mPlanTv.setBackgroundDrawable(drawable2);
                mPlanTv.setTextColor(getResources().getColor(R.color.bg_light_red));


                FragmentTransaction mMeFragmentTransaction1 = mFragmentManager.beginTransaction();
                mMeFragmentTransaction1.replace(R.id.view_layout, mMeFragment).commit();
                break;
        }
    }

}
