package com.android.common.utils;

public abstract class IOAuthCallBack {

	public abstract void onStartRequest();
	public abstract void getSuccess(String successJson);
	public abstract void getFailue(String failueJson);
}
