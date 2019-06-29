package com.android.nana.recruit.merecruit;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.recruit.adapter.PositionAdapter;
import com.android.nana.recruit.bean.PositionManagementEntity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/30.
 */

public class PositionManagementActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private String id, mid;
    private ListView mListView;
    private PositionAdapter mAdapter;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<PositionManagementEntity.Positions> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            mMultipleStatusView.loading();
            initData();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_position_management);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mListView = findViewById(R.id.view_recycler);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mAdapter = new PositionAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        mTitleTv.setText("管理招聘职位");
        mBackTv.setVisibility(View.VISIBLE);
    }

    private void initData() {
        RecruitDbHelper.positionList(mid, id, new IOAuthCallBack() {
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
                            for (PositionManagementEntity.Positions item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        mMultipleStatusView.empty();
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

    public ArrayList<PositionManagementEntity.Positions> parseData(String result) {//Gson 解析
        ArrayList<PositionManagementEntity.Positions> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("positions"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                PositionManagementEntity.Positions entity = gson.fromJson(data.optJSONObject(i).toString(), PositionManagementEntity.Positions.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
