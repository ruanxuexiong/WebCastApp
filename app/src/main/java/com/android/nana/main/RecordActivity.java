package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.RecordAdapter;
import com.android.nana.bean.HomeRecordEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.inquiry.ConversationListActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
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

import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/12/1.
 */

public class RecordActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, RecordAdapter.RecordListener {

    private TextView mBackTv;
    private TextView mTitleTv;


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
    private ArrayList<Message> mMessage = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(RecordActivity.this)) {
            EventBus.getDefault().register(RecordActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.fragment_record);
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
        if (null != getIntent().getParcelableArrayListExtra("msg") && getIntent().getParcelableArrayListExtra("msg").size() > 0) {
            mMessage = getIntent().getParcelableArrayListExtra("msg");
        }
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("联系记录");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetWorkUtils.isNetworkConnected(RecordActivity.this)) {
            mDataList.clear();
            mid = (String) SharedPreferencesUtils.getParameter(RecordActivity.this, "userId", "");
            showProgressDialog("", "加载中...");
            loadData(mid, page);
        } else {
            mMultipleStatusView.noNetwork();
        }
        mAdapter = new RecordAdapter(RecordActivity.this, mDataList, this,mMessage);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
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
    public void onItemClick(View view) {
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RecordActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", item);
        bundle.putSerializable("mid", mid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onVideoClick(View view) {
     /*   HomeRecordEntity item = mDataList.get((Integer) view.getTag());

        AppointmentModel mAppointmentModel = new AppointmentModel(RecordActivity.this);
        mAppointmentModel.init(mid, item.getUid(), "");
        mAppointmentModel.doDialog("", item.getUname());*/
    }

    @Override
    public void onCallClick(View view) {
   /*     HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", item.getUid());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(RecordActivity.this.getPackageName());
        startActivity(intent);*/
    }

    @Override
    public void onCommentClick(View view) {
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RecordActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", item);
        bundle.putSerializable("mid", mid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onPictureClick(View view) {
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RecordActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", item.getUid());
        startActivity(intent);
    }

    @Override
    public void onRefuse(View view) {//拒绝
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RecordActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", item);
        bundle.putSerializable("mid", mid);
        bundle.putSerializable("refuse", "refuse");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onInvalid(View view) {//失效
        HomeRecordEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RecordActivity.this, ConversationListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", item);
        bundle.putSerializable("mid", mid);
        bundle.putSerializable("invalid", "invalid");
        intent.putExtras(bundle);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(RecordActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateAppointment(UpdateEvent updateEvent) {
        loadData(mid, page);
    }
}
