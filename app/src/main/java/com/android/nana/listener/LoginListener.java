package com.android.nana.listener;

public class LoginListener {
	
	private static LoginListener mLoginListener;
	public OnLoginListener mOnLoginListener;
	public OnMainListener mOnMainListener;
	public ThirdLoginBackListener mThirdBackListener;
	
	public static LoginListener getInstance(){
		if (mLoginListener == null) {
			mLoginListener = new LoginListener();
		}
		return mLoginListener;
	}
	
	public interface OnLoginListener{
		void login();
		void register();
	}
	
	public interface OnMainListener{
		void result();
	}

	public interface ThirdLoginBackListener{
		void onSuccess(String result);
	}

}
