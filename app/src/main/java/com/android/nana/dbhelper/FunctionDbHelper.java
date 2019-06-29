package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

/**
 * Created by lenovo on 2017/12/29.
 */

public class FunctionDbHelper {

    //选择职能标签
    public static void shoiceFunctionLabel(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Profession/choosePerfession", requestParams, callBack);
    }

    //更多标签
    public static void moreProfession(String parentid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("parentid", parentid);
        HttpRequest.post("Profession/moreProfession", requestParams, callBack);
    }

    //找人导航
    public static void navgation(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Search/navgation", requestParams, callBack);
    }

    //全部城市
    public static void getCitys(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Region/Citys", requestParams, callBack);
    }


    /**
     * 关键字搜索
     *
     * @param keyword    关键字
     * @param mid        用户ID
     * @param cityId     城市ID
     * @param propertyId 职能ID
     * @param page       页码
     * @param callBack   回调
     */
    public static void searchUserbyKeyword(String keyword, String mid, String cityId, String provinceId,String propertyId,String parPropertyId, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("keyword", keyword);
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("parPropertyId", propertyId);//一级
        requestParams.addBodyParameter("propertyId", parPropertyId);//二级行业
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/searchUserbyKeyword", requestParams, callBack);
    }

    //全部行业
    public static void selectProfession(IOAuthCallBack callBack){
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Profession/selectProfession", requestParams, callBack);
    }

    //高级搜索
    public static void advanceSearch(String mid,String sex,String position,String cityId,String provinceId,String school,String propertyId,String parPropertyId,int page,IOAuthCallBack callBack){
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("sex", sex);
        requestParams.addBodyParameter("position", position);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("school", school);
        requestParams.addBodyParameter("propertyId",parPropertyId );//二级ID
        requestParams.addBodyParameter("parPropertyId", propertyId);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/advanceSearch", requestParams, callBack);
    }

    //保存职能标签
    public static void updateProfession(String mid,String parentid,String id,IOAuthCallBack callBack){
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("parentid", parentid);
        HttpRequest.post("Profession/updateProfession", requestParams, callBack);
    }

    //删除职能标签
    public static void delProfession(String mid,IOAuthCallBack callBack){
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Profession/delProfession", requestParams, callBack);
    }
}
