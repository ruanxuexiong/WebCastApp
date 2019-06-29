package com.android.nana.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.auth.LoginActivity;
import com.android.nana.bean.UserInfo;
import com.android.nana.card.SendFragment;
import com.android.nana.connect.Constants;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.CustomVideoEvent;
import com.android.nana.eventBus.ForwardEvent;
import com.android.nana.eventBus.GroupDetailEvent;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.eventBus.OpenVideoPathEvent;
import com.android.nana.eventBus.SendMsgEvent;
import com.android.nana.eventBus.SendVideoMsgEvent;
import com.android.nana.eventBus.VideoMessageItemEvent;
import com.android.nana.friend.PhoneContactFragment;
import com.android.nana.friend.RedPhoneContactActivity;
import com.android.nana.identity.identity_homeActivity;
import com.android.nana.mail.GroupDetailActivity;
import com.android.nana.mail.OperationRong;
import com.android.nana.mail.PrivateChatDetailActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.qiniu.VideoTrimActivity;
import com.android.nana.user.Audio.AudioExtensionModule;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.eventbus.CloseVideoPathEvent;
import com.luck.picture.lib.eventbus.VideoPathEvent;
import com.luck.picture.lib.rxbus2.RxBus;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import bean.CallkitGroupInfoEntity;
import io.rong.callkit.RongCallKit;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.MainActivity;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;

import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;

import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import static com.android.nana.connect.Constants.HOST_URL;
import static com.android.nana.util.Utils.getVideoThumbnail;

/**
 * 会话页面
 * 1，设置 ActionBar title
 * 2，加载会话页面
 * 3，push 和 通知 判断
 */

public class ConversationActivity extends BaseActivity implements View.OnClickListener, SendFragment.SendListener, RongIM.LocationProvider, RongIM.ConversationBehaviorListener {

    private String TAG = ConversationActivity.class.getSimpleName();
    /**
     * 对方id
     */
    private String mTargetId;

    /**
     * 是否需要弹框
     * mIsTanchang=1=弹窗   2=不弹窗
     */
    private String mIsTanchang;
    private String mMoney;
    private String mEnough;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * title
     */
    private String title;
    private LoadingDialog mDialog;

    private SharedPreferences sp;

    private final String TextTypingTitle = "对方正在输入...";
    private final String VoiceTypingTitle = "对方正在讲话...";
    private Handler mHandler;

    public static final int SET_TEXT_TYPING_TITLE = 1;
    public static final int SET_VOICE_TYPING_TITLE = 2;
    public static final int SET_TARGET_ID_TITLE = 0;

    private TextView mBackTv, mAction1, mTitletv;
    private String mid;
    private boolean isFriend = false;
    private FragmentManager fm;
    private SendFragment dialog;
    private MySendMessageListener mListener;
    private boolean isSend = false;
    public static String tanchang = "";
    private boolean isGroup = false;//判断是否是群聊
    private MyExtensionModule module = new MyExtensionModule();

    //自定义视频
    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<File> mFileImags = new ArrayList<>();
    private UploadManager uploadManager;
    private ArrayList<String> mPaths = new ArrayList<>();
    private ArrayList<File> mCharts = new ArrayList<>();
    private ArrayList<String> mImags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mid = (String) SharedPreferencesUtils.getParameter(ConversationActivity.this, "userId", "");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        Intent intent = getIntent();
        //new一个uploadManager类
        uploadManager = new UploadManager();

        if (intent == null || intent.getData() == null)
            return;

        mTargetId = getIntent().getData().getQueryParameter("targetId");

        if (mTargetId.contains("group_")) {
            isGroup = true;
            mTargetId = mTargetId.replace("group_", "");
        }
        if (mTargetId.contains("=")) {//是否是好友如果是
            String b[] = mTargetId.split("=");
            for (int i = 0; i < b.length; i++) {
                if (i == 0) {
                    mTargetId = b[i];
                } else if (i == 1) {
                    mIsTanchang = b[i];
                } else if (i == 2) {
                    mMoney = b[i];
                } else {
                    mEnough = b[i];
                }
            }
            isFriend = true;
        }
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.US));
        title = intent.getData().getQueryParameter("title");
        String sTitle = getIntent().getData().getQueryParameter("title");
        if (!TextUtils.isEmpty(sTitle)) {
            mTitletv.setText(sTitle);
        }
        setActionBarTitle(mConversationType, mTargetId);
        setActionIcon();
        isPushMessage(intent);

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SET_TEXT_TYPING_TITLE:
                        setTitle(TextTypingTitle);
                        break;
                    case SET_VOICE_TYPING_TITLE:
                        setTitle(VoiceTypingTitle);
                        break;
                    case SET_TARGET_ID_TITLE:
                        setActionBarTitle(mConversationType, mTargetId);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            @Override
            public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(mConversationType) && targetId.equals(mTargetId)) {
                    int count = typingStatusSet.size();
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();

                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            mHandler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);
                        } else if (objectName.equals(voiceTag.value())) {
                            mHandler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {//当前会话没有用户正在输入，标题栏仍显示原来标题
                        mHandler.sendEmptyMessage(SET_TARGET_ID_TITLE);
                    }
                }
            }
        });


        if (!EventBus.getDefault().isRegistered(ConversationActivity.this)) {
            EventBus.getDefault().register(ConversationActivity.this);
        }

        RongCallKit.setGroupMemberProvider(new RongCallKit.GroupMembersProvider() {
            @Override
            public ArrayList<String> getMemberList(String groupId, final RongCallKit.OnGroupMembersResult result) {
                getGroupMembersForCall();
                mCallMemberResult = result;
                return null;
            }
        });

        /**
         * 修改用户是否是好友状态下
         * 显示视频聊天或语音聊天
         */

        if (isFriend) {//非好友状态不能视频与语音
            List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
            IExtensionModule defaultModule = null;
            if (moduleList != null) {
                for (IExtensionModule module : moduleList) {
                    if (module instanceof DefaultExtensionModule) {
                        defaultModule = module;
                        break;
                    }
                }
                if (defaultModule != null) {
                    RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                    RongExtensionManager.getInstance().registerExtensionModule(module);
                }
            }
        } else {
            List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
            IExtensionModule defaultModule = null;
            if (moduleList != null) {
                for (IExtensionModule module : moduleList) {
                    if (module instanceof DefaultExtensionModule) {
                        defaultModule = module;
                        break;
                    }
                }
                if (defaultModule != null) {
                    RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                    RongExtensionManager.getInstance().registerExtensionModule(new AudioExtensionModule());
                }
            }
        }

        fm = getSupportFragmentManager();
        mListener = new MySendMessageListener();
        RongIM.getInstance().setSendMessageListener(mListener);

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }


    //CallKit start 4
    private RongCallKit.OnGroupMembersResult mCallMemberResult;

    private void getGroupMembersForCall() {


        if (mTargetId.contains("activity")) {

            HomeDbHelper.getChatGroupMember(mid, mTargetId, "", new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            JSONArray member = new JSONArray(data.getString("member"));
                            ArrayList<CallkitGroupInfoEntity.Member> userIds = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < member.length(); i++) {
                                CallkitGroupInfoEntity.Member entity = gson.fromJson(member.optJSONObject(i).toString(), CallkitGroupInfoEntity.Member.class);
                                userIds.add(entity);
                            }
                            mCallMemberResult.onGetMemberList(userIds);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {

                }
            });

        } else {

            HomeDbHelper.memberList(mTargetId, "", new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        JSONObject result = new JSONObject(jsonObject.getString("result"));

                        if (result.getString("state").equals("0")) {
                            JSONArray member = new JSONArray(data.getString("member"));
                            ArrayList<CallkitGroupInfoEntity.Member> userIds = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < member.length(); i++) {
                                CallkitGroupInfoEntity.Member entity = gson.fromJson(member.optJSONObject(i).toString(), CallkitGroupInfoEntity.Member.class);
                                userIds.add(entity);
                            }
                            mCallMemberResult.onGetMemberList(userIds);
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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                finish();
                break;
            case R.id.toolbar_right_2:
                if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
                    Intent intent = new Intent(this, GroupDetailActivity.class);
                    intent.putExtra("conversationType", Conversation.ConversationType.GROUP);

                    if (mTargetId.contains("activity")) {
                        intent.putExtra("activity", "activity");
                        intent.putExtra("mTargetId", mTargetId);
                    } else {
                        intent.putExtra("mTargetId", mTargetId);
                    }

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ConversationActivity.this, PrivateChatDetailActivity.class);
                    intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
                    intent.putExtra("mid", mTargetId);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.conversation);
    }

    @Override
    protected void findViewById() {
        mTitletv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction1 = findViewById(R.id.toolbar_right_2);

    }

    @Override
    protected void init() {
        RongIM.setLocationProvider(this);
        RongIM.setConversationBehaviorListener(this);
        mBackTv.setVisibility(View.VISIBLE);
    }

    protected void setActionIcon() {
        mAction1.setVisibility(View.VISIBLE);

        if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon2_menu);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mAction1.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.right);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mAction1.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAction1.setOnClickListener(this);
    }

    /**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     */
    private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;
        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            //setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            //   setDiscussionActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            setTitle("系统消息");
        } else if (conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
            ///  setAppPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
            // setPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setTitle("意见反馈");
        } else {
            setTitle("消息");
        }
    }


    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */

    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;
        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
                //RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                enterActivity();
            } else if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(ConversationActivity.this, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    // SealAppContext.getInstance().popAllActivity();
                    return;
                }
                enterActivity();
            } else {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(this, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    // SealAppContext.getInstance().popAllActivity();
                    return;
                }
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enterActivity();
                    }
                }, 300);
            } else {
                enterFragment(mConversationType, mTargetId);
            }
        }
    }


    private void enterActivity() {
        String token = sp.getString("loginToken", "");
        if (token.equals("default")) {
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
        } else {
            reconnect(token);
        }
    }


    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
            }

            @Override
            public void onSuccess(String s) {
                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);
            }
        });
    }

    private ConversationFragmentEx fragment;

    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragment = ConversationFragmentEx.newInstance(mTargetId, isGroup, mIsTanchang, mMoney, mEnough);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.add(R.id.rong_content, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ConversationActivity.this);
        RongIM.getInstance().setSendMessageListener(null);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (null != countDownTimer) {
            countDownTimer.cancel();//关闭计算器
        }

        if (null != mToCountDownTimer) {
            mToCountDownTimer.cancel();
        }

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinishActivity(MailEvent mailEvent) {
        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, mTargetId);
        finish();//删除好友关闭当前页
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinishActivity(GroupDetailEvent groupEvent) {
        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTargetId);
        finish();//删除好友关闭当前页
    }


    public View view;
    public VoiceMessage voiceMessage;
    public UIMessage uiMessage;
    public VoiceMessageItemProvider.ViewHolder viewHolder;

    private MediaPlayer mediaPlayer;
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private boolean isFitstInit = true;//用户第一次打开时
    private int currentPosition;//记录当前播放位置
    private boolean isStop = false;//判断是否在暂停状态
    private boolean isReceiveStop = false;//判断是否在播放状态
    private SeekBar mSeekBar, mReceiveSeekBar;
    private int msgid;
    private ImageView mPlayIv;//判断上一个播放按钮
    private ImageView mReceivePlayIv;
    private CountDownTimer countDownTimer;
    private CountDownTimer mToCountDownTimer;

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSendMsg(SendMsgEvent event) {

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        view = event.view;
        voiceMessage = event.voiceMessage;
        uiMessage = event.uiMessage;
        viewHolder = event.viewHolder;

        if (uiMessage.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND) {
            if (null == mediaPlayer && isFitstInit == true) {
                msgid = uiMessage.getMessageId();
            } else if (null != mediaPlayer && isStop && msgid == uiMessage.getMessageId()) {
                isFitstInit = false;
            } else if (null != mediaPlayer && msgid == uiMessage.getMessageId()) {

            } else if (null != mediaPlayer && msgid != uiMessage.getMessageId()) {
                msgid = uiMessage.getMessageId();
                isFitstInit = true;
                mediaPlayer.seekTo(0);
                mediaPlayer.reset();
                timer.cancel();
                timer.purge();
                mSeekBar.setProgress(0);
                currentPosition = 0;
                mPlayIv.setImageResource(R.drawable.icon_new_play);
                stopMedia();
            }
        } else {
            if (null == mediaPlayer && isFitstInit == true) {
                msgid = uiMessage.getMessageId();
            } else if (null != mediaPlayer && isReceiveStop && msgid == uiMessage.getMessageId()) {
                isFitstInit = false;
            } else if (null != mediaPlayer && msgid == uiMessage.getMessageId()) {

            } else if (null != mediaPlayer && msgid != uiMessage.getMessageId()) {
                msgid = uiMessage.getMessageId();
                isFitstInit = true;
                mediaPlayer.seekTo(0);
                mediaPlayer.reset();
                timer.cancel();
                timer.purge();
                currentPosition = 0;
                mReceiveSeekBar.setProgress(0);
                mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                stopMedia();
            }
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }


        play();
    }

    private void play() {
        if (uiMessage.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND) {
            mSeekBar = viewHolder.mSeekBar;
            mPlayIv = viewHolder.mPlayIv;
            viewHolder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeekBarChanging = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSeekBarChanging = false;
                    //在当前位置播放
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });


            try {
                JSONObject jsonObject;
                String path = null;
                if (null != voiceMessage.getExtra()) {
                    jsonObject = new JSONObject(voiceMessage.getExtra());
                    path = HOST_URL + jsonObject.getString("name") + ".wav";
                } else {
                    try {
                        File media = new File(new URI(voiceMessage.getUri().toString()));
                        path = media.getAbsolutePath();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (mediaPlayer != null && isFitstInit == true) {
                    isFitstInit = false;
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
                    mediaPlayer.setDataSource(path);//文件路径
                    mediaPlayer.prepareAsync();//数据缓冲
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(final MediaPlayer mp) {
                            mp.start();
                            //获取音频总时间
                            int duration = mp.getDuration();
                            mSeekBar.setMax(duration);
                            viewHolder.mPlayIv.setImageResource(R.drawable.icon_new_suspend);

                            downTimer(duration);//启动计时器

                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!isSeekBarChanging) {
                                        if (null != mp) {
                                            currentPosition = mp.getCurrentPosition();
                                            mSeekBar.setProgress(mp.getCurrentPosition());
                                        }
                                    }
                                }
                            }, 0, 50);
                        }
                    });


                } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    isStop = true;
                    mediaPlayer.pause();
                    timer.purge();
                    viewHolder.mPlayIv.setImageResource(R.drawable.icon_new_play);
                } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
                    if (currentPosition > 0) {
                        mediaPlayer.seekTo(currentPosition);
                    }
                    //启动播放
                    mediaPlayer.start();
                    viewHolder.mPlayIv.setImageResource(R.drawable.icon_new_suspend);
                }
                //播放完监听
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //播放位置变为0
                        isFitstInit = true;
                        isStop = false;
                        mp.seekTo(0);
                        mp.reset();
                        timer.cancel();
                        timer.purge();
                        mSeekBar.setProgress(0);
                        currentPosition = 0;
                        viewHolder.mPlayIv.setImageResource(R.drawable.icon_new_play);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mReceivePlayIv = viewHolder.mReceivePlayIv;
            mReceiveSeekBar = viewHolder.mReceiveSeekBar;
            mReceiveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeekBarChanging = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSeekBarChanging = false;
                    //在当前位置播放
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });


            try {
                JSONObject jsonObject = null;

                if (null != voiceMessage.getExtra()) {
                    jsonObject = new JSONObject(voiceMessage.getExtra());
                    if ("".equals(jsonObject.getString("name"))) {
                        File media = new File(new URI(voiceMessage.getUri().toString()));
                        if (media.exists()) {
                            if (mediaPlayer != null && isFitstInit == true) {
                                isFitstInit = false;
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
                                mediaPlayer.setDataSource(media.getAbsolutePath());//文件路径
                                mediaPlayer.prepareAsync();//数据缓冲
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    public void onPrepared(final MediaPlayer mp) {
                                        mp.start();
                                        //获取音乐总时间
                                        int duration = mp.getDuration();
                                        //将音乐总时间设置为SeekBar的最大值
                                        mReceiveSeekBar.setMax(duration);
                                        viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                                        downToTimer(duration);//启动计时器

                                        timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (!isSeekBarChanging) {
                                                    if (null != mp) {
                                                        mReceiveSeekBar.setProgress(mp.getCurrentPosition());
                                                    }
                                                }
                                            }
                                        }, 0, 50);
                                    }
                                });


                            } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                isReceiveStop = true;
                                mediaPlayer.pause();
                                viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                            } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
                                if (currentPosition > 0) {
                                    mediaPlayer.seekTo(currentPosition);
                                }
                                //启动播放
                                mediaPlayer.start();
                                viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                            }
                            //播放完监听
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    //播放位置变为0
                                    isFitstInit = true;
                                    isReceiveStop = false;
                                    mp.seekTo(0);
                                    mp.reset();
                                    timer.cancel();
                                    timer.purge();
                                    currentPosition = 0;
                                    mReceiveSeekBar.setProgress(0);
                                    viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                                }
                            });
                        } else {
                            ToastUtils.showToast("播放失败，请稍后重试！");
                        }
                    } else {
                        String path = HOST_URL + jsonObject.getString("name") + ".wav";
                        if (mediaPlayer != null && isFitstInit == true) {
                            isFitstInit = false;
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
                            mediaPlayer.setDataSource(path);//文件路径
                            mediaPlayer.prepareAsync();//数据缓冲
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(final MediaPlayer mp) {
                                    mp.start();
                                    //获取音乐总时间
                                    int duration = mp.getDuration();
                                    //将音乐总时间设置为SeekBar的最大值
                                    mReceiveSeekBar.setMax(duration);
                                    viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                                    downToTimer(duration);
                                    timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (!isSeekBarChanging) {
                                                if (null != mp) {
                                                    mReceiveSeekBar.setProgress(mp.getCurrentPosition());
                                                }
                                            }
                                        }
                                    }, 0, 50);
                                }
                            });

                        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            isReceiveStop = true;
                            mediaPlayer.pause();
                            viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                        } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
                            if (currentPosition > 0) {
                                mediaPlayer.seekTo(currentPosition);
                            }
                            //启动播放
                            mediaPlayer.start();
                            viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                        }
                        //播放完监听
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //播放位置变为0
                                isFitstInit = true;
                                isReceiveStop = false;
                                mp.seekTo(0);
                                mp.reset();
                                timer.cancel();
                                timer.purge();
                                currentPosition = 0;
                                mReceiveSeekBar.setProgress(0);
                                viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                            }
                        });
                    }
                } else {

                    if (mediaPlayer != null && isFitstInit == true) {
                        isFitstInit = false;
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
                        mediaPlayer.setDataSource(voiceMessage.getUri().toString());//文件路径
                        mediaPlayer.prepareAsync();//数据缓冲
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(final MediaPlayer mp) {
                                mp.start();
                                //获取音乐总时间
                                int duration = mp.getDuration();
                                //将音乐总时间设置为SeekBar的最大值
                                mReceiveSeekBar.setMax(duration);
                                viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                                downToTimer(duration);

                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (!isSeekBarChanging) {
                                            if (null != mp) {
                                                mReceiveSeekBar.setProgress(mp.getCurrentPosition());
                                            }
                                        }
                                    }
                                }, 0, 50);
                            }
                        });

                    } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        isReceiveStop = true;
                        mediaPlayer.pause();
                        viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                    } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
                        if (currentPosition > 0) {
                            mediaPlayer.seekTo(currentPosition);
                        }
                        //启动播放
                        mediaPlayer.start();
                        viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_suspend);
                    }
                    //播放完监听
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //播放位置变为0
                            isFitstInit = true;
                            isReceiveStop = false;
                            mp.seekTo(0);
                            mp.reset();
                            timer.cancel();
                            timer.purge();
                            currentPosition = 0;
                            mReceiveSeekBar.setProgress(0);
                            viewHolder.mReceivePlayIv.setImageResource(R.drawable.icon_new_play);
                        }
                    });
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void downToTimer(int duration) {
        mToCountDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                viewHolder.mTimeTv.setText(String.valueOf(time) + "\"");
            }

            @Override
            public void onFinish() {
                if (voiceMessage.getDuration() > 1000) {
                    viewHolder.mTimeTv.setText(Utils.timeParse(voiceMessage.getDuration()) + "\"");
                } else {
                    viewHolder.mTimeTv.setText(voiceMessage.getDuration() + "\"");
                }

            }
        };
        mToCountDownTimer.start();
    }

    private void downTimer(int duration) {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                viewHolder.mMeTimeTv.setText(String.valueOf(time) + "\"");
            }

            @Override
            public void onFinish() {
                viewHolder.mMeTimeTv.setText(voiceMessage.getDuration() + "\"");
            }
        };
        countDownTimer.start();
    }


    /**
     * 设置群聊界面 ActionBar
     *
     * @param targetId 会话 Id
     */
    private void setGroupActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }

    @Override
    public void onSendClick(io.rong.imlib.model.Message message) {
        isSend = true;
        RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, mTargetId, message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.e("errorCode", "errorCode");
            }

            @Override
            public void onSuccess(Integer integer) {
                CardDbHelper.sendMessage(mid, mTargetId, "0", "-1", new IOAuthCallBack() {
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
        dialog.dismiss();
    }

    @Override
    public void onSendRemindClick(io.rong.imlib.model.Message message) {
        isSend = true;
        RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, mTargetId, message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                Log.e("errorCode", "errorCode");

            }

            @Override
            public void onSuccess(Integer integer) {

                CardDbHelper.sendMessage(mid, mTargetId, "0", "1", new IOAuthCallBack() {
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
        dialog.dismiss();
    }

    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        // showToast("你好啊");
        // */
        //Tool.mLastLocationCallback=callback;
        //  Intent intent = new Intent(context, identity_homeActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // context.startActivity(intent);
//        SealAppContext.getInstance().setLastLocationCallback(locationCallback);
//        Intent intent = new Intent(context, identity_homeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, io.rong.imlib.model.UserInfo userInfo) {
        Intent intent = new Intent(ConversationActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", userInfo.getUserId());
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, io.rong.imlib.model.UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, io.rong.imlib.model.Message message) {
        // 消息点击事件，判断如果是位置消息就取出Content()跳转到地图activity
        if (message.getContent() instanceof LocationMessage) {
//            Intent intent = new Intent(ConversationActivity.this, identity_homeActivity.class);
//            intent.putExtra("location", message.getContent());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            showToast("我是地图消息");
        }
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, io.rong.imlib.model.Message message) {
        return false;
    }


    //消息监听
    private class MySendMessageListener implements RongIM.OnSendMessageListener {

        @Override
        public io.rong.imlib.model.Message onSend(io.rong.imlib.model.Message message) {

            if (!"".equals(tanchang) && tanchang.equals("-1")) {
                tanchang = "";
                return message;
            } else if (null != mEnough && mEnough.equals("-1")) {
                showRecharge();
            } else if (isSend) {
                isSend = false;
                return message;
            } else if (null != mIsTanchang && mIsTanchang.equals("1")) {//1为弹窗 -1 为不弹窗
                dialog = SendFragment.newInstance(mMoney, mEnough, message);
                dialog.setSendListener(ConversationActivity.this);
                dialog.show(fm, "fragment_bottom_dialog");
                return null;
            } else if (null != mIsTanchang && mIsTanchang.equals("-1")) {
                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
                return message;
            }

            if (null != mEnough && mEnough.equals("-1")) {
                return null;
            } else {
                return message;
            }
        }

        @Override
        public boolean onSent(final io.rong.imlib.model.Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {

            HomeDbHelper.MykefuIds(mid, new IOAuthCallBack() {//设置客服顶置功能
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {

                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        JSONArray array = new JSONArray(data.getString("uid"));
                        for (int i = 0; i < array.length(); i++) {
                            if (message.getTargetId().equals(array.get(i).toString())) {
                                OperationRong.setConversationTop(ConversationActivity.this, Conversation.ConversationType.PRIVATE, array.get(i).toString(), true);
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
            return true;
        }
    }

    private void showRecharge() {//跳转充值页面
        new AlertDialog.Builder(ConversationActivity.this).setTitle("温馨提示").setMessage("您的余额不足，请马上充值!")
                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        Intent intent = new Intent(ConversationActivity.this, RechargeActivity.class);
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


    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    CardDbHelper.sendMessage(mid, mTargetId, "0", "1", new IOAuthCallBack() {
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
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();//保存当前播放点
            mediaPlayer.pause();
            timer.purge();//移除所有任务;
            viewHolder.mPlayIv.setImageResource(R.drawable.icon_new_play);
        }
        super.onPause();
    }

    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCustomVideo(CustomVideoEvent event) {

        selectList.clear();
        mPaths.clear();
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(ConversationActivity.this)
                .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(false)// 是否裁剪
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .videoQuality(1)// 视频录制质量 0 or 1 int
                .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                .recordVideoSecond(100000)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            showProgressDialog("", "发送中...");
            selectList = PictureSelector.obtainMultipleResult(data);
            for (LocalMedia media : selectList) {
                if (media.getPictureType().equals("video/mp4")) {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(media.getPath());
                    Bitmap bitmap = mmr.getFrameAtTime();
                    mFileImags.add(Utils.compressImage(bitmap));
                    mPaths.add(media.getPath());
                }
            }


            CustomerDbHelper.getToken(new IOAuthCallBack() {
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

                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = new Date(System.currentTimeMillis());
                            final String mName = format.format(date);
                            final String token = data.getString("token");


                            for (String path : mPaths) {
                                uploadManager.put(path, mName + ".mp4", token, new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        if (info.isOK()) {
                                            Bitmap bitmap = Utils.getVideoThumbnail(Constants.HOST_URL + key);
                                            uploadImages(key, BitmapUtils.getBitmapToFile(bitmap, BitmapUtils.getFileName()), BitmapUtils.getFileName(), token);
                                        } else {
                                            dismissProgressDialog();
                                            ToastUtils.showToast("发送失败请稍后重试");
                                        }
                                    }
                                }, null);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {
                    ToastUtils.showToast("发布失败请稍后重试！");
                    dismissProgressDialog();
                }
            });
        }
    }

    /**
     * 上传视频首帧图
     *
     * @param file
     */
    private void uploadImages(final String videoUrl, File file, String fileName, String token) {


        uploadManager.put(file, fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(final String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {

                    Glide.with(ConversationActivity.this).asBitmap().load(HOST_URL + key).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            float bitScale = (float) width / (float) height;
                            int time = 0;//视频时长
                            //保留2位小数

                            BigDecimal b = new BigDecimal(bitScale);
                            bitScale = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                            String ratio = String.valueOf(bitScale);

                            MediaPlayer mediaPlayer = new MediaPlayer();
                            try {
                                File urlFile = new File(Constants.HOST_URL + videoUrl);
                                mediaPlayer.setDataSource(urlFile.getPath());
                                mediaPlayer.prepareAsync();
                                time = mediaPlayer.getDuration();//获得了视频的时长（以毫秒为单位）
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            VideoMessage message = new VideoMessage().obtain(Uri.parse(Constants.HOST_URL + videoUrl), Uri.parse(Constants.HOST_URL + key), time, Constants.HOST_URL + videoUrl, Constants.HOST_URL + key, ratio);

                            if (isGroup) {

                                RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, mTargetId, message, "视频消息", null, new RongIMClient.SendMessageCallback() {
                                    @Override
                                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                                        dismissProgressDialog();
                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        dismissProgressDialog();
                                    }
                                });
                            } else {
                                RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, mTargetId, message, "视频消息", null, new RongIMClient.SendMessageCallback() {
                                    @Override
                                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                                        dismissProgressDialog();
                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        dismissProgressDialog();
                                    }
                                });
                            }
                        }
                    });

                }
            }
        }, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onVideoMsgVideo(SendVideoMsgEvent event) {

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(ConversationActivity.this).externalPictureVideo(event.videoMessage.getVideoUrl());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onVideoPath(VideoPathEvent event) {
        RxBus.getDefault().post(new CloseVideoPathEvent());

        Intent intent = new Intent(ConversationActivity.this, VideoTrimActivity.class);
        intent.putExtra("video_path", event.path);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenVideoPath(OpenVideoPathEvent event) {
        showProgressDialog("", "发送中...");
        sendVideoPath(event.path);
    }

    private void sendVideoPath(final String path) {

        CustomerDbHelper.getToken(new IOAuthCallBack() {
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

                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        final String mName = format.format(date);
                        final String token = data.getString("token");


                        uploadManager.put(path, mName + ".mp4", token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                dismissProgressDialog();
                                if (info.isOK()) {
                                    Bitmap bitmap = getVideoThumbnail(HOST_URL + key);
                                    uploadImages(key, BitmapUtils.getBitmapToFile(bitmap, BitmapUtils.getFileName()), BitmapUtils.getFileName(), token);
                                } else {
                                    dismissProgressDialog();
                                    ToastUtils.showToast("发送失败请稍后重试");
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
                ToastUtils.showToast("发布失败请稍后重试！");
                dismissProgressDialog();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onVideoMesgItem(VideoMessageItemEvent event) {
        FragmentManager fm = getSupportFragmentManager();//成为专家
        VideoMessageItemFragment dialog = VideoMessageItemFragment.newInstance(event.mMsgId);
        dialog.show(fm, "fragment_bottom_dialog");
    }

}

