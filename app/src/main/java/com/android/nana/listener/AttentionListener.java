package com.android.nana.listener;

public class AttentionListener {
	
	private static AttentionListener mAttentionListener;
	public OnAttentionListener mOnAttentionListener;
	
	public static AttentionListener getInstance(){
		if (mAttentionListener == null) {
			mAttentionListener = new AttentionListener();
		}
		return mAttentionListener;
	}
	
	public interface OnAttentionListener{
		void result(String t, String s);
	}
}
