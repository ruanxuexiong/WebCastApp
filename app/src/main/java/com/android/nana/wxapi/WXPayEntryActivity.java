package com.android.nana.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.common.helper.UIHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.util.Constant;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    public static String WXPaySuccess = "支付成功";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.Payment.WxPay.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("返回zhi", req.openId);
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
        }
        if (resp.errCode == 0) {//支付成功
            UIHelper.showToast(WXPayEntryActivity.this, WXPaySuccess);
            EventBus.getDefault().post(new MessageEvent(""));
        } else if (resp.errCode == -1) {
            UIHelper.showToast(WXPayEntryActivity.this, "支付失败");
        } else if (resp.errCode == -2) {
            UIHelper.showToast(WXPayEntryActivity.this, "用户取消");
        }
        WXPayEntryActivity.this.finish();
    }
}