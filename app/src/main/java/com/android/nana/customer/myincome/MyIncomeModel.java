package com.android.nana.customer.myincome;

import android.content.Context;

import com.android.common.helper.UIHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.bean.OrderEntity;
import com.android.nana.dbhelper.MyIncomeHelper;
import com.android.nana.util.MD5;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

public class MyIncomeModel {

    public static void recharge(final Context context, String money, final String payType, String userId, String time, final OnMyIncomeLinstener onMyIncomeLinstener) {

        String appSignature = MD5.MD5Hash(userId + "&" + money + "&" + payType + "&" + time + "&" + "aaa8916a9dcb8e38e8c5a2d0b5d221f8");

        MyIncomeHelper.recharge(userId, money, payType, time, appSignature, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                UIHelper.showOnLoadingDialog(context, "正在处理，请稍后");
            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        OrderEntity item = parseData(successJson);
                        String orderId = item.getId();
                        String money = item.getMoney();
                        switch (payType) {
                            case "PAY_WITH_WECHAT": // 微信支付
                                onMyIncomeLinstener.doWxPay(money, orderId);
                                break;
                            case "PAY_WITH_ALIPAY": // 支付宝支付
                                onMyIncomeLinstener.doAliPay(money, orderId);
                                break;
                        }
                    } else {
                        ToastUtils.showToast("支付发起失败，请稍后重试！");
                    }

                    UIHelper.hideOnLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("充值失败，请稍后重试！");
                UIHelper.hideOnLoadingDialog();
            }
        });
    }

    public interface OnMyIncomeLinstener {
        void doWxPay(String money, String orderId);

        void doAliPay(String money, String orderId);
    }

    private static OrderEntity parseData(String result) {
        OrderEntity entity = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.getString("order"), OrderEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
