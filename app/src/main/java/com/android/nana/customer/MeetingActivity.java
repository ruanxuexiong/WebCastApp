package com.android.nana.customer;

import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.MeetingAdapter;
import com.android.nana.bean.RecordEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/14.
 * 新见面记录
 */

public class MeetingActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {


    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private MeetingAdapter mAdapter;
    private MultipleStatusView mMultipleStatusView;
    private String uid;
    private int page = 1;

    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private ArrayList<RecordEntity> mItem = new ArrayList<>();

    @Override
    protected void bindViews() {
        setContentView(R.layout.meeting_record);
    }

    @Override
    protected void findViewById() {
        mIbBack = findViewById(R.id.common_btn_back);
        mTxtTitle = findViewById(R.id.common_txt_title);

        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);

        mTxtTitle.setText("见面记录");

        if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            mAdapter = new MeetingAdapter(MeetingActivity.this, mItem);
        }
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        if (NetWorkUtils.isNetworkConnected(MeetingActivity.this)) {
            mLayout.autoRefresh();
            //loadData(page);
        } else {
            mMultipleStatusView.noNetwork();
        }

    }

    @Override
    protected void setListener() {
        mIbBack.setOnClickListener(this);
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


    private void loadData(int page) {
        CustomerDbHelper.record(uid, page, new IOAuthCallBack() {
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
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (RecordEntity item : parseData(successJson)) {
                                if (!mItem.contains(item)) {
                                    mItem.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                mMultipleStatusView.empty();
                            }
                        }
                    } else {
                        ToastUtils.showToast(result.getString("description"));
                        mMultipleStatusView.error();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dismissProgressDialog();
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    private ArrayList<RecordEntity> parseData(String result) {//Gson 解析
        ArrayList<RecordEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                RecordEntity entity = gson.fromJson(data.optJSONObject(i).toString(), RecordEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {

                loadData(page);
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

                loadData(page);
            }
        }, 500);
    }
}
