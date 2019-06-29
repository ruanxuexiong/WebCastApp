package com.android.nana.find;

/**
 * Created by lenovo on 2019/2/26.
 */

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.common.BaseApplication;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.eventBus.CloseFragmentEvent;
import com.android.nana.find.adapter.PhotoWallAdapter;
import com.android.nana.find.bean.Moment;
import com.android.nana.find.http.HttpService;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.main.MainActivity;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindPhotoWallFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

        private String mUserId;
    private int mPage = 1;
    private MultipleStatusView mMultipleStatusView;
    private ListView mListView;
    private PhotoWallAdapter mAdapter;
    private boolean isLoadMore = false;
    private RefreshLayout mRefreshLayout;
    private double lng = 0.0, lat = 0.0;
    private boolean isPrepared;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private ArrayList<Moment.Moments> mDataList = new ArrayList<>();
    private ArrayList<ArrayList<Moment.Moments>> mMapDataList = new ArrayList<>();

    public static FindPhotoWallFragment newInstance() {
        FindPhotoWallFragment fragment = new FindPhotoWallFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //可见的并且是初始化之后才加载
        if (isPrepared && isVisibleToUser) {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }


    @Override
    protected void initData() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        if (NetWorkUtils.isNetworkConnected(getActivity())) {
            mMultipleStatusView.loading();
            mUserId = BaseApplication.getInstance().getCustomerId(getActivity());
            loadData(mPage);
            initLocation();
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_find_photo_wall;
    }

    @Override
    public void initView() {
        mMultipleStatusView = mContentView.findViewById(R.id.multiple_status_view);
        mListView = mContentView.findViewById(R.id.list_view);
        mRefreshLayout = mContentView.findViewById(R.id.refreshLayout);
        mAdapter = new PhotoWallAdapter(getActivity(), mMapDataList);
        mListView.setAdapter(mAdapter);
        isPrepared = true;
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(getActivity());
        locationClient.setLocationOption(getDefaultOption());
        locationClient.setLocationListener(locationListener);
        locationClient.setLocationOption(locationOption);
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
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
                if (lng != 0.0 && lat != 0.0) {
                    locationClient.stopLocation();
                    mRefreshLayout.autoRefresh();
                }
                else {

                }
            }
        }
    };


    private void loadData(int mPage) {

        HttpService.discovery(mUserId, lat, lng, mPage, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mDataList = new ArrayList<>();
                        if (parseData(successJson).size() > 0) {
                            for (Moment.Moments item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        }
                        mMapDataList.add(mDataList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (isLoadMore) {
                            isLoadMore = false;
                            ToastUtils.showToast("暂无数据");
                            mRefreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            isLoadMore = false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
            }
        });

    }

    @Override
    public void bindEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    private ArrayList<Moment.Moments> parseData(String result) {
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

    @Override
    public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
        mPage++;
        isLoadMore = true;
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(mPage);
                refreshLayout.finishLoadMore();
            }
        }, 500);
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        mPage = 1;
        isLoadMore = false;
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new CloseFragmentEvent());
                mDataList.clear();
                mMapDataList.clear();
                loadData(mPage);
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        }, 500);
    }
}
