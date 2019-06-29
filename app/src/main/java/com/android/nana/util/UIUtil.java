package com.android.nana.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.nana.WebCastApplication;

public class UIUtil {

	private static long lastClickTime;
	private static UIUtil mUiUtil;
	
	public static UIUtil getInstance(){
		
		if (mUiUtil == null) {
			mUiUtil = new UIUtil();
		}
		
		return mUiUtil;
	}
	
	public void setLayoutParams(View convertView, int width) {
		
		LayoutParams layoutParams = convertView.getLayoutParams();
		layoutParams.height = width;
		convertView.setLayoutParams(layoutParams);

	}

	public void setLayoutParams(View convertView, int width, boolean isWidth) {

		LayoutParams layoutParams = convertView.getLayoutParams();
		layoutParams.width = width;
		convertView.setLayoutParams(layoutParams);

	}

	public void setLayoutParams(View convertView, int width, int height) {
		
		LayoutParams layoutParams = convertView.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = height;
		convertView.setLayoutParams(layoutParams);
		
	}
	
	/**
	 * dip转为 px
	 */
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((dipValue * scale) + 0.5);
	}
	
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	
	public void hideSoftInputFromWindow(Activity mActivity, EditText mEditText) {
		
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);   
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(),0);  

	}

	/** 获取屏幕的宽度 */
	public int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static boolean isDoubleClick(int interval) {
		long currentClickTime = System.currentTimeMillis();
		if ((currentClickTime - lastClickTime) > interval) {
			lastClickTime = currentClickTime;
			return false;
		}
		return true;
	}

	public static Float dp2px(float dipValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, WebCastApplication.getInstance().getResources().getDisplayMetrics());
		return value;
	}



}
