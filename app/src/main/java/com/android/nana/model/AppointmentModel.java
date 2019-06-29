package com.android.nana.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.android.common.helper.DialogHelper;
import com.android.common.pay.WxPaymentBuilder;
import com.android.common.ui.CustomWindowDialog;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/30 0030.
 * 预约支付
 */

public class AppointmentModel {

    private Context mContext;

    private CustomWindowDialog mCwdPay; // 支付
    private WxPaymentBuilder mWxPaymentBuilder;

    private String meetMoney, rate, mName, mThisName;

    public AppointmentModel(Context context) {

        mContext = context;
        //  mCwdPay = new CustomWindowDialog(context);
        //mWxPaymentBuilder = new WxPaymentBuilder((Activity) context);

    }

    String mThisUserId, mUserId, mPayPassword, mMoney;

    public void init(String thisUserId, String userId, String payPassword) {

        mThisUserId = thisUserId;
        mUserId = userId;
        mPayPassword = payPassword;

    }

    private void loadData(String mUserId) {
        HomeDbHelper.getMeetingInfo(mUserId, mThisUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        final JSONObject data = new JSONObject(jsonobject.getString("data"));
                        meetMoney = data.getString("meetMoney");
                        //  rate = data.getString("rate");
                        DialogHelper.customAlert(mContext, "支付确认", "您需要预付" + data.getString("meetMoney") + "元的约见费用，约见\n 不成功将会在24小时内退回您的账户。", "确认支付", "取消", new DialogHelper.OnAlertConfirmClick() {
                            @Override
                            public void OnClick(String content) {

                                EventBus.getDefault().post(new MessageEvent(""));
                                meeting(content);
                            }

                            @Override
                            public void OnClick() {
                                EventBus.getDefault().post(new MessageEvent(""));
                            }
                        }, new DialogHelper.OnAlertConfirmClick() {
                            @Override
                            public void OnClick(String content) {
                            }

                            @Override
                            public void OnClick() {

                                EventBus.getDefault().post(new MessageEvent(""));
                            }
                        });
                    } else if (result.getString("state").equals("-1")) {
                        DialogHelper.customAlert(mContext, "提示", result.getString("description"), "知道了", "取消", new DialogHelper.OnAlertConfirmClick() {
                            @Override
                            public void OnClick(String content) {

                            }

                            @Override
                            public void OnClick() {

                            }
                        }, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    public void setMoney(String money) {

        mMoney = money;

    }


    public void doDialog(final String money, String name) {
        this.mName = name;
        loadData(mUserId);
    }

    public void doPriceDialog(final String money, String name) {

        DialogHelper.customPriceAlert(mContext, "输入约见费用", "对方设置了由您自愿出价，请输入您想预付的约见费用", "确定", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast("约见费用不能为空！");
                } else {
                    show(content);
                }
            }

            @Override
            public void OnClick() {
            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {
            }
        });
    }

    public void meeting(String message) {

        HomeDbHelper.appointMeeting(mUserId, mThisUserId, message, "", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
                        ToastUtils.showToast(result.getString("description"));
                    } else if (result.getString("state").equals("-1")) {
                        ToastUtils.showToast(result.getString("description"));
                    } else if (result.getString("state").equals("-3")) {
                        new AlertDialog.Builder(mContext).setTitle("温馨提示").setMessage("您的余额不足，请充值后再约见！")
                                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        Intent intent = new Intent(mContext, RechargeActivity.class);
                                        intent.putExtra("UserId", mUserId);
                                        intent.putExtra("Money", mMoney);
                                        intent.putExtra("IsAnchor", true);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }

                                }).show();
                    } else {

                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    private void show(final String money) {
        DialogHelper.customAlert(mContext, "支付确认", "您需要预付" + money + "元的约见费用，约见\n 不成功将会在24小时内退回您的账户。", "确认支付", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

                EventBus.getDefault().post(new MessageEvent(""));
                meetingPrice(money, content);
            }

            @Override
            public void OnClick() {
                EventBus.getDefault().post(new MessageEvent(""));
            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {

                EventBus.getDefault().post(new MessageEvent(""));
            }
        });
    }

    private void meetingPrice(String money, String content) {
        HomeDbHelper.appointMeeting(mUserId, mThisUserId, content, money, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
                        ToastUtils.showToast(result.getString("description"));
                    } else if (result.getString("state").equals("-1")) {
                        ToastUtils.showToast(result.getString("description"));
                    } else if (result.getString("state").equals("-3")) {
                        new AlertDialog.Builder(mContext).setTitle("温馨提示").setMessage("您的余额不足，请充值后再约见！")
                                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        Intent intent = new Intent(mContext, RechargeActivity.class);
                                        intent.putExtra("UserId", mUserId);
                                        intent.putExtra("Money", mMoney);
                                        intent.putExtra("IsAnchor", true);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }

                                }).show();
                    } else {

                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }
}
