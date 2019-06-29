package com.android.common.models;

import com.android.common.BaseApplication;

import org.json.JSONObject;

import java.util.List;

public class ResultRequestListModel extends BaseModel {
	
	private BaseResultModel mBaseResultModel;

	public ResultRequestListModel() {
		
	}
	
	public ResultRequestListModel(String json){

		mBaseResultModel = BaseApplication.getInstance().getResultRequestListModel(json);

		mIsSuccess = mBaseResultModel.mIsSuccess;
	}
	
	public List<JSONObject> ToList(){

		return mBaseResultModel.mList;

	}
}
