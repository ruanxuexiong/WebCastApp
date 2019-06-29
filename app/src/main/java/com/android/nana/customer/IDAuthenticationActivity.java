package com.android.nana.customer;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.base.BaseActivity;
import com.android.common.builder.PhotoBuilder;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class IDAuthenticationActivity extends BaseActivity {
    private EditText ed_username,ed_idcard;
    private TextView mIbBack;
    private TextView mTxtTitle;
    private ImageView mIvPositive, mIvPositiveP, mIvOpposite, mIvOppositeP;
    private TextView mTxtCommit;

    private PhotoBuilder mHeaderPhotoBuilder;
    private PhotoBuilder mHeaderPhotoBuilder2;
    private File mHeadImage, mBackgroundImage;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String mUserId;

    @Override
    protected void bindViews() {
        mUserId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        mHeaderPhotoBuilder = new PhotoBuilder(this);
        mHeaderPhotoBuilder.setAspectXY(22, 15);
        mHeaderPhotoBuilder.setOutputXY(440, 300);
        mHeaderPhotoBuilder2 = new PhotoBuilder(this);
        mHeaderPhotoBuilder2.setAspectXY(22, 15);
        mHeaderPhotoBuilder2.setOutputXY(440, 300);
        setContentView(R.layout.id_auth);

    }

    @Override
    protected void findViewById() {

        mIbBack = findViewById(R.id.iv_toolbar_back);
        mTxtTitle = findViewById(R.id.tv_title);
        mTxtTitle.setText("上传身份证照片");
        ed_username=findViewById(R.id.ed_username);
        ed_idcard=findViewById(R.id.ed_idcard);
        mIvPositive = findViewById(R.id.id_auth_iv_positive);
        mIvPositiveP = findViewById(R.id.id_auth_iv_positive_p);
        mIvOpposite = findViewById(R.id.id_auth_iv_opposite);
        mIvOppositeP = findViewById(R.id.id_auth_iv_opposite_p);
        mTxtCommit = findViewById(R.id.id_auth_txt_commit);
        mIbBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void locationData() {
        super.locationData();
    }

    @Override
    protected void init() {
    }

    @Override
    protected void setListener() {
        mIbBack.setOnClickListener(mBackPullListener);
        // 正面
        mIvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo();
            }
        });

        // 反面
        mIvOpposite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choiceBg();
            }
        });

        // 提交
        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_idcard.getText().length()!=0&&ed_username.getText().length()!=0){
                    commit();
                }
                else {
                    Toast.makeText(mBaseApplication, "请完善信息后再点提交", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void photo() {//身份证验证
        PictureSelector.create(IDAuthenticationActivity.this)
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
        PictureSelector.create(IDAuthenticationActivity.this)
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

    public void commit() {
        CustomerDbHelper.identityAuthentication(mUserId, mHeadImage, mBackgroundImage,ed_username.getText().toString(),ed_idcard.getText().toString(), mCommitCallBack);
    }


    private IOAuthCallBack mCommitCallBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {
            UIHelper.showOnLoadingDialog(IDAuthenticationActivity.this);
        }

        @Override
        public void getSuccess(String success) {
            BaseModel mResultDetailModel = new ResultRequestModel(success);
            UIHelper.showToast(IDAuthenticationActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(IDAuthenticationActivity.this, "请求失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    mIvPositiveP.setVisibility(View.GONE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mHeadImage = outputImage;
                        }
                        mIvPositive.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                case PictureConfig.BACKGROUND_PHOTO:
                    mIvOppositeP.setVisibility(View.GONE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mBackgroundImage = outputImage;
                        }
                        mIvOpposite.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
