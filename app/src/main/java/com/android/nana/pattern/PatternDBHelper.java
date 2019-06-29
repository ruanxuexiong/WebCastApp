package com.android.nana.pattern;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

/**
 * Created by lenovo on 2017/12/8.
 */

public class PatternDBHelper {


    /**
     * 开启模式
     *
     * @param mid
     * @param type
     * @param money
     * @param callBack
     */
    public static void open(String mid, String type, String money, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("money", money);
        HttpRequest.post("OpenFace/open", requestParams, callBack);
    }

    /**
     * 开启模式
     *
     * @param mid
     * @param callBack
     */
    public static void openDetail(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("OpenFace/openDetail", requestParams, callBack);
    }

    /**
     * 关闭模式
     * @param mid
     * @param callBack
     */
    public static void close(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("OpenFace/close", requestParams, callBack);
    }

}
