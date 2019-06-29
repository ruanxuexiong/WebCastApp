package com.android.nana.find;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.common.BaseApplication;
import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.CloseFragmentEvent;
import com.android.nana.eventBus.FollowEvent;
import com.android.nana.find.adapter.PagerAdapter;
import com.android.nana.find.base.CloseEvent;
import com.android.nana.find.base.MineEvent;
import com.android.nana.friend.FriendEntity;
import com.android.nana.friend.FriendSearchActivity;
import com.android.nana.friend.NewMsgActivity;
import com.android.nana.friend.VideoDynamicActivity;
import com.android.nana.material.MyDataActivity;
import com.android.nana.partner.PartnerFragment;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by lenovo on 2018/9/26.
 */

public class FindFragment extends BaseRequestFragment implements View.OnClickListener {
    private FrameLayout main_frame;
    private ViewPager mViewPager;
    private View mBackIv;
    private SlidingTabLayout mTabLayout;
    private PagerAdapter mAdapter;
    //是否隐藏了头部
    private boolean isHideHeaderLayout = false;
    //AppBarLayout
    private AppBarLayout mAppBarLayout;
    //顶部HeaderLayout
    private LinearLayout headerLayout;
    private CustomPopWindow mCustomPopWindow;
    private AppCompatTextView mCameraTv, mAddTv;
    private RoundImageView mAvatarIv;
    private ImageView mSureIv;
    private RelativeLayout mHeadLl;
    private ImageView mSearchIv;
    private LinearLayout mMsgLl;
    private ImageView mMsgAvatarIv;
    private TextView mMsgTv;
    private String mMsgNum;
    //分享内容
    private String mShareUrl, mMessage, mShareTitle, mSareDesc, mShareLogo;
    private String mUserId;
    private RoundImageView mHeadIv;

    public LatLng BEIJING;//经纬度
    private double lng, lat;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(FindFragment.this)) {
            EventBus.getDefault().register(FindFragment.this);
        }
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(getActivity());
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

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                locationClient.stopLocation();
                lng = location.getLongitude();//经    度
                lat = location.getLatitude();//纬    度
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_find;
    }

    @Override
    protected void findViewById() {
        main_frame=getActivity().findViewById(R.id.main_frame);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mBackIv = findViewById(R.id.iv_back);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.layout_tab);
        headerLayout = (LinearLayout) findViewById(R.id.ll_header_layout);
        mCameraTv = (AppCompatTextView) findViewById(R.id.iv_camera);
        mAddTv = (AppCompatTextView) findViewById(R.id.tv_add);
        mAvatarIv = (RoundImageView) findViewById(R.id.iv_head);
        mSureIv = (ImageView) findViewById(R.id.iv_id);
        mHeadLl = (RelativeLayout) findViewById(R.id.ll_head);
        mSearchIv = (ImageView) findViewById(R.id.iv_search);

        mMsgLl = (LinearLayout) findViewById(R.id.ll_msg);
        mMsgAvatarIv = (ImageView) findViewById(R.id.iv_avatar);
        mMsgTv = (TextView) findViewById(R.id.tv_msg);
        mHeadIv = (RoundImageView) findViewById(R.id.iv_head);
        initAppBarLayout();
    }

    @Override
    public void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    private void initAppBarLayout() {
        LayoutTransition mTransition = new LayoutTransition();
        ObjectAnimator addAnimator = ObjectAnimator.ofFloat(null, "translationY", 0, 1.f).
                setDuration(mTransition.getDuration(LayoutTransition.APPEARING));
        mTransition.setAnimator(LayoutTransition.APPEARING, addAnimator);
        final int headerHeight = getResources().getDimensionPixelOffset(R.dimen.header_offset);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mAppBarLayout.setLayoutTransition(mTransition);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = Math.abs(verticalOffset);
                if (verticalOffset >= headerHeight) {
                    isHideHeaderLayout = true;

                    //当偏移量超过顶部layout的高度时，我们认为他已经完全移动出屏幕了
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                            mParams.setScrollFlags(0);
                            mBackIv.setVisibility(View.VISIBLE);
                            headerLayout.setLayoutParams(mParams);
                            headerLayout.setVisibility(View.GONE);
                            mBackIv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    headerLayout.setVisibility(View.VISIBLE);
                                    mBackIv.setVisibility(View.GONE);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                                            mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                                            headerLayout.setLayoutParams(mParams);
                                        }
                                    }, 110);
                                }
                            });
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    protected void init() {
        setAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserId = BaseApplication.getInstance().getCustomerId(getActivity());
        //初始化定位
        initLocation();
        loadDataMsg(mUserId, lng, lat);
        loadShare(mUserId);
    }

    private void loadDataMsg(String mUserId, double lng, double lat) {
        FriendDbHelper.moments(mUserId, 1, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        FriendEntity.Message message = parseMsgData(successJson);
                        if (null != message && Integer.valueOf(message.getCount()) > 0) {
                            mMsgLl.setVisibility(View.VISIBLE);
                            mMsgNum = message.getCount();
                            mMsgTv.setText(mMsgNum + "新消息");
                            ImgLoaderManager.getInstance().showImageView(message.getAvatar(), mMsgAvatarIv);
                        } else {
                            mMsgLl.setVisibility(View.GONE);
                        }
                    } else {
                        mMsgLl.setVisibility(View.GONE);
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

    private void setAdapter() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mViewPager.setOffscreenPageLimit(3);
        mAdapter = new PagerAdapter(fragmentManager, getActivity());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

        mTabLayout.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    main_frame.setVisibility(View.VISIBLE);
                }
                else if (position==1){
                    main_frame.setVisibility(View.VISIBLE);
                }
                else if (position==2){
                    main_frame.setVisibility(View.GONE);
                    headerLayout.setVisibility(View.GONE);
                    NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    protected void setListener() {
        mBackIv.setOnClickListener(this);
        mHeadLl.setOnClickListener(this);
        mCameraTv.setOnClickListener(this);
        mAddTv.setOnClickListener(this);
        mSearchIv.setOnClickListener(this);
        mHeadIv.setOnClickListener(this);
        mMsgLl.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_camera:
                intent.setClass(getContext(), VideoDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_head:
                intent.setClass(getContext(), VideoDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_add:
                onAction2Click();
                break;
            case R.id.iv_search:
                intent.setClass(getContext(), FriendSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_head:
                Intent intent1 = new Intent(getActivity(), MyDataActivity.class);
                intent1.putExtra("thisUid", mUserId);
                startActivity(intent1);
                break;
            case R.id.ll_msg:
                Intent intent2 = new Intent(getActivity(), NewMsgActivity.class);
                intent2.putExtra("mid", mUserId);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void back() {
        if (isHideHeaderLayout) {
            isHideHeaderLayout = false;
            headerLayout.setVisibility(View.VISIBLE);
            mBackIv.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                    mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                    headerLayout.setLayoutParams(mParams);
                }
            }, 300);
        }
    }

    private void onAction2Click() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_share_layout, null);

        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .setFocusable(true)
                .setBgDarkAlpha(0.7f)
                .enableBackgroundDark(true)
                .setOutsideTouchable(true)
                .create();
        mCustomPopWindow.showAsDropDown(mAddTv, 0, 10);
        handleLogic(contentView);
    }


    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                switch (v.getId()) {
                    case R.id.tv_share:
                        share();
                        break;
                }
            }
        };
        contentView.findViewById(R.id.tv_share).setOnClickListener(listener);
    }

    private void loadShare(String mUserId) {
        HomeDbHelper.shenFriend(mUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mShareUrl = data.getString("shareUrl");
                        mMessage = data.getString("message");
                        mShareTitle = data.getString("shareTitle");
                        mSareDesc = data.getString("shareDesc");
                        mShareLogo = data.getString("shareLogo");
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

    private void share() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PartnerFragment dialog = PartnerFragment.newInstance(mUserId, mShareTitle, mShareLogo, mSareDesc, mMessage, mShareUrl);
        dialog.show(fm, "fragment_bottom_dialog");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(FindFragment.this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(MineEvent event) {

        if ("1".equals(event.mine.getStatus())) {
            mSureIv.setVisibility(View.VISIBLE);
        } else {
            mSureIv.setVisibility(View.GONE);
        }
        ImgLoaderManager.getInstance().showImageView(event.mine.getAvatar(), mAvatarIv);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClose(CloseEvent event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                mParams.setScrollFlags(0);
                mBackIv.setVisibility(View.VISIBLE);
                headerLayout.setLayoutParams(mParams);
                headerLayout.setVisibility(View.GONE);
                mBackIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        headerLayout.setVisibility(View.VISIBLE);
                        mBackIv.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                                mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                                headerLayout.setLayoutParams(mParams);
                            }
                        }, 110);
                    }
                });
            }
        }, 100);
    }

    public FriendEntity.Message parseMsgData(String result) {//msg 解析
        FriendEntity.Message entity = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.getString("message"), FriendEntity.Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFollowMsgDate(FollowEvent friendsEvent) {//更新朋友圈消息
        if (Integer.valueOf(friendsEvent.count) > 0) {
            mTabLayout.showDot(1);
        } else if (friendsEvent.count.equals("0")) {
            mTabLayout.hideMsg(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCloseFragment(CloseFragmentEvent event) {
        headerLayout.setVisibility(View.VISIBLE);
        mBackIv.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) headerLayout.getLayoutParams();
                mParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                headerLayout.setLayoutParams(mParams);
            }
        }, 110);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }
}
