package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.find.bean.MapMarker;
import com.android.nana.find.http.HttpService;
import com.android.nana.find.map.MarkerOverlay;
import com.android.nana.loading.MultipleStatusView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/1/7.
 */

public class MeLocationActivity extends BaseActivity implements AMapLocationListener, View.OnClickListener, AMap.OnMapLoadedListener, AMap.OnCameraChangeListener {

    private TextView mTitleTv, mBackTv;
    private String mUserId;
    private ArrayList<MapMarker.Users> mDataList = new ArrayList<>();
    private ArrayList<MapMarker.Articles> mArticles = new ArrayList<>();
    private AMap aMap;
    private MapView mapView;
    private LatLng BEIJING;// 中心点
    private MarkerOverlay markerOverlay;
    private boolean isAction = true;
    private boolean isSearch = false;
    private boolean isInfo = false;
    private double lng, lat;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private MultipleStatusView mMultipleStatusView;
    private Button btn_action, btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = BaseApplication.getInstance().getCustomerId(this);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_location);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mapView = findViewById(R.id.map);
        btn_location = findViewById(R.id.btn_location);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        btn_action = findViewById(R.id.btn_action);
    }

    @Override
    protected void init() {
        mTitleTv.setText("红包在哪");
        mBackTv.setVisibility(View.VISIBLE);
        initLocation();
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(this);
        locationClient.setLocationOption(getDefaultOption());
        locationClient.setLocationListener(this);
        btn_location.setOnClickListener(this);
        locationClient.setLocationOption(locationOption);
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        return mOption;
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        btn_action.setOnClickListener(this);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMapLoadedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_action:
                Intent intent = new Intent(MeLocationActivity.this, HelpOtherActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_location:
                if (markerOverlay != null)
                    markerOverlay.zoomLatLngZoom();
                break;
            default:
                break;
        }
    }

    private void load(String mUserId, double lng, double lat) {
        HttpService.getAroundUser(mUserId, String.valueOf(lng), String.valueOf(lat), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (MapMarker.Users item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                        }
                        if (articles(successJson).size() > 0) {
                            for (MapMarker.Articles item : articles(successJson)) {
                                mArticles.add(item);
                            }
                        }
                        if (null == markerOverlay) {
                            markerOverlay = new MarkerOverlay(MeLocationActivity.this, aMap, mDataList, mArticles, BEIJING);
                            markerOverlay.addToMap();
//                            markerOverlay.zoomToSpanWithCenter();
                            markerOverlay.zoomLatLngZoom();
                        } else {
                            markerOverlay.addPoint(mDataList, mArticles);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.error();
                mMultipleStatusView.dismiss();
            }
        });
    }

    private ArrayList<MapMarker.Articles> articles(String result) {
        ArrayList<MapMarker.Articles> arrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonObject.getString("data"));
            JSONArray array = new JSONArray(data.getString("article_lists"));
            String noticeFirendMsg = data.getString("noticeFirendMsg");
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                MapMarker.Articles article = gson.fromJson(array.optJSONObject(i).toString(), MapMarker.Articles.class);
                article.setNoticeFirendMsg(noticeFirendMsg);
                arrayList.add(article);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<MapMarker.Users> parseData(String result) {
        ArrayList<MapMarker.Users> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("users"));
            String notice_msg = data.getString("notice_msg");
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                MapMarker.Users entity = gson.fromJson(array.optJSONObject(i).toString(), MapMarker.Users.class);
                entity.setNotice_msg(notice_msg);
                moment.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != markerOverlay) {
            markerOverlay.removeFromMap();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location) {
            double lngMath = location.getLongitude();//经    度
            double latMath = location.getLatitude();//纬    度
            lng = (Math.round(lngMath * 1000000) / 1000000.0);
            lat = (Math.round(latMath * 1000000) / 1000000.0);
            if (lng > 0.0) {
                locationClient.stopLocation();
                load(mUserId, lng, lat);
            }
            BEIJING = new LatLng(lat, lng);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {// //地图改变状态后调用
        BEIJING = cameraPosition.target;
        double lngMath = BEIJING.longitude;//经    度
        double latMath = BEIJING.latitude;//纬    度
        lng = (Math.round(lngMath * 1000000) / 1000000.0);
        lat = (Math.round(latMath * 1000000) / 1000000.0);
        mDataList.clear();
        load(mUserId, lng, lat);
    }

    @Override
    public void onMapLoaded() {

    }
}
