package com.android.nana.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.customer.myincome.MyIncomeStatisticsActivity;
import com.android.nana.customer.myincome.Prepaid_Activity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.FriendEvent;
import com.android.nana.find.ArticleActivity;
import com.android.nana.find.LocationActivity;
import com.android.nana.find.fragment.PointFailedFragment;
import com.android.nana.find.fragment.PointFragment;
import com.android.nana.find.http.HttpService;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.EditPersonalActivity;
import com.android.nana.material.ReportActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.nestlistview.NestFullListView;
import com.android.nana.nestlistview.NestFullListViewAdapter;
import com.android.nana.nestlistview.NestFullViewHolder;
import com.android.nana.ui.RoundImageView;
import com.android.nana.user.weight.VideoPlayerController;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.MD5;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.util.WeiBoContentTextUtil;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.FlowLayout;
import com.android.nana.widget.ImageBrowserActivity;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.NestedScrollLinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by lenovo on 2017/10/25.
 */

public class AlbumDetailsActivity extends BaseActivity implements View.OnClickListener, CommentAdapter.CommentListener, ReplyAdapter.ReplyListener {
    String type="";
    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;

    private String mStrId;//当前朋友圈ID
    private String mid;//当前朋友圈ID

    private RoundImageView mAvatarIv;
    private ImageView mDentyIv;
    private TextView mNameTv;
    private TextView mTimeTv;
    private TextView mDistanceTv;
    private ImageView mChoiceIv;
    private TextView mContentTv;

    private RelativeLayout mAvatarRl;//头像
    private RelativeLayout mZanView, mCommentView, mShareView;

    //点赞
    private TextView mZanTv;
    private TextView mCommentNumTv;
    private TextView mCollectionTv;
    private FlowLayout mAvatarll;//点赞头像

    private AlbumDetailsEntity mItem;
    private boolean isZan = false;//点赞加载数据
    private EditText mCommentEdt;
    private TextView mCommentTv;

    private RecyclerView mRecyclerViewA;
    private MultipleStatusView mMultipleStatusView;
    private CommentAdapter mAdapter;
    private boolean isRep = false;//回复评论
    private String mTouid, mToCommentid;
    private LinearLayout mZanll;
    private View imageView;

    private PercentRelativeLayout mThree2, mFour1, mFour2;
    private LinearLayout mTwoll, mThree1, mFive1, mFive2;
    private ImageView mOne1, mOne2, mTwo21, mTwo22;
    private RelativeLayout mOnePl;
    private TextView mUrlTv;
    private SubsamplingScaleImageView mLongImg;
    private ImageView mThreeIv1, mThreeIv2, mThreeIv3, mThreeIv21, mThreeIv22, mThreeIv23;
    private ImageView mFourIv1, mFourIv2, mFourIv3, mFourIv4;
    private ImageView mFourIv21, mFourIv22, mFourIv23, mFourIv24;
    private ImageView mFiveIv1, mFiveIv2, mFiveIv3, mFiveIv4, mFiveIv5;
    private ImageView mFiveIv21, mFiveIv22, mFiveIv23, mFiveIv24, mFiveIv25;
    private LinearLayout mClearll;
    private TextView mConutTv;
    private NestFullListView mListView;
    private LinearLayout mListViewll;

    private LinearLayout mRedLl;
    private TextView mPeopleNumTv;

    //视频

    private TextView mMapTv;
    private SimpleDraweeView mSimpleDraweeView;
    private CustomPopWindow mCustomPopWindow;
    //高德
    private boolean isLocation = false;//获取经纬度
    public LatLng BEIJING;//经纬度
    private double lng, lat;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private NiceVideoPlayer mVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//点击屏幕关闭软件盘
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mCommentView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetWorkUtils.isNetworkConnected(AlbumDetailsActivity.this)) {
            mMultipleStatusView.loading();
            if (!"".equals(getIntent().getStringExtra("type"))&&null!=getIntent().getStringExtra("type")){
                    type=getIntent().getStringExtra("type");
            }
            mStrId = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            initLocation();
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
                if (lng != 0.0 && lat != 0.0) {
                    locationClient.stopLocation();
                    loadLngLatData(lng, lat);
                } else {
                    locationClient.stopLocation();
                    loadLngLatData(0.0, 0.0);
                }
                BEIJING = new LatLng(lat, lng);
            }
        }
    };

    private void loadLngLatData(double lng, double lat) {

        FriendDbHelper.article(mid, mStrId, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                mMultipleStatusView.dismiss();
                if (isZan) {
                    isZan = false;
                    mAvatarll.removeAllViews();
                    mAvatarll.cancels();
                }
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        AlbumDetailsEntity entity = parseData(successJson);
                        if (null != entity) {
                            showView(entity);
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
                mMultipleStatusView.noEmpty();
            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_album_details);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mMapTv = findViewById(R.id.tv_map);
        mSimpleDraweeView = findViewById(R.id.ic_red);
        mAvatarIv = findViewById(R.id.iv_avatar);
        mDentyIv = findViewById(R.id.iv_identy);
        mNameTv = findViewById(R.id.tv_user_name);
        mTimeTv = findViewById(R.id.tv_moment_time);
        mDistanceTv = findViewById(R.id.tv_distance);
        mChoiceIv = findViewById(R.id.iv_choice);
        mContentTv = findViewById(R.id.tv_content);

        mZanTv = findViewById(R.id.tv_zan);
        mCommentNumTv = findViewById(R.id.tv_comment_num);
        mCollectionTv = findViewById(R.id.tv_collection);

        mZanView = findViewById(R.id.view_zan);
        mAvatarll = findViewById(R.id.ll_head);
        mShareView = findViewById(R.id.view_share);
        mAvatarRl = findViewById(R.id.rl_avatar);
        mCommentView = findViewById(R.id.view_comment);

        mCommentTv = findViewById(R.id.popup_comment_send_tv);
        mCommentEdt = findViewById(R.id.popup_comment_edt);
        imageView = LayoutInflater.from(AlbumDetailsActivity.this).inflate(R.layout.item_img, null);

        mCommentEdt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() == 0) {
                    mCommentTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_album_text));
                    mCommentTv.setTextColor(getResources().getColor(R.color.green_99));
                } else {
                    mCommentTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_album_choice_text));
                    mCommentTv.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mRecyclerViewA = findViewById(R.id.recycler_view_a);
        NestedScrollLinearLayoutManager layoutManager = new NestedScrollLinearLayoutManager(AlbumDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewA.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerViewA.addItemDecoration(new ListViewDecoration());

        mZanll = findViewById(R.id.ll_zan);
        mListViewll = findViewById(R.id.ll_listview);
        mListView = findViewById(R.id.list_view);

        mRedLl = findViewById(R.id.ll_red);
        mPeopleNumTv = findViewById(R.id.tv_people_num);


    }

    @Override
    protected void init() {

        mBackTv.setVisibility(View.VISIBLE);

        mAction2.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_check);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(this);
    }


    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAction2.setOnClickListener(this);
        mChoiceIv.setOnClickListener(this);
        mZanView.setOnClickListener(this);
        mCommentView.setOnClickListener(this);

        mShareView.setOnClickListener(this);
        mAvatarRl.setOnClickListener(this);
        mCommentTv.setOnClickListener(this);
        mZanll.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                if (Utils.isFastClick()) {
                    onAction2Click();
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
                break;
            case R.id.iv_choice://收藏
                if (Utils.isFastClick()) {
                    onCollection();
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
                break;
            case R.id.view_zan://点赞
                if (Utils.isFastClick()) {
                    onEventZan();
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
                break;
            case R.id.view_comment:
                if (Utils.isFastClick()) {
                    mCommentEdt.requestFocus();
                    mCommentEdt.setText("");
                    mCommentEdt.setHint("评论...");
                    isRep = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
                break;
            case R.id.view_share:
                share();
                break;
            case R.id.rl_avatar:
                if (mid.equals(mItem.getUserId())) {
                    Intent intent = new Intent(AlbumDetailsActivity.this, EditDataActivity.class);
                    intent.putExtra("UserId", mid);
                    startActivity(intent);
                } else {
                    Intent intentEdit = new Intent(AlbumDetailsActivity.this, EditDataActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UserId", mItem.getUserId());
                    intentEdit.putExtras(bundle);
                    startActivity(intentEdit);
                }
                break;
            case R.id.popup_comment_send_tv://评论
                if (Utils.isFastClick()) {
                    onComment();
                } else {
                    ToastUtils.showToast("操作太频繁，请稍后再试！");
                }
                break;
            case R.id.ll_zan://点赞列表
                Intent intent = new Intent(AlbumDetailsActivity.this, SpotZanListActivity.class);
                intent.putExtra("id", mStrId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void onComment() {//评论

        if (TextUtils.isEmpty(mCommentEdt.getText().toString())) {
            ToastUtils.showToast("评论内容不能为空");
            return;
        }

        if (isRep) {
            FriendDbHelper.addComment(mid, mStrId, mTouid, mToCommentid, mCommentEdt.getText().toString().trim(), new IOAuthCallBack() {
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
                            ToastUtils.showToast("评论成功");
                            mCommentEdt.setText("");
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                    .hideSoftInputFromWindow(mCommentView.getWindowToken(),
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                            loadData(mid, mStrId);
                        }
                        else if (result.getString("state").equals("-2")){
                            showNormalDialog();
                        }
                        else {
                           ToastUtils.showToast(result.getString("description")+"");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {
                    ToastUtils.showToast("服务器数据返回错误");
                }
            });

        } else {
            FriendDbHelper.addComment(mid, mStrId, mItem.getUserId(), "0", mCommentEdt.getText().toString(), new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ToastUtils.showToast("评论成功");
                            isZan = true;
                            mCommentEdt.setText("");
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                    .hideSoftInputFromWindow(mCommentView.getWindowToken(),
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                            loadData(mid, mStrId);
                        }
                        else if (result.getString("state").equals("-2")){
                            showNormalDialog();
                        }
                        else {
                            ToastUtils.showToast(result.getString("description")+"");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {
                    ToastUtils.showToast("服务器数据返回错误");
                }
            });
        }
    }

    private void share() {//分享
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(mItem.getShareUrl(), mItem.getShareTitle(), mItem.getShareDesc(), mItem.getShareLogo());
        dialog.show(fm, "fragment_bottom_dialog");
    }

    private void onEventZan() {//点赞
        if (mItem.getLaudResult().equals("1")) {//取消点赞
            cancelGood(mItem);
        } else {
            String time = Utils.getTime();
            String secret = mid + "&" + mStrId + "&" + time + "&" + "89aaa16a9dcb8e38e8c5a2d0b5d221d2" + "&" + mItem.getShowBound();
            String sign = MD5.MD5Hash(secret);
            HttpService.laudUserArticle(mid, mStrId, time, sign, mItem.getShowBound(), new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            isZan = true;//清除数据重新加载
                            loadData(mid, mStrId);

                            if (result.getString("description").equals("点赞成功")) {
                                ToastUtils.showToast(result.getString("description"));
                            } else {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                String bound = data.getString("bound");
                                String mAdvertising = data.getString("advertising");
                                String mAdvType = data.getString("adv_type");
                                String mAdvUrl = data.getString("adv_url");
                                int timeout=data.getInt("timeout");
                                FragmentManager fm = AlbumDetailsActivity.this.getSupportFragmentManager();
                                PointFragment dialog = PointFragment.newInstance(bound,mAdvertising,mAdvType,mAdvUrl,"1",timeout);
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
                                FragmentManager fm = AlbumDetailsActivity.this.getSupportFragmentManager();
                                PointFailedFragment dialog = PointFailedFragment.newInstance(mAdvertising,mAdvType,mAdvUrl);
                                dialog.show(fm, "dialog");
                                isZan = true;//清除数据重新加载
                                loadData(mid, mStrId);
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
    }


    private void cancelGood(AlbumDetailsEntity item) {//取消点赞
        CustomerDbHelper.cancelLaudUserArticle(mid, item.getId(), new IOAuthCallBack() {
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
                        FragmentManager fm = getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound, mAdvertising, mAdvType, mAdvUrl, "1",0);
                        dialog.show(fm, "dialog");

                    }

                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast(result.getString("description"));
                        isZan = true;//清除数据重新加载
                        loadData(mid, mStrId);
                    }else {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        String bound = data.getString("bound");
                        String mAdvertising = data.getString("advertising");
                        String mAdvType = data.getString("adv_type");
                        String mAdvUrl = data.getString("adv_url");
                   //     int timeout=data.getInt("timeout");

                        FragmentManager fm = AlbumDetailsActivity.this.getSupportFragmentManager();
                        PointFragment dialog = PointFragment.newInstance(bound,mAdvertising,mAdvType,mAdvUrl,"-1",0);
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

    private void onCollection() {//收藏
        if (mItem.getCollectioned().equals("1")) {
            cancelCollection();
        } else {
            collection();
        }
    }

    private void collection() {//收藏

        CustomerDbHelper.collectionUserArticle(mid, mStrId, new IOAuthCallBack() {
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
                        loadData(mid, mStrId);
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

    private void cancelCollection() {//取消收藏
        CustomerDbHelper.cancelCollectionUserArticle(mid, mStrId, new IOAuthCallBack() {
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
                        loadData(mid, mStrId);
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

    private void onAction2Click() {
        bottomMenu();
    }


    private void loadData(String mid, String id) {

        FriendDbHelper.article(mid, id, lng, lat, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                //  mMultipleStatusView.dismiss();
                dismissProgressDialog();
                if (isZan) {
                    isZan = false;
                    mAvatarll.removeAllViews();
                    mAvatarll.cancels();
                }
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        AlbumDetailsEntity entity = parseData(successJson);
                        if (null != entity) {
                            showView(entity);
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

    private void showView(final AlbumDetailsEntity item) {//显示view
        mTitleTv.setText("动态详情");
        mItem = item;
        if (type.equals("1")){
            mTimeTv.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 0, 0);
            mDistanceTv.setLayoutParams(lp);
        }
        else {
            mTimeTv.setVisibility(View.GONE);
        }
        mTimeTv.setText(item.getTime());
        mDistanceTv.setText(item.getJuli());
        if (null != item.getAddress()&& !"".equals(item.getAddress())){
            mMapTv.setText(item.getAddress());
        }else {
            mMapTv.setVisibility(View.GONE);
        }
        if (null==item.getUser()){
            return;
        }
        if (null != item.getUser().getUname() && !"".equals(item.getUser().getUname())) {
            mNameTv.setText(item.getUser().getUname());
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.icon_gif))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        mSimpleDraweeView.setController(controller);

        mMapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumDetailsActivity.this, LocationActivity.class);
                intent.putExtra("lat", item.getLat());
                intent.putExtra("lng", item.getLng());
                intent.putExtra("name", item.getUser().getUsername());
                startActivity(intent);
            }
        });
      /*  mViewNumTv.setText(item.getView_count() + "次浏览");
        if (item.getUser().getStatus().equals("1")) {
            mDentyIv.setVisibility(View.VISIBLE);
        }*/

        if (item.getLaudResult().equals("0")) {
            mPeopleNumTv.setText("点赞得现金红包");
        } else {
            mPeopleNumTv.setText(item.getBoundCount() + "人领取");
        }

        if (item.getIsReceived().equals("1")){
            mPeopleNumTv.setText("红包已领完");
        }

        if (item.getShowBound().equals("1")) {
            mRedLl.setVisibility(View.VISIBLE);
            mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View contentView = LayoutInflater.from(AlbumDetailsActivity.this).inflate(R.layout.pop_red_layout, null);

                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(AlbumDetailsActivity.this)
                            .setView(contentView)
                            .setFocusable(true)
                            .setBgDarkAlpha(0.7f)
                            .setOutsideTouchable(true)
                            .create();
                    mCustomPopWindow.showAsDropDown(v, -150, 0);

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
            });
        } else {
            mRedLl.setVisibility(GONE);
        }


        if (item.getCollectioned().equals("1")) {
            mChoiceIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_collection));
        } else {
            mChoiceIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_collection));
        }

        if (item.getLaudResult().equals("1")) {
            mZanTv.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_friend_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mZanTv.setCompoundDrawables(drawable, null, null, null);

            Drawable drawable1 = getResources().getDrawable(R.drawable.icon_zan);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            mCollectionTv.setCompoundDrawables(drawable1, null, null, null);

            mCollectionTv.setTextColor(getResources().getColor(R.color.main_blue));
        } else {
            Drawable drawable1 = getResources().getDrawable(R.drawable.ic_zan);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            mCollectionTv.setCompoundDrawables(drawable1, null, null, null);
            mCollectionTv.setTextColor(getResources().getColor(R.color.green_54));

            Drawable drawable = getResources().getDrawable(R.drawable.ic_friend_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mZanTv.setCompoundDrawables(drawable, null, null, null);
        }

        if (Integer.valueOf(item.getLaudCount()) > 0) {//点钟数
            mZanTv.setText(item.getLaudCount() + "人点赞");
        } else {
            mZanTv.setText("0人点赞");
        }

        mCommentNumTv.setVisibility(View.VISIBLE);
        mCommentNumTv.setText(item.getComment_count() + "条评论·" + item.getRepost_count() + "次分享·" + item.getView_count() + "次浏览");
//        mContentTv.setText(item.getContent());
        mContentTv.setText(WeiBoContentTextUtil.getWeiBoContent(item.getContent(), AlbumDetailsActivity.this, mContentTv, new WeiBoContentTextUtil.OnClistener() {
            @Override
            public void toUserData(String name) {
                for (AlbumDetailsEntity.UserListBean userID : item.getUser_list()) {
                    if (name.equals(userID.getUname())) {

                        Intent intent = new Intent(AlbumDetailsActivity.this, EditDataActivity.class);
                        intent.putExtra("UserId", userID.getTouid());
                        startActivity(intent);

                        return;
                    }
                }

            }

            @Override
            public void toArticle(String name) {
                for (AlbumDetailsEntity.Article article : item.getTag_list()) {
                    if (name.equals(article.getTag_name())) {
                        Intent intent = new Intent(AlbumDetailsActivity.this, ArticleActivity.class);
                        intent.putExtra("tagid", article.getTag_id());
                        startActivity(intent);
                        return;
                    }
                }
            }
        }));
        if (item.getLaudAvatar().size() > 0 && item.getLaudAvatar().size() < 4) {
            mAvatarll.setFlag(true);
            mAvatarll.setUrls(item.getLaudAvatar());
        }

        /**
         * 评论内容
         */
        if (mItem.getComments().size() > 0) {
            mMultipleStatusView.dismiss();
            mAdapter = new CommentAdapter(AlbumDetailsActivity.this, mItem.getComments(), AlbumDetailsActivity.this, AlbumDetailsActivity.this);
            mRecyclerViewA.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            mMultipleStatusView.noComment();
        }

        ImgLoaderManager.getInstance().showImageView(item.getUser().getAvatar(), mAvatarIv);

        if (mItem.getUserArticlePictures().size() > 0) {
            final ArrayList<AlbumDetailsEntity.Pictures> list = mItem.getUserArticlePictures();
            mListViewll.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);

            if (mItem.getType().equals("2")) {
                mListView.setAdapter(new NestFullListViewAdapter<AlbumDetailsEntity.Pictures>(R.layout.item_video, list) {
                    @Override
                    public void onBind(final int pos, AlbumDetailsEntity.Pictures pictures, NestFullViewHolder holder) {

                        mVideoPlayer = holder.getView(R.id.video_player);
                        final VideoPlayerController controller = new VideoPlayerController(AlbumDetailsActivity.this);
                        mVideoPlayer.setController(controller);
                        WindowManager manager = (WindowManager) AlbumDetailsActivity.this.getSystemService(Context.WINDOW_SERVICE);
                        double width = manager.getDefaultDisplay().getWidth();
                        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();

                        TextView mPlayerUrlTv = holder.getView(R.id.tv_player_url);
                        if (null != item.getSpread_url() && !"".equals(item.getSpread_url   ())) {
                            mPlayerUrlTv.setVisibility(View.VISIBLE);

                            if (item.getSpread_url_type().equals("1")) {
                                mPlayerUrlTv.setText("点击立即下载");
                                mPlayerUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.c2f0));
                            } else if (item.getSpread_url_type().equals("2")) {
                                mPlayerUrlTv.setText("点击了解详情");
                                mPlayerUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ffb));
                            } else if (item.getSpread_url_type().equals("3")) {
                                mPlayerUrlTv.setText("点击立即购买");
                                mPlayerUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ff8080));
                            }else if (item.getSpread_url_type().equals("4")){
                                mPlayerUrlTv.setText("点击阅读文章");
                                mPlayerUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ffc));
                            }

                            mPlayerUrlTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(AlbumDetailsActivity.this, CommonActivity.class);
                                    intent.putExtra("title", item.getSpread_url());
                                    intent.putExtra("url", item.getSpread_url());
                                    AlbumDetailsActivity.this.startActivity(intent);
                                }
                            });
                        }

                        if (Integer.valueOf(pictures.getHeight()) > Integer.valueOf(pictures.getWidth())) { //高视频
                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
                                double total = Double.valueOf(width) / 375.00;
                                double videoWidth = 237 * Double.valueOf(total);
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
                            } else {
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
                            }
                        } else if (Integer.valueOf(pictures.getWidth()) > Integer.valueOf(pictures.getHeight())) {
                            if (Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent()))) >= 500) {
                                params.width = (int) width;
                                params.height = (int) 600.0;
                            } else {
                                params.width = (int) width;
                                params.height = Integer.valueOf((int) (width / Double.valueOf(pictures.getPercent())));
                            }
                        }else {
                            double total = Double.valueOf(width) / 375.00;
                            double videoWidth = 237 * Double.valueOf(total);
                            params.width = (int) width;
                            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(pictures.getPercent())));
                        }

                        mVideoPlayer.setLayoutParams(params);

                        Glide.with(AlbumDetailsActivity.this).load(pictures.getPicture_url()).into(controller.imageView());

                        mVideoPlayer.continueFromLastPosition(false);
                        mVideoPlayer.getTcpSpeed();
                        mVideoPlayer.setUp(pictures.getPath(), null);
                        mVideoPlayer.start();


                        controller.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Drawable.ConstantState drawableCs = controller.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                                if (controller.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                                    mVideoPlayer.setVolume(0);
                                    controller.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                                } else {
                                    mVideoPlayer.setVolume(100);
                                    controller.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                                }
                            }
                        });

                        controller.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                                PictureSelector.create(AlbumDetailsActivity.this).externalPictureVideo(list.get(pos).getPath());
                            }
                        });
                    }
                });

            } else {
                if (null != mVideoPlayer) {
                    mVideoPlayer.release();
                }
                mListView.setAdapter(new NestFullListViewAdapter<AlbumDetailsEntity.Pictures>(R.layout.item_img, list) {
                    @Override
                    public void onBind(int pos, AlbumDetailsEntity.Pictures pictures, NestFullViewHolder holder) {


                        mOnePl = holder.getView(R.id.prl_one);
                        mOne1 = holder.getView(R.id.iv_img_one1);
                        mOne2 = holder.getView(R.id.iv_img_one2);
                        mLongImg = holder.getView(R.id.longImg);
                        mUrlTv = holder.getView(R.id.tv_url);


                        mTwoll = holder.getView(R.id.ll_two);
                        mTwo21 = holder.getView(R.id.iv_img_two21);
                        mTwo22 = holder.getView(R.id.iv_img_two22);

                        mThree1 = holder.getView(R.id.ll_three);
                        mThreeIv1 = holder.getView(R.id.iv_img_three1);
                        mThreeIv2 = holder.getView(R.id.iv_img_three2);
                        mThreeIv3 = holder.getView(R.id.iv_img_three3);

                        mThree2 = holder.getView(R.id.prl_three2);
                        mThreeIv21 = holder.getView(R.id.iv_img_three21);
                        mThreeIv22 = holder.getView(R.id.iv_img_three22);
                        mThreeIv23 = holder.getView(R.id.iv_img_three23);

                        mFour1 = holder.getView(R.id.prl_four);
                        mFourIv1 = holder.getView(R.id.iv_img_four1);
                        mFourIv2 = holder.getView(R.id.iv_img_four2);
                        mFourIv3 = holder.getView(R.id.iv_img_four3);
                        mFourIv4 = holder.getView(R.id.iv_img_four4);

                        mFour2 = holder.getView(R.id.prl_four2);
                        mFourIv21 = holder.getView(R.id.iv_img_four21);
                        mFourIv22 = holder.getView(R.id.iv_img_four22);
                        mFourIv23 = holder.getView(R.id.iv_img_four23);
                        mFourIv24 = holder.getView(R.id.iv_img_four24);

                        mFive1 = holder.getView(R.id.prl_five);
                        mFiveIv1 = holder.getView(R.id.iv_img_five);
                        mFiveIv2 = holder.getView(R.id.iv_img_five2);
                        mFiveIv3 = holder.getView(R.id.iv_img_five3);
                        mFiveIv4 = holder.getView(R.id.iv_img_five4);
                        mFiveIv5 = holder.getView(R.id.iv_img_five5);

                        mFive2 = holder.getView(R.id.prl_five1);
                        mFiveIv21 = holder.getView(R.id.iv_img_five21);
                        mFiveIv22 = holder.getView(R.id.iv_img_five22);
                        mFiveIv23 = holder.getView(R.id.iv_img_five23);
                        mFiveIv24 = holder.getView(R.id.iv_img_five24);
                        mFiveIv25 = holder.getView(R.id.iv_img_five25);
                        mClearll = holder.getView(R.id.ll_clear);
                        mConutTv = holder.getView(R.id.tv_count_img);

                        if (list.size() == 1) {
                            mOnePl.setVisibility(View.VISIBLE);
                            if (null != item.getSpread_url() && !"".equals(item.getSpread_url())) {
                                mUrlTv.setVisibility(View.VISIBLE);

                                if (item.getSpread_url_type().equals("1")) {
                                    mUrlTv.setText("点击立即下载");
                                    mUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.c2f0));
                                } else if (item.getSpread_url_type().equals("2")) {
                                    mUrlTv.setText("点击了解详情");
                                    mUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ffb));
                                } else if (item.getSpread_url_type().equals("3")) {
                                    mUrlTv.setText("点击立即购买");
                                    mUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ff8080));
                                }else if (item.getSpread_url_type().equals("4")){
                                    mUrlTv.setText("点击阅读文章");
                                    mUrlTv.setBackgroundColor(AlbumDetailsActivity.this.getResources().getColor(R.color.ffc));
                                }


                                mUrlTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(AlbumDetailsActivity.this, CommonActivity.class);
                                        intent.putExtra("title", item.getSpread_url());
                                        intent.putExtra("url", item.getSpread_url());
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            }
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mOne1.setVisibility(View.VISIBLE);
                                mOne2.setVisibility(GONE);

                                if (Integer.valueOf(list.get(0).getHeight()) >= 500) {
                                    mLongImg.setVisibility(View.VISIBLE);
                                    mOne1.setVisibility(GONE);
                                   Glide.with(AlbumDetailsActivity.this)
                                            .download(list.get(0).getPath())
                                            .into(new SimpleTarget<File>() {
                                                @Override
                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                    super.onLoadFailed(errorDrawable);

                                                }

                                                @Override
                                                public void onResourceReady(File resource, Transition<? super File> transition) {
                                                    mLongImg.setQuickScaleEnabled(true);
                                                    mLongImg.setZoomEnabled(true);
                                                    mLongImg.setPanEnabled(true);
                                                    mLongImg.setDoubleTapZoomDuration(500);
                                                    mLongImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                                    mLongImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                                    mLongImg.setImage(ImageSource.uri(resource.getAbsolutePath()), new ImageViewState(0, new PointF(0, 0), 0));
                                                }
                                            });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne1);
                                }

                                mLongImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_LONG_IMG_URLS, "111");
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });

                                mOne1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else {
                                mOne1.setVisibility(GONE);
                                mOne2.setVisibility(View.VISIBLE);
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mOne2);

                                mOne2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            }

                        } else if (list.size() == 2) {

                            mTwoll.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mTwo21);
                                mTwo21.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mTwo22);
                                mTwo22.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        } else if (list.size() == 3) {
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mThree2.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv21);
                                    mThreeIv21.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv22);

                                    mThreeIv22.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv23);

                                    mThreeIv23.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mThree1.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mThreeIv1);

                                    mThreeIv1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mThreeIv2);

                                    mThreeIv2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mThreeIv3);
                                    mThreeIv3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } else if (list.size() == 4) {
                            if (Integer.valueOf(list.get(0).getHeight()) > Integer.valueOf(list.get(0).getWidth())) {
                                mFour1.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv1);

                                    mFourIv1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv2);
                                    mFourIv2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 2) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv3);

                                    mFourIv3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 3) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv4);

                                    mFourIv4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mFour2.setVisibility(View.VISIBLE);
                                if (pos == 0) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFourIv21);

                                    mFourIv21.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 1) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFourIv22);

                                    mFourIv22.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 2) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFourIv23);

                                    mFourIv23.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                } else if (pos == 3) {
                                    ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFourIv24);

                                    mFourIv24.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<String> urls = new ArrayList<>();
                                            for (AlbumDetailsEntity.Pictures image : list) {
                                                urls.add(image.getPath());
                                            }
                                            Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                            intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                            AlbumDetailsActivity.this.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } else if (list.size() == 5) {
                            mFive1.setVisibility(View.VISIBLE);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(0).getPath(), mFiveIv1);

                                mFiveIv1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(1).getPath(), mFiveIv2);

                                mFiveIv2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(2).getPath(), mFiveIv3);

                                mFiveIv3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(3).getPath(), mFiveIv4);

                                mFiveIv4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 4) {
                                ImgLoaderManager.getInstance().showImageView(list.get(4).getPath(), mFiveIv5);

                                mFiveIv5.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 4);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            mFive2.setVisibility(View.VISIBLE);
                            mClearll.getBackground().setAlpha(90);
                            if (pos == 0) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv21);

                                mFiveIv21.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 0);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 1) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv22);

                                mFiveIv22.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 1);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 2) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv23);

                                mFiveIv23.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 2);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 3) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv24);

                                mFiveIv24.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 3);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            } else if (pos == 4) {
                                ImgLoaderManager.getInstance().showImageView(list.get(pos).getPath(), mFiveIv25);

                                mFiveIv25.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> urls = new ArrayList<>();
                                        for (AlbumDetailsEntity.Pictures image : list) {
                                            urls.add(image.getPath());
                                        }
                                        Intent intent = new Intent(AlbumDetailsActivity.this, ImageBrowserActivity.class);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImageBrowserActivity.EXTRA_IMAGE_INDEX, 4);
                                        AlbumDetailsActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        }
                        mConutTv.setText("+" + String.valueOf(Integer.valueOf(list.size()) - 5));
                    }
                });
            }
        }
    }

    public AlbumDetailsEntity parseData(String result) {
        AlbumDetailsEntity album = new AlbumDetailsEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            album = gson.fromJson(data.toString(), AlbumDetailsEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return album;
    }

    private void bottomMenu() {
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
        menuItem2.setText("举报");

        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                if (mItem.getCollectioned().equals("1")) {
                    cancelCollection();//取消收藏
                } else {
                    collection();//收藏
                }
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                onReport();
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void onReport() {//举报
        FriendDbHelper.reportArticle(mid, mStrId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        Intent intent = new Intent(AlbumDetailsActivity.this, ReportActivity.class);
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

            }
        });
    }

    @Override
    public void onItemClick(AlbumDetailsEntity.Comments item) {
        if (item.getUid().equals(mid)) {
            bottomCommentMenu(item);
        } else if (mItem.getUserId().equals(mid) && !item.getUid().equals(mid)) {
            bottomReplyMenu(item);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            mCommentEdt.setHint("回复" + item.getUname() + ":");
            mCommentEdt.requestFocus();
            isRep = true;
            mTouid = item.getUid();
            mToCommentid = item.getComment_id();
        }
    }

    @Override
    public void onAvatarClick(AlbumDetailsEntity.Comments item) {//点击头像
        Intent intentEdit = new Intent(AlbumDetailsActivity.this, EditDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserId", item.getUid());
        intentEdit.putExtras(bundle);
        startActivity(intentEdit);
    }


    private void bottomReplyMenu(final AlbumDetailsEntity.Comments item) {

        final BottomMenuFragment bottomRepMenuFragment = new BottomMenuFragment("0", true);
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menu1 = new MenuItem();
        menu1.setText(item.getUname() + ":" + item.getContent());
        menu1.setStyle(MenuItem.MenuItemStyle.COMMON);

        menu1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                bottomRepMenuFragment.dismiss();
            }
        });

        MenuItem menu2 = new MenuItem();
        menu2.setText("回复");
        menu2.setStyle(MenuItem.MenuItemStyle.COMMON);
        menu2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                mCommentEdt.setHint("回复" + item.getUname() + ":");
                mCommentEdt.requestFocus();
                isRep = true;
                mTouid = item.getUid();
                mToCommentid = item.getComment_id();
            }
        });


        MenuItem menu3 = new MenuItem();
        menu3.setText("删除");
        menu3.setStyle(MenuItem.MenuItemStyle.STRESS);

        menu3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu3) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                deleteComment(item);
            }
        });

        menuItemList.add(menu1);
        menuItemList.add(menu2);
        menuItemList.add(menu3);
        bottomRepMenuFragment.setMenuItems(menuItemList);
        bottomRepMenuFragment.show(getFragmentManager(), "BottomMenuReplytFragment");

    }

    private void bottomCommentMenu(final AlbumDetailsEntity.Comments item) {
        final BottomMenuFragment bottomMenuFragment = new BottomMenuFragment(true);
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("删除评论后，评论下所有回复都会被删除");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("删除评论");
        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                bottomMenuFragment.dismiss();
            }
        });


        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                deleteComment(item);
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuCommentFragment");
    }

    private void deleteComment(AlbumDetailsEntity.Comments item) {//删除评论
        FriendDbHelper.delComment(mid, item.getComment_id(), new IOAuthCallBack() {
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
                        loadData(mid, mStrId);
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

    @Override
    public void onContentClick(AlbumDetailsEntity.Child item) {
        if (item.getUid().equals(mid)) {
            bottomReplyLevelMenu(item);
        } else if (mItem.getUserId().equals(mid) && !item.getUid().equals(mid)) {
            bottomReplylevelMenu(item);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            mCommentEdt.setHint("回复" + item.getUname() + ":");
            mCommentEdt.requestFocus();
            isRep = true;
            mTouid = item.getUid();
            mToCommentid = item.getComment_id();
        }
    }

    @Override
    public void onReplyAvatarClick(AlbumDetailsEntity.Child item) {//点击回复头像
        Intent intentEdit = new Intent(AlbumDetailsActivity.this, EditDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("UserId", item.getUid());
        intentEdit.putExtras(bundle);
        startActivity(intentEdit);
    }

    private void bottomReplyLevelMenu(final AlbumDetailsEntity.Child item) {//二级评论
        final BottomMenuFragment bottomMenuFragment = new BottomMenuFragment(true);
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("删除评论后，评论下所有回复都会被删除");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("删除评论");
        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                bottomMenuFragment.dismiss();
            }
        });


        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {

                deleteReply(item);
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuCommentFragment");
    }

    private void deleteReply(AlbumDetailsEntity.Child item) {//删除回复评论
        FriendDbHelper.delComment(mid, item.getComment_id(), new IOAuthCallBack() {
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
                        loadData(mid, mStrId);
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


    private void bottomReplylevelMenu(final AlbumDetailsEntity.Child item) {//二级回复

        final BottomMenuFragment bottomRepMenuFragment = new BottomMenuFragment("0", true);
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menu1 = new MenuItem();
        menu1.setText(item.getUname() + ":" + item.getContent());
        menu1.setStyle(MenuItem.MenuItemStyle.COMMON);

        menu1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                bottomRepMenuFragment.dismiss();
            }
        });

        MenuItem menu2 = new MenuItem();
        menu2.setText("回复");
        menu2.setStyle(MenuItem.MenuItemStyle.COMMON);


        menu2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                mCommentEdt.setHint("回复" + item.getUname() + ":");
                mCommentEdt.requestFocus();
                isRep = true;
                mTouid = item.getUid();
                mToCommentid = item.getComment_id();
            }
        });


        MenuItem menu3 = new MenuItem();
        menu3.setText("删除");
        menu3.setStyle(MenuItem.MenuItemStyle.STRESS);

        menu3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomRepMenuFragment, menu3) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                deleteReply(item);
            }
        });

        menuItemList.add(menu1);
        menuItemList.add(menu2);
        menuItemList.add(menu3);
        bottomRepMenuFragment.setMenuItems(menuItemList);
        bottomRepMenuFragment.show(getFragmentManager(), "BottomMenuReplytFragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //更新朋友圈列表数据
        EventBus.getDefault().post(new FriendEvent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mVideoPlayer) {
            mVideoPlayer.releasePlayer();
        }
    }


    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(AlbumDetailsActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("请先完善您的头像和昵称资料？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    //    getHuafei();
                        Intent intent=new Intent(AlbumDetailsActivity.this,EditPersonalActivity.class);
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
