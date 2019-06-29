package com.android.nana.user;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.SendFragment;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.eventBus.HindFragment;
import com.android.nana.eventBus.MoneyEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.util.VerticalSeekBar;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;

import static android.os.Environment.getExternalStorageDirectory;
import static com.android.nana.util.JsonParser.parseIatResult;


/**
 * Created by Qin on 2018/9/11.
 */

public class ChatFragment extends DialogFragment implements RecognizerListener, SendFragment.SendListener ,RongIM.OnSendMessageListener {

    private TextView mXTv, mVTv;
    private final static String TAG = "Recognizer";
    private EditText mContentTv;
    private VerticalSeekBar mSeekBar;


    private SpeechRecognizer mIat = null;
    private static String mAppId;
    private String uid, str;
    private LinearLayout mXYLl, mSendLl;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private int mTime;
    private long length = 60 * 1000;
    private CountDownTimer countDownTimer;
    private boolean isGroup;
    private UploadManager uploadManager;
    private boolean isSpeak = false;//判断是否手动挂断录音
    private String mIsTanchang;
    private String mMoney;
    private String mEnough;
    private SendFragment dialog;
    private FragmentManager fm;
    private String mid;
    private boolean isSend = false;//是否不在提醒收费弹框


    public static ChatFragment newInstance(String uid, boolean isGroup, String mIsTanchang, String mMoney, String mEnough) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putBoolean("isGroup", isGroup);
        bundle.putString("mIsTanchang", mIsTanchang);
        bundle.putString("mMoney", mMoney);
        bundle.putString("mEnough", mEnough);
        fragment.setArguments(bundle);
        fragment.setCancelable(false);
        return fragment;
    }

    public static ChatFragment newInstance(String uid, String string, boolean isGroup, String mIsTanchang, String mMoney, String mEnough) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putString("str", string);
        bundle.putBoolean("isGroup", isGroup);
        bundle.putString("mIsTanchang", mIsTanchang);
        bundle.putString("mMoney", mMoney);
        bundle.putString("mEnough", mEnough);
        fragment.setArguments(bundle);
        fragment.setCancelable(false);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("uid");
        str = getArguments().getString("str");
        isGroup = getArguments().getBoolean("isGroup");
        mIsTanchang = getArguments().getString("mIsTanchang");
        mMoney = getArguments().getString("mMoney");
        mEnough = getArguments().getString("mEnough");
        fm = getActivity().getSupportFragmentManager();
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        RongIM.getInstance().setSendMessageListener(this);

        if (SpeechUtility.getUtility() == null) {
            SpeechUtility.createUtility(getActivity().getApplicationContext(), SpeechConstant.APPID + "=" + (mAppId == null ? "581f2927" : mAppId));
        }
        uploadManager = new UploadManager();

        if (!EventBus.getDefault().isRegistered(ChatFragment.this)) {
            EventBus.getDefault().register(ChatFragment.this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentTv = view.findViewById(R.id.iat_text);
        mXTv = view.findViewById(R.id.tv_X);
        mVTv = view.findViewById(R.id.tv_V);
        mSeekBar = view.findViewById(R.id.seekBar2);
        mXYLl = view.findViewById(R.id.ll_xy);
        mSendLl = view.findViewById(R.id.ll_send);

        if (null != str && str.equals("show")) {//长按显示
            mXYLl.setVisibility(View.GONE);
            mSendLl.setVisibility(View.VISIBLE);
        } else {
            mXYLl.setVisibility(View.VISIBLE);
            mSendLl.setVisibility(View.GONE);
        }


        mVTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVTv.getText().toString().equals("说完了")) {
                    isSpeak = true;
                    mIat.stopListening();//停止录音
                    mVTv.setText("发送");
                    if (null != countDownTimer) {
                        countDownTimer.cancel();//关闭计算器
                    }
                    return;
                }
                if (mVTv.getText().toString().equals("发送")) {
                    if (null != mEnough && "-1".equals(mEnough)) {
                        showRecharge();
                    } else if (null != mIsTanchang && "1".equals(mIsTanchang)) {
                        io.rong.imlib.model.Message mMessage = new Message();
                        mMessage.setExtra("");
                        dialog = SendFragment.newInstance(mMoney, mEnough, mMessage);
                        dialog.setSendListener(ChatFragment.this);
                        dialog.show(fm, "fragment_bottom_dialog");
                    } else {
                        sendMessage(isSend);
                    }
                }
            }
        });

        mXTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatFragment.this.dismiss();
            }
        });
        downTimer(length, 1000);

        if (mIat == null || !mIat.isListening()) {
            startRecognize();
        } else {
            reset();
        }
    }

    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
        mSeekBar.setProgress(volume * 2);//设置当前进度
    }

    @Override
    public void onBeginOfSpeech() {
        RLog.e(TAG, "RecognizerView onBeginOfSpeech");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onEndOfSpeech() {

      /*  if (null != str && str.equals("show")) {//长按显示
            if ("".equals(mContentTv.getText().toString())) {
                this.dismiss();
            } else {
                    sendMessage(isSend);
            }
        }*/
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        printResult(recognizerResult);
    }

    @Override
    public void onError(SpeechError speechError) {
        if (speechError.getErrorCode() == ErrorCode.ERROR_NO_NETWORK) {
            Toast.makeText(getContext(), getContext().getString(R.string.rc_plugin_recognize_check_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    public void startRecognize() {
        if (null == mIat) {
            mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);
        }
        if (mIat.isListening()) {
            return;
        }
        setParam();
        int ret = mIat.startListening(this);
        if (ret != ErrorCode.SUCCESS) {
            RLog.d(TAG, "startRecognize ret error " + ret);
        }
    }

    /**
     * 初始化监听器。
     */
    private static InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            RLog.i(TAG, "onInit 初始化成功 " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtils.showToast("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 参数设置,设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
     *
     * @param
     * @return
     */
    private void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        if ("zh".equals(Locale.getDefault().getLanguage().toLowerCase())) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "14000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "14000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void reset() {
        if (null != mIat) {
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
    }

    private void printResult(RecognizerResult results) {
        String json = results.getResultString();
        String text = parseIatResult(json);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        if (isSpeak) {
            isSpeak = false;
            mContentTv.setText(mContentTv.getText() + "。");
            mContentTv.requestFocus();
        } else {
            mContentTv.setText(resultBuffer.toString());
        }

    }

    private void sendMessage(final boolean send) {
        ChatFragment.this.dismiss();
        final String msg = mContentTv.getText().toString().trim();

        final String wav = getExternalStorageDirectory() + "/msc/iat.wav";

        CustomerDbHelper.getToken(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        final String mName = format.format(date);
                        final String token = data.getString("token");
                        uploadManager.put(wav, mName + ".wav", token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    sendVoice(mName, msg, wav, send);
                                } else {
                                    ToastUtils.showToast("发送败请稍后重试");
                                }
                            }
                        }, null);
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

    private void sendVoice(String name, String msg, String wav, final boolean send) {//发送语音
        File file = new File(wav);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mTime = Integer.valueOf(Utils.timeParse(mediaPlayer.getDuration()));//获得了视频的时长（以毫秒为单位）
        } catch (IOException e) {
            e.printStackTrace();
        }

        File uriFile = new File(Environment.getExternalStorageDirectory() + "/msc/20181024151429.wav");
        VoiceMessage mMessageVoice = VoiceMessage.obtain(Uri.fromFile(uriFile), mTime);
        JSONObject object = new JSONObject();
        try {
            object.put("MessageText", msg);
            object.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = object.toString();
        mMessageVoice.setExtra(jsonStr);

        if (isGroup) {
            RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, uid, mMessageVoice, "语音消息", null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                }

                @Override
                public void onSuccess(Integer integer) {
                    ChatFragment.this.dismiss();
                }
            });
        } else {
            RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, uid, mMessageVoice, "语音消息", null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                    Log.e("=====er", errorCode.getMessage());
                }

                @Override
                public void onSuccess(Integer integer) {
                    ChatFragment.this.dismiss();
                    if (send) {
                        hideDialog();
                    } else {
                        showDialog();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reset();
        countDownTimer.cancel();//关闭计算器
    }


    private void downTimer(long length, final long num) {

        countDownTimer = new CountDownTimer(length, num) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / num;
                if (String.valueOf(time).equals("10")) {//倒计时 等于10 秒后 重新启动另外一个计算器弹出Toast
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("9")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("8")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("7")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("6")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("5")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("4")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("3")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("2")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                } else if (String.valueOf(time).equals("1")) {
                    ToastUtils.ToatBytTime(getActivity(), "您还能说" + String.valueOf(time) + "秒", 2000);
                }
            }

            @Override
            public void onFinish() {
                mIat.stopListening();
                mVTv.setText("发送");
                mContentTv.requestFocus();
            }
        };
        countDownTimer.start();
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSetFragment(HindFragment hide) {
        mXYLl.setVisibility(View.VISIBLE);
        mSendLl.setVisibility(View.GONE);
        mIat.stopListening();
        mVTv.setText("发送");
        if (null != countDownTimer) {
            countDownTimer.cancel();//关闭计算器
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(ChatFragment.this);
    }

    @Override
    public void onSendClick(Message message) {
        dialog.dismiss();
        ChatFragment.this.dismiss();
        sendMessage(false);
    }

    @Override
    public void onSendRemindClick(Message message) {
        dialog.dismiss();
        sendMessage(true);
    }

    private void showDialog() {
        isSend = false;
        CardDbHelper.sendMessage(mid, uid, "0", "-1", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        mIsTanchang = "1";
                        mEnough = data.getString("enough");
                        EventBus.getDefault().post(new MoneyEvent(mEnough,mIsTanchang));
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

    private void hideDialog() {
        isSend = true;
        CardDbHelper.sendMessage(mid, uid, "0", "1", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        mIsTanchang = "-1";
                        mEnough = data.getString("enough");
                        EventBus.getDefault().post(new MoneyEvent(mEnough,mIsTanchang));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                ChatFragment.this.dismiss();
            }

            @Override
            public void getFailue(String failueJson) {

            }

        });
    }

    private void showRecharge() {//跳转充值页面
        new AlertDialog.Builder(getActivity()).setTitle("温馨提示").setMessage("您的余额不足，请马上充值!")
                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        Intent intent = new Intent(getActivity(), RechargeActivity.class);
                        intent.putExtra("UserId", mid);
                        intent.putExtra("IsAnchor", true);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }

                }).show();
    }

    @Override
    public Message onSend(Message message) {

        return message;
    }

    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        return false;
    }
}
