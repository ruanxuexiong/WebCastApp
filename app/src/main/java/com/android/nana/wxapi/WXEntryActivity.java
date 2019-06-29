package com.android.nana.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.common.helper.UIHelper;
import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.eventBus.ShareMsgEvent;
import com.android.nana.listener.LoginListener;
import com.android.nana.util.Constant;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
    	api = WXAPIFactory.createWXAPI(this, Constant.Payment.WxPay.APP_ID);
		api.registerApp(Constant.Payment.WxPay.APP_ID);
        api.handleIntent(getIntent(), this);
        
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
        api.handleIntent(intent, this);
        
	}

	 @Override  
     public void onResp(BaseResp resp) {
		 
		 // 微信三方登录授权返回
		 if(resp instanceof SendAuth.Resp){
			SendAuth.Resp newResp = (SendAuth.Resp) resp;
			
			//获取微信传回的code
			String code = newResp.code;
			 if (TextUtils.isEmpty(code)) {
				 code = getIntent().getStringExtra("_wxapi_sendauth_resp_token");
			 }
			String path = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
					"appid="+Constant.Payment.WxPay.APP_ID+
					"&secret="+Constant.Payment.WxPay.APP_SECERT+
					"&code="+code+"&grant_type=authorization_code";
			
			HttpRequest.postUrl(path, null, new IOAuthCallBack() {

				@Override
				public void onStartRequest() {

				}

				@Override
				public void getSuccess(String successJson) {

//					{
//					    "access_token": "BX_DtkPD5fqW8dRWmeyU4Sg4vOQT2_NOVPsN3IP9vDweNhvrf2CMJadHOM3k6Pnhmw7VFYTbWL9Xy_1J5Ymdkf731D6tSWaEA2a5vsdwdrQ",
//					    "expires_in": 7200,
//					    "refresh_token": "OXN6s5l2gwAbjNLvCLD7oE3Q1hCjCrh3PljIwjB0YAOxXMZ2sp8tldEBuPYbzqI5-wit89BjhIKt-uFgJYwUjcHIrNZKZoN-Neyd3baTyyI",
//					    "openid": "oQEgPwHKjaBBrx7ttP5IDcDkv4i0",
//					    "scope": "snsapi_userinfo",
//					    "unionid": "ouIWjs6nngpMYFg0I8tFpGnNlsfs"
//					}

					String openid = "";
					String access_token = "";

					try {
						JSONObject obj = new JSONObject(successJson);

						openid = obj.optString("openid");
						access_token = obj.optString("access_token");

					} catch (Exception e) {
						e.printStackTrace();
					}

					String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"";

					HttpRequest.postUrl(url, null, new IOAuthCallBack() {

						@Override
						public void onStartRequest() {

						}

						@Override
						public void getSuccess(String successJson) {

//							{
//								"openid":"OPENID",
//								"nickname":"NICKNAME",
//								"sex":1,
//								"province":"PROVINCE",
//								"city":"CITY",
//								"country":"COUNTRY",
//								"headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
//								"privilege":[
//								"PRIVILEGE1",
//								"PRIVILEGE2"
//								],
//								"unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
//							}

							if (LoginListener.getInstance().mThirdBackListener != null) {
								LoginListener.getInstance().mThirdBackListener.onSuccess(successJson);
							}
							WXEntryActivity.this.finish();

						}

						@Override
						public void getFailue(String failueJson) {

							UIHelper.showToast(WXEntryActivity.this, "获取数据失败，请稍后重试");

							WXEntryActivity.this.finish();
						}

					});

				}

				@Override
				public void getFailue(String failueJson) {

					UIHelper.showToast(WXEntryActivity.this, "获取数据失败，请稍后重试");

					WXEntryActivity.this.finish();
				}
			});

		 } else {
			 
			 // 微信分享返回
			 switch (resp.errCode) {
			 case BaseResp.ErrCode.ERR_OK:
				 Toast.makeText(WXEntryActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
				 EventBus.getDefault().post(new ShareMsgEvent());
				 WXEntryActivity.this.finish();
				 break;
			 case BaseResp.ErrCode.ERR_USER_CANCEL:
				 Toast.makeText(WXEntryActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
				 WXEntryActivity.this.finish();
				 break;
			 default:
				 Toast.makeText(WXEntryActivity.this, "default", Toast.LENGTH_SHORT).show();
				 WXEntryActivity.this.finish();
				 break;
			 }
		 }
     }

	@Override
	public void onReq(BaseReq arg0) {
		
		
	}  
	
}