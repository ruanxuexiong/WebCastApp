package com.android.nana.find.http;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

/**
 * Created by lenovo on 2018/9/26.
 */

public class HttpService {


    /**
     *   查看手机归属地
     */
    public static void getHuafei(String phone,String money,String userId,String time,String sign, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("money", money);
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("sign", sign);
        HttpRequest.post("RechargePhone/recharge", requestParams, callBack);
    }

    /**
     *   查看手机归属地
     */
    public static void getPhone(String phone, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        HttpRequest.post("RechargePhone/getPhoneOperator", requestParams, callBack);
    }

    /**
     * 信息流
     */
    public static void getMoments(String mid, int page, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("UserArticle/allMoments", requestParams, callBack);
    }

    /**
     * 附近的人
     */
    public static void getByDistance(String mid, int page, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("userArticle/getMomentByDistance", requestParams, callBack);
    }
    /**
     * 话题动态列表
     */
    public static void getArticleByTag(String tagid,String mid, int page, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("tagid", tagid);
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("Article/getArticleByTag", requestParams, callBack);
    }
    /**
     * 获取第一条动态信息
     */
    public static void getArticleByTopic(String tagid,IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("tagid", tagid);
        HttpRequest.post("Article/getArticleByTopic", requestParams, callBack);
    }
    /**
     * 高德地图-周边检索
     */
    public static void getAroundUser(String mid, String lng, String lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("lng", lng);
        requestParams.addBodyParameter("lat", lat);
        HttpRequest.post("Location/getAroundUser", requestParams, callBack);
    }

    /**
     * 高德地图 - 个性化设置
     */
    public static void getLocationUser(String mid, String nick, String location_avatar, String call, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("nick", nick);
        requestParams.addBodyParameter("location_avatar", location_avatar);
        requestParams.addBodyParameter("call", call);
        HttpRequest.post("Location/updateLocationUser", requestParams, callBack);
    }

    //朋友圈-动态点赞
    public static void laudUserArticle(String mid, String userArticleId, String time, String sign, String showBound, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", mid);
        requestParams.addBodyParameter("userArticleId", userArticleId);
        requestParams.addBodyParameter("app_time", time);
        requestParams.addBodyParameter("sign", sign);
        requestParams.addBodyParameter("showBound", showBound);
        HttpRequest.post("userArticle/laudUserArticle", requestParams, callBack);
    }

    //发现页
    public static void discovery(String mid, double lat, double lng, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("UserArticle/discovery", requestParams, callBack);
    }


    //新版发现页
    public static void Newdiscovery(String mid, double lat, double lng,String refresh, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("refresh", refresh);
        HttpRequest.post("Article/lists", requestParams, callBack);
    }
}
