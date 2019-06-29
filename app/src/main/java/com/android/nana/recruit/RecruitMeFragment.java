package com.android.nana.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnDismissListener;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.recruit.bean.MeRecruitEntity;
import com.android.nana.recruit.companyinfo.CompanyInfoActivity;
import com.android.nana.recruit.merecruit.PositionManagementActivity;
import com.android.nana.recruit.eventBus.MeRecruitEvent;
import com.android.nana.recruit.merecruit.AuthCompanyActivity;
import com.android.nana.recruit.merecruit.MeCompanyActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2018/3/8.
 */

public class RecruitMeFragment extends BaseRequestFragment implements View.OnClickListener, OnItemClickListener, OnDismissListener {

    private LinearLayout mAuthCompanyLl;
    private LinearLayout mCompanyLl;
    private String mid;
    private TextView mCommunicateNumTv;
    private TextView mBeNumTv;
    private AlertView mAlertView;
    private MeRecruitEntity entity;
    private LinearLayout mRecruitLl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(RecruitMeFragment.this)) {
            EventBus.getDefault().register(RecruitMeFragment.this);
        }
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me_recruit;
    }

    @Override
    protected void findViewById() {
        mAuthCompanyLl = (LinearLayout) findViewById(R.id.ll_authCompany);
        mCompanyLl = (LinearLayout) findViewById(R.id.ll_company);
        mBeNumTv = (TextView) findViewById(R.id.tv_be_num);
        mRecruitLl = (LinearLayout) findViewById(R.id.ll_recruit);
        mCommunicateNumTv = (TextView) findViewById(R.id.tv_communicate_num);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {
        mAuthCompanyLl.setOnClickListener(this);
        mCompanyLl.setOnClickListener(this);
        mRecruitLl.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_authCompany:
                if (entity.getCompany().getPass().equals("3")) {//完善资料
                    showDialogs();
                } else if (entity.getCompany().getPass().equals("0")) {//未认证
                    showThe();
                } else if (entity.getCompany().getPass().equals("1")) {//等待认证
                    ToastUtils.showToast("等待认证中...");
                } else if (entity.getCompany().getPass().equals("-1")) {//未通过
                    showThe();
                } else {//认证通过后跳转发布职
                    ToastUtils.showToast("公司信息认证已通过，请勿重复认证");
                }
                break;
            case R.id.ll_company:
                Intent meIntent = new Intent(getActivity(), MeCompanyActivity.class);
                meIntent.putExtra("id", entity.getCompany().getId());
                meIntent.putExtra("mid", mid);
                startActivity(meIntent);
                break;
            case R.id.ll_recruit:
                Intent intent = new Intent(getActivity(), PositionManagementActivity.class);
                intent.putExtra("id", entity.getCompany().getId());
                intent.putExtra("mid", mid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(RecruitMeFragment.this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRecruit(MeRecruitEvent event) {//更新招聘信息
        loadData();
    }

    private void loadData() {
        RecruitDbHelper.mySelf(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        entity = parseData(successJson);
                        mBeNumTv.setText(String.valueOf(entity.getInterest()));
                        mCommunicateNumTv.setText(String.valueOf(entity.getTalk()));
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

    private MeRecruitEntity parseData(String result) {//Gson 解析
        MeRecruitEntity entity = new MeRecruitEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), MeRecruitEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void showDialogs() {
        mAlertView = new AlertView("提示", "请先完善公司信息，完善公司信息\n之后才能使用招聘功能", "取消", new String[]{"完善信息"}, null, getActivity(), AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }

    private void showThe() {
        mAlertView = new AlertView("提示", "需先通过公司认证，才能发布职位\n招聘信息", "取消", new String[]{"去认证"}, null, getActivity(), AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }


    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0 && entity.getCompany().getPass().equals("3")) {
            startActivity(new Intent(getActivity(), CompanyInfoActivity.class));
        } else if (position == 0 && entity.getCompany().getPass().equals("0")) {
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
