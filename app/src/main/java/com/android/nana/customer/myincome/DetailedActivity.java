package com.android.nana.customer.myincome;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.listener.DetailsListener;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.nana.R.id.refreshLayout;


/**
 * Created by lenovo on 2018/7/12.
 */

public class DetailedActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener, MoneyAllAdapter.MoneyAllListener {

    private TextView mTitleTv;
    private TextView mRightTv;
    private TextView mBackTv;
    private DetailedFragment mDialog;
    private boolean isUpdate = false;//筛选判断
    //选择字体
    private int type;

    private String mTotalExpendiureStr;//总支出
    private String mTotalRevenueStr;//总收入
    private String mTotalRecharge;//累计充值
    private String mTotalTixian;//累计提现
    private MoneyAllAdapter mAdapter;
    private int page = 1;
    private String mid;
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<MoneyAllBean.Orders> mDataList = new ArrayList<>();
    private boolean isLoadMore = false;
    private LinearLayout mTitleLl;
    private TextView mNewTitleTv, mTitleSubTv;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_wallet_detailed);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mRefreshLayout = findViewById(refreshLayout);

        mTitleLl = findViewById(R.id.ll_title);
        mNewTitleTv = findViewById(R.id.tv_new_title);
        mTitleSubTv = findViewById(R.id.tv_title_sub);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setVisibility(View.GONE);
        mTitleLl.setVisibility(View.VISIBLE);
        mNewTitleTv.setVisibility(View.VISIBLE);
        mTitleSubTv.setVisibility(View.VISIBLE);
        mNewTitleTv.setText("收支明细");
        mTitleSubTv.setText("全部");

        mRightTv.setVisibility(View.VISIBLE);
        Drawable drawable = this.getResources().getDrawable(R.drawable.icon_choice_detailed);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mRightTv.setCompoundDrawables(null, null, drawable, null);
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setTextSize(16);
        mRightTv.setText("筛选");
        mid = (String) SharedPreferencesUtils.getParameter(DetailedActivity.this, "userId", "");

        if (NetWorkUtils.isNetworkConnected(DetailedActivity.this)) {
            showProgressDialog("", "加载中...");
            loadAllData(mid, page);
        } else {
            mMultipleStatusView.noNetwork();
        }

        mAdapter = new MoneyAllAdapter(DetailedActivity.this, mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    private void loadAllData(String mid, int page) {//全部
        type = 0;
        CustomerDbHelper.getMoneyAll(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        if (parseData(successJson).size() > 0) {
                            for (MoneyAllBean.Orders item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.ShowData(mTotalExpendiureStr, mTotalRevenueStr);
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
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

    private void loadExpenditureData(String mid, int page) {//支出
        type = 1;
        CustomerDbHelper.getExpenditure(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        if (parseData(successJson).size() > 0) {
                            for (MoneyAllBean.Orders item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.ShowData(mTotalExpendiureStr, mTotalRevenueStr);
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isLoadMore) {
                                ToastUtils.showToast("暂无数据");
                                isLoadMore = false;
                                mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                            } else {
                                mMultipleStatusView.noEmpty();
                                isLoadMore = false;
                            }
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

    //收入
    private void loadIncomeData(String mid, int page) {//支出
        type = 2;
        CustomerDbHelper.getIncomeData(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        if (parseData(successJson).size() > 0) {
                            for (MoneyAllBean.Orders item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.ShowData(mTotalExpendiureStr, mTotalRevenueStr);
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isLoadMore) {
                                ToastUtils.showToast("暂无数据");
                                isLoadMore = false;
                                mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                            } else {
                                mMultipleStatusView.noEmpty();
                                isLoadMore = false;
                            }
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

    //充值
    private void loadRechargeData(String mid, int page) {//充值
        type = 3;
        CustomerDbHelper.getRechargeData(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        if (parseRechargeData(successJson).size() > 0) {
                            for (MoneyAllBean.Orders item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mTotalRevenueStr = "totalRecharge";
                            mAdapter.ShowData(mTotalRecharge, mTotalRevenueStr);
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isLoadMore) {
                                ToastUtils.showToast("暂无数据");
                                isLoadMore = false;
                                mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                            } else {
                                mMultipleStatusView.noEmpty();
                                isLoadMore = false;
                            }
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

    private void loadPutForwardData(String mid, int page) {//提现
        type = 4;
        CustomerDbHelper.getPutForwardData(mid, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        if (parsePutForwardData(successJson).size() > 0) {
                            for (MoneyAllBean.Orders item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mTotalRevenueStr = "totalTixian";
                            mAdapter.ShowData(mTotalTixian, mTotalRevenueStr);
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if (isLoadMore) {
                                ToastUtils.showToast("暂无数据");
                                isLoadMore = false;
                                mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                            } else {
                                mMultipleStatusView.noEmpty();
                                isLoadMore = false;
                            }
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
        mRightTv.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        DetailsListener.getInstance().mOnDetailsListener = new DetailsListener.OnDetailsListener() {
            @Override
            public void onClickAll(int choice) {//全部
                type = choice;
                page = 1;
                mTitleTv.setVisibility(View.GONE);
                mTitleLl.setVisibility(View.VISIBLE);
                mNewTitleTv.setVisibility(View.VISIBLE);
                mTitleSubTv.setVisibility(View.VISIBLE);
                mNewTitleTv.setText("收支明细");
                mTitleSubTv.setText("全部");

                mDataList.clear();
                showProgressDialog("", "加载中...");
                loadAllData(mid, page);
            }

            @Override
            public void onClickExp(int choice) {//支出
                type = choice;
                page = 1;
                mTitleTv.setVisibility(View.GONE);
                mTitleLl.setVisibility(View.VISIBLE);
                mNewTitleTv.setVisibility(View.VISIBLE);
                mTitleSubTv.setVisibility(View.VISIBLE);
                mNewTitleTv.setText("收支明细");
                mTitleSubTv.setText("支出");

                mDataList.clear();
                showProgressDialog("", "加载中...");
                loadExpenditureData(mid, page);
            }

            @Override
            public void onClickIncome(int choice) {//收入
                type = choice;
                page = 1;
                mTitleTv.setVisibility(View.GONE);
                mTitleLl.setVisibility(View.VISIBLE);
                mNewTitleTv.setVisibility(View.VISIBLE);
                mTitleSubTv.setVisibility(View.VISIBLE);
                mNewTitleTv.setText("收支明细");
                mTitleSubTv.setText("收入");

                mDataList.clear();
                showProgressDialog("", "加载中...");
                loadIncomeData(mid, page);
            }

            @Override
            public void onClickRecharge(int choice) {//充值
                type = choice;
                page = 1;
                mTitleTv.setVisibility(View.GONE);
                mTitleLl.setVisibility(View.VISIBLE);
                mNewTitleTv.setVisibility(View.VISIBLE);
                mTitleSubTv.setVisibility(View.VISIBLE);
                mNewTitleTv.setText("收支明细");
                mTitleSubTv.setText("充值");

                mDataList.clear();
                showProgressDialog("", "加载中...");
                loadRechargeData(mid, page);
            }

            @Override
            public void onClickPut(int choice) {//提现
                type = choice;
                page = 1;
                mTitleSubTv.setVisibility(View.GONE);
                mTitleTv.setVisibility(View.GONE);
                mTitleLl.setVisibility(View.VISIBLE);
                mNewTitleTv.setVisibility(View.VISIBLE);
                mTitleSubTv.setVisibility(View.VISIBLE);
                mNewTitleTv.setText("收支明细");
                mTitleSubTv.setText("提现");

                mDataList.clear();
                showProgressDialog("", "加载中...");
                loadPutForwardData(mid, page);
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                onAction2Click();
                break;
            default:
                break;
        }
    }

    private void onAction2Click() {
        if (isUpdate) {
            isUpdate = false;
            Drawable drawable = this.getResources().getDrawable(R.drawable.icon_choice_detailed);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mRightTv.setCompoundDrawables(null, null, drawable, null);
        } else {
            isUpdate = true;
            Drawable drawable = this.getResources().getDrawable(R.drawable.icon_choice_detailed_up);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mRightTv.setCompoundDrawables(null, null, drawable, null);

        }

        FragmentManager fm = this.getSupportFragmentManager();
        mDialog = DetailedFragment.newInstance();
        mDialog.show(fm, "fragment_bottom_dialog");

    }

    private ArrayList<MoneyAllBean.Orders> parseRechargeData(String result) {//充值
        ArrayList<MoneyAllBean.Orders> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray records = new JSONArray(data.getString("orders"));
            mTotalRecharge = data.getString("totalRecharge");//累计充值
            Gson gson = new Gson();
            for (int i = 0; i < records.length(); i++) {
                MoneyAllBean.Orders entity = gson.fromJson(records.optJSONObject(i).toString(), MoneyAllBean.Orders.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private ArrayList<MoneyAllBean.Orders> parsePutForwardData(String result){//提现
        ArrayList<MoneyAllBean.Orders> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray records = new JSONArray(data.getString("orders"));
            mTotalTixian = data.getString("totalTixian");//累计提现
            Gson gson = new Gson();
            for (int i = 0; i < records.length(); i++) {
                MoneyAllBean.Orders entity = gson.fromJson(records.optJSONObject(i).toString(), MoneyAllBean.Orders.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private ArrayList<MoneyAllBean.Orders> parseData(String result) {//Gson 解析
        ArrayList<MoneyAllBean.Orders> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray records = new JSONArray(data.getString("orders"));

            mTotalExpendiureStr = data.getString("totalExpendiure");//总支出
            mTotalRevenueStr = data.getString("totalRevenue");//总收入

            Gson gson = new Gson();
            for (int i = 0; i < records.length(); i++) {
                MoneyAllBean.Orders entity = gson.fromJson(records.optJSONObject(i).toString(), MoneyAllBean.Orders.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        page = 1;
        isLoadMore = false;
        if (type == 0) {//全部
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    loadAllData(mid, page);
                    refreshLayout.finishRefresh();
                    refreshLayout.setNoMoreData(false);
                }
            }, 500);
        } else if (type == 1) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    loadExpenditureData(mid, page);
                    refreshLayout.finishRefresh();
                    refreshLayout.setNoMoreData(false);
                }
            }, 500);
        } else if (type == 2) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    loadIncomeData(mid, page);
                    refreshLayout.finishRefresh();
                    refreshLayout.setNoMoreData(false);
                }
            }, 500);
        } else if (type == 3) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    loadRechargeData(mid, page);
                    refreshLayout.finishRefresh();
                    refreshLayout.setNoMoreData(false);
                }
            }, 500);
        } else if (type == 4) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    loadPutForwardData(mid, page);
                    refreshLayout.finishRefresh();
                    refreshLayout.setNoMoreData(false);
                }
            }, 500);
        }
    }

    @Override
    public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
        page++;
        isLoadMore = true;
        if (type == 0) {//全部
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadAllData(mid, page);
                    refreshLayout.finishLoadMore();
                }
            }, 500);
        } else if (type == 1) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadExpenditureData(mid, page);
                    refreshLayout.finishLoadMore();
                }
            }, 500);
        } else if (type == 2) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadIncomeData(mid, page);
                    refreshLayout.finishLoadMore();
                }
            }, 500);
        } else if (type == 3) {//充值
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadRechargeData(mid, page);
                    refreshLayout.finishLoadMore();
                }
            }, 500);
        } else if (type == 4) {//提现
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadPutForwardData(mid, page);
                    refreshLayout.finishLoadMore();
                }
            }, 500);
        }
    }

    @Override
    public void onClickItem(View view) {
        Intent intent = new Intent(DetailedActivity.this, MoneyDetailActivity.class);
        intent.putExtra("id", mDataList.get((Integer) view.getTag()).getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }
}
