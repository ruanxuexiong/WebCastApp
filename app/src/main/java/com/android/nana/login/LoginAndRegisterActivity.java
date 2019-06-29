package com.android.nana.login;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.common.base.BaseFragmentActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.nana.R;
import com.android.nana.listener.LoginListener;
import com.android.nana.login.thirdlogin.ThirdSinaLoginBuilder;
import com.android.nana.util.Constant;
import com.android.nana.util.SharedPreferencesUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.URLEncoder;

import cn.jpush.android.api.JPushInterface;

public class LoginAndRegisterActivity extends BaseFragmentActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private RadioGroup mTabRg;
    private ImageView mIvWx, mIvWb;

    private FragmentBuilder mFragmentBuilder;

    private IWXAPI mIWXAPI;
    private RadioButton mLogin, mRegister;

    /**
     * Sina登录
     */
    private ThirdSinaLoginBuilder mSinaLoginBuilder;
    private String mSinaAppKey = "3371819193"; // 3084348301
    private String mRedirectUrl = "https://sns.whalecloud.com/sina2/callback";
    private String mScope = "all";

    @Override
    protected void bindViews() {

        setContentView(R.layout.login_and_register);

    }

    @Override
    protected void locationData() {

        LoginListener.getInstance().mOnLoginListener = new LoginListener.OnLoginListener() {
            @Override
            public void login() {

                setResult(RESULT_OK);
                LoginAndRegisterActivity.this.finish();
            }

            @Override
            public void register() {

                ((RadioButton) mTabRg.getChildAt(0)).setChecked(true);
            }
        };
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("登录");

        mTabRg = (RadioGroup) findViewById(R.id.login_and_register_tab_rg_menu);

        mLogin = (RadioButton) findViewById(R.id.login_and_register_tab_rb_1);
        mRegister = (RadioButton) findViewById(R.id.login_and_register_tab_rb_2);
    }

    @Override
    protected void init() {

        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.Payment.WxPay.APP_ID);

        mSinaLoginBuilder = new ThirdSinaLoginBuilder(this, mSinaAppKey, mRedirectUrl, mScope);

    }

    @Override
    protected void initFragments() {

        if ("".equals(SharedPreferencesUtils.getParameter(LoginAndRegisterActivity.this, "isSuccess", ""))) {
            mFragmentBuilder = new FragmentBuilder(this, R.id.login_and_register_tab_content);
            mFragmentBuilder.registerFragement("登录", new LoginFragment());
            mLogin.setChecked(false);
            mRegister.setChecked(true);
            mFragmentBuilder.registerFragement("注册", new RegisterFragment());
            mFragmentBuilder.switchFragment(1);
        } else {
            mFragmentBuilder = new FragmentBuilder(this, R.id.login_and_register_tab_content);
            mLogin.setChecked(true);
            mRegister.setChecked(false);
            mFragmentBuilder.registerFragement("登录", new LoginFragment());
            mFragmentBuilder.registerFragement("注册", new RegisterFragment());
            mFragmentBuilder.switchFragment(0);
        }


    }

    @Override
    protected void setListener() {

        mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.login_and_register_tab_rb_1:
                        mFragmentBuilder.switchFragment(0);
                        mTxtTitle.setText("登录");
                        break;
                    case R.id.login_and_register_tab_rb_2:
                        mFragmentBuilder.switchFragment(1);
                        mTxtTitle.setText("注册");
                        break;
                }
            }
        });

        mIbBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LoginAndRegisterActivity.this.finish();

            }
        });

		/*mIvWb.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View v) {

				mSinaLoginBuilder.loginSina(new ThirdSinaLoginBuilder.LoginSinaBackListener() {

					@Override
					public void onWeiboException(WeiboException e) {

						UIHelper.showToast(LoginAndRegisterActivity.this,e.getMessage());

					}

					@Override
					public void onCancel() {

						UIHelper.showToast(LoginAndRegisterActivity.this,"取消授权");

					}

					@Override
					public void onSuccess(UserInfo user, String success) {

//		            	{
//	            	    	"id": 3750524940,
//	            	    	"idstr": "3750524940",
//	            	    	"class": 1,
//	            	    	"screen_name": "谜城丶",
//	            	    	"name": "谜城丶",
//	            	    	"province": "63",
//	            	    	"city": "21",
//	            	    	"location": "青海 海东",
//	            	    	"description": "",
//	            	    	"url": "",
//	            	    	"profile_image_url": "http://tva4.sinaimg.cn/crop.0.0.996.996.50/df8c780cjw8f0qilft8xlj20ro0ro40j.jpg",
//	            	    	"cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
//	            	    	"profile_url": "u/3750524940",
//	            	    	"domain": "",
//	            	    	"weihao": "",
//	            	    	"gender": "m",
//	            	    	"followers_count": 13,
//	            	    	"friends_count": 16,
//	            	    	"pagefriends_count": 0,
//	            	    	"statuses_count": 9,
//	            	    	"favourites_count": 1,
//	            	    	"created_at": "Tue Aug 27 09:01:16 +0800 2013",
//	            	    	"following": false,
//	            	    	"allow_all_act_msg": false,
//	            	    	"geo_enabled": true,
//	            	    	"verified": false,
//	            	    	"verified_type": -1,
//	            	    	"remark": "",
//	            	    	"status": {
//	            	        	"created_at": "Thu Sep 24 22:52:27 +0800 2015",
//	            	        	"id": 3890732379588203,
//	            	        	"mid": "3890732379588203",
//	            	        	"idstr": "3890732379588203",
//	            	        	"text": "这是轮行天下的一个测试分享内容。",
//	            	        	"source_allowclick": 0,
//	            	        	"source_type": 1,
//	            	        	"source": "<a href=\"http://app.weibo.com/t/feed/mBiqg\" rel=\"nofollow\">未通过审核应用</a>",
//	            	        	"favorited": false,
//	            	        	"truncated": false,
//	            	        	"in_reply_to_status_id": "",
//	            	        	"in_reply_to_user_id": "",
//	            	        	"in_reply_to_screen_name": "",
//	            	        	"pic_urls": [
//	            	            	{
//	            	                "thumbnail_pic": "http://ww3.sinaimg.cn/thumbnail/df8c780cjw1ewdxjwv1w3j207k07kjre.jpg"
//	            	            	}
//	            	        	],
//	            	        	"thumbnail_pic": "http://ww3.sinaimg.cn/thumbnail/df8c780cjw1ewdxjwv1w3j207k07kjre.jpg",
//	            	        	"bmiddle_pic": "http://ww3.sinaimg.cn/bmiddle/df8c780cjw1ewdxjwv1w3j207k07kjre.jpg",
//	            	        	"original_pic": "http://ww3.sinaimg.cn/large/df8c780cjw1ewdxjwv1w3j207k07kjre.jpg",
//	            	        	"geo": null,
//	            	        	"reposts_count": 0,
//	            	        	"comments_count": 0,
//	            	        	"attitudes_count": 0,
//	            	        	"isLongText": false,
//	            	        	"mlevel": 0,
//	            	        	"visible": {
//	            	            	"type": 0,
//	            	            	"list_id": 0
//	            	        	},
//	            	        	"biz_feature": 0,
//	            	        	"darwin_tags": [],
//	            	        	"hot_weibo_tags": [],
//	            	        	"text_tag_tips": [],
//	            	        	"userType": 0,
//	            	        	"positive_recom_flag": 0
//	            	    	},
//	            	    	"ptype": 0,
//	            	    	"allow_all_comment": true,
//	            	    	"avatar_large": "http://tva4.sinaimg.cn/crop.0.0.996.996.180/df8c780cjw8f0qilft8xlj20ro0ro40j.jpg",
//	            	    	"avatar_hd": "http://tva4.sinaimg.cn/crop.0.0.996.996.1024/df8c780cjw8f0qilft8xlj20ro0ro40j.jpg",
//	            	    	"verified_reason": "",
//	            	    	"verified_trade": "",
//	            	    	"verified_reason_url": "",
//	            	    	"verified_source": "",
//	            	    	"verified_source_url": "",
//	            	    	"follow_me": false,
//	            	    	"online_status": 0,
//	            	    	"bi_followers_count": 0,
//	            	    	"lang": "zh-cn",
//	            	    	"star": 0,
//	            	    	"mbtype": 0,
//	            	    	"mbrank": 0,
//	            	    	"block_word": 0,
//	            	    	"block_app": 0,
//	            	    	"credit_score": 80,
//	            	    	"user_ability": 0,
//	            	    	"urank": 9
//	            	    }

						doThirdLogin(success, "MICRO_BLOG");

					}

					@Override
					public void onError(String error) {

						UIHelper.showToast(LoginAndRegisterActivity.this,error);
					}
				});

			}
		});*/

	/*	mIvWx.setOnClickListener(new OnClickListener() {
            @Override
			public void onClick(View arg0) {

				SendAuth.Req req = new SendAuth.Req();
				//授权读取用户信息
				req.scope = "snsapi_userinfo";
				//自定义信息
				req.state = "wechat_sdk_demo_test";
				//向微信发送请求
				mIWXAPI.sendReq(req);

				LoginListener.getInstance().mThirdBackListener = new LoginListener.ThirdLoginBackListener() {
					@Override
					public void onSuccess(String result) {

//						{
//							"openid":"OPENID",
//							"nickname":"NICKNAME",
//							"sex":1,
//							"province":"PROVINCE",
//							"city":"CITY",
//							"country":"COUNTRY",
//							"headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
//							"privilege":[
//								"PRIVILEGE1",
//								"PRIVILEGE2"
//							],
//							"unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
//						}

						doThirdLogin(result, "WECHAT");

					}
				};

			}

		});*/

    }

    private void doThirdLogin(String success, String type) {

        try {
            final String json = URLEncoder.encode(success, "UTF-8");
//			json = Base64.encodeToString(success.getBytes(),Base64.DEFAULT);

            String registrationId = JPushInterface.getRegistrationID(this);

         /*   LoginDbHelper.login(type, json, registrationId, new IOAuthCallBack() {

                @Override
                public void onStartRequest() {

                    UIHelper.showOnLoadingDialog(LoginAndRegisterActivity.this);
                }

                @Override
                public void getSuccess(String successJson) {

                    SharedPreferencesUtils.setParameter(LoginAndRegisterActivity.this, "json", json);

                    LoginModel.doLogin(LoginAndRegisterActivity.this, successJson, "FacethreeZhibo2017");

                    UIHelper.hideOnLoadingDialog();
                }

                @Override
                public void getFailue(String failueJson) {

                    UIHelper.showToast(LoginAndRegisterActivity.this, "获取数据失败，请稍后重试");
                    UIHelper.hideOnLoadingDialog();

                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case ThirdSinaLoginBuilder.mRequestCode: // Sina 登录返回

                mSinaLoginBuilder.onActivityResult(requestCode, resultCode, data);

                break;
        }

    }

}
