package com.android.nana.identity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.auth.RegisterActivity;
import com.android.nana.builder.ApplyCompanyCodeBuilder;
import com.android.nana.builder.PhoneValidateCodeBuilder;
import com.android.nana.customer.myincome.MyIncomeAddBankCartActivity;
import com.android.nana.customer.myincome.Prepaid_Activity;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.login.ForgetPassActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.pay.SignUtils;
import com.android.nana.webview.JumpWebViewActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class enterprise_twoActivity extends AppCompatActivity {
    TextView tv_title;
    AppCompatTextView mBackTv;
    private List<LocalMedia> selectList = new ArrayList<>();
    private File mHeadImage, mBackgroundImage;
    private Button idcard_z, idcard_f, btn_submit;
    private ImageView idcard_back1, idcard_back2;
    private EditText ed_phone, ed_code, ed_company, ed_credit_code;
    private TextView mTxtGetCode;
    private Checkable checkbox;
    private ApplyCompanyCodeBuilder applyCompanyCodeBuilder;
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_enterprise_two);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("企业认证");
        mBackTv = findViewById(R.id.iv_toolbar_back);
        idcard_z = findViewById(R.id.idcard_z);
        idcard_f = findViewById(R.id.idcard_f);
        idcard_back1 = findViewById(R.id.idcard_back1);
        idcard_back2 = findViewById(R.id.idcard_back2);
        btn_submit = findViewById(R.id.btn_submit);
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
        ed_phone = findViewById(R.id.ed_phone);
        ed_code = findViewById(R.id.ed_code);
        ed_company = findViewById(R.id.ed_company);
        ed_credit_code = findViewById(R.id.ed_credit_code);
        checkbox = findViewById(R.id.checkbox);
        tv_name = findViewById(R.id.tv_name);
        String mName = (String) SharedPreferencesUtils.getParameter(enterprise_twoActivity.this, "username", "");

        tv_name.setText("用户名称   " + mName);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = BaseApplication.getInstance().getCustomerId(enterprise_twoActivity.this);
                String mTime = System.currentTimeMillis() + "";
                submitData(ed_phone.getText().toString().trim(), userId, ed_code.getText().toString().trim(), ed_company.getText().toString().trim(), ed_credit_code.getText().toString().trim(), mHeadImage, mBackgroundImage, mTime);

            }
        });
        mTxtGetCode = findViewById(R.id.get_code);

        applyCompanyCodeBuilder = new ApplyCompanyCodeBuilder(enterprise_twoActivity.this, ed_phone, mTxtGetCode);
        mTxtGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String userId = BaseApplication.getInstance().getCustomerId(enterprise_twoActivity.this);
                String mTime = System.currentTimeMillis() + "";
                String sign = sendCodeSing(userId, ed_phone.getText().toString().trim(), mTime);
                // if (Utils.isMobileExact(mEtPhone.getText().toString())) {
                applyCompanyCodeBuilder.verificationFormat("手机号不能为空", "手机号格式不正确");
                applyCompanyCodeBuilder.countDown(59);
                applyCompanyCodeBuilder.getCardByPhone(userId, ed_phone.getText().toString().trim(), mTime, sign, new ApplyCompanyCodeBuilder.PhoneValidateCodeListener() {

                    @Override
                    public void success(String result) {

                        ResultRequestModel mResultDetailModel = new ResultRequestModel(result);
                        UIHelper.showToast(enterprise_twoActivity.this, mResultDetailModel.mMessage);

                    }

                    @Override
                    public void failue(String failue) {

                        UIHelper.showToast(enterprise_twoActivity.this, failue);
                    }
                });
            }
        });
    }

    public String sign(String phone, String userId, String code, String credit_code, String time) {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("phone", phone);
        map.put("userId", userId);
        map.put("code", code);
        map.put("credit_code", credit_code);
        map.put("time", time);
        return SignUtils.signSort(map);
    }

    private String sendCodeSing(String userId, String phone, String time) {

        Map<String, String> map = new TreeMap<String, String>();
        map.put("phone", phone);
        map.put("userId", userId);
        map.put("time", time);

        return SignUtils.signSort(map);
    }

    private void submitData(String phone, String userId, String code, String company, String credit_code, File pic_1, File pic_2, String time) {
        if (pic_1 == null) {
            ToastUtils.showToast("请上传企业营业执照");
            return;
        }
        if (pic_2 == null) {
            ToastUtils.showToast("请上传认证申请公函");
            return;
        }
        if (TextUtils.isEmpty(company)) {
            ToastUtils.showToast("请输入企业名称");
            return;
        }

        if (TextUtils.isEmpty(credit_code)) {
            ToastUtils.showToast("请输入信用代码");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast("请输入手机验证码");
            return;
        }


        String mSign = sign(phone, userId, code, credit_code, time);
        if (checkbox.isChecked())
            RecruitDbHelper.postApplyCompany(phone, userId, code, company, credit_code, pic_1, pic_2, time, mSign, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                    UIHelper.showOnLoadingDialog(enterprise_twoActivity.this);
                }

                @Override
                public void getSuccess(String successJson) {
                    ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                    if (mResultDetailModel.mIsSuccess) {
                        setResult(RESULT_OK);
                        enterprise_twoActivity.this.finish();
                    }
                    UIHelper.showToast(enterprise_twoActivity.this, mResultDetailModel.mMessage);
                    UIHelper.hideOnLoadingDialog();
                }

                @Override
                public void getFailue(String failueJson) {
                    UIHelper.showToast(enterprise_twoActivity.this, "获取数据失败，请稍后重试");
                    UIHelper.hideOnLoadingDialog();
                }
            });
        else ToastUtils.showToast("请同意企业认证规则");
    }

    private void photo() {//身份证验证
        PictureSelector.create(enterprise_twoActivity.this)
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
        PictureSelector.create(enterprise_twoActivity.this)
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

    public void onJumpWeb(View view) {
        startJumpActivity(JumpWebViewActivity.class, "企业认证规则", "9");
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
                        idcard_z.setText("重新上传");
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
                        idcard_f.setText("重新上传");
                    }
                    break;
                default:
                    break;
            }

        }

    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {
        Intent intent = new Intent(enterprise_twoActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }
}
