package com.android.nana.user;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;

import java.util.List;

/**
 * Created by lenovo on 2017/9/8.
 */

public class JsonMananger {

    static {
        TypeUtils.compatibleWithJavaBean = true;
    }
    private static final String tag = JsonMananger.class.getSimpleName();

    /**
     * 将json字符串转换成java对象
     * @param json
     * @param cls
     * @return
     * @throws HttpException
     */
    public static <T> T jsonToBean(String json, Class<T> cls) throws HttpException {
        return JSON.parseObject(json, cls);
    }

    /**
     * 将json字符串转换成java List对象
     * @param json
     * @param cls
     * @return
     * @throws HttpException
     */
    public static <T> List<T> jsonToList(String json, Class<T> cls) throws HttpException {
        return JSON.parseArray(json, cls);
    }

    /**
     * 将bean对象转化成json字符串
     * @param obj
     * @return
     * @throws HttpException
     */
    public static String beanToJson(Object obj) throws HttpException {
        String result = JSON.toJSONString(obj);
        Log.e(tag, "beanToJson: " + result);
        return result;
    }
}
