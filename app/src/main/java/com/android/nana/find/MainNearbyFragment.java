package com.android.nana.find;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;

import com.android.common.builder.FragmentBuilder;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.find.base.CloseEvent;
import com.android.nana.widget.BadgeRadioButton;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2018/9/28.
 */

public class MainNearbyFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    private BadgeRadioButton mListBtn, mMapBtn;
    private FragmentBuilder mFragmentBuilder;
    private RadioGroup mTabRg;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;

    public static MainNearbyFragment newInstance() {
        MainNearbyFragment fragment = new MainNearbyFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //可见的并且是初始化之后才加载
        if (isPrepared && isVisibleToUser) {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }


    @Override
    protected void initData() {

    }


    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_main_nearby;
    }

    @Override
    public void initView() {
        mTabRg = mContentView.findViewById(R.id.main_tab_rg_menu);
        mListBtn = mContentView.findViewById(R.id.list_btn);
        mMapBtn = mContentView.findViewById(R.id.map_btn);
        mFragmentBuilder = new FragmentBuilder(getActivity(), R.id.fl_content);
      //  mFragmentBuilder.registerFragement("附近的人", new NearbyFragment());
      //  mFragmentBuilder.registerFragement("地图", new MapFragment());
        mFragmentBuilder.switchFragment(0);
        //已经初始化
        isPrepared = true;
    }

    @Override
    public void bindEvent() {
        mListBtn.setOnClickListener(this);
        mMapBtn.setOnClickListener(this);
        mTabRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.list_btn:
                mFragmentBuilder.switchFragment(0);
                break;
            case R.id.map_btn:
                EventBus.getDefault().post(new CloseEvent());//隐藏发现头部
                mFragmentBuilder.switchFragment(1);
                break;
            default:
                break;
        }
    }



}
