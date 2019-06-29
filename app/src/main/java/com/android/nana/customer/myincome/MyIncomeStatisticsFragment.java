package com.android.nana.customer.myincome;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewFragment;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.StatisticsAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.listener.MyIncomeListener;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@SuppressLint("ValidFragment")
public class MyIncomeStatisticsFragment extends BaseListViewFragment {

    private String mType;

    private StatisticsAdapter mAdapter;
    private String mEndTime;
    private String mUserId;

    public MyIncomeStatisticsFragment() {

    }

    public static MyIncomeStatisticsFragment newInstance(String type) {
        MyIncomeStatisticsFragment f = new MyIncomeStatisticsFragment();
        Bundle b = new Bundle();
        b.putString("Type", type);
        f.setArguments(b);
        return f;
    }

    @Override
    protected StatisticsAdapter getBaseJsonAdapter() {
        mAdapter = new StatisticsAdapter(getActivity(), mType);
        return mAdapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh();
    }

    @Override
    protected void initList() {
        mPageIndex = 1;
        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0) {
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount() - 1), "addTime", "");
        } else {
            mEndTime = "";
        }
        CustomerDbHelper.consume(mPageIndex, mPageSize, mUserId, mEndTime, mType, mIOAuthCallBack);

    }

    @Override
    protected void initOther() {
        super.initOther();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.grey_e6)));
        mListView.setDividerHeight(1);
    }

    @Override
    protected int getLayoutId() {

        Bundle args = getArguments();

        if (args != null) mType = args.getString("Type");

        mUserId = BaseApplication.getInstance().getCustomerId(getActivity());

        return R.layout.statistics;
    }

    @Override
    protected void findViewById() {


    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onCompletedList(String json) {
        super.onCompletedList(json);

        JSONObject jsonObject = JSONUtil.getStringToJson(json);

        String totalInCome = JSONUtil.get(jsonObject, "totalInCome", "");
        String totalExpenditure = JSONUtil.get(jsonObject, "totalExpenditure", "");

        if (MyIncomeListener.getInstance().mOnMyIncomeListener != null) {
            MyIncomeListener.getInstance().mOnMyIncomeListener.result(totalInCome, totalExpenditure);
        }

    }
}
