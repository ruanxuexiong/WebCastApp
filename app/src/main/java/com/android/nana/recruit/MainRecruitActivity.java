package com.android.nana.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.nana.R;
import com.android.nana.job.MainJobActivity;
import com.android.nana.recruit.eventBus.MeRecruitEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/3/5.
 */

public class MainRecruitActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

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
        setContentView(R.layout.activity_main_recruit);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mTitleTv = findViewById(R.id.tv_title);
        mTabRg = findViewById(R.id.main_tab_rg_menu);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setText("我要求职");
        mTitleTv.setText("招聘");

        mFragmentBuilder = new FragmentBuilder(this, R.id.main_tab_content);
        mFragmentBuilder.registerFragement("牛人", new RecruitFragment());
        mFragmentBuilder.registerFragement("互动", new ChatFragment());
        mFragmentBuilder.registerFragement("我的招牌", new RecruitMeFragment());
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
                startActivity(new Intent(this, MainJobActivity.class));
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
                //更新我的招聘信息
                EventBus.getDefault().post(new MeRecruitEvent());
                mFragmentBuilder.switchFragment(2);
                break;
            default:
                break;
        }
    }
}
