package com.android.nana.main;


import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.HelpOtherAdapter;
import com.android.nana.bean.Ranking;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;
import com.luck.picture.lib.decoration.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

public class HelpOtherActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView mTitleTv, mBackTv;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HelpOtherAdapter helpOtherAdapter;
    private GridLayoutManager mLayoutManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 20;
    private int PAGE = 1;
    private List<Ranking.DataBean> datas = new ArrayList<>();
    boolean hasRefresh=false;
    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_help_other);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void init() {
        mTitleTv.setText("排行榜");
        mBackTv.setVisibility(View.VISIBLE);
        initData();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initData() {
        if(PAGE==1){
            hasRefresh=true;
        }else {
            hasRefresh = false;
        }
            FriendDbHelper.postRanking(PAGE, PAGE_COUNT, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
              //  Log.e("TAG","SUCCESS=="+successJson);

                Gson gson = new Gson();
                Ranking ranking = gson.fromJson(successJson, Ranking.class);
                if (ranking.getResult().getState() == 0) {
                    if (ranking.getData().size() < PAGE_COUNT)
                        helpOtherAdapter.updateList(ranking.getData(), false,hasRefresh);
                    else
                        updateRecyclerView(ranking.getData());
                } else {
                    helpOtherAdapter.updateList(null, false,hasRefresh);
                    ToastUtils.showToast(ranking.getResult().getDescription());
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void getFailue(String failueJson) {
                refreshLayout.setRefreshing(false);
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
        }
    }

    private void initRefreshLayout() {
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(false);

    }

    private void initRecyclerView() {
        helpOtherAdapter = new HelpOtherAdapter(datas, this, datas.size() > 0 ? true : false);
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(helpOtherAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (helpOtherAdapter.isFadeTips() == false && lastVisibleItem + 1 == helpOtherAdapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PAGE++;
                                initData();
//                                updateRecyclerView(helpOtherAdapter.getRealLastPosition(), helpOtherAdapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }

                    if (helpOtherAdapter.isFadeTips() == true && lastVisibleItem + 2 == helpOtherAdapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                updateRecyclerView(helpOtherAdapter.getRealLastPosition(), helpOtherAdapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void updateRecyclerView(List<Ranking.DataBean> datas) {
        List<Ranking.DataBean> newDatas = datas;
        if (newDatas.size() > 0) {
            helpOtherAdapter.updateList(newDatas, true,hasRefresh);
        } else {
            helpOtherAdapter.updateList(null, false,hasRefresh);
        }
    }

    @Override
    public void onRefresh() {

        PAGE=1;
        initData();
      //  refreshLayout.setRefreshing(false);
    }
}
