package com.android.nana.login.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public class ThirdSinaLoginBuilder {
	
	public final static int mRequestCode = 32973;
	
	private Context mContext;
	
	/** 授权认证所需要的信息 */
	private AuthInfo mAuthInfo;
	/** SSO 授权认证实例 */
    private SsoHandler mSsoHandler;
    /** 微博授权认证回调 */
    private AuthListener mAuthListener;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    
    private String mAppKey;
    
    private LoginSinaBackListener mBackListener;
	
	public ThirdSinaLoginBuilder(Context context,String appKey, String redirectUrl, String scope) {
		
		mContext = context;
		mAppKey = appKey;
		// 创建授权认证信息
		mAuthInfo = new AuthInfo(mContext, appKey, redirectUrl, scope);
		mAuthListener = new AuthListener();
		
	}
	
	public void loginSina(LoginSinaBackListener backListener){
		
		mBackListener = backListener;
		
		if (mSsoHandler == null && mAuthInfo != null) {
			mSsoHandler = new SsoHandler((Activity)mContext, mAuthInfo);
		}
		
        if (mSsoHandler != null) {
            mSsoHandler.authorize(mAuthListener);
        }
		
	}
	
	/**
     * 登入按钮的监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {

                AccessTokenKeeper.writeAccessToken(mContext, accessToken);
                
                // 获取当前已保存过的 Token
                mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
                // 获取用户信息接口
                mUsersAPI = new UsersAPI(mContext, mAppKey, mAccessToken);
                
                mUsersAPI.show(Long.parseLong(mAccessToken.getUid()), mListener);
            } else {
            	
            	if (mBackListener != null) {
            		mBackListener.onError("授权失败");
            	}
            	
            }
            
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	
        	if (mBackListener != null) {
        		mBackListener.onWeiboException(e);
			}
        	
        }

        @Override
        public void onCancel() {
        	
        	if (mBackListener != null) {
        		mBackListener.onCancel();
        	}
            
        }
    }
    
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                UserInfo user = UserInfo.parse(response);
                if (user != null) {
                    if (mBackListener != null) {
                    	mBackListener.onSuccess(user,response);
					}
                } else {
                	if (mBackListener != null) {
                		mBackListener.onError(response);
                	}
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	
        	if (mBackListener != null) {
        		mBackListener.onWeiboException(e);
			}
            
        }
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    
	public interface LoginSinaBackListener{
		
		void onSuccess(UserInfo user, String success);
		void onCancel();
		void onWeiboException(WeiboException e);
		void onError(String error);
		
	}
	
}
