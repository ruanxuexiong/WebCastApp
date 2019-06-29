package com.android.nana.recruit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnDismissListener;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.job.adapter.ExamplePagerAdapter;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.recruit.bean.BeListEntity;
import com.android.nana.recruit.companyinfo.CompanyInfoActivity;
import com.android.nana.recruit.companyinfo.PostActivity;
import com.android.nana.recruit.merecruit.AuthCompanyActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.google.gson.Gson;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2018/3/8.
 */

public class RecruitFragment extends BaseRequestFragment implements View.OnClickListener, OnFilterDoneListener, DropDownMenu.DropDownMenuSearchListener, MultipleStatusView.OnActionListener, OnItemClickListener, OnDismissListener {


    private ViewPager mViewPager;
    private static final String[] CHANNELS = new String[]{"运营助理", "渠道推广"};
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ExamplePagerAdapter mExamplePagerAdapter = new ExamplePagerAdapter(mDataList);

    private TextView mAddJobTv;
    private TextView mSearchTv;
    private String mid;
    private int page = 0;
    private BeListEntity entity;
    private AlertView mAlertView;
    private String pass;//认证昨天
    private MultipleStatusView mMultipleStatusView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recruit;
    }

    @Override
    protected void findViewById() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAddJobTv = (TextView) findViewById(R.id.tv_add_job);
        mSearchTv = (TextView) findViewById(R.id.tv_search);

        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        initMagicIndicator();
        mViewPager.setAdapter(mExamplePagerAdapter);
        mMultipleStatusView.loading();
        initData();
    }

    private void initData() {
        RecruitDbHelper.beList(mid, String.valueOf(page), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        entity = parseData(successJson);
                        pass = entity.getPass();
                        if (null == entity.getCompany()) {//完善公司信息
                            pass = "3";
                        } else if (pass.equals("0")) {//未认证
                            mMultipleStatusView.noRecruit();
                        } else if (pass.equals("1")) {//等待认证
                            mMultipleStatusView.noRecruit();
                        } else if (pass.equals("-1")) {//未通过
                            mMultipleStatusView.noRecruit();
                        } else if (pass.equals("2")) {//已认证
                            mMultipleStatusView.noRecruit();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    protected void setListener() {
        mMultipleStatusView.setOnLoadListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_job:

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

    @Override
    public void onLoad(View view) {
        if (pass.equals("3")) {//完善资料
            showDialogs();
        } else if (pass.equals("0")) {//未认证
            showThe();
        } else if (pass.equals("1")) {//等待认证
            ToastUtils.showToast("等待认证中...");
        } else if (pass.equals("-1")) {//未通过
            showThe();
        } else {//认证通过后跳转发布职位
            Intent intent = new Intent(getActivity(), PostActivity.class);
            intent.putExtra("id", entity.getCompany().getId());
            intent.putExtra("mid", mid);
            intent.putExtra("name", entity.getCompany().getCompany());
            startActivity(intent);
        }
    }

    private void showDialogs() {
        mAlertView = new AlertView("提示", "请先完善公司信息，完善公司信息\n之后才能使用招聘功能", "取消", new String[]{"完善信息"}, null, getActivity(), AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }

    private void showThe() {
        mAlertView = new AlertView("提示", "需先通过公司认证，才能发布职位\n招聘信息", "取消", new String[]{"去认证"}, null, getActivity(), AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }

    public BeListEntity parseData(String result) {//Gson 解析
        BeListEntity entity = new BeListEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), BeListEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0 && pass.equals("3")) {
            startActivity(new Intent(getActivity(), CompanyInfoActivity.class));
        } else if (position == 0 && pass.equals("0")) {
            Intent intent = new Intent(getActivity(), AuthCompanyActivity.class);
            intent.putExtra("id", entity.getCompany().getId());
            intent.putExtra("mid", mid);
            startActivity(intent);
        }
    }

    @Override
    public void onDismiss(Object o) {

    }
}
