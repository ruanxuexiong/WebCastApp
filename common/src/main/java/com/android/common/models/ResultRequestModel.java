package com.android.common.models;

import com.android.common.BaseApplication;
import com.android.common.utils.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ResultRequestModel extends BaseModel {
	
	private BaseResultModel mBaseResultModel;

	public ResultRequestModel() {
		
	}
	
	public ResultRequestModel(String json){

		mBaseResultModel = BaseApplication.getInstance().getResultRequestModel(json);

		mFullResult = mBaseResultModel.mFullResult;
		mIsSuccess = mBaseResultModel.mIsSuccess;
		mMessage = mBaseResultModel.mMessage;
		mJsonData = mBaseResultModel.mJsonData;

	}

	@Override
	public List<JSONObject> ToList() {
		return null;
	}
}
