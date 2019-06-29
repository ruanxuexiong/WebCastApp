package com.android.nana.material;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.eventBus.MoreEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/2.
 */

public class MoreProfessionActivity extends BaseActivity implements View.OnClickListener,MoreAdapter.MoreListener {

    private String mParentId;//一级标签ID
    private TextView mTitleTv, mBackTv;
    private GridView mGridView;
    private MoreAdapter mAdapter;
    private ArrayList<MoreBean.Profession> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_more_profess);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mGridView = findViewById(R.id.mineMainview);
        mBackTv.setVisibility(View.VISIBLE);
        mAdapter = new MoreAdapter(this, mDataList,this);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("ParentId")) {
            mParentId = getIntent().getStringExtra("ParentId");
            mTitleTv.setText(getIntent().getStringExtra("title"));
            loadData(mParentId);
        }
    }

    private void loadData(String mParentId) {
        FunctionDbHelper.moreProfession(mParentId, new IOAuthCallBack() {
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
                            for (MoreBean.Profession item : parseData(successJson)) {
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
                finish();
                break;
            default:
                break;
        }
    }

    private ArrayList<MoreBean.Profession> parseData(String result) {
        ArrayList<MoreBean.Profession> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray profession = new JSONArray(data.getString("profession"));
            if (profession.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < profession.length(); i++) {
                    MoreBean.Profession bean = gson.fromJson(profession.optJSONObject(i).toString(), MoreBean.Profession.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void OnItemClick(String mTwoId,String mContent) {

        EventBus.getDefault().post(new MoreEvent(mParentId, mTwoId, mContent,mTitleTv.getText().toString().trim()));//更新职能标签
        this.finish();
    }
}
