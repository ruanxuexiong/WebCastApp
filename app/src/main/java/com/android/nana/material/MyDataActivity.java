package com.android.nana.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.customer.EditWorkActivity;
import com.android.nana.customer.EducationListActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.friend.AlbumDetailsActivity;
import com.android.nana.friend.AlbumEntity;
import com.android.nana.friend.VideoDynamicActivity;
import com.android.nana.friend.WholeAlbumAdapter;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.ratingbar.RatingBar;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.MD5;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.LabelsView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

/**
 * Created by lenovo on 2018/5/31.
 */

public class MyDataActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, WholeAlbumAdapter.WholeAlbumListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;
    private View mView;
    private String mid, mHead;
    private int page = 1;

    private String mFaceModeStr;//三分钟模式状态
    private RoundedImageView mAvatar;
    private TextView mNumTv;
    private ImageView mGenderIv;
    private TextView mMoneyTv;
    private ArrayList<String> label = new ArrayList<>();
    private LinearLayout mWorkLL, mWorkExperienceLL, mLabelExperienceLL, mEducationLL,mEducation, mTagesLL;
    private ImageView mBackgrounIv, mIdentyIv;
    private TextView mNameTv, mPositionTv, mAddressTv, mInfoTv, mMoreTv, mEducationMoerTv;
    private TextView mAlbumTv;
    private TransitionSingleHelper mTSHelper;
    private LinearLayout mAlbumDataLL, mAlbumLL;
    private ImageView mReleaseIv;
    private RelativeLayout mNavigationFriend;
    private String mNameStr, mLogo;
    private RatingBar mRatingBar;
    private RelativeLayout mNavigationShare, mNavigationFollow, mNavigationMore;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<AlbumEntity.Articles> mDataList = new ArrayList<>();

    private boolean isZan = false;//是否点击加载列表
    private WholeAlbumAdapter mAdapter;

    //高德
    public LatLng BEIJING;//经纬度
    private double lng, lat;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(MyDataActivity.this)) {
            EventBus.getDefault().register(MyDataActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
        if (NetWorkUtils.isNetworkConnected(MyDataActivity.this)) {
            mMultipleStatusView.loading();
            initData(mid);
        } else {
            mMultipleStatusView.noNetwork();
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
                if (lng != 0.0 && lat != 0.0 ) {
                    locationClient.stopLocation();
                    loadLngLatData(lng,lat);
                }else {
                    locationClient.stopLocation();
                    loadLngLatData(lng,lat);
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    private void loadLngLatData(double lng,double lat) {

        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mid, mid, page,lng,lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
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

                        if (parseData(successJson).size() > 0) {
                            mAlbumLL.setVisibility(View.VISIBLE);
                            mReleaseIv.setVisibility(View.GONE);
                            for (AlbumEntity.Articles item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        } else {
                            mAlbumLL.setVisibility(View.GONE);
                            mReleaseIv.setVisibility(View.VISIBLE);
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                    mMultipleStatusView.noEmpty();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
                mMultipleStatusView.noEmpty();
            }
        });
    }
    private void loadData(String mid, int page) {
        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mid, mid, page,lng,lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
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

                        if (parseData(successJson).size() > 0) {
                            mAlbumLL.setVisibility(View.VISIBLE);
                            mReleaseIv.setVisibility(View.GONE);
                            for (AlbumEntity.Articles item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        } else {
                            mAlbumLL.setVisibility(View.GONE);
                            mReleaseIv.setVisibility(View.VISIBLE);
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

    private void initData(final String mUid) {
        WebCastDbHelper.getUserInfo(mUid, mUid, "2", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                try {
                    JSONObject json = new JSONObject(successJson);
                    JSONObject result = new JSONObject(json.getString("result"));
                    if (result.getString("state").equals("0")) {
                        MeDataBean item = parseMeData(successJson);

                        mNameTv.setText(item.getUsername());
                        mNameStr = item.getUsername();
                        mBackgrounIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(item.getBackgroundImage(), mBackgrounIv);
                        if (!"".equals(item.getPosition()) && !"".equals(item.getCompany())) {
                            mPositionTv.setText(item.getPosition() + " | " + item.getCompany());
                        } else {
                            mPositionTv.setVisibility(View.GONE);
                        }

                        if ("1".equals(item.getStatus())) {
                            mIdentyIv.setVisibility(View.VISIBLE);
                        } else {
                            mIdentyIv.setVisibility(View.GONE);
                        }

                        if (!"".equals(item.getAvatar())) {
                            mLogo = item.getAvatar();

                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.icon_head_default)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true);

                            Glide.with(MyDataActivity.this)
                                    .asBitmap()
                                    .load(item.getAvatar())
                                    .apply(options)
                                    .into(mAvatar);
                        } else {
                            mLogo = "http://www.facethree.com/themes/simplebootx/Public/images/zblogo.png";
                        }

                        if ("".equals(item.getAvatar())) {
                            mHead = "http://www.facethree.com/data/upload/share/person.png";
                        } else {
                            mHead = item.getAvatar();
                        }
                        if (!"".equals(item.getIdcard())) {
                            mNumTv.setText("ID:"+item.getIdcard());
                        }

                        if ("1".equals(item.getSex())) {
                            mGenderIv.setImageDrawable(getDrawable(R.drawable.icon_sex_male));
                        } else if ("2".equals(item.getSex())) {
                            mGenderIv.setImageDrawable(getDrawable(R.drawable.icon_sex_female));
                        } else {
                            mGenderIv.setVisibility(View.GONE);
                        }


                        if (null != item.getProvince() && null != item.getDistrict() && null != item.getCity()) {
                            mAddressTv.setText("常居：" + item.getProvince() + "·" + item.getCity() + "·" + item.getDistrict());
                        } else {
                            mAddressTv.setVisibility(View.GONE);
                        }

                        if (!"".equals(item.getIntroduce())) {
                            mInfoTv.setText("简介：" + item.getIntroduce());
                        } else {
                            mInfoTv.setText("简介：暂无简介");
                        }

                     /*   if (!"".equals(item.getMoney_time())) {
                            mMoneyTv.setText("收费：" + item.getCharge() + "/分钟" + "（前3分钟免费）");
                        }*/


                     /*   mFaceModeStr = item.getFaceMode();//三分钟模式
                        switch (mFaceModeStr) {
                            case "1":
                                break;
                            case "2":
                                mMoneyTv.setVisibility(View.VISIBLE);
                                mMoneyTv.setText("见面金额：由对方提出合理的见面金额");
                                break;
                            case "3":
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            case "4":
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            case "5"://未开启三分钟模式+非好友
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            default:
                                break;
                        }*/

                        if (item.getWorkHistorys().size() > 0) {//工作
                            mWorkLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.setClickable(false);
                            mWorkExperienceLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.removeAllViews();
                            mMoreTv.setVisibility(View.VISIBLE);
                            for (int i = 0; i < item.getWorkHistorys().size(); i++) {
                                MeDataBean.WorkHistorys mItem = item.getWorkHistorys().get(i);
                                View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.work_experience_item, null);
                                TextView mTitle = mView.findViewById(R.id.tv_title);
                                TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                LinearLayout mImgLl = mView.findViewById(R.id.ll_img);
                                mImgLl.setVisibility(View.GONE);

                                new com.android.nana.util.ImgLoaderManager(R.drawable.icon_work).showImageView(mItem.getPicture(), mPicture);
                                mTitle.setText(mItem.getName());
                                mCompanyTv.setText(mItem.getPosition());
                                if ("".equals(mItem.getBeginTime())) {
                                    mTimeTv.setTextColor(getResources().getColor(R.color.gules));
                                    mTimeTv.setText("起止日期和详细描述待完善");
                                } else {
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());
                                }
                                mWorkExperienceLL.addView(mView);

                                mMoreTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MyDataActivity.this, EditWorkActivity.class);
                                       /* intent.putExtra("IsHideEdit", true);
                                        intent.putExtra("userId", mUid);*/
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            mWorkLL.setVisibility(View.GONE);
                            mMoreTv.setVisibility(View.GONE);
                            mWorkExperienceLL.setClickable(true);
                            mWorkExperienceLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.removeAllViews();
                            View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.item_work_experience, null);
                            TextView mAddTv = mView.findViewById(R.id.tv_add);
                            mAddTv.setVisibility(View.GONE);
                            TextView mAdd1Tv = mView.findViewById(R.id.tv_add1);
                            mAdd1Tv.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.addView(mView);
                        }

                        //教育经历
                        if (item.getEducationExperiences().size() > 0) {
                            mEducationLL.setClickable(false);
                            mEducationLL.removeAllViews();
                            if (item.getEducationExperiences().size() > 2) {
                                mEducationMoerTv.setVisibility(View.VISIBLE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {
                                    MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                    mView.findViewById(R.id.ll_img).setVisibility(View.GONE);

                                    new com.android.nana.util.ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);

                                    mEducationMoerTv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                         /*   Intent intent = new Intent(MyDataActivity.this, EducationActivity.class);
                                            intent.putExtra("IsHideEdit", true);
                                            intent.putExtra("userId", mUid);
                                            startActivity(intent);*/
                                            Intent intent = new Intent(MyDataActivity.this, EducationListActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mEducationMoerTv.setVisibility(View.VISIBLE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {
                                    MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                    mView.findViewById(R.id.ll_img).setVisibility(View.GONE);
                                    new com.android.nana.util.ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);
                                }
                                mEducationMoerTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                         /*   Intent intent = new Intent(MyDataActivity.this, EducationActivity.class);
                                            intent.putExtra("IsHideEdit", true);
                                            intent.putExtra("userId", mUid);
                                            startActivity(intent);*/
                                        Intent intent = new Intent(MyDataActivity.this, EducationListActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {

                            mEducation.setVisibility(View.GONE);
                            mEducationMoerTv.setVisibility(View.GONE);

                            mEducationLL.setVisibility(View.VISIBLE);
                            mEducationLL.removeAllViews();
                            View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.item_work_experience, null);
                            TextView mAddTv = mView.findViewById(R.id.tv_add);
                            mAddTv.setVisibility(View.GONE);
                            TextView mAdd1Tv = mView.findViewById(R.id.tv_add1);
                            TextView mAddStartTv = mView.findViewById(R.id.tv_add_start);
                            mAdd1Tv.setVisibility(View.VISIBLE);
                            mAdd1Tv.setText("添加教育经历信息");
                            mAddStartTv.setText("更好的展示你的专业知识");
                            mEducationLL.addView(mView);
                        }

                        //标签

                        if (null != item.getProperty() && item.getProperty().size() > 0) {
                            label.clear();
                            mTagesLL.setVisibility(View.VISIBLE);
                            mLabelExperienceLL.setVisibility(View.VISIBLE);
                            mLabelExperienceLL.removeAllViews();
                            mLabelExperienceLL.setClickable(false);

                            View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.item_tages, null);
                            LabelsView mLabelsView = mView.findViewById(R.id.lv_name);
                            mLabelsView.setSelectType(LabelsView.SelectType.NONE);//标签不可选

                            for (int i = 0; i < item.getProperty().size(); i++) {
                                MeDataBean.Property mItem = item.getProperty().get(i);
                                if (null != mItem.getParentName()) {
                                    label.add(mItem.getParentName() + "-" + mItem.getName());
                                } else {
                                    mTagesLL.setVisibility(View.GONE);
                                    mLabelExperienceLL.setVisibility(View.GONE);
                                }
                            }
                            mLabelsView.setLabels(label);
                            mLabelExperienceLL.addView(mView);
                        }else {
                            mTagesLL.setVisibility(View.GONE);
                        }


                        if (item.getPictures().size() > 0) {
                            mAlbumTv.setText("我的动态");

                            mAlbumDataLL.removeAllViews();
                            for (int i = 0; i < item.getPictures().size(); i++) {
                                View mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.item_images, null);
                                ImageView image = mView.findViewById(R.id.iv_img);
                                com.android.nana.util.ImgLoaderManager.getInstance().showImageView(item.getPictures().get(i).toString(), image);
                                mAlbumDataLL.addView(mView);
                            }
                        }

                        addGroupImage(Float.valueOf(item.getStars()));
                    }
                } catch (JSONException e) {
                    mMultipleStatusView.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_my_deta);
    }

    @Override
    protected void findViewById() {
        mView = LayoutInflater.from(MyDataActivity.this).inflate(R.layout.activity_me_deta, null);


        mInfoTv = mView.findViewById(R.id.tv_introduce);
        mTagesLL = mView.findViewById(R.id.ll_tags);
        mNameTv = mView.findViewById(R.id.tv_name);
        mAlbumTv = mView.findViewById(R.id.tv_album);
        mAddressTv = mView.findViewById(R.id.tv_address);
        mPositionTv = mView.findViewById(R.id.tv_position);
        mBackgrounIv = mView.findViewById(R.id.customer_detail_iv_top);
        mAvatar = mView.findViewById(R.id.iv_head);

        mAlbumLL = mView.findViewById(R.id.ll_album);
        mAlbumDataLL = mView.findViewById(R.id.ll_album_data);
        mWorkLL = mView.findViewById(R.id.ll_work);
        mWorkExperienceLL = mView.findViewById(R.id.anchor_detail_ll_work_experience);
        mReleaseIv = mView.findViewById(R.id.iv_release);
        mMoreTv = mView.findViewById(R.id.tv_more);
        mEducationMoerTv = mView.findViewById(R.id.tv_education_more);
        mLabelExperienceLL = mView.findViewById(R.id.anchor_detail_ll_label_experience);
        mEducation = mView.findViewById(R.id.ll_education);
        mEducationLL = mView.findViewById(R.id.anchor_detail_ll_education_experience);
        mIdentyIv = mView.findViewById(R.id.iv_identy);
        mNumTv = mView.findViewById(R.id.tv_num);
        mMoneyTv = mView.findViewById(R.id.tv_money);
        mGenderIv = mView.findViewById(R.id.iv_sex);
        mRatingBar = mView.findViewById(R.id.ratingBar);
        mNavigationShare = mView.findViewById(R.id.navigation_share);
        mNavigationFriend = mView.findViewById(R.id.navigation_friend);
        mNavigationFollow = mView.findViewById(R.id.navigation_follow);
        mNavigationMore = mView.findViewById(R.id.navigation_more);

        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);
        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);


    }

    @Override
    protected void init() {
        mTitleTv.setText("我的资料");
        mBackTv.setVisibility(View.VISIBLE);
        mAction2.setVisibility(View.VISIBLE);
        if (null != getIntent().getExtras().getString("thisUid")) {
            mid = getIntent().getExtras().getString("thisUid");
        }

        mTSHelper = new TransitionManager(MyDataActivity.this).getSingle();
        Drawable drawable = getResources().getDrawable(R.drawable.icon_me_edit);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction2Click();
            }
        });

        mListView.addHeaderView(mView);
        mAdapter = new WholeAlbumAdapter(MyDataActivity.this, mDataList, this, mid);
        mListView.setAdapter(mAdapter);
    }

    private void onAction2Click() {
        Intent intent = new Intent(this, EditPersonalActivity.class);
        intent.putExtra("thisUid", mid);
        startActivity(intent);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAlbumLL.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        mReleaseIv.setOnClickListener(this);
        mNavigationFriend.setOnClickListener(this);
        mNavigationShare.setOnClickListener(this);
        mNavigationFollow.setOnClickListener(this);
        mNavigationMore.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    public MeDataBean parseMeData(String result) {

        MeDataBean entity = new MeDataBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), MeDataBean.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public ArrayList<AlbumEntity.Articles> parseData(String result) {//相册
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_album:
                startActivity(new Intent(MyDataActivity.this, VideoDynamicActivity.class));
                break;
            case R.id.iv_head:
                mTSHelper.startViewerActivity(mAvatar, mHead);
                break;
            case R.id.navigation_friend:
                ToastUtils.showToast("自己不能添加自己为好友！");
                break;
            case R.id.navigation_follow:
                ToastUtils.showToast("自己不能关注自己！");
                break;
            case R.id.navigation_share:
                share();
                break;
            case R.id.navigation_more:
                bottomMenu();
                break;
            case R.id.iv_release:
                startActivity(new Intent(MyDataActivity.this, VideoDynamicActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(mid, page);
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
                loadData(mid, page);
            }
        }, 500);
    }

    @Override
    public void onZanClick(View view,WholeAlbumAdapter.ViewHolder viewHolder) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item,viewHolder);
        } else {
            article(mid,item,viewHolder);
        }
    }

    public void article(String mUserId, final AlbumEntity.Articles item, final WholeAlbumAdapter.ViewHolder viewHolder) {
        String time = Utils.getTime();
        String secret = mUserId + "&" + item.getId() + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + item.getShowBound();
        String sign = MD5.MD5Hash(secret);

        HttpService.laudUserArticle(mUserId, item.getId(), time, sign, item.getShowBound(), new IOAuthCallBack() {
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

                        Drawable drawable = MyDataActivity.this.getResources().getDrawable(R.drawable.icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor( MyDataActivity.this.getResources().getColor(R.color.main_blue));


                        if (result.getString("description").equals("点赞成功")) {
                            ToastUtils.showToast(result.getString("description"));
                        } else {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String bound = data.getString("bound");
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            int timeout=data.getInt("timeout");
                            FragmentManager fm = MyDataActivity.this.getSupportFragmentManager();
                            PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",timeout);
                            dialog.show(fm, "dialog");
                        }
                    }
                    else if (result.getString("state").equals("-2")){
                        showNormalDialog();
                    }
                    else {
                        if (result.getString("description").equals("此红包已领完")) {

                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            FragmentManager fm = MyDataActivity.this.getSupportFragmentManager();
                            PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                            dialog.show(fm, "dialog");

                            Drawable drawable = MyDataActivity.this.getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(MyDataActivity.this.getResources().getColor(R.color.main_blue));
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
        Intent intent = new Intent(MyDataActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("type","1");
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        Intent intent = new Intent(MyDataActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mid);
        startActivity(intent);
    }

    @Override
    public void onReportClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());
        bottomDelteMenu(item);
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
        PictureSelector.create(MyDataActivity.this).externalPictureVideo(path);
    }

    @Override
    public void onRedClick(View view) {

    }

    private void share() {//分享
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User/index?uid=" + mid + "&thisUid=" + mid, mNameStr, mInfoTv.getText().toString(), mLogo);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    //添加评论星
    private void addGroupImage(Float size) {
        mRatingBar.setmClickable(false);
        mRatingBar.setStarEmptyDrawable(getResources().getDrawable(R.drawable.icon_star_empty));
        //设置填充的星星
        mRatingBar.setStarFillDrawable(getResources().getDrawable(R.drawable.icon_new_comment));
        //设置半颗星
        mRatingBar.setStarHalfDrawable(getResources().getDrawable(R.drawable.icon_star_half));
        //设置显示的星星个数
        mRatingBar.setStar(size);
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

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                Intent intent = new Intent(MyDataActivity.this, ReportActivity.class);
                intent.putExtra("uid", mid);
                intent.putExtra("thisId", mid);
                startActivity(intent);
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                ToastUtils.showToast("自己不能拉黑自己");
                return;

            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(MyDataActivity.this);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(FriendEvent friendEvent) {
        isZan = true;
        loadData(mid, 1);
        initData(mid);
    }

    private void cancelGood(final AlbumEntity.Articles item, final WholeAlbumAdapter.ViewHolder viewHolder) {//取消点赞
        CustomerDbHelper.cancelLaudUserArticle(mid, item.getId(), new IOAuthCallBack() {
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

                        Drawable drawable = MyDataActivity.this.getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor( MyDataActivity.this.getResources().getColor(R.color.green_54));

                    } else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                        int timeout=data.getInt("timeout");
                        FragmentManager fm = MyDataActivity.this.getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound,mAdvertising,mAdvType,mAdvUrl,"-1",timeout);
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
        CustomerDbHelper.laudUserArticle(mid, item.getId(), new IOAuthCallBack() {
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
                        loadData(mid, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                Log.e("====", failueJson);
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

                FriendDbHelper.delArticle(mid, item.getId(), new IOAuthCallBack() {
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
                                loadData(mid, 1);
                                initData(mid);
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
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyDataActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请先完善您的头像和昵称资料？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        //    getHuafei();
                        Intent intent=new Intent(MyDataActivity.this,EditPersonalActivity.class);
                        intent.putExtra("thisUid",mid);
                        startActivity(intent);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

}
