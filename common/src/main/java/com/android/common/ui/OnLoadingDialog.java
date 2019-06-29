package com.android.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.common.R;

public class OnLoadingDialog extends Dialog {

	public OnLoadingDialog(Context context, int theme) {
		super(context, theme);
		View view = LayoutInflater.from(context).inflate(R.layout.onload_layout,null);
		
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		this.setContentView(view);
		this.show();
	}
	
	public OnLoadingDialog(Context context, String text, int theme) {
		super(context, theme);
		View view = LayoutInflater.from(context).inflate(R.layout.onload_layout,null);
		TextView mTxtStr = (TextView) view.findViewById(R.id.onload_txt_str);
		mTxtStr.setText(text);
		
		this.setCanceledOnTouchOutside(true);
		this.setCancelable(true);
		this.setContentView(view);
		this.show();
	}

}