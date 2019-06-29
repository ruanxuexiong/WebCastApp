package com.android.nana.inquiry;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.listener.MainListener;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.callkit.BaseCallActivity;
import io.rong.callkit.CallFloatBoxView;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.CallUserProfile;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.calllib.message.CallSTerminateMessage;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by lenovo on 2018/4/17.
 */

public class SingleVideoCallActivity extends BaseCallActivity implements Handler.Callback {
    private static final String TAG = "SingleVideoCallActivity";
    private LayoutInflater inflater;
    private RongCallSession callSession;
    private FrameLayout mLPreviewContainer;
    private FrameLayout mSPreviewContainer;
    private FrameLayout mButtonContainer;
    private LinearLayout mUserInfoContainer;
    private Boolean isInformationShow = false;
    private SurfaceView mLocalVideo = null;
    private boolean muted = false;
    private boolean handFree = false;
    private boolean startForCheckPermissions = false;

    private int EVENT_FULL_SCREEN = 1;

    private String targetId = null;
    private RongCallCommon.CallMediaType mediaType;

    private String charge;//每分钟收费
    private String timeLeft;// 剩余通话时长（分钟）
    private String isFree;//前三分钟是否免费
    private TextView mToastTv;
    private TextView mTimeTv;
    private String type;//买家为1
    private String pageId;//订单ID
    private String mUserId;
    private RongCallAction mCallAction;

    @Override
    final public boolean handleMessage(Message msg) {
        if (msg.what == EVENT_FULL_SCREEN) {
            hideVideoCallInformation();
            return true;
        }
        return false;
    }

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_voip_activity_single_call);
        mUserId = (String) SharedPreferencesUtils.getParameter(SingleVideoCallActivity.this, "userId", "");

        Intent intent = getIntent();
        mLPreviewContainer = findViewById(R.id.rc_voip_call_large_preview);
        mSPreviewContainer = findViewById(R.id.rc_voip_call_small_preview);
        mButtonContainer = findViewById(R.id.rc_voip_btn);
        mUserInfoContainer = findViewById(R.id.rc_voip_user_info);

        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));

        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            if (null != callSession.getExtra()) {
                try {
                    JSONObject jsonObject = new JSONObject(callSession.getExtra());
                    charge = jsonObject.getString("charge");
                    isFree = jsonObject.getString("isFree");
                    timeLeft = jsonObject.getString("timeLeft");
                    type = jsonObject.getString("type");
                    pageId = jsonObject.getString("pageId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mediaType = callSession.getMediaType();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            if (callSession != null) {
                mediaType = callSession.getMediaType();
            }
        }
        if (mediaType != null) {
            inflater = LayoutInflater.from(this);
            if (null != getIntent().getStringExtra("charge")) {
                charge = getIntent().getStringExtra("charge");
                timeLeft = getIntent().getStringExtra("timeLeft");
                isFree = getIntent().getStringExtra("isFree");
                type = getIntent().getStringExtra("type");
                pageId = getIntent().getStringExtra("pageId");
            }
            initView(mediaType, callAction);

            if (!requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
                return;
            }
            setupIntent();
        } else {
            RLog.w(TAG, "恢复的瞬间，对方已挂断");
            setShouldShowFloat(false);
            CallFloatBoxView.hideFloatBox();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null) {
            return;
        }
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();
        }

        if (!requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            return;
        }
        if (callSession != null) {
            setupIntent();
        }

        super.onNewIntent(intent);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                boolean permissionGranted;
                if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS);
                } else {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, VIDEO_CALL_PERMISSIONS);

                }
                if (permissionGranted) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        setupIntent();
                    }
                } else {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionDenied();
                    } else {
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {

            String[] permissions;
            if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                permissions = AUDIO_CALL_PERMISSIONS;
            } else {
                permissions = VIDEO_CALL_PERMISSIONS;
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    setupIntent();
                }
            } else {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionDenied();
                } else {
                    finish();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupIntent() {
        RongCallCommon.CallMediaType mediaType;
        Intent intent = getIntent();
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
//        if (callAction.equals(RongCallAction.ACTION_RESUME_CALL)) {
//            return;
//        }
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            mediaType = callSession.getMediaType();
            targetId = callSession.getInviterUserId();
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
            Conversation.ConversationType conversationType = Conversation.ConversationType.valueOf(intent.getStringExtra("conversationType").toUpperCase(Locale.US));
            targetId = intent.getStringExtra("targetId");
            List<String> userIds = new ArrayList<>();
            userIds.add(targetId);

            if (null != charge) {
                JSONObject jsonObject = new JSONObject();
                String json = null;
                try {
                    jsonObject.put("charge", charge);
                    jsonObject.put("timeLeft", timeLeft);
                    jsonObject.put("isFree", isFree);
                    jsonObject.put("type", type);
                    jsonObject.put("pageId", pageId);
                    json = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RongCallClient.getInstance().startCall(conversationType, targetId, userIds, mediaType, json);
            } else {
                RongCallClient.getInstance().startCall(conversationType, targetId, userIds, mediaType, null);
            }


        } else { // resume call
            callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            handFree = false;
        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            handFree = true;
        }

        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && userInfo.getPortraitUri() != null) {
                    userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }

        createPowerManager();
        createPickupDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pickupDetector != null && mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            pickupDetector.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pickupDetector != null) {
            pickupDetector.unRegister();
        }
    }

    private void initView(RongCallCommon.CallMediaType mediaType, RongCallAction callAction) {
        FrameLayout buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        RelativeLayout userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
        userInfoLayout.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.GONE);
        this.mCallAction = callAction;

        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            buttonLayout.findViewById(R.id.rc_voip_call_mute).setVisibility(View.GONE);
            buttonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
            mLPreviewContainer.setVisibility(View.GONE);
            mSPreviewContainer.setVisibility(View.GONE);

            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setText(R.string.rc_voip_audio_call_inviting);
                onIncomingCallRinging();
            }
        } else {
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_video_call_user_info, null);
            mToastTv = userInfoLayout.findViewById(R.id.tv_toast);//弹框
            mToastTv.setVisibility(View.VISIBLE);
            mTimeTv = userInfoLayout.findViewById(R.id.tv_time);//时间
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
                buttonLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                TextView callInfo = userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);

                if (null != type && "1".equals(type)) {
                    callInfo.setText("正在向您发起联系");
                    mToastTv.setVisibility(View.VISIBLE);
                    if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    } else if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    }

                    if (isFree.equals("-1")) {
                        mToastTv.setText("本次联系按" + charge + "元/分钟向对方收费");
                    }
                } else if (null != type && "0".equals(type)) {
                    callInfo.setText(R.string.rc_voip_video_call_inviting);
                    mToastTv.setVisibility(View.VISIBLE);
                    if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    } else if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    }
                    if (isFree.equals("-1")) {
                        mToastTv.setText("本次联系按" + charge + "元/分钟收费");
                    }
                } else {
                    mToastTv.setVisibility(View.GONE);
                    callInfo.setText(R.string.rc_voip_video_call_inviting);
                }
                onIncomingCallRinging();
                ImageView answerV = buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                answerV.setImageResource(R.drawable.rc_voip_vedio_answer_selector);
            } else {
                TextView textView = userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);

                if (null != type && "1".equals(type)) {
                    textView.setText("正在呼叫...");
                    mToastTv.setVisibility(View.VISIBLE);
                    if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    } else if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    }
                    if (isFree.equals("-1")) {
                        mToastTv.setText("本次联系按" + charge + "元/分钟收费");
                    }
                } else if (null != type && "0".equals(type)) {
                    textView.setText("正在呼叫...");
                    mToastTv.setVisibility(View.VISIBLE);
                    mToastTv.setText("本次联系按" + charge + "元/分钟向对方收费");

                    if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    } else if (isFree.equals("1")) {
                        mToastTv.setText("接通后前三分钟免费，后续按" + charge + "元/分钟收费。");
                    }
                    if (isFree.equals("-1")) {
                        mToastTv.setText("本次联系按" + charge + "元/分钟收费");
                    }
                } else {
                    textView.setText(R.string.rc_voip_audio_call_inviting);
                }
            }
        }
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(buttonLayout);
        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoLayout);
    }

    @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
            mLPreviewContainer.setVisibility(View.VISIBLE);
            localVideo.setTag(callSession.getSelfUserId());
            mLPreviewContainer.addView(localVideo);
        }
        onOutgoingCallRinging();
    }

    private long length;
    private String total;//通话总时间
    private String time;//计算得出总时长
    private CountDownTimer startTimer;//启动
    private CountDownTimer endTimer;//结束后10 秒线程

    @Override
    public void onCallConnected(final RongCallSession callSession, SurfaceView localVideo) {//用户接通回调
        super.onCallConnected(callSession, localVideo);
        this.callSession = callSession;

        if (null != mToastTv) {
            mToastTv.setVisibility(View.GONE);//隐藏呼叫提示
        }

        if (null != isFree && isFree.equals("1")) {//1 表示前三分钟免费

            length = (Long.valueOf(timeLeft) * 60) * 1000;
            startTimer = new CountDownTimer(length, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    total = millisUntilFinished / 1000 + "";
                }

                @Override
                public void onFinish() {
                    long length = 10 * 1000;
                    mTimeTv.setVisibility(View.VISIBLE);
                    mToastTv.setVisibility(View.VISIBLE);

                    if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "0".equals(type)) {
                        mToastTv.setText("您的账户余额已不足以继续通话，通话\n即将挂断，请在挂断后充值再发起联系");
                    } else if ("0".equals(type)) {
                        mToastTv.setText("对方账户余额不足，通话即将挂断，您\n可以提醒对方充值后再发起联系。");
                    }

                    if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "1".equals(type)) {
                        mToastTv.setText("对方账户余额不足，通话即将挂断，您\n可以提醒对方充值后再发起联系。");
                    } else if ("1".equals(type)) {
                        mToastTv.setText("您的账户余额已不足以继续通话，通话\n即将挂断，请在挂断后充值再发起联系");
                    }
                    endTimer = new CountDownTimer(length, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mTimeTv.setText("00:" + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish() {
                            time = String.valueOf((Long.valueOf(timeLeft) * 60) - Long.valueOf(total));
                            startTimer.cancel();
                            endTimer.cancel();
                            updateTime(mUserId, pageId, time);
                            RongCallClient.getInstance().hangUpCall(callSession.getCallId());
                            stopRing();//挂断视频通话
                        }
                    };
                    endTimer.start();
                }
            };
            startTimer.start();

        } else if (null != timeLeft) {

            length = (Long.valueOf(timeLeft) * 60) * 1000;
            startTimer = new CountDownTimer(length, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    total = millisUntilFinished / 1000 + "";
                }

                @Override
                public void onFinish() {
                    long length = 10 * 1000;
                    mTimeTv.setVisibility(View.VISIBLE);
                    mToastTv.setVisibility(View.VISIBLE);

                    if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "0".equals(type)) {
                        mToastTv.setText("您的账户余额已不足以继续通话，通话\n即将挂断，请在挂断后充值再发起联系");
                    } else if ("0".equals(type)) {
                        mToastTv.setText("对方账户余额不足，通话即将挂断，您\n可以提醒对方充值后再发起联系。");
                    }

                    if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "1".equals(type)) {
                        mToastTv.setText("对方账户余额不足，通话即将挂断，您\n可以提醒对方充值后再发起联系。");
                    } else if ("1".equals(type)) {
                        mToastTv.setText("您的账户余额已不足以继续通话，通话\n即将挂断，请在挂断后充值再发起联系");
                    }

                    endTimer = new CountDownTimer(length, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mTimeTv.setText("00:" + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish() {
                            if (null != timeLeft && null != total) {
                                time = String.valueOf((Long.valueOf(timeLeft) * 60) - Long.valueOf(total));
                            }
                            startTimer.cancel();
                            endTimer.cancel();
                            updateTime(mUserId, pageId, time);
                            RongCallClient.getInstance().hangUpCall(callSession.getCallId());
                            stopRing();//挂断视频通话
                        }
                    };
                    endTimer.start();
                }
            };
            startTimer.start();
        }


        TextView remindInfo = mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        setupTime(remindInfo);


        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
            FrameLayout btnLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            mLocalVideo = localVideo;
            mLocalVideo.setTag(callSession.getSelfUserId());
        }

        RongCallClient.getInstance().setEnableLocalAudio(!muted);
        View muteV = mButtonContainer.findViewById(R.id.rc_voip_call_mute);
        if (muteV != null) {
            muteV.setSelected(muted);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            RongCallClient.getInstance().setEnableSpeakerphone(false);
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(handFree);
        }
        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        if (handFreeV != null) {
            handFreeV.setSelected(handFree);
        }
        stopRing();
    }

    @Override
    protected void onDestroy() {
        stopRing();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
        }
        RLog.d(TAG, "SingleCallActivity onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRemoteUserJoined(final String userId, RongCallCommon.CallMediaType mediaType, SurfaceView remoteVideo) {//UI修改
        super.onRemoteUserJoined(userId, mediaType, remoteVideo);
        if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mLPreviewContainer.setVisibility(View.VISIBLE);
            mLPreviewContainer.removeAllViews();
            remoteVideo.setTag(userId);

            mLPreviewContainer.addView(remoteVideo);
            mLPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInformationShow) {
                        hideVideoCallInformation();
                    } else {
                        showVideoCallInformation();
                        handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                    }
                }
            });
            mSPreviewContainer.setVisibility(View.VISIBLE);
            mSPreviewContainer.removeAllViews();
            if (mLocalVideo != null) {
                mLocalVideo.setZOrderMediaOverlay(true);
                mLocalVideo.setZOrderOnTop(true);
                mSPreviewContainer.addView(mLocalVideo);
            }
            mSPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SurfaceView fromView = (SurfaceView) mSPreviewContainer.getChildAt(0);
                    SurfaceView toView = (SurfaceView) mLPreviewContainer.getChildAt(0);

                    mLPreviewContainer.removeAllViews();
                    mSPreviewContainer.removeAllViews();
                    fromView.setZOrderOnTop(false);
                    fromView.setZOrderMediaOverlay(false);
                    mLPreviewContainer.addView(fromView);
                    toView.setZOrderOnTop(true);
                    toView.setZOrderMediaOverlay(true);
                    mSPreviewContainer.addView(toView);
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);

        }
    }

    @Override
    public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {
        if (callSession.getSelfUserId().equals(userId)) {
            showShortToast(getString(R.string.rc_voip_switched_to_audio));
        } else {
            if (callSession.getMediaType() != RongCallCommon.CallMediaType.AUDIO) {
                RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
                showShortToast(getString(R.string.rc_voip_remote_switched_to_audio));
            }
        }
        initAudioCallView();
        handler.removeMessages(EVENT_FULL_SCREEN);
        mButtonContainer.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
    }

    private void initAudioCallView() {
        mLPreviewContainer.removeAllViews();
        mLPreviewContainer.setVisibility(View.GONE);
        mSPreviewContainer.removeAllViews();
        mSPreviewContainer.setVisibility(View.GONE);

        findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE);

        View userInfoView = inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
        TextView timeView = userInfoView.findViewById(R.id.rc_voip_call_remind_info);
        setupTime(timeView);

        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoView);
        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }
        mUserInfoContainer.setVisibility(View.VISIBLE);
        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);

        View button = inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(button);
        mButtonContainer.setVisibility(View.VISIBLE);
        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        handFreeV.setSelected(handFree);

        if (pickupDetector != null) {
            pickupDetector.register(this);
        }
    }

    public void onHangupBtnClick(View view) {
        RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            finish();
            return;
        }
        RongCallClient.getInstance().hangUpCall(session.getCallId());
        stopRing();
    }

    public void onReceiveBtnClick(View view) {
        RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            finish();
            return;
        }
        RongCallClient.getInstance().acceptCall(session.getCallId());
    }

    public void hideVideoCallInformation() {
        isInformationShow = false;
        mUserInfoContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);

        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE);
    }

    public void showVideoCallInformation() {
        isInformationShow = true;
        mUserInfoContainer.setVisibility(View.VISIBLE);
        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);
        FrameLayout btnLayout = (FrameLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        btnLayout.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
        btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        btnLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.VISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(btnLayout);
        View view = findViewById(R.id.rc_voip_audio_chat);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
                initAudioCallView();
            }
        });
    }

    public void onHandFreeButtonClick(View view) {
        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected());
        view.setSelected(!view.isSelected());
        handFree = view.isSelected();
    }

    public void onMuteButtonClick(View view) {
        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
        muted = view.isSelected();
    }

    @Override
    public void onCallDisconnected(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {//挂断
        super.onCallDisconnected(callSession, reason);

        if (null != timeLeft) {
            if (null != startTimer) {
                startTimer.cancel();
            }
            if (null != endTimer) {//最后10秒计时器
                endTimer.cancel();
            }


            //更新我要咨询谁
            if (MainListener.getInstance().mOnMessageRefreshListener != null) {
                MainListener.getInstance().mOnMessageRefreshListener.refersh();
            }
            if (null != total) {
                time = String.valueOf((Long.valueOf(timeLeft) * 60) - Long.valueOf(total));//计算出通话时长
                updateTime(mUserId, pageId, time);
            }

            if (null != total) {
                if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "1".equals(type)) {
                    if (MainListener.getInstance().mOnEvaluateListener != null) {//更新谁要咨询我通话时长UI
                        MainListener.getInstance().mOnEvaluateListener.showEvaluate(charge, isFree, time);
                    }
                } else if ("1".equals(type)) {
                    if (MainListener.getInstance().mOnShowDialogListener != null) {//显示视频结束后弹窗
                        MainListener.getInstance().mOnShowDialogListener.showDialog(charge, isFree, time);
                    }
                }
                if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "0".equals(type)) {
                    if (MainListener.getInstance().mOnShowDialogListener != null) {//显示视频结束后弹窗
                        MainListener.getInstance().mOnShowDialogListener.showDialog(charge, isFree, time);
                    }
                } else if ("0".equals(type)) {
                    if (MainListener.getInstance().mOnEvaluateListener != null) {//更新谁要咨询我通话时长UI
                        MainListener.getInstance().mOnEvaluateListener.showEvaluate(charge, isFree, time);
                    }
                }
            }

        } else if (null != isFree && isFree.equals("1")) {
            if (MainListener.getInstance().mOnMessageRefreshListener != null) {//更新我要咨询谁
                MainListener.getInstance().mOnMessageRefreshListener.refersh();
            }
            if (null != total) {
                time = String.valueOf((Long.valueOf(timeLeft) * 60) - Long.valueOf(total));//计算出通话时长
                updateTime(mUserId, pageId, time);
            }
            if (null != total) {
                if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "1".equals(type)) {
                    if (MainListener.getInstance().mOnEvaluateListener != null) {//更新谁要咨询我通话时长UI
                        MainListener.getInstance().mOnEvaluateListener.showEvaluate(charge, isFree, time);
                    }
                } else if ("1".equals(type)) {
                    if (MainListener.getInstance().mOnShowDialogListener != null) {//显示视频结束后弹窗
                        MainListener.getInstance().mOnShowDialogListener.showDialog(charge, isFree, time);
                    }
                }
                if (mCallAction.equals(RongCallAction.ACTION_INCOMING_CALL) && "0".equals(type)) {
                    if (MainListener.getInstance().mOnShowDialogListener != null) {//显示视频结束后弹窗
                        MainListener.getInstance().mOnShowDialogListener.showDialog(charge, isFree, time);
                    }
                } else if ("0".equals(type)) {
                    if (MainListener.getInstance().mOnEvaluateListener != null) {//更新谁要咨询我通话时长UI
                        MainListener.getInstance().mOnEvaluateListener.showEvaluate(charge, isFree, time);
                    }
                }
            }

        }

        String senderId;
        String extra = "";

        isFinishing = true;
        if (callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return;
        }
        senderId = callSession.getInviterUserId();
        switch (reason) {
            case HANGUP:
            case REMOTE_HANGUP:
                long time = getTime();
                if (time >= 3600) {
                    extra = String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
                } else {
                    extra = String.format("%02d:%02d", (time % 3600) / 60, (time % 60));
                }
                break;
            case OTHER_DEVICE_HAD_ACCEPTED:
                showShortToast(getString(R.string.rc_voip_call_other));
                break;
        }


        if (!TextUtils.isEmpty(senderId) && null == charge) {//群聊信息屏蔽此消息
            CallSTerminateMessage message = new CallSTerminateMessage();
            message.setReason(reason);
            message.setMediaType(callSession.getMediaType());
            message.setExtra(extra);
            if (senderId.equals(callSession.getSelfUserId())) {
                message.setDirection("MO");
                RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.PRIVATE, callSession.getTargetId(), io.rong.imlib.model.Message.SentStatus.SENT, message, null);
            } else {
                message.setDirection("MT");
                io.rong.imlib.model.Message.ReceivedStatus receivedStatus = new io.rong.imlib.model.Message.ReceivedStatus(0);
                receivedStatus.setRead();
                RongIM.getInstance().insertIncomingMessage(Conversation.ConversationType.PRIVATE, callSession.getTargetId(), senderId, receivedStatus, message, null);
            }
        }
        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    private void updateTime(String mUserId, String pageId, String time) {

        HomeDbHelper.updateTimeLong(mUserId, pageId, time, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                    } else {
                        //  ToastUtils.showToast(result.getString("description"));
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

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);
        if (bundle == null)
            return;
        muted = bundle.getBoolean("muted");
        handFree = bundle.getBoolean("handFree");
        setShouldShowFloat(true);

        callSession = RongCallClient.getInstance().getCallSession();
        RongCallCommon.CallMediaType mediaType = callSession.getMediaType();
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        inflater = LayoutInflater.from(this);
        initView(mediaType, callAction);
        targetId = callSession.getTargetId();
        UserInfo userInfo = RongContext.getInstance().getUserInfoFromCache(targetId);
        if (userInfo != null) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(userInfo.getName());
            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
                AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null) {
                    userPortrait.setAvatar(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
                }
            }
        }
        SurfaceView localVideo = null;
        SurfaceView remoteVideo = null;
        String remoteUserId = null;
        for (CallUserProfile profile : callSession.getParticipantProfileList()) {
            if (profile.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                localVideo = profile.getVideoView();
            } else {
                remoteVideo = profile.getVideoView();
                remoteUserId = profile.getUserId();
            }
        }
        if (localVideo != null && localVideo.getParent() != null) {
            ((ViewGroup) localVideo.getParent()).removeView(localVideo);
        }
        onCallOutgoing(callSession, localVideo);
        onCallConnected(callSession, localVideo);
        if (remoteVideo != null && remoteVideo.getParent() != null) {
            ((ViewGroup) remoteVideo.getParent()).removeView(remoteVideo);
            onRemoteUserJoined(remoteUserId, mediaType, remoteVideo);
        }
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        callSession = RongCallClient.getInstance().getCallSession();
        bundle.putBoolean("muted", muted);
        bundle.putBoolean("handFree", handFree);
        bundle.putInt("mediaType", callSession.getMediaType().getValue());

        return getIntent().getAction();
    }

    public void onMinimizeClick(View view) {
        super.onMinimizeClick(view);
    }

    public void onSwitchCameraClick(View view) {
        RongCallClient.getInstance().switchCamera();
    }

    @Override
    public void onBackPressed() {
        return;
//        List<CallUserProfile> participantProfiles = callSession.getParticipantProfileList();
//        RongCallCommon.CallStatus callStatus = null;
//        for (CallUserProfile item : participantProfiles) {
//            if (item.getUserId().equals(callSession.getSelfUserId())) {
//                callStatus = item.getCallStatus();
//                break;
//            }
//        }
//        if (callStatus != null && callStatus.equals(RongCallCommon.CallStatus.CONNECTED)) {
//            super.onBackPressed();
//        } else {
//            RongCallClient.getInstance().hangUpCall(callSession.getCallId());
//        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (targetId != null && targetId.equals(userInfo.getUserId())) {
            TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            if (userInfo.getName() != null)
                userName.setText(userInfo.getName());
            AsyncImageView userPortrait = (AsyncImageView) mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
            if (userPortrait != null && userInfo.getPortraitUri() != null) {
                userPortrait.setResource(userInfo.getPortraitUri().toString(), R.drawable.rc_default_portrait);
            }
        }
    }

//    @Override
//    public void showOnGoingNotification() {
//        Intent intent = new Intent(getIntent().getAction());
//        Bundle bundle = new Bundle();
//        onSaveFloatBoxState(bundle);
//        intent.putExtra("floatbox", bundle);
//        intent.putExtra("callAction", RongCallAction.ACTION_RESUME_CALL.getName());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationUtil.showNotification(this, "todo", "coontent", pendingIntent, CALL_NOTIFICATION_ID);
//    }
/*
    @Override
    protected void bindViews() {
        setContentView(R.layout.rc_voip_multi_video_call);
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

    }*/
}
