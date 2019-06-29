package com.android.nana.listener;

public class HomeListener {
	
	private static HomeListener mHomeListener;
	public OnHomeListener mOnHomeListener;
	
	public static HomeListener getInstance(){
		if (mHomeListener == null) {
			mHomeListener = new HomeListener();
		}
		return mHomeListener;
	}
	
	public interface OnHomeListener{
		void result(String text);
	}
}
