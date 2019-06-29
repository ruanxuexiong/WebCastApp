package com.android.nana.red;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.RedEvent;
import com.android.nana.red.adapter.PagerAdapter;
import com.flyco.tablayout.SlidingTabLayout;

import static com.android.nana.R.id.iv_toolbar_back;

/**
 * Created by lenovo on 2018/11/5.
 */

public class RedEnvelopesActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private SlidingTabLayout mTabLayout;
    private RedEvent mRed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_red_envelopes);
    }

    @Override
    protected void findViewById() {


        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(iv_toolbar_back);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.layout_tab);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发点赞红包");
        mBackTv.setVisibility(View.VISIBLE);
       /* if (null != getIntent().getSerializableExtra("red")) {
            mRed = (RedEvent) getIntent().getSerializableExtra("red");
        }*/
        setAdapter();
    }

    private void setAdapter() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(2);
        mAdapter = new PagerAdapter(fragmentManager, this,mRed);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setViewPager(mViewPager);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }
}
