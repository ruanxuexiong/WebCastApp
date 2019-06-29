package com.android.nana.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public abstract class CountdownTimer {
	private static final int MSG_RUN = 1;
	private final long mCountdownInterval;
	private long mTotalTime;
	private long mRemainTime;

	public CountdownTimer(long millisInFuture, long countDownInterval) {
		mTotalTime = millisInFuture;
		mCountdownInterval = countDownInterval;
		mRemainTime = millisInFuture;
	}

	public final void cancel() {
		mHandler.removeMessages(MSG_RUN);
	}

	public final void resume() {
		mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(MSG_RUN));
	}

	public final void pause() {
		mHandler.removeMessages(MSG_RUN);
	}

	public synchronized final CountdownTimer start() {
		if (mRemainTime <= 0) {
			onFinish();
			return this;
		}
		mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_RUN), mCountdownInterval);
		return this;
	}

	public abstract void onTick(long millisUntilFinished, int percent);

	public abstract void onFinish();

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			synchronized (CountdownTimer.this) {
				if (msg.what == MSG_RUN) {
					mRemainTime = mRemainTime - mCountdownInterval;
					if (mRemainTime <= 0) {
						onFinish();
					} else if (mRemainTime < mCountdownInterval) {
						sendMessageDelayed(obtainMessage(MSG_RUN), mRemainTime);
					} else {

						onTick(mRemainTime,
								new Long(100 * (mTotalTime - mRemainTime)
										/ mTotalTime).intValue());

						sendMessageDelayed(obtainMessage(MSG_RUN), mCountdownInterval);
					}
				}
			}
		}
	};

}