package com.android.nana.recruit.merecruit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.util.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/3/26.
 * 公司认证
 */

public class AuthCompanyActivity extends BaseActivity implements View.OnClickListener {

    private String id, mid;
    private TextView mBackTv;
    private TextView mTitleTv;
    private Button mSubBtn;
    private ImageView mAuthImgIv;
    private File mPhotoImage = null;
    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");//公司id
            mid = getIntent().getStringExtra("mid");
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_auth_company);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mSubBtn = findViewById(R.id.btn_sub);
        mAuthImgIv = findViewById(R.id.iv_auth_img);
    }

    @Override
    protected void init() {
        mTitleTv.setText("公司认证");
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mAuthImgIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_sub:
                sub();
                break;
            case R.id.iv_auth_img:
                photograph();
                break;
            default:
                break;
        }
    }

    private void sub() {//上传营业执照
        if (null == mPhotoImage) {
            ToastUtils.showToast("营业执照不能为空！");
            return;
        }

        RecruitDbHelper.authCompany(mid, id, mPhotoImage, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {


                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("上传成功！");
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
       selectList = PictureSelector.obtainMultipleResult(data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    File outputImage = new File(selectList.get(0).getCompressPath());
                    if (outputImage.exists()) {
                        mPhotoImage = outputImage;
                    }
                    mAuthImgIv.setImageURI(Uri.fromFile(outputImage));
                }
                break;
            default:
                break;
        }
    }

    private void photograph() {
       PictureSelector.create(AuthCompanyActivity.this)
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
                .selectionMedia(selectList)// 是否传入已选图片
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }
}
