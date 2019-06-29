package com.android.nana.friend;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.bean.CollectionEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditPersonalActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.user.weight.VideoPlayerController;
import com.android.nana.util.MD5;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.ImageBrowserActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/10/24.
 */

public class CollectionActvity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, CollectionAdapter.CollectionListener {

    private String mid;
    private int page = 1;
    private TextView mTitleTv;
    private TextView mBackTv;

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<CollectionEntity> mDataList = new ArrayList<>();

    private CustomPopWindow mCustomPopWindow;
    private CollectionAdapter mAdapter;
    private boolean isZan = false;//是否点击加载列表
    public LatLng BEIJING;//经纬度
    private double lng, lat;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private int firstVisible;//当前第一个可见的item
    private int visibleCount;//当前可见的item个数
    private NiceVideoPlayer mVideoPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mid = (String) SharedPreferencesUtils.getParameter(CollectionActvity.this, "userId", "");

        if (NetWorkUtils.isNetworkConnected(CollectionActvity.this)) {
            mMultipleStatusView.loading();
            // loadData(mid, page);
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetWorkUtils.isNetworkConnected(CollectionActvity.this)) {
            mMultipleStatusView.loading();
            isZan = true;
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
                lng = location.getLongitude();//经    度
                lat = location.getLatitude();//纬    度
                if (lng != 0.0 && lat != 0.0) {
                    locationClient.stopLocation();
                    loadLngLatData(mid, page, lng, lat);
                } else {
                    locationClient.stopLocation();
                    loadLngLatData(mid, page, lng, lat);
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    private void loadLngLatData(String mid, int page, double lng, double lat) {
        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.collection(mid, page, lng, lat, new IOAuthCallBack() {
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
                            for (CollectionEntity item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        } else {
                            mMultipleStatusView.noEmpty();
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
        setContentView(R.layout.activity_collection);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);

        mListView = findViewById(R.id.content_view);
        mLayout = findViewById(R.id.refresh_view);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // autoPlayVideo(view);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisible == firstVisibleItem) {
                    return;
                }
                firstVisible = firstVisibleItem;
                visibleCount = visibleItemCount;

            }
        });
    }

    private void autoPlayVideo(AbsListView view) {
        for (int i = 0; i < visibleCount; i++) {
            if (view != null && view.getChildAt(i) != null && view.getChildAt(i).findViewById(R.id.video_player) != null) {
                VideoPlayerController controller = new VideoPlayerController(this);
                mVideoPlayer = view.getChildAt(i).findViewById(R.id.video_player);
                mVideoPlayer.setController(controller);
                int percents = 100;
                Rect rect = new Rect();
                //获取当前view 的 位置
                mVideoPlayer.getLocalVisibleRect(rect);
                int height = mVideoPlayer.getHeight();
                if (rect.top > 0) {
                    percents = (height - rect.top) * 100 / height;
                } else if (viewIsPartiallyHiddenBottom(rect, height)) {
                    percents = rect.bottom * 100 / height;
                }
                if (percents > 10) {
                    mVideoPlayer.start();
                    return;
                }
            }
        }
        //释放其他视频资源
        if (null != mVideoPlayer) {
            mVideoPlayer.release();
        }
    }

    private boolean viewIsPartiallyHiddenBottom(Rect rect, int height) {
        return rect.bottom > 0 && rect.bottom < height;
    }


    @Override
    protected void init() {
        mTitleTv.setText("收藏");
        mBackTv.setVisibility(View.VISIBLE);

        mAdapter = new CollectionAdapter(CollectionActvity.this, mDataList, this);
        mListView.setAdapter(mAdapter);
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

    private void loadData(String mid, int page) {

        if (isZan) {//点赞后清除数据重新加载
            isZan = false;
            mDataList.clear();
        }

        FriendDbHelper.collection(mid, page, lng, lat, new IOAuthCallBack() {
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
                            for (CollectionEntity item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        } else {
                            ToastUtils.showToast("暂无数据");
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

    public ArrayList<CollectionEntity> parseData(String result) {//相册

        ArrayList<CollectionEntity> entity = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray array = new JSONArray(jsonobject.getString("data"));

            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                CollectionEntity item = gson.fromJson(array.optJSONObject(i).toString(), CollectionEntity.class);
                entity.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void onZanClick(View view, final CollectionAdapter.ViewHolder viewHolder) {
        final CollectionEntity item = mDataList.get((Integer) view.getTag());


        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item, viewHolder);
        } else {
            String time = Utils.getTime();
            String secret = mid + "&" + item.getId() + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + item.getShowBound();
            String sign = MD5.MD5Hash(secret);

            HttpService.laudUserArticle(mid, item.getId(), time, sign, item.getShowBound(), new IOAuthCallBack() {
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

                            Drawable drawable = CollectionActvity.this.getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(CollectionActvity.this.getResources().getColor(R.color.main_blue));

                            if (result.getString("description").equals("点赞成功")) {
                                ToastUtils.showToast(result.getString("description"));
                            } else {

                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                String bound = data.getString("bound");
                                String mAdvertising = data.getString("advertising");
                                String mAdvType = data.getString("adv_type");
                                String mAdvUrl = data.getString("adv_url");
                                int timeout=data.getInt("timeout");
                                FragmentManager fm = CollectionActvity.this.getSupportFragmentManager();
                                PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",timeout);
                                dialog.show(fm, "dialog");
                            }
                        } else {
                            if (result.getString("description").equals("此红包已领完")) {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                String mAdvertising = data.getString("advertising");
                                String mAdvType = data.getString("adv_type");
                                String mAdvUrl = data.getString("adv_url");
                                FragmentManager fm = CollectionActvity.this.getSupportFragmentManager();
                                PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                                dialog.show(fm, "dialog");

                                Drawable drawable = CollectionActvity.this.getResources().getDrawable(R.drawable.icon_zan);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                                viewHolder.mZanTv.setTextColor(CollectionActvity.this.getResources().getColor(R.color.main_blue));
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
                    Log.e("======", failueJson);
                }
            });
        }
    }


    @Override
    public void onShareClick(View view) {
        CollectionEntity item = mDataList.get((Integer) view.getTag());
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        CollectionEntity item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(CollectionActvity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }


    @Override
    public void onItemAvatarClick(View view) {
        CollectionEntity item = mDataList.get((Integer) view.getTag());

        if (mid.equals(item.getUserId())) {
            Intent intentEdit = new Intent(CollectionActvity.this, EditPersonalActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("thisUid", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        } else {
            Intent intentEdit = new Intent(CollectionActvity.this, EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {
        CollectionEntity item = mDataList.get((Integer) view.getTag());
        bottomMenu(item);
    }

    @Override
    public void onImageItemClick(View view, int index) {
        CollectionEntity item = mDataList.get((Integer) view.getTag());
        ArrayList<String> urls = new ArrayList<>();
        for (CollectionEntity.Pictures image : item.getUserArticlePictures()) {
            urls.add(image.getPath());
        }
        Intent intent = new Intent(CollectionActvity.this, ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
        CollectionActvity.this.startActivity(intent);
    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(CollectionActvity.this).externalPictureVideo(path);
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
        mCustomPopWindow.showAsDropDown(view, -168, 0);
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

    private void cancelGood(final CollectionEntity item, final CollectionAdapter.ViewHolder viewHolder) {//取消点赞
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

                        Drawable drawable = CollectionActvity.this.getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(CollectionActvity.this.getResources().getColor(R.color.green_54));
                    } else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                        int timeout=data.getInt("timeout");
                        FragmentManager fm = CollectionActvity.this.getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "-1",timeout);
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

    private void zan(CollectionEntity item) {//点赞
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

            }
        });
    }

    private void bottomMenu(final CollectionEntity item) {
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

    private void cancelCollection(CollectionEntity mItem) {//取消收藏
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
                        loadData(mid, 1);
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

    private void collection(CollectionEntity mItem) {//收藏

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
                        loadData(mid, 1);
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


    private void onReport(final CollectionEntity mItem) {//举报
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
                        Intent intent = new Intent(CollectionActvity.this, ReportActivity.class);
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
    protected void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }
}
