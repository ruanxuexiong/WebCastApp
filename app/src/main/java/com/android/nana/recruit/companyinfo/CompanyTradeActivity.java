package com.android.nana.recruit.companyinfo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.recruit.eventBus.CompanyTradeEvent;
import com.android.nana.wanted.Labels;
import com.android.nana.wanted.TradeAdapter;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/23.
 */

public class CompanyTradeActivity extends BaseActivity implements View.OnClickListener, TradeAdapter.TradeOnCheckChangedListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private RecyclerView mRecyclerView;
    private TradeAdapter mAdapter;
    private ArrayList<Labels> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_company_trade);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("选择行业");
        mBackTv.setVisibility(View.VISIBLE);

        initData();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mAdapter = new TradeAdapter(this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        WebCastDbHelper.getProfessions(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (Labels item : parseData(successJson)) {
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
    public void onSelectedLabels(Labels.Sec label, boolean isChecked) {
        EventBus.getDefault().post(new CompanyTradeEvent(label));
        this.finish();
    }

    private ArrayList<Labels> parseData(String result) {//Gson 解析
        ArrayList<Labels> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                Labels entity = gson.fromJson(data.get(i).toString(), Labels.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
