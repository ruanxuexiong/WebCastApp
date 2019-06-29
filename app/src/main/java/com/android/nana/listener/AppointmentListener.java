package com.android.nana.listener;

public class AppointmentListener {
	
	private static AppointmentListener mAppointmentListener;
	public OnAppointmentListener mOnBeingAppointmentListener;
	public OnAppointmentListener mOnSuccessAppointmentListener;
	public OnDialVideoListener mOnDialVideoListener;
	public OnRefershListener mOnRefershListener;

	public static AppointmentListener getInstance(){
		if (mAppointmentListener == null) {
			mAppointmentListener = new AppointmentListener();
		}
		return mAppointmentListener;
	}
	
	public interface OnAppointmentListener{
		void callBack();
	}

	public interface OnDialVideoListener{
		void refersh();
	}

	public interface OnRefershListener{
		void refersh();
	}

}
