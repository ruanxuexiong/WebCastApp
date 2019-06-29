package com.android.common.pay;


import android.app.Activity;

public class AliPayBuilder {

	private Activity mActivity;
	private boolean isExist;

	/**
	 * 签约合作者身份ID
	 */
	protected String mPartner;
	/**
	 * 签约卖家支付宝账号
	 */
	protected String mSeller;
	/**
	 * 私钥
	 */
	protected String mRsaPrivate;
	/**
	 * 商品名
	 */
	protected String mSubject;
	/**
	 * 商品ID
	 */
	protected String mOrderId;
	/**
	 * 商品描述
	 */
	protected String mBody;
	/**
	 * 商品价格
	 */
	protected String mPrice;
	/**
	 * 通知回调地址
	 */
	protected String mNotifyUrl = "http://notify.msp.hk/notify.htm";

	public AliPayBuilder(Activity activity) {
		this.mActivity = activity;
	}
	
	public AliPayBuilder registerAliConfig(String partner, String seller, String rsaPriver) {
		this.mPartner = partner;
		this.mSeller = seller;
		this.mRsaPrivate = rsaPriver;
		return this;
	}

//	public AliPayBuilder registerOrderInfo(String subject,String orderId, String body, String price) {
//		this.mSubject = subject;
//		this.mOrderId = orderId;
//		this.mBody = body;
//		this.mPrice = price;
//		return this;
//	}
//
//	public AliPayBuilder registerNotifyUrl(String notifyUrl) {
//		this.mNotifyUrl = notifyUrl;
//		return this;
//	}
//
//	public void build(){
//
//		check();
//	}
//
//	public void doPay(){
//		if (isExist) {
//			pay();
//		} else {
//			UIHelper.showToast(mActivity, "检测到终端设备不存在支付宝认证账户");
//		}
//	}
//
//	// 支付宝公钥
////	public static final String RSA_PUBLIC = "";
//	private static final int SDK_PAY_FLAG = 1;
//	private static final int SDK_CHECK_FLAG = 2;
//
//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case SDK_PAY_FLAG: {
//					PayResult payResult = new PayResult((String) msg.obj);
//					/**
//					 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
//					 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
//					 * docType=1) 建议商户依赖异步通知
//					 */
//					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//
//					String resultStatus = payResult.getResultStatus();
//					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//					if (TextUtils.equals(resultStatus, "9000")) {
//						UIHelper.showToast(mActivity, "支付成功");
//					} else {
//						// 判断resultStatus 为非"9000"则代表可能支付失败
//						// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//						if (TextUtils.equals(resultStatus, "8000")) {
//
//							UIHelper.showToast(mActivity, "支付结果确认中");
//
//						} else {
//							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//							UIHelper.showToast(mActivity, "支付失败");
//						}
//					}
//					break;
//				}
//				case SDK_CHECK_FLAG: {
//					// Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
//					break;
//				}
//				case 999:
//					UIHelper.showToast(mActivity, "检测到终端设备不存在支付宝认证账户");
//					break;
//			}
//		};
//	};
//
//	/**
//	 * call alipay sdk pay. 调用SDK支付
//	 *
//	 */
//	public void pay() {
//		if (TextUtils.isEmpty(mPartner) || TextUtils.isEmpty(mRsaPrivate) || TextUtils.isEmpty(mSeller)) {
//			new AlertDialog.Builder(mActivity).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialoginterface, int i) {
//
//						}
//					}).show();
//			return;
//		}
//		String orderInfo = getOrderInfo(mSubject, mOrderId, mBody, mPrice, mNotifyUrl);
//
//		/**
//		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
//		 */
//		String sign = sign(orderInfo);
//		try {
//			/**
//			 * 仅需对sign 做URL编码
//			 */
//			sign = URLEncoder.encode(sign, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		/**
//		 * 完整的符合支付宝参数规范的订单信息
//		 */
//		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//
//		Runnable payRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask alipay = new PayTask(mActivity);
//				// 调用支付接口，获取支付结果
//				String result = alipay.pay(payInfo, true);
//
//				Message msg = new Message();
//				msg.what = SDK_PAY_FLAG;
//				msg.obj = result;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		// 必须异步调用
//		Thread payThread = new Thread(payRunnable);
//		payThread.start();
//	}
//
//	/**
//	 * check whether the device has authentication alipay account.
//	 * 查询终端设备是否存在支付宝认证账户
//	 *
//	 */
//	public void check() {
//		Runnable checkRunnable = new Runnable() {
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(mActivity);
//				// 调用查询接口，获取查询结果
//				boolean isExist = payTask.checkAccountIfExist();
//
//				AliPayBuilder.this.isExist = isExist;
//				mHandler.sendEmptyMessage(999);
//
////				Message msg = new Message();
////				msg.what = SDK_CHECK_FLAG;
////				msg.obj = isExist;
////				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//	}
//
//	/**
//	 * get the sdk version. 获取SDK版本号
//	 *
//	 */
//	public void getSDKVersion() {
//		PayTask payTask = new PayTask(mActivity);
//		String version = payTask.getVersion();
//		UIHelper.showToast(mActivity, version);
//	}
//
//	/**
//	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
//	 *
//	 */
//	public void h5Pay() {
//		Intent intent = new Intent(mActivity, H5PayDemoActivity.class);
//		Bundle extras = new Bundle();
//		/**
//		 * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
//		 * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
//		 * 商户可以根据自己的需求来实现
//		 */
//		String url = "http://m.taobao.com";
//		// url可以是一号店或者美团等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
//		extras.putString("url", url);
//		intent.putExtras(extras);
//		mActivity.startActivity(intent);
//
//	}
//
//	/**
//	 * create the order info. 创建订单信息
//	 *
//	 */
//	private String getOrderInfo(String subject, String orderId, String body, String price, String notify_url) {
//
//		// 签约合作者身份ID
//		String orderInfo = "partner=" + "\"" + mPartner + "\"";
//
//		// 签约卖家支付宝账号
//		orderInfo += "&seller_id=" + "\"" + mSeller + "\"";
//
//		// 商户网站唯一订单号
//		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";
//
//		// 商品名称
//		orderInfo += "&subject=" + "\"" + subject + "\"";
//
//		// 商品详情
//		orderInfo += "&body=" + "\"" + body + "\"";
//
//		// 商品金额
//		orderInfo += "&total_fee=" + "\"" + price + "\"";
//
//		// 服务器异步通知页面路径
//		orderInfo += "&notify_url=" + "\"" + notify_url + "\"";
//
//		// 服务接口名称， 固定值
//		orderInfo += "&service=\"mobile.securitypay.pay\"";
//
//		// 支付类型， 固定值
//		orderInfo += "&payment_type=\"1\"";
//
//		// 参数编码， 固定值
//		orderInfo += "&_input_charset=\"utf-8\"";
//
//		// 设置未付款交易的超时时间
//		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
//		// 取值范围：1m～15d。
//		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
//		// 该参数数值不接受小数点，如1.5h，可转换为90m。
//		orderInfo += "&it_b_pay=\"30m\"";
//
//		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
//		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
//
//		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//		orderInfo += "&return_url=\"m.alipay.com\"";
//
//		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//		// orderInfo += "&paymethod=\"expressGateway\"";
//
//		return orderInfo;
//	}
//
//	/**
//	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
//	 *
//	 */
//	private String getOutTradeNo() {
//		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
//		Date date = new Date();
//		String key = format.format(date);
//
//		Random r = new Random();
//		key = key + r.nextInt();
//		key = key.substring(0, 15);
//		return key;
//	}
//
//	/**
//	 * sign the order info. 对订单信息进行签名
//	 *
//	 * @param content
//	 *            待签名订单信息
//	 */
//	private String sign(String content) {
//		return SignUtils.sign(content, mRsaPrivate);
//	}
//
//	/**
//	 * get the sign type we use. 获取签名方式
//	 *
//	 */
//	private String getSignType() {
//		return "sign_type=\"RSA\"";
//	}

}
