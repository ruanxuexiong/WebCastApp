package com.android.nana.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.AddWorkActivity;
import com.android.nana.material.MeDataBean;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/7/31.
 */

public class EditWorkActivity extends BaseActivity implements View.OnClickListener, WorkExpertAdapter.WorkExpertListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private ImageView mAddWorkIv;
    private WorkExpertAdapter mAdapter;
    private String mUserId;
    private boolean isUser = false;//判断是否从用户界面点击
    private ListView mListView;
    private ArrayList<MeDataBean.WorkHistorys> mDataList = new ArrayList<>();
    private MultipleStatusView mMultipleStatusView;
    private RelativeLayout mShowRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_work);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mAddWorkIv = findViewById(R.id.iv_add_work);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mShowRl = findViewById(R.id.rl_show);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("工作经历");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != getIntent().getStringExtra("uid")) {
            mUserId = getIntent().getStringExtra("uid");
            isUser = true;
            mShowRl.setVisibility(View.GONE);
            if (NetWorkUtils.isNetworkConnected(EditWorkActivity.this)) {
                mMultipleStatusView.loading();
                loadData(mUserId);
            } else {
                mMultipleStatusView.noNetwork();
            }
        } else {
            mUserId = (String) SharedPreferencesUtils.getParameter(EditWorkActivity.this, "userId", "");
            if (NetWorkUtils.isNetworkConnected(EditWorkActivity.this)) {
                mMultipleStatusView.loading();
                loadData(mUserId);
            } else {
                mMultipleStatusView.noNetwork();
            }
        }

        mAdapter = new WorkExpertAdapter(EditWorkActivity.this, mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    private void loadData(String mUserId) {
        mDataList.clear();
        CustomerDbHelper.queryUserWorkHistory(mUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (MeDataBean.WorkHistorys item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                        } else {
                            mMultipleStatusView.noEmpty();
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
                ToastUtils.showToast("加载失败,请稍后重试！");
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAddWorkIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.iv_add_work:
                intent.setClass(this, AddWorkActivity.class);
                intent.putExtra("add", "add");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public ArrayList<MeDataBean.WorkHistorys> parseData(String result) {//Gson 解析
        ArrayList<MeDataBean.WorkHistorys> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                MeDataBean.WorkHistorys entity = gson.fromJson(data.optJSONObject(i).toString(), MeDataBean.WorkHistorys.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onItemClick(View view) {
        if (!isUser) {
            MeDataBean.WorkHistorys mItem = mDataList.get((Integer) view.getTag());
            Intent intent = new Intent(EditWorkActivity.this, AddWorkActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", mItem);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
