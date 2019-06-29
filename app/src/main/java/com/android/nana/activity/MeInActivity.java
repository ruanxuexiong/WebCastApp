package com.android.nana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.MeInAdapter;
import com.android.nana.bean.MeStartEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
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
 * Created by lenovo on 2017/9/6.
 * 我参加的活动
 */

public class MeInActivity extends BaseActivity implements View.OnClickListener, MeInAdapter.MeInListener, PullToRefreshLayout.OnRefreshListener {

    private ImageButton mBackBtn;
    private TextView mTitleTv;
    private int page = 1;
    private String mid;

    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;

    private MeInAdapter mAdapterIn;
    private ArrayList<MeStartEntity> mDataInList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NetWorkUtils.isNetworkConnected(MeInActivity.this)) {
            if (null != getIntent().getStringExtra("mid")) {
                showProgressDialog("", "加载中...");
                mid = getIntent().getStringExtra("mid");
                loadInData(mid, page);
            }
        } else {
            mMultipleStatusView.noNetwork();
        }

        if (!EventBus.getDefault().isRegistered(MeInActivity.this)) {
            EventBus.getDefault().register(MeInActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_in);
        mListView = findViewById(R.id.content_view);
        mLayout = findViewById(R.id.refresh_view);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我参加的");
        mAdapterIn = new MeInAdapter(MeInActivity.this, mDataInList, this);
        mListView.setAdapter(mAdapterIn);
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                this.finish();
                break;
        }
    }


    private void loadInData(String id, int page) {
        HomeDbHelper.followActivities(id, "2", page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataInList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            dismissProgressDialog();
                            for (MeStartEntity item : parseData(successJson)) {
                                if (!mDataInList.contains(item)) {
                                    mDataInList.add(item);
                                }
                            }
                            mMultipleStatusView.dismiss();
                            mAdapterIn.notifyDataSetChanged();
                        } else {
                            dismissProgressDialog();
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                mMultipleStatusView.empty();
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
            }
        });
    }

    public ArrayList<MeStartEntity> parseData(String result) {//Gson 解析
        ArrayList<MeStartEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                MeStartEntity entity = gson.fromJson(data.optJSONObject(i).toString(), MeStartEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onMeItemClick(View view) {
        Intent intent = new Intent(MeInActivity.this, DetailsActivity.class);
        intent.putExtra("id", mDataInList.get((Integer) view.getTag()).getId());
        intent.putExtra("isJoin", "isJoin");
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;

        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadInData(mid, page);
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
                loadInData(mid, page);
            }
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(MessageEvent messageEvent) {
        if (messageEvent.message.equals("updateData")) {
            loadInData(mid, page);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MeInActivity.this);
    }
}
