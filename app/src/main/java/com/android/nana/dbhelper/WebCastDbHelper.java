package com.android.nana.dbhelper;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;

public class WebCastDbHelper {

    public static void lists(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Column/lists", requestParams, callBack);
    }

    public static void getUserInfo(String uid, String thisId, String meetStatus, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", uid);
        requestParams.addBodyParameter("mid", thisId);
        requestParams.addBodyParameter("meetStatus", meetStatus);
        HttpRequest.post("User/userInfo", requestParams, callBack);
    }

    //发起咨询
    public static void getStart(String mid, String adUid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("adUid", adUid);
        HttpRequest.post("Advisory/start", requestParams, callBack);
    }

    //获取个人数据
    public static void getMyUserInfo(String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", uid);
        HttpRequest.post("User/myUserInfo", requestParams, callBack);
    }

    /**
     * 获取职位列表
     */
    public static void getJobsList(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();// JobPosition/positions
        HttpRequest.post("UserJob/jobs", requestParams, callBack);
    }

    //获取行业标签
    public static void getProfessions(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.get("JobPosition/professons", requestParams, callBack);
    }

    //增加求职意向

    public static void saveJobInfo(String mid, String id, String profession, String jobId, String provinceId, String cityId, String areaId, String low_salary, String top_salary, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("profession", profession);
        requestParams.addBodyParameter("jobId", jobId);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("areaId", areaId);
        requestParams.addBodyParameter("low_salary", low_salary);
        requestParams.addBodyParameter("top_salary", top_salary);
        HttpRequest.post("UserJob/addUserJob", requestParams, callBack);
    }

    //我的个人页面
    public static void getUserInfo(String thisId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", thisId);
        requestParams.addBodyParameter("meetStatus", "3");//（1=预约见面   2=马上视频  3=旧的预约模式）
        HttpRequest.post("User/myUserInfo", requestParams, callBack);
    }

    //退出登录
    public static void outLogin(String mui, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mui);
        HttpRequest.post("Login/Logout", requestParams, callBack);
    }

    //保存个人标签

    public static void addTags(String userid, String columnIds, String expertises, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userid);
        requestParams.addBodyParameter("columnIds", columnIds);
        requestParams.addBodyParameter("expertises", expertises);
        HttpRequest.post("PersonalExpertise/setPersonalTags", requestParams, callBack);
    }

    //编辑求职意向
    public static void editJobInfo(String id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("id", id);
        HttpRequest.post("UserJob/UserJobInfo", requestParams, callBack);
    }

    //推荐首页信息

    public static void userInfo(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Recommend/userInfo", requestParams, callBack);
    }

    //获取二维码
    public static void getQR(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("recUid", mid);
        HttpRequest.post("Recommend/getQR", requestParams, callBack);
    }


    public static void getRecommendRank(String page, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("page", page);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Recommend/RecommendMoneyList", requestParams, callBack);
    }

    //首页分享
    public static void getWxInfo(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("rank", "11");
        HttpRequest.post("Index/getWxInfo", requestParams, callBack);
    }

    //保存个人信息

    public static void saveUserInfo(String mid, File headImage, File backgroundImage, String realname, String sex, String provinceId, String cityId, String areaId, String company, String position, String parentId, String proId, String introduce, String workId, String sign, String birthday, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        if (null != headImage) {
            requestParams.addBodyParameter("headImage", headImage);
        }

        if (null != backgroundImage) {
            requestParams.addBodyParameter("backgroundImage", backgroundImage);
        } else {
            requestParams.addBodyParameter("backgroundImage", "");
        }

        requestParams.addBodyParameter("realname", realname);
        requestParams.addBodyParameter("sex", sex);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("areaId", areaId);
        requestParams.addBodyParameter("company", company);
        requestParams.addBodyParameter("position", position);
        requestParams.addBodyParameter("parentId", parentId);//职能1级标签
        requestParams.addBodyParameter("proId", proId);//职能2级标签

        if (introduce.equals("")) {
            requestParams.addBodyParameter("introduce", "");
        } else {
            requestParams.addBodyParameter("introduce", introduce);
        }

        if (sign.equals("")) {
            requestParams.addBodyParameter("sign", "");
        } else {
            requestParams.addBodyParameter("sign", sign);
        }

        if (null != workId) {
            requestParams.addBodyParameter("workId", workId);
        } else {
            requestParams.addBodyParameter("workId", "");
        }

        if (null != birthday && !"".equals(birthday)) {
            requestParams.addBodyParameter("birthday", birthday);
        } else {
            requestParams.addBodyParameter("birthday", "");
        }

        HttpRequest.post("User/savePersonInfo", requestParams, callBack);
    }

    //编辑个人信息

    public static void editUserInfo(String mUid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mUid);
        HttpRequest.post("User/myPersonInfo", requestParams, callBack);
    }

    //黑名单列表
    public static void getBackList(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", mid);
        HttpRequest.post("Private/BackList", requestParams, callBack);
    }

    //移除
    public static void deleteBackList(String id, String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("Private/removeBackList", requestParams, callBack);
    }

    //获取城市
    public static void getLists(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Region/lists", requestParams, callBack);
    }

    //普通筛选
    public static void searchFilterUser(String mid, String cityId, String sex, String lowPrice, String hightPrice, String tag, int page, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        if (cityId.equals("1")) {
            requestParams.addBodyParameter("cityId", "");
        } else {
            requestParams.addBodyParameter("cityId", cityId);
        }
        if (null != sex) {
            requestParams.addBodyParameter("sex", sex);
        } else {
            requestParams.addBodyParameter("sex", "");
        }
        if ("".equals(lowPrice)) {
            requestParams.addBodyParameter("lowPrice", "");
        } else {
            requestParams.addBodyParameter("lowPrice", lowPrice);
        }

        if ("".equals(hightPrice)) {
            requestParams.addBodyParameter("hightPrice", "");
        } else {
            requestParams.addBodyParameter("hightPrice", hightPrice);
        }
        if (null != tag) {
            requestParams.addBodyParameter("tag", tag);
        } else {
            requestParams.addBodyParameter("tag", "");
        }
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/filterUser", requestParams, callBack);
    }

    //hr模式
    public static void searchHrFilterUser(String mid, String cityId, String sex, String jobId, String professoinId, String lowsalary, String hightsalary, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);

        if (cityId.equals("1")) {
            requestParams.addBodyParameter("cityId", "");
        } else {
            requestParams.addBodyParameter("cityId", cityId);
        }

        if (null != sex) {
            requestParams.addBodyParameter("sex", sex);
        } else {
            requestParams.addBodyParameter("sex", "");
        }

        if ("".equals(jobId)) {
            requestParams.addBodyParameter("jobId", "");
        } else {
            requestParams.addBodyParameter("jobId", jobId);
        }
        if (null != professoinId) {
            requestParams.addBodyParameter("professoinId", professoinId);
        } else {
            requestParams.addBodyParameter("professoinId", "");
        }

        if (null != lowsalary) {
            requestParams.addBodyParameter("lowsalary", lowsalary);
        } else {
            requestParams.addBodyParameter("lowsalary", "");
        }

        if (null != hightsalary) {
            requestParams.addBodyParameter("hightsalary", hightsalary);
        } else {
            requestParams.addBodyParameter("hightsalary", "");
        }
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/filterUser", requestParams, callBack);
    }

    //关键词搜索
    public static void searchUsers(String mid, String keyword, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("keyword", keyword);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Search/searchUsers", requestParams, callBack);
    }

    //获取日期
    public static void getYear(IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Meeting/retDate", requestParams, callBack);
    }
    //更改通知设置
    public static void setNotice(String userId,String type,String status,IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("status", status);
        HttpRequest.post("Notice/set", requestParams, callBack);
    }
    //获取设置
    public static void getNotice(String userId,IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        HttpRequest.post("Notice/view", requestParams, callBack);
    }
}
