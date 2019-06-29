package com.android.nana.listener;

public class WebCastListener {
	
	private static WebCastListener mWebCastListener;
	public OnWebCastListener mOnWebCastListener;
	
	public static WebCastListener getInstance(){
		if (mWebCastListener == null) {
			mWebCastListener = new WebCastListener();
		}
		return mWebCastListener;
	}
	
	public interface OnWebCastListener{
		void result(String text);
	}
}
