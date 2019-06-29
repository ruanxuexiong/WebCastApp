package com.android.nana.partner;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.material.EditDataActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/8.
 * 人数排行
 */

public class MeNumberFragment extends Fragment implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, RanAdapter.OnItemClickListener {

    private Context mContext;
    private String mUid, mMsg, mUrl, mType = "total", rank = "2";
    private TextView mRankingTv, mCurrentTv;
    private FloatingActionButton mFlaBut;
    private int mPage = 1;

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;

    private RanAdapter mAdapter;
    private ArrayList<RanBean> mDataList = new ArrayList<>();

    public static MeNumberFragment newInstance(Context context, String mUid, String msg, String mUrl) {
        MeNumberFragment fragment = new MeNumberFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mUid", mUid);
        bundle.putString("mMsg", msg);
        bundle.putString("mUrl", mUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number, null);

        if (!EventBus.getDefault().isRegistered(MeNumberFragment.this)) {
            EventBus.getDefault().register(MeNumberFragment.this);
        }

        initView(view);
        return view;
    }

    private void initView(View view) {

        mRankingTv = (TextView) view.findViewById(R.id.ranking_tv);
        mFlaBut = (FloatingActionButton) view.findViewById(R.id.fab);

        mCurrentTv = (TextView) view.findViewById(R.id.current_tv);
        mLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        mListView = (PullableListView) view.findViewById(R.id.content_view);

        mLayout.setOnRefreshListener(this);
        mFlaBut.setOnClickListener(this);
        mRankingTv.setOnClickListener(this);

        if (null != getArguments().getString("mUid")) {
            mUid = getArguments().getString("mUid");
            mMsg = getArguments().getString("mMsg");
            mUrl = getArguments().getString("mUrl");
            mLayout.autoRefresh();
        }

        mAdapter = new RanAdapter(getContext(), mDataList, this);
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ranking_tv:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ProfitDialogFragment dialog = ProfitDialogFragment.newInstance();
                dialog.show(fm, "invite_bottom_dialog");
                break;
            case R.id.fab:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PartnerFragment partnerFragment = PartnerFragment.newInstance(mUid, mMsg, mUrl);
                partnerFragment.show(fragmentManager, "fragment_bottom_dialog");
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        mPage = 1;
        switch (mType) {
            case "total":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                   //     loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "month":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                 //       loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "week":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                    //    loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "day":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                  //      loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
        }

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        mPage++;
        switch (mType) {
            case "total":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                //        loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "month":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
           //             loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "week":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
               //         loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
            case "day":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                  //      loadData(mType, mPage, mUid, rank);
                    }
                }, 500);
                break;
        }
    }


//    private void loadData(String order, final int page, String mid, String rank) {
//        WebCastDbHelper.getRecommendRank(order, String.valueOf(page), mid, rank, new IOAuthCallBack() {
//            @Override
//            public void onStartRequest() {
//
//            }
//
//            @Override
//            public void getSuccess(String successJson) {
//
//
//                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
//                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mDataList.clear();
//                } else if (null != mPullToLoadmore) {
//                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
//                try {
//                    JSONObject jsonObject = new JSONObject(successJson);
//                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
//                    JSONObject jsonObject2 = new JSONObject(jsonObject.getString("data"));
//
//                    String myrank = "<font color='#999999'>我的当前排名： </font>" + "<font color='#FF0000'>" + jsonObject2.getString("myrank") + "</font>";
//                    mCurrentTv.setText(Html.fromHtml(myrank));
//                    if (jsonObject1.getString("state").equals("0")) {
//                        for (RanBean item : parseData(successJson)) {
//                            if (!mDataList.contains(item)) {
//                                mDataList.add(item);
//                            }
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void getFailue(String failueJson) {
//
//            }
//        });
//    }

    public ArrayList<RanBean> parseData(String result) {//Gson 解析
        ArrayList<RanBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("ranks"));

            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                RanBean entity = gson.fromJson(data.optJSONObject(i).toString(), RanBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(MeNumberFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMoonEvent(MessageEvent messageEvent) {

        switch (messageEvent.message) {
            case "total":
                mRankingTv.setText("总排行");
                this.mType = messageEvent.message;
                mLayout.autoRefresh();
                break;
            case "month":
                mRankingTv.setText("月排行");
                this.mType = messageEvent.message;
                mLayout.autoRefresh();
                break;
            case "week":
                mRankingTv.setText("周排行");
                this.mType = messageEvent.message;
                mLayout.autoRefresh();
                break;
            case "day":
                mRankingTv.setText("当天排行");
                this.mType = messageEvent.message;
                mLayout.autoRefresh();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {


        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getRecUid());
        startActivity(intent);
    }
}
