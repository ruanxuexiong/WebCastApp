package com.android.nana.mail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.NewFriendAdapter;
import com.android.nana.bean.NewFriendEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.eventBus.NewMsgEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.SwipeListLayout;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lenovo on 2017/9/12.
 */

public class NewFriendActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener, NewFriendAdapter.FriendListene {

    private String mid;
    private LinearLayout mSearchRl;
    private TextView mTitleTv, mBackTv;


    private SmartRefreshLayout mRefreshLayout;
    private ListView mListView;
    private boolean isLoadMore = false;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<NewFriendEntity> mDataList = new ArrayList();
    private Set<SwipeListLayout> sets = new HashSet();
    private NewFriendAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!EventBus.getDefault().isRegistered(NewFriendActivity.this)) {
            EventBus.getDefault().register(NewFriendActivity.this);
        }

    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_new_friend);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mSearchRl = findViewById(R.id.rl_search);

        mListView = findViewById(R.id.list_view);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);

    }

    @Override
    protected void init() {
        mTitleTv.setText("新的朋友");
        mBackTv.setVisibility(View.VISIBLE);

        mAdapter = new NewFriendAdapter(NewFriendActivity.this, mDataList, this);
        mListView.setAdapter(mAdapter);

        if (NetWorkUtils.isNetworkConnected(NewFriendActivity.this)) {
            if (null != getIntent().getStringExtra("mid")) {
                mMultipleStatusView.loading();
                mid = getIntent().getStringExtra("mid");
                loadData(mid);
            }
        } else {
            mMultipleStatusView.noNetwork();
        }

    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSearchRl.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }

    private void loadData(String mid) {

        HomeDbHelper.newFriends(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                Log.e("TAG", "success=" + successJson);
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (NewFriendEntity entity : parseData(successJson)) {
                                if (!mDataList.contains(entity)) {
                                    mDataList.add(entity);
                                }
                            }
                        }
                    } else {
                        if (isLoadMore) {
                            isLoadMore = false;
                            ToastUtils.showToast("暂无数据");
                            mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            mMultipleStatusView.noEmpty();
                            isLoadMore = false;
                        }
                    }

                    if (null != mAdapter) {
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
                mMultipleStatusView.noEmpty();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                EventBus.getDefault().post(new MailEvent());
                this.finish();
                break;
            case R.id.rl_search:
                Intent intentSearch = new Intent(NewFriendActivity.this, SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
        }
    }

    private ArrayList<NewFriendEntity> parseData(String result) {
        ArrayList<NewFriendEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                NewFriendEntity entity = gson.fromJson(data.optJSONObject(i).toString(), NewFriendEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onItemClick(View view) {
        NewFriendEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(NewFriendActivity.this, EditDataActivity.class);
        intent.putExtra("newFriend", "new");
        intent.putExtra("uid", item.getId());

        if ("".equals(item.getMessage())) {
            intent.putExtra("msg", "申请加你为好友");
        } else {
            intent.putExtra("msg", item.getMessage());
        }
        intent.putExtra("follow", item.getFollow());
        intent.putExtra("thisUid", mid);
        startActivity(intent);
    }

    @Override
    public void onAgreeClick(View view) {//同意接口
        final NewFriendEntity item = mDataList.get((Integer) view.getTag());

        HomeDbHelper.passFriend(mid, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mRefreshLayout.autoRefresh();
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
    public void onDeleteItem(View view) {
        final NewFriendEntity item = mDataList.get((Integer) view.getTag());
        HomeDbHelper.deleteFriend(item.getLog_id(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                ResultRequestModel resultRequestModel = new ResultRequestModel(successJson);
                if (resultRequestModel.mIsSuccess) {
                    mDataList.remove(item);
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(resultRequestModel.mMessage);
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void setOnSwipeStatusListener(SwipeListLayout sll_main) {
        sll_main.setOnSwipeStatusListener(new NewFriendActivity.MyOnSlipStatusListener(sll_main));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(NewFriendActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpNewMsgDate(NewMsgEvent newmsg) {
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoadMore = true;

        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(mid);
                mRefreshLayout.finishLoadMore();
            }
        }, 500);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataList.clear();
                loadData(mid);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.setNoMoreData(false);
            }
        }, 500);
    }
}
