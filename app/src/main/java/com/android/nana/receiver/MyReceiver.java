package com.android.nana.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.android.nana.R;
import com.android.nana.activity.FollowListActivity;
import com.android.nana.eventBus.AddFriendEvent;
import com.android.nana.eventBus.FriendsEvent;
import com.android.nana.eventBus.PushExtraEvent;
import com.android.nana.friend.NewMsgActivity;
import com.android.nana.listener.MainListener;
import com.android.nana.mail.NewFriendActivity;
import com.android.nana.main.MainActivity;
import com.android.nana.main.WantActivity;
import com.android.nana.main.WhoSeeActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.NotifyUtil;
import com.android.nana.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    private PushExtra pushExtra;
    private Gson gson = new Gson();
    private String mid;
    private int requestCode = (int) SystemClock.uptimeMillis();

    @Override
    public void onReceive(Context context, Intent intent) {
          Bundle bundle = intent.getExtras();
        mid = (String) SharedPreferencesUtils.getParameter(context, "userId", "");
        if (bundle == null) {
            return;
        }
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (extra == null || extra.isEmpty()) {
            return;
        }
        pushExtra = gson.fromJson(extra, PushExtra.class);

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction()) && pushExtra.getType() != null) {

            switch (pushExtra.getType()) {
                case "addComment"://评论

                   /* if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent newMsg = new Intent(context, NewMsgActivity.class);
                        newMsg.putExtra("mid", mid);
                        newMsg.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, newMsg, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.mipmap.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }*/
                    EventBus.getDefault().post(new PushExtraEvent(pushExtra.getCount(), pushExtra.getAvatar()));

                    break;
                case "addArticle"://发送朋友圈推送
                    EventBus.getDefault().post(new FriendsEvent(pushExtra.getCount(), pushExtra.getAvatar()));
                    break;
                case "addFriend"://添加好友
                    EventBus.getDefault().post(new AddFriendEvent());
                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent newFriend = new Intent(context, NewFriendActivity.class);
                        newFriend.putExtra("mid", mid);
                        newFriend.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, newFriend, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }

                    break;
                case "laud"://朋友圈点赞
                    EventBus.getDefault().post(new PushExtraEvent(pushExtra.getCount(), pushExtra.getAvatar()));

                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent newMsg = new Intent(context, NewMsgActivity.class);
                        newMsg.putExtra("mid", mid);
                        newMsg.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, newMsg, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                case "passFriend"://同意好友申请
                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent newFriend = new Intent(context, NewFriendActivity.class);
                        newFriend.putExtra("mid", mid);
                        newFriend.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, newFriend, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                case "refundFriend"://拒绝好友申请
                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent newFriend = new Intent(context, NewFriendActivity.class);
                        newFriend.putExtra("mid", mid);
                        newFriend.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, newFriend, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                case "shuiyaojianwo"://发起约见推送
                    if (MainListener.getInstance().mOnMessageRefreshListener != null) {
                        MainListener.getInstance().mOnMessageRefreshListener.refersh();
                    }
                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent who = new Intent(context, WhoSeeActivity.class);
                        who.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, who, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }

                    break;
                case "woyaojianshui"://双方同意推送
                    if (MainListener.getInstance().mOnAgreeListener != null) {
                        MainListener.getInstance().mOnAgreeListener.refresh(pushExtra);
                    }

                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent want = new Intent(context, WantActivity.class);
                        want.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, want, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                case "refuseMeeting"://拒绝

                    if (MainListener.getInstance().mOnCustomerRefreshListener != null) {
                        MainListener.getInstance().mOnCustomerRefreshListener.refersh();
                    }

                    if (MainListener.getInstance().mOnMessageRefreshListener != null) {//更新我要咨询谁
                        MainListener.getInstance().mOnMessageRefreshListener.refersh();
                    }
                    receivingNotification(context, bundle);
                    break;
                case "viewInfo"://浏览用户信息

                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent want = new Intent(context, EditDataActivity.class);
                        want.putExtra("UserId", pushExtra.getUid());
                        want.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, want, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                case "Follow":
                    if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
                        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                        Intent follow = new Intent(context, FollowListActivity.class);
                        follow.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, follow, PendingIntent.FLAG_UPDATE_CURRENT);
                        int smallIcon = R.drawable.ic_launcher;
                        String ticker = "您有一条新通知";
                        //实例化工具类，并且调用接口
                        NotifyUtil notify2 = new NotifyUtil(context, 2);
                        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
                    }
                    break;
                default:
                    if (MainListener.getInstance().mOnMessageRefreshListener != null) {
                        MainListener.getInstance().mOnMessageRefreshListener.refersh();
                    }
                    if (MainListener.getInstance().mOnCustomerRefreshListener != null) {
                        MainListener.getInstance().mOnCustomerRefreshListener.refersh();
                    }
                    receivingNotification(context, bundle);
                    break;
            }
        }

       /* if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            JSONObject object = JSONUtil.getStringToJson(json);
            String openVcNameKey = JSONUtil.get(object, "key", "");

            if ("addComment".equals(bundle.getString(JPushInterface.EXTRA_MESSAGE))) {//评论推送
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                pushExtra = gson.fromJson(extra, PushExtra.class);
                EventBus.getDefault().post(new PushExtraEvent(pushExtra.getCount(), pushExtra.getAvatar()));
            } else if ("addArticle".equals(bundle.getString(JPushInterface.EXTRA_MESSAGE))) {//发送朋友圈推送
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                pushExtra = gson.fromJson(extra, PushExtra.class);
                EventBus.getDefault().post(new FriendsEvent(pushExtra.getCount(), pushExtra.getAvatar()));
            } else if ("addFriend".equals(openVcNameKey)) {//添加好友
                EventBus.getDefault().post(new AddFriendEvent());
            } else {
                if (MainListener.getInstance().mOnMessageRefreshListener != null) {
                    MainListener.getInstance().mOnMessageRefreshListener.refersh();
                }
                if (MainListener.getInstance().mOnCustomerRefreshListener != null) {
                    MainListener.getInstance().mOnCustomerRefreshListener.refersh();
                }
            }

            receivingNotification(context, bundle);//自定义消息通知栏
        }*/
    }

    private void receivingNotification(Context context, Bundle bundle) {
        if (null != bundle.getString(JPushInterface.EXTRA_MESSAGE)) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            if (!"addComment".equals(message.trim()) && !"addArticle".equals(message.trim())) {
                String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                int smallIcon = R.drawable.ic_launcher;
                String ticker = "您有一条新通知";
                //实例化工具类，并且调用接口
                NotifyUtil notify2 = new NotifyUtil(context, 2);
                notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, message, true, true, false);
            }
        }
    }


}
