package com.android.nana.red;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

/**
 * Created by lenovo on 2018/11/6.
 */

public class RedHttps {

    /**
     * 发送红包是否余额
     */
    public static void checkIsEnough(String mid, String money,IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("money", money);
        HttpRequest.post("Money/checkIsEnough", requestParams, callBack);
    }
}
