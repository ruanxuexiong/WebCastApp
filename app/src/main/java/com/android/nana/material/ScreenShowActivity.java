package com.android.nana.material;

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
import com.android.nana.adapter.ScreenAdapter;
import com.android.nana.bean.ScreenEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/26.
 */

public class ScreenShowActivity extends BaseActivity implements View.OnClickListener, ScreenAdapter.ScreenListener, PullToRefreshLayout.OnRefreshListener {


    private String uid;
    private String cityid;
    private String sex;
    private String minStr;
    private String higStr;
    private String tags;
    private String jobId;
    private String lowsalary;
    private String hightsalary;

    private ImageButton mBackBtn;
    private TextView mTitleTv;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private ScreenAdapter mAdapter;
    private int page = 1;
    private String type;

    private ArrayList<ScreenEntity> mDataList = new ArrayList<>();
    private UserInfo mUserInfo;
    private AppointmentModel mAppointmentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppointmentModel = new AppointmentModel(ScreenShowActivity.this);
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(ScreenShowActivity.this, "userInfo", UserInfo.class);
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_screen_show_list);
    }

    @Override
    protected void findViewById() {
        mBackBtn = (ImageButton) findViewById(R.id.common_btn_back);
        mTitleTv = (TextView) findViewById(R.id.common_txt_title);

        mLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mListView = (PullableListView) findViewById(R.id.content_view);
        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);

        if (null != getIntent().getStringExtra("save")) {//普通模式
            type = getIntent().getStringExtra("save");
            uid = getIntent().getStringExtra("uid");
            cityid = getIntent().getStringExtra("cityid");
            sex = getIntent().getStringExtra("sex");
            minStr = getIntent().getStringExtra("minStr");
            higStr = getIntent().getStringExtra("higStr");
            tags = getIntent().getStringExtra("tags");
            if (NetWorkUtils.isNetworkConnected(ScreenShowActivity.this)) {
                mMultipleStatusView.loading();
                loadData(page);
            } else {
                mMultipleStatusView.noNetwork();
            }
        }

        if (null != getIntent().getStringExtra("hrSave")) {//hr模式
            type = getIntent().getStringExtra("hrSave");
            uid = getIntent().getStringExtra("uid");
            jobId = getIntent().getStringExtra("jobId");
            tags = getIntent().getStringExtra("tags");
            sex = getIntent().getStringExtra("sex");
            cityid = getIntent().getStringExtra("cityid");
            lowsalary = getIntent().getStringExtra("mLowStr");
            hightsalary = getIntent().getStringExtra("mTopStr");
            if (NetWorkUtils.isNetworkConnected(ScreenShowActivity.this)) {
                mMultipleStatusView.loading();
                loadHrData(page);
            } else {
                mMultipleStatusView.noNetwork();
            }
        }

        mAdapter = new ScreenAdapter(ScreenShowActivity.this, mDataList, ScreenShowActivity.this);
        mListView.setAdapter(mAdapter);
    }

    private void loadHrData(int page) {
        WebCastDbHelper.searchHrFilterUser(uid, cityid, sex, jobId, tags, lowsalary, hightsalary, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
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
                            for (ScreenEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
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

    private void loadData(int page) {//普通模式
        WebCastDbHelper.searchFilterUser(uid, cityid, sex, minStr, higStr, tags, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
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
                            for (ScreenEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
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
    protected void init() {
        mTitleTv.setText("筛选");
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
        }
    }

    public ArrayList<ScreenEntity> parseData(String result) {//Gson 解析
        ArrayList<ScreenEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ScreenEntity entity = gson.fromJson(data.optJSONObject(i).toString(), ScreenEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    public void onMakeClick(View view) {
        ScreenEntity mItem = mDataList.get((Integer) view.getTag());
        mAppointmentModel.init(mUserInfo.getId(), mItem.getId(), mUserInfo.getPayPassword());
        mAppointmentModel.doDialog(mItem.getMoney(), mItem.getUsername());
    }

    @Override
    public void onContentClick(View view) {
        Intent intent = new Intent(ScreenShowActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getId());
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        if ("save".equals(type)) {
            new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                @Override
                public void run() {
                    loadData(page);
                }
            }, 500);
        } else if ("hrSave".equals(type)) {
            loadHrData(page);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;

        if ("save".equals(type)) {
            new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                @Override
                public void run() {
                    loadData(page);
                }
            }, 500);
        } else if ("hrSave".equals(type)) {
            loadHrData(page);
        }
    }
}
