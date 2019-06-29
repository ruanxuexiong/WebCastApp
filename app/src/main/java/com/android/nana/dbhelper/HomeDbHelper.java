package com.android.nana.dbhelper;

import android.app.Activity;

import com.android.common.utils.HttpRequest;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.StringHelper;
import com.lidroid.xutils.http.RequestParams;

import java.io.File;
import java.util.List;

public class HomeDbHelper {

    /**
     * 首页
     *
     * @param thisUserId
     * @param callBack
     */
    public static void index(String thisUserId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("thisUserId", thisUserId);
        HttpRequest.post("Index/banner", requestParams, callBack);

    }

    /**
     * 首页图片
     *
     * @param type
     * @param callBack
     */
    public static void lists(String type, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("type", type);
        HttpRequest.post("Picture/lists", requestParams, callBack);

    }

    /**
     * 获取全球地区
     *
     * @param callBack
     */
    public static void areas(IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        HttpRequest.post("Area/lists", requestParams, callBack);

    }

    /**
     * 马上见面
     *
     * @param thisUserId
     * @param userId
     * @param payType
     * @param callBack
     */
    public static void directSeeding(String thisUserId, String userId, String payPassword,
                                     String payType, String time, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("thisUserId", thisUserId);
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("payPassword", payPassword);
        requestParams.addBodyParameter("payType", payType);
        requestParams.addBodyParameter("time", time);
        HttpRequest.post("Order/directSeeding", requestParams, callBack);

    }

    //返回见面金额和抽佣

    public static void getMeetingInfo(String uid, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Meeting/meetingInfo", requestParams, callBack);
    }

    //马上见面

    public static void appointMeeting(String uid, String mid, String message, String faceMoney, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("message", message);
        requestParams.addBodyParameter("faceMoney", faceMoney);
        requestParams.addBodyParameter("meetStatus", "3");//（1=预约见面   2=马上视频 3=旧的预约模式）
        HttpRequest.post("Meeting/appointMeeting", requestParams, callBack);
    }


    //好友状态下视频
    public static void appointFriendsMeeting(String uid, String mid, String message, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("message", message);
        requestParams.addBodyParameter("meetStatus", "4");//（1=预约见面   2=马上视频 3=旧的预约模式  4=互为好友后马上视频）
        HttpRequest.post("Meeting/appointMeeting", requestParams, callBack);
    }

    /**
     * 拒绝
     *
     * @param userId
     * @param directSeedingRecordId
     * @param callBack
     */
    public static void refuse(String userId, String directSeedingRecordId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("directSeedingRecordId", directSeedingRecordId);
        HttpRequest.post("Order/refuse", requestParams, callBack);
    }

    /**
     * 同意
     *
     * @param userId
     * @param directSeedingRecordId
     * @param callBack
     */
    public static void confirm(Activity mContext, String userId, String directSeedingRecordId, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("directSeedingRecordId", directSeedingRecordId);
        requestParams.addBodyParameter("version", StringHelper.getVersionName(mContext));
        HttpRequest.post("Order/confirm", requestParams, callBack);
    }


    //同意视频
    public static void agree(String mid, String recordId, String zxUid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("recordId", recordId);
        requestParams.addBodyParameter("zxUid", zxUid);
        HttpRequest.post("Advisory/agree", requestParams, callBack);
    }

    //更新通话时长

    public static void updateTimeLong(String mid, String talkId, String timeLong, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("talkId", talkId);
        requestParams.addBodyParameter("timeLong", timeLong);
        HttpRequest.post("Advisory/updateTimeLong", requestParams, callBack);
    }

    //拒绝视频

    public static void disAgree(String mid, String recordId, String zxUid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("recordId", recordId);
        requestParams.addBodyParameter("zxUid", zxUid);
        HttpRequest.post("Advisory/disAgree", requestParams, callBack);
    }

    //发起视频通话
    public static void startVideo(String mid, String recordId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("recordId", recordId);
        HttpRequest.post("Advisory/startVideo", requestParams, callBack);
    }

    /**
     * 设置直播时间
     *
     * @param userId
     * @param directSeedingRecordId
     * @param time
     * @param callBack
     */
    public static void setTime(String userId, String directSeedingRecordId, String time, IOAuthCallBack callBack) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("time", time);
        requestParams.addBodyParameter("directSeedingRecordId", directSeedingRecordId);
        HttpRequest.post("User/setTime", requestParams, callBack);
    }

    /**
     * 发起视频直播
     */
    public static void pushMessage(String userId, String thisUserId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("thisUserId", thisUserId);
        HttpRequest.post("order/pushMessage", requestParams, callBack);
    }

    /**
     * 信鸽推送
     */
    public static void pushXGMessage(String userId, String thisUserId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userId);
        requestParams.addBodyParameter("thisUserId", thisUserId);
        HttpRequest.post("Meeting/pushVideoNotify", requestParams, callBack);
    }

    /**
     * 检测视频是否合法
     */
    public static void checkValidVideo(String userid, String recordId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("userId", userid);
        requestParams.addBodyParameter("directSeedingRecordId", recordId);
        HttpRequest.post("Order/checkValidVideo", requestParams, callBack);
    }

    /**
     * 阅读预约留言
     */
    public static void readMessage(String directId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("directId", directId);
        HttpRequest.post("DirectSeedRecord/readMessage", requestParams, callBack);
    }

    /**
     * 活动中心数据
     */
    public static void popular(String keyword, int page, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("keyword", keyword);
        requestParams.addBodyParameter("page", String.valueOf(page));
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Activity/popular", requestParams, callBack);
    }

    /**
     * 活动详情
     */
    public static void activity(String id, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Activity/activity", requestParams, callBack);
    }

    //编辑活动群组
    public static void editActivity(String id, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Activity/editActivity", requestParams, callBack);
    }

    //参加活动
    public static void joinIn(String mid, String cuid, String id, String password, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("cuid", cuid);
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("password", password);
        HttpRequest.post("Activity/joinIn", requestParams, callBack);
    }

    //我发起的活动

    public static void startActivities(String mid, String type, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Activity/startActivities", requestParams, callBack);
    }

    //我参加的活动
    public static void followActivities(String mid, String type, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("type", type);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Activity/followActivities", requestParams, callBack);
    }

    //创建活动
    public static void addActivity(String mid, String title, String count, List<File> list, String introduce, String provinceId, String cityId, String date, String password, String ispay, String charge, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("title", title);
        requestParams.addBodyParameter("introduce", introduce);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("date", date);
        requestParams.addBodyParameter("password", password);
        requestParams.addBodyParameter("ispay", ispay);
        requestParams.addBodyParameter("charge", charge);
        requestParams.addBodyParameter("count", count);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                requestParams.addBodyParameter("pic" + (i + 1), list.get(i));
            }
        } else {
            requestParams.addBodyParameter("pic", "");
        }
        HttpRequest.post("Activity/add", requestParams, callBack);
    }

    //删除群组
    public static void deleteGroup(String id, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("id", id);
        HttpRequest.post("Activity/del", requestParams, callBack);
    }

    //移除用户列表
    public static void removeUser(String mid, String linkId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("linkId", linkId);
        HttpRequest.post("Activity/removeUser", requestParams, callBack);
    }

    //编辑活动
    public static void updateActivity(String mid, String id, String title, String count, List<File> list, String introduce, String provinceId, String cityId, String date, String password, String ispay, String charge, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("title", title);
        //requestParams.addBodyParameter("picture", picture);
        requestParams.addBodyParameter("introduce", introduce);
        requestParams.addBodyParameter("provinceId", provinceId);
        requestParams.addBodyParameter("cityId", cityId);
        requestParams.addBodyParameter("date", date);
        if (!"".equals(password)) {
            requestParams.addBodyParameter("password", password);
        }
        requestParams.addBodyParameter("ispay", ispay);
        requestParams.addBodyParameter("charge", charge);

        requestParams.addBodyParameter("count", count);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                requestParams.addBodyParameter("pic" + (i + 1), list.get(i));
            }
        } else {
            requestParams.addBodyParameter("pic", "");
        }

        HttpRequest.post("Activity/updateActivity", requestParams, callBack);
    }

    //我的关注
    public static void followers(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Follow/followers", requestParams, callBack);
    }

    //通讯录
    public static void friendsBook(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);//friendsBookAndroid
        HttpRequest.post("Friends/friendsBookAndroid", requestParams, callBack);
    }

    //通过id获取用户头像名称
    public static void getUserName(String uids, String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uids", uids);
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Friends/userInfo", requestParams, callBack);
    }


    //见面记录
    public static void meetingRecords(String mid, int page, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("page", String.valueOf(page));
        HttpRequest.post("Index/MeetingRecords", requestParams, callBack);
    }

    //评论详情
    public static void evaluate(String mid, String recordId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("recordId", recordId);
        HttpRequest.post("Advisory/evaluate", requestParams, callBack);
    }

    //提交评论
    public static void pushEvalution(String mid, String recordId, String stars, String labels, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("recordId", recordId);
        requestParams.addBodyParameter("stars", stars);
        requestParams.addBodyParameter("reviews", labels);
        HttpRequest.post("Advisory/pushEvalution", requestParams, callBack);
    }

    //谁要见我我要见谁消息
    public static void meetingNum(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Advisory/zxRecordNum", requestParams, callBack);
    }


    //新的朋友
    public static void newFriends(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Friends/newFriends", requestParams, callBack);
    }

    //通过好友申请
    public static void passFriend(String mid, String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("Friends/passFriend", requestParams, callBack);
    }

    //拒绝好友
    public static void refundFriend(String mid, String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("Friends/refundFriend", requestParams, callBack);
    }

    //删除新朋友列表
    public static void deleteFriend(String log_id, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("log_id", log_id);
        HttpRequest.post("Friends/delFriendsLog", requestParams, callBack);
    }

    //聊天详情
    public static void chatPersonInfo(String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("ChatRongCloud/chatPersonInfo", requestParams, callBack);
    }

    //创建群聊
    public static void addGroup(String groupName, File picture, String mid, String uids, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("groupName", groupName);
        if (null != picture) {
            requestParams.addBodyParameter("picture", picture);
        } else {
            requestParams.addBodyParameter("picture", "");
        }

        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uids", uids);
        HttpRequest.post("ChatGroup/addGroup", requestParams, callBack);
    }

    //获取群聊名称
    public static void getGroupsInfo(String groupIds, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("groupIds", groupIds);
        HttpRequest.post("ChatGroup/GroupsInfo", requestParams, callBack);
    }

    //获取群组名称
    public static void getActivityInfo(String activityId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("activityId", activityId);
        HttpRequest.post("Activity/activityInfo", requestParams, callBack);
    }

    //我的群聊
    public static void getGroupList(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("ChatGroup/groupList", requestParams, callBack);
    }

    //群聊设置
    public static void getGroupInfo(String mid, String groupId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        HttpRequest.post("ChatGroup/groupInfo", requestParams, callBack);
    }

    //群组设置
    public static void getChatGroupMember(String mid, String groupId, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("ChatGroup/member", requestParams, callBack);
    }

    //邀请联系人接口
    public static void selectMember(String mid, String groupId, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("ChatGroup/selectMember", requestParams, callBack);
    }

    //删除群成员列表
    public static void quitMember(String groupId, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("groupId", groupId);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("ChatGroup/quitMember", requestParams, callBack);
    }

    //移除成员
    public static void quitGroup(String mid, String groupId, String uids, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        requestParams.addBodyParameter("uids", uids);
        HttpRequest.post("ChatGroup/quitGroup", requestParams, callBack);
    }

    //添加成员到群聊
    public static void joinGroup(String uids, String groupId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("uids", uids);
        requestParams.addBodyParameter("groupId", groupId);
        HttpRequest.post("ChatGroup/joinGroup", requestParams, callBack);
    }

    //群成员
    public static void memberList(String groupId, String keyword, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("groupId", groupId);
        requestParams.addBodyParameter("keyword", keyword);
        HttpRequest.post("ChatGroup/member", requestParams, callBack);
    }

    //更改群信息
    public static void updateGroup(String groupName, String groupId, File picture, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("groupId", groupId);
        if (null != picture) {
            requestParams.addBodyParameter("picture", picture);
        } else {
            requestParams.addBodyParameter("picture", "");
        }
        requestParams.addBodyParameter("groupName", groupName);
        HttpRequest.post("ChatGroup/updateGroup", requestParams, callBack);
    }

    //解散群组
    public static void dimissGroup(String mid, String groupId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        HttpRequest.post("ChatGroup/dimissGroup", requestParams, callBack);
    }

    //退出群聊
    public static void exitGroup(String mid, String groupId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("groupId", groupId);
        HttpRequest.post("ChatGroup/exitGroup", requestParams, callBack);
    }

    //群组头像
    public static void syncGroup(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("ChatGroup/syncGroup", requestParams, callBack);
    }

    //退出群组
    public static void quitActivity(String mid, String activityId, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("activityId", activityId);
        HttpRequest.post("Activity/quitActivity", requestParams, callBack);
    }

    //转发
    public static void searchPushFriend(String mid, String name, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("name", name);
        HttpRequest.post("ChatGroup/searchPushFriend", requestParams, callBack);
    }

    //浏览器分享
    public static void transfriend(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("ChatGroup/transfriend", requestParams, callBack);
    }

    //首页分享
    public static void shenFriend(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Friends/tellFriend", requestParams, callBack);
    }

    //判断是否是好友
    public static void isFriend(String mid, String uid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("uid", uid);
        HttpRequest.post("ChatRongCloud/isFriend", requestParams, callBack);
    }

    //广告弹窗
    public static void adDialog(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Index/advertisement", requestParams, callBack);
    }

    //获取客服的UID
    public static void MykefuIds(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Friends/MykefuIds", requestParams, callBack);
    }

    //推广链接
    public static void extensionUrl(String mid, IOAuthCallBack callBack) {
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("mid", mid);
        HttpRequest.post("Cooperation/index", requestParams, callBack);
    }
}
