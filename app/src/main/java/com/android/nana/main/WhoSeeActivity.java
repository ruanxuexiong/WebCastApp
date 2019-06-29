package com.android.nana.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.inquiry.ConversationListActivity;
import com.android.nana.listener.MainListener;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.RecordBean;
import com.android.nana.material.WantBean;
import com.android.nana.material.WhoSeeAdapter;
import com.android.nana.util.Constant;
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
import io.rong.callkit.RongCallKit;
import io.rong.callkit.RongVoIPIntent;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/12/1.
 */

public class WhoSeeActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, WhoSeeAdapter.WhoSeeListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private String mUserId;
    private WhoSeeAdapter mAdapter;
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private int type;

    private WantBean item;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private int page = 1;
    private ArrayList<WantBean> mDataList = new ArrayList<>();
    private ArrayList<Message> mMessage = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(WhoSeeActivity.this)) {
            EventBus.getDefault().register(WhoSeeActivity.this);
        }

    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.appointment);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);

        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        type = Constant.Appointment.Other_me;
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("谁联系我");

        if (null != getIntent().getParcelableArrayListExtra("msg") && getIntent().getParcelableArrayListExtra("msg").size() > 0) {
            mMessage = getIntent().getParcelableArrayListExtra("msg");
        }

        MainListener.getInstance().mOnMessageListener = new MainListener.OnMessageListener() {
            @Override
            public void refersh(Message message) {
                if (message.getReceivedStatus().isRead()) {
                    mMessage.add(message);
                }
            }
        };
        mAdapter = new WhoSeeAdapter(WhoSeeActivity.this, mDataList, WhoSeeActivity.this, mMessage);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                page = 1;
                mMultipleStatusView.loading();
                loadData(mUserId, page);
                mMultipleStatusView.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetWorkUtils.isNetworkConnected(WhoSeeActivity.this)) {
            mUserId = (String) SharedPreferencesUtils.getParameter(WhoSeeActivity.this, "userId", "");
            mLayout.autoRefresh();
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

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
        EventBus.getDefault().unregister(WhoSeeActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(UpdateEvent updateEvent) {
        loadData(mUserId, page);
    }

    @Override
    public void onVideoClick(View view) {

    }

    @Override
    public void onRefuseClick(View view) {//拒绝
        WantBean item = mDataList.get((Integer) view.getTag());

        HomeDbHelper.disAgree(mUserId, item.getId(), item.getZx_user().getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mLayout.autoRefresh();
                        ToastUtils.showToast("拒绝成功");

                        if (MainListener.getInstance().mOnMessageRefreshListener != null) {//更新我要咨询谁
                            MainListener.getInstance().mOnMessageRefreshListener.refersh();
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
    public void onAgreeClick(final View view) {//同意

        WantBean item = mDataList.get((Integer) view.getTag());

        HomeDbHelper.agree(mUserId, item.getId(), item.getZx_user().getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mLayout.autoRefresh();
                        if (MainListener.getInstance().mOnMessageRefreshListener != null) {//更新我要咨询谁
                            MainListener.getInstance().mOnMessageRefreshListener.refersh();
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
    public void onOpenItemClick(View view) {
        item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(WhoSeeActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("want", item);
        bundle.putString("whoSee", "whoSee");
        bundle.putParcelableArrayList("msg", mMessage);
        bundle.putString("type", "0");//卖家为1
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(View view) {
        WantBean item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(WhoSeeActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", item.getThisUserId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(View view) {
        final WantBean item = mDataList.get((Integer) view.getTag());
        HomeDbHelper.startVideo(mUserId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        TimeBean bean = parseTimeData(successJson);
                        startSingleCall(WhoSeeActivity.this, item.getZx_user().getId(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, item.getTalkId());
                    } else {
                        ToastUtils.showToast("对方余额不足或视频无效，请稍后再试!");
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
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        switch (type) {
            case 1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, page);
                    }
                }, 500);
                break;
            case -1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, page);
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

                        loadData(mUserId, page);
                    }
                }, 500);
                break;
            case -1:
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {

                        loadData(mUserId, page);
                    }
                }, 500);
                break;
        }
    }


    private void loadData(String mid, int page) {
        CustomerDbHelper.zxToMe(mid, page, new IOAuthCallBack() {
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
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseWhoData(successJson).size() > 0) {
                            for (WantBean item : parseWhoData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                                mMultipleStatusView.dismiss();
                                mAdapter.notifyDataSetChanged();
                            }

                            String want = (String) SharedPreferencesUtils.getParameter(getApplicationContext(), "who", "");
                            if ("1".equals(want)) {
                                FragmentManager fm = getSupportFragmentManager();//成为专家
                                WantFragment dialog = WantFragment.newInstance("1");
                                dialog.show(fm, "fragment_bottom_dialog");
                            }
                        } else {
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                mMultipleStatusView.onMsg();
                            }
                            String want = (String) SharedPreferencesUtils.getParameter(getApplicationContext(), "who", "");
                            if ("1".equals(want)) {
                                FragmentManager fm = getSupportFragmentManager();//成为专家
                                WantFragment dialog = WantFragment.newInstance("1");
                                dialog.show(fm, "fragment_bottom_dialog");
                            }
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

    public ArrayList<WantBean> parseWhoData(String result) {//Gson 解析
        ArrayList<WantBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray jsonArray = new JSONArray(data.getString("zxToMe"));
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                WantBean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), WantBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    public TimeBean parseTimeData(String result) {//Gson 解析
        TimeBean detail = new TimeBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), TimeBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    private void startSingleCall(Context context, String targetId, RongCallKit.CallMediaType mediaType, TimeBean bean, String pageId) {
        if (RongCallKit.checkEnvironment(context, mediaType)) {

            String action = RongVoIPIntent.RONG_INTENT_ACTION_SINGLE_VIDEO;
            Intent intent = new Intent(action);
            Bundle bundle = new Bundle();
            bundle.putString("targetId", targetId);
            bundle.putString("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            bundle.putString("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());

            bundle.putString("charge", bean.getCharge());// 每分钟收费
            bundle.putString("timeLeft", bean.getTimeLeft());// 剩余通话时长（分钟）
            bundle.putString("isFree", bean.getIsFree());//前三分钟是否免费
            bundle.putString("type", "0");//卖家为0
            bundle.putString("pageId", pageId);
            intent.putExtras(bundle);
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
        }
    }
}
