package com.android.common.models;

import org.json.JSONObject;

import java.util.List;

public abstract class BaseModel {

	public String mFullResult;

	public boolean mIsSuccess;
	public String mMessage;
	public JSONObject mJsonData;
	public abstract List<JSONObject> ToList();

}
