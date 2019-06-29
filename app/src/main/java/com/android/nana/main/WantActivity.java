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
import com.android.nana.material.RecordAdapter;
import com.android.nana.material.WantBean;
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

public class WantActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, RecordAdapter.RecordListener {

    private TextView mBackTv;
    private TextView mTitleTv;

    private String mUserId;
    private int type = 1;

    private RecordAdapter mAdapter;
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;

    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;

    private ArrayList<WantBean> mItem = new ArrayList<>();
    private MultipleStatusView mMultipleStatusView;
    private int page = 1;
    private ArrayList<Message> mMessage = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(WantActivity.this)) {
            EventBus.getDefault().register(WantActivity.this);
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

    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("我联系谁");
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

        mAdapter = new RecordAdapter(WantActivity.this, mItem, this, mMessage);
        mListView.setAdapter(mAdapter);

        String want = (String) SharedPreferencesUtils.getParameter(getApplicationContext(), "want", "");
        if ("2".equals(want)) {
            FragmentManager fm = getSupportFragmentManager();
            WantFragment dialog = WantFragment.newInstance("2");
            dialog.show(fm, "fragment_bottom_dialog");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetWorkUtils.isNetworkConnected(WantActivity.this)) {
            mUserId = (String) SharedPreferencesUtils.getParameter(WantActivity.this, "userId", "");
            mLayout.autoRefresh();
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                mMultipleStatusView.loading();
                loadSeeOther(mUserId, 1);
                mMultipleStatusView.dismiss();
            }
        });
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

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;

        switch (type) {
            case 1:
                if (null != mUserId && !"".equals(mUserId)) {
                    new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                        @Override
                        public void run() {
                            loadSeeOther(mUserId, page);
                        }
                    }, 500);
                }
                break;
            case -1:
                if (null != mUserId && !"".equals(mUserId)) {
                    new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                        @Override
                        public void run() {
                            loadSeeOther(mUserId, page);
                        }
                    }, 500);
                }
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
                if (null != mUserId && !"".equals(mUserId)) {
                    new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                        @Override
                        public void run() {
                            loadSeeOther(mUserId, page);
                        }
                    }, 500);
                }
                break;
            case -1:
                if (null != mUserId && !"".equals(mUserId)) {
                    new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                        @Override
                        public void run() {
                            loadSeeOther(mUserId, page);
                        }
                    }, 500);
                }
                break;
        }
    }

    private void loadSeeOther(String mId, int page) {

        CustomerDbHelper.zxToOther(mId, page, new IOAuthCallBack() {
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
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                    if (jsonObject1.getString("state").equals("0")) {

                        if (parseData(successJson).size() > 0) {
                            for (WantBean item : parseData(successJson)) {
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

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    public ArrayList<WantBean> parseData(String result) {//Gson 解析
        ArrayList<WantBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray jsonArray = new JSONArray(data.getString("zxToOther"));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(WantActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateAppointment(UpdateEvent updateEvent) {
        loadSeeOther(mUserId, page);
    }

    @Override
    public void onVideoClick(View view) {
        final WantBean item = mItem.get((Integer) view.getTag());

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
                        startSingleCall(WantActivity.this, item.getZx_user().getId(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, item.getTalkId());
                    }else {
                        ToastUtils.showToast("余额不足或视频无效，请稍后再试!");
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
            bundle.putString("type", "1");//买家为1
            bundle.putString("pageId", pageId);
            intent.putExtras(bundle);
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
        }
    }

    @Override
    public void onOpenItemClick(View view) {
        WantBean item = mItem.get((Integer) view.getTag());
        Intent intent = new Intent(WantActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("want", item);
        bundle.putParcelableArrayList("msg", mMessage);
        bundle.putString("type", "1");//买家为1
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onHeadClick(View view) {//点击头像
        WantBean item = mItem.get((Integer) view.getTag());
        Intent intent = new Intent(WantActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", item.getUserId());
        startActivity(intent);
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
}
