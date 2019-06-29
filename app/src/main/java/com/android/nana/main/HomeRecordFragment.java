package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.adapter.RecordAdapter;
import com.android.nana.bean.HomeRecordEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by lenovo on 2017/8/29.
 */

public class HomeRecordFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener, RecordAdapter.RecordListener {

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;

    private String mid;
    private int page = 1;
    private RecordAdapter mAdapter;
    private ArrayList<HomeRecordEntity> mDataList = new ArrayList<>();
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
    private boolean isTalking = false;

    public static HomeRecordFragment newInstance() {
        HomeRecordFragment fragment = new HomeRecordFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(HomeRecordFragment.this)) {
            EventBus.getDefault().register(HomeRecordFragment.this);
        }
    }

    @Override
    protected void initData() {
        if (NetWorkUtils.isNetworkConnected(getContext())) {
            mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
            showProgressDialog("", "加载中...");
            mLayout.autoRefresh();
        } else {
            mMultipleStatusView.noNetwork();
        }

     //   mAdapter = new RecordAdapter(getContext(), mDataList, this);
       // mListView.setAdapter(mAdapter);
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    public void initView() {
        mLayout = mContentView.findViewById(R.id.refresh_view);
        mListView = mContentView.findViewById(R.id.content_view);
        mMultipleStatusView = mContentView.findViewById(R.id.multiple_status_view);
    }


    private void loadData(String mid, int page) {
        HomeDbHelper.meetingRecords(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        if (parseData(successJson).size() > 0) {
                            dismissProgressDialog();
                            for (HomeRecordEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            dismissProgressDialog();
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                mMultipleStatusView.onMsg();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {

                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }

    @Override
    public void bindEvent() {
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {

                loadData(mid, page);
            }
        }, 500);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {

                loadData(mid, page);
            }
        }, 500);
    }

    private ArrayList<HomeRecordEntity> parseData(String result) {//Gson 解析
        ArrayList<HomeRecordEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray records = new JSONArray(data.getString("records"));
            Gson gson = new Gson();
            for (int i = 0; i < records.length(); i++) {
                HomeRecordEntity entity = gson.fromJson(records.optJSONObject(i).toString(), HomeRecordEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onItemClick(View view) {
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", item.getUid());
        startActivity(intent);
    }


    @Override
    public void onVideoClick(View view) {
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());

        AppointmentModel mAppointmentModel = new AppointmentModel(getActivity());
        mAppointmentModel.init(mid, item.getUid(), "");
        mAppointmentModel.doDialog("", item.getUname());
    }

    @Override
    public void onCallClick(View view) {//直呼

        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", item.getUid());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getActivity().getPackageName());
        startActivity(intent);
    }

    @Override
    public void onCommentClick(View view) {

    }

    @Override
    public void onPictureClick(View view) {

    }

    @Override
    public void onRefuse(View view) {

    }

    @Override
    public void onInvalid(View view) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(HomeRecordFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateAppointment(UpdateEvent updateEvent) {
        loadData(mid, page);
    }


}
