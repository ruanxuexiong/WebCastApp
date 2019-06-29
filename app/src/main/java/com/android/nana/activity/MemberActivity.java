package com.android.nana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.DetailsAdapter;
import com.android.nana.bean.DetailsEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by lenovo on 2017/10/10.
 * 查看成员
 */

public class MemberActivity extends BaseActivity implements DetailsAdapter.DetailsListener, View.OnClickListener {

    private String id, mid;
    private AppointmentModel mAppointmentModel;
    private DetailsAdapter mAdapter;
    private DetailsEntity entity;
    private MultipleStatusView mMultiplpView;
    private AppCompatTextView mBackTv, mTitleTv;
    private ArrayList<DetailsEntity.Users> mDataList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            showProgressDialog("", "正在加载...");
            loadData(id, mid);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_member);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRecyclerView = findViewById(R.id.mRecyclerview);
        mMultiplpView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mAppointmentModel = new AppointmentModel(MemberActivity.this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MemberActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MemberActivity.this));

        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DetailsAdapter(MemberActivity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);//设置adapter
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    private void loadData(String id, String mid) {
        HomeDbHelper.activity(id, mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mDataList.clear();

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        entity = parseData(successJson);
                        mTitleTv.setText("群组成员(" + entity.getNum() + ")");
                        if (entity.getUsers().size() > 0) {
                            for (DetailsEntity.Users item : entity.getUsers()) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mMultiplpView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultiplpView.empty();
                        }
                        dismissProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void onMakeClick(View view) {
        DetailsEntity.Users mItem = mDataList.get((Integer) view.getTag());
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(MemberActivity.this, "userInfo", UserInfo.class);
        mAppointmentModel.init(mUserInfo.getId(), mItem.getId(), mUserInfo.getPayPassword());
        mAppointmentModel.doDialog("", mItem.getUsername());
    }

    @Override
    public void onContentClick(View view) {
        Intent intent = new Intent(MemberActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(View view) {
        DetailsEntity.Users mItem = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", mItem.getId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(this.getPackageName());
        startActivity(intent);
    }


    public DetailsEntity parseData(String result) {//Gson 解析
        DetailsEntity entity = new DetailsEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), DetailsEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                MemberActivity.this.finish();
                break;
        }
    }

}
