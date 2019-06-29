package com.android.common.base;

import org.json.JSONObject;

import android.app.Activity;

public abstract class BaseJsonAdapter<TViewHolder> extends BaseListAdapter<JSONObject, TViewHolder>{

	public BaseJsonAdapter(Activity context) {
		super(context);
	}

}
