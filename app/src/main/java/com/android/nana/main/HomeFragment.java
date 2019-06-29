package com.android.nana.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.HomePagerAdapter;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.MainMsgEvent;
import com.android.nana.eventBus.WantEvent;
import com.android.nana.eventBus.WhoSeeEvent;
import com.android.nana.material.EditUserInfoActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.StateButton;
import com.flyco.tablayout.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/8/29.
 */

public class HomeFragment extends BaseRequestFragment implements View.OnClickListener {

    private HomePagerAdapter mPageAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;
    private RelativeLayout mSearchRl;
    private String mid;//当前用户id;
    public int whoseeComment = 0;//谁要见我num
    public int wantComment = 0;//我要见谁num

    private Dialog mPerfectDialog;
    private View mPerfectView;
    private TextView mPerfectContentTv;
    private StateButton mPerfectBtn;
    private ImageButton mCloseBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(HomeFragment.this)) {
            EventBus.getDefault().register(HomeFragment.this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");

        CustomerDbHelper.checkIsAlert(mid, mIOCheckIsAlert);//当前用户id身份证是否认证
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void findViewById() {
        mTabLayout = (SlidingTabLayout) findViewById(R.id.layout_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mSearchRl = (RelativeLayout) findViewById(R.id.rl_search);

        mPerfectView = LayoutInflater.from(getActivity()).inflate(R.layout.home_perfect_dialog, null);
        mPerfectBtn = (StateButton) mPerfectView.findViewById(R.id.btn_perfect);
        mPerfectContentTv = (TextView) mPerfectView.findViewById(R.id.tv_content);
        mCloseBtn = (ImageButton) mPerfectView.findViewById(R.id.close_iv);
        mPerfectDialog = new AlertDialog.Builder(getActivity()).create();
        mPerfectDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void init() {
        setAdapter();
    }

    @Override
    protected void setListener() {
        mSearchRl.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
            case R.id.close_iv:
                mPerfectDialog.dismiss();
                break;
        }
    }

    private void setAdapter() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(3);
        mPageAdapter = new HomePagerAdapter(fragmentManager, getActivity(), mid);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(HomeFragment.this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateMsg(WhoSeeEvent whoSeeEvent) {//谁要见我msg

        whoseeComment = Integer.valueOf(whoSeeEvent.num);
        if (whoseeComment > 99) {
            whoseeComment = 99;
        }

        if (whoseeComment > 0) {
            mTabLayout.showMsg(1, whoseeComment);
            mTabLayout.setMsgMargin(1, 2, 10);
            EventBus.getDefault().post(new MainMsgEvent(whoseeComment, wantComment));
        }else {
            mTabLayout.hideMsg(1);
            EventBus.getDefault().post(new MainMsgEvent(whoseeComment, wantComment));
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateWantMsg(WantEvent wantEvent) {//我要见谁msg
        wantComment = Integer.valueOf(wantEvent.num);
        if (wantComment > 99) {
            wantComment = 99;
        }

        if (wantComment > 0) {
            mTabLayout.showMsg(2, wantComment);
            mTabLayout.setMsgMargin(2, 2, 10);
            EventBus.getDefault().post(new MainMsgEvent(whoseeComment, wantComment));
        }else {
            mTabLayout.hideMsg(2);
            EventBus.getDefault().post(new MainMsgEvent(whoseeComment, wantComment));
        }

    }

    private IOAuthCallBack mIOCheckIsAlert = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {

            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject result = new JSONObject(jsonObject.getString("result"));
                if (result.getString("state").equals("0")) {
                    isAlert();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {

        }
    };

    //当前用户是否认证

    private void isAlert() {
        CustomerDbHelper.checkUser(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("-1")) {//未完善资料
                        perfect(result.getString("description"));
                    } else if (result.getString("state").equals("-2")) {//未认证
                        certified(result.getString("description"));
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

    private void perfect(String description) {//未完善
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUser = new Intent(getActivity(), EditUserInfoActivity.class);
                Bundle bundleUser = new Bundle();
                bundleUser.putString("uid", mid);
                intentUser.putExtras(bundleUser);
                startActivity(intentUser);
            }
        });
    }

    private void certified(String description) {//未认证
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setText("马上认证");
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
            }
        });
    }
}
