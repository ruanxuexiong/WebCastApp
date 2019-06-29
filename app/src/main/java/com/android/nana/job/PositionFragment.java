package com.android.nana.job;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.nana.R;
import com.android.nana.job.adapter.ExamplePagerAdapter;
import com.android.nana.job.position.ManagerPositionActivity;
import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2018/3/5.
 */

public class PositionFragment extends BaseRequestFragment implements View.OnClickListener, OnFilterDoneListener, DropDownMenu.DropDownMenuSearchListener {

    private ViewPager mViewPager;
    private static final String[] CHANNELS = new String[]{"运营助理", "渠道推广"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);

    private TextView mAddJobTv;
    private TextView mSearchTv;

    //详情item === item_job

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_position;
    }

    @Override
    protected void findViewById() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAddJobTv = (TextView) findViewById(R.id.tv_add_job);
        mSearchTv = (TextView) findViewById(R.id.tv_search);
    }

    @Override
    protected void init() {
        initMagicIndicator();
        mViewPager.setAdapter(mExamplePagerAdapter);
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.white));
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(17);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.green_99));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.green));

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {//点击事件
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.green));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    protected void setListener() {
        mAddJobTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_add_job:
                startActivity(new Intent(getActivity(), ManagerPositionActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {

    }

    @Override
    public void onClickCity() {

    }

    @Override
    public void onSeniorClick() {

    }
}
