package com.android.nana.friend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/3/11.
 */

public class MeCollectionActivity extends com.android.common.base.BaseActivity implements View.OnClickListener,OnRefreshListener, OnLoadMoreListener,CollectionPictureImageGridAdapter.CollectionPictureListener {
    private String mid;
    private TextView mDynamicTv;
    private TextView mTitleTv;
    private TextView mBackTv;
    private MultipleStatusView mMultipleStatusView;
    private SmartRefreshLayout mRefreshLayout;
    private ListView mListView;
    private boolean isLoadMore = false;
    private MeCollectionAdapter mAdapter;
    private ArrayList<MeCollectionEntity> mDataList = new ArrayList<>();

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_collection);
    }

    @Override
    protected void findViewById() {
        mDynamicTv = findViewById(R.id.tv_dynamic);
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mListView = findViewById(R.id.content_view);
    }

    @Override
    protected void init() {
        mid = getIntent().getStringExtra("uid");
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("我的收藏");
        loadData();
        mAdapter = new MeCollectionAdapter(MeCollectionActivity.this, mDataList,this);
        mListView.setAdapter(mAdapter);
    }

    private void loadData() {
        FriendDbHelper.getCollectionByMonth(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mMultipleStatusView.dismiss();
                        if (parseData(successJson).size() > 0) {
                            for (MeCollectionEntity entity : parseData(successJson)) {
                                mDataList.add(entity);
                            }
                        }
                    } else {
                        if (isLoadMore) {
                            isLoadMore = false;
                            ToastUtils.showToast("暂无数据");
                            mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            mMultipleStatusView.noEmpty();
                            isLoadMore = false;
                        }
                    }

                    if (null != mAdapter) {
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
                mMultipleStatusView.noEmpty();
            }
        });
    }

    public ArrayList<MeCollectionEntity> parseData(String result) {//相册

        ArrayList<MeCollectionEntity> entity = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray array = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                MeCollectionEntity item = gson.fromJson(array.optJSONObject(i).toString(), MeCollectionEntity.class);
                entity.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    protected void setListener() {
        mDynamicTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dynamic:
                Intent album = new Intent(MeCollectionActivity.this, CollectionActvity.class);
                album.putExtra("uid", mid);
                startActivity(album);
                break;
            case R.id.iv_toolbar_back:
              finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoadMore = true;

        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                //   loadData(); 无上拉加载
                mRefreshLayout.finishLoadMore();
            }
        }, 500);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataList.clear();
                loadData();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.setNoMoreData(false);
            }
        }, 500);
    }

    @Override
    public void onItemClick(String id) {
        Intent intent = new Intent(MeCollectionActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mid", mid);
        startActivity(intent);
    }
}
