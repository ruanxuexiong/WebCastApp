package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

/**
 * Created by lenovo on 2017/10/25.
 * 朋友圈接口
 */

public class FriendDbHelper {


    public static final RequestParams REQUEST_PARAMS = new RequestParams();

    /**
     * 朋友圈
     *
     * @param mid      当前登录id
     * @param page
     * @param callBack
     */
    public static void moments(String mid, int page, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("UserArticle/moments", requestParams, callBack);
    }

    /**
     * 分享朋友圈数量
     */

    public static void shareArticle(String mid, String articleId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("articleId", articleId);
        HttpRequest.post("UserArticle/shareArticle", requestParams, callBack);
    }

    /**
     * 发现界面
     *
     * @param mid
     * @param callBack
     */
    public static void newMoments(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("UserArticle/newMoments", requestParams, callBack);
    }

    /**
     * 所有消息列表
     *
     * @param mid
     * @param callBack
     */
    public static void allMsgList(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("UserArticle/allMessage", requestParams, callBack);
    }

    /**
     * 新消息列表
     */
    public static void newMessages(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("UserArticle/newMessages", requestParams, callBack);
    }

    /**
     * 朋友圈详情
     *
     * @param mid
     * @param id
     * @param callBack
     */
    public static void article(String mid, String id, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("articleId", id);
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("UserArticle/article", requestParams, callBack);
    }

    /**
     * 朋友圈详情举报
     */

    public static void reportArticle(String mid, String id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("articleId", id);
        HttpRequest.post("UserArticle/reportArticle", requestParams, callBack);
    }


    /**
     * 朋友圈评论
     *
     * @param mid       当前登录ID
     * @param id        动态ID
     * @param toId      被评论的用户ID
     * @param commentId 被回复的上级评论ID
     * @param content   评论内容
     * @param callBack
     */
    public static void addComment(String mid, String id, String toId, String commentId, String content, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("articleId", id);
        requestParams.addBodyParameter("to_uid", toId);
        requestParams.addBodyParameter("to_comment_id", commentId);
        requestParams.addBodyParameter("content", content);
        HttpRequest.post("UserArticle/addComment", requestParams, callBack);
    }

    /**
     * 删除评论
     *
     * @param mid
     * @param commentId
     * @param callBack
     */
    public static void delComment(String mid, String commentId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("commentId", commentId);
        HttpRequest.post("UserArticle/delComment", requestParams, callBack);
    }

    /**
     * 点赞列表
     *
     * @param id
     * @param callBack
     */
    public static void laudUserList(String id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("articleId", id);
        HttpRequest.post("UserArticle/laudUserList", requestParams, callBack);
    }

    /**
     * 清空消息列表
     *
     * @param mid
     * @param callBack
     */
    public static void cleanMessage(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("UserArticle/cleanMessage", requestParams, callBack);
    }

    /**
     * 我的收藏
     */
    public static void collection(String mid, int pag, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(pag));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("UserArticle/myCollection", requestParams, callBack);
    }

    /**
     * 我的相册
     *
     * @param mid
     * @param uid
     * @param page
     * @param callBack
     */
    public static void moments(String mid, String uid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("UserArticle/myMoments", requestParams, callBack);
    }

    /**
     * 我的相册
     * 新接口
     *
     * @param mid
     * @param uid
     * @param page
     * @param callBack
     */
    public static void myPictures(String mid, String uid, int page, double lng, double lat, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("lng", String.valueOf(lng));
        requestParams.addBodyParameter("lat", String.valueOf(lat));
        HttpRequest.post("UserArticle/myPictures", requestParams, callBack);
    }

    /**
     * 删除动态
     *
     * @param mid
     * @param articleId
     * @param callBack
     */
    public static void delArticle(String mid, String articleId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("articleId", articleId);
        HttpRequest.post("UserArticle/delArticle", requestParams, callBack);
    }

    /**
     * 搜索朋友圈
     *
     * @param mid
     * @param keyword
     * @param callBack
     */
    public static void searchMoments(String mid, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("UserArticle/searchMoments", requestParams, callBack);
    }

    /**
     * 通讯录好友
     *
     * @param mid
     * @param callBack
     */
    public static void getPhoneUser(String name, String phone, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phones", phone);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Friends/addPhoneMobile", requestParams, callBack);
    }


    //发送短信
    public static void sendMsg(String idcard, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("idcard", idcard);
        HttpRequest.post("Friends/smsTemplateToAddFriend", requestParams, callBack);
    }

    //我的相册
    public static void getPicturesByMonth(String mid, String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("UserArticle/myPicturesByMonth", requestParams, callBack);
    }

    //发送消息成功后回调
    public static void getInformResult(String mid, String uid,String sign,String time, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", mid);
        requestParams.addBodyParameter("toUid", uid);
        requestParams.addBodyParameter("sign", sign);
        requestParams.addBodyParameter("time", time);
        HttpRequest.post("Inform/informResult", requestParams, callBack);
    }

    //判断是否通知
    public static void toInform(String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("toUid", uid);
        HttpRequest.post("Inform/isInform", requestParams, callBack);
    }
    //排行版
    public static void postRanking(int page,int limit, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("page", page+"");
        requestParams.addBodyParameter("limit", limit+"");
        HttpRequest.post("Inform/ranking", requestParams, callBack);
    }
    //我的收藏
    public static void getCollectionByMonth(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("UserArticle/myCollectionByMonth", requestParams, callBack);
    }
}
