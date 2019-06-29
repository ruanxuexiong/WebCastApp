package com.android.nana.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.FollowAdapter;
import com.android.nana.bean.FollowEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;


/**
 * Created by lenovo on 2017/9/4.
 */

public class FollowListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, FollowAdapter.FollowListener {


    private AppCompatTextView mBackTv;
    private TextView mTitleTv;
    private String mUserId;
    private String endTime = "";

    private FollowAdapter mAdapter;
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private MultipleStatusView mMultipleStatusView;
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private ArrayList<FollowEntity> mDataList = new ArrayList<>();

    private UserInfo mUserInfo;
    private int page = 1;
    private AppointmentModel mAppointmentModel;
    private String mFansNum;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_follow);

        mAppointmentModel = new AppointmentModel(FollowListActivity.this);
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(FollowListActivity.this, "userInfo", UserInfo.class);
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
        mTitleTv.setText("关注我的");
        mBackTv.setVisibility(View.VISIBLE);

        if (null != getIntent().getStringExtra("fansNum") && !"".equals(getIntent().getStringExtra("fansNum"))) {
            mFansNum = getIntent().getStringExtra("fansNum");


            View view = LayoutInflater.from(this).inflate(R.layout.followlist_footer,null);
            TextView mNumTv = view.findViewById(R.id.tv_num);
            mNumTv.setText(mFansNum+"位粉丝");
            mListView.addFooterView(view);
        }

        if (NetWorkUtils.isNetworkConnected(FollowListActivity.this)) {
            showProgressDialog("", "加载中...");
            mUserId = (String) SharedPreferencesUtils.getParameter(FollowListActivity.this, "userId", "");
            loadData(endTime, page);
        } else {
            mMultipleStatusView.noNetwork();
        }
        mAdapter = new FollowAdapter(FollowListActivity.this, mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mLayout.setOnRefreshListener(this);
        mBackTv.setOnClickListener(this);
    }

    private void loadData(String time, int page) {
        CustomerDbHelper.queryAttentionUserLists(1, page, time, mUserId, "0", "ATTENTION_MINE", new IOAuthCallBack() {
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
                            for (FollowEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    dismissProgressDialog();
                                    endTime = item.getAddTime();
                                    mDataList.add(item);
                                }
                            }
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (null != mPullToLoadmore) {
                                ToastUtils.showToast("暂无数据");
                            } else {
                                dismissProgressDialog();
                                mMultipleStatusView.empty();
                            }
                        }
                    } else if (result.getString("state").equals("-1")) {
                       // mMultipleStatusView.empty();
                        ToastUtils.showToast("暂无数据");
                        dismissProgressDialog();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                EventBus.getDefault().post(new MailEvent());
                FollowListActivity.this.finish();
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        endTime = "";
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(endTime, page);
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
                 loadData(endTime, page);
            }
        }, 500);
    }

    public ArrayList<FollowEntity> parseData(String result) {//Gson 解析
        ArrayList<FollowEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FollowEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FollowEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onVideoClick(View view) {
        FollowEntity mItem = mDataList.get((Integer) view.getTag());

        mAppointmentModel.init(mUserInfo.getId(), mItem.getUser().getId(), mUserInfo.getPayPassword());

        mAppointmentModel.doDialog(mItem.getUser().getMoney(), mItem.getUser().getUsername());
    }

    @Override
    public void onItemClick(View view) {
        FollowEntity mItem = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(FollowListActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mItem.getUser().getId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(View view) {//直呼
        FollowEntity mItem = mDataList.get((Integer) view.getTag());

        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", mItem.getUser().getId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(this.getPackageName());
        this.getApplicationContext().startActivity(intent);
    }
}
