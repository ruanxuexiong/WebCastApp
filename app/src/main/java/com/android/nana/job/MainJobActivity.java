package com.android.nana.job;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.nana.R;
import com.android.nana.recruit.MainRecruitActivity;

/**
 * Created by lenovo on 2018/3/5.
 */

public class MainJobActivity extends BaseActivity implements View.OnClickListener ,RadioGroup.OnCheckedChangeListener {


    private TextView mBackTv;
    private TextView mRight2Tv;
    private TextView mTitleTv;
    private RadioGroup mTabRg;
    private FragmentBuilder mFragmentBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_main_job);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mTabRg = findViewById(R.id.main_tab_rg_menu);
        mTitleTv = findViewById(R.id.tv_title);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("我要招人");
        mTitleTv.setText("求职");

        mFragmentBuilder = new FragmentBuilder(this, R.id.main_tab_content);
        mFragmentBuilder.registerFragement("职位", new PositionFragment());
        mFragmentBuilder.registerFragement("公司", new CompanyFragment());
        mFragmentBuilder.registerFragement("互动", new InteractFragment());
        mFragmentBuilder.registerFragement("求职管理", new JobManFragment());
        mFragmentBuilder.switchFragment(0);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mTabRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                this.finish();
                startActivity(new Intent(this, MainRecruitActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.main_tab_rb_1:
                mFragmentBuilder.switchFragment(0);
                break;
            case R.id.main_tab_rb_2:
                mFragmentBuilder.switchFragment(1);
                break;
            case R.id.main_tab_rb_3:
                mFragmentBuilder.switchFragment(2);
                break;
            case R.id.main_tab_rb_4:
                mFragmentBuilder.switchFragment(3);
                break;
            default:
                break;
        }
    }
}
