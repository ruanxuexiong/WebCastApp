package com.android.nana.find.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.find.bean.MapMarker;
import com.android.nana.friend.PhoneContactActivity;
import com.android.nana.friend.PhoneContactFragment;
import com.android.nana.friend.RedPhoneContactActivity;
import com.android.nana.user.ConversationActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.pay.SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;


/**
 * Created by lenovo on 2018/9/29.
 */

public class MarkerOverlay {
    private String time="";
    private List<MapMarker.Users> pointList = new ArrayList<>();
    private List<MapMarker.Articles> articles = new ArrayList<>();
    private AMap aMap;
    private LatLng centerPoint;
    private Marker centerMarker;
    private Context mContext;
    private List<LatLng> mLatLng = new ArrayList<>();
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private ArrayList<String> user_id = new ArrayList<>();
    private ArrayList<String> articles_id = new ArrayList<>();
    private boolean sendMsg = false;

    public MarkerOverlay(Context mContext, AMap amap, List<MapMarker.Users> points, List<MapMarker.Articles> articles, LatLng centerpoint) {
        this.aMap = amap;
        this.mContext = mContext;
        this.centerPoint = centerpoint;
        initPointList(points);
        initArticles(articles);
        initCenterMarker();
    }


    //初始化list
    private void initPointList(List<MapMarker.Users> points) {
        if (points != null && points.size() > 0) {
            for (MapMarker.Users point : points) {
                pointList.add(point);
            }
        }
    }

    private void initArticles(List<MapMarker.Articles> articles) {
        if (articles != null && articles.size() > 0) {
            for (MapMarker.Articles point : articles) {
                this.articles.add(point);
            }
        }
    }

    //初始化中心点Marker
    private void initCenterMarker() {
        this.centerMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point6))
                .position(centerPoint)
                .title("您在此处"));
        centerMarker.showInfoWindow();
    }

    /**
     * 设置改变中心点经纬度
     *
     * @param centerpoint 中心点经纬度
     */
    public void setCenterPoint(LatLng centerpoint) {
        this.centerPoint = centerpoint;
        if (centerMarker == null)
            initCenterMarker();
        this.centerMarker.setPosition(centerpoint);
        centerMarker.setVisible(true);
        centerMarker.showInfoWindow();
    }

    /**
     * 添加Marker到地图中。
     * new MarkerOptions().position(pointList.get(i).get_id()).
     * icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.icon_map_female)))
     */
    public void addToMap() {
        try {
            for (int i = 0; i < pointList.size(); i++) {
                MarkerOptions options = new MarkerOptions();
                mLatLng.add(new LatLng(Double.parseDouble(pointList.get(i).getLat()), Double.parseDouble(pointList.get(i).getLng())));
                for (int x = 0; x < mLatLng.size(); x++) {
                    options.position(mLatLng.get(x));
                }
                if ("1".equals(pointList.get(i).getSex())) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_map_male)));
                } else {
                    options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_map_female)));
                }
                user_id.add(pointList.get(i).get_id());
                Marker marker = aMap.addMarker(options);
                marker.setTitle("用户");
                marker.setObject(pointList.get(i));
                mMarkers.add(marker);
            }
            for (int i = 0; i < articles.size(); i++) {
                MarkerOptions options = new MarkerOptions();
                mLatLng.add(new LatLng(Double.parseDouble(articles.get(i).getLat()), Double.parseDouble(articles.get(i).getLng())));
                for (int x = 0; x < mLatLng.size(); x++) {
                    options.position(mLatLng.get(x));

                }
                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_art)));
                Marker marker = aMap.addMarker(options);
                marker.setTitle("红包");
                marker.setObject(articles.get(i));
                articles_id.add(articles.get(i).getId());
                mMarkers.add(marker);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                if (marker.getTitle().equals("红包") || marker.getTitle().equals("您在此处") || marker.getTitle().equals("用户")) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.marker_info_window, null);
                    TextView tv_title = view.findViewById(R.id.tv_title);
                    Button btn_detail = view.findViewById(R.id.btn_detail);
                    MapMarker.Users users = null;
                    MapMarker.Articles article = null;
                    if (marker.getObject() instanceof MapMarker.Users) {
                        users = (MapMarker.Users) marker.getObject();
                    } else if (marker.getObject() instanceof MapMarker.Articles) {
                        article = (MapMarker.Articles) marker.getObject();
                    }
                    final MapMarker.Users finalUsers = users;
                    btn_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (marker.getTitle().equals("红包")) {
                                Intent intent = new Intent(mContext, RedPhoneContactActivity.class);
                                mContext.startActivity(intent);
                            } else if (marker.getTitle().equals("用户")) {
                                toInform(finalUsers.getUid(), finalUsers.getUserName(), finalUsers.getNotice_msg());

                            }
                        }
                    });
                    if (marker.getTitle().equals("您在此处")) {
                        btn_detail.setVisibility(View.GONE);
                        tv_title.setText(marker.getTitle());
                    } else if (marker.getTitle().equals("红包")) {
                        btn_detail.setVisibility(View.VISIBLE);
                        if (article != null) {
                            StringBuilder stringBuilder = new StringBuilder(article.getNoticeFirendMsg());
                            if (stringBuilder.length() > 10) {
                                tv_title.setText(stringBuilder.insert(stringBuilder.length() / 2, "\n"));
                            } else
                                tv_title.setText(stringBuilder);
                        }
                        btn_detail.setText("马上通知");
                        btn_detail.setBackground(mContext.getResources().getDrawable(R.drawable.map_bg_select));
                    } else {
                        btn_detail.setVisibility(View.VISIBLE);
                        if (users != null) {
                            StringBuilder stringBuilder = new StringBuilder(users.getInform_msg());
                            if (stringBuilder.length() > 10) {
                                tv_title.setText(stringBuilder.insert(stringBuilder.length() / 2, "\n"));
                            } else
                                tv_title.setText(stringBuilder);
                            if (!users.getInform_status().equals("0")) {
                                btn_detail.setVisibility(View.GONE);
                            } else {
                                btn_detail.setVisibility(View.VISIBLE);
                            }
                        }
                        btn_detail.setText("发消息");
                        btn_detail.setBackground(mContext.getResources().getDrawable(R.drawable.map_bg_blue_select));
                    }
                    return view;
                }
                return null;
            }
        });

    }

    public void toInform(final String uid, final String name, final String notice_msg) {
        FriendDbHelper.toInform(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = jsonObject.getJSONObject("result");
                    if (result.getString("state").equals("-1")) {
                        ToastUtils.showToast(result.getString("description"));
                    } else {
                        showSendDialog(uid, name, notice_msg);
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

    public void showSendDialog(final String uid, final String name, String notice_msg) {
        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        final PhoneContactFragment dialog = PhoneContactFragment.newInstance(name, notice_msg, "");
        dialog.show(fm, "dialog");
        dialog.setOnClickListener(new PhoneContactFragment.OnClick() {
            @Override
            public void sendMsg(String msg) {
                TextMessage textMessage = TextMessage.obtain(msg);
                RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, uid, textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        sendMsg = true;
                        startChat(uid, name);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void dismiss() {
                if (sendMsg) {
                    String mid = (String) SharedPreferencesUtils.getParameter(mContext, "userId", "");
                    time=getTime();
                    Map<String,String> map=new HashMap<>();
                    map.put("userId",mid);
                    map.put("toUid",uid);
                    map.put("time",time);
                    String sign= SignUtils.signSort(map);
                    FriendDbHelper.getInformResult(mid, uid,sign,time, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {

                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });
                    sendMsg = false;
                }
            }
        });
    }

    public void startChat(final String uid, final String name) {
        String mid = (String) SharedPreferencesUtils.getParameter(mContext, "userId", "");
        HomeDbHelper.isFriend(mid, uid, new IOAuthCallBack() {
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
                        if (data.getString("friend").equals("2")) {//非好友状态
                            JSONObject mTanchuang = new JSONObject(data.getString("tanchuang"));
                            String isTanchang = mTanchuang.getString("isTanchang");//（1=弹窗   2=不弹窗）
                            String mMoney = mTanchuang.getString("money");
                            String mEnough = mTanchuang.getString("enough");//余额是否足够：enough（1=是  -1=否）
                            RongIM.getInstance().startPrivateChat(mContext, uid + "=" + isTanchang + "=" + mMoney + "=" + mEnough + "=", name);
                            return;
                        } else {
                            RongIM.getInstance().startPrivateChat(mContext, uid, name);
                            return;
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

    /**
     * 去掉MarkerOverlay上所有的Marker。
     */
    public void removeFromMap() {
        for (Marker mark : mMarkers) {
            mark.remove();
        }
        pointList.clear();
        articles.clear();
        user_id.clear();
        articles_id.clear();
        centerMarker.remove();
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中，且地图中心点不变。
     */
    public void zoomToSpanWithCenter() {
        if (mLatLng != null && mLatLng.size() > 0) {
            if (aMap == null)
                return;
            centerMarker.setVisible(true);
            centerMarker.showInfoWindow();
            LatLngBounds bounds = getLatLngBounds(centerPoint, mLatLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }

    /**
     * 缩放移动地图
     */
    public void zoomLatLngZoom() {
        if (aMap == null)
            return;
        centerMarker.setVisible(true);
        centerMarker.showInfoWindow();
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 12));
    }

    //根据中心点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerpoint, List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (centerpoint != null) {
            for (int i = 0; i < pointList.size(); i++) {
                LatLng p = pointList.get(i);
                LatLng p1 = new LatLng((centerpoint.latitude * 2) - p.latitude, (centerpoint.longitude * 2) - p.longitude);
                b.include(p);
                b.include(p1);
            }
        }
        return b.build();
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中。
     */
 /*   public void zoomToSpan() {
        if (pointList != null && pointList.size() > 0) {
            if (aMap == null)
                return;
            centerMarker.setVisible(false);
            LatLngBounds bounds = getLatLngBounds(pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }*/

    /**
     * 根据自定义内容获取缩放bounds
     */
    private LatLngBounds getLatLngBounds(List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < pointList.size(); i++) {
            LatLng p = pointList.get(i);
            b.include(p);
        }
        return b.build();
    }

    /**
     * 添加Marker点
     */
    public void addPoint(List<MapMarker.Users> pointList, List<MapMarker.Articles> articles) {
        try {
            for (int i = 0; i < pointList.size(); i++) {
                MarkerOptions options = new MarkerOptions();
                mLatLng.add(new LatLng(Double.parseDouble(pointList.get(i).getLat()), Double.parseDouble(pointList.get(i).getLng())));
                for (int x = 0; x < mLatLng.size(); x++) {
                    options.position(mLatLng.get(x));
                }
                if ("1".equals(pointList.get(i).getSex())) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_map_male)));
                } else {
                    options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_map_female)));
                }
                if (!user_id.contains(pointList.get(i).get_id())) {
                    user_id.add(pointList.get(i).get_id());
                    Marker marker = aMap.addMarker(options);
                    marker.setObject(pointList.get(i));
                    marker.setTitle("用户");
                    mMarkers.add(marker);
                } else {
                    for (Marker marker : mMarkers) {
                        if (marker.getObject() instanceof MapMarker.Users) {
                            if (((MapMarker.Users) marker.getObject()).get_id().equals(pointList.get(i).get_id())) {
                                marker.setObject(pointList.get(i));

                            }
                        }
                    }
                }
            }
            for (int i = 0; i < articles.size(); i++) {
                if (!articles_id.contains(articles.get(i).getId())) {
                    MarkerOptions options = new MarkerOptions();
                    mLatLng.add(new LatLng(Double.parseDouble(articles.get(i).getLat()), Double.parseDouble(articles.get(i).getLng())));
                    for (int x = 0; x < mLatLng.size(); x++) {
                        options.position(mLatLng.get(x));
                    }
                    options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_art)));
                    Marker marker = aMap.addMarker(options);
                    marker.setTitle("红包");
                    marker.setObject(articles.get(i));
                    articles_id.add(articles.get(i).getId());
                    mMarkers.add(marker);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }
}
