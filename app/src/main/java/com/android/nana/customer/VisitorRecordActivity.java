package com.android.nana.customer;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.VisitorAdapter;
import com.android.nana.bean.UserInfo;
import com.android.nana.bean.VisitorEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class VisitorRecordActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, View.OnClickListener, VisitorAdapter.VisitorListener {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private String mUserId;
    private String addTime;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;

    private VisitorAdapter mAdapter;
    private ArrayList<VisitorEntity> mDataList = new ArrayList<>();
    private UserInfo mUserInfo;
    private AppointmentModel mAppointmentModel;

    private LocalBroadcastManager manager = LocalBroadcastManager.getInstance(VisitorRecordActivity.this);
    private boolean isTalking = false;


    @Override
    protected void bindViews() {
        mUserId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        setContentView(R.layout.common_list);
    }

    @Override
    protected void findViewById() {

        mLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mListView = (PullableListView) findViewById(R.id.content_view);
        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("谁看过我的档案");

        mAdapter = new VisitorAdapter(VisitorRecordActivity.this, mDataList, VisitorRecordActivity.this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        mLayout.autoRefresh();

        mAppointmentModel = new AppointmentModel(VisitorRecordActivity.this);
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(VisitorRecordActivity.this, "userInfo", UserInfo.class);
    }

    private void loadData() {
        CustomerDbHelper.queryWatchHistoryLists(1, 1, addTime, mUserId, mUserId, new IOAuthCallBack() {
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
                        if (parseData(successJson).size() > 0) {
                            for (VisitorEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    addTime = item.getAddTime();
                                    mDataList.add(item);
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
    protected void setListener() {
        mIbBack.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);

        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                addTime = "";
                mMultipleStatusView.loading();
                loadData();
                mMultipleStatusView.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
        }
    }

    public ArrayList<VisitorEntity> parseData(String result) {//Gson 解析
        ArrayList<VisitorEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("users"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                VisitorEntity entity = gson.fromJson(data.optJSONObject(i).toString(), VisitorEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onMakeClick(View view) {

        VisitorEntity mItem = mDataList.get((Integer) view.getTag());

        mAppointmentModel.init(mUserInfo.getId(), mItem.getId(), mUserInfo.getPayPassword());

        mAppointmentModel.doDialog(mItem.getMoney(), mItem.getUsername());
    }

    @Override
    public void onContentClick(View view) {

        Intent intent = new Intent(VisitorRecordActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(View view) {
        String mid = (String) SharedPreferencesUtils.getParameter(VisitorRecordActivity.this, "userId", "");;
        VisitorEntity mItem = mDataList.get((Integer) view.getTag());

        if (isTalking) {
            ToastUtils.showToast("通话中，请稍后重试...");
        } else {
            HomeDbHelper.appointFriendsMeeting(mItem.getId(), mid, "", new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        if (result.getString("state").equals("0")) {
                        //    onDialVideo(data.getString("id"), data.getString("thisUid"), data.getString("thisname"), data.getString("userId"), data.getString("username"));
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
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;

        addTime = "";
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData();
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
                loadData();
            }
        }, 500);
    }
}
