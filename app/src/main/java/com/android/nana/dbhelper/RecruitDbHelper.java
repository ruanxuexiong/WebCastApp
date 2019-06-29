package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;
import java.util.List;

/**
 * Created by lenovo on 2018/3/23.
 */

public class RecruitDbHelper {

    /**
     * 牛人列表
     */
    public static void beList(String mid, String page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", page);
        HttpRequest.post("JobBoss/index", requestParams, callBack);
    }

    /**
     * 公司规模
     */
    public static void Companyscale(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("JobPosition/Companyscale", requestParams, callBack);
    }

    /**
     * 融资阶段
     */
    public static void CompanyFinace(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("JobPosition/CompanyFinace", requestParams, callBack);
    }

    /**
     * 公司主页
     */
    public static void UpdateCompany(String mid, String professoinId, String scaleId, String finaceId, String website, String introduction, String name, File logo, List<File> list, String count, String companyId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("professoinId", professoinId);
        requestParams.addBodyParameter("scaleId", scaleId);
        requestParams.addBodyParameter("finaceId", finaceId);
        requestParams.addBodyParameter("website", website);
        requestParams.addBodyParameter("introduction", introduction);
        requestParams.addBodyParameter("name", name);
        requestParams.addBodyParameter("logo", logo);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                requestParams.addBodyParameter("pic" + (i + 1), list.get(i));
            }
        } else {
            requestParams.addBodyParameter("pic", "");
        }
        requestParams.addBodyParameter("count", count);
        requestParams.addBodyParameter("companyId", companyId);
        HttpRequest.post("JobBoss/updateCompany", requestParams, callBack);
    }

    /**
     * 公司认证
     */
    public static void authCompany(String mid, String companyId, File authertication, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("companyId", companyId);
        requestParams.addBodyParameter("authertication", authertication);
        HttpRequest.post("JobBoss/authCompany", requestParams, callBack);
    }

    /**
     * 我的招聘
     */
    public static void mySelf(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("JobBoss/myself", requestParams, callBack);
    }

    /**
     * 公司信息页
     */
    public static void companyInfo(String mid, String companyId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("companyId", companyId);
        HttpRequest.post("JobBoss/companyInfo", requestParams, callBack);
    }

    /**
     * 发布职位
     */
    public static void publish(String mid, String companyId, String positionId, String job, String provinceId, String cityId, String areaId, String address, String nature, String range, String experience, String edu, String desc, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("companyId", companyId);
        requestParams.addBodyParameter("positionId", positionId);
        requestParams.addBodyParameter("job", job);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("areaId", areaId);
        requestParams.addBodyParameter("address", address);
        requestParams.addBodyParameter("nature", nature);
        requestParams.addBodyParameter("range", range);
        requestParams.addBodyParameter("experience", experience);
        requestParams.addBodyParameter("edu", edu);
        requestParams.addBodyParameter("desc", desc);
        HttpRequest.post("JobBoss/addPosition", requestParams, callBack);
    }

    /**
     * 管理招聘职位
     */
    public static void positionList(String mid, String companyId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("companyId", companyId);
        HttpRequest.post("JobBoss/positionList", requestParams, callBack);
    }
    /**
     * 企业申请提交审核
     */
    public static void postApplyCompany(String phone, String userId,String code, String company,String credit_code,File pic_1,File pic_2, String time,String sign, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("code", code);
        requestParams.addBodyParameter("company", company);
        requestParams.addBodyParameter("credit_code", credit_code);
        requestParams.addBodyParameter("pic_1", pic_1);
        requestParams.addBodyParameter("pic_2", pic_2);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("sign", sign);
        HttpRequest.post("AuthFirm/doApplyCompany", requestParams, callBack);
    }
    /**
     * 企业发送短信验证码
     */
    public static void sendCompanyCode(String userId,String phone, String time,String sign, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("sign", sign);
        HttpRequest.post("AuthFirm/sendCode", requestParams, callBack);
    }
    /**
     * 获取企业认证信息
     */
    public static void getApplyCompany(String userId, String time,String sign, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("sign", sign);
        HttpRequest.post("AuthFirm/getApplyCompany", requestParams, callBack);
    }
    /**
     * 编辑公司主页
     */

}
