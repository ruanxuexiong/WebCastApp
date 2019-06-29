package com.android.nana.find;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.common.BaseApplication;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.CloseFragmentEvent;
import com.android.nana.find.adapter.NearbyAdapter;
import com.android.nana.find.base.MineEvent;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.friend.AlbumDetailsActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditPersonalActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.util.MD5;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.ImageBrowserActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/9/26.
 */

public class NearbyFragment extends BaseFragment implements NearbyAdapter.FlowListener {
    private FrameLayout main_frame;
    private MultipleStatusView mMultipleStatusView;
    private String mUserId;
    private int mPage = 1;
    private boolean isLoadingMore = false;
    private NearbyAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<Moment.Moments> mDataList = new ArrayList<>();
    private ListView mListView;
    private boolean isZan = false;//是否点击加载列表
    private boolean isLoad = false;//是否需要加载更多
    private String mArticleId;
    private Moment.Mine mine;
    private CustomPopWindow mCustomPopWindow;

    public LatLng BEIJING;//经纬度
    private double lng = 0.0, lat = 0.0;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private int firstVisible;//当前第一个可见的item

    public static NearbyFragment newInstance() {
        NearbyFragment fragment = new NearbyFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        if (NetWorkUtils.isNetworkConnected(getActivity())) {
            mMultipleStatusView.loading();
            mUserId = BaseApplication.getInstance().getCustomerId(getActivity());
            //  loadLngLatData(true, lng, lat);
            initLocation();
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_nearby;
    }

    @Override
    public void initView() {
        //  main_frame=getActivity().findViewById(R.id.main_frame);
        mMultipleStatusView = mContentView.findViewById(R.id.multiple_status_view);
        mListView = mContentView.findViewById(R.id.list_view);
        mRefreshLayout = mContentView.findViewById(R.id.layout_swipe_refresh);

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccent));
        mAdapter = new NearbyAdapter(getActivity(), mDataList, this, true);
        mListView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                EventBus.getDefault().post(new CloseFragmentEvent());
                loadData(true);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (isLoad && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore) {
                    isLoadingMore = true;
                    loadData(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                isLoad = ((firstVisibleItem + visibleItemCount) == totalItemCount);
                if (firstVisible == firstVisibleItem) {
                    return;
                }
                firstVisible = firstVisibleItem;
            }
        });
    }

    @Override
    public void bindEvent() {

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
                double lngMath = location.getLongitude();//经    度
                double latMath = location.getLatitude();//纬    度
                lng = (Math.round(lngMath * 1000000) / 1000000.0);
                lat = (Math.round(latMath * 1000000) / 1000000.0);
                if (lng != 0.0 && lat != 0.0) {
                    locationClient.stopLocation();
                    loadLngLatData(true, lng, lat);
                } else {
                    locationClient.stopLocation();
                    loadLngLatData(true, lng, lat);
                    mMultipleStatusView.dismiss();
                    //  toast_();
                    //  Toast.makeText(getActivity(),"没有开启定位",Toast.LENGTH_LONG).show();
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //    main_frame.setVisibility(View.VISIBLE);
    }


    private void loadLngLatData(boolean isRefresh, double lng, double lat) {
        if (isRefresh || mDataList.size() < 1) {
            mPage = 1;
        } else {
            mPage++;
        }
        if (mPage == 1) {
            mDataList.clear();
        }
        HttpService.getByDistance(mUserId, mPage, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mRefreshLayout.setRefreshing(false);
                isLoadingMore = false;
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseMomentsData(successJson).size() > 0) {
                            for (Moment.Moments item : parseMomentsData(successJson)) {
                                mDataList.add(item);
                            }
                        }
                        mine = parseMineData(successJson);
                        EventBus.getDefault().post(new MineEvent(mine));
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast("暂无数据");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                    mRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mRefreshLayout.setRefreshing(false);
                mMultipleStatusView.dismiss();
                mMultipleStatusView.error();
            }
        });
    }

    private void loadData(boolean isRefresh) {
        if (isRefresh || mDataList.size() < 1) {
            mPage = 1;
        } else {
            mPage++;
        }
        if (mPage == 1) {
            mDataList.clear();
        }

        HttpService.getByDistance(mUserId, mPage, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                mRefreshLayout.setRefreshing(false);
                isLoadingMore = false;
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseMomentsData(successJson).size() > 0) {
                            for (Moment.Moments item : parseMomentsData(successJson)) {
                                mDataList.add(item);
                            }
                        }
                        mine = parseMineData(successJson);
                        EventBus.getDefault().post(new MineEvent(mine));
                    } else {
                        mMultipleStatusView.noEmpty();
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mRefreshLayout.setRefreshing(false);
                    mMultipleStatusView.error();
                    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mRefreshLayout.setRefreshing(false);
                mMultipleStatusView.error();
                mMultipleStatusView.dismiss();
            }
        });
    }

    public ArrayList<Moment.Moments> parseMomentsData(String result) {
        ArrayList<Moment.Moments> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("moments"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                Moment.Moments entity = gson.fromJson(array.optJSONObject(i).toString(), Moment.Moments.class);
                moment.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }


    public Moment.Mine parseMineData(String result) {//当前用户信息

        Moment.Mine mine = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            mine = gson.fromJson(data.getString("mine"), Moment.Mine.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mine;
    }

    @Override
    public void onMsgClick(Moment message) {

    }

    @Override
    public void onZanClick(View view, NearbyAdapter.ViewHolder viewHolder) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());

        if (item.getLaudResult().equals("1")) {//取消点赞
            cancelGood(item, viewHolder);
        } else {
            article(mUserId, item, viewHolder);
        }
    }

    //领取红包点赞
    public void article(String mUserId, final Moment.Moments item, final NearbyAdapter.ViewHolder viewHolder) {
        String time = Utils.getTime();
        String secret = mUserId + "&" + item.getId() + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + item.getShowBound();
        String sign = MD5.MD5Hash(secret);
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        PointFragment dialog = PointFragment.newInstance("99", "http://qiniu.nanapal.com/SFZ_video_20190409132006821.mp4", "2", "http://qiniu.nanapal.com/SFZ_video_20190409132006821.mp4", "-1", 8);
//        dialog.show(fm, "dialog");
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

                        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.main_blue));

                        if (result.getString("description").equals("点赞成功")) {
                            ToastUtils.showToast(result.getString("description"));
                         //   FragmentManager fm = getActivity().getSupportFragmentManager();
                        //    PointFragment dialog = PointFragment.newInstance("", "", "1", "", "1",10);
                        //    dialog.show(fm, "dialog");
                        } else {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String bound = data.getString("bound");
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            int timeout=data.getInt("timeout");
                          //  Log.e("TAG","bound=="+bound+"mAdvertising="+mAdvertising+"mAdvType="+mAdvType+"mAdvUrl="+mAdvUrl+"timeout="+timeout);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",timeout);
                            dialog.show(fm, "dialog");
                        }
                    }
                    else if (result.getString("state").equals("-2")){
                        showNormalDialog();
                    }
                    else {
                     //   ToastUtils.showToast("该动态已点赞111111111111");

                        if (result.getString("description").equals("此红包已领完")) {

                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                            dialog.show(fm, "dialog");

                            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.main_blue));
                        } else if (result.getString("description").equals("该动态已点赞")){
                         //   ToastUtils.showToast(result.getString("description"));

                    //        ToastUtils.showToast("该动态已点赞---");
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String bound = data.getString("bound");
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            int timeout=data.getInt("timeout");
                            //  Log.e("TAG","bound=="+bound+"mAdvertising="+mAdvertising+"mAdvType="+mAdvType+"mAdvUrl="+mAdvUrl+"timeout="+timeout);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",0);
                            dialog.show(fm, "dialog");

                        }
                        else {
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
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        mArticleId = item.getId();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mUserId);
        startActivity(intent);
    }

    @Override
    public void onAvatarClick(Moment.Mine mine) {
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mUserId);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());

        if (mUserId.equals(item.getUserId())) {
            Intent intent = new Intent(getActivity(), EditDataActivity.class);
            intent.putExtra("UserId", mUserId);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(getActivity(), EditDataActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId", item.getUserId());
            intentEdit.putExtras(bundle);
            startActivity(intentEdit);
        }
    }

    @Override
    public void onReportClick(View view) {
        Moment.Moments mItem = mDataList.get((Integer) view.getTag());
        bottomMenu(mItem);
    }

    @Override
    public void onImageItemClick(View view, int index) {
        Moment.Moments mItem = mDataList.get((Integer) view.getTag());
        ArrayList<String> urls = new ArrayList<>();
        for (Moment.Pictures image : mItem.getUserArticlePictures()) {
            urls.add(image.getPath());
        }
        Intent intent = new Intent(getActivity(), ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
        startActivity(intent);
    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(getActivity()).externalPictureVideo(path);
    }

    @Override
    public void onAMapLocation(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), LocationActivity.class);
        intent.putExtra("lat", item.getLat());
        intent.putExtra("lng", item.getLng());
        intent.putExtra("name", item.getUser().getUsername());
        startActivity(intent);
    }

    @Override
    public void onRedClick(View view) {//点击红包
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_red_layout, null);

        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(getActivity())
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

    private void cancelGood(final Moment.Moments item, final NearbyAdapter.ViewHolder viewHolder) {//取消点赞
        CustomerDbHelper.cancelLaudUserArticle(mUserId, item.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));

                      if (result.getString("description").equals("已领红包无法取消点赞")){
                        //   ToastUtils.showToast(result.getString("description"));
                 //         ToastUtils.showToast("已领红包无法取消点赞");
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                       // int timeout=data.getInt("timeout");
                        //  Log.e("TAG","bound=="+bound+"mAdvertising="+mAdvertising+"mAdvType="+mAdvType+"mAdvUrl="+mAdvUrl+"timeout="+timeout);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",0);
                        dialog.show(fm, "dialog");

                    }


                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        //isZan = true;//清除数据重新加载
                        //loadData(true);
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getActivity().getResources().getColor(R.color.green_54));

                    } else {
                    //    ToastUtils.showToast("else---");
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                      //  int timeout = data.getInt("timeout");
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "-1", 0);
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

    private void bottomMenu(final Moment.Moments mItem) {
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

        if (mItem.getUserId().equals(mUserId)) {
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

                if (mItem.getUserId().equals(mUserId)) {//删除
                    delete(mItem);
                } else {
                    onReport(mItem);
                }
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getActivity().getFragmentManager(), "BottomMenuFragment");
    }


    private void cancelCollection(Moment.Moments mItem) {//取消收藏
        CustomerDbHelper.cancelCollectionUserArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(true);
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

    private void delete(Moment.Moments mItem) {//自己的朋友圈删除功能
        FriendDbHelper.delArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(true);
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

    private void onReport(final Moment.Moments mItem) {//举报
        FriendDbHelper.reportArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        Intent intent = new Intent(getActivity(), ReportActivity.class);
                        intent.putExtra("uid", mItem.getUserId());
                        intent.putExtra("thisId", mUserId);
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


    private void collection(Moment.Moments mItem) {//收藏

        CustomerDbHelper.collectionUserArticle(mUserId, mItem.getId(), new IOAuthCallBack() {
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
                        loadData(true);
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


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onClick(View v) {

    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请先完善您的头像和昵称资料？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        //    getHuafei();
                        Intent intent = new Intent(getActivity(), EditPersonalActivity.class);
                        intent.putExtra("thisUid", mUserId);
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
