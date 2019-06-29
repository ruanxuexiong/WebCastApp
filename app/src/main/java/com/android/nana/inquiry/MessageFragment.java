package com.android.nana.inquiry;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.android.nana.R;
import com.android.nana.inquiry.adapter.MessageAdapter;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by lenovo on 2018/4/24.
 */

public class MessageFragment extends DialogFragment implements View.OnClickListener, MessageAdapter.MessageInterface {


    private ArrayList<String> mDataList;
    private ListView mListView;
    private MessageAdapter mAdapter;
    private String mTalkId;

    public static MessageFragment newInstance(ArrayList<String> mDataList, String mTalkId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("message", mDataList);
        args.putString("talkId", mTalkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明

        mDataList = getArguments().getStringArrayList("message");
        mTalkId = getArguments().getString("talkId");
        View view = inflater.inflate(R.layout.activity_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = view.findViewById(R.id.list_view);
        mAdapter = new MessageAdapter(getActivity(), mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(String string) {

        this.dismiss();
        if ("请选择您希望通话的时间".equals(string)) {

            TimePickerView mPvTime = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    Calendar calendar = Calendar.getInstance();
                    String str1 = getTime(calendar.getTime());
                    String str2 = getTime(date);
                    String str3 = getTime1(calendar.getTime());
                    String str4 = getTime1(date);
                    String str5 = getTime2(date);

                    if (Utils.isDate2Bigger(str1, str2)) {
                        TextMessage mTextMessage;
                        if (Utils.isDateBigger(str3, str4)) {
                            mTextMessage = TextMessage.obtain("请问您今天" + str5 + "方便接听吗？");
                        } else {
                            mTextMessage = TextMessage.obtain("请问您" + str2 + "方便接听吗？");
                        }

                        Message mMessage = Message.obtain(mTalkId, Conversation.ConversationType.GROUP, mTextMessage);

                        RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, mTalkId, mMessage.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                            @Override
                            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTalkId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });
                            }
                        });
                    } else {
                        ToastUtils.showToast("联系时间不能小于当前时间！");
                    }
                }
            }) //年月日时分秒 的显示与否，不设置则默认全部显示
                    .setType(new boolean[]{false, true, true, true, true, false})
                    .setLabel("", "月", "日", "点", "分", "")
                    .isCenterLabel(false)
                    .isCyclic(false)
                    .setDividerColor(Color.DKGRAY)
                    .setContentSize(20)
                    .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                    .setDecorView(null)
                    .build();

            mPvTime.show();
        } else {
            TextMessage mTextMessage = TextMessage.obtain(string);
            Message mMessage = Message.obtain(mTalkId, Conversation.ConversationType.GROUP, mTextMessage);

            RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, mTalkId, mMessage.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                }

                @Override
                public void onSuccess(Integer integer) {
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTalkId, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            });
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日HH点mm分");
        return format.format(date);
    }

    private String getTime1(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    private String getTime2(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("HH点mm分");
        return format.format(date);
    }
}
