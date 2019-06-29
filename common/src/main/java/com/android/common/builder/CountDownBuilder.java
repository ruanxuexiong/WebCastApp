package com.android.common.builder;

import android.os.Handler;
import android.os.Message;

public class CountDownBuilder {
	
	private int mTimeRunSecond = 60;
	private OnCountRunning mOnCountRunning;
	
	public CountDownBuilder(OnCountRunning onCountRunning){
		mOnCountRunning = onCountRunning;
	}
	
	public void Run(int second){
		mTimeRunSecond = second;
		mtimRunable.run();
	}
	
	private timeRunable mtimRunable = new timeRunable();
	private class timeRunable implements Runnable {

		@Override
		public void run() {
			if (mTimeRunSecond > 0) {
				mTimeHandler.sendEmptyMessage(0);
				mOnCountRunning.onRuning(mTimeRunSecond);
			} else {
				mOnCountRunning.onFinished();
			}
		}
	};
	
	Handler mTimeHandler = new Handler() {
		public void handleMessage(Message msg) {
			mTimeRunSecond--;
			mTimeHandler.postDelayed(mtimRunable, 1000);
		}
	};

	public interface OnCountRunning{
		void onRuning(int second);
		void onFinished();
	}

}
