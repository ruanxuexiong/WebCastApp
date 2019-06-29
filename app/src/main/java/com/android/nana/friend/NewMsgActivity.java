package com.android.nana.friend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.MsgListEntity;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/10/26.
 */

public class NewMsgActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, MsgListAdapter.MsgAdapterListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<MsgListEntity.Msg> mDataList = new ArrayList<>();
    private MsgListAdapter mAdapter;
    private String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NetWorkUtils.isNetworkConnected(NewMsgActivity.this)) {
            mMultipleStatusView.loading();
            if (null != getIntent().getStringExtra("mid")) {
                mid = getIntent().getStringExtra("mid");
                loadData(mid);
            }
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    private void loadData(String mid) {
        FriendDbHelper.newMessages(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {

                mMultipleStatusView.dismiss();
                try {
                    if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                        mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                        mDataList.clear();
                    } else if (null != mPullToLoadmore) {
                        mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseMsgData(successJson).size() > 0) {
                            for (MsgListEntity.Msg item : parseMsgData(successJson)) {
                                mDataList.add(item);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_msg_list);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);
        mListView = findViewById(R.id.content_view);

        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("新消息");
        mBackTv.setVisibility(View.VISIBLE);
        mAction2.setVisibility(View.GONE);
        mAction2.setText("清空");

        mAdapter = new MsgListAdapter(NewMsgActivity.this, mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mAction2.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                onAction2Click();
                break;
            default:
                break;
        }
    }

    private void onAction2Click() {
    }

    public ArrayList<MsgListEntity.Msg> parseMsgData(String result) {

        ArrayList<MsgListEntity.Msg> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));

            JSONArray array = new JSONArray(data.getString("msg"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                MsgListEntity.Msg entity = gson.fromJson(array.optJSONObject(i).toString(), MsgListEntity.Msg.class);
                moment.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;

        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        }, 500);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        }, 500);
    }

    @Override
    public void onItemClick(View view) {
        MsgListEntity.Msg item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(NewMsgActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getArticleId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //阅读消息清空朋友圈列表
        EventBus.getDefault().post(new FriendEvent());
    }
}
