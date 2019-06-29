package com.android.nana.main;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.common.builder.FragmentBuilder;
import com.android.common.helper.UIHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.WebCastApplication;
import com.android.nana.auth.LoginActivity;
import com.android.nana.bean.CheckPermissionsActivity;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.bean.GroupEntity;
import com.android.nana.bean.MessageEntity;
import com.android.nana.connect.Constants;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.dbhelper.LoginDbHelper;
import com.android.nana.eventBus.AddFriendEvent;
import com.android.nana.eventBus.FollowEvent;
import com.android.nana.eventBus.FriendsEvent;
import com.android.nana.eventBus.PushExtraEvent;
import com.android.nana.listener.LoginListener;
import com.android.nana.mail.NewFriendActivity;
import com.android.nana.user.ForwardActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ShortCutUtil;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.BadgeRadioButton;
import com.arialyy.aria.core.Aria;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.DbException;

import com.umeng.analytics.MobclickAgent;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.NotificationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.VoiceMessage;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.umeng.socialize.utils.DeviceConfig.context;


public class MainActivity extends CheckPermissionsActivity implements IUnReadMessageObserver, RongIM.UserInfoProvider, RongIM.GroupInfoProvider {

    private RadioGroup mTabRg;
    private FragmentBuilder mFragmentBuilder;
    private long firstTime = 0;
    private int num = 0;//记录谁要见我，我要见谁角标
    private int count = 0;//记录聊天数


    private String loginToken;
    private static final int LOGIN = 5;//首次登录
    private static final int GET_TOKEN = 6;//重新获取token
    private String userid;
    private BadgeRadioButton mBrbMain2;
    private BadgeRadioButton mBrbMain1;
    private BadgeRadioButton mBrbMain3;
    private BadgeRadioButton mBrbMain4;
    private com.android.nana.bean.UserInfo mUserInfo;
    private com.android.nana.find.FindFragment mFindFragment = new com.android.nana.find.FindFragment();
    private MsgFragment mMsgFragment = new MsgFragment();
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();
    private boolean isAction = false;
    private MessageItemLongClickAction action;
    private Notification notification = null;

    //高德
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private double lng;
    private double lat;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MainActivity.this)) {
            EventBus.getDefault().register(MainActivity.this);
        }

        setMessageItemLongClickAction();//转发功能
        mUserInfo = (com.android.nana.bean.UserInfo) SharedPreferencesUtils.getObject(MainActivity.this, "userInfo", com.android.nana.bean.UserInfo.class);
        loadData();
        Aria.download(this).register();
        download();
        getQinNiuYunUrl();
    }

    private void getQinNiuYunUrl() {
        LoginDbHelper.getQiNiuYunUrl(new IOAuthCallBack() {
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
                        Constants.HOST_URL = data.getString("qiniu_url");
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

    private void download() {
        Aria.download(this)
                .load(com.android.nana.connect.Constants.HOST_URL + "20181024151429.wav")     //读取下载地址
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/msc/20181024151429.wav") //设置文件保存的完整路径
                .start();   //启动下载
    }


    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
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
                address = location.getAddress();//地址
                SharedPreferencesUtils.setParameter(MainActivity.this, "lng", lng);
                SharedPreferencesUtils.setParameter(MainActivity.this, "lat", lat);
                if (lng > 0.0 && lat > 0.0) {
                    locationClient.stopLocation();
                    updateLocation();
                }
                else {
                    locationClient.stopLocation();
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){//未开启定位权限
                        //开启定位权限,200是标识码
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
                    }else{
                        //  initLocation();//开始定位

                        //    Toast.makeText(getActivity(),"已开启定位权限",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };


    @Override
    protected void bindViews() {
        try {
            WebCastApplication.getInstance().mDbUtils.deleteAll(MessageEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        ShortCutUtil.addShortcutByPackageName(this, "com.android.nana");
        setContentView(R.layout.activity_pager);
    }


    @Override
    protected void initFragments() {
        mFragmentBuilder = new FragmentBuilder(this, R.id.main_tab_content);
        mFragmentBuilder.registerFragement("哪哪", mMsgFragment);
        mFragmentBuilder.registerFragement("通讯录", new MailFragment());
        mFragmentBuilder.registerFragement("发现", mFindFragment);
        mFragmentBuilder.registerFragement("我", new MeFragment());

        if (null != getIntent().getStringExtra("name")) {
            mFragmentBuilder.switchFragment(2);
            mBrbMain3.setChecked(true);
        } else {
            mFragmentBuilder.switchFragment(0);
        }
    }

    @Override
    protected void locationData() {

        LoginListener.getInstance().mOnMainListener = new LoginListener.OnMainListener() {
            @Override
            public void result() {
                init();
            }
        };
    }


    @Override
    protected void findViewById() {
        mTabRg = findViewById(R.id.main_tab_rg_menu);
        mBrbMain2 = findViewById(R.id.main_tab_rb_2);
        mBrbMain1 = findViewById(R.id.main_tab_rb_1);
        mBrbMain3 = findViewById(R.id.main_tab_rb_3);
        mBrbMain4 = findViewById(R.id.main_tab_rb_4);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("mUid")) {
            userid = getIntent().getStringExtra("mUid");
            SharedPreferencesUtils.setParameter(this, "userId", userid);
        } else {
            userid = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        }
        if (!TextUtils.isEmpty(userid)) {
            loginRongIM(LOGIN, userid, "1");//登录融云im
        }

    }


    private void initData() {
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };
        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }


    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");

            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {
                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            startActivity(new Intent(MainActivity.this, NewFriendActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }


    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {

                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {

                            }

                            @Override
                            public void onSuccess(String s) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    protected void setListener() {

        mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab_rb_1:
                        mFragmentBuilder.switchFragment(0);
                        break;
                    case R.id.main_tab_rb_2:
                        mFragmentBuilder.switchFragment(1);
                        break;
                    case R.id.main_tab_rb_3:
                        //更新发现消息
                        mFragmentBuilder.switchFragment(2);
                        break;
                    case R.id.main_tab_rb_4:
                        mFragmentBuilder.switchFragment(3);
                        break;
                    default:
                        break;
                }
            }
        });
        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupInfoProvider(this, true);
    }


    private void loginRongIM(int requestCode, String mid, String type) {//登录融云
        switch (requestCode) {
            case LOGIN:
                LoginDbHelper.getRcToken(mid, type, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {
                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {
                                loginToken = jsonObject.getString("data");
                                if (!TextUtils.isEmpty(loginToken)) {
                                    RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                        @Override
                                        public void onTokenIncorrect() {
                                            reGetToken();
                                        }

                                        @Override
                                        public void onSuccess(String s) {

                                            UserInfo userInfo = new UserInfo(mUserInfo.getId(), mUserInfo.getUsername(), Uri.parse(mUserInfo.getAvatar()));
                                            RongIM.getInstance().refreshUserInfoCache(userInfo);
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                        }
                                    });
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
                break;
            case GET_TOKEN:
                LoginDbHelper.getRcToken(mid, type, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {
                    }

                    @Override
                    public void getSuccess(String successJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {
                                loginToken = jsonObject.getString("data");
                                if (!TextUtils.isEmpty(loginToken)) {
                                    RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                        @Override
                                        public void onTokenIncorrect() {
                                            reGetToken();
                                        }

                                        @Override
                                        public void onSuccess(String s) {

                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {

                                        }
                                    });
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
                break;
        }
    }


    private void reGetToken() {
        loginRongIM(LOGIN, userid, "2");
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            exitProgrames();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void exitProgrames() {
        if ((System.currentTimeMillis() - firstTime) > 1800) {
            UIHelper.showToast(this, "再按一次退出程序！");
            firstTime = System.currentTimeMillis();
        } else {
            MainActivity.this.finish();
            MobclickAgent.onKillProcess(this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onResume() {
        super.onResume();
        initLocation();
        initData();
        if (!TextUtils.isEmpty(userid)) {
            loginRongIM(LOGIN, userid, "1");//登录融云im
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    private void updateLocation() {//更新定位信息
        LoginDbHelper.updateLocation(userid, String.valueOf(lng), String.valueOf(lat), address, new IOAuthCallBack() {
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        EventBus.getDefault().unregister(MainActivity.this);
        locationClient.stopLocation();
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


    private void upMsgData() {
        String mid = (String) SharedPreferencesUtils.getParameter(MainActivity.this, "userId", "");
        HomeDbHelper.friendsBook(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        String num = result.getString("description");
                        if (!num.equals("0")) {
                            mBrbMain2.setBadgeNumber(Integer.valueOf(num));
                        } else {
                            mBrbMain2.setBadgeNumber(-1);
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


    @RequiresApi(api = 26)
    @Override
    public void onCountChanged(int count) {
        if (num > 0 && num < 100 || count > 0 && count < 100) {
            this.count = count;
            int total = count + num;
            mBrbMain1.setBadgeNumber(total);
            if (isHome()) {
                notification(total);//角标
            }

        } else if (count == 0 || num == 0) {
            mBrbMain1.setBadgeNumber(-1);
            noNotiication(0);//角标
        } else {
            mBrbMain1.setBadgeNumber(-1);
            noNotiication(0);//角标
        }
    }

    private void noNotiication(int count) {
        try {
            ShortcutBadger.applyCount(MainActivity.this,count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = 26)
    private void notification(int count) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        String notificationChannelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "id";
            NotificationChannel channel = null;
            channel = new NotificationChannel(channelId,"Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.RED); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            nm.createNotificationChannel(channel);
            notificationChannelId = channel.getId();
        }

        try {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(MainActivity.this, notificationChannelId)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("您有一条新消息")
                    .setTicker("您有一条新消息")
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setNumber(count)
                    .build();

            ShortcutBadger.applyCount(MainActivity.this, count);
            ShortcutBadger.applyNotification(getApplication(), notification, count);

            nm.notify(0, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }


    private void setMessageItemLongClickAction() {

        action = new MessageItemLongClickAction.Builder()
                .title("转发")
                .showFilter(new MessageItemLongClickAction.Filter() {
                    @Override
                    public boolean filter(UIMessage message) {
                        MessageContent messageContent = message.getContent();
                        return !(messageContent instanceof NotificationMessage)
                                && !(messageContent instanceof VoiceMessage);
                    }
                })
                .actionListener(new MessageItemLongClickAction.MessageItemLongClickListener() {
                    @Override
                    public boolean onMessageItemLongClick(Context context, UIMessage message) {
                        if (message.getContent() instanceof RecallNotificationMessage) {
                            return false;
                        } else {
                            Intent intent = new Intent(context, ForwardActivity.class);
                            intent.putExtra("msg", message.getMessage());
                            context.startActivity(intent);
                            return true;
                        }
                    }
                }).build();


        for (MessageItemLongClickAction message : RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions()) {
            if (message.getTitle(MainActivity.this).equals("转发")) {
                isAction = true;
                return;
            }
        }

        if (!isAction) {
            RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions().add(1, action);
        }
    }

    //获取通讯录列表
    private void loadData() {
        String userid = (String) SharedPreferencesUtils.getParameter(MainActivity.this, "userId", "");
        HomeDbHelper.friendsBook(userid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mFriendsData = parseData(successJson);
                        for (FriendsBookEntity entity : mFriendsData) {
                            UserInfo userInfo = new UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMsgDate(PushExtraEvent pushExtraEvent) {//评论推送
        if (pushExtraEvent.count.equals("null")) {
            mBrbMain3.setBadgeNumber(-1);
            EventBus.getDefault().post(new FollowEvent("0"));
        } else if (Integer.valueOf(pushExtraEvent.count) == 0) {
            mBrbMain3.setBadgeNumber(0);
        } else if (Integer.valueOf(pushExtraEvent.count) > 0 && Integer.valueOf(pushExtraEvent.count) < 100) {
            mBrbMain3.setBadgeNumber(0);
        } else {
            EventBus.getDefault().post(new FollowEvent("0"));
            mBrbMain3.setBadgeNumber(-1);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFriendsMsgDate(FriendsEvent friendsEvent) {//更新朋友圈消息
        if (Integer.valueOf(friendsEvent.count) > 0) {
            mBrbMain3.setBadgeNumber(0);
            EventBus.getDefault().post(new FollowEvent(friendsEvent.count));
            SharedPreferencesUtils.setParameter(MainActivity.this, "avatar", friendsEvent.avatar);
        } else {
            EventBus.getDefault().post(new FollowEvent("0"));
            mBrbMain3.setBadgeNumber(-1);
        }
    }


    //添加好友   EventBus.getDefault().post(new MailEvent());
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onAddFriendMsgDate(AddFriendEvent friendsEvent) {//更新朋友圈消息

        upMsgData();//更新通讯录通知
    }

    @Override
    public Group getGroupInfo(String groupsId) {
        if (groupsId.contains("activity")) {
            HomeDbHelper.getActivityInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        } else {
            HomeDbHelper.getGroupsInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        return null;
    }

    @Override
    public UserInfo getUserInfo(String s) {

        HomeDbHelper.getUserName(s, userid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        for (FriendsBookEntity entity : parseNmaeData(successJson)) {
                            UserInfo userInfo = new UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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

        return null;
    }


    public ArrayList<GroupEntity> parseGroupData(String result) {//Gson 解析
        ArrayList<GroupEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                GroupEntity entity = gson.fromJson(data.optJSONObject(i).toString(), GroupEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    public ArrayList<FriendsBookEntity> parseNmaeData(String result) {//Gson 解析用户头像
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    public ArrayList<FriendsBookEntity> parseData(String result) {
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public void Gonebottom(){
       mTabRg.setVisibility(View.GONE);
    }
}
