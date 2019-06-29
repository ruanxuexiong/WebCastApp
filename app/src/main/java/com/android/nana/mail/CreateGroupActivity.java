package com.android.nana.mail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.SelectEvent;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.model.Conversation;

/**
 * Created by lenovo on 2017/9/18.
 */

public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mRight2Tv;
    private String mid;
    private EditText mGreateNameEt;
    private String mGroupName, mGroupId;
    private String mGroupUids = "";
    private RoundImageView mHeadIv;
    private ArrayList<String> mArrayUid = new ArrayList<>();

    private File mHeadImage = null;
    private StateButton mNextBtn;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mid = getIntent().getStringExtra("mid");
        mArrayUid = getIntent().getStringArrayListExtra("mUids");
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_create_group);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mGreateNameEt = findViewById(R.id.et_group_name);
        mHeadIv = findViewById(R.id.customer_iv_head);
        mNextBtn = findViewById(R.id.btn_next);
    }

    @Override
    protected void init() {
        mTitleTv.setText("编辑群资料");
        mRight2Tv.setCompoundDrawables(null, null, null, null);
        mRight2Tv.setText("跳过");
        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mHeadIv.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                createGroupName();
                break;
            case R.id.customer_iv_head:
                addHead();
                break;
            case R.id.btn_next:
                nextGroupName();
                break;
            default:
                break;
        }
    }

    private void addHead() {//添加群聊头像
        PictureSelector.create(CreateGroupActivity.this)
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


    private void nextGroupName() {

        mGroupName = mGreateNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(mGroupName)) {
            ToastUtils.showToast("群聊名称不能为空！");
            return;
        }
        if (mGroupName.length() == 1) {
            ToastUtils.showToast("群名称不少于2个字");
            return;
        }
        if (AndroidEmoji.isEmoji(mGroupName)) {
            if (mGroupName.length() <= 2) {
                ToastUtils.showToast("群名称不少于2个字");
                return;
            }
        }

        for (int i = 0; i < mArrayUid.size(); i++) {
            if (mArrayUid.size() > 1) {
                mGroupUids += mArrayUid.get(i) + ",";
            } else {
                mGroupUids = mArrayUid.get(i);
            }
        }

        HomeDbHelper.addGroup(mGroupName, mHeadImage, mid, mGroupUids, new IOAuthCallBack() {
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
                        mGroupId = data.getString("groupId");//群聊ID
                        RongIM.getInstance().startConversation(CreateGroupActivity.this, Conversation.ConversationType.GROUP, "group_"+mGroupId, mGroupName);
                        ToastUtils.showToast("创建成功");

                        //关闭选择联系人页面
                        EventBus.getDefault().post(new SelectEvent());
                        finish();
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

    private void createGroupName() {//跳过创建群组

        for (int i = 0; i < mArrayUid.size(); i++) {
            if (mArrayUid.size() > 1) {
                mGroupUids += mArrayUid.get(i) + ",";
            } else {
                mGroupUids = mArrayUid.get(i);
            }
        }

        HomeDbHelper.addGroup("", mHeadImage, mid, mGroupUids, new IOAuthCallBack() {
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
                        mGroupId = data.getString("groupId");//群聊ID
                        mGroupName = data.getString("group");//群聊名称
                        RongIM.getInstance().startConversation(CreateGroupActivity.this, Conversation.ConversationType.GROUP, "group_"+mGroupId, mGroupName);
                        ToastUtils.showToast("创建成功");

                        //关闭选择联系人页面
                        EventBus.getDefault().post(new SelectEvent());
                        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data);
            File outputImage = new File(selectList.get(0).getCompressPath());
            if (outputImage.exists()) {
                mHeadImage = outputImage;
            }
            mHeadIv.setImageURI(Uri.fromFile(outputImage));
        }
    }
}
