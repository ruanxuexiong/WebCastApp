package com.android.nana.dbhelper;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.StringHelper;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;
import java.util.List;

public class CustomerDbHelper {

    /**
     * 编辑个人信息
     *
     * @param userId
     * @param nickname        用户昵称
     * @param username        真是姓名
     * @param title           头衔
     * @param provinceId
     * @param cityId
     * @param districtId
     * @param age
     * @param purposeId       注册目的ID
     * @param introduce       个人介绍
     * @param headImage       头像
     * @param backgroundImage 背景图片
     * @param callBack
     */
    public static void updateUserInfo(String userId, String nickname, String username, String title,
                                      String provinceId, String cityId, String districtId, String age,
                                      String gender, String purposeId, String purpose, String introduce,
                                      File headImage, File backgroundImage, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("nickname", nickname);
        requestParams.addBodyParameter("username", username);
        requestParams.addBodyParameter("title", title);
        if (null != provinceId) {
            requestParams.addBodyParameter("provinceId", provinceId);
        } else {
            requestParams.addBodyParameter("provinceId", "");
        }

        if (null != cityId) {
            requestParams.addBodyParameter("cityId", cityId);
        } else {
            requestParams.addBodyParameter("cityId", "");
        }

        if (null != districtId) {
            requestParams.addBodyParameter("districtId", districtId);
        } else {
            requestParams.addBodyParameter("districtId", "");
        }

        requestParams.addBodyParameter("age", age);
        requestParams.addBodyParameter("sex", gender);
        requestParams.addBodyParameter("purposeId", purposeId);
        if (!TextUtils.isEmpty(purpose)) {
            requestParams.addBodyParameter("purpose", purpose);
        }
        requestParams.addBodyParameter("introduce", introduce);
        if (headImage != null) {
            requestParams.addBodyParameter("headImage", headImage);
        }
        if (backgroundImage != null) {
            requestParams.addBodyParameter("backgroundImage", backgroundImage);
        }
        HttpRequest.post("User/updateUserInfo", requestParams, callBack);

    }

    /**
     * 获取用户注册目的
     *
     * @param callBack
     */
    public static void queryPurpose(IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        HttpRequest.post("User/queryPurpose", requestParams, callBack);

    }

    /**
     * 获取直播用户
     *
     * @param pageIndex
     * @param pageSize
     * @param thisUserId
     * @param beginAge
     * @param endAge
     * @param sex
     * @param purposeId
     * @param provinceId
     * @param callBack
     */
    public static void queryUserLists(Activity mContext, int page, int pageIndex, int pageSize, String thisUserId, String columnId,
                                      String beginAge, String endAge, String sex, String purposeId,
                                      String provinceId, String orderByType, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        // requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        // requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));

        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("columnId", columnId);
        requestParams.addBodyParameter("beginAge", beginAge);
        requestParams.addBodyParameter("endAge", endAge);
        requestParams.addBodyParameter("sex", sex);
        requestParams.addBodyParameter("purposeId", purposeId);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("orderByType", orderByType);
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mContext));
        HttpRequest.post("User/queryUserLists", requestParams, callBack);

    }

    public static void queryUserLists(Activity mActivity, int pageIndex, int pageSize, String thisUserId, String keywords, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));

        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("keywords", keywords);
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mActivity));
        HttpRequest.post("User/queryUserLists", requestParams, callBack);

    }


    /**
     * 获取用户详细信息
     *
     * @param callBack
     */
    public static void queryUserInfo(String userId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("User/queryUserInfo", requestParams, callBack);

    }

    //我的钱包
    public static void getMoneyWallet(String userId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", userId);
        HttpRequest.post("Money/myWallet", requestParams, callBack);
    }

    //收支明细
    public static void getMoneyAll(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Income/all", requestParams, callBack);
    }


    /**
     * 用户身份认证
     *
     * @param userId
     * @param positivePicture
     * @param backPicture
     * @param callBack
     */
    public static void identityAuthentication(String userId, File positivePicture, File backPicture,String uname,String  idcard, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("uname", uname);
        requestParams.addBodyParameter("idcard", idcard);
        if (positivePicture == null) {
            requestParams.addBodyParameter("positivePicture", "");
        } else {
            requestParams.addBodyParameter("positivePicture", positivePicture);
        }

        if (backPicture == null) {
            requestParams.addBodyParameter("backPicture", "");
        } else {
            requestParams.addBodyParameter("backPicture", backPicture);
        }


        HttpRequest.post("User/identityAuthenticationNew", requestParams, callBack);

    }


    /**
     * 获取用户关注列表
     *
     * @param pageIndex
     * @param pageSize
     * @param endTime
     * @param thisUserId
     * @param columnId
     * @param type
     * @param callBack
     */
    public static void queryAttentionUserLists(int pageIndex, int pageSize, String endTime,
                                               String thisUserId, String columnId, String type, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("columnId", columnId.equals("0") ? "" : columnId);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("User/queryAttentionUserLists", requestParams, callBack);

    }

    /**
     * 获取看过我的档案用户列表信息
     *
     * @param pageIndex
     * @param pageSize
     * @param endTime
     * @param thisUserId
     * @param userId
     * @param callBack
     */
    public static void queryWatchHistoryLists(int pageIndex, int pageSize, String endTime,
                                              String thisUserId, String userId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("User/queryWatchHistoryLists", requestParams, callBack);

    }

    /**
     * 获取用户邀请见面记录
     *
     * @param pageIndex
     * @param pageSize
     * @param endTime
     * @param userId
     * @param type
     * @param callBack
     */
    public static void queryUserInvitationRecord(Activity mContext, int pageIndex, int pageSize, String endTime, String userId,
                                                 String type, String seeType, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("type", type);
        if (!TextUtils.isEmpty(seeType)) {
            requestParams.addBodyParameter("seeType", seeType);
        }
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mContext));
        HttpRequest.post("User/queryUserInvitationRecord", requestParams, callBack);
    }

    //谁要见我邀约记录
    public static void seedMimeRecord(String mid, String type, int page, String meetStatus, Activity mContext, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("meetStatus", meetStatus);
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mContext));
        HttpRequest.post("DirectSeedRecord/seedMimeRecord", requestParams, callBack);
    }

    //我要见谁接口
    public static void seeOtherRecord(String mid, String type, int mPage, String meetStatus, Activity mContext, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("page", String.valueOf(mPage));
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mContext));
        requestParams.addBodyParameter("meetStatus", meetStatus);
        HttpRequest.post("DirectSeedRecord/seeOtherRecord", requestParams, callBack);
    }


    //我要咨询谁
    public static void zxToOther(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Advisory/zxToOther", requestParams, callBack);
    }

    //咨询 - H5相关信息
    public static void AdvisoryHtmlUrl(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Advisory/AdvisoryHtmlUrl", requestParams, callBack);
    }

    public static void AdvisoryHtmlIndexUrl(String mid, IOAuthCallBack callBack) {//首页
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Advisory/AdvisoryIndexUrl", requestParams, callBack);
    }

    //谁要咨询我
    public static void zxToMe(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Advisory/zxToMe", requestParams, callBack);
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

    /**
     * 关注直播用户
     *
     * @param thisUserId
     * @param userId
     * @param callBack
     */
    public static void attention(String thisUserId, String userId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("User/attention", requestParams, callBack);

    }

    public static void cancelAttention(String thisUserId, String userId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("User/cancelAttention", requestParams, callBack);

    }

    //移除好友
    public static void removeFriend(String thisUserid, String userId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", thisUserid);
        requestParams.addBodyParameter("uid", userId);
        HttpRequest.post("Friends/removeFriend", requestParams, callBack);
    }


    /**
     * 根据用户id获取工作经历
     *
     * @param callBack
     */
    public static void queryUserWorkHistory(int pageIndex, int pageSize, String userId, String endTime, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        HttpRequest.post("WorkHistory/queryUserWorkHistory", requestParams, callBack);
    }

    /**
     * 获取工作经历
     */
    public static void queryUserWorkHistory(String userId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("WorkHistory/queryUserWorkHistory", requestParams, callBack);
    }

    /**
     * 根据用户id获取教育经历
     *
     * @param pageIndex
     * @param pageSize
     * @param userId
     * @param endTime
     * @param callBack
     */
    public static void queryUserEducationExperience(int pageIndex, int pageSize, String userId, String endTime, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        HttpRequest.post("EducationExperience/queryUserEducationExperience", requestParams, callBack);
    }

    public static void queryUserEducationExperience(String userId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("EducationExperience/queryUserEducationExperience", requestParams, callBack);
    }

    /**
     * 设置用户工作经历
     *
     * @param userId
     * @param name
     * @param position
     * @param introduce
     * @param beginTime
     * @param endTime
     * @param picture
     * @param callBack
     */
    public static void setUserWorkHistory(String userId, String name, String position, String introduce,
                                          String beginTime, String endTime, File picture, String workHistoryId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("position", position);
        requestParams.addBodyParameter("introduce", introduce);
        requestParams.addBodyParameter("beginTime", beginTime);
        requestParams.addBodyParameter("endTime", endTime);
        if (!TextUtils.isEmpty(workHistoryId)) {
            requestParams.addBodyParameter("workHistoryId", workHistoryId);
        }
        if (picture != null) {
            requestParams.addBodyParameter("picture", picture);
        }
        HttpRequest.post("WorkHistory/setUserWorkHistory", requestParams, callBack);

    }

    /**
     * 删除工作经历
     *
     * @param workHistoryId
     * @param callBack
     */
    public static void deleteWorkHistory(String workHistoryId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("workHistoryId", workHistoryId);
        HttpRequest.post("WorkHistory/deleteWorkHistory", requestParams, callBack);

    }

    /**
     * 设置用户教育经历
     *
     * @param userId
     * @param name
     * @param major
     * @param beginTime
     * @param endTime
     * @param picture
     * @param educationExperienceId
     * @param callBack
     */
    public static void setEducationExperience(String userId, String name, String major, String beginTime,
                                              String endTime, File picture, String educationExperienceId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("major", major);
        requestParams.addBodyParameter("beginTime", beginTime);
        requestParams.addBodyParameter("endTime", endTime);
        if (!TextUtils.isEmpty(educationExperienceId)) {
            requestParams.addBodyParameter("educationExperienceId", educationExperienceId);
        }
        if (picture != null) {
            requestParams.addBodyParameter("picture", picture);
        }
        HttpRequest.post("EducationExperience/setEducationExperience", requestParams, callBack);

    }

    //添加教育经历

    public static void setEducationExperience(String userId, String name, String major, String beginTime,
                                              String endTime, File picture, String educationExperienceId, int qualification, String experience, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("major", major);
        requestParams.addBodyParameter("beginTime", beginTime);
        requestParams.addBodyParameter("endTime", endTime);
        if (!TextUtils.isEmpty(educationExperienceId)) {
            requestParams.addBodyParameter("educationExperienceId", educationExperienceId);
        }
        if (picture != null) {
            requestParams.addBodyParameter("picture", picture);
        }
        requestParams.addBodyParameter("qualification", String.valueOf(qualification));
        requestParams.addBodyParameter("experience", experience);
        HttpRequest.post("EducationExperience/setEducationExperience", requestParams, callBack);
    }

    /**
     * 删除教育经历
     *
     * @param educationExperienceId
     * @param callBack
     */
    public static void deleteEducationExperience(String educationExperienceId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("educationExperienceId", educationExperienceId);
        HttpRequest.post("EducationExperience/deleteEducationExperience", requestParams, callBack);

    }

    /**
     * 发表文章
     *
     * @param userId
     * @param content
     * @param count
     * @param list
     * @param callBack
     */
    public static void publishArticle(String userId, String content, int count, List<File> list, String type, String lng, String lat, String address, int bound_type, String bound_num, String bound_total, String pre_bound, String distance, String app_time, String app_secret, String advertising, String advType, String advUrl, String mExtensionUrl, String extensionUrlType,String userIdList, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("content", content);
        requestParams.addBodyParameter("count", String.valueOf(count));
        requestParams.addBodyParameter("type", type);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                requestParams.addBodyParameter("picture" + (i + 1), list.get(i));
            }
        } else {
            requestParams.addBodyParameter("picture", "");
        }
        requestParams.addBodyParameter("lng", lng);
        requestParams.addBodyParameter("lat", lat);
        requestParams.addBodyParameter("address", address);
        if (0 == bound_type) {
            requestParams.addBodyParameter("bound_type", String.valueOf(bound_type));
            requestParams.addBodyParameter("distance", "-1");
        } else {
            requestParams.addBodyParameter("bound_type", String.valueOf(bound_type));
            requestParams.addBodyParameter("bound_num", bound_num);
            requestParams.addBodyParameter("bound_total", bound_total);
            requestParams.addBodyParameter("pre_bound", pre_bound);
            requestParams.addBodyParameter("distance", distance);
            requestParams.addBodyParameter("app_time", app_time);
            requestParams.addBodyParameter("app_secret", app_secret);

            if (null != advertising && !"".equals(advertising)) {
                requestParams.addBodyParameter("advertising", advertising);
                requestParams.addBodyParameter("adv_type", advType);
                requestParams.addBodyParameter("adv_url", advUrl);
            } else {
                requestParams.addBodyParameter("advertising", "");
                requestParams.addBodyParameter("adv_type", "");
                requestParams.addBodyParameter("adv_url", "");
            }

        }
        requestParams.addBodyParameter("spread_url", mExtensionUrl);
        requestParams.addBodyParameter("spread_url_type", extensionUrlType);
        requestParams.addBodyParameter("userIdList", userIdList);
        HttpRequest.post("UserArticle/publishArticle", requestParams, callBack);
    }



    //获取话题

    public static void getHuati(String tag_name,  IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();

        requestParams.addBodyParameter("tag_name", tag_name);

        HttpRequest.post("Article/getUserArticleTag", requestParams, callBack);
    }



    /**
     * 发布视频
     *
     * @param userId
     * @param content
     * @param count
     * @param type
     * @param callBack
     */
    public static void publishArticle(String userId, String content, int count, File file, String type, String video, String lng, String lat, String address, int bound_type, String bound_num, String bound_total, String pre_bound, String distance, String app_time, String app_secret, String advertising, String advType, String advUrl, String mExtensionUrl, String spread_url_type,String userIdList, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("content", content);
        requestParams.addBodyParameter("count", String.valueOf(count));
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("video", video);
        requestParams.addBodyParameter("video_pic", file);

        requestParams.addBodyParameter("lng", lng);
        requestParams.addBodyParameter("lat", lat);
        requestParams.addBodyParameter("address", address);

        if (0 == bound_type) {
            requestParams.addBodyParameter("bound_type", String.valueOf(bound_type));
            requestParams.addBodyParameter("distance", "-1");
        } else {
            requestParams.addBodyParameter("bound_type", String.valueOf(bound_type));
            requestParams.addBodyParameter("bound_num", bound_num);
            requestParams.addBodyParameter("bound_total", bound_total);
            requestParams.addBodyParameter("pre_bound", pre_bound);
            requestParams.addBodyParameter("distance", distance);
            requestParams.addBodyParameter("app_time", app_time);
            requestParams.addBodyParameter("app_secret", app_secret);

            if (null != advertising) {
                requestParams.addBodyParameter("advertising", advertising);
            } else {
                requestParams.addBodyParameter("advertising", "");
            }
            if (null != advType) {
                requestParams.addBodyParameter("adv_type", advType);
            }else {
                requestParams.addBodyParameter("adv_type", "");
            }

            if (null != advUrl){
                requestParams.addBodyParameter("adv_url", advUrl);
            }else {
                requestParams.addBodyParameter("adv_url", "");
            }

        }
        requestParams.addBodyParameter("spread_url", mExtensionUrl);
        requestParams.addBodyParameter("spread_url_type", spread_url_type);
        requestParams.addBodyParameter("userIdList", userIdList);
        HttpRequest.post("UserArticle/publishArticle", requestParams, callBack);
    }

    /**
     * 收藏文章
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void collectionUserArticle(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("userArticleId", userArticleId);
        HttpRequest.post("UserArticle/collectionUserArticle", requestParams, callBack);

    }

    /**
     * 取消收藏
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void cancelCollectionUserArticle(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("userArticleId", userArticleId);
        HttpRequest.post("UserArticle/cancelCollectionUserArticle", requestParams, callBack);

    }

    public static void queryUserArticle(String userId, String thisUserId, String endTime, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", "1");
        requestParams.addBodyParameter("pageSize", "1");
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("thisUserId", thisUserId);
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        HttpRequest.post("UserArticle/queryUserArticle", requestParams, callBack);

    }


    /**
     * 消费统计
     *
     * @param pageIndex
     * @param pageSize
     * @param userId
     * @param endTime
     * @param type
     * @param callBack
     */
    public static void consume(int pageIndex, int pageSize, String userId, String endTime, String type, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        requestParams.addBodyParameter("userId", userId);
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("User/consume", requestParams, callBack);

    }

    /**
     * 获取用户文章详细信息
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void queryUserArticleInfo(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userArticleId", userArticleId);
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("UserArticle/queryUserArticleInfo", requestParams, callBack);

    }

    /**
     * 删除用户文章
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void deleteUserArticle(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userArticleId", String.valueOf(userArticleId));
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("UserArticle/deleteUserArticle", requestParams, callBack);

    }

    /**
     * 用户文章点赞
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void laudUserArticle(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("userArticleId", userArticleId);
        HttpRequest.post("UserArticle/laudUserArticle", requestParams, callBack);

    }

    /**
     * 取消用户文章点赞
     *
     * @param userId
     * @param userArticleId
     * @param callBack
     */
    public static void cancelLaudUserArticle(String userId, String userArticleId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("userArticleId", String.valueOf(userArticleId));
        HttpRequest.post("UserArticle/cancelLaudUserArticle", requestParams, callBack);

    }

    /**
     * 根据用户ID获取直播专长信息
     *
     * @param pageIndex
     * @param pageSize
     * @param userId
     * @param endTime
     * @param callBack
     */
    public static void queryUserPersonalExpertise(int pageIndex, int pageSize, String userId, String endTime, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("pageIndex", String.valueOf(pageIndex));
        requestParams.addBodyParameter("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(endTime)) {
            requestParams.addBodyParameter("endTime", endTime);
        }
        HttpRequest.post("PersonalExpertise/queryUserPersonalExpertise", requestParams, callBack);

    }

    public static void setPersonalExpertise(String userId, String personalExpertiseId, List<String> list,
                                            List<String> expertises, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("expertises", userId);

        if (list != null) for (int i = 0; i < list.size(); i++)
            requestParams.addBodyParameter("columnIds[" + i + "]", list.get(i));

        if (!TextUtils.isEmpty(personalExpertiseId))
            requestParams.addBodyParameter("personalExpertiseId", personalExpertiseId);

        if (expertises != null) for (int i = 0; i < expertises.size(); i++)
            requestParams.addBodyParameter("expertises[" + i + "]", expertises.get(i));

        HttpRequest.post("PersonalExpertise/setPersonalExpertise", requestParams, callBack);

    }

    public static void deletePersonalExpert(String personalExpertiseId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("personalExpertiseId", personalExpertiseId);
        HttpRequest.post("PersonalExpertise/deletePersonalExpertise", requestParams, callBack);

    }

    /**
     * 获取文章信息
     *
     * @param termId
     * @param callBack
     */
    public static void article(String termId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("termId", termId);
        HttpRequest.post("Posts/article", requestParams, callBack);

    }

    /**
     * 更新通话时长
     *
     * @param userId
     * @param directSeedingRecordId
     * @param timeLong
     * @param callBack
     */
    public static void updateTimeLong(Context mContext, String userId, String directSeedingRecordId, String timeLong, String type, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("directSeedingRecordId", directSeedingRecordId);
        requestParams.addBodyParameter("timeLong", timeLong);
        if (null != type) {
            requestParams.addBodyParameter("meetStatus", "4");//（1=预约见面   2=马上视频 3=旧的预约模式） 4=互为好友后马上视频
        } else {
            requestParams.addBodyParameter("meetStatus", "3");
        }

        HttpRequest.post("Meeting/updateTimeLong", requestParams, callBack);

    }

    /**
     * 设置邮箱
     *
     * @param userId
     * @param email
     * @param callBack
     */
    public static void setEmail(String userId, String email, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("email", email);
        HttpRequest.post("User/setEmail", requestParams, callBack);

    }


    /**
     * 获取主播见面时长
     *
     * @param callBack
     */
    public static void queryUserSeeTimeLong(IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        HttpRequest.post("User/queryUserSeeTimeLong", requestParams, callBack);

    }


    /**
     * 举报
     */
    public static void addReport(String uid, String reportUid, String reason, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("reportUid", reportUid);
        requestParams.addBodyParameter("reason", reason);
        HttpRequest.post("Private/addReport", requestParams, callBack);
    }

    /**
     * 获取举报列表
     */
    public static void getReportList(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("Private/reasonList", requestParams, callBack);
    }

    /**
     * 拉黑
     */
    public static void addBackList(String uid, String backUid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("backUid", backUid);
        HttpRequest.post("Private/addBackList", requestParams, callBack);
    }

    //开启求职模式
    public static void openJobModel(String mUid, String type, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mUid);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("UserJob/doJobModel", requestParams, callBack);
    }

    //开启HR模式

    public static void odHrModel(String mUid, String type, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mUid);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("UserJob/doHrModel", requestParams, callBack);
    }

    //查看别人相册
    public static void getUserArticle(String userId, String thisUserId, String endTime, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("endTime", endTime);
        HttpRequest.post("UserArticle/UserArticle", requestParams, callBack);
    }

    //首页获取标签
    public static void getIndexTags(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("Index/tags", requestParams, callBack);
    }

    //获取用户列表
    public static void getIndexUserList(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("order", "order");
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Index/UserList", requestParams, callBack);
    }

    //筛选
    public static void searchFilterUser(String mid, String cityId, String sex, String lowPrice, String hightPrice, String tag, String order, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("lowPrice", lowPrice);
        requestParams.addBodyParameter("hightPrice", hightPrice);
        requestParams.addBodyParameter("tag", tag);
        requestParams.addBodyParameter("order", order);
        requestParams.addBodyParameter("sex", sex);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/filterUser", requestParams, callBack);
    }

    //当前用户是否认证
    public static void checkIsAlert(String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("CheckUserInfo/checkIsAlert", requestParams, callBack);
    }

    // 每天启动弹出消息
    public static void checkUser(String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("CheckUserInfo/checkUser", requestParams, callBack);
    }

    //新见面记录
    public static void record(String uid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", uid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Meeting/myInviteRecord", requestParams, callBack);
    }

    //首页加载活动

    public static void loadActivity(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Index/activities", requestParams, callBack);
    }

    //添加好友
    public static void addFriend(String mid, String uid, String message, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("message", message);
        HttpRequest.post("Friends/addFriend", requestParams, callBack);
    }

    //我的粉丝
    public static void followings(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Follow/followings", requestParams, callBack);
    }

    /**
     * 获取7牛云token
     */
    public static void getToken(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("UserArticle/getQiniuToken", requestParams, callBack);
    }

    //支出明细
    public static void getExpenditure(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Income/expendiureLog", requestParams, callBack);
    }

    //收入明细
    public static void getIncomeData(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Income/RevenueLog", requestParams, callBack);
    }

    //收入明细充值
    public static void getRechargeData(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Income/rechargeLog", requestParams, callBack);
    }

    //收入明细提现
    public static void getPutForwardData(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Income/tixianLog", requestParams, callBack);
    }

    //收入明细详情
    public static void getDetailData(String mid, String id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("id", id);
        HttpRequest.post("Income/detail", requestParams, callBack);
    }

    //是否允许自定义位置

    public static void isAllowLocation(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("UserArticle/isAllowLocation", requestParams, callBack);
}
}
