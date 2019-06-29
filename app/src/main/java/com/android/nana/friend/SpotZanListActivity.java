package com.android.nana.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.ZanEntity;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.NetWorkUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/11/2.
 */

public class SpotZanListActivity extends BaseActivity implements View.OnClickListener, ZanAdapter.ZanListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private String id;
    private ListView mListView;
    private ZanAdapter mAdapter;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<ZanEntity.Users> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NetWorkUtils.isNetworkConnected(SpotZanListActivity.this)) {
            mMultipleStatusView.loading();
            id = getIntent().getStringExtra("id");
            loadData(id);
        } else {
            mMultipleStatusView.noNetwork();
        }
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_spot_zan);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mAdapter = new ZanAdapter(SpotZanListActivity.this, mDataList, SpotZanListActivity.this);
        mListView.setAdapter(mAdapter);
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

    private void loadData(String id) {
        FriendDbHelper.laudUserList(id, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (ZanEntity.Users item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                        }
                        mTitleTv.setText("共" + data.getString("num") + "个赞");
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }


    private ArrayList<ZanEntity.Users> parseData(String result) {
        ArrayList<ZanEntity.Users> users = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("users"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                ZanEntity.Users entity = gson.fromJson(array.optJSONObject(i).toString(), ZanEntity.Users.class);
                users.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void onItemClick(View view) {
        ZanEntity.Users users = mDataList.get((Integer) view.getTag());
        Intent intentEdit = new Intent(SpotZanListActivity.this, EditDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserId", users.getId());
        intentEdit.putExtras(bundle);
        startActivity(intentEdit);
    }
}
