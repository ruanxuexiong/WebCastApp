package com.android.nana.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.eventBus.DleLabelEvent;
import com.android.nana.eventBus.FunctionEvent;
import com.android.nana.eventBus.MoreEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/12/29.
 */

public class FunctionLabelActivity extends BaseActivity implements View.OnClickListener, FunctionLabelAdapter.FunctionListener {

    private TextView mTitleTv, mBackTv;
    private String mUid;
    private String mParentName, mLabelName;
    private RecyclerView mRecyclerView;
    private FunctionLabelAdapter mAdapter;
    private LinearLayout mLabelLl;
    private TextView mDelTv;
    private ArrayList<FunctionBean.Profession> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(FunctionLabelActivity.this)) {
            EventBus.getDefault().register(FunctionLabelActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_function_label);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLabelLl = findViewById(R.id.ll_label);
        mDelTv = findViewById(R.id.tv_del);
    }

    @Override
    protected void init() {
        mTitleTv.setText("选择职能标签");

        if (null != getIntent().getStringExtra("parentName") && null != getIntent().getStringExtra("labelName")) {
            mParentName = getIntent().getStringExtra("parentName");
            mLabelName = getIntent().getStringExtra("labelName");
            mLabelLl.setVisibility(View.VISIBLE);
            mDelTv.setText(mLabelName);
        }

        if (null != getIntent().getStringExtra("uid")) {//当前登录用户ID
            mUid = getIntent().getStringExtra("uid");
            showProgressDialog("", "加载中...");
            loadData(mUid);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new FunctionLabelAdapter(this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData(String mUid) {

        FunctionDbHelper.shoiceFunctionLabel(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (FunctionBean.Profession item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
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
        mBackTv.setOnClickListener(this);
        mDelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                finish();
                break;
            case R.id.tv_del:
                delete();
                break;
            default:
                break;
        }
    }

    private void delete() {//删除职能标签
        FunctionDbHelper.delProfession(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mLabelLl.setVisibility(View.GONE);
                        EventBus.getDefault().post(new DleLabelEvent());
                        ToastUtils.showToast("删除成功!");
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

    private ArrayList<FunctionBean.Profession> parseData(String result) {
        ArrayList<FunctionBean.Profession> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray profession = new JSONArray(data.getString("profession"));
            if (profession.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < profession.length(); i++) {
                    FunctionBean.Profession bean = gson.fromJson(profession.optJSONObject(i).toString(), FunctionBean.Profession.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void onLabeClick(String mOneId, String mTwoId, String mContent, String mTitle) {
        EventBus.getDefault().post(new FunctionEvent(mOneId, mTwoId, mContent, mTitle));//更新职能标签
        this.finish();
    }

    @Override
    public void onMoreClick(String mOneId, String mTitle) {
        Intent intent = new Intent(this, MoreProfessionActivity.class);
        intent.putExtra("ParentId", mOneId);
        intent.putExtra("title", mTitle);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMoreEvent(MoreEvent moreEvent) {//职能标签
        EventBus.getDefault().post(new FunctionEvent(moreEvent.mOneId, moreEvent.mTwoId, moreEvent.mContent, moreEvent.mTitle));//更新职能标签
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(FunctionLabelActivity.this);
    }
}
