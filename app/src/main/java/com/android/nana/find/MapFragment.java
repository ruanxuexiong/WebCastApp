package com.android.nana.find;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.android.common.BaseApplication;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.find.base.BaseFragment;
import com.android.nana.find.bean.MapMarker;
import com.android.nana.find.http.HttpService;
import com.android.nana.find.map.MapMsgActivity;
import com.android.nana.find.map.MarkerOverlay;
import com.android.nana.find.map.SetActivity;
import com.google.gson.Gson;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import net.soulwolf.widget.speedyselector.widget.SelectorTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by lenovo on 2018/9/28.
 */

public class MapFragment extends BaseFragment implements AMap.OnMapClickListener, View.OnClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnCameraChangeListener {

    public LatLng BEIJING;//经纬度
    private double lng, lat;
    protected static CameraPosition cameraPosition;
    private MarkerOverlay markerOverlay;
    private String mUserId;
    private ArrayList<MapMarker.Users> mDataList = new ArrayList<>();
    private ArrayList<MapMarker.Articles> mArticles = new ArrayList<>();
    View infoWindow = null;
    View thisInfoWindow = null;
    private boolean isAction = false;
    private boolean isSearch = false;
    private boolean isInfo = false;

    private String count;
    private int messageNum;
    private RelativeLayout mMsgRl;
    private TextView mSetTv;
    private SelectorTextView mMsgTv;

    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化定位
        mUserId = BaseApplication.getInstance().getCustomerId(getActivity());
        initLocation();
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
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
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
                if (lng > 0.0) {
                    locationClient.stopLocation();
                    load(mUserId, lng, lat);
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };


    @Override
    protected LatLng getTarget() {
        return BEIJING;
    }

    @Override
    protected CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    @Override
    protected void setCameraPosition(CameraPosition cameraPosition) {
        MapFragment.cameraPosition = cameraPosition;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        aMap.setOnMapClickListener(this);// 地图点击监听
        aMap.setInfoWindowAdapter(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setOnCameraChangeListener(this);

        mMsgRl = (RelativeLayout) findViewById(R.id.rl_msg);
        mMsgTv = (SelectorTextView) findViewById(R.id.tv_num);
        mSetTv = (TextView) findViewById(R.id.tv_set);

        mMsgRl.setOnClickListener(this);
        mSetTv.setOnClickListener(this);
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

                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        count = data.getString("count");
                     //   messageNum = Integer.valueOf(data.getString("messageNum"));
                        if (messageNum > 0) {
                            mMsgTv.setVisibility(View.VISIBLE);
                            mMsgTv.setText(String.valueOf(messageNum));
                        } else {
                            mMsgTv.setVisibility(View.GONE);
                        }
                        if (parseData(successJson).size() > 0) {
                            for (MapMarker.Users item : parseData(successJson)) {
                                mDataList.add(item);
                            }
                            isAction = true;
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rl_msg:
                Intent intent1 = new Intent(getActivity(), MapMsgActivity.class);
                break;
            case R.id.tv_set:
                Intent intent = new Intent(getActivity(), SetActivity.class);
                intent.putExtra("uid", mUserId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        infoWindow.setVisibility(View.GONE);
    }


    private ArrayList<MapMarker.Users> parseData(String result) {
        ArrayList<MapMarker.Users> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("users"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                MapMarker.Users entity = gson.fromJson(array.optJSONObject(i).toString(), MapMarker.Users.class);
                moment.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        if (isInfo) {
            if (infoWindow == null) {
                infoWindow = LayoutInflater.from(getContext()).inflate(R.layout.custom_view, null);
            }
            TextView mTitleTv = infoWindow.findViewById(R.id.title);
            TextView mContentTv = infoWindow.findViewById(R.id.tv_content);

         /*   MapMarker mapMarker = (MapMarker) marker.getObject();
            TextView mTitleTv = infoWindow.findViewById(R.id.title);
            TextView mContentTv = infoWindow.findViewById(R.id.tv_content);
            LinearLayout mHelloLl = infoWindow.findViewById(R.id.ll_hello);

            mHelloLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    HelloFragment dialog = HelloFragment.newInstance();
                    dialog.show(fm, "dialog");
                }
            });*/

            CircleImageView mHeadIv = infoWindow.findViewById(R.id.iv_head);

            MapMarker mapMarker = (MapMarker) marker.getObject();
            if (null != mapMarker && mapMarker.getUsers().getAvatar().equals("1")) {
                mHeadIv.setVisibility(View.GONE);
                ImgLoaderManager.getInstance().showImageView(mapMarker.getUsers().getAvatar(), mHeadIv);
                mTitleTv.setText(mapMarker.getUsers().getShow_desc());
            } else {
                return null;
            }
        } else {

        /*    if (thisInfoWindow == null) {
                thisInfoWindow = LayoutInflater.from(getContext()).inflate(R.layout.map_info_view, null);
            }
            ImageView imageView = thisInfoWindow.findViewById(R.id.iv_avatar);
            MapMarker mapMarker = (MapMarker) marker.getObject();
            if (null != mapMarker && "1".equals(mapMarker.getUsers().getSex())) {
                imageView.setImageResource(R.drawable.icon_map_male);
            } else {
                imageView.setImageResource(R.drawable.icon_map_female);
            }*/
            return thisInfoWindow;
        }
        return infoWindow;
    }

    private void render(Marker marker, View infoWindow) {
        TextView mTitleTv = infoWindow.findViewById(R.id.title);
        CircleImageView mHeadIv = infoWindow.findViewById(R.id.iv_head);

        MapMarker mapMarker = (MapMarker) marker.getObject();
        if (null != mapMarker && mapMarker.getUsers().getShow_avatar().equals("1")) {
            mHeadIv.setVisibility(View.GONE);
            ImgLoaderManager.getInstance().showImageView(mapMarker.getUsers().getAvatar(), mHeadIv);
            mTitleTv.setText(mapMarker.getUsers().getShow_desc());
        } else {
            return;
        }

        /* else {
            if (null != mapMarker && mapMarker.getShow_avatar().equals("-1")) {
                infoWindow.setVisibility(View.GONE);
                return;
            }
            mHeadIv.setVisibility(View.GONE);
            mTitleTv.setText(mapMarker.getShow_desc());
            ImgLoaderManager.getInstance().showImageView(mapMarker.getAvatar(), mHeadIv);
        }*/
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
      /*  MapMarker mapMarker = (MapMarker) marker.getObject();
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", mapMarker.getUid());
        startActivity(intent);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationClient.stopLocation();
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }


    //地图改变状态后调用
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

        if (isAction) {
            isAction = false;
            isSearch = true;
            markerOverlay = new MarkerOverlay(getActivity(), aMap, mDataList,mArticles, BEIJING);
            markerOverlay.addToMap();
            markerOverlay.zoomToSpanWithCenter();
        } else if (isSearch) {
            isSearch = false;
        } else {
            isInfo = true;
            BEIJING = cameraPosition.target;
            double lngMath = BEIJING.longitude;//经    度
            double latMath = BEIJING.latitude;//纬    度
            lng = (Math.round(lngMath * 1000000) / 1000000.0);
            lat = (Math.round(latMath * 1000000) / 1000000.0);
            mDataList.clear();
            loadData(mUserId, lng, lat);
        }
    }

    private void loadData(String mUserId, double lng, double lat) {

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
                        if (null == markerOverlay) {
                            markerOverlay = new MarkerOverlay(getActivity(), aMap, mDataList,mArticles, BEIJING);
                            markerOverlay.addToMap();
                            markerOverlay.zoomToSpanWithCenter();
                        } else {
                            markerOverlay.addPoint(mDataList,mArticles);
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

}
