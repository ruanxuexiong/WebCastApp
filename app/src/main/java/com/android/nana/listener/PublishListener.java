package com.android.nana.listener;

import android.content.Intent;

public class PublishListener {
	
	private static PublishListener mPublishListener;
	public OnCheckListener mOnCheckListener;
	public OnRefreshListener mOnRefreshListener;
	public OnBackListener mOnBackListener;

	public static PublishListener getInstance(){
		if (mPublishListener == null) {
			mPublishListener = new PublishListener();
		}
		return mPublishListener;
	}
	
	public interface OnCheckListener{
		void checked();
	}

	public interface OnRefreshListener{
		void refresh();
	}

	public interface OnBackListener{
		void back(int requestCode, Intent data);
	}
}
