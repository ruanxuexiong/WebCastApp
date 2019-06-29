package com.android.nana.listener;

public class DynamicsListener {
	
	private static DynamicsListener mDynamicsListener;
	public OnDynamicsListener mOnDynamicsListener;
	
	public static DynamicsListener getInstance(){
		if (mDynamicsListener == null) {
			mDynamicsListener = new DynamicsListener();
		}
		return mDynamicsListener;
	}
	
	public interface OnDynamicsListener{
		void result(String t, String s);
	}
}
