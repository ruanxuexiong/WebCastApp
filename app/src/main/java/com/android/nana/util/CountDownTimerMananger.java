package com.android.nana.util;

import android.widget.TextView;

/**
 * 实现计时功能的类
 * @author shen
 *
 */
public class CountDownTimerMananger extends CountdownTimer {

	private TextView day;
	private TextView hour;
	private TextView minute;
	private TextView second;
	
	public void set(TextView day,TextView hour, TextView min, TextView sed) {
		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sed;
	}
	
	public void set(TextView sed) {
		this.second = sed;
	}
	
	public CountDownTimerMananger(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		if (day != null) {
			day.setText("00");
		}
		if (hour != null) {
			hour.setText("00");
		}
		if (minute != null) {
			minute.setText("00");
		}
		if (second != null) {
			second.setText("00");
		}
		if (mCountDownTimerFinishListener != null) {
			mCountDownTimerFinishListener.onFinish();
		}
	}
	
	// 更新剩余时间
	@Override
	public void onTick(long millisUntilFinished, int percent) {
		long myday = (millisUntilFinished / 1000) / 3600 / 24;
		long myhour = (millisUntilFinished / 1000 - myday * 3600 * 24) / 3600;
		long myminute = ((millisUntilFinished / 1000) - myday * 3600 * 24 - myhour * 3600) / 60;
		long mysecond = millisUntilFinished / 1000 - myday * 3600 * 24 - myhour * 3600 - myminute * 60;
		if (day != null) {
			day.setText(String.format("%02d", myday));
		}
		if (hour != null) {
			hour.setText(String.format("%02d", myhour));
		}
		if (minute != null) {
			minute.setText(String.format("%02d", myminute));
		}
		if (second != null) {
			second.setText(String.format("%02d", mysecond));
		}
		if (mCountDownTimerFinishListener != null) {
			mCountDownTimerFinishListener.onCountdown(millisUntilFinished);
		}
		
	}
	
	private OnCountDownTimerListener mCountDownTimerFinishListener;
	
	public void setOnCountDownTimerListener(OnCountDownTimerListener l){
		mCountDownTimerFinishListener = l;
	}
	
	public interface OnCountDownTimerListener {
		void onFinish();
		void onCountdown(long timeCountdown);
	}
	
}