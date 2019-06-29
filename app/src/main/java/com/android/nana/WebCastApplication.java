package com.android.nana;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.common.BaseApplication;
import com.android.common.models.BaseResultModel;
import com.android.common.models.DBModel;
import com.android.common.utils.JSONUtil;
import com.android.nana.addialog.DisplayUtil;
import com.android.nana.bean.MessageEntity;
import com.android.nana.user.SealAppContext;
import com.android.nana.user.VideoMessage;
import com.android.nana.user.VideoMessageItemProvider;
import com.android.nana.user.VoiceMessageItemProvider;
import com.android.nana.util.DynamicTimeFormat;
import com.android.nana.util.SharedPreferencesUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.push.RongPushClient;

import okhttp3.internal.cache.CacheInterceptor;

public class WebCastApplication extends BaseApplication {

    static {
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                //全局设置（优先级最低）
                layout.setEnableAutoLoadMore(true);
                layout.setEnableOverScrollDrag(false);
                layout.setEnableOverScrollBounce(true);
                layout.setEnableLoadMoreWhenContentNotFull(true);
                layout.setEnableScrollContentWhenRefreshed(true);
            }
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
                layout.setPrimaryColorsId(R.color.activity_bg, R.color.green__66);

                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
   //     RongIM.setServerInfo("nav.cn.ronghub.com", "up.qbox.me");

//        Config config = new PushConfig.Builder()
//                .enableMiPush("2882303761517932803", "5841793219803") //配置小米推送
//                .enableHWPush(true)  // 配置华为推送
//                .enableMeiZuPush("119674","2667c09ac8ea4c1d882553c324794903") //配置魅族推送
//                .build();
      //  RongPushClient.setPushConfig(config);

       // RongPushClient.registerHWPush(this);
        RongPushClient.registerMiPush(this,"2882303761517932803", "5841793219803");
        RongIM.init(this);


        SealAppContext.init(this);
        initDisplayOpinion();
        Fresco.initialize(this);
        //初始化腾讯x5内核
        initX5();
        //融云自定义消息体
        RongIM.registerMessageType(VideoMessage.class);
        RongIM.getInstance().registerMessageTemplate(new VoiceMessageItemProvider());
        RongIM.getInstance().registerMessageTemplate(new VideoMessageItemProvider());


    }

    private void initX5() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initDisplayOpinion() {//弹窗广告
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void init() {
        super.init();
        initDbUtils();
        JPushInterface.init(this);            // 初始化 JPush
        initImageLoader(this);
         initBugly();//腾讯bugly
        initUMShare();//友盟
        //initLiveBase();//腾讯云短视频

        //保存第一次启动数据
        SharedPreferencesUtils.setParameter(getApplicationContext(), "want", "2");//我要咨询谁
        SharedPreferencesUtils.setParameter(getApplicationContext(), "who", "1");//谁要咨询我
        SharedPreferencesUtils.setParameter(getApplicationContext(), "msg", "3");//首页msg 提示
    }

  /*  private void initLiveBase() {
        TXLiveBase.setConsoleEnabled(true);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(TXLiveBase.getSDKVersionStr());
        CrashReport.initCrashReport(getApplicationContext(), strategy);
    }*/


    private void initUMShare() {
        PlatformConfig.setWeixin("wx40bf9b91c3e4c7e0", "d065270fddb30d6ec574d2b843610e60");
    }

    private void initBugly() {
        Beta.autoInit = true; //自动检查更新
        Beta.autoCheckUpgrade = true;//自动检查更新
        Beta.initDelay = 1 * 1000; //升级周期为60s
        Beta.largeIconId = R.mipmap.ic_launcher;
        Beta.smallIconId = R.mipmap.ic_launcher;
        Beta.defaultBannerId = R.mipmap.ic_launcher;
        Beta.showInterruptedStrategy = false;//点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
        // Beta.canShowUpgradeActs.add(MainActivity.class);
        // Beta.canShowUpgradeActs.add(AboutWebCastActivity.class);
        Beta.tipsDialogLayoutId = R.layout.tips_dialog;
        Bugly.init(getApplicationContext(), "283b639603", false);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public BaseResultModel getResultRequestListModel(String json) {

        BaseResultModel baseModel = new BaseResultModel();

        JSONObject jsonObject = JSONUtil.getStringToJson(json);
        JSONObject result = JSONUtil.getJsonObject(jsonObject, "result");

        int state = JSONUtil.get(result, "state", 1);
        if (state == 0) baseModel.mIsSuccess = true;

        List<JSONObject> datas = JSONUtil.getList(jsonObject, "data");
        if (datas != null) {
            baseModel.mList = datas;
        } else {
            JSONObject data = JSONUtil.getJsonObject(jsonObject, "data");
            if (data != null) {
                List<JSONObject> user = JSONUtil.getList(data, "users");
                if (user != null) baseModel.mList = user;
            }
        }

        return baseModel;
    }

    @Override
    public BaseResultModel getResultRequestModel(String json) {
        BaseResultModel baseModel = new BaseResultModel();
        JSONObject jsonObject = JSONUtil.getStringToJson(json);

        if (null != JSONUtil.getJsonObject(jsonObject, "result")) {
            JSONObject result = JSONUtil.getJsonObject(jsonObject, "result");
            baseModel.mJsonData = JSONUtil.getJsonObject(jsonObject, "data");

            int state = JSONUtil.get(result, "state", 1);
            if (state == 0) baseModel.mIsSuccess = true;

            baseModel.mMessage = JSONUtil.get(result, "description", "");

            baseModel.mFullResult = json;
        }
        return baseModel;
    }

    @Override
    public boolean checkLogin(Context activity) {
        return !TextUtils.isEmpty((String) SharedPreferencesUtils.getParameter(activity, "userId", ""));
    }

    @Override
    public String getCustomerId(Context activity) {
        return (String) SharedPreferencesUtils.getParameter(activity, "userId", "");
    }

    @Override
    public String getBaseUrl() {
        return "http://119.23.13.73/zbnew/api/";   // 正式环境：http://www.nanapal.com/api/    测试环境：http://119.23.13.73/zbnew/api/
    }

    @Override
    public String getAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "";
    }

    @Override
    public String getFileFolder() {
        return "NaNa";
    }

    @Override
    public int getFirstPageIndex() {
        return 1;
    }

    @Override
    public void registerBaseRequestParams(RequestParams params) {
    }


    private void initDbUtils() {
        mDaoConfig = new DbUtils.DaoConfig(this);
        mDaoConfig.setDbName("chayu");
        mDaoConfig.setDbVersion(1);
        mDbUtils = DbUtils.create(this);

        try {
            mDbUtils.createTableIfNotExist(DBModel.class);
            // 创建消息类对象表
            mDbUtils.createTableIfNotExist(MessageEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
