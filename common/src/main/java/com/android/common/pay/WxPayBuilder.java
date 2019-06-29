package com.android.common.pay;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.lidroid.xutils.http.RequestParams;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WxPayBuilder {
	
	private Context mContext;
	private IWXAPI mApi;
	private String mAppId;
	private String mSerialNumber;

	private String mUrl = "http://17wscz.com/Tenpay/GetPrepay";
	
	public WxPayBuilder(Context context) {
		
		mContext = context;

	}
	
	public void registerAliConfig(String appId, String serialNumber) {
		mAppId = appId;
		mSerialNumber = serialNumber;
		
	}

	public void build(){

		mApi = WXAPIFactory.createWXAPI(mContext, mAppId);

	}
	
	public void pay() {
		if (mApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
					
			RequestParams requestParams = new RequestParams();
			requestParams.addBodyParameter("SerialNumber", mSerialNumber);
			
			HttpRequest.postUrl(mUrl, requestParams, new IOAuthCallBack() {
				
				@Override
				public void onStartRequest() {
					
					showProgress("正在获取数据...");
					
				}
				
				@Override
				public void getSuccess(String successJson) {
					
					JSONObject obj = JSONUtil.getStringToJson(successJson);
					int IsSuccess = obj.optInt("IsSuccess");
					if (IsSuccess == 1) {
						JSONObject ob = obj.optJSONObject("Result");
						PayReq req = new PayReq();
						req.appId			= JSONUtil.get(ob, "AppId", "");
						req.partnerId		= JSONUtil.get(ob, "MechId", "");
						req.prepayId		= JSONUtil.get(ob, "PrePayId", "");
						req.nonceStr		= JSONUtil.get(ob, "NonceStr", "");
						req.timeStamp		= JSONUtil.get(ob, "TimeStamp", "");
//						req.packageValue	= JSONUtil.get(ob, "Package", "");
						req.packageValue	= "Sign=WXPay";
						req.sign			= JSONUtil.get(ob, "PaySign", "");
						req.extData			= "app data"; // optional
						// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
						mApi.registerApp(req.appId);
						mApi.sendReq(req);
						
					}
					
					hideProgress();
				}
				
				@Override
				public void getFailue(String failueJson) {
					
					hideProgress();
				}
			});
					
		} else {
        	Toast.makeText(mContext, "微信版本过低，暂不支持支付功能", Toast.LENGTH_SHORT).show();
		}
	}
	
	public ProgressDialog mProgressDialog = null;

	public void showProgress(String title) {
		if (mProgressDialog != null) {
			mProgressDialog.show();
		}

		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(mContext, null, title);
		}
	}

	public void hideProgress() {

		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}

}
