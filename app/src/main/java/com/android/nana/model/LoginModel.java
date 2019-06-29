package com.android.nana.model;

import android.content.Context;

import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.WebCastApplication;
import com.android.nana.bean.UserInfo;
import com.android.nana.listener.LoginListener;
import com.android.nana.util.PreferencesKeys;
import com.android.nana.util.PreferencesUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class LoginModel {

    public static void doLoginThird(Context context, String successJson, String pass) {

        //     ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
        try {
            JSONObject jsonObject = new JSONObject(successJson);
            JSONObject data = new JSONObject(jsonObject.getString("data"));
            JSONObject result = new JSONObject(jsonObject.getString("result"));
            if (result.getString("state").equals("0")) {
                JSONObject object = JSONUtil.getJsonObject(data, "user");

                String id = JSONUtil.get(object, "id", "");
                String user_login = JSONUtil.get(object, "user_login", "");
                String username = JSONUtil.get(object, "username", "");
                String user_nicename = JSONUtil.get(object, "user_nicename", "");
                String user_email = JSONUtil.get(object, "user_email", "");
                String user_url = JSONUtil.get(object, "user_url", "");
                String avatar = JSONUtil.get(object, "avatar", "");
                String sex = JSONUtil.get(object, "sex", "");
                String birthday = JSONUtil.get(object, "birthday", "");
                String signature = JSONUtil.get(object, "signature", "");
                String last_login_ip = JSONUtil.get(object, "last_login_ip", "");
                String last_login_time = JSONUtil.get(object, "last_login_time", "");
                String create_time = JSONUtil.get(object, "create_time", "");
                String user_activation_key = JSONUtil.get(object, "user_activation_key", "");
                String user_status = JSONUtil.get(object, "user_status", "");
                String score = JSONUtil.get(object, "score", "");
                String user_type = JSONUtil.get(object, "user_type", "");
                String coin = JSONUtil.get(object, "coin", "");
                String mobile = JSONUtil.get(object, "mobile", "");
                String title = JSONUtil.get(object, "title", "");
                String backgroundImage = JSONUtil.get(object, "backgroundImage", "");
                String age = JSONUtil.get(object, "age", "");
                String introduce = JSONUtil.get(object, "introduce", "");
                String purposeId = JSONUtil.get(object, "purposeId", "");
                String provinceId = JSONUtil.get(object, "provinceId", "");
                String cityId = JSONUtil.get(object, "cityId", "");
                String districtId = JSONUtil.get(object, "districtId", "");
                String province = JSONUtil.get(object, "province", "");
                String city = JSONUtil.get(object, "city", "");
                String district = JSONUtil.get(object, "district", "");
                String money = JSONUtil.get(object, "money", "");
                String balance = JSONUtil.get(object, "balance", "");
                String payPassword = JSONUtil.get(object, "payPassword", "");
                String login_type = JSONUtil.get(object, "loginType", "");
                String status = JSONUtil.get(object, "status", ""); // 0待审核  1审核通过  2审核未通过

                UserInfo userInfo = new UserInfo(id, user_login, username, user_nicename, user_email, user_url,
                        avatar, sex, birthday, signature, last_login_ip, last_login_time, create_time,
                        user_activation_key, user_status, score, user_type, coin, mobile, title,
                        backgroundImage, age, introduce, purposeId, provinceId, cityId, districtId,
                        province, city, district, money, balance, payPassword, login_type, status);

                SharedPreferencesUtils.setParameter(context, "userId", id);
                SharedPreferencesUtils.setParameter(context, "user_nicename", user_nicename);
                SharedPreferencesUtils.setParameter(context, "userName", user_login);
                SharedPreferencesUtils.setParameter(context, "username", username);
                SharedPreferencesUtils.setParameter(context, "userPass", pass);
                SharedPreferencesUtils.setParameter(context, "payPassword", payPassword);
                SharedPreferencesUtils.setParameter(context, "mobile", mobile);
                SharedPreferencesUtils.setParameter(context, "status", status);//是否是认证用户
                SharedPreferencesUtils.saveObject(context, "userInfo", userInfo);

                if (LoginListener.getInstance().mOnLoginListener != null) {
                    LoginListener.getInstance().mOnLoginListener.login();
                }
                if (LoginListener.getInstance().mOnMainListener != null) {
                    LoginListener.getInstance().mOnMainListener.result();
                }

                PreferencesUtils.putString(WebCastApplication.getInstance(), PreferencesKeys.UID, id);
            } else {
                JSONObject object = JSONUtil.getJsonObject(data, "user");
                String id = JSONUtil.get(object, "id", "");
                String username = JSONUtil.get(object, "username", "");
                String avatar = JSONUtil.get(object, "avatar", "");
                UserInfo userInfo = new UserInfo(id,username,avatar);
                SharedPreferencesUtils.saveObject(context, "userInfo", userInfo);

                ToastUtils.showToast(result.getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void doLogin(Context context, String successJson, String pass) {

        ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
        if (mResultDetailModel.mIsSuccess) {
            JSONObject object = JSONUtil.getJsonObject(mResultDetailModel.mJsonData, "user");

            String id = JSONUtil.get(object, "id", "");
            String user_login = JSONUtil.get(object, "user_login", "");
            String username = JSONUtil.get(object, "username", "");
            String user_nicename = JSONUtil.get(object, "user_nicename", "");
            String user_email = JSONUtil.get(object, "user_email", "");
            String user_url = JSONUtil.get(object, "user_url", "");
            String avatar = JSONUtil.get(object, "avatar", "");
            String sex = JSONUtil.get(object, "sex", "");
            String birthday = JSONUtil.get(object, "birthday", "");
            String signature = JSONUtil.get(object, "signature", "");
            String last_login_ip = JSONUtil.get(object, "last_login_ip", "");
            String last_login_time = JSONUtil.get(object, "last_login_time", "");
            String create_time = JSONUtil.get(object, "create_time", "");
            String user_activation_key = JSONUtil.get(object, "user_activation_key", "");
            String user_status = JSONUtil.get(object, "user_status", "");
            String score = JSONUtil.get(object, "score", "");
            String user_type = JSONUtil.get(object, "user_type", "");
            String coin = JSONUtil.get(object, "coin", "");
            String mobile = JSONUtil.get(object, "mobile", "");
            String title = JSONUtil.get(object, "title", "");
            String backgroundImage = JSONUtil.get(object, "backgroundImage", "");
            String age = JSONUtil.get(object, "age", "");
            String introduce = JSONUtil.get(object, "introduce", "");
            String purposeId = JSONUtil.get(object, "purposeId", "");
            String provinceId = JSONUtil.get(object, "provinceId", "");
            String cityId = JSONUtil.get(object, "cityId", "");
            String districtId = JSONUtil.get(object, "districtId", "");
            String province = JSONUtil.get(object, "province", "");
            String city = JSONUtil.get(object, "city", "");
            String district = JSONUtil.get(object, "district", "");
            String money = JSONUtil.get(object, "money", "");
            String balance = JSONUtil.get(object, "balance", "");
            String payPassword = JSONUtil.get(object, "payPassword", "");
            String login_type = JSONUtil.get(object, "loginType", "");
            String idcard = JSONUtil.get(object, "idcard", "");
            String status = JSONUtil.get(object, "status", ""); // 0待审核  1审核通过  2审核未通过

            UserInfo userInfo = new UserInfo(id, user_login, username, user_nicename, user_email, user_url,
                    avatar, sex, birthday, signature, last_login_ip, last_login_time, create_time,
                    user_activation_key, user_status, score, user_type, coin, mobile, title,
                    backgroundImage, age, introduce, purposeId, provinceId, cityId, districtId,
                    province, city, district, money, balance, payPassword, login_type, status);

            SharedPreferencesUtils.setParameter(context, "userId", id);
            SharedPreferencesUtils.setParameter(context, "user_nicename", user_nicename);
            SharedPreferencesUtils.setParameter(context, "userName", user_login);
            SharedPreferencesUtils.setParameter(context, "payPassword", payPassword);
            SharedPreferencesUtils.setParameter(context, "mobile", mobile);
            SharedPreferencesUtils.setParameter(context, "status", status);
            SharedPreferencesUtils.saveObject(context, "userInfo", userInfo);
            SharedPreferencesUtils.setParameter(context, "username", username);
            SharedPreferencesUtils.setParameter(context, "userPass", pass);
            SharedPreferencesUtils.setParameter(context, "idcard", idcard);

            if (LoginListener.getInstance().mOnLoginListener != null) {
                LoginListener.getInstance().mOnLoginListener.login();
            }
            if (LoginListener.getInstance().mOnMainListener != null) {
                LoginListener.getInstance().mOnMainListener.result();
            }

            PreferencesUtils.putString(WebCastApplication.getInstance(), PreferencesKeys.UID, id);
        } else {

            UIHelper.showToast(context, mResultDetailModel.mMessage);
        }

    }


    public static void doLogin(Context context, String successJson) {

        ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
        if (mResultDetailModel.mIsSuccess) {
            JSONObject object = JSONUtil.getJsonObject(mResultDetailModel.mJsonData, "user");

            String id = JSONUtil.get(object, "id", "");
            String user_login = JSONUtil.get(object, "user_login", "");
            String username = JSONUtil.get(object, "username", "");
            String user_nicename = JSONUtil.get(object, "user_nicename", "");
            String user_email = JSONUtil.get(object, "user_email", "");
            String user_url = JSONUtil.get(object, "user_url", "");
            String avatar = JSONUtil.get(object, "avatar", "");
            String sex = JSONUtil.get(object, "sex", "");
            String birthday = JSONUtil.get(object, "birthday", "");
            String signature = JSONUtil.get(object, "signature", "");
            String last_login_ip = JSONUtil.get(object, "last_login_ip", "");
            String last_login_time = JSONUtil.get(object, "last_login_time", "");
            String create_time = JSONUtil.get(object, "create_time", "");
            String user_activation_key = JSONUtil.get(object, "user_activation_key", "");
            String user_status = JSONUtil.get(object, "user_status", "");
            String score = JSONUtil.get(object, "score", "");
            String user_type = JSONUtil.get(object, "user_type", "");
            String coin = JSONUtil.get(object, "coin", "");
            String mobile = JSONUtil.get(object, "mobile", "");
            String title = JSONUtil.get(object, "title", "");
            String backgroundImage = JSONUtil.get(object, "backgroundImage", "");
            String age = JSONUtil.get(object, "age", "");
            String introduce = JSONUtil.get(object, "introduce", "");
            String purposeId = JSONUtil.get(object, "purposeId", "");
            String provinceId = JSONUtil.get(object, "provinceId", "");
            String cityId = JSONUtil.get(object, "cityId", "");
            String districtId = JSONUtil.get(object, "districtId", "");
            String province = JSONUtil.get(object, "province", "");
            String city = JSONUtil.get(object, "city", "");
            String district = JSONUtil.get(object, "district", "");
            String money = JSONUtil.get(object, "money", "");
            String balance = JSONUtil.get(object, "balance", "");
            String payPassword = JSONUtil.get(object, "payPassword", "");
            String login_type = JSONUtil.get(object, "loginType", "");
            String status = JSONUtil.get(object, "status", ""); // 0待审核  1审核通过  2审核未通过

            UserInfo userInfo = new UserInfo(id, user_login, username, user_nicename, user_email, user_url,
                    avatar, sex, birthday, signature, last_login_ip, last_login_time, create_time,
                    user_activation_key, user_status, score, user_type, coin, mobile, title,
                    backgroundImage, age, introduce, purposeId, provinceId, cityId, districtId,
                    province, city, district, money, balance, payPassword, login_type, status);

            SharedPreferencesUtils.setParameter(context, "userId", id);
            SharedPreferencesUtils.setParameter(context, "user_nicename", user_nicename);
            SharedPreferencesUtils.setParameter(context, "userName", user_login);
            SharedPreferencesUtils.setParameter(context, "payPassword", payPassword);
            SharedPreferencesUtils.setParameter(context, "mobile", mobile);
            SharedPreferencesUtils.setParameter(context, "status", status);
            SharedPreferencesUtils.saveObject(context, "userInfo", userInfo);
            SharedPreferencesUtils.setParameter(context, "username", username);
            SharedPreferencesUtils.saveObject(context, "avatar", avatar);

            if (LoginListener.getInstance().mOnLoginListener != null) {
                LoginListener.getInstance().mOnLoginListener.login();
            }
            if (LoginListener.getInstance().mOnMainListener != null) {
                LoginListener.getInstance().mOnMainListener.result();
            }

            PreferencesUtils.putString(WebCastApplication.getInstance(), PreferencesKeys.UID, id);
        } else {

            UIHelper.showToast(context, mResultDetailModel.mMessage);
        }

    }

    public static void deLogin(Context context) {
        mContex = context;

        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(mContex, "userInfo", UserInfo.class);

        if (mUserInfo == null) return;

    /*    mPassword = (String) SharedPreferencesUtils.getParameter(mContex, "userPass", "");
        String registrationId = JPushInterface.getRegistrationID(mContex);

        switch (mUserInfo.getLogin_type()) {
            case "MOBILE":
                //  LoginDbHelper.login(mUserInfo.getUser_login(), mPassword, mUserInfo.getLogin_type(), registrationId, mLoginIoAuthCallBack);
                break;
            case "WECHAT":
            case "MICRO_BLOG":
                mPassword = "FacethreeZhibo2017";
                String json = (String) SharedPreferencesUtils.getParameter(mContex, "json", "");
                // LoginDbHelper.thirdLogin(mUserInfo.getLogin_type(), json, registrationId, mLoginIoAuthCallBack);
                break;
        }*/

    }

    private static Context mContex;
    private static UserInfo mUserInfo;
    private static String mPassword;

    private static IOAuthCallBack mLoginIoAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {

            if (!mUserInfo.getLogin_type().equals("MOBILE")) {
                SharedPreferencesUtils.setParameter(mContex, "json", successJson);

            }
            LoginModel.doLogin(mContex, successJson, mPassword);

        }

        @Override
        public void getFailue(String failueJson) {

        }
    };

}
