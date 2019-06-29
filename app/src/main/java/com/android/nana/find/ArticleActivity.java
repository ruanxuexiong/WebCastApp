package com.android.nana.find;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.bean.ArticleByTopic;
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
import com.android.nana.friend.VideoDynamicActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditPersonalActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.MD5;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.ImageBrowserActivity;
import com.android.nana.widget.RMoreTextView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ArticleActivity extends BaseActivity implements NearbyAdapter.FlowListener {
    String title_name="", title_name2="";
    private int mPage = 1;
    private String mUserId;
    private String tagid;
    private MultipleStatusView mMultipleStatusView;
    public LatLng BEIJING;//经纬度
    private double lng = 0.0, lat = 0.0;
    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private NearbyAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<Moment.Moments> mDataList = new ArrayList<>();
    private ListView mListView;
    private boolean isLoadingMore = false;
    private String mArticleId;
    private CustomPopWindow mCustomPopWindow;
    private boolean isLoad = false;//是否需要加载更多
    private int firstVisible;//当前第一个可见的item
    private Moment.Mine mine;
    private Toolbar toolbar;
    private AppBarLayout appbar;
    private TextView mTitleTv, mBackTv;
    private RoundImageView iv_head;
    private TextView tv_name;
    private RMoreTextView tv_content;
    private TextView tv_tag_name;
    private TextView tv_count;
    private String articleId;
    private Button btn_join;
    private boolean isZan = false;//是否点击加载列表

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_article);
    }

    @Override
    protected void findViewById() {

        btn_join = findViewById(R.id.btn_join);
        iv_head = findViewById(R.id.iv_head);
        tv_name = findViewById(R.id.tv_name);
        tv_content = findViewById(R.id.tv_content);
        mTitleTv = findViewById(R.id.tv_title);
        tv_tag_name = findViewById(R.id.tv_tag_name);
        tv_count = findViewById(R.id.tv_count);
        toolbar = findViewById(R.id.toolbar);
        appbar = findViewById(R.id.appbar);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mListView = findViewById(R.id.list_view);
        mRefreshLayout = findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccent));
        mAdapter = new NearbyAdapter(ArticleActivity.this, mDataList, this, true);
        mListView.setAdapter(mAdapter);

        setAvatorChange();
    }

    /**
     * 渐变toolbar背景
     */
    private void setAvatorChange() {
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //verticalOffset始终为0以下的负数
                float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());
                toolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.main_blue), percent));
                mTitleTv.setTextColor(changeAlpha(getResources().getColor(R.color.white), percent));
            }
        });
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void init() {
        tagid = getIntent().getStringExtra("tagid");
        initData();
    }

    protected void initData() {

        if (NetWorkUtils.isNetworkConnected(ArticleActivity.this)) {
            mMultipleStatusView.loading();
            mUserId = BaseApplication.getInstance().getCustomerId(ArticleActivity.this);
            //  loadLngLatData(true, lng, lat);
            getArticleByTopic();
            initLocation();
        } else {
            mMultipleStatusView.noNetwork();
        }
    }

    @Override
    protected void setListener() {
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (articleId != null) {
                    Intent intent = new Intent(ArticleActivity.this, AlbumDetailsActivity.class);
                    intent.putExtra("id", articleId);
                    intent.putExtra("mid", mUserId);
                    startActivity(intent);
                }
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ArticleActivity.this, VideoDynamicActivity.class);
                intent.putExtra("huati",title_name2);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                EventBus.getDefault().post(new CloseFragmentEvent());
                getArticleByTopic();
                loadLngLatData(true, lng, lat);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (isLoad && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore) {
                    isLoadingMore = true;
                    loadLngLatData(false, lng, lat);
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

    private void getArticleByTopic() {

        HttpService.getArticleByTopic(tagid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "正在加载...");
            }

            @Override
            public void getSuccess(String successJson) {
                ResultRequestModel resultRequestModel=new ResultRequestModel(successJson);
                if(!resultRequestModel.mIsSuccess){
                    ToastUtils.showToast(resultRequestModel.mMessage);
                    dismissProgressDialog();
                    return;
                }
                Gson gson = new Gson();
                ArticleByTopic articleByTopic = gson.fromJson(successJson, ArticleByTopic.class);
                if (articleByTopic.getResult().getState() == 0) {
                    articleId = articleByTopic.getData().getArticle().getId();
                    ImgLoaderManager.getInstance().showImageView(articleByTopic.getData().getUser().getAvatar(), iv_head);
                    tv_name.setText(articleByTopic.getData().getUser().getUsername());
                     title_name = "#" + articleByTopic.getData().getTag_info().getTag_name() + "#";
                    title_name2=articleByTopic.getData().getTag_info().getTag_name();
                    mTitleTv.setText(title_name);
                    tv_tag_name.setText(title_name);
                    String str = "%s阅读· %s动态";
                    String string = String.format(str, articleByTopic.getData().getView_count(), articleByTopic.getData().getTag_count());
                    tv_count.setText(string);
                    tv_content.setText(articleByTopic.getData().getArticle().getContent());

                } else
                    ToastUtils.showToast(articleByTopic.getResult().getDescription());
                dismissProgressDialog();
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
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


        HttpService.getArticleByTag(tagid, mUserId, mPage, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.noEmpty();
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

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(ArticleActivity.this);
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

    //领取红包点赞
    public void article(String mUserId, final Moment.Moments item, final NearbyAdapter.ViewHolder viewHolder) {
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

                        Drawable drawable = getResources().getDrawable(R.drawable.icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getResources().getColor(R.color.main_blue));

                        if (result.getString("description").equals("点赞成功")) {
                            ToastUtils.showToast(result.getString("description"));
                        } else {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String bound = data.getString("bound");
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            int timeout=data.getInt("timeout");
                            FragmentManager fm = getSupportFragmentManager();
                            PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",timeout);
                            dialog.show(fm, "dialog");
                        }
                    } else if (result.getString("state").equals("-2")) {

                        showNormalDialog();
                    } else {
                        if (result.getString("description").equals("此红包已领完")) {

                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            String mAdvertising = data.getString("advertising");
                            String mAdvType = data.getString("adv_type");
                            String mAdvUrl = data.getString("adv_url");
                            FragmentManager fm = getSupportFragmentManager();
                            PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising, mAdvType, mAdvUrl);
                            dialog.show(fm, "dialog");

                            Drawable drawable = getResources().getDrawable(R.drawable.icon_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                            viewHolder.mZanTv.setTextColor(getResources().getColor(R.color.main_blue));
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

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ArticleActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请先完善您的头像和昵称资料？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        //    getHuafei();
                        Intent intent = new Intent(ArticleActivity.this, EditPersonalActivity.class);
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
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        //isZan = true;//清除数据重新加载
                        //loadData(true);
                        viewHolder.mNumTv.setVisibility(View.VISIBLE);
                        viewHolder.mNumTv.setText(item.getLaudCount() + "人点赞");

                        Drawable drawable = getResources().getDrawable(R.drawable.ic_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        viewHolder.mZanTv.setCompoundDrawables(drawable, null, null, null);
                        viewHolder.mZanTv.setTextColor(getResources().getColor(R.color.green_54));
                    } else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                        int timeout=data.getInt("timeout");
                        FragmentManager fm = getSupportFragmentManager();
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

    @Override
    public void onShareClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        mArticleId = item.getId();
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(item.getShareUrl(), item.getShareTitle(), item.getShareDesc(), item.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onItemClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(ArticleActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mUserId);
        startActivity(intent);
    }

    @Override
    public void onAvatarClick(Moment.Mine mine) {
        Intent intent = new Intent(ArticleActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mUserId);
        startActivity(intent);
    }

    @Override
    public void onItemAvatarClick(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());

        if (mUserId.equals(item.getUserId())) {
            Intent intent = new Intent(ArticleActivity.this, EditDataActivity.class);
            intent.putExtra("UserId", mUserId);
            startActivity(intent);
        } else {
            Intent intentEdit = new Intent(ArticleActivity.this, EditDataActivity.class);
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
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
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
                        loadLngLatData(true, lng, lat);
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
                        Intent intent = new Intent(ArticleActivity.this, ReportActivity.class);
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
                        loadLngLatData(true, lng, lat);
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
                        loadLngLatData(true, lng, lat);
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
    public void onImageItemClick(View view, int index) {
        Moment.Moments mItem = mDataList.get((Integer) view.getTag());
        ArrayList<String> urls = new ArrayList<>();
        for (Moment.Pictures image : mItem.getUserArticlePictures()) {
            urls.add(image.getPath());
        }
        Intent intent = new Intent(ArticleActivity.this, ImageBrowserActivity.class);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
        startActivity(intent);
    }

    @Override
    public void onPlayView(String path) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(ArticleActivity.this).externalPictureVideo(path);
    }

    @Override
    public void onAMapLocation(View view) {
        Moment.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(ArticleActivity.this, LocationActivity.class);
        intent.putExtra("lat", item.getLat());
        intent.putExtra("lng", item.getLng());
        intent.putExtra("name", item.getUser().getUsername());
        startActivity(intent);
    }

    @Override
    public void onRedClick(View view) {
        View contentView = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.pop_red_layout, null);
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(ArticleActivity.this)
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
}
