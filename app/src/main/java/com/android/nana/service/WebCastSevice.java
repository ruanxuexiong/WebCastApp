package com.android.nana.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class WebCastSevice extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
/*
    private long mBeginTimeLong, mEndTimeLong;

    public static String mDirectSeedingRecordId = "0";
    public static String userid = "";
    public static String recordId = "";
    public static String meetStatus;

    private boolean mIsActive; // 主播方
    private boolean mIsEnd; // 是否结束
    private boolean isVideo = false;//是否已经开始视频
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    }

    private BroadcastReceiver mComingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (null != intent.getExtras().getString("extra_info")) {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getExtras().getString("extra_info"));
                    mDirectSeedingRecordId = jsonObject.getString("MtcCallServerUserDataKey");//通过justalk传递过来的key值
                    Log.e("传递过来的ID", mDirectSeedingRecordId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 通话开始
    private BroadcastReceiver mTalkingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mBeginTimeLong = System.currentTimeMillis();
            mIsEnd = false;
            isVideo = true;
            mIsActive = true;

            Log.e("传递过来的ID", mDirectSeedingRecordId);
            UserModel.getTiemLong(mDirectSeedingRecordId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        String jsonObject1 = jsonObject.getString("data");
                        String result = jsonObject.getString("result");

                        JSONObject state = new JSONObject(result);
                        if (state.getString("state").equals("0")) {
                            JSONObject jsonObject2 = new JSONObject(jsonObject1);
                            talkingCountDownTimer(jsonObject2.getString("ret_time"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void getFailue(String failueJson) {
                    Log.e("返回值===", failueJson);
                }
            });

        }
    };

    // 主动挂断
    private BroadcastReceiver mDidTermReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (isVideo) {
                isVideo = false;
                mIsActive = false;
                mEndTimeLong = System.currentTimeMillis();
                countDownTimer.cancel();
                mIsEnd = true;
                doUpdateTimeLong(BaseApplication.getInstance().getCustomerId(getApplication()));
                EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
            }
        }
    };

    // 被动挂断
    private BroadcastReceiver mTermedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVideo) {
                countDownTimer.cancel();
                isVideo = false;
                mIsEnd = true;
                mIsActive = false;
                EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
            }
        }
    };


    private void doUpdateTimeLong(String userid) {


        mIsActive = false;

        if (userid.equals("")) return;

        if (mBeginTimeLong == 0) return; // 表示对方没有接听


        String timeLong = String.valueOf((mEndTimeLong - mBeginTimeLong) / 1000);


        if (timeLong.equals("0")) return;

        updateTimeLong(timeLong, userid);
    }

    private void updateTimeLong(String timeLong, String userid) {

        CustomerDbHelper.updateTimeLong(getApplication(), userid, mDirectSeedingRecordId, timeLong, meetStatus, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                if (mResultDetailModel.mIsSuccess) {
                    if (AppointmentListener.getInstance().mOnDialVideoListener != null) {
                        AppointmentListener.getInstance().mOnDialVideoListener.refersh();
                    }
                    mIsEnd = true;
                }
            }

            @Override
            public void getFailue(String failueJson) {

//				UIHelper.showToast(WebCastSevice.this, failueJson);

            }
        });

    }

    Toast toast;
    TextView textView;


    private void talkingCountDownTimer(final String seeTimeLong) {

        Log.e("服务器返回的时间===", seeTimeLong);

        final long timeLong = Integer.valueOf(seeTimeLong) * 1000;

        countDownTimer = new CountDownTimer(timeLong, 1000) {
            @Override
            public void onTick(long timeCountdown) {

                long myday = (timeCountdown / 1000) / 3600 / 24;
                long myhour = (timeCountdown / 1000 - myday * 3600 * 24) / 3600;
                long myminute = ((timeCountdown / 1000) - myday * 3600 * 24 - myhour * 3600) / 60;
                long mysecond = timeCountdown / 1000 - myday * 3600 * 24 - myhour * 3600 - myminute * 60;

                if (myday == 0 && myhour == 0 && myminute == 0 && mysecond <= 30 && !mIsEnd && null == meetStatus) {
                    if (mysecond == 30) {
                        UIHelper.showToast(WebCastSevice.this, "通话即将结束", 1);
                        EventBus.getDefault().post(new UpdateEvent("update"));//更新谁要见我数据
                    } else {
                        if (toast == null) {
                            toast = Toast.makeText(WebCastSevice.this, String.valueOf(mysecond), 1);
                            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                            View view = inflater.inflate(R.layout.toast_layout_root, null);
                            textView = (TextView) view.findViewById(R.id.text);
                            toast.setDuration(1);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setView(view);
                            textView.setText(String.valueOf(mysecond));
                            toast.show();
                        } else {
                            textView.setText(String.valueOf(mysecond));
                            toast.show();
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                if (!mIsEnd) {

                    Log.e("zhisxl", "sssss");
                    if (mIsActive) {
                        mIsEnd = true;
                        Log.e("计算时间===", seeTimeLong + "");
                        updateTimeLong(seeTimeLong, BaseApplication.getInstance().getCustomerId(getApplication()));
                    }

                    isVideo = false;
                    mEndTimeLong = 0;
                    mBeginTimeLong = 0;
                    countDownTimer.cancel();
                }
            }
        };
        countDownTimer.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }


    *//**
     * 电话已拨出。
     * @param rongCallSession
     * @param surfaceView
     *//*
    @Override
    public void onCallOutgoing(RongCallSession rongCallSession, SurfaceView surfaceView) {


    }

    *//**
     * 电话已经建立
     * @param rongCallSession
     * @param surfaceView
     *//*
    @Override
    public void onCallConnected(RongCallSession rongCallSession, SurfaceView surfaceView) {

        Log.e("电话建立=[=",rongCallSession.getExtra());
    }

    *//**
     * 通话结束。 通话中，对方挂断，己方挂断，或者通话过程网络异常造成的通话中断，都会回调
     * @param rongCallSession
     * @param callDisconnectedReason
     *//*
    @Override
    public void onCallDisconnected(RongCallSession rongCallSession, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {

    }

    *//**
     * 被叫端正在振铃。
     * @param s
     *//*
    @Override
    public void onRemoteUserRinging(String s) {

    }

    *//**
     * 被叫端加入通话。 主叫端拨出电话，被叫端收到请求后，加入通话，回调
     * @param s
     * @param callMediaType
     * @param surfaceView
     *//*
    @Override
    public void onRemoteUserJoined(String s, RongCallCommon.CallMediaType callMediaType, SurfaceView surfaceView) {

    }

    *//**
     * 通话中的某一个参与者，邀请好友加入通话，发出邀请请求后，回调
     * @param s
     * @param callMediaType
     *//*
    @Override
    public void onRemoteUserInvited(String s, RongCallCommon.CallMediaType callMediaType) {

    }

    *//**
     * 通话中的远端参与者离开。 回调 onRemoteUserLeft 通知状态更新
     * @param s
     * @param callDisconnectedReason
     *//*
    @Override
    public void onRemoteUserLeft(String s, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {

    }

    *//**
     * 当通话中的某一个参与者切换通话类型，例如由 audio 切换至 video，
     * @param s
     * @param callMediaType
     * @param surfaceView
     *//*
    @Override
    public void onMediaTypeChanged(String s, RongCallCommon.CallMediaType callMediaType, SurfaceView surfaceView) {

    }

    *//**
     * 通话过程中，发生异常。
     * @param callErrorCode
     *//*
    @Override
    public void onError(RongCallCommon.CallErrorCode callErrorCode) {

    }

    *//**
     * 远端参与者 camera 状态发生变化时，回调
     * @param s
     * @param b
     *//*
    @Override
    public void onRemoteCameraDisabled(String s, boolean b) {

    }*/
}
