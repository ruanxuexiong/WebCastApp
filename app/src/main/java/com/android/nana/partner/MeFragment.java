package com.android.nana.partner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.customer.myincome.MyIncomeWithdrawActivity;
import com.android.nana.customer.myincome.WalletActivity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.material.EditDataActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/8.
 */

public class MeFragment extends Fragment implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, RanAdapter.OnItemClickListener {
    private LinearLayout zanwushuju_lin;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MePagerAdapter mPageAdapter;
    private String mUid;
    private TextView mNumSelTv, mMeSelTv, mProfitTv, mNumTv, mBtnTv,mNumberTv;
    private String mUserName, mAvatar, mIntroduce, mMsg, mUrl;
    private String mShareUrl,mShareTitle,mShareDesc,mShareLogo;
    private ImageView huodong_tixian,huodong_share_2,now_share_;
    private RelativeLayout mProfitRl, mNumRl, mMeRl,zanwushuju_re;

    private FragmentTransaction mFragmetTrans;
    private FragmentManager mFragmentManager;

    private MeProfitFragment profitFragment;
    private MeNumberFragment numberFragment;
    private MeInviteFragment inviteFragment;

    private FloatingActionButton mFlaBut;
    private int mPage = 1;

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private String mType = "total";

    private RanAdapter mAdapter;
    private ArrayList<RanBean> mDataList = new ArrayList<>();

    public MeFragment newInstance(String mUid) {
        MeFragment fragment = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mUid", mUid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_profit, null);

        initView(view);
        return view;
    }

    private void initView(View view) {
        now_share_=view.findViewById(R.id.now_share_);
        zanwushuju_re=view.findViewById(R.id.zanwushuju_re);
        zanwushuju_lin=view.findViewById(R.id.zanwushuju_lin);
        huodong_share_2=view.findViewById(R.id.huodong_share_2);
        huodong_tixian=view.findViewById(R.id.huodong_tixian);
        mProfitTv = view.findViewById(R.id.tv_profit);
        mNumTv = view.findViewById(R.id.tv_num);
        mFlaBut = view.findViewById(R.id.fab);
        mLayout = view.findViewById(R.id.refresh_view);
        mListView = view.findViewById(R.id.content_view);
        mNumberTv = view.findViewById(R.id.tv_number);

        now_share_.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
        huodong_share_2.setOnClickListener(this);
        mFlaBut.setOnClickListener(this);
        huodong_tixian.setOnClickListener(this);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(getActivity()).load(R.mipmap.huodong_tixian).apply(options).into(huodong_tixian);
/*
       // mProfitRl = (RelativeLayout) view.findViewById(R.id.profit_rl);
        mNumRl = (RelativeLayout) view.findViewById(R.id.num_rl);
        mMeRl = (RelativeLayout) view.findViewById(R.id.me_rl);

       // mProfitSelTv = (TextView) view.findViewById(R.id.profit_sel_tv);
        mNumSelTv = (TextView) view.findViewById(R.id.num_sel_tv);
        mMeSelTv = (TextView) view.findViewById(R.id.me_sel_tv);

        mFragmentManager = getActivity().getSupportFragmentManager();
        mFragmetTrans = mFragmentManager.beginTransaction();

        //mProfitRl.setOnClickListener(this);
        mNumRl.setOnClickListener(this);
        mMeRl.setOnClickListener(this);*/

        if (null != getArguments().getString("mUid")) {
            mUid = getArguments().getString("mUid");
            loadData(mUid);
            mLayout.autoRefresh();
        }

        mAdapter = new RanAdapter(getContext(), mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

/*    private void setAdapter() {
        profitFragment = MeProfitFragment.newInstance(getActivity(), this.mUid, mMsg, mUrl);
        numberFragment = MeNumberFragment.newInstance(getActivity(), this.mUid, mMsg, mUrl);
        inviteFragment = MeInviteFragment.newInstance(getActivity(), this.mUid, mMsg, mUrl);


        Drawable drawable = getResources().getDrawable(R.drawable.sel_partner_selected);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mNumSelTv.setTextColor(getResources().getColor(R.color.white));
        mNumSelTv.setBackgroundDrawable(drawable);

        mFragmetTrans.replace(R.id.me_profitt_view, profitFragment).commit();
    }*/


    private void loadData(String mUid) {

        WebCastDbHelper.userInfo(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                      /*  mProfitTv.setText("￥" + jsonObject1.getString("balance"));
                        mNumTv.setText(jsonObject1.getString("num") + "人");*/

                        mUserName = jsonObject1.getString("username");
                        mAvatar = jsonObject1.getString("avatar");
                        mIntroduce = jsonObject1.getString("introduce");
                        mMsg = jsonObject1.getString("message");
                        mUrl = jsonObject1.getString("shareUrl");

                        mShareUrl = jsonObject1.getString("shareUrl");
                        mShareTitle = jsonObject1.getString("shareTitle");
                        mShareDesc = jsonObject1.getString("shareDesc");
                        mShareLogo = jsonObject1.getString("shareLogo");

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
    public void onDestroy() {
        super.onDestroy();
/*
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.now_share_:
                FragmentManager fragmentManager3 = getActivity().getSupportFragmentManager();
                PartnerFragment partnerFragment3 = PartnerFragment.newInstance(mUid,mShareTitle,mShareLogo,mShareDesc, mMsg, mShareUrl);
                partnerFragment3.show(fragmentManager3, "fragment_bottom_dialog");
                break;
            case R.id.huodong_share_2:
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                PartnerFragment partnerFragment2 = PartnerFragment.newInstance(mUid,mShareTitle,mShareLogo,mShareDesc, mMsg, mShareUrl);
                partnerFragment2.show(fragmentManager2, "fragment_bottom_dialog");
                break;
            case  R.id.huodong_tixian:
                //提现
                Intent intent=new Intent(getActivity(),WalletActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.fab:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PartnerFragment partnerFragment = PartnerFragment.newInstance(mUid,mShareTitle,mShareLogo,mShareDesc, mMsg, mShareUrl);
                partnerFragment.show(fragmentManager, "fragment_bottom_dialog");
                break;
            default:
                break;
       /*     case R.id.profit_rl:

               *//**//* Drawable drawable = getResources().getDrawable(R.drawable.sel_partner_selected);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mProfitSelTv.setTextColor(getResources().getColor(R.color.white));
                mProfitSelTv.setBackgroundDrawable(drawable);
                mProfitSelTv.setPadding(18, 8, 18, 8);*//**//*

                Drawable drawable6 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
                drawable6.setBounds(0, 0, drawable6.getMinimumWidth(), drawable6.getMinimumHeight());
                mNumSelTv.setTextColor(getResources().getColor(R.color.gules));
                mNumSelTv.setBackgroundDrawable(drawable6);
                mNumSelTv.setPadding(18, 8, 18, 8);

                Drawable drawable7 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
                drawable7.setBounds(0, 0, drawable7.getMinimumWidth(), drawable7.getMinimumHeight());

                mMeSelTv.setTextColor(getResources().getColor(R.color.gules));
                mMeSelTv.setBackgroundDrawable(drawable7);
                mMeSelTv.setPadding(18, 8, 18, 8);

                FragmentTransaction mProfitTransaction1 = mFragmentManager.beginTransaction();
                mProfitTransaction1.replace(R.id.me_profitt_view, profitFragment).commit();

                break;*//*
            case R.id.num_rl:

                Drawable drawable1 = getResources().getDrawable(R.drawable.sel_partner_selected);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                mNumSelTv.setTextColor(getResources().getColor(R.color.white));
                mNumSelTv.setBackgroundDrawable(drawable1);
                mNumSelTv.setPadding(18, 8, 18, 8);
//
               // Drawable drawable8 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
              //  drawable8.setBounds(0, 0, drawable8.getMinimumWidth(), drawable8.getMinimumHeight());
              //  mProfitSelTv.setTextColor(getResources().getColor(R.color.gules));

             //   mProfitSelTv.setBackgroundDrawable(drawable8);
              //  mProfitSelTv.setPadding(18, 8, 18, 8);

                Drawable drawable9 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
                drawable9.setBounds(0, 0, drawable9.getMinimumWidth(), drawable9.getMinimumHeight());
                mMeSelTv.setTextColor(getResources().getColor(R.color.gules));

                mMeSelTv.setBackgroundDrawable(drawable9);
                mMeSelTv.setPadding(18, 8, 18, 8);

                FragmentTransaction mNumbTransaction1 = mFragmentManager.beginTransaction();
                mNumbTransaction1.replace(R.id.me_profitt_view, numberFragment).commit();
                break;
            case R.id.me_rl:

                Drawable drawable2 = getResources().getDrawable(R.drawable.sel_partner_selected);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                mMeSelTv.setTextColor(getResources().getColor(R.color.white));
                mMeSelTv.setBackgroundDrawable(drawable2);
                mMeSelTv.setPadding(18, 8, 18, 8);

           //     Drawable drawable3 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
              //  drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
              //  mProfitSelTv.setTextColor(getResources().getColor(R.color.gules));

               // mProfitSelTv.setBackgroundDrawable(drawable3);
               // mProfitSelTv.setPadding(18, 8, 18, 8);

                Drawable drawable4 = getResources().getDrawable(R.drawable.shape_ranking_bg_press);
                drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                mNumSelTv.setTextColor(getResources().getColor(R.color.gules));

                mNumSelTv.setBackgroundDrawable(drawable4);
                mNumSelTv.setPadding(18, 8, 18, 8);

                FragmentTransaction mInviterTransaction1 = mFragmentManager.beginTransaction();
                mInviterTransaction1.replace(R.id.me_profitt_view, inviteFragment).commit();
                break;*/
        }
    }

    @Override
    public void onItemClick(View view) {
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mDataList.get((Integer) view.getTag()).getInviteUid());
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        mPage = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                newloadData( mPage, mUid);
            }
        }, 500);
       /* switch (mType) {
            case "total":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "month":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "week":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "day":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "2");
                    }
                }, 500);
                break;
        }*/
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        mPage++;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                newloadData( mPage, mUid);
            }
        }, 500);
     /*   switch (mType) {
            case "total":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "month":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "week":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
            case "day":
                new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
                    @Override
                    public void run() {
                        loadData(mType, mPage, mUid, "3");
                    }
                }, 500);
                break;
        }*/
    }

    private void newloadData( final int page, final String mid) {
        WebCastDbHelper.getRecommendRank(String.valueOf(page), mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                    JSONObject jsonObject2 = new JSONObject(jsonObject.getString("data"));
             /*       String myrank = "<font color='#999999'>我的当前排名: </font>" + "<font color='#FF0000'>" + jsonObject2.getString("myrank") + "</font>";
                    mCurrentTv.setText(Html.fromHtml(myrank));*/

                    if (jsonObject1.getString("state").equals("0")) {
                        for (RanBean item : parseData(successJson)) {
                            if (!mDataList.contains(item)) {
                                mDataList.add(item);
                            }
                        }

                        if (null != jsonObject2.getString("accumulativeMoney") && !"".equals(jsonObject2.getString("accumulativeMoney")) && !"null".equals(jsonObject2.getString("accumulativeMoney"))){
                            mProfitTv.setText("￥"+jsonObject2.getString("accumulativeMoney"));
                        }else {
                            mProfitTv.setText("￥0");
                        }

                        if (null != jsonObject2.getString("todayMoney") && !"".equals(jsonObject2.getString("todayMoney"))&& !"null".equals(jsonObject2.getString("todayMoney"))){
                            mNumTv.setText("￥"+jsonObject2.getString("todayMoney"));
                        }else {
                            mNumTv.setText("￥0");
                        }

                        if (null != jsonObject2.getString("num") && !"".equals(jsonObject2.getString("num"))&& !"null".equals(jsonObject2.getString("num"))&&!"0".equals(jsonObject2.getString("num"))){
                            mNumberTv.setText("已邀请朋友（"+jsonObject2.getString("num")+"人)");
                            zanwushuju_lin.setVisibility(View.GONE);
                            huodong_share_2.setVisibility(View.VISIBLE);
                            zanwushuju_re.setVisibility(View.VISIBLE);
                        }else {
                            mNumberTv.setText("已邀请朋友（0人)");
                            zanwushuju_lin.setVisibility(View.VISIBLE);
                            huodong_share_2.setVisibility(View.GONE);
                            zanwushuju_re.setVisibility(View.GONE);
                        }


                        mAdapter.notifyDataSetChanged();
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

    public ArrayList<RanBean> parseData(String result) {//Gson 解析
        ArrayList<RanBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("list"));
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
}
