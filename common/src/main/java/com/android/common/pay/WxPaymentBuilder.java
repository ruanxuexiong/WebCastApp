package com.android.common.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.android.common.pay.wx.MD5;
import com.android.common.pay.wx.Utils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WxPaymentBuilder {

	private Activity mActivity;
	private IWXAPI mMsgApi;
	private PayReq mPayReq;
	private Map<String, String> resultunifiedorder;

	private int mFeePrice;
	private String mTransactionNumber,mOrderDescription;
	private String mNotifyUrl;
	private String mAppId,mAppKey,mMchId;

	public WxPaymentBuilder(Activity activity) {

		mActivity = activity;
		mMsgApi = WXAPIFactory.createWXAPI(activity, null);

		mPayReq = new PayReq();

		resultunifiedorder = new HashMap<>();
	}

	public WxPaymentBuilder registerPrice(int feePrice){

		mFeePrice = feePrice;
		return this;
	}

	public WxPaymentBuilder registerTransactionNumber(String transactionNumber){
		mTransactionNumber = transactionNumber;
		return this;
	}

	public WxPaymentBuilder registerNotifyUrl(String notifyUrl){
		mNotifyUrl = notifyUrl;
		return this;
	}

	public WxPaymentBuilder registerAppInfo(String appId,String appKey,String mchId){
		mAppId = appId;
		mAppKey = appKey;
		mMchId = mchId;
		return this;
	}

	public WxPaymentBuilder registerOrderDescription(String orderDescription){
		mOrderDescription = orderDescription;
		return this;
	}

	public void build(){

		mMsgApi.registerApp(mAppId);

	}

	public void doPay(){

		new GetPrepayIdTask().execute();

	}

	public void sendPayReq(){
		mMsgApi.registerApp(mAppId);
		mMsgApi.sendReq(mPayReq);
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	@SuppressWarnings("deprecation")
	private String genProductArgs() {

		try {
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", mAppId));
			packageParams.add(new BasicNameValuePair("body", mOrderDescription));
			packageParams.add(new BasicNameValuePair("mch_id", mMchId));
			packageParams.add(new BasicNameValuePair("nonce_str", genNonceStr()));
			packageParams.add(new BasicNameValuePair("notify_url",mNotifyUrl));
			packageParams.add(new BasicNameValuePair("out_trade_no",mTransactionNumber));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",getIp()));
			packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(mFeePrice)));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlString = toXml(packageParams);
			return new String(xmlString.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	private void genPayReq() {

		mPayReq.appId = mAppId;
		mPayReq.partnerId = mMchId;
		mPayReq.prepayId = resultunifiedorder.get("prepay_id");
		mPayReq.packageValue = "Sign=WXPay";
//		mPayReq.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
		mPayReq.nonceStr = genNonceStr();
		mPayReq.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", mPayReq.appId));
		signParams.add(new BasicNameValuePair("noncestr", mPayReq.nonceStr));
		signParams.add(new BasicNameValuePair("package", mPayReq.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", mPayReq.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", mPayReq.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", mPayReq.timeStamp));

		String sign = genAppSign(signParams);
		mPayReq.sign = sign;

	}

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(mAppKey);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(mAppKey);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}
			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String getIp(){
		return "127.0.0.1";
	}

	private static String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(mActivity, "加载中", "正在生成...");
		}

		@Override
		protected void onPostExecute(Map<String,String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}

			resultunifiedorder = result;

			Log.i("tag", ""+result);

			genPayReq();

			sendPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String>  doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			byte[] buf = Utils.httpPost(url, entity);

			String content = new String(buf);
			Map<String,String> xml=decodeXml(content);

			return xml;
		}
	}

}
