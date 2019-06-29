package com.android.nana.mail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.PrivateEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.eventBus.UpdateEvent;
import com.android.nana.material.EditDataActivity;
import com.android.nana.network.async.AsyncTaskManager;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by lenovo on 2017/9/13.
 */

public class PrivateChatDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnItemClickListener {

    private String uid;
    private TextView mBackTv;
    private TextView mTitleTv;

    private RoundedImageView mHeadIv;
    private ImageView mDentyIv;
    private UserInfo mUserInfo;
    private AlertView mAlertView;

    private TextView mNameTv, mNumTv, mInfoTv;
    private LinearLayout mDetailsLl, mCloseLl, mQueryLl;
    private SwitchButton mDisturbBtn, mOverheadBtn;

    private StateButton mDelBtn;
    private String mThisId;
    private static final int SEARCH_TYPE_FLAG = 1;
    private SealSearchConversationResult mResult;
    private Conversation.ConversationType mConversationType;
    private AsyncTaskManager asyncTaskManager;
    private static final int REMOVEBLACKLIST = 168;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("mid")) {
            uid = getIntent().getStringExtra("mid");
            mThisId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
            mUserInfo = RongUserInfoManager.getInstance().getUserInfo(uid);
            mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");

            asyncTaskManager = AsyncTaskManager.getInstance(PrivateChatDetailActivity.this);
            updateUI();
            loadData();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.fr_friend_detail);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);

        mHeadIv = findViewById(R.id.iv_head);
        mDentyIv = findViewById(R.id.iv_identy);

        mNumTv = findViewById(R.id.tv_num);
        mNameTv = findViewById(R.id.tv_name);
        mInfoTv = findViewById(R.id.tv_info);
        mDetailsLl = findViewById(R.id.details_ll);


        mDelBtn = findViewById(R.id.btn_del);
        mCloseLl = findViewById(R.id.ll_close);
        mQueryLl = findViewById(R.id.query_ll);
        mDisturbBtn = findViewById(R.id.btn_disturb);
        mOverheadBtn = findViewById(R.id.btn_overhead);
    }

    @Override
    protected void init() {
        mTitleTv.setText("聊天详情");
        mBackTv.setVisibility(View.VISIBLE);
    }


    @Override
    protected void setListener() {

        mQueryLl.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mDetailsLl.setOnClickListener(this);
        mCloseLl.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);

        mDisturbBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {//免打扰

                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
            }
        });

        mOverheadBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {//会话顶置
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                PrivateChatDetailActivity.this.finish();
                break;
            case R.id.details_ll:
                Intent intent = new Intent(PrivateChatDetailActivity.this, EditDataActivity.class);
                intent.putExtra("UserId", uid);
                startActivity(intent);
                break;
            case R.id.ll_close:
                mAlertView = new AlertView("提示", "确定删除聊天记录吗？", "取消", new String[]{"清空"}, null, PrivateChatDetailActivity.this, AlertView.Style.Alert, PrivateChatDetailActivity.this).setCancelable(true);
                mAlertView.show();
                break;
            case R.id.query_ll:
                Intent searchIntent = new Intent(PrivateChatDetailActivity.this, SealSearchChattingDetailActivity.class);

                searchIntent.putExtra("filterString", "");
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);

                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(uid);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);

                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(conversation.getTargetId());
                mResult.setId(conversation.getTargetId());
                String portraitUri = userInfo.getPortraitUri().toString();
                if (!TextUtils.isEmpty(portraitUri)) {
                    mResult.setPortraitUri(portraitUri);
                }
                if (!TextUtils.isEmpty(userInfo.getName())) {
                    mResult.setTitle(userInfo.getName());
                } else {
                    mResult.setTitle(userInfo.getUserId());
                }

                searchIntent.putExtra("searchConversationResult", mResult);
                searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                startActivity(searchIntent);
                break;
            case R.id.btn_del:

                mAlertView = new AlertView("提示", "确定移除该好友？", "取消", new String[]{"确定"}, null, PrivateChatDetailActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {

                        if (position == 0) {
                            delUser();//删除好友
                        }
                    }
                }).setCancelable(true);
                mAlertView.show();
                break;
            default:
                break;
        }
    }

    private void delUser() {//删除好友

        CustomerDbHelper.removeFriend(mThisId, uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String success) {
                try {
                    JSONObject jsonobject = new JSONObject(success);
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                    if (jsonobject1.getString("state").equals("0")) {
                        ToastUtils.showToast(jsonobject1.getString("description"));
                        EventBus.getDefault().post(new MailEvent());//更新通讯录数据
                        EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
                        PrivateChatDetailActivity.this.finish();
                      //  addToBlacklist(uid);
                    } else {
                        ToastUtils.showToast(jsonobject1.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    private void addToBlacklist(final String uid) {
        RongIM.getInstance().addToBlacklist(uid, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {

                EventBus.getDefault().post(new MailEvent());//更新通讯录数据
                PrivateChatDetailActivity.this.finish();

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    private void loadData() {

        HomeDbHelper.chatPersonInfo(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        PrivateEntity mItem = parseData(successJson);
                        ImgLoaderManager.getInstance().showImageView(mItem.getAvatar(), mHeadIv);
                        if (mItem.getStatus().equals("1")) {
                            mDentyIv.setVisibility(View.VISIBLE);
                            Glide.with(PrivateChatDetailActivity.this).load(R.drawable.icon_authen).into(mDentyIv);
                        }
                        else if(mItem.getStatus().equals("4")) {
                            mDentyIv.setVisibility(View.VISIBLE);
                            Glide.with(PrivateChatDetailActivity.this).load(R.mipmap.user_vip).into(mDentyIv);
                        }
                        else {
                            mDentyIv.setVisibility(View.GONE);
                        }
                        mNameTv.setText(mItem.getUname());
                        mNumTv.setText(mItem.getIdcard());

                        if (null != mItem.getWorkHistorys()) {
                            if ("".equals(mItem.getWorkHistorys().getName()) || "".equals(mItem.getWorkHistorys().getPosition())) {
                                mInfoTv.setVisibility(View.GONE);
                            } else if (!"".equals(mItem.getWorkHistorys().getName()) && !"".equals(mItem.getWorkHistorys().getPosition())) {
                                mInfoTv.setText(mItem.getWorkHistorys().getPosition() + " | " + mItem.getWorkHistorys().getName());
                            } else if (!"".equals(mItem.getWorkHistorys().getName())) {
                                mInfoTv.setText(mItem.getWorkHistorys().getName());
                            } else if (!"".equals(mItem.getWorkHistorys().getPosition())) {
                                mInfoTv.setText(mItem.getWorkHistorys().getPosition());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    public PrivateEntity parseData(String result) {//Gson 解析
        PrivateEntity entity = null;
        try {
            JSONObject jsonobject = new JSONObject(result);
            Gson gson = new Gson();
            entity = gson.fromJson(jsonobject.getString("data"), PrivateEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }


    private void updateUI() {//修改是否开启免打扰模式
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, uid, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation == null) {
                        return;
                    }

                    if (conversation.isTop()) {
                        mOverheadBtn.setChecked(true);
                    } else {
                        mOverheadBtn.setChecked(false);
                    }

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });

            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, uid, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                    if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                        mDisturbBtn.setChecked(true);
                    } else {
                        mDisturbBtn.setChecked(false);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0) {
            if (RongIM.getInstance() != null) {
                if (mUserInfo != null) {
                    RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            ToastUtils.showToast("清除成功");
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            ToastUtils.showToast("清除失败");
                        }
                    });
                }
            }
        }
    }
}
