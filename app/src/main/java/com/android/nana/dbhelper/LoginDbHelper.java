package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

public class LoginDbHelper {


    //新接口用户登录
    public static void doLogin(String registrationId, String phone, String password, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("registrationId", registrationId);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/doLoginWithPhone", requestParams, callBack);
    }


    /**
     * 三方登录
     *
     * @param type
     * @param grantString
     * @param callBack
     */
 /*   public static void login(String type, String grantString, String registrationId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("grantString", grantString);
        requestParams.addBodyParameter("registrationId", registrationId);
        HttpRequest.post("Login/login", requestParams, callBack);
    }*/

    /**
     * 第三方登录
     */
    public static void thirdLogin(String type, String registrationId, String grantString, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("registrationId", registrationId);
        requestParams.addBodyParameter("grantString", grantString);
        HttpRequest.post("Wechat/LoginWithOauth", requestParams, callBack);
    }

    /**
     * 获取短信验证码
     *
     * @param phone
     * @param callBack
     */
    public static void sendMessage(String phone, String type, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("Public/sendMessage", requestParams, callBack);
    }

    //微信登录绑定手机号码发送短信
    public static void sendMessage(String phone, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        HttpRequest.post("Public/sendMessage", requestParams, callBack);
    }

    /**
     * 绑定手机号
     *
     * @param phone
     * @param callBack
     */
    public static void registerOauth(String phone, String verify, String type, String registrationId, String grantString, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("verify", verify);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("registrationId", registrationId);
        requestParams.addBodyParameter("grantString", grantString);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/registerOauth", requestParams, callBack);
    }

    //短信登录接口
    public static void sendLoginCode(String phone, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        HttpRequest.post("Public/sendLoginCode", requestParams, callBack);
    }

    //验证码登录
    public static void doLoginCode(String phone, String code, String registrationId, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("code", code);
        requestParams.addBodyParameter("registrationId", registrationId);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/doLoginWithCode", requestParams, callBack);
    }

    public static void register(String phone, String password, String verify, String name, String param, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("verify", verify);
        requestParams.addBodyParameter("realname", name);
        requestParams.addBodyParameter("param", param);
        HttpRequest.post("Login/register", requestParams, callBack);
    }

    public static void doRegister(String phone, String verify, String realname, String password, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("verify", verify);
        requestParams.addBodyParameter("realname", realname);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/register", requestParams, callBack);
    }

    public static void resetPassword(String phone, String password, String verify, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("verify", verify);
        HttpRequest.post("User/resetPassword", requestParams, callBack);

    }

    public static void updatePassword(String userId, String oldPassword, String password, String confirmPassword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("oldPassword", oldPassword);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("confirmPassword", confirmPassword);
        HttpRequest.post("User/updatePassword", requestParams, callBack);
    }

    /**
     * 更换手机号
     *
     * @param userId
     * @param phone
     * @param verify
     * @param callBack
     */
    public static void changeMobile(String userId, String phone, String verify, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("verify", verify);
        HttpRequest.post("User/changeMobile", requestParams, callBack);

    }

    public static void updatePwd(String userId, String oldPassword, String newPassword, String confirmPassword, String phone, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("oldPassword", oldPassword);
        requestParams.addBodyParameter("newPassword", newPassword);
        requestParams.addBodyParameter("confirmPassword", confirmPassword);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/setNewPassword", requestParams, callBack);
    }

    //获取融云token
    public static void getRcToken(String uid, String type, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("ChatRongCloud/getRcToken", requestParams, callBack);
    }

    //更新定位信息
    public static void updateLocation(String uid, String lng, String lat,String address, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", uid);
        requestParams.addBodyParameter("lng", lng);
        requestParams.addBodyParameter("lat", lat);
        requestParams.addBodyParameter("address", address);
        HttpRequest.post("Location/updateLocation", requestParams, callBack);
    }

    //获取七牛云url
    public static void getQiNiuYunUrl(IOAuthCallBack callBack){
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("Index/getQiniuUrl", requestParams, callBack);
    }
}
