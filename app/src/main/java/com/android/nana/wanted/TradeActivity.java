package com.android.nana.wanted;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.LabelsSecEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/6/27.
 */

public class TradeActivity extends BaseActivity implements View.OnClickListener, TradeAdapter.TradeOnCheckChangedListener {

    private ImageButton mBackBtn;
    private TextView mTitle, mSaveTv;
    private RecyclerView mRecyclerView;
    private TradeAdapter mAdapter;
    private int num = 0;//记录选中条数
    private ArrayList<Labels.Sec> mSec = new ArrayList<>();//保存选中标签
    private ArrayList<Labels> mDataList = new ArrayList<>();

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_istener);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.common_btn_back);
        mTitle = findViewById(R.id.common_txt_title);
        mSaveTv = findViewById(R.id.common_txt_right_text);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void init() {
        mTitle.setText("期望行业");
        mSaveTv.setText("确定");
        mSaveTv.setTextColor(getResources().getColor(R.color.green));
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
        mBackBtn.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
        }
    }

    private void save() {
        TradeActivity.this.finish();
        EventBus.getDefault().post(new LabelsSecEvent(mSec));
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

    @Override
    public void onSelectedLabels(Labels.Sec label, boolean isChecked) {
        if (isChecked) {
            mSec.add(label);
            num++;
            mSaveTv.setText("确定(" + num + ")");
        } else {
            mSec.remove(label);
            num--;
            if (num == 0) {
                mSaveTv.setText("确定");
            } else {
                mSaveTv.setText("确定(" + num + ")");
            }
        }
    }
}
