package com.android.common.utils;

import android.util.Log;

import com.android.common.BaseApplication;
import com.android.common.DbConst;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HttpRequest {

    /**
     * 执行post方法，不通过缓存获取，不缓存数据
     *
     * @param path
     * @param params
     * @param callBack
     */
    public static void post(final String path, RequestParams params, final IOAuthCallBack callBack) {


        final String url = DbConst.mBaseUrl + path;
        if (params == null) params = new RequestParams();

        if (callBack != null) callBack.onStartRequest();

        BaseApplication.getInstance().registerBaseParams(params);

        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(1000 * 10);
        http.configTimeout(1000 * 25);
        http.configSoTimeout(1000 * 25);
        http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException exception, String message) {

                //	Log.i("HttpUrl", url);
                //	Log.i("Http网络加载失败", message);
                if (callBack != null) callBack.getFailue(message);
            }

            @Override
            public void onSuccess(ResponseInfo<String> result) {
                //	Log.i("HttpUrl", url);
                //Log.i("http网络加载成功", result.result);
                if (callBack != null) callBack.getSuccess(result.result);
            }
        });
    }

    public static void get(final String path, RequestParams params, final IOAuthCallBack callBack) {

        final String url = DbConst.mBaseUrl + path;
        if (params == null) params = new RequestParams();

        callBack.onStartRequest();

        BaseApplication.getInstance().registerBaseParams(params);

        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(1000 * 10);
        http.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException exception, String message) {
                // Log.i("HttpUrl", url);
                // Log.i("Http网络加载失败", message);
                callBack.getFailue(message);
            }

            @Override
            public void onSuccess(ResponseInfo<String> result) {
                callBack.getSuccess(result.result);
                // Log.i("HttpUrl", url);
                // Log.i("http网络加载成功", result.result);
            }
        });
    }

    public static void postUrl(final String path, RequestParams params, final IOAuthCallBack callBack) {

        if (params == null) params = new RequestParams();

        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(1000 * 60);
        http.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException exception, String message) {
                //Log.i("Http网络加载失败", message);
                callBack.getFailue(message);
            }

            @Override
            public void onSuccess(ResponseInfo<String> result) {
                callBack.getSuccess(result.result);
            }
        });
    }

}
