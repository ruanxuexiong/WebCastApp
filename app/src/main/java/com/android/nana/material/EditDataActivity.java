package com.android.nana.material;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnDismissListener;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.customer.EditWorkActivity;
import com.android.nana.customer.EducationListActivity;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.NewMsgEvent;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.friend.AlbumDetailsActivity;
import com.android.nana.friend.AlbumEntity;
import com.android.nana.friend.WholeAlbumActivity;
import com.android.nana.friend.WholeAlbumAdapter;
import com.android.nana.inquiry.ConversationListActivity;
import com.android.nana.listener.MainListener;
import com.android.nana.mail.OperationRong;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.ratingbar.RatingBar;
import com.android.nana.transition.TransitionConstant;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.user.AddFriendActivity;
import com.android.nana.user.SealUserInfoManager;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.MD5;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.DrawableTextView;
import com.android.nana.widget.LabelsView;
import com.android.nana.widget.PasswordDialog;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by lenovo on 2018/6/1.
 */

public class EditDataActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnDismissListener, PullToRefreshLayout.OnRefreshListener, WholeAlbumAdapter.WholeAlbumListener {

    private int page = 1;
    private ImageView mBackgrounIv, mIdentyIv;
    private AlertView mAlertView;
    private TextView mAction2;
    private String uid, mThisId, mLogo;
    private RoundedImageView mAvatar;
    private String isfollow = "0";
    private View mTagsView;
    private LinearLayout mWorkLL, mWorkExperienceLL, mEducationLL, mEducationExperienceLL, mTagesLL, mTagesDataLL, mAlbumLL, mAlbumDataLL;
    private String mMoneyStr, mNameStr;
    private TextView mNameTv, mPositionTv, mAddressTv, mBtnTv, mMoneyTv, mMoreTv, mEducationTv, mAlbumTv, mNumTv;
    private String mHead, mHasmeet, mStatus, mFriend;
    private String mIsLimit, mLimitTip;
    private RoundedImageView mRivHead;
    private TransitionSingleHelper mTransiTion;
    private String mFaceModeStr;//三分钟模式状态

    private TextView mBackTv, mTitleTv, mIntroduceTv, mCallTv, mSendMsgTv, mAgreeTv;
    private RelativeLayout mNavigationShare, mNavigationFollow, mNavigationMore;
    private RelativeLayout mNavigationFriend;
    private CheckBox mFollowCheckBox, mFriendCheckBox;
    private View mToolbarView;
    private LinearLayout mSendMsgLl;
    private LinearLayout mBottomll;
    private TextView mAddMsgTv;
    private LinearLayout mMsgLl, mAddUserLl;
    private DrawableTextView mSendMsg;
    private boolean isAgree = false;//是否显示同意拒绝按钮
    private String mIsTanchang, mMoney, mEnough;
    private ImageView mSexIv;
    private RatingBar mRatingBar;

    private Dialog mDialog;
    private View mView, mMeView;
    private CircleImageView mHeadIv;
    private LinearLayout mTipLl;
    private TextView mTipTv;
    private ImageView mSureIv;
    private TextView mDialogNameTv;
    private TextView mTimeTv;
    private RelativeLayout mRechargeRl;
    private TextView mBalanceTv;
    private TextView mTimeRestTv;
    private TextView mLaunchTv;
    private TextView mSanTv;
    private String mAvatarStr;
    private String mChargeStr;
    private String mIsFreeStr;
    private String mBalanceStr;
    private String mTimeLeftStr;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private ArrayList<AlbumEntity.Articles> mDataList = new ArrayList<>();

    private boolean isZan = false;//是否点击加载列表
    private WholeAlbumAdapter mAdapter;
    //高德
    private boolean isLocation = false;//获取经纬度
    public LatLng BEIJING;//经纬度
    private double lng, lat;
    private AMapLocationClient locationClient = null;
    private CustomPopWindow mCustomPopWindow;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(EditDataActivity.this)) {
            EventBus.getDefault().register(EditDataActivity.this);
        }
        SealUserInfoManager.getInstance().openDB();
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_detailed);
    }

    @Override
    protected void findViewById() {
        mMeView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.activity_user_details, null);

        mNameTv = mMeView.findViewById(R.id.tv_name);
        mMoneyTv = mMeView.findViewById(R.id.tv_money);
        mBtnTv = findViewById(R.id.tv_btn);
        mWorkLL = mMeView.findViewById(R.id.ll_work);
        mMoreTv = mMeView.findViewById(R.id.tv_more);
        mTagsView = mMeView.findViewById(R.id.view_tags);
        mWorkExperienceLL = mMeView.findViewById(R.id.ll_work_experience);
        mEducationLL = mMeView.findViewById(R.id.ll_education);
        mEducationTv = mMeView.findViewById(R.id.tv_education_more);
        mEducationExperienceLL = mMeView.findViewById(R.id.ll_education_experience);

        mTagesLL = mMeView.findViewById(R.id.ll_tags);
        mTagesDataLL = mMeView.findViewById(R.id.ll_tags_data);
        mAddressTv = mMeView.findViewById(R.id.tv_address);
        mPositionTv = mMeView.findViewById(R.id.tv_details);

        mAvatar = mMeView.findViewById(R.id.iv_head);
        mAlbumLL = mMeView.findViewById(R.id.ll_album);
        mAlbumDataLL = mMeView.findViewById(R.id.ll_album_data);
        mAlbumTv = mMeView.findViewById(R.id.tv_album);
        mBackgrounIv = mMeView.findViewById(R.id.customer_detail_iv_top);
        mNavigationShare = mMeView.findViewById(R.id.navigation_share);
        mRivHead = mMeView.findViewById(R.id.iv_head);
        mSexIv = mMeView.findViewById(R.id.iv_sex);
        mIdentyIv = mMeView.findViewById(R.id.iv_identy);
        mNumTv = mMeView.findViewById(R.id.tv_num);
        mRatingBar = mMeView.findViewById(R.id.ratingBar);
        mIntroduceTv = mMeView.findViewById(R.id.tv_introduce);
        mToolbarView = mMeView.findViewById(R.id.toolbar_view);

        mFollowCheckBox = mMeView.findViewById(R.id.checkBox_follow);
        mNavigationFollow = mMeView.findViewById(R.id.navigation_follow);

        mNavigationMore = mMeView.findViewById(R.id.navigation_more);
        mFriendCheckBox = mMeView.findViewById(R.id.checkBox_friend);
        mNavigationFriend = mMeView.findViewById(R.id.navigation_friend);

        //好友状态发送消息
        mSendMsgLl = findViewById(R.id.ll_send_msg);
        mSendMsgTv = findViewById(R.id.tv_send_msg);
        mCallTv = findViewById(R.id.tv_call);//直接视频

        //消息
        mMsgLl = mMeView.findViewById(R.id.ll_msg);
        mAddMsgTv = mMeView.findViewById(R.id.tv_add_msg);
        mAddUserLl = mMeView.findViewById(R.id.ll_add_user);
        mAgreeTv = findViewById(R.id.tv_agree);

        mBottomll = findViewById(R.id.ll_bottom);
        mSendMsg = findViewById(R.id.tv_msg);

        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);

        mAction2 = findViewById(R.id.toolbar_right_2);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("UserId")) {
            uid = getIntent().getStringExtra("UserId");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");

            if (uid.equals(mThisId)) {
                mBottomll.setVisibility(View.GONE);
                mAction2.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.icon_me_edit);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAction2.setCompoundDrawables(drawable, null, null, null);
                mAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAction2Click();
                    }
                });
            } else {
                mAction2.setVisibility(View.VISIBLE);
                mAction2.setText("设置");
                mAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomMenu();
                    }
                });
            }

            initData(uid, mThisId);
        }
        mTransiTion = new TransitionManager(EditDataActivity.this).getSingle();


        mToolbarView.setVisibility(View.GONE);
        mBackTv.setVisibility(View.VISIBLE);

        if (null != getIntent().getStringExtra("newFriend") && null != getIntent().getStringExtra("follow") && getIntent().getStringExtra("follow").equals("2")) {
            uid = getIntent().getStringExtra("uid");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
            initData(uid, mThisId);
        } else if (null != getIntent().getStringExtra("newFriend") && null != getIntent().getStringExtra("follow") && getIntent().getStringExtra("follow").equals("1")) {
            uid = getIntent().getStringExtra("uid");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
            initData(uid, mThisId);

        } else if (null != getIntent().getStringExtra("newFriend") && null != getIntent().getStringExtra("follow") && getIntent().getStringExtra("follow").equals("0")) {

            uid = getIntent().getStringExtra("uid");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");

            mMsgLl.setVisibility(View.VISIBLE);//显示添加好消息
            mAddUserLl.setVisibility(View.GONE);//隐藏分享等控件
            mSendMsgLl.setVisibility(View.VISIBLE);//拒绝 同意
            mBtnTv.setVisibility(View.GONE);//约见按钮
            mAddMsgTv.setText(getIntent().getStringExtra("msg"));
            mSendMsgTv.setText("拒绝");

            mAgreeTv.setText("同意");
            mAgreeTv.setVisibility(View.VISIBLE);
            isAgree = true;
            initData(uid, mThisId);

        } else if (null != getIntent().getStringExtra("newFriend") && null != getIntent().getStringExtra("follow") && getIntent().getStringExtra("follow").equals("3")) {

            uid = getIntent().getStringExtra("uid");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
            initData(uid, mThisId);
        }
        initDialog();
        mListView.addHeaderView(mMeView);
        mAdapter = new WholeAlbumAdapter(EditDataActivity.this, mDataList, this, mThisId);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
        if (null != getIntent().getStringExtra("UserId")) {
            uid = getIntent().getStringExtra("UserId");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");

            if (uid.equals(mThisId)) {
                mBottomll.setVisibility(View.GONE);
                mAction2.setVisibility(View.VISIBLE);
                Drawable drawable = getResources().getDrawable(R.drawable.icon_me_edit);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAction2.setCompoundDrawables(drawable, null, null, null);
                mAction2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAction2Click();
                    }
                });
            }
            isLocation = true;
        }
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this);
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }


    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }


    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                double lngMath = location.getLongitude();//经    度
                double latMath = location.getLatitude();//纬    度
                lng = (Math.round(lngMath * 1000000) / 1000000.0);
                lat = (Math.round(latMath * 1000000) / 1000000.0);
                if (lng != 0.0 && lat != 0.0 && isLocation) {
                    loadLngLatData(isLocation);
                } else {
                    loadLngLatData(isLocation);
                }
                locationClient.stopLocation();
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    private void loadLngLatData(boolean is) {
        this.isLocation = false;
        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mThisId, uid, page, lng, lat, new IOAuthCallBack() {
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
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        if (parseAlbumData(successJson).size() > 0) {
                            mAlbumLL.setVisibility(View.VISIBLE);
                            mAlbumTv.setVisibility(View.VISIBLE);
                            mAlbumTv.setText(mNameStr + "的动态");
                            for (AlbumEntity.Articles item : parseAlbumData(successJson)) {
                                mDataList.add(item);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("数据请求失败");
            }
        });
    }

    private void onAction2Click() {
        Intent intent = new Intent(this, EditPersonalActivity.class);
        intent.putExtra("thisUid", mThisId);
        startActivity(intent);
    }

    private void initDialog() {
        mView = LayoutInflater.from(this).inflate(R.layout.inquiry_dialog, null);
        mDialog = new AlertDialog.Builder(this).create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mTipLl = mView.findViewById(R.id.ll_tip);
        mTipTv = mView.findViewById(R.id.tv_tip);
        mHeadIv = mView.findViewById(R.id.iv_head);
        mSureIv = mView.findViewById(R.id.iv_sure);
        mDialogNameTv = mView.findViewById(R.id.tv_name);
        mTimeTv = mView.findViewById(R.id.tv_time);
        mRechargeRl = mView.findViewById(R.id.rl_recharge);
        mBalanceTv = mView.findViewById(R.id.tv_balance);
        mTimeRestTv = mView.findViewById(R.id.tv_rest_time);
        mLaunchTv = mView.findViewById(R.id.tv_launch);
        mSanTv = mView.findViewById(R.id.tv_san);
    }

    private void initData(final String uid, final String mThisId) {
        showProgressDialog("", "加载中请稍后...");
        WebCastDbHelper.getUserInfo(uid, mThisId, "2", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));

                    if ("null" != jsonObject1.getString("username")) {
                        mNameTv.setText(jsonObject1.getString("username"));
                        mNameStr = jsonObject1.getString("username");
                        mTitleTv.setText(mNameStr);
                    }

                    if (null != jsonObject1.getString("backgroundImage")) {
                        mBackgrounIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(jsonObject1.getString("backgroundImage"), mBackgrounIv);
                    }

                    if (jsonObject1.getString("isfollow").equals("1")) {
                        isfollow = jsonObject1.getString("isfollow");
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_check_follow);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFollowCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFollowCheckBox.setText("已关注");
                        mFollowCheckBox.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_user_follow);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFollowCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFollowCheckBox.setTextColor(getResources().getColor(R.color.green__66));
                    }

                    mHasmeet = jsonObject1.getString("hasmeet");//是否有见面记录

                    if (null != jsonObject1.getString("isLimit")) {
                        mIsLimit = jsonObject1.getString("isLimit");
                    }
                    if (null != jsonObject1.getString("limitTip")) {
                        mLimitTip = jsonObject1.getString("limitTip");
                    }
                    if (jsonObject1.getString("status").equals("1")) {
                        mStatus = jsonObject1.getString("status");
                        mIdentyIv.setVisibility(View.VISIBLE);
                    } else {
                        mIdentyIv.setVisibility(View.GONE);
                    }

                    JSONObject mTanchuang = new JSONObject(jsonObject1.getString("tanchuang"));
                    mIsTanchang = mTanchuang.getString("isTanchang");
                    mMoney = mTanchuang.getString("money");
                    mEnough = mTanchuang.getString("enough");


                    //是否是好友
                    mFriend = jsonObject1.getString("friend");
                    if (mFriend.equals("0")) {
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_add_friend);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFriendCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFriendCheckBox.setText("加好友");
                        mFriendCheckBox.setTextColor(getResources().getColor(R.color.green__66));
                    } else if (mFriend.equals("1")) {
                        Drawable drawable = getResources().getDrawable(R.drawable.icon_request);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFriendCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFriendCheckBox.setText("已请求");
                        mFriendCheckBox.setTextColor(getResources().getColor(R.color.green__66));
                        mNavigationFriend.setClickable(false);
                    } else if (mFriend.equals("2")) {
                        Drawable drawable = getResources().getDrawable(R.drawable.icon_add_friend_show);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFriendCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFriendCheckBox.setText("好友");
                        mFriendCheckBox.setTextColor(getResources().getColor(R.color.green));

                        mSendMsgLl.setVisibility(View.GONE);
                        mBtnTv.setVisibility(View.GONE);
                        //  mCallTv.setVisibility(View.VISIBLE);
                        mNavigationFriend.setClickable(true);
                        mAgreeTv.setVisibility(View.GONE);
                        //   mSendMsgTv.setText("发送消息");
                        mSendMsg.setVisibility(View.VISIBLE);//发送消息
                        mMoneyTv.setVisibility(View.GONE);
                        mBtnTv.setVisibility(View.GONE);//约见按钮

                        HomeDbHelper.getUserName(uid, mThisId, new IOAuthCallBack() {//更新聊天头像
                            @Override
                            public void onStartRequest() {

                            }

                            @Override
                            public void getSuccess(String successJson) {
                                try {
                                    JSONObject jsonObject = new JSONObject(successJson);
                                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                                    if (result.getString("state").equals("0")) {
                                        for (FriendsBookEntity entity : parseNmaeData(successJson)) {
                                            UserInfo userInfo = new UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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

                    } else if (mFriend.equals("3")) {
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_add_friend);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mFriendCheckBox.setCompoundDrawables(null, drawable, null, null);
                        mFriendCheckBox.setText("已拒绝");
                        mFriendCheckBox.setTextColor(getResources().getColor(R.color.green__66));
                    }

                    if (null != jsonObject1.getString("isFree")) {//是否显示首次三分钟显示
                        mIsFreeStr = jsonObject1.getString("isFree");
                        if (mIsFreeStr.equals("1")) {
                            mSanTv.setVisibility(View.VISIBLE);
                        } else {
                            mSanTv.setVisibility(View.GONE);
                        }
                    }
                    if (null != jsonObject1.getString("money_time")) {
                        mChargeStr = jsonObject1.getString("charge");
                        if (mIsFreeStr.equals("1")) {
                            //mMoneyTv.setText("收费：" + jsonObject1.getString("charge") + "/分钟" + "（前3分钟免费）");
                        } else {
                            //  mMoneyTv.setText("收费：" + jsonObject1.getString("charge") + "/分钟");
                        }
                        mMoneyTv.setVisibility(View.GONE);
                    } else {
                        mMoneyTv.setVisibility(View.GONE);
                    }

                    mFaceModeStr = jsonObject1.getString("faceMode");//三分钟模式
                    switch (mFaceModeStr) {
                        case "1":
                            break;
                        case "2":
                            if (!isAgree) {
                                mMoneyTv.setVisibility(View.VISIBLE);
                                mMoneyTv.setText("见面金额：由您提出合理的见面金额");
                                mSendMsgLl.setVisibility(View.VISIBLE);
                                mSendMsgTv.setText("发送消息");
                                mSendMsgTv.setVisibility(View.VISIBLE);
                                mAgreeTv.setVisibility(View.GONE);
                                mSendMsg.setVisibility(View.GONE);//发送消息
                                mCallTv.setVisibility(View.VISIBLE);//约见按钮
                                mBtnTv.setVisibility(View.GONE);//约见按钮
                            } else {
                                isAgree = false;
                            }
                            break;
                        case "3":
                           /* mSendMsgLl.setVisibility(View.VISIBLE);
                            mSendMsgTv.setText("发送消息");
                            mSendMsgTv.setVisibility(View.GONE);
                            mAgreeTv.setVisibility(View.GONE);
                            mSendMsg.setVisibility(View.GONE);//发送消息
                            mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                            mCallTv.setVisibility(View.VISIBLE);//约见按钮
                            mBtnTv.setVisibility(View.GONE);//约见按钮*/
                            break;
                        case "4":
                            mSendMsg.setVisibility(View.VISIBLE);//发送消息
                            mMoneyTv.setVisibility(View.GONE);//显示金额
                            mBtnTv.setVisibility(View.GONE);//约见按钮
                            break;
                        case "5"://未开启三分钟模式+非好友
                            if (!isAgree) {
                                mSendMsg.setVisibility(View.GONE);//发送消息
                                mMoneyTv.setVisibility(View.GONE);//显示金额
                                mBtnTv.setVisibility(View.VISIBLE);//约见按钮
                            } else {
                                isAgree = false;
                            }
                            break;
                        default:
                            break;
                    }


                    //身份id
                    if (!"".equals(jsonObject1.getString("idcard"))) {
                        mNumTv.setText("ID:" + jsonObject1.getString("idcard"));
                    }


                    if (jsonObject1.getString("sex").equals("1")) {
                        mSexIv.setImageDrawable(getDrawable(R.drawable.icon_sex_male));
                    } else if (jsonObject1.getString("sex").equals("2")) {
                        mSexIv.setImageDrawable(getDrawable(R.drawable.icon_sex_female));
                    } else {
                        mSexIv.setVisibility(View.GONE);
                    }

                    if (null != jsonObject1.getString("avatar")) {
                        mLogo = jsonObject1.getString("avatar");
                    } else {
                        mLogo = "http://www.facethree.com/themes/simplebootx/Public/images/zblogo.png";
                    }

                    mAvatarStr = jsonObject1.getString("avatar");
                    ImgLoaderManager.getInstance().showImageView(jsonObject1.getString("avatar"), mAvatar);

                    if ("".equals(jsonObject1.getString("avatar"))) {
                        mHead = "http://www.facethree.com/data/upload/share/person.png";
                    } else {
                        mHead = jsonObject1.getString("avatar");
                    }

                    if (!"".equals(jsonObject1.getString("position")) && !"".equals(jsonObject1.getString("company"))) {
                        mPositionTv.setText(jsonObject1.getString("position") + " | " + jsonObject1.getString("company"));
                    } else if (!"".equals(jsonObject1.getString("position"))) {
                        mPositionTv.setText(jsonObject1.getString("position"));
                    } else if (!"".equals(jsonObject1.getString("company"))) {
                        mPositionTv.setText(jsonObject1.getString("company"));
                    } else {
                        mPositionTv.setVisibility(View.GONE);
                    }

                    if ("null" != jsonObject1.getString("province") && !"null".equals(jsonObject1.getString("city")) && "null" != jsonObject1.getString("district")) {
                        mAddressTv.setText("常居：" + jsonObject1.getString("province") + "·" + jsonObject1.getString("city") + "·" + jsonObject1.getString("district"));
                    } else if ("null" != jsonObject1.getString("province") && "null" != jsonObject1.getString("city")) {
                        mAddressTv.setText("常居：" + jsonObject1.getString("province") + "·" + jsonObject1.getString("city"));
                    } else {
                        mAddressTv.setVisibility(View.GONE);
                    }

                    if (!"".equals(jsonObject1.getString("introduce"))) {
                        mIntroduceTv.setText("简介：" + jsonObject1.getString("introduce"));
                    } else {
                        mIntroduceTv.setText("简介：暂无简介");
                    }


                    mIntroduceTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EditDataActivity.this, ProfileActivity.class);
                            intent.putExtra("title", mPositionTv.getText().toString());
                            intent.putExtra("introduce", mIntroduceTv.getText().toString());
                            startActivity(intent);
                        }
                    });


                    mMoneyStr = jsonObject1.getString("money");


                    //工作经历
                    List<JSONObject> workHistorys = JSONUtil.getList(jsonObject1, "workHistorys");

                    if (workHistorys.size() > 0) {
                        mWorkLL.setVisibility(View.VISIBLE);
                        mWorkExperienceLL.setVisibility(View.VISIBLE);
                        mWorkExperienceLL.removeAllViews();
                        mMoreTv.setVisibility(View.VISIBLE);
                        for (int i = 0; i < workHistorys.size(); i++) {
                            JSONObject mItem = workHistorys.get(i);
                            View mView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.work_experience_item, null);
                            TextView mTitle = mView.findViewById(R.id.tv_title);
                            TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                            ImageView mPicture = mView.findViewById(R.id.iv_picture);
                            TextView mTimeTv = mView.findViewById(R.id.tv_time);
                            mView.findViewById(R.id.ll_img).setVisibility(View.GONE);

                            new ImgLoaderManager(R.drawable.icon_work).showImageView(mItem.getString("picture"), mPicture);
                            mTitle.setText(mItem.getString("name"));
                            mCompanyTv.setText(mItem.getString("position"));
                            if ("".equals(mItem.getString("beginTime"))) {
                                mTimeTv.setText("");
                            } else {
                                mTimeTv.setText(mItem.getString("beginTime") + "-" + mItem.getString("endTime"));
                            }
                            mWorkExperienceLL.addView(mView);
                        }
                        mMoreTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(EditDataActivity.this, EditWorkActivity.class);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });

                    } else {
                        mWorkLL.setVisibility(View.GONE);
                        mWorkExperienceLL.setVisibility(View.GONE);
                    }


                    //教育经历
                    List<JSONObject> education = JSONUtil.getList(jsonObject1, "educationExperiences");

                    if (education.size() > 0) {
                        mEducationTv.setVisibility(View.VISIBLE);
                        mEducationLL.setVisibility(View.VISIBLE);
                        mEducationExperienceLL.setVisibility(View.VISIBLE);
                        mEducationExperienceLL.removeAllViews();
                        if (education.size() > 2) {
                            mEducationTv.setVisibility(View.VISIBLE);
                            for (int i = 0; i < education.size(); i++) {
                                JSONObject mItem = education.get(i);
                                View mView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.work_experience_item, null);
                                TextView mTitle = mView.findViewById(R.id.tv_title);
                                TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                mView.findViewById(R.id.ll_img).setVisibility(View.GONE);
                                new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getString("picture"), mPicture);
                                mTitle.setText(mItem.getString("name"));
                                mCompanyTv.setText(mItem.getString("major"));
                                mTimeTv.setText(mItem.getString("beginTime") + "-" + mItem.getString("endTime"));

                                mEducationExperienceLL.addView(mView);
                            }

                            mEducationTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(EditDataActivity.this, EducationListActivity.class);
                                    intent.putExtra("userId", uid);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            mEducationTv.setVisibility(View.VISIBLE);
                            for (int i = 0; i < education.size(); i++) {
                                JSONObject mItem = education.get(i);
                                View mView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.work_experience_item, null);
                                TextView mTitle = mView.findViewById(R.id.tv_title);
                                TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                mView.findViewById(R.id.ll_img).setVisibility(View.GONE);
                                new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getString("picture"), mPicture);
                                mTitle.setText(mItem.getString("name"));
                                mCompanyTv.setText(mItem.getString("major"));
                                mTimeTv.setText(mItem.getString("beginTime") + "-" + mItem.getString("endTime"));

                                mEducationExperienceLL.addView(mView);
                            }

                            mEducationTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(EditDataActivity.this, EducationListActivity.class);
                                    intent.putExtra("userId", uid);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        mEducationTv.setVisibility(View.GONE);
                        mEducationLL.setVisibility(View.GONE);
                        mEducationExperienceLL.setVisibility(View.GONE);
                    }

                    //标签
                    List<JSONObject> tages = JSONUtil.getList(jsonObject1, "property");

                    if (null != tages && tages.size() > 0) {
                        mTagesLL.setVisibility(View.VISIBLE);
                        mTagsView.setVisibility(View.VISIBLE);
                        mTagesDataLL.setVisibility(View.VISIBLE);
                        mTagesDataLL.removeAllViews();
                        View mView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.item_tages, null);
                        LabelsView mLabelsView = mView.findViewById(R.id.lv_name);
                        mLabelsView.setSelectType(LabelsView.SelectType.NONE);//标签不可选
                        ArrayList<String> label = new ArrayList<>();
                        for (int i = 0; i < tages.size(); i++) {
                            JSONObject mItem = tages.get(i);
                            if (null != mItem.getString("parentName")) {
                                label.add(mItem.getString("parentName") + "-" + mItem.getString("name"));
                            } else {
                                mTagesDataLL.setVisibility(View.GONE);
                                mTagesLL.setVisibility(View.GONE);
                                mTagsView.setVisibility(View.GONE);
                            }
                        }
                        mLabelsView.setLabels(label);
                        mTagesDataLL.addView(mView);
                    } else {
                        mTagesDataLL.setVisibility(View.GONE);
                        mTagesLL.setVisibility(View.GONE);
                        mTagsView.setVisibility(View.GONE);
                    }
                    //相册
                    if (jsonObject1.getJSONArray("pictures").length() > 0) {
                        mAlbumTv.setText(mNameStr + "的动态");
                        mAlbumDataLL.removeAllViews();
                        for (int i = 0; i < jsonObject1.getJSONArray("pictures").length(); i++) {
                            View mView = LayoutInflater.from(EditDataActivity.this).inflate(R.layout.item_images, null);
                            ImageView image = mView.findViewById(R.id.iv_img);
                            ImgLoaderManager.getInstance().showImageView(jsonObject1.getJSONArray("pictures").get(i).toString(), image);
                            mAlbumDataLL.addView(mView);
                        }
                    } else {
                        mAlbumLL.setVisibility(View.GONE);
                        mAlbumDataLL.setVisibility(View.GONE);
                        mAlbumTv.setVisibility(View.GONE);
                    }

                    addGroupImage(Float.valueOf(jsonObject1.getString("stars")));
                    mBalanceStr = jsonObject1.getString("MyTotalBalance");
                    mTimeLeftStr = jsonObject1.getString("timeLeft");
                    dismissProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void setListener() {
        mNavigationShare.setOnClickListener(this);
        mNavigationFollow.setOnClickListener(this);
        mNavigationMore.setOnClickListener(this);
        mNavigationMore.setOnClickListener(this);
        mNavigationFriend.setOnClickListener(this);

        mAlbumLL.setOnClickListener(this);
        mBtnTv.setOnClickListener(this);
        mRivHead.setOnClickListener(this);
        mBackTv.setOnClickListener(this);

        mAgreeTv.setOnClickListener(this);
        mSendMsgTv.setOnClickListener(this);
        mCallTv.setOnClickListener(this);
        mSendMsg.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_follow:
                follow();
                break;
            case R.id.ll_album:
                Intent intent = new Intent(EditDataActivity.this, WholeAlbumActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            case R.id.tv_btn:
                if (!Utils.isDoubleClick()) {
                    sendMsg("5");
                  /*  ImageLoader.getInstance().displayImage(mAvatarStr, mHeadIv);
                    mDialogNameTv.setText(mNameStr);
                    mTimeTv.setText("收费标准：" + mChargeStr + "/分钟");
                    mBalanceTv.setText("账户余额：" + mBalanceStr + "元");
                    mTimeRestTv.setText("可通话" + mTimeLeftStr + "分钟");

                    if (null != mIsLimit && "1".equals(mIsLimit)) {
                        mTipLl.setVisibility(View.VISIBLE);
                        mTipTv.setText(mLimitTip);
                    }

                    mRechargeRl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(EditDataActivity.this, RechargeActivity.class));
                        }
                    });

                    mLaunchTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initStart();
                        }
                    });

                    mDialog.show();
                    mDialog.getWindow().setContentView(mView);*/
                }
                break;
            case R.id.iv_head:
                mTransiTion.startViewerActivity(mRivHead, mHead);
                break;
            case R.id.navigation_share:
                share();
                break;
            case R.id.iv_toolbar_back:
                EditDataActivity.this.finish();
                break;
            case R.id.navigation_more:
                bottomMenu();
                break;
            case R.id.navigation_friend:

                if (uid.equals(mThisId)) {
                    ToastUtils.showToast("自己不能添加自己为好友！");
                    return;
                }
                if (mHasmeet.equals("1") && !mFriend.equals("2")) {
                    Intent intent1 = new Intent(EditDataActivity.this, AddFriendActivity.class);
                    intent1.putExtra("uid", uid);
                    intent1.putExtra("userName", mNameStr);
                    intent1.putExtra("info", mIntroduceTv.getText().toString());
                    intent1.putExtra("logo", mLogo);
                    intent1.putExtra("thisUserId", mThisId);
                    intent1.putExtra("status", mStatus);
                    startActivity(intent1);
                } else if (mFriend.equals("2")) {
                    friend2Dialog();//移除好友
                } else {
                    friendDialog();
                }
                break;
            case R.id.tv_call:

                if (!Utils.isDoubleClick()) {//防止用户重复点击按钮
                 /*   AppointmentModel mAppointmentModel = new AppointmentModel(EditDataActivity.this);
                    if (mFaceModeStr.equals("2")) {
                        mAppointmentModel.init(mThisId, uid, "");
                        mAppointmentModel.doPriceDialog(mMoneyStr, mNameStr);
                    } else {
                        mAppointmentModel.init(mThisId, uid, "");
                        mAppointmentModel.doDialog(mMoneyStr, mNameStr);
                    }*/

                    ImageLoader.getInstance().displayImage(mAvatarStr, mHeadIv);
                    mDialogNameTv.setText(mNameStr);
                    mTimeTv.setText("收费标准：" + mChargeStr + "/分钟");
                    mBalanceTv.setText("账户余额：" + mBalanceStr + "元");
                    mTimeRestTv.setText("可通话" + mTimeLeftStr + "分钟");

                    mRechargeRl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(EditDataActivity.this, RechargeActivity.class));
                        }
                    });

                    mLaunchTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showProgressDialog("", "加载中...");
                            initStart();
                        }
                    });

                    mDialog.show();
                    mDialog.getWindow().setContentView(mView);
                }
                break;
            case R.id.tv_send_msg:
                if (!Utils.isDoubleClick()) {
                    if (mSendMsgTv.getText().equals("拒绝")) {
                        refuse();
                    } else {
                        if (mFaceModeStr.equals("4") || mFaceModeStr.equals("1")) {//好友状态未开启三分钟模式
                            sendMsg();
                        } else {
                            sendMsg("5");
                        }
                    }
                }
                break;
            case R.id.tv_agree://同意按钮
                if (!Utils.isDoubleClick()) {
                    agree();
                }
                break;
            case R.id.tv_msg:

                if (mFaceModeStr.equals("4") || mFaceModeStr.equals("1")) {//好友状态未开启三分钟模式
                    sendMsg();
                } else {
                    sendMsg("5");
                }
                break;
            default:
                break;

        }
    }


    private void friendDialog() {
        DialogHelper.callAlert(EditDataActivity.this, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

            }
        }, null);
    }

    private void bottomMenu(final AlbumEntity.Articles item) {

        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menuItem1 = new MenuItem();

        if (item.getCollectioned().equals("1")) {
            menuItem1.setText("取消收藏");
        } else {
            menuItem1.setText("收藏");
        }
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("举报");

        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                if (item.getCollectioned().equals("1")) {
                    cancelCollection(item);//取消收藏
                } else {
                    collection(item);//收藏
                }
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                onReport(item);
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void onReport(final AlbumEntity.Articles item) {//举报
        FriendDbHelper.reportArticle(uid, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        Intent intent = new Intent(EditDataActivity.this, ReportActivity.class);
                        intent.putExtra("uid", item.getUserId());
                        intent.putExtra("thisId", uid);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast(failueJson);
            }
        });
    }

    private void cancelCollection(AlbumEntity.Articles item) {

        CustomerDbHelper.cancelCollectionUserArticle(mThisId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        isZan = true;
                        loadData(uid, 1);
                        ToastUtils.showToast("取消收藏成功！");
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

    private void collection(AlbumEntity.Articles item) {
        CustomerDbHelper.collectionUserArticle(mThisId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        isZan = true;
                        loadData(uid, 1);
                        ToastUtils.showToast("收藏成功");
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

    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("举报");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("拉黑");
        menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setText("设置备注名");
        menuItem3.setStyle(MenuItem.MenuItemStyle.COMMON);


        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                Intent intent = new Intent(EditDataActivity.this, ReportActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("thisId", mThisId);
                startActivity(intent);
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                if (uid.equals(mThisId)) {
                    ToastUtils.showToast("自己不能拉黑自己");
                    return;
                } else {
                    dialogs();
                }
            }
        });

        menuItem3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                final PasswordDialog passwordDialog= new PasswordDialog(EditDataActivity.this).setTitle("备注名");
                passwordDialog.show();
                passwordDialog.getEt_phone().setInputType(InputType.TYPE_CLASS_TEXT);
                passwordDialog.getEt_phone().setHint("填写备注名");
                passwordDialog .setNoOnclickListener("取消", new PasswordDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        passwordDialog.dismiss();
                    }
                });
                passwordDialog.setYesOnclickListener("确定", new PasswordDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick(String str) {
                        ToastUtils.showToast(str);
                        passwordDialog.dismiss();
                    }
                });

            }
        });

        menuItemList.add(menuItem3);
        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void follow() {
        if (isfollow.equals("0")) {
            CustomerDbHelper.attention(mThisId, uid, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    dismissProgressDialog();
                    try {
                        JSONObject jsonobject = new JSONObject(successJson);
                        JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                        if (jsonobject1.getString("state").equals("0")) {
                            isfollow = "1";
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_check_follow);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            mFollowCheckBox.setCompoundDrawables(null, drawable, null, null);
                            mFollowCheckBox.setText("已关注");
                            mFollowCheckBox.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            ToastUtils.showToast(jsonobject1.getString("description"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {
                    dismissProgressDialog();
                }
            });
        } else {
            DialogHelper.agreeAlert(EditDataActivity.this, "提示", "确定不再关注此人？", "确认", "取消", new DialogHelper.OnAlertConfirmClick() {
                @Override
                public void OnClick(String content) {
                }

                @Override
                public void OnClick() {

                    CustomerDbHelper.cancelAttention(mThisId, uid, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String success) {
                            try {
                                JSONObject jsonobject = new JSONObject(success);
                                JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                                if (jsonobject1.getString("state").equals("0")) {
                                    isfollow = "0";
                                    Drawable drawable = getResources().getDrawable(R.drawable.ic_user_follow);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    mFollowCheckBox.setCompoundDrawables(null, drawable, null, null);
                                    mFollowCheckBox.setText("关注TA");
                                    mFollowCheckBox.setTextColor(getResources().getColor(R.color.green__66));
                                } else {
                                    ToastUtils.showToast(jsonobject1.getString("description"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {
                            dismissProgressDialog();
                        }
                    });
                }
            }, null);
        }
    }


    private void initStart() {
        WebCastDbHelper.getStart(mThisId, uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                if (MainListener.getInstance().mOnMessageRefreshListener != null) {//更新我要咨询谁
                    MainListener.getInstance().mOnMessageRefreshListener.refersh();
                }

                dismissProgressDialog();
                mDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        StartBean bean = parseData(successJson);
                        SharedPreferencesUtils.saveObject(EditDataActivity.this, "start", bean);
                        Intent intent = new Intent(EditDataActivity.this, ConversationListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("start", bean);
                        bundle.putString("status", mStatus);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (result.getString("state").equals("-1")) {
                        mDialog.dismiss();
                        mAlertView = new AlertView("提示", result.getString("description"), "取消", new String[]{"去充值"}, null, EditDataActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    startActivity(new Intent(EditDataActivity.this, RechargeActivity.class));
                                } else {
                                    mDialog.dismiss();
                                    mAlertView.dismiss();
                                }
                            }
                        }).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(Object o) {
                                mDialog.dismiss();
                                mAlertView.dismiss();
                            }
                        });
                        mAlertView.show();
                    } else if (result.getString("state").equals("-3")) {
                        mDialog.dismiss();
                        mAlertView = new AlertView("提示", result.getString("description"), "取消", new String[]{"去充值"}, null, EditDataActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    startActivity(new Intent(EditDataActivity.this, RechargeActivity.class));
                                } else {
                                    mDialog.dismiss();
                                    mAlertView.dismiss();
                                }
                            }
                        }).setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(Object o) {
                                mDialog.dismiss();
                                mAlertView.dismiss();
                            }
                        });
                        mAlertView.show();
                    } else {
                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }


    private void share() {//分享
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User/index?uid=" + uid + "&thisUid=" + mThisId, mNameStr, mIntroduceTv.getText().toString(), mLogo);
        dialog.show(fm, "fragment_bottom_dialog");
    }


    private void dialogs() {//提示框
        mAlertView = new AlertView("提示", "拉黑后将不会收到对方发来的见面,\n可在“设置-黑名单“中解除。\n是否确认？", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }

    private void refuse() {//拒绝好友

        HomeDbHelper.refundFriend(mThisId, uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("拒绝成功");
                        EventBus.getDefault().post(new NewMsgEvent());
                        EditDataActivity.this.finish();
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

    private void agree() {//同意好友

        RongIM.getInstance().removeFromBlacklist(uid, new RongIMClient.OperationCallback() {//检查是否存在黑名单
            @Override
            public void onSuccess() {
                HomeDbHelper.passFriend(mThisId, uid, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {

                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {

                                mAddUserLl.setVisibility(View.VISIBLE);//同意后显示好友信息
                                mMsgLl.setVisibility(View.GONE);
                                EventBus.getDefault().post(new NewMsgEvent());
                                initData(uid, mThisId);
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
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.showToast("同意失败，请稍后重试！");
            }
        });


    }

    private void friend2Dialog() {//移除好友
        DialogHelper.agreeAlert(EditDataActivity.this, "提示", "确定移除该好友？", "确认", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {

                CustomerDbHelper.removeFriend(mThisId, uid, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String success) {
                        try {
                            JSONObject jsonobject = new JSONObject(success);
                            JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                            if (jsonobject1.getString("state").equals("0")) {
                                //   addToBlacklist(uid);//加入黑名单
                                ToastUtils.showToast(jsonobject1.getString("description"));
                                EventBus.getDefault().post(new MailEvent());//更新通讯录数据
                                EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
                                EditDataActivity.this.finish();
                            } else {
                                ToastUtils.showToast(jsonobject1.getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        dismissProgressDialog();
                    }
                });
            }
        }, null);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {

    }

    public ArrayList<FriendsBookEntity> parseNmaeData(String result) {//Gson 解析用户头像
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private void sendMsg() {//发送消息
        HomeDbHelper.MykefuIds(mThisId, new IOAuthCallBack() {//设置客服顶置功能
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONArray array = new JSONArray(data.getString("uid"));
                    for (int i = 0; i < array.length(); i++) {
                        if (uid.equals(array.get(i).toString())) {
                            OperationRong.setConversationTop(EditDataActivity.this, Conversation.ConversationType.PRIVATE, array.get(i).toString(), true);
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
        RongIM.getInstance().startPrivateChat(EditDataActivity.this, uid, mNameStr);
    }


    private void sendMsg(String state) {//非好友
        RongIM.getInstance().startPrivateChat(EditDataActivity.this, uid + "=" + mIsTanchang + "=" + mMoney + "=" + mEnough + "=", mNameStr);
        // startActivity(new Intent(EditDataActivity.this, ConversationListActivity.class));
    }


    //添加评论星
    private void addGroupImage(float size) {
        mRatingBar.setmClickable(false);
        mRatingBar.setStarEmptyDrawable(getResources().getDrawable(R.drawable.icon_star_empty));
        //设置填充的星星
        mRatingBar.setStarFillDrawable(getResources().getDrawable(R.drawable.icon_new_comment));
        //设置半颗星
        mRatingBar.setStarHalfDrawable(getResources().getDrawable(R.drawable.icon_star_half));
        //设置显示的星星个数
        mRatingBar.setStar(size);
    }

    private StartBean parseData(String result) {//相册
        StartBean entity = new StartBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));

            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), StartBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEditMessageEvent(MessageEvent event) {
        if (null != getIntent().getStringExtra("UserId")) {
            uid = getIntent().getStringExtra("UserId");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
            initData(uid, mThisId);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinishEditDataActivity(MailEvent mailEvent) {
        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, uid);//移除会话列表
        EditDataActivity.this.finish();//删除好友关闭当前页
    }

    private void loadData(String mid, final int page) {
        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mid, uid, page, lng, lat, new IOAuthCallBack() {
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
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        if (parseAlbumData(successJson).size() > 0) {
                            mAlbumLL.setVisibility(View.VISIBLE);
                            mAlbumTv.setVisibility(View.VISIBLE);
                            mAlbumTv.setText(mNameStr + "的动态");
                            for (AlbumEntity.Articles item : parseAlbumData(successJson)) {
                                mDataList.add(item);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("数据请求失败");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(EditDataActivity.this);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(mThisId, page);
            }
        }, 500);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(mThisId, page);
            }
        }, 500);
    }

    @Override
    public void onZanClick(View view, final WholeAlbumAdapter.ViewHolder viewHolder) {
        final AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());
        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item, viewHolder);
        } else {
            String time = Utils.getTime();
            String secret = mThisId + "&" + item.getId() + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + item.getShowBound();
            String sign = MD5.MD5Hash(secret);
            HttpService.laudUserArticle(mThisId, item.getId(), time, sign, item.getShowBound(), new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {

                            viewHolder.mNumTv.setVisibility(View.VISIBLE);
                            viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                            Drawable drawable = EditDataActivity.this.getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(EditDataActivity.this.getResources().getColor(R.color.main_blue));

                            if (result.getString("description").equals("点赞成功")) {
                                ToastUtils.showToast(result.getString("description"));
                            } else {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                String bound = data.getString("bound");
                                String mAdvertising = data.getString("advertising");
                                String mAdvType = data.getString("adv_type");
                                String mAdvUrl = data.getString("adv_url");
                                int timeout = data.getInt("timeout");
                                FragmentManager fm = EditDataActivity.this.getSupportFragmentManager();
                                PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1", timeout);
                                dialog.show(fm, "dialog");
                            }
                        } else {
                            if (result.getString("description").equals("此红包已领完")) {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                String mAdvertising = data.getString("advertising");
                                String mAdvType = data.getString("adv_type");
                                String mAdvUrl = data.getString("adv_url");
                                FragmentManager fm = EditDataActivity.this.getSupportFragmentManager();
                                PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                                dialog.show(fm, "dialog");
                                Drawable drawable = EditDataActivity.this.getResources().getDrawable(R.drawable.icon_zan);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                                viewHolder.mZanTv.setTextColor(EditDataActivity.this.getResources().getColor(R.color.main_blue));
                            } else {
                                ToastUtils.showToast(result.getString("description"));
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
    }

    @Override
    public void onShareClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(EditDataActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mThisId);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (uid.equals(mThisId)) {
            Intent intent = new Intent(EditDataActivity.this, EditDataActivity.class);
            intent.putExtra("UserId", mThisId);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(EditDataActivity.this, EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (uid.equals(mThisId)) {
            bottomDelteMenu(item);
        } else {
            bottomMenu(item);
        }
    }

    @Override
    public void onImageItemClick(View view, int index) {

    }

    @Override
    public void onAvatarClick(View view) {

    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(EditDataActivity.this).externalPictureVideo(path);
    }

    @Override
    public void onRedClick(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_red_layout, null);

        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setFocusable(true)
                .setBgDarkAlpha(0.7f)
                .setOutsideTouchable(true)
                .create();
        mCustomPopWindow.showAsDropDown(view, -150, 0);
        handleLogic(contentView);
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
            }
        };
        contentView.findViewById(R.id.ll_red).setOnClickListener(listener);
    }


    public ArrayList<AlbumEntity.Articles> parseAlbumData(String result) {//相册
        ArrayList<AlbumEntity.Articles> entity = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("articles"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                AlbumEntity.Articles item = gson.fromJson(array.optJSONObject(i).toString(), AlbumEntity.Articles.class);
                entity.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }


    private void cancelGood(final AlbumEntity.Articles item, final WholeAlbumAdapter.ViewHolder viewHolder) {//取消点赞
        CustomerDbHelper.cancelLaudUserArticle(mThisId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = EditDataActivity.this.getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(EditDataActivity.this.getResources().getColor(R.color.green_54));
                    } else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                        int timeout = data.getInt("timeout");
                        FragmentManager fm = EditDataActivity.this.getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "-1", timeout);
                        dialog.show(fm, "dialog");
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

    private void zan(AlbumEntity.Articles item) {//点赞
        CustomerDbHelper.laudUserArticle(mThisId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        isZan = true;//清除数据重新加载
                        loadData(mThisId, 1);
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

    private void bottomDelteMenu(final AlbumEntity.Articles item) {//删除相册

        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("删除");
        menuItem1.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                FriendDbHelper.delArticle(mThisId, item.getId(), new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {
                                ToastUtils.showToast(result.getString("description"));
                                isZan = true;//清除数据重新加载
                                loadData(mThisId, 1);
                                initData(uid, mThisId);
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
        });
        menuItemList.add(menuItem1);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }


}
