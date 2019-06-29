package com.android.nana.dbhelper;

import android.util.Log;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.customer.myincome.Prepaid_Activity;
import com.lidroid.xutils.http.RequestParams;


/**
 * Created by Cristina on 2017/3/21.
 */
public class MyIncomeHelper {


    /**
     * 修改提现密码
     */

    public static void updatePayPwd(String userId, String phone, String oldPassword, String newPassword, String confirmPassword, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("oldPassword", oldPassword);
        requestParams.addBodyParameter("newPassword", newPassword);
        requestParams.addBodyParameter("confirmPassword", confirmPassword);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/updatewithdrawPassword", requestParams, callBack);
    }

    /**
     * 设置用户支付密码
     *
     * @param phone
     * @param password
     * @param verify
     * @param callBack
     */
    public static void setPayPassword(String phone, String password, String verify, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("verify", verify);
        HttpRequest.post("User/setPayPassword", requestParams, callBack);

    }

    //设置新的提现密码 和找回提现密码
    public static void doPayPassword(String phone, String verify, String password, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("verify", verify);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Passport/setwithdrawPassword", requestParams, callBack);
    }

    /**
     * 获取用户银行卡信息
     *
     * @param userId
     * @param callBack
     */
    public static void queryUserBankCard(String userId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("BankCard/queryUserBankCard", requestParams, callBack);

    }

    /**
     * 删除用户银行卡
     *
     * @param bankCardId
     * @param callBack
     */
    public static void deleteBankCard(String bankCardId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("bankCardId", bankCardId);
        HttpRequest.post("BankCard/deleteBankCard", requestParams, callBack);

    }

    /**
     * 银行卡详情
     *
     * @param cardNumber
     * @param callBack
     */
    public static void queryBankCardTypeWithCardNumber(String cardNumber, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("cardNumber", cardNumber);
        HttpRequest.post("BankCard/queryBankCardTypeWithCardNumber", requestParams, callBack);
    }

    /**
     * 添加银行卡
     *
     * @param userId
     * @param cardNumber
     * @param bankCardTypeId
     * @param callBack
     */
    public static void addBankCard(String userId, String username, String cardNumber,
                                   String bankCardTypeId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("username", username);
        requestParams.addBodyParameter("carNumber", cardNumber);
        requestParams.addBodyParameter("bankCardTypeId", bankCardTypeId);
        HttpRequest.post("BankCard/addBankCard", requestParams, callBack);
    }

    /**
     * 发起提现
     *
     * @param userId
     * @param money
     * @param bankCardId
     * @param callBack
     */
    public static void withDraw(String userId, String money, String bankCardId, String payPassword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("money", money);
        requestParams.addBodyParameter("payPassword", payPassword);
        requestParams.addBodyParameter("bankCardId", bankCardId);
        HttpRequest.post("Order/withDraw", requestParams, callBack);
    }

    /**
     * 用户提现
     */
    public static void setWithDraw(String userId, String money, String bankCardId, String payPassword, String phone, String appTime, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("money", money);
        requestParams.addBodyParameter("bankCardId", bankCardId);
        requestParams.addBodyParameter("payPassword", payPassword);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("app_time", appTime);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("money/withDraw", requestParams, callBack);
    }

    /**
     * 发起充值
     *
     * @param userId
     * @param money
     * @param payType
     * @param callBack
     */
    public static void recharge(String userId, String money, String payType, String time, String appSignature, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("money", money);
        requestParams.addBodyParameter("payType", payType);
        requestParams.addBodyParameter("app_time", time);
        requestParams.addBodyParameter("app_signature", appSignature);
        HttpRequest.post("Order/recharge", requestParams, callBack);
    }

    /**
     * 设置见面金额
     *
     * @param userId
     * @param money
     * @param callBack
     */
    public static void setSeeMoney(String userId, String money, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("money", money);
        HttpRequest.post("User/setSeeMoney", requestParams, callBack);

    }

    /**
     * 确认支付是否成功
     */
    public static void rechargeSuccess(String uid, String md5, String orderId, String time, String openId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("id", md5);
        requestParams.addBodyParameter("orderId", orderId);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("wxId", openId);
        HttpRequest.post("Order/rechargeSuccess", requestParams, callBack);
    }
    /**
     * 提现到支付宝
     */
    public static void withDrawAliPay(String alipayAccount, String money, String userId, String time, String sign,String payPassword, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("alipayAccount", alipayAccount);
        requestParams.addBodyParameter("money", money);
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("payPassword", payPassword);
        requestParams.addBodyParameter("sign", sign);

        HttpRequest.post("RechargePhone/withDrawToAlipay", requestParams, callBack);
    }
}
