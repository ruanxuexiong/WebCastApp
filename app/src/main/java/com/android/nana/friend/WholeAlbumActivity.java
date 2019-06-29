package com.android.nana.friend;

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
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.ShareFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.makeramen.roundedimageview.RoundedImageView;
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

/**
 * Created by lenovo on 2017/11/16.
 */

public class WholeAlbumActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, WholeAlbumAdapter.WholeAlbumListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;
    private String uid;
    private int page = 1;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<AlbumEntity.Articles> mDataList = new ArrayList<>();

    private boolean forLoad = true;//第一次加载数据
    private boolean isZan = false;//是否点击加载列表
    private String mid;
    private WholeAlbumAdapter mAdapter;
    private AlbumEntity.Mine mine;

    private ImageView mBackgIv;
    private RoundedImageView mAvatarIv;
    private TextView mNameTv;
    private TextView mDocTv;
    private View mView;
    private LinearLayout mDynamicll;

    //点赞信息
    private LinearLayout mMsgLl;
    private TextView mMsgTv;
    private CircleImageView mMsgAvatarIv;

    //高德

    public LatLng BEIJING;//经纬度
    private double lng, lat;
    private AMapLocationClient locationClient = null;
    private CustomPopWindow mCustomPopWindow;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(WholeAlbumActivity.this)) {
            EventBus.getDefault().register(WholeAlbumActivity.this);
        }

        if (NetWorkUtils.isNetworkConnected(WholeAlbumActivity.this)) {
            mMultipleStatusView.loading();
          //  loadData(mid, page);
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
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
                    loadLngLatData(0.0,0.0);
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };
    private void loadLngLatData(double lng,double lat) {

        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            forLoad = true;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mid, uid, page,lng,lat, new IOAuthCallBack() {
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
                            for (AlbumEntity.Articles item : parseData(successJson)) {
                                if (!mid.equals(uid)) {
                                    mTitleTv.setText(item.getUser().getUname() + "的动态");
                                }
                                mDataList.add(item);
                            }
                        }
                        AlbumEntity.Message message = parseMsgData(successJson);
                        if (null != message && Integer.valueOf(message.getCount()) > 0) {
                            mMsgLl.setVisibility(View.VISIBLE);
                            mMsgTv.setText(message.getCount() + "新消息");
                            ImgLoaderManager.getInstance().showImageView(message.getAvatar(), mMsgAvatarIv);
                        } else {
                            mMsgLl.setVisibility(View.GONE);
                        }


                        if (forLoad) {
                            forLoad = false;
                            mine = parseMineData(successJson);
                            if (null != mine) {
                                if (!"".equals(mine.getBackgroundImage())) {
                                    ImgLoaderManager.getInstance().showImageView(mine.getBackgroundImage(), mBackgIv);
                                }
                                if (!"".equals(mine.getAvatar())) {
                                    ImgLoaderManager.getInstance().showImageView(mine.getAvatar(), mAvatarIv);
                                }
                                if (!"null".equals(mine.getSign())) {
                                    mDocTv.setText(mine.getSign());
                                }
                                mNameTv.setText(mine.getUname());
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
                mMultipleStatusView.dismiss();
                mMultipleStatusView.noEmpty();
            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_whole_album);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);

        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {

        mid = (String) SharedPreferencesUtils.getParameter(WholeAlbumActivity.this, "userId", "");
        uid = getIntent().getStringExtra("uid");

        if (mid.equals(uid)) {
            mAction2.setVisibility(View.VISIBLE);
        }

        mBackTv.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_more_unselect);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction2Click();
            }
        });

        View view = LayoutInflater.from(WholeAlbumActivity.this).inflate(R.layout.activity_album_head, null);
        mBackgIv = view.findViewById(R.id.iv_bg);
        mAvatarIv = view.findViewById(R.id.iv_avatar);
        mDocTv = view.findViewById(R.id.tv_doc);
        mNameTv = view.findViewById(R.id.tv_name);
        mMsgLl = view.findViewById(R.id.ll_msg);
        mMsgAvatarIv = view.findViewById(R.id.iv_msg_avatar);
        mMsgTv = view.findViewById(R.id.tv_msg);
        mView = view.findViewById(R.id.view);
        mDynamicll = view.findViewById(R.id.ll_dynamic);
        setOnClickListener(mDynamicll, mAvatarIv, mMsgLl);
        mListView.addHeaderView(view);

        if (mid.equals(uid)) {
            mTitleTv.setText("我的相册");
            mDynamicll.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
        }
        mAdapter = new WholeAlbumAdapter(WholeAlbumActivity.this, mDataList, this, mid);
        mListView.setAdapter(mAdapter);
    }

    private void setOnClickListener(LinearLayout mDynamicll, RoundedImageView mAvatarIv, LinearLayout mMsgLl) {

        mMsgLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WholeAlbumActivity.this, NewMsgActivity.class);
                intent.putExtra("mid", mid);
                startActivity(intent);
            }
        });
        mDynamicll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WholeAlbumActivity.this, VideoDynamicActivity.class));
            }
        });
        mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mid.equals(mine.getId())) {
                    Intent intent = new Intent(WholeAlbumActivity.this, EditDataActivity.class);
                    intent.putExtra("UserId", mid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WholeAlbumActivity.this, EditDataActivity.class);
                    intent.putExtra("UserId", mine.getId());
                    startActivity(intent);
                }
            }
        });
    }

    private void onAction2Click() {
        Intent intent = new Intent(WholeAlbumActivity.this, MsgListActivity.class);
        intent.putExtra("mid", uid);
        startActivity(intent);
    }

    private void loadData(final String mid, final int page) {
        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            forLoad = true;
            mDataList.clear();
        }

        FriendDbHelper.myPictures(mid, uid, page,lng,lat, new IOAuthCallBack() {
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
                            for (AlbumEntity.Articles item : parseData(successJson)) {
                                if (!mid.equals(uid)) {
                                    mTitleTv.setText(item.getUser().getUname() + "的动态");
                                }
                                mDataList.add(item);
                            }
                        }
                        AlbumEntity.Message message = parseMsgData(successJson);
                        if (null != message && Integer.valueOf(message.getCount()) > 0) {
                            mMsgLl.setVisibility(View.VISIBLE);
                            mMsgTv.setText(message.getCount() + "新消息");
                            ImgLoaderManager.getInstance().showImageView(message.getAvatar(), mMsgAvatarIv);
                        } else {
                            mMsgLl.setVisibility(View.GONE);
                        }


                        if (forLoad) {
                            forLoad = false;
                            mine = parseMineData(successJson);
                            if (null != mine) {
                                if (!"".equals(mine.getBackgroundImage())) {
                                    ImgLoaderManager.getInstance().showImageView(mine.getBackgroundImage(), mBackgIv);
                                }
                                if (!"".equals(mine.getAvatar())) {
                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.drawable.icon_head_default)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true);

                                    Glide.with(WholeAlbumActivity.this)
                                            .asBitmap()
                                            .load(mine.getAvatar())
                                            .apply(options)
                                            .into(mAvatarIv);
                                }
                                if (!"null".equals(mine.getSign())) {
                                    mDocTv.setText(mine.getSign());
                                }
                                mNameTv.setText(mine.getUname());
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
                mMultipleStatusView.dismiss();
                mMultipleStatusView.noEmpty();
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }


    public AlbumEntity.Mine parseMineData(String result) {
        AlbumEntity.Mine entity = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.getString("mine"), AlbumEntity.Mine.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    public AlbumEntity.Message parseMsgData(String result) {//点赞信息
        AlbumEntity.Message entity = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.getString("message"), AlbumEntity.Message.class);
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
    public void onZanClick(View view ,WholeAlbumAdapter.ViewHolder viewHolder) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item,viewHolder);
        } else {
            zan(item,viewHolder);
        }
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

                        Drawable drawable = WholeAlbumActivity.this.getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor( WholeAlbumActivity.this.getResources().getColor(R.color.green_54));
                    } else {
                        ToastUtils.showToast(result.getString("description"));
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

    private void zan(final AlbumEntity.Articles item, final WholeAlbumAdapter.ViewHolder viewHolder) {//点赞
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
                     //   isZan = true;//清除数据重新加载
                       // loadData(mid, 1);
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = WholeAlbumActivity.this.getResources().getDrawable(R.drawable.icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor( WholeAlbumActivity.this.getResources().getColor(R.color.main_blue));

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
        Intent intent = new Intent(WholeAlbumActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (uid.equals(mid)) {
            Intent intent = new Intent(WholeAlbumActivity.this, EditDataActivity.class);
            intent.putExtra("UserId", mid);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(WholeAlbumActivity.this, EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {
        AlbumEntity.Articles item = mDataList.get((Integer) view.getTag());

        if (uid.equals(mid)) {
            bottomDelteMenu(item);
        } else {
            bottomMenu(item);
        }
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

                FriendDbHelper.delArticle(uid, item.getId(), new IOAuthCallBack() {
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
                                loadData(uid, 1);
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

    @Override
    public void onImageItemClick(View view, int index) {

    }

    @Override
    public void onAvatarClick(View view) {

    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(WholeAlbumActivity.this).externalPictureVideo(path);
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
        mCustomPopWindow.showAsDropDown(view, -120, 0);
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


    private void cancelCollection(AlbumEntity.Articles item) {

        CustomerDbHelper.cancelCollectionUserArticle(uid, item.getId(), new IOAuthCallBack() {
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

    private void collection(AlbumEntity.Articles item) {//点赞
        CustomerDbHelper.collectionUserArticle(uid, item.getId(), new IOAuthCallBack() {
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
                        Intent intent = new Intent(WholeAlbumActivity.this, ReportActivity.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(WholeAlbumActivity.this);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(FriendEvent friendEvent) {
        isZan = true;
        loadData(mid, 1);
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
}
