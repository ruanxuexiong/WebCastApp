package com.android.nana.mail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.GroupActivityAdapter;
import com.android.nana.adapter.GroupMemberAdapter;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.GroupInfoEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.GroupDetailEvent;
import com.android.nana.eventBus.GroupEvent;
import com.android.nana.eventBus.GroupFinisEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.DemoGridView;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/9/19.
 */

public class GroupDetailActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {


    private TextView mTitleTv;
    private TextView mBackTv;
    private String mTargetId;//群组id
    private String mid;//当前登录id
    private DemoGridView mGridView;
    private TextView mNumTv, mNameTv;
    private ImageView mGroupIv;
    private String sHold;//是否是自己创建的群
    private List<GroupInfoEntity.Member> mGroupMember;
    private StateButton mDelBtn;
    private RelativeLayout mSizeItemRl;
    private RelativeLayout mHeadRl;
    private RelativeLayout mNameRl;
    private RelativeLayout mNoticeRl;
    private RelativeLayout mRecordRl;

    private File mHeadImage = null;
    private AlertView mAlertView;
    private SealSearchConversationResult mResult;
    private static final int SEARCH_TYPE_FLAG = 1;
    private Conversation.ConversationType mConversationType;
    private String sPicture;

    private SwitchButton mDisturbBtn, mOverheadBtn;
    private RelativeLayout mClearRl;
    private boolean isClear = false;//是否清空
    private boolean isDissolution = false;//是否解散群组
    private boolean isSignOutlution = false;//是否退出群组
    private MultipleStatusView mMultipleStatusView;
    private View mViewHead, mViewName;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getIntent().getStringExtra("activity") && null != getIntent().getStringExtra("mTargetId")) {
            mTargetId = getIntent().getStringExtra("mTargetId");
            mid = (String) SharedPreferencesUtils.getParameter(GroupDetailActivity.this, "userId", "");
            getActivityGroup(mTargetId, mid, "");

        } else if (null != getIntent().getStringExtra("mTargetId")) {
            mTargetId = getIntent().getStringExtra("mTargetId");
            mid = (String) SharedPreferencesUtils.getParameter(GroupDetailActivity.this, "userId", "");
            getGroup(mid, mTargetId);
        }

        if (!EventBus.getDefault().isRegistered(GroupDetailActivity.this)) {
            EventBus.getDefault().register(GroupDetailActivity.this);
        }

        mTargetId = getIntent().getStringExtra("mTargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");

        updateUI();
    }

    private void getActivityGroup(final String mTargetId, String mid, String s) {

        HomeDbHelper.getChatGroupMember(mid, mTargetId, s, new IOAuthCallBack() {
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
                        mGroupMember = parseData(successJson);
                        if (mGroupMember.size() > 0) {
                            mGridView.setAdapter(new GroupActivityAdapter(GroupDetailActivity.this, mGroupMember, mTargetId, true));
                            mNumTv.setText(data.getString("num") + "人");
                            mNameRl.setVisibility(View.GONE);
                            mHeadRl.setVisibility(View.GONE);//群头像
                            mViewHead.setVisibility(View.GONE);
                            mViewName.setVisibility(View.GONE);
                            mDelBtn.setVisibility(View.GONE);
                            mMultipleStatusView.dismiss();
                        } else {
                            ToastUtils.showToast("该群组已被解散!");
                            mMultipleStatusView.empty(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToast("该群聊已被解散!");
                    mMultipleStatusView.empty(true);
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    private void getGroup(String mid, final String mTargetId) {//获取群聊用户信息
        HomeDbHelper.getGroupInfo(mid, mTargetId, new IOAuthCallBack() {
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
                        mGroupMember = parseData(successJson);
                        sHold = data.getString("hold");
                        if (data.getString("hold").equals("1")) {//1自己创建的群
                            mGridView.setAdapter(new GroupMemberAdapter(GroupDetailActivity.this, mGroupMember, true, mTargetId));
                        } else {
                            mGridView.setAdapter(new GroupMemberAdapter(GroupDetailActivity.this, mGroupMember, false, mTargetId));
                        }

                        mNumTv.setText(data.getString("num") + "人");
                        mNameTv.setText(data.getString("name"));
                        if ("".equals(data.getString("picture"))) {
                            mGroupIv.setImageResource(R.drawable.rc_default_group_portrait);
                        } else {
                            sPicture = data.getString("picture");//群图像
                            ImgLoaderManager.getInstance().showImageView(data.getString("picture"), mGroupIv);
                        }

                        if (sHold.equals("1")) {
                            mDelBtn.setText("解散并删除");
                        } else {
                            mDelBtn.setText("删除并退出");
                        }
                        mMultipleStatusView.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToast("该群聊已被解散!");
                    mMultipleStatusView.empty(true);
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.group_member_size_item:
                Intent intent = new Intent(GroupDetailActivity.this, WholeGroupNameActivity.class);
                intent.putExtra("GroupId", mTargetId);
                startActivity(intent);
                break;
            case R.id.rl_head:
                if (sHold.equals("1")) {
                    photograph();
                } else {
                    mAlertView = new AlertView("提示", "只有群主可以修改群聊头像", "取消", new String[]{"确定"}, null, GroupDetailActivity.this, AlertView.Style.Alert, GroupDetailActivity.this).setCancelable(true);
                    mAlertView.show();
                }
                break;
            case R.id.rl_name:
                if (sHold.equals("1")) {
                    Intent groupn = new Intent(this, UpdateGroupNameActivity.class);
                    groupn.putExtra("GroupId", mTargetId);
                    groupn.putExtra("GroupName", mNameTv.getText().toString());
                    startActivity(groupn);
                } else {
                    mAlertView = new AlertView("提示", "只有群主可以修改群聊名称", "取消", new String[]{"确定"}, null, GroupDetailActivity.this, AlertView.Style.Alert, GroupDetailActivity.this).setCancelable(true);
                    mAlertView.show();
                }
                break;
            case R.id.rl_notice:
                Intent noticeIntent = new Intent(GroupDetailActivity.this, GroupNoticeActivity.class);
                noticeIntent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                noticeIntent.putExtra("GroupId", mTargetId);
                startActivity(noticeIntent);
                break;
            case R.id.rl_record:
                Intent searchIntent = new Intent(GroupDetailActivity.this, SealSearchChattingDetailActivity.class);
                searchIntent.putExtra("filterString", "");
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(mTargetId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
                mResult.setId(mTargetId);
                if (!TextUtils.isEmpty(sPicture)) {
                    mResult.setPortraitUri(sPicture);
                }
                if (!TextUtils.isEmpty(mNameTv.getText().toString())) {
                    mResult.setTitle(mNameTv.getText().toString());
                } else {
                    mResult.setTitle(mTargetId);
                }
                searchIntent.putExtra("searchConversationResult", mResult);
                searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                startActivity(searchIntent);
                break;
            case R.id.rl_clear:
                isClear = true;
                mAlertView = new AlertView("提示", "确定删除聊天记录吗？", "取消", new String[]{"清空"}, null, GroupDetailActivity.this, AlertView.Style.Alert, GroupDetailActivity.this).setCancelable(true);
                mAlertView.show();
                break;
            case R.id.btn_del:
                if (sHold.equals("1")) {
                    isDissolution = true;
                    mAlertView = new AlertView("提示", "确认解散群聊吗？", "取消", new String[]{"确定"}, null, GroupDetailActivity.this, AlertView.Style.Alert, GroupDetailActivity.this).setCancelable(true);
                    mAlertView.show();
                } else {
                    isSignOutlution = true;
                    mAlertView = new AlertView("提示", "确认退出群聊吗？", "取消", new String[]{"确定"}, null, GroupDetailActivity.this, AlertView.Style.Alert, GroupDetailActivity.this).setCancelable(true);
                    mAlertView.show();
                }
                break;
            default:
                break;
        }
    }

    private void photograph() {
        PictureSelector.create(GroupDetailActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(false)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .showCropGrid(false)
                .selectionMedia(selectList)// 是否传入已选图片
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_group_detail);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mGridView = findViewById(R.id.gridview);

        mNumTv = findViewById(R.id.tv_num);
        mNameTv = findViewById(R.id.tv_name);
        mGroupIv = findViewById(R.id.iv_group);

        mDelBtn = findViewById(R.id.btn_del);
        mHeadRl = findViewById(R.id.rl_head);
        mNameRl = findViewById(R.id.rl_name);
        mNoticeRl = findViewById(R.id.rl_notice);
        mRecordRl = findViewById(R.id.rl_record);
        mSizeItemRl = findViewById(R.id.group_member_size_item);

        mClearRl = findViewById(R.id.rl_clear);
        mDisturbBtn = findViewById(R.id.btn_disturb);
        mOverheadBtn = findViewById(R.id.btn_overhead);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);

        mViewHead = findViewById(R.id.view_head);
        mViewName = findViewById(R.id.view_name);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("activity")) {
            mTitleTv.setText("群组设置");
        } else {
            mTitleTv.setText("群聊设置");
        }

        mBackTv.setVisibility(View.VISIBLE);

    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSizeItemRl.setOnClickListener(this);
        mHeadRl.setOnClickListener(this);
        mNameRl.setOnClickListener(this);
        mRecordRl.setOnClickListener(this);
        mNoticeRl.setOnClickListener(this);
        mClearRl.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);

        mDisturbBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    OperationRong.setConverstionNotif(GroupDetailActivity.this, Conversation.ConversationType.GROUP, mTargetId, true);
                } else {
                    OperationRong.setConverstionNotif(GroupDetailActivity.this, Conversation.ConversationType.GROUP, mTargetId, false);
                }
            }
        });

        mOverheadBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    OperationRong.setConversationTop(GroupDetailActivity.this, Conversation.ConversationType.GROUP, mTargetId, true);
                } else {
                    OperationRong.setConversationTop(GroupDetailActivity.this, Conversation.ConversationType.GROUP, mTargetId, false);
                }
            }
        });
    }

    private void updateUI() {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Conversation>() {
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

            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
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

    private ArrayList<GroupInfoEntity.Member> parseData(String result) {
        ArrayList<GroupInfoEntity.Member> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray member = new JSONArray(data.getString("member"));
            Gson gson = new Gson();
            for (int i = 0; i < member.length(); i++) {
                GroupInfoEntity.Member entity = gson.fromJson(member.optJSONObject(i).toString(), GroupInfoEntity.Member.class);
                detail.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(GroupDetailActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(GroupEvent groupEvent) {
        getGroup(mid, mTargetId);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinishActivity(GroupFinisEvent groupFinisEvent) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showProgressDialog("", "加载中...");
            upDataGroup(data);//修改群聊图形
        }
    }

    private void upDataGroup(Intent data) {
        selectList = PictureSelector.obtainMultipleResult(data);
        File outputImage = new File(selectList.get(0).getCompressPath());
        if (outputImage.exists()) {
            mHeadImage = outputImage;
        }

        HomeDbHelper.updateGroup(mNameTv.getText().toString(), mTargetId, mHeadImage, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("更改成功");
                        Group groupInfo = new Group(mTargetId, mNameTv.getText().toString(), Uri.parse(data.getString("picture")));
                        RongIM.getInstance().refreshGroupInfoCache(groupInfo);
                        mGroupIv.setImageURI(Uri.fromFile(mHeadImage));
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
    public void onItemClick(Object o, int position) {

        if (position == 0 && isClear) {//清除聊天记录
            isClear = false;
            if (RongIM.getInstance() != null) {
                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Boolean>() {
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
        } else if (position == 0 && isDissolution) {
            isDissolution = false;
            dimissGroup();
        } else if (position == 0 && isSignOutlution) {
            isDissolution = false;
            signOutGroup();
        }
    }

    private void signOutGroup() {//退出群聊
        HomeDbHelper.exitGroup(mid, mTargetId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        ToastUtils.showToast("退出群聊成功！");
                                        GroupDetailActivity.this.finish();
                                        EventBus.getDefault().post(new GroupDetailEvent());//更新群聊
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTargetId, null);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {

                                    }
                                });
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
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


    private void dimissGroup() {//解散群聊
        HomeDbHelper.dimissGroup(mid, mTargetId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, mTargetId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        ToastUtils.showToast("解散成功");
                                        GroupDetailActivity.this.finish();
                                        EventBus.getDefault().post(new GroupDetailEvent());//更新群聊
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, mTargetId, null);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {

                                    }
                                });
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
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
