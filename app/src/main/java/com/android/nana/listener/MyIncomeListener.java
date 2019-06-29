package com.android.nana.listener;

public class MyIncomeListener {
	
	private static MyIncomeListener mMyIncomeListener;
	public OnMyIncomeListener mOnMyIncomeListener;
	
	public static MyIncomeListener getInstance(){
		if (mMyIncomeListener == null) {
			mMyIncomeListener = new MyIncomeListener();
		}
		return mMyIncomeListener;
	}
	
	public interface OnMyIncomeListener{
		void result(String t, String s);
	}
}
