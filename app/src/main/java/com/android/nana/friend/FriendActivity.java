package com.android.nana.friend;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
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
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.eventBus.FriendsEvent;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.PushExtraEvent;
import com.android.nana.eventBus.ShareMsgEvent;
import com.android.nana.find.LocationActivity;
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
import com.android.nana.widget.ImageBrowserActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
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
 * Created by lenovo on 2017/10/23.
 */

public class FriendActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, FriendAdapter.FriendListener {


    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;

    private String mid;
    private int page = 1;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;

    private FriendEntity.Message message;
    private FriendEntity.Mine mine;
    private ArrayList<FriendEntity.Moments> moment = new ArrayList<>();
    private FriendAdapter mAdapter;
    private boolean forLoad = true;//第一次加载数据
    private boolean isZan = false;//是否点击加载列表
    private String mArticleId;

    private ImageView mBackgIv;
    private CircleImageView mAvatarIv;
    private TextView mNameTv;
    private LinearLayout mMsgLl;
    private CircleImageView mMsgAvatarIv;
    private TextView mMsgTv;
    private LinearLayout mSearchll;

    public LatLng BEIJING;//经纬度
    private double lng, lat;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(FriendActivity.this)) {
            EventBus.getDefault().register(FriendActivity.this);
        }
        mid = (String) SharedPreferencesUtils.getParameter(FriendActivity.this, "userId", "");
        //初始化定位


        if (NetWorkUtils.isNetworkConnected(FriendActivity.this)) {
            mMultipleStatusView.loading();
            initLocation();

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

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                lng = location.getLongitude();//经    度
                lat = location.getLatitude();//纬    度
                loadData(mid, page,lng,lat);
                BEIJING = new LatLng(lat, lng);
            }
        }
    };



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


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_friend);
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
        mTitleTv.setText("智慧圈");
        mBackTv.setVisibility(View.VISIBLE);

        mAction2.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_new_camera);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(this);

        View view = LayoutInflater.from(FriendActivity.this).inflate(R.layout.activity_friend_head, null);
        mBackgIv = view.findViewById(R.id.iv_bg);
        mAvatarIv = view.findViewById(R.id.iv_avatar);
        mNameTv = view.findViewById(R.id.tv_name);
        mMsgLl = view.findViewById(R.id.ll_msg);
        mMsgAvatarIv = view.findViewById(R.id.iv_msg_avatar);
        mMsgTv = view.findViewById(R.id.tv_msg);
        mSearchll = view.findViewById(R.id.ll_search);
        setOnClickListener(mMsgLl, mAvatarIv, mSearchll);
        mListView.addHeaderView(view);
        mAdapter = new FriendAdapter(FriendActivity.this, moment, this);
        mListView.setAdapter(mAdapter);
    }

    private void setOnClickListener(LinearLayout mMsgLl, CircleImageView mAvatarIv, LinearLayout mSearchll) {
        mMsgLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendActivity.this, NewMsgActivity.class);
                intent.putExtra("mid", mid);
                startActivity(intent);
            }
        });
        mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendActivity.this, EditDataActivity.class);
                intent.putExtra("UserId", mid);
                startActivity(intent);
            }
        });
        mSearchll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendActivity.this, FriendSearchActivity.class));
            }
        });
    }


    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);
    }

    private void loadData(final String mid, int page,double lng,double lat) {
        //清空更新朋友圈消息
        if (!"".equals(SharedPreferencesUtils.getParameter(FriendActivity.this, "avatar", ""))) {
            SharedPreferencesUtils.removeParameter(FriendActivity.this, "avatar");
        }

        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            forLoad = true;
            moment.clear();
        }

        FriendDbHelper.moments(mid, page,lng,lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();

                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    forLoad = true;
                    moment.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseMomentsData(successJson).size() > 0) {
                            for (FriendEntity.Moments item : parseMomentsData(successJson)) {
                                moment.add(item);
                            }
                        } else {
                            ToastUtils.showToast("暂无数据");
                        }
                        message = parseMsgData(successJson);
                        mine = parseMineData(successJson);
                        if (forLoad) {
                            forLoad = false;
                            if (null != mine) {
                                mNameTv.setText(mine.getUname());
                                if (!"".equals(mine.getAvatar())) {
                                    ImgLoaderManager.getInstance().showImageView(mine.getAvatar(), mAvatarIv);
                                }
                                if (!"".equals(mine.getBackgroundImage())) {
                                    ImgLoaderManager.getInstance().showImageView(mine.getBackgroundImage(), mBackgIv);
                                }
                            }

                            if (null != message && Integer.valueOf(message.getCount()) > 0) {
                                mMsgLl.setVisibility(View.VISIBLE);
                                mMsgTv.setText(message.getCount() + "新消息");
                                ImgLoaderManager.getInstance().showImageView(message.getAvatar(), mMsgAvatarIv);
                            } else {
                                mMsgLl.setVisibility(View.GONE);
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
                dismissProgressDialog();
                ToastUtils.showToast("数据返回异常：" + failueJson);
            }
        });
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
        startActivity(new Intent(this, VideoDynamicActivity.class));
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

    public FriendEntity.Mine parseMineData(String result) {//当前用户信息

        FriendEntity.Mine mine = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            mine = gson.fromJson(data.getString("mine"), FriendEntity.Mine.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mine;
    }

    public ArrayList<FriendEntity.Moments> parseMomentsData(String result) {//相册

        ArrayList<FriendEntity.Moments> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));

            JSONArray array = new JSONArray(data.getString("moments"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                FriendEntity.Moments entity = gson.fromJson(array.optJSONObject(i).toString(), FriendEntity.Moments.class);
                moment.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;

        page = 1;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(mid, 1,lng,lat);
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
                loadData(mid, page,lng,lat);
            }
        }, 500);
    }

    @Override
    public void onMsgClick(FriendEntity.Message message) {
        Intent intent = new Intent(FriendActivity.this, NewMsgActivity.class);
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    /**
     * 列表点赞
     *
     * @param view
     */
    @Override
    public void onZanClick(View view,FriendAdapter.ViewHolder viewHolder) {
        FriendEntity.Moments item = moment.get((Integer) view.getTag());

        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item);
        } else {
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
                            loadData(mid, 1,lng,lat);
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

    private void cancelGood(FriendEntity.Moments item) {//取消点赞
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
                        isZan = true;//清除数据重新加载
                        loadData(mid, 1,lng,lat);
                    }else {
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

    @Override
    public void onShareClick(View view) {//分享
        FriendEntity.Moments item = moment.get((Integer) view.getTag());
        mArticleId = item.getId();
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        FriendEntity.Moments item = moment.get((Integer) view.getTag());
        Intent intent = new Intent(FriendActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    @Override
    public void onAvatarClick(FriendEntity.Mine mine) {
        Intent intent = new Intent(FriendActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mid);
        startActivity(intent);
    }


    /**
     * 朋友圈 列表
     * 点击别把头像事件
     *
     * @param view
     */
    @Override
    public void onItemAvatarClick(View view) {
        FriendEntity.Moments item = moment.get((Integer) view.getTag());

        if (mid.equals(item.getUserId())) {
            Intent intent = new Intent(FriendActivity.this, EditDataActivity.class);
            intent.putExtra("UserId", mid);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(FriendActivity.this, EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {//举报收藏
        FriendEntity.Moments mItem = moment.get((Integer) view.getTag());
        bottomMenu(mItem);
    }

    @Override
    public void onImageItemClick(View view, int index) {

        FriendEntity.Moments mItem = moment.get((Integer) view.getTag());
        ArrayList<String> urls = new ArrayList<>();
        for (FriendEntity.Pictures image : mItem.getUserArticlePictures()) {
            urls.add(image.getPath());
        }
        Intent intent = new Intent(FriendActivity.this, ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
        FriendActivity.this.startActivity(intent);
    }

    @Override
    public void onPlayView(String path) {//视频播放

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();

        PictureSelector.create(FriendActivity.this).externalPictureVideo(path);
    }

    @Override
    public void onAMapLocation(View view) {
        FriendEntity.Moments item = moment.get((Integer) view.getTag());
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("lat", item.getLat());
        intent.putExtra("lng", item.getLng());
        intent.putExtra("name", item.getUser().getUsername());
        startActivity(intent);
    }

    @Override
    public void onRedClick(View view) {

    }

    private void bottomMenu(final FriendEntity.Moments mItem) {

        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();

        if (mItem.getCollectioned().equals("1")) {
            menuItem1.setText("取消收藏");
        } else {
            menuItem1.setText("收藏");
        }
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
        MenuItem menuItem2 = new MenuItem();

        if (mItem.getUserId().equals(mid)) {
            menuItem2.setText("删除");
        } else {
            menuItem2.setText("举报");
        }

        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                if (mItem.getCollectioned().equals("1")) {
                    cancelCollection(mItem);//取消收藏
                } else {
                    collection(mItem);//收藏
                }
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                if (mItem.getUserId().equals(mid)) {//删除
                    delete(mItem);
                } else {
                    onReport(mItem);
                }
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void delete(FriendEntity.Moments mItem) {//自己的朋友圈删除功能
        FriendDbHelper.delArticle(mid, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(mid, 1,lng,lat);
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

    private void cancelCollection(FriendEntity.Moments mItem) {//取消收藏
        CustomerDbHelper.cancelCollectionUserArticle(mid, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(mid, 1,lng,lat);
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

    private void collection(FriendEntity.Moments mItem) {//收藏

        CustomerDbHelper.collectionUserArticle(mid, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(mid, 1,lng,lat);
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

    private void onReport(final FriendEntity.Moments mItem) {//举报
        FriendDbHelper.reportArticle(mid, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        Intent intent = new Intent(FriendActivity.this, ReportActivity.class);
                        intent.putExtra("uid", mItem.getUserId());
                        intent.putExtra("thisId", mid);
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

        EventBus.getDefault().post(new FriendsEvent("0", ""));
        //更新发现朋友圈
        EventBus.getDefault().post(new MessageEvent("FindMsg"));
        //取消注册事件
        EventBus.getDefault().unregister(FriendActivity.this);
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(FriendEvent friendEvent) {
        //   mMultipleStatusView.loading();
        isZan = true;
        loadData(mid, 1,lng,lat);
        //清空MainActivity 发现角标
        EventBus.getDefault().post(new PushExtraEvent("0", ""));
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onShareUpdata(ShareMsgEvent shareMsgEvent) {
        FriendDbHelper.shareArticle(mid, mArticleId, new IOAuthCallBack() {
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
                        loadData(mid, 1,lng,lat);
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
