package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;

/**
 * Created by lenovo on 2018/1/19.
 * 名片接口
 */

public class CardDbHelper {


    /**
     * 个人原有信息
     *
     * @param mid
     * @param callBack
     */
    public static void oldPersonInfo(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("BusinessCard/oldPersonInfo", requestParams, callBack);
    }

    /**
     * 名片首页
     */
    public static void index(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("BusinessCard/index", requestParams, callBack);
    }

    /**
     * 新增编辑名片
     *
     * @param mid
     * @param cardId
     * @param name
     * @param position
     * @param company
     * @param phone
     * @param card
     * @param logo
     * @param mobile
     * @param fax
     * @param email
     * @param postal
     * @param template
     * @param address
     * @param callBack
     */
    public static void addCard(String type, String mid, String cardId, String name, String position, String company, String phone, File card, File logo, String mobile, String fax, String email, String postal, String template, String address, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cardId", cardId);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("position", position);
        requestParams.addBodyParameter("company", company);
        requestParams.addBodyParameter("phone", phone);
        if (null != card) {
            requestParams.addBodyParameter("card", card);
        } else {
            requestParams.addBodyParameter("card", "");
        }
        if (null != logo) {
            requestParams.addBodyParameter("logo", logo);
        } else {
            requestParams.addBodyParameter("logo", "");
        }

        requestParams.addBodyParameter("mobile", mobile);
        requestParams.addBodyParameter("fax", fax);
        if (null != email) {
            requestParams.addBodyParameter("email", email);
        } else {
            requestParams.addBodyParameter("email", "");
        }

        requestParams.addBodyParameter("postal", postal);
        requestParams.addBodyParameter("templatestl", template);
        requestParams.addBodyParameter("address", address);
        HttpRequest.post("BusinessCard/addCard", requestParams, callBack);
    }

    /**
     * 名片列表
     */
    public static void cardList(String mid, String type, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("BusinessCard/myCardList", requestParams, callBack);
    }

    /**
     * 删除名片
     */
    public static void delCard(String mid, String cardId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cardId", cardId);
        HttpRequest.post("BusinessCard/delCard", requestParams, callBack);
    }

    /**
     * 编辑名片
     */
    public static void editCard(String mid, String cardId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cardId", cardId);
        HttpRequest.post("BusinessCard/editCard", requestParams, callBack);
    }

    /**
     * 发送聊天消息
     */
    public static void sendMessage(String mid, String uid, String status, String banTip, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("status", status);
        requestParams.addBodyParameter("banTip", banTip);
        HttpRequest.post("ChatRongCloud/sendMessage", requestParams, callBack);
    }

    /**
     * 打印名片
     */
    public static void orderCard(String mid, String cardId, String name, String phone, String provinceId, String cityId, String areaId, String address, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cardId", cardId);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("areaId", areaId);
        requestParams.addBodyParameter("address", address);
        HttpRequest.post("BusinessCard/orderCard", requestParams, callBack);
    }

    /**
     * 搜索
     */
    public static void searchCard(String mid, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("BusinessCard/searchCard", requestParams, callBack);
    }
}
