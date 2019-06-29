package com.android.nana.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseRequestFragment;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.DetailsActivity;
import com.android.nana.activity.MainCreateActivity;
import com.android.nana.bean.BannerEntity;
import com.android.nana.bean.HomeActivityEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.HomePageEvent;
import com.android.nana.home.AllCategoryListActivity;
import com.android.nana.home.HomePageUserBean;
import com.android.nana.home.HomeTagsBean;
import com.android.nana.home.SelectedCountryActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditUserInfoActivity;
import com.android.nana.material.ScreenActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.model.UserModel;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.CategoryListActivity;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.StateButton;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by THINK on 2017/7/11.
 */

public class HomePageFragment extends BaseRequestFragment implements PullToRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ConvenientBanner mBanner;
    private TextView mTxtCountry, mTxtSearch, mTxtFilter;
    private TextView mTxtAllCategory;

    private LinearLayout mLlList;

    private int mScreenWidth;

    private PullToRefreshLayout mLayout;
    private LinearLayout mLlAllLayout;
    private View mVHome;

    private LinearLayout mTagsLL, mTabll;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<HomeTagsBean> mTags = new ArrayList<>();
    private ArrayList<HomePageUserBean> mDataList = new ArrayList<>();
    private int mPage = 1;

    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private String thisUserId, state;

    private Dialog mPerfectDialog, mCertifiedDialog;
    private View mPerfectView, mCertifiedView;
    private TextView mPerfectContentTv;
    private StateButton mPerfectBtn;
    private ImageButton mCloseBtn;
    private UserInfo mUserInfo;
    private AppointmentModel mAppointmentModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppointmentModel = new AppointmentModel(HomePageFragment.this.getContext());
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(getContext(), "userInfo", UserInfo.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void findViewById() {

        mScreenWidth = mBaseApplication.getScreenWidth(getActivity());

        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_view);
        // mTxtCountry = (TextView) findViewById(R.id.home_layout_txt_country);
        mTxtSearch = (TextView) findViewById(R.id.home_layout_txt_search);
        mTxtFilter = (TextView) findViewById(R.id.home_layout_txt_filter);
        mLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);

        mVHome = getActivity().getLayoutInflater().inflate(R.layout.home, null);
        // 首页顶部广告
        mBanner = mVHome.findViewById(R.id.home_cb_banner);
        //  UIUtil.getInstance().setLayoutParams(mBanner, (int) (0.45 * mScreenWidth));

        mLlList = (LinearLayout) mVHome.findViewById(R.id.home_ll_list);
        mTxtAllCategory = (TextView) mVHome.findViewById(R.id.home_txt_all_category);
        mTagsLL = (LinearLayout) mVHome.findViewById(R.id.tages_ll);
        mLlAllLayout = (LinearLayout) findViewById(R.id.refresh_layout);
        mLlAllLayout.addView(mVHome);

        mPerfectView = LayoutInflater.from(getActivity()).inflate(R.layout.home_perfect_dialog, null);
        mPerfectBtn = (StateButton) mPerfectView.findViewById(R.id.btn_perfect);
        mPerfectContentTv = (TextView) mPerfectView.findViewById(R.id.tv_content);
        mCloseBtn = (ImageButton) mPerfectView.findViewById(R.id.close_iv);
        mPerfectDialog = new AlertDialog.Builder(getActivity()).create();
        mPerfectDialog.setCanceledOnTouchOutside(false);

        mTabll = mVHome.findViewById(R.id.ll_tab);
    }

    @Override
    protected void init() {
        mMultipleStatusView.loading();

        if (NetWorkUtils.isNetworkConnected(getContext())) {
            thisUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
            HomeDbHelper.index(thisUserId, mIOAuthCallBack);//首页轮播图

            //获取标签
            CustomerDbHelper.getIndexTags(mGetTagesCallBack);
            //获取用户列表
            CustomerDbHelper.getIndexUserList(thisUserId, mPage, mIOUserListCallBack);

            //当前用户id身份证是否认证
            CustomerDbHelper.checkIsAlert(thisUserId, mIOCheckIsAlert);

            //加载活动
            CustomerDbHelper.loadActivity(mIOActivityBack);
        } else {
            mMultipleStatusView.noNetwork();
        }

        if (!EventBus.getDefault().isRegistered(HomePageFragment.this)) {
            EventBus.getDefault().register(HomePageFragment.this);
        }
    }

    private IOAuthCallBack mIOActivityBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void getSuccess(String successJson) {
            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject result = new JSONObject(jsonObject.getString("result"));
                if (result.getString("state").equals("0")) {
                    mTabll.removeAllViews();
                    View mView = LayoutInflater.from(getActivity()).inflate(R.layout.home_tab, null);
                    TableLayout table = mView.findViewById(R.id.home_tab);
                    table.setStretchAllColumns(true);

                    ArrayList<HomeActivityEntity> mList = parseTabData(successJson);
                    for (int i = 0; i < mList.size(); i++) {
                        final HomeActivityEntity entity = mList.get(i);

                        switch (i) {
                            case 0:
                                ImageView mTab1Iv = mView.findViewById(R.id.iv_tab1);
                                TextView mTab1Tv = mView.findViewById(R.id.tv_tab1);
                                mTab1Tv.setText(entity.getTitle());
                                ImgLoaderManager.getInstance().showImageView(entity.getPicture(), mTab1Iv);
                                mView.findViewById(R.id.ll_tab1).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                        intent.putExtra("id", entity.getId());
                                        intent.putExtra("mid", mUserInfo.getId());
                                        intent.putExtra("homeActivity", "homeActivity");
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 1:
                                ImageView mTab2Iv = mView.findViewById(R.id.iv_tab2);
                                TextView mTab2Tv = mView.findViewById(R.id.tv_tab2);
                                mTab2Tv.setText(entity.getTitle());
                                ImgLoaderManager.getInstance().showImageView(entity.getPicture(), mTab2Iv);

                                mView.findViewById(R.id.ll_tab2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                        intent.putExtra("id", entity.getId());
                                        intent.putExtra("mid", mUserInfo.getId());
                                        intent.putExtra("homeActivity", "homeActivity");
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 2:
                                ImageView mTab3Iv = mView.findViewById(R.id.iv_tab3);
                                TextView mTab3Tv = mView.findViewById(R.id.tv_tab3);
                                mTab3Tv.setText(entity.getTitle());
                                ImgLoaderManager.getInstance().showImageView(entity.getPicture(), mTab3Iv);
                                mView.findViewById(R.id.ll_tab3).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                        intent.putExtra("id", entity.getId());
                                        intent.putExtra("mid", mUserInfo.getId());
                                        intent.putExtra("homeActivity", "homeActivity");
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 3:
                                ImageView mTab4Iv = mView.findViewById(R.id.iv_tab4);
                                TextView mTab4Tv = mView.findViewById(R.id.tv_tab4);
                                mTab4Tv.setText(entity.getTitle());
                                ImgLoaderManager.getInstance().showImageView(entity.getPicture(), mTab4Iv);
                                mView.findViewById(R.id.ll_tab4).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getActivity(), MainCreateActivity.class));
                                    }
                                });
                                break;
                        }

                    /*    for (int j = 0; j < 2; j++) {
                            LinearLayout linearLayout = new LinearLayout(getActivity());
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setBackground(getResources().getDrawable(R.drawable.table_frame_gray));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(3, 0, 0, 0);

                            TextView tv = new TextView(getActivity());
                            tv.setText(entity.getTitle());
                            tv.setPadding(0, 7, 0, 7);
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextColor(getResources().getColor(R.color.green_33));
                            tv.setTextSize(16);
                            tv.setLayoutParams(params);

                            ImageView imageView = new ImageView(getActivity());
                            imageView.setPadding(15, 7, 0, 7);
                            imageView.setLayoutParams(new ViewGroup.LayoutParams(40, 40));

                            ImgLoaderManager.getInstance().showImageView(entity.getPicture(), imageView);
                            linearLayout.addView(imageView);
                            linearLayout.addView(tv);


                            tableRow.addView(linearLayout);

                        }
                        table.addView(tableRow, new TableLayout.LayoutParams(MP, WC, 1));*/
                    }

                    mTabll.addView(mView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            ToastUtils.showToast("请求失败，请稍后重试!");
        }
    };

    @Override
    protected void setListener() {
        //  mTxtCountry.setOnClickListener(this);
        mTxtSearch.setOnClickListener(this);
        mTxtFilter.setOnClickListener(this);
        mTxtAllCategory.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
        mCloseBtn.setOnClickListener(this);

        //点击重新加载事件
        mMultipleStatusView.setOnLoadListener(new MultipleStatusView.OnActionListener() {
            @Override
            public void onLoad(View view) {
                if (NetWorkUtils.isNetworkConnected(getContext())) {
                    String thisUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
                    HomeDbHelper.index(thisUserId, mIOAuthCallBack);//首页轮播图

                    //获取标签
                    CustomerDbHelper.getIndexTags(mGetTagesCallBack);
                    //获取用户列表
                    CustomerDbHelper.getIndexUserList(thisUserId, mPage, mIOUserListCallBack);
                } else {
                    mMultipleStatusView.noNetwork();
                }
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;

        mPage = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                CustomerDbHelper.getIndexUserList(thisUserId, mPage, mIOUserListCallBack);
                HomeDbHelper.index(thisUserId, mIOAuthCallBack);//首页轮播图
                CustomerDbHelper.getIndexTags(mGetTagesCallBack);
                CustomerDbHelper.loadActivity(mIOActivityBack);//活动
            }
        }, 500);

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        mPage++;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                //获取用户列表
                CustomerDbHelper.getIndexUserList(thisUserId, mPage, mIOUserListCallBack);
            }
        }, 500);
    }


    //首页bann
    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {
            mMultipleStatusView.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                if (jsonObject1.getString("state").equals("0")) {
                    populateData(successJson);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            mMultipleStatusView.dismiss();
        }
    };

    private void populateData(String successJson) {//首页轮播图

        UserModel.doBanner(getActivity(), mBanner, parseBannerData(successJson));
    }

    private IOAuthCallBack mGetTagesCallBack = new IOAuthCallBack() {//获取标签
        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {
            mMultipleStatusView.dismiss();

            try {
                mTags.clear();
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                if (jsonObject1.getString("state").equals("0")) {
                    mTags = parseData(successJson);
                    mTagsLL.removeAllViews();
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

                    if (mTags.size() > 0) {
                        for (int i = 0; i < mTags.size(); i++) {
                            View mView = LayoutInflater.from(getActivity()).inflate(R.layout.home_tags, null);
                            LinearLayout homeLL = (LinearLayout) mView.findViewById(R.id.ll_home);
                            ImageView homeIv = (ImageView) mView.findViewById(R.id.iv_home);
                            TextView homeTv = (TextView) mView.findViewById(R.id.tv_tag);

                            homeTv.setText(mTags.get(i).getName());
                            ImgLoaderManager.getInstance().showImageView(mTags.get(i).getPicture(), homeIv);
                            final int finalI = i;
                            homeLL.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startCategoryListActivity(mTags.get(finalI).getName(), "1", mTags.get(finalI).getId());
                                }
                            });

                            mTagsLL.addView(mView, param);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            mMultipleStatusView.dismiss();
        }
    };

    //当前用户是否认证

    private void isAlert() {
        CustomerDbHelper.checkUser(thisUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("-1")) {//未完善资料
                        perfect(result.getString("description"));
                    } else if (result.getString("state").equals("-2")) {//未认证
                        certified(result.getString("description"));
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

    private void certified(String description) {//未认证
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setText("马上认证");
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
            }
        });
    }

    private void perfect(String description) {//未完善
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUser = new Intent(getActivity(), EditUserInfoActivity.class);
                Bundle bundleUser = new Bundle();
                bundleUser.putString("uid", thisUserId);
                intentUser.putExtras(bundleUser);
                startActivity(intentUser);
            }
        });
    }


    private IOAuthCallBack mIOCheckIsAlert = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {

            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject result = new JSONObject(jsonObject.getString("result"));
                if (result.getString("state").equals("0")) {
                    isAlert();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {

        }
    };
    //获取用户列表
    private IOAuthCallBack mIOUserListCallBack = new IOAuthCallBack() {


        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {

            mMultipleStatusView.dismiss();

            try {
                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                mLlList.removeAllViews();
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));


                if (jsonObject1.getString("state").equals("0")) {

                    for (HomePageUserBean item : parseUserData(successJson)) {
                        if (!mDataList.contains(item)) {
                            mDataList.add(item);
                        }
                    }

                    if (mDataList.size() > 0) {
                        for (int i = 0; i < mDataList.size(); i++) {
                            HomePageUserBean mItem = mDataList.get(i);
                            View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.home_list_item, null);
                            ViewHolder viewHolder = new ViewHolder();
                            viewHolder.mIvPicture = (RoundImageView) convertView.findViewById(R.id.home_list_item_iv_picture);
                            viewHolder.mIvIdenty = (ImageView) convertView.findViewById(R.id.home_list_item_iv_identy);
                            viewHolder.mTxtName = (TextView) convertView.findViewById(R.id.home_list_item_txt_name);
                            viewHolder.mTxtDesc = (TextView) convertView.findViewById(R.id.home_list_item_txt_desc);
                            // viewHolder.mCallTv = convertView.findViewById(R.id.tv_call);


                            // viewHolder.mTxtInfo = (TextView) convertView.findViewById(R.id.home_list_item_txt_info);
                            viewHolder.mTxtAppointment = (TextView) convertView.findViewById(R.id.home_list_item_txt_appointment);
                            //     viewHolder.mTxtMoney = (TextView) convertView.findViewById(R.id.home_list_item_txt_money);
                            viewHolder.mLinear = (LinearLayout) convertView.findViewById(R.id.content);

                            if (!"null".equals(mItem.getUsername()) && null != mItem.getUsername()) {
                                viewHolder.mTxtName.setText(mItem.getUsername());
                            }

                            ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), viewHolder.mIvPicture);
                            if (mItem.getStatus().equals("1")) {
                                viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
                                Glide.with(getContext()).load(R.drawable.icon_authen).into(viewHolder.mIvIdenty);
                            }
                            else if(mItem.getStatus().equals("4")) {
                                viewHolder.mIvIdenty.setVisibility(View.VISIBLE);
                                Glide.with(getContext()).load(R.mipmap.user_vip).into(viewHolder.mIvIdenty);
                            }
                            else {
                                viewHolder.mIvIdenty.setVisibility(View.GONE);
                            }

                            if ("" != mItem.getPosition() && "" != mItem.getCompany() && null != mItem.getPosition() && null != mItem.getCompany()) {
                                viewHolder.mTxtDesc.setText(mItem.getPosition() + " | " + mItem.getCompany());
                            } else if ("" != mItem.getCompany()) {
                                viewHolder.mTxtDesc.setText(mItem.getCompany());
                            } else if ("" != mItem.getPosition()) {
                                viewHolder.mTxtDesc.setText(mItem.getPosition());
                            }


                            state = mItem.getIsJob();
                         /*   if ("1".equals(mItem.getIsJob())) {
                                viewHolder.mTxtMoney.setText(mItem.getMoney() + "元/" + 24 + "小时");
                            } else {
                                viewHolder.mTxtMoney.setText(mItem.getMoney() + "元/" + mItem.getTime() + "分钟");
                            }*/
                        /*    String str = "被成功邀请:<font color='#FF0000'>" + mItem.getInviteSuccessCount() + "</font>次";
                            viewHolder.mTxtInfo.setText(Html.fromHtml(str));*/

                            final String money = mItem.getMoney();
                            final String id = mItem.getId();
                            final String name = mItem.getUsername();

                            viewHolder.mTxtAppointment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!BaseApplication.getInstance().checkLogin(getContext())) {
                                        Intent intent = new Intent();
                                        intent.setClass(getContext(), com.android.nana.auth.WelcomeActivity.class);
                                        getContext().startActivity(intent);
                                        return;
                                    }

                                    mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(getContext(), "userInfo", UserInfo.class);
                                    mAppointmentModel.init(mUserInfo.getId(), id, mUserInfo.getPayPassword());
                                    mAppointmentModel.doDialog(money, name);
                                }
                            });

                            viewHolder.mLinear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!BaseApplication.getInstance().checkLogin(getContext())) {

                                        getContext().startActivity(new Intent(getContext(), com.android.nana.auth.WelcomeActivity.class));
                                        return;
                                    }

                                    Intent intent = new Intent(getActivity(), EditDataActivity.class);
                                    intent.putExtra("UserId", id);
                                    startActivity(intent);
                                }
                            });

                            mLlList.addView(convertView);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            mMultipleStatusView.dismiss();
            mMultipleStatusView.error();
        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.home_layout_txt_country:
                startActivity(new Intent(getActivity(), SelectedCountryActivity.class));
                break;
            case R.id.home_layout_txt_search:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", state);
                startActivity(intentSearch);
                break;
            case R.id.home_layout_txt_filter:
                Intent intentScreen = new Intent(getActivity(), ScreenActivity.class);
                intentScreen.putExtra("state", state);
                startActivity(intentScreen);
                break;
            case R.id.home_txt_all_category:
                Intent intent = new Intent(getActivity(), CategoryListActivity.class);
                intent.putExtra("Name", "全部");
                intent.putExtra("CategoryId", "1");
                intent.putExtra("ChildCategoryId", "0");
                intent.putExtra("count", "count");
                startActivity(intent);
                break;
            case R.id.close_iv:
                mPerfectDialog.dismiss();
                break;
        }
    }

    public ArrayList<HomeTagsBean> parseData(String result) {//Gson 解析
        ArrayList<HomeTagsBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                HomeTagsBean entity = gson.fromJson(data.optJSONObject(i).toString(), HomeTagsBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<HomePageUserBean> parseUserData(String result) {//Gson 解析
        ArrayList<HomePageUserBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                HomePageUserBean entity = gson.fromJson(data.optJSONObject(i).toString(), HomePageUserBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private void startCategoryListActivity(String name, String categoryId, String childCategoryId) {

        if (childCategoryId.equals("1")) {
            startActivity(new Intent(getActivity(), AllCategoryListActivity.class));
        } else {
            Intent intent = new Intent(getActivity(), CategoryListActivity.class);
            intent.putExtra("Name", name);
            intent.putExtra("CategoryId", categoryId);
            intent.putExtra("ChildCategoryId", childCategoryId);
            startActivity(intent);
        }
    }

    class ViewHolder {
        RoundImageView mIvPicture;
        ImageView mIvIdenty;
        LinearLayout mLinear;
        TextView mTxtName, mTxtInfo, mTxtDesc, mTxtAppointment, mTxtAttention, mTxtMoney, mCallTv;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(HomePageFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onHomeEvent(HomePageEvent homePageEvent) {

        mLayout.autoRefresh();
    }

    public ArrayList<BannerEntity> parseBannerData(String result) {//Gson 解析
        ArrayList<BannerEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                BannerEntity entity = gson.fromJson(data.optJSONObject(i).toString(), BannerEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<HomeActivityEntity> parseTabData(String result) {//Gson 解析
        ArrayList<HomeActivityEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                HomeActivityEntity entity = gson.fromJson(data.optJSONObject(i).toString(), HomeActivityEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


}
