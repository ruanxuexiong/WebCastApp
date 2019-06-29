package com.android.nana.inquiry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.HomeRecordEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.UpdateMessageEvent;
import com.android.nana.eventBus.UpdateWantMessageEvent;
import com.android.nana.eventBus.WantEvent;
import com.android.nana.friend.CustomMessage;
import com.android.nana.listener.MainListener;
import com.android.nana.main.TimeBean;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.StartBean;
import com.android.nana.material.WantBean;
import com.android.nana.receiver.PushExtra;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.DrawableTextView;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongCallKit;
import io.rong.callkit.RongVoIPIntent;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by lenovo on 2018/4/11.
 */

public class ConversationListActivity extends BaseActivity implements View.OnClickListener {

    private FragmentTransaction mFragmentTrans;
    private FragmentManager mFragmentManager;
    private NewConversationListFragment mListFragment;

    private TextView mTitleTv;
    private TextView mBackTv;
    private String mTargetId;
    private StateButton mSendBtn;
    private StartBean mBean;
    private WantBean mWantBean;
    private String type;//判断是卖家买家 卖家为1  买家为0

    private CircleImageView mHeadIv;
    private ImageView mSureIv;
    private TextView mNameTv;
    private TextView mDesTv;
    private String status;
    private LinearLayout mWaitLl;
    private TextView mWaitTv;

    private LinearLayout mVideoLl;
    private DrawableTextView mVideoBtn;
    private DrawableTextView mCloseVideoBtn;

    //同意
    private LinearLayout mHideLl;
    private LinearLayout mRefuseLl;
    private LinearLayout mAgreeLl;

    //谁咨询我发送消息
    private LinearLayout mSendMsgLl;
    private ImageView mSendIv;
    private EditText mContentEt;
    private String mUserId;
    private TextView mEvaluateTv;

    //总费用
    private LinearLayout mCostLl;
    private TextView mTimeLenTv;
    private TextView mTotalCostTv;
    private HomeRecordEntity entity;
    private boolean mTrigger = false;//自动触发点击评价按钮
    private RelativeLayout mAvatarRl;
    private ArrayList<Message> mMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(ConversationListActivity.this)) {
            EventBus.getDefault().register(ConversationListActivity.this);
        }

        mUserId = (String) SharedPreferencesUtils.getParameter(ConversationListActivity.this, "userId", "");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_conversation_list);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mSendBtn = findViewById(R.id.btn_send);

        mHeadIv = findViewById(R.id.iv_head);
        mSureIv = findViewById(R.id.iv_sure);
        mNameTv = findViewById(R.id.tv_name);
        mDesTv = findViewById(R.id.tv_des);
        mWaitLl = findViewById(R.id.ll_wait);
        mWaitTv = findViewById(R.id.tv_wait);

        mVideoLl = findViewById(R.id.ll_video);
        mVideoBtn = findViewById(R.id.tv_btn);
        mCloseVideoBtn = findViewById(R.id.tv_close_btn);

        mHideLl = findViewById(R.id.ll_hide);
        mRefuseLl = findViewById(R.id.ll_refuse);
        mAgreeLl = findViewById(R.id.ll_agree);

        mSendMsgLl = findViewById(R.id.ll_send);
        mContentEt = findViewById(R.id.et_content);
        mSendIv = findViewById(R.id.iv_send);
        mEvaluateTv = findViewById(R.id.tv_evaluate);

        mCostLl = findViewById(R.id.ll_cost);
        mTimeLenTv = findViewById(R.id.tv_len_time);
        mTotalCostTv = findViewById(R.id.tv_total_cost);
        mAvatarRl = findViewById(R.id.rl_avatar);

        MainListener.getInstance().mOnAgreeListener = new MainListener.OnAgreeListener() {//推送同意
            @Override
            public void refresh(final PushExtra pushExtra) {
                mVideoLl.setVisibility(View.VISIBLE);
                mWaitLl.setVisibility(View.GONE);
                mHideLl.setVisibility(View.GONE);

                mVideoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HomeDbHelper.startVideo(mUserId, pushExtra.getId(), new IOAuthCallBack() {
                            @Override
                            public void onStartRequest() {
                            }

                            @Override
                            public void getSuccess(String successJson) {
                                dismissProgressDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(successJson);
                                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                                    if (result.getString("state").equals("0")) {
                                        TimeBean bean = parseTimeData(successJson);
                                        startSingleCall(ConversationListActivity.this, pushExtra.getUid(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, pushExtra.getTalkId(), "1");
                                    } else {
                                        ToastUtils.showToast("余额不足或视频无效，请稍后再试!");
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
                });
            }
        };

        MainListener.getInstance().mOnCustomerRefreshListener = new MainListener.OnCustomerRefreshListener() {//拒绝
            @Override
            public void refersh() {
                mWaitLl.setVisibility(View.VISIBLE);
                mWaitLl.setBackgroundResource(R.color.red_fef35l);
                mWaitTv.setText("对方已拒绝");
                mHideLl.setVisibility(View.GONE);//同意拒绝
                mSendBtn.setClickable(false);
                mSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_aa));
            }
        };
    }

    @Override
    protected void init() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTrans = mFragmentManager.beginTransaction();
        mBackTv.setVisibility(View.VISIBLE);
        if (null != getIntent().getSerializableExtra("start")) {
            mBean = (StartBean) getIntent().getSerializableExtra("start");
            type = "1";
            mNameTv.setText(mBean.getUser().getUsername());

            if (!"".equals(mBean.getUser().getCompany()) && !"".equals(mBean.getUser().getPosition()) && null != mBean.getUser().getCompany() && null != mBean.getUser().getPosition()) {
                mDesTv.setText(mBean.getUser().getPosition() + "|" + mBean.getUser().getCompany());
            } else if (null != mBean.getUser().getCompany() && !"".equals(mBean.getUser().getCompany())) {
                mDesTv.setText(mBean.getUser().getCompany());
            } else if (!"".equals(mBean.getUser().getPosition()) && null != mBean.getUser().getPosition()) {
                mDesTv.setText(mBean.getUser().getPosition());
            }

            ImageLoader.getInstance().displayImage(mBean.getUser().getAvatar(), mHeadIv);
            mListFragment = initConversationList(mBean.getTalkId());

            mTitleTv.setText("我 联系" + mBean.getUser().getUsername());

            switch (mBean.getStatus()) {
                case "0":
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mVideoBtn.setVisibility(View.GONE);//视频
                    mCloseVideoBtn.setVisibility(View.VISIBLE);//视频结束
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "1":
                    mHideLl.setVisibility(View.VISIBLE);

                    mVideoLl.setVisibility(View.GONE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    break;
                case "3"://等待同意咨询
                    mWaitLl.setVisibility(View.VISIBLE);
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "4"://已同意
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "5"://已拒绝
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitLl.setBackgroundResource(R.color.red_fef35l);
                    mWaitTv.setText("对方已拒绝");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "6"://已失效
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitTv.setText("超时已失效");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                default:
                    break;
            }
        }

        if (null != getIntent().getStringExtra("status")) {
            status = getIntent().getStringExtra("status");
            if (status.equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            } else {
                mSureIv.setVisibility(View.GONE);
            }
        }
        if (null != getIntent().getSerializableExtra("want") && null != getIntent().getStringExtra("whoSee")) {//谁要咨询我
            mWantBean = (WantBean) getIntent().getSerializableExtra("want");
            type = getIntent().getStringExtra("type");

            if (null != getIntent().getParcelableArrayListExtra("msg") && getIntent().getParcelableArrayListExtra("msg").size() > 0) {
                mMessage = getIntent().getParcelableArrayListExtra("msg");
                for (int i = 0; i < mMessage.size(); i++) {
                    if (mWantBean.getTalkId().equals(mMessage.get(i).getTargetId())) {
                        mMessage.get(i).getReceivedStatus().setRead();//设置为已读
                        mMessage.get(i).setReceivedStatus(mMessage.get(i).getReceivedStatus());
                        EventBus.getDefault().post(new UpdateMessageEvent(mMessage.get(i), mWantBean));//更新首页消息红点
                        MainListener.getInstance().mOnMessageListener.refersh(mMessage.get(i));//更新消息红点
                    }
                }
            }

            //发送消息
            mSendBtn.setVisibility(View.GONE);
            mSendMsgLl.setVisibility(View.VISIBLE);

            mTitleTv.setText(mWantBean.getZx_user().getUsername() + " 联系 我");
            mNameTv.setText(mWantBean.getZx_user().getUsername());

            if (!"".equals(mWantBean.getZx_user().getPosition()) && !"".equals(mWantBean.getZx_user().getCompany()) && null != mWantBean.getZx_user().getCompany() && null != mWantBean.getZx_user().getPosition()) {
                mDesTv.setText(mWantBean.getZx_user().getPosition() + "|" + mWantBean.getZx_user().getCompany());
            } else if (null != mWantBean.getZx_user().getCompany() && !"".equals(mWantBean.getZx_user().getCompany())) {
                mDesTv.setText(mWantBean.getZx_user().getCompany());
            } else if (!"".equals(mWantBean.getZx_user().getPosition()) && null != mWantBean.getZx_user().getPosition()) {
                mDesTv.setText(mWantBean.getZx_user().getPosition());
            }

            ImageLoader.getInstance().displayImage(mWantBean.getZx_user().getAvatar(), mHeadIv);
            mTargetId = mWantBean.getTalkId();
            mListFragment = initConversationList(mWantBean.getTalkId());

            if (mWantBean.getZx_user().getStatus().equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            }

            switch (mWantBean.getStatus()) {
                case "0":
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mVideoBtn.setVisibility(View.GONE);//视频
                    mCloseVideoBtn.setVisibility(View.VISIBLE);//视频结束
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "1":
                    mHideLl.setVisibility(View.VISIBLE);

                    mVideoLl.setVisibility(View.GONE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    break;
                case "3"://等待同意咨询
                    mWaitLl.setVisibility(View.VISIBLE);
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "4"://已同意
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "5"://已拒绝
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitLl.setBackgroundResource(R.color.red_fef35l);
                    mWaitTv.setText("已拒绝");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "6"://已失效
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitTv.setText("超时已失效");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                default:
                    break;
            }

        } else if (null != getIntent().getSerializableExtra("want")) {//首页我要咨询谁传递过来参数
            mWantBean = (WantBean) getIntent().getSerializableExtra("want");
            type = getIntent().getStringExtra("type");
            mTitleTv.setText("我 联系 " + mWantBean.getZx_user().getUsername());
            mNameTv.setText(mWantBean.getZx_user().getUsername());

            if (null != getIntent().getParcelableArrayListExtra("msg") && getIntent().getParcelableArrayListExtra("msg").size() > 0) {
                mMessage = getIntent().getParcelableArrayListExtra("msg");
                for (int i = 0; i < mMessage.size(); i++) {
                    if (mWantBean.getTalkId().equals(mMessage.get(i).getTargetId())) {
                        mMessage.get(i).getReceivedStatus().setRead();//设置为已读
                        mMessage.get(i).setReceivedStatus(mMessage.get(i).getReceivedStatus());
                        EventBus.getDefault().post(new UpdateWantMessageEvent(mMessage.get(i)));//更新首页消息红点
                        MainListener.getInstance().mOnMessageListener.refersh(mMessage.get(i));//更新消息红点
                    }
                }
            }

            if (!"".equals(mWantBean.getZx_user().getPosition()) && !"".equals(mWantBean.getZx_user().getCompany()) && null != mWantBean.getZx_user().getCompany() && null != mWantBean.getZx_user().getPosition()) {
                mDesTv.setText(mWantBean.getZx_user().getPosition() + "|" + mWantBean.getZx_user().getCompany());
            } else if (null != mWantBean.getZx_user().getCompany() && !"".equals(mWantBean.getZx_user().getCompany())) {
                mDesTv.setText(mWantBean.getZx_user().getCompany());
            } else if (!"".equals(mWantBean.getZx_user().getPosition()) && null != mWantBean.getZx_user().getPosition()) {
                mDesTv.setText(mWantBean.getZx_user().getPosition());
            }

            ImageLoader.getInstance().displayImage(mWantBean.getZx_user().getAvatar(), mHeadIv);

            mTargetId = mWantBean.getTalkId();
            mListFragment = initConversationList(mWantBean.getTalkId());

            if (mWantBean.getZx_user().getStatus().equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            }

            switch (mWantBean.getStatus()) {
                case "0":
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mVideoBtn.setVisibility(View.GONE);//视频
                    mCloseVideoBtn.setVisibility(View.VISIBLE);//视频结束
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "1":
                    mHideLl.setVisibility(View.VISIBLE);

                    mVideoLl.setVisibility(View.GONE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    break;
                case "3"://等待同意咨询
                    mWaitLl.setVisibility(View.VISIBLE);
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "4"://已同意
                    mVideoLl.setVisibility(View.VISIBLE);
                    mWaitLl.setVisibility(View.GONE);//拒绝
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "5"://已拒绝
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitLl.setBackgroundResource(R.color.red_fef35l);
                    mWaitTv.setText("对方已拒绝");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                case "6"://已失效
                    mWaitLl.setVisibility(View.VISIBLE);
                    mWaitTv.setText("超时已失效");
                    mHideLl.setVisibility(View.GONE);//同意拒绝
                    break;
                default:
                    break;
            }
        } else if (null != getIntent().getSerializableExtra("refuse")) {//拒绝

            entity = (HomeRecordEntity) getIntent().getSerializableExtra("record");
            mListFragment = initConversationList(entity.getTalkId());
            mUserId = getIntent().getStringExtra("mid");
            if (entity.getStatus().equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            }

            if (entity.getMeetStatus().equals("1")) {//谁要咨询我
                mTitleTv.setText(entity.getUname() + " 联系我");
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }

                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);

                mWaitLl.setVisibility(View.VISIBLE);
                mWaitLl.setBackgroundResource(R.color.red_fef35l);
                mWaitTv.setText("已拒绝");
                mHideLl.setVisibility(View.GONE);//同意拒绝

                mSendMsgLl.setVisibility(View.VISIBLE);
                mSendBtn.setVisibility(View.GONE);
                mContentEt.setCursorVisible(false);
                mContentEt.setFocusable(false);
                mContentEt.setFocusableInTouchMode(false);
                mContentEt.setBackgroundDrawable(getDrawable(R.drawable.bg_edit_no));


                mSendIv.setClickable(false);
                mSendIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendIv.setImageDrawable(getDrawable(R.drawable.icon_no_send));
            } else {
                mTitleTv.setText("我 联系 " + entity.getUname());
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }
                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);
                mWaitLl.setVisibility(View.VISIBLE);
                mWaitLl.setBackgroundResource(R.color.red_fef35l);
                mWaitTv.setText("对方已拒绝");
                mHideLl.setVisibility(View.GONE);//同意拒绝
                mSendBtn.setClickable(false);
                mSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_aa));
            }

        } else if (null != getIntent().getSerializableExtra("invalid")) {//失效
            entity = (HomeRecordEntity) getIntent().getSerializableExtra("record");
            mListFragment = initConversationList(entity.getTalkId());
            mUserId = getIntent().getStringExtra("mid");
            if (entity.getStatus().equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            }

            if (entity.getMeetStatus().equals("1")) {//谁要咨询我
                mTitleTv.setText(entity.getUname() + " 联系我");
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }
                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);

                mWaitLl.setVisibility(View.VISIBLE);
                mWaitTv.setText("超时已失效");
                mHideLl.setVisibility(View.GONE);//同意拒绝

                mSendMsgLl.setVisibility(View.VISIBLE);
                mSendBtn.setVisibility(View.GONE);
                mContentEt.setCursorVisible(false);
                mContentEt.setFocusable(false);
                mContentEt.setFocusableInTouchMode(false);
                mContentEt.setBackgroundDrawable(getDrawable(R.drawable.bg_edit_no));


                mSendIv.setClickable(false);
                mSendIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendIv.setImageDrawable(getDrawable(R.drawable.icon_no_send));
            } else {
                mTitleTv.setText("我 联系 " + entity.getUname());
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }

                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);
                mWaitLl.setVisibility(View.VISIBLE);
                mWaitTv.setText("超时已失效");
                mHideLl.setVisibility(View.GONE);//同意拒绝

                mSendBtn.setClickable(false);
                mSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_aa));
            }


        } else if (null != getIntent().getSerializableExtra("record")) {//咨询记录
            entity = (HomeRecordEntity) getIntent().getSerializableExtra("record");
            mListFragment = initConversationList(entity.getTalkId());
            mUserId = getIntent().getStringExtra("mid");
            if (null != entity.getStatus() && entity.getStatus().equals("1")) {
                mSureIv.setVisibility(View.VISIBLE);
            }
            if (entity.getMeetStatus().equals("1")) {//谁要咨询我
                mTitleTv.setText(entity.getUname() + " 联系我");
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }
                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);
                mSendMsgLl.setVisibility(View.VISIBLE);
                mSendBtn.setVisibility(View.GONE);
                mContentEt.setCursorVisible(false);
                mContentEt.setFocusable(false);
                mContentEt.setFocusableInTouchMode(false);
                mContentEt.setBackgroundDrawable(getDrawable(R.drawable.bg_edit_no));


                mSendIv.setClickable(false);
                mSendIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendIv.setImageDrawable(getDrawable(R.drawable.icon_no_send));
                getTotalCost(mUserId, entity.getId());//获取咨询总费用

            } else {//我要咨询谁
                mTitleTv.setText("我 联系 " + entity.getUname());
                mNameTv.setText(entity.getUname());

                if (!"".equals(entity.getPosition()) && !"".equals(entity.getCompany()) && null != entity.getCompany() && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition() + "|" + entity.getCompany());
                } else if (null != entity.getCompany() && !"".equals(entity.getCompany())) {
                    mDesTv.setText(entity.getCompany());
                } else if (!"".equals(entity.getPosition()) && null != entity.getPosition()) {
                    mDesTv.setText(entity.getPosition());
                }
                ImageLoader.getInstance().displayImage(entity.getAvatar(), mHeadIv);
                mEvaluateTv.setVisibility(View.VISIBLE);
                if (entity.getZx_comment().equals("1")) {
                    mEvaluateTv.setText("查看评价");
                } else {
                    mEvaluateTv.setText("评价");
                }
                mVideoLl.setVisibility(View.VISIBLE);
                mWaitLl.setVisibility(View.GONE);//拒绝
                mVideoBtn.setVisibility(View.GONE);//视频
                mCloseVideoBtn.setVisibility(View.VISIBLE);//视频结束
                mHideLl.setVisibility(View.GONE);//同意拒绝
                mSendBtn.setClickable(false);
                mSendBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_aa));
                if (entity.getZx_comment().equals("1")) {//查看评价

                    mEvaluateTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, entity.getId(), "1");// 1 代表查看
                            dialog.show(fm, "fragment_bottom_dialog");
                        }
                    });
                } else {

                    mEvaluateTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fm = getSupportFragmentManager();
                            EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, entity.getId(), "2");
                            dialog.show(fm, "fragment_bottom_dialog");
                        }
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, entity.getId(), "2");//2 代表评论
                    dialog.show(fm, "fragment_bottom_dialog");
                }
            }
        }


        MainListener.getInstance().mOnShowDialogListener = new MainListener.OnShowDialogListener() {//视频结束后调用
            @Override
            public void showDialog(String charge, String isFree, String total) {

                mBean = (StartBean) SharedPreferencesUtils.getObject(ConversationListActivity.this, "start", StartBean.class);
                mVideoLl.setVisibility(View.VISIBLE);
                mWaitLl.setVisibility(View.GONE);//拒绝
                mVideoBtn.setVisibility(View.GONE);//视频
                mCloseVideoBtn.setVisibility(View.GONE);//视频结束
                mHideLl.setVisibility(View.GONE);//同意拒绝
                mCostLl.setVisibility(View.VISIBLE);

                mTrigger = true;
                mSendBtn.setClickable(false);
                mSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendBtn.setNormalBackgroundColor(getResources().getColor(R.color.grey_aa));
                mEvaluateTv.setVisibility(View.VISIBLE);
                mEvaluateTv.setText("评价");
                mEvaluateTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fm = getSupportFragmentManager();
                        EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, mBean.getId(), "2");
                        dialog.show(fm, "fragment_bottom_dialog");
                    }
                });
                mCostLl.setVisibility(View.VISIBLE);
                Long m = Long.valueOf(total) / 60;
                Long s = Long.valueOf(total) % 60;
                double money;

                if (m == 0 || m < 10) {
                    if (s == 0 || s < 10) {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":0" + s);
                    } else {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":" + s);
                    }
                } else if (s == 0 || s < 10) {
                    if (m == 0 || m < 10) {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":" + s);
                    } else {
                        mTimeLenTv.setText("联系总时长：" + m + ":0" + s);
                    }
                } else {
                    mTimeLenTv.setText("联系总时长：" + m + ":" + s);
                }

                if (isFree.equals("1")) {//免费三分钟
                    if (s > 0) {
                        m += 1;
                    }
                    if (m > 3) {
                        m = m - 3;
                        money = m * Double.valueOf(charge);
                        mTotalCostTv.setText("共计消费：" + money + "元");
                    } else {
                        mTotalCostTv.setText("共计消费：0");
                    }
                } else {
                 /*   if (s > 0) {
                        m += 1;
                    }
                    money = m * Double.valueOf(charge);
                    mTotalCostTv.setText("共计消费：" + money + "元");*/
                    loadEvaluate(mUserId, mBean.getId());
                }

            }
        };

        MainListener.getInstance().mOnEvaluateListener = new MainListener.OnEvaluateListener() {
            @Override
            public void showEvaluate(String charge, String isFree, String total) {

                mVideoBtn.setVisibility(View.GONE);//视频
                mSendMsgLl.setVisibility(View.VISIBLE);
                mSendBtn.setVisibility(View.GONE);
                mContentEt.setCursorVisible(false);
                mContentEt.setFocusable(false);
                mContentEt.setFocusableInTouchMode(false);
                mContentEt.setBackgroundDrawable(getDrawable(R.drawable.bg_edit_no));

                mSendIv.setClickable(false);
                mSendIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mSendIv.setImageDrawable(getDrawable(R.drawable.icon_no_send));

                mCostLl.setVisibility(View.VISIBLE);
                Long m = Long.valueOf(total) / 60;
                Long s = Long.valueOf(total) % 60;
                double money;

                if (m == 0 || m < 10) {
                    if (s == 0 || s < 10) {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":0" + s);
                    } else {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":" + s);
                    }
                } else if (s == 0 || s < 10) {
                    if (m == 0 || m < 10) {
                        mTimeLenTv.setText("联系总时长：" + "0" + m + ":" + s);
                    } else {
                        mTimeLenTv.setText("联系总时长：" + m + ":0" + s);
                    }
                } else {
                    mTimeLenTv.setText("联系总时长：" + m + ":" + s);
                }

                if (isFree.equals("1")) {//免费三分钟
                    if (s > 0) {
                        m += 1;
                    }
                    if (m > 3) {
                        m = m - 3;
                        money = m * Double.valueOf(charge);
                        mTotalCostTv.setText("共计收入：" + money + "元");
                    } else {
                        mTotalCostTv.setText("共计收入：0");
                    }
                } else {
                    if (s > 0) {
                        m += 1;
                    }
                    money = m * Double.valueOf(charge);
                    mTotalCostTv.setText("共计收入：" + money + "元");
                }
            }
        };

        mFragmentTrans.replace(R.id.view_frame_layout, mListFragment).commit();
        if (mTrigger) {
            ConversationListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEvaluateTv.performClick();//自动触发评价点击事件
                }
            });
        }
        RongIM.registerMessageType(CustomMessage.class);

    }

    private void loadEvaluate(String mid, String recordId) {//总计消费

        HomeDbHelper.evaluate(mid, recordId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        EvaluateBean bean = parseData(successJson);
                        mTotalCostTv.setText("共计消费：" + bean.getZxUser().getZx_money());
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

    private void getTotalCost(String mid, String recordId) {//获取咨询总费用

        HomeDbHelper.evaluate(mid, recordId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        EvaluateBean bean = parseData(successJson);
                        mCostLl.setVisibility(View.VISIBLE);
                        mTimeLenTv.setText("咨询总时长：" + bean.getZxUser().getZx_time());
                        mTotalCostTv.setText("共计收入：" + bean.getZxUser().getZx_money());
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


    private NewConversationListFragment initConversationList(String mTargetId) {
        if (mListFragment == null) {
            NewConversationListFragment listFragment = new NewConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName())
                    .appendQueryParameter("targetId", mTargetId).build();
            listFragment.setUri(uri);

            mListFragment = listFragment;
            return listFragment;
        } else {
            return mListFragment;
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mVideoBtn.setOnClickListener(this);
        mRefuseLl.setOnClickListener(this);
        mAgreeLl.setOnClickListener(this);
        mSendIv.setOnClickListener(this);
        mAvatarRl.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_send:
                if (null != mBean) {
                    FragmentManager fm = getSupportFragmentManager();
                    MessageFragment dialog = MessageFragment.newInstance(mBean.getMessage(), mBean.getTalkId());
                    dialog.show(fm, "fragment_bottom_dialog");
                } else if (null != mWantBean) {
                    FragmentManager fm = getSupportFragmentManager();
                    MessageFragment dialog = MessageFragment.newInstance(mWantBean.getMessage(), mWantBean.getTalkId());
                    dialog.show(fm, "fragment_bottom_dialog");
                }
                break;
            case R.id.tv_btn:
                showProgressDialog("", "加载中...");

                if (type.equals("1") && null != mBean) {//我要咨询谁
                    HomeDbHelper.startVideo(mUserId, mBean.getId(), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject result = new JSONObject(jsonObject.getString("result"));
                                if (result.getString("state").equals("0")) {
                                    TimeBean bean = parseTimeData(successJson);
                                    startSingleCall(ConversationListActivity.this, mWantBean.getZx_user().getId(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, mWantBean.getTalkId(), type);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {
                        }
                    });
                } else if (type.equals("1") && null != mWantBean) {//我要咨询谁
                    HomeDbHelper.startVideo(mUserId, mWantBean.getId(), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject result = new JSONObject(jsonObject.getString("result"));
                                if (result.getString("state").equals("0")) {
                                    TimeBean bean = parseTimeData(successJson);
                                    startSingleCall(ConversationListActivity.this, mWantBean.getZx_user().getId(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, mWantBean.getTalkId(), type);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {
                        }
                    });
                } else {//谁要咨询我
                    HomeDbHelper.startVideo(mUserId, mWantBean.getId(), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {
                        }

                        @Override
                        public void getSuccess(String successJson) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject result = new JSONObject(jsonObject.getString("result"));
                                if (result.getString("state").equals("0")) {
                                    TimeBean bean = parseTimeData(successJson);
                                    startSingleCall(ConversationListActivity.this, mWantBean.getZx_user().getId(), RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO, bean, mWantBean.getTalkId(), type);
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
                break;

            case R.id.ll_refuse://拒绝
                refuse();
                break;
            case R.id.ll_agree://同意
                agree();
                break;
            case R.id.iv_send:

                if (null != mTargetId) {
                    if (TextUtils.isEmpty(mContentEt.getText().toString().trim())) {
                        ToastUtils.showToast("内容不能为空!");
                        return;
                    }

                    TextMessage mTextMessage = TextMessage.obtain(mContentEt.getText().toString().trim());
                    Message mMessage = Message.obtain(mTargetId, Conversation.ConversationType.GROUP, mTextMessage);
                    RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, mTargetId, mMessage.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        }

                        @Override
                        public void onSuccess(Integer integer) {

                            hideIMM(ConversationListActivity.this, mSendIv);
                            mContentEt.setText("");
                            RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Boolean>() {
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
                break;
            case R.id.rl_avatar:
                if (null != mBean) {//我要咨询谁
                    Intent intent = new Intent(this, EditDataActivity.class);
                    intent.putExtra("UserId", mBean.getUser().getId());
                    startActivity(intent);
                } else if (null != mWantBean) {
                    Intent intent = new Intent(this, EditDataActivity.class);
                    intent.putExtra("UserId", mWantBean.getZx_user().getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, EditDataActivity.class);
                    intent.putExtra("UserId", entity.getUid());
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }

    private void refuse() {//拒绝
        showProgressDialog("", "加载中...");
        HomeDbHelper.disAgree(mWantBean.getUserId(), mWantBean.getId(), mWantBean.getZx_user().getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mWaitLl.setVisibility(View.VISIBLE);
                        mWaitLl.setBackgroundResource(R.color.red_fef35l);
                        mWaitTv.setText("已拒绝");
                        mHideLl.setVisibility(View.GONE);//同意拒绝
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

    private void agree() {//同意
        showProgressDialog("", "加载中...");
        HomeDbHelper.agree(mWantBean.getUserId(), mWantBean.getId(), mWantBean.getZx_user().getId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mVideoLl.setVisibility(View.VISIBLE);
                        mWaitLl.setVisibility(View.GONE);//拒绝
                        mHideLl.setVisibility(View.GONE);//同意拒绝
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

    public void hideIMM(Context context, View view) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startSingleCall(Context context, String targetId, RongCallKit.CallMediaType mediaType, TimeBean bean, String pageId, String type) {
        if (RongCallKit.checkEnvironment(context, mediaType)) {

            String action = RongVoIPIntent.RONG_INTENT_ACTION_SINGLE_VIDEO;
            Intent intent = new Intent(action);

            Bundle bundle = new Bundle();
            bundle.putString("targetId", targetId);
            bundle.putString("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
            bundle.putString("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());

            bundle.putString("charge", bean.getCharge());// 每分钟收费
            bundle.putString("timeLeft", bean.getTimeLeft());// 剩余通话时长（分钟）
            bundle.putString("isFree", bean.getIsFree());//前三分钟是否免费
            bundle.putString("type", type);//卖家为0 买家为1
            bundle.putString("pageId", pageId);
            intent.putExtras(bundle);
            intent.setPackage(context.getPackageName());
            context.startActivity(intent);
        }
    }

    public TimeBean parseTimeData(String result) {//Gson 解析
        TimeBean detail = new TimeBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), TimeBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLoadData(WantEvent wantEvent) {//我要见谁msg
        mEvaluateTv.setText("查看评价");
        if (null != mBean) {
            mEvaluateTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, mBean.getId(), "1");// 1 代表查看
                    dialog.show(fm, "fragment_bottom_dialog");
                }
            });
        } else {
            mEvaluateTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    EvaluateFragment dialog = EvaluateFragment.newInstance(mUserId, entity.getId(), "1");// 1 代表查看
                    dialog.show(fm, "fragment_bottom_dialog");
                }
            });
        }
    }

    public EvaluateBean parseData(String result) {//Gson 解析
        EvaluateBean detail = new EvaluateBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), EvaluateBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ConversationListActivity.this);
    }
}
