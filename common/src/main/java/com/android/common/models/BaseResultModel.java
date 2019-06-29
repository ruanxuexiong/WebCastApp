package com.android.common.models;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class BaseResultModel {

    public boolean mIsSuccess;
    public String mMessage;
    public String mFullResult;
    public JSONObject mJsonData;
    public List<JSONObject> mList;

}
