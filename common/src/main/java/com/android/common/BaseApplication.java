package com.android.common;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.android.common.models.BaseResultModel;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public abstract class BaseApplication extends MultiDexApplication {

    public DaoConfig mDaoConfig;
    public DbUtils mDbUtils;
    public static BaseApplication mInstance;

    private static Context applicationContext;

    public static BaseApplication getInstance() {
        return mInstance;
    }

    public void onCreate() {

        super.onCreate();

        mInstance = this;
        applicationContext = this;
        initImageLoader(this);

        init();
    }

    protected void init() {

    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取屏幕的宽度
     */
    public int getScreenWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public int getScreenHeight(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public abstract BaseResultModel getResultRequestListModel(String json);

    public abstract BaseResultModel getResultRequestModel(String json);

    public abstract String getBaseUrl();

    public abstract boolean checkLogin(Context activity);

    public abstract String getCustomerId(Context activity);


    public abstract String getAbsolutePath();

    public abstract String getFileFolder();

    public abstract int getFirstPageIndex();

    public abstract void registerBaseRequestParams(RequestParams params);

    public void registerBaseParams(RequestParams params) {

        registerBaseRequestParams(params);
    }

    public int getSuccessInt() {
        return 1;
    }

    public int getErrorInt() {
        return 2;
    }

    public int getEmptyInt() {
        return 3;
    }


    public String getSuccessField() {
        return "IsSuccess";
    }

    public String getMessageField() {
        return "Message";
    }

    public String getResultField() {
        return "Result";
    }




}
