package com.android.common.models;

import com.android.common.utils.JSONUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class AdvertisementDisplayModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mPictureUrl;
	public String mLinkUrl;
	public int mPictureId;
	public boolean mIsLink;
	
	public static List<AdvertisementDisplayModel> getList(List<JSONObject> jsonObjects,
			String pathField,
			String urlField){
		
		List<AdvertisementDisplayModel> list = new ArrayList<AdvertisementDisplayModel>();
		
		for (JSONObject object : jsonObjects) {

			AdvertisementDisplayModel model = new AdvertisementDisplayModel();
			model.mPictureUrl = JSONUtil.getPictureUrl(object, pathField);
			model.mLinkUrl = JSONUtil.get(object, urlField, "");
			
			list.add(model);
		}
		
		return list;
	}
}
