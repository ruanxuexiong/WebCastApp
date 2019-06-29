package com.android.nana.material;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.android.common.base.BaseRequestFragment;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.eventBus.WhoSeeEvent;
import com.android.nana.loading.MultipleStatusView;
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

/**
 * Created by THINK on 2017/7/3.
 */

public class WhoSeeFragment extends BaseRequestFragment implements PullToRefreshLayout.OnRefreshListener, WhoSeeAdapter.WhoSeeListener {

    private String mUserId;
    private WhoSeeAdapter mAdapter;
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private int type;


    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private ArrayList<RecordBean> mItem = new ArrayList<>();

    private MultipleStatusView mMultipleStatusView;
    private int page = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(WhoSeeFragment.this)) {
            EventBus.getDefault().register(WhoSeeFragment.this);
        }
    }

    public static WhoSeeFragment newInstance(int type, String mid) {
        WhoSeeFragment fragment = new WhoSeeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("mid", mid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.appointment;
    }

    @Override
    protected void findViewById() {
        mLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mListView = (PullableListView) findViewById(R.id.content_view);
        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);

        Bundle args = getArguments();
        if (null != args) {
            type = args.getInt("type");
           // mAdapter = new WhoSeeAdapter(getContext(), mItem, WhoSeeFragment.this, type);
        }

        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        if (NetWorkUtils.isNetworkConnected(getContext())) {
            Bundle args = getArguments();
            if (null != args) {
                mUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
                mLayout.autoRefresh();
            }
        } else {
            mMultipleStatusView.noNetwork();
        }
    }


    @Override
    protected void setListener() {
        mLayout.setOnRefreshListener(this);
        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                page = 1;
                mMultipleStatusView.loading();
                loadData(mUserId, 1, page);
                mMultipleStatusView.dismiss();
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        switch (type) {
            case 1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, 1, page);
                    }
                }, 500);
                break;
            case -1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, -1, page);
                    }
                }, 500);
                break;
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;
        switch (type) {
            case 1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, 1, page);
                    }
                }, 500);
                break;
            case -1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, -1, page);
                    }
                }, 500);
                break;
        }
    }


    private void loadData(final String mid, final int type, int page) {
        //（1=预约见面   2=马上视频 3=旧的预约模式）
        CustomerDbHelper.seedMimeRecord(mid, String.valueOf(type), page, "3", getActivity(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mItem.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                    if (jsonobject1.getString("state").equals("0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {

                                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                                    String num = jsonobject1.getString("description");
                                    if (Integer.valueOf(num) > 0) {
                                        EventBus.getDefault().post(new WhoSeeEvent(num));
                                    } else {
                                        EventBus.getDefault().post(new WhoSeeEvent("0"));
                                    }
                                }

                                if (parseData(successJson).size() > 0) {
                                    for (RecordBean item : parseData(successJson)) {
                                        if (!mItem.contains(item)) {
                                            mItem.add(item);
                                        }
                                    }
                                    mMultipleStatusView.dismiss();
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    if (null != mPullToLoadmore) {
                                        ToastUtils.showToast("暂无数据");
                                    } else {
                                        mMultipleStatusView.onMsg();
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
                mMultipleStatusView.error();
            }
        });
    }

    @Override
    public void onVideoClick(View view) {
    }

    @Override
    public void onRefuseClick(View view) {//拒绝


        HomeDbHelper.refuse(mUserId, mItem.get((Integer) view.getTag()).getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                    if (jsonObject1.getString("state").equals("0")) {
                        mLayout.autoRefresh();
                        ToastUtils.showToast("拒绝成功");
                        // loadData(mUserId, 1, 1);
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

    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
    private boolean isTalking = false;

    @Override
    public void onAgreeClick(final View view) {//同意或视频
/*
        if ("4".equals(mItem.get((Integer) view.getTag()).getStatus())) {
            manager.registerReceiver(mTalkingReceiver, new IntentFilter(MtcCallConstants.MtcCallTalkingNotification));
            manager.registerReceiver(mTermedReceiver, new IntentFilter(MtcCallConstants.MtcCallTermedNotification));
            manager.registerReceiver(mDidTermReceiver, new IntentFilter(MtcCallConstants.MtcCallDidTermNotification));

            if (isTalking) {
                ToastUtils.showToast("通话中，请稍后重试...");
            } else {
                RecordBean bean = mItem.get((Integer) view.getTag());
                isSheckValidVideo(mItem.get((Integer) view.getTag()).getUserId(), mItem.get((Integer) view.getTag()).getId(), bean);
            }
        } else {
            DialogHelper.agreeAlert(getActivity(), "提示", "点击确认，您可以马上或在一个小时内点击“视频”呼叫对方；如您现在不方便，请点取消，您可以在对方发起邀约的24小时内点击同意。", "确认", "取消", new DialogHelper.OnAlertConfirmClick() {
                @Override
                public void OnClick(String content) {

                }

                @Override
                public void OnClick() {

                    HomeDbHelper.agree(mUserId, mItem.get((Integer) view.getTag()).getId(), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                                if (jsonObject1.getString("state").equals("0")) {
                                    loadData(mUserId, 1, page);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {

                            Log.e("返回值===", failueJson);
                        }
                    });
                }
            }, null);
        }*/
    }

    @Override
    public void onOpenItemClick(View view) {
        RecordBean item = mItem.get((Integer) view.getTag());

        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", item.getThisUserId());
        startActivity(intent);
    }

    @Override
    public void onHeadClick(View view) {

    }

    @Override
    public void onCallClick(View view) {

    }


    private void isSheckValidVideo(String thisUserId, final String id, final RecordBean bean) {//视频

        HomeDbHelper.checkValidVideo(thisUserId, id, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                    if (jsonobject1.getString("state").equals("0")) {
                        pushMessage(bean, id);
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

    private void pushMessage(final RecordBean item, final String id) {

        HomeDbHelper.pushXGMessage(item.getThisUserId(), item.getUserId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
              //  onDialVideo(id, item.getThisUserId(), item.getThisname(), item.getUsername(), id);
            }

            @Override
            public void getFailue(String failueJson) {

                ToastUtils.showToast("请求失败，请稍后重试!");

            }
        });
    }

  /*  public void onDialVideo(String directSeedingRecordId, String number, String displayName, String peerDisplayName, String recordId) {
        Map<String, String> map = new HashMap<>();
        map.put("MtcCallInfoServerUserDataKey", directSeedingRecordId);

        WebCastSevice.mDirectSeedingRecordId = directSeedingRecordId;
        WebCastSevice.userid = number;
        WebCastSevice.recordId = recordId;

        MtcCallDelegate.call("FacethreeZhibo2017" + number, "三分钟来电：" + displayName, peerDisplayName, true, map);
    }*/


    public ArrayList<RecordBean> parseData(String result) {//Gson 解析
        ArrayList<RecordBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                RecordBean entity = gson.fromJson(data.optJSONObject(i).toString(), RecordBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(WhoSeeFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(UpdateEvent updateEvent) {
        loadData(mUserId, 1, page);
    }
}
