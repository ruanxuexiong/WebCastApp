package com.android.nana.identity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.customer.myincome.MyIncomeWithdrawActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.webview.JumpWebViewActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class user_idActivity extends AppCompatActivity {
    private EditText ed_username, ed_idcard;
    TextView tv_title;
    AppCompatTextView mBackTv;
    private List<LocalMedia> selectList = new ArrayList<>();
    private File mHeadImage, mBackgroundImage;
    private Button idcard_z, idcard_f;
    ImageView idcard_back1, idcard_back2;
    private Button mTxtCommit;
    private String mUserId;
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mUserId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        setContentView(R.layout.activity_user_id);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("个人认证");
        mBackTv = findViewById(R.id.iv_toolbar_back);
        ed_username = findViewById(R.id.ed_username);
        ed_idcard = findViewById(R.id.ed_idcard);
        idcard_z = findViewById(R.id.idcard_z);
        idcard_f = findViewById(R.id.idcard_f);
        idcard_back1 = findViewById(R.id.idcard_back1);
        idcard_back2 = findViewById(R.id.idcard_back2);
        mTxtCommit = findViewById(R.id.id_auth_txt_commit);
        tv_name = findViewById(R.id.tv_name);
        mBackTv.setVisibility(View.VISIBLE);
        mBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        idcard_z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo();
            }
        });
        idcard_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceBg();
            }
        });


        String mName = (String) SharedPreferencesUtils.getParameter(user_idActivity.this, "username", "");
        tv_name.setText("用户名称   " + mName);
        // 提交
        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_idcard.getText().length() != 0 && ed_username.getText().length() != 0) {
                    commit();
                } else {
                    Toast.makeText(user_idActivity.this, "请完善信息后再点提交", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void onJumpWeb(View view) {
        startJumpActivity(JumpWebViewActivity.class, "个人认证规则", "10");
    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {
        Intent intent = new Intent(user_idActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

    private void photo() {//身份证验证
        PictureSelector.create(user_idActivity.this)
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


    private void choiceBg() {//背景
        PictureSelector.create(user_idActivity.this)
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
                .forResult(PictureConfig.BACKGROUND_PHOTO);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    //  mIvPositiveP.setVisibility(View.GONE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mHeadImage = outputImage;
                        }
                        idcard_back1.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                case PictureConfig.BACKGROUND_PHOTO:
                    //    mIvOppositeP.setVisibility(View.GONE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mBackgroundImage = outputImage;
                        }
                        idcard_back2.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void commit() {
        CustomerDbHelper.identityAuthentication(mUserId, mHeadImage, mBackgroundImage, ed_username.getText().toString(), ed_idcard.getText().toString(), mCommitCallBack);
    }


    private IOAuthCallBack mCommitCallBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {
            UIHelper.showOnLoadingDialog(user_idActivity.this);
        }

        @Override
        public void getSuccess(String success) {
            BaseModel mResultDetailModel = new ResultRequestModel(success);
            UIHelper.showToast(user_idActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(user_idActivity.this, "请求失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();
        }
    };

}
