package com.android.nana.material;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnDismissListener;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.BaseCheckPermissionsActivity;
import com.android.nana.bean.TagesEntity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.AutographEvent;
import com.android.nana.eventBus.BriefEvent;
import com.android.nana.eventBus.DleLabelEvent;
import com.android.nana.eventBus.FunctionEvent;
import com.android.nana.eventBus.LabelEvent;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JsonFileReader;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by THINK on 2017/7/20.
 */

public class EditUserInfoActivity extends BaseCheckPermissionsActivity implements View.OnClickListener, OnDismissListener, OnItemClickListener {

    private TextView mDesTv, mTitleTv, mRightTv, mSexTv, mCityTv, mPortraitTv;
    private TextView mCompanyTv, mPositionTv, mLabelTv, mBriefTv, mBgTv, mAutographTv;
    private ImageButton mBack;
    private ImageView mBgIv;
    private EditText mNameEt;
    private RelativeLayout mSexRl, mCityRl, mCompanyRl, mPositionRl, mLabelRl, mBriefRl;

    private OptionsPickerView mPickerView;
    private ArrayList<String> mSex = new ArrayList<>();
    private ArrayList<String> label = new ArrayList<>();

    private OptionsPickerView pvOptions;
    private String mUid, workId;
    private String provinceid, cityid, areaid;
    private RelativeLayout mAutographrl;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    //拍照
    private File mHeadImage = null;
    private File mBackgroundImage = null;
    private RoundedImageView mIvHeader;
    private AlertView mAlertView;

    //职能标签
    private String mOneId = "";//一级ID
    private String mTwoId = "";//二级ID
    private String mContent;//内容

    private TextView mBirthTv;
    private RelativeLayout mBirthRl;
    private TimePickerView mPvTime;
    private String mParentName, mLabelName;
    private ImageView mDleIv;
    private  String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(EditUserInfoActivity.this)) {
            EventBus.getDefault().register(EditUserInfoActivity.this);
        }

    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_user_info);
    }

    @Override
    protected void findViewById() {
        mDesTv = findViewById(R.id.tv_des);
        mSexTv = findViewById(R.id.tv_sex);
        mSexRl = findViewById(R.id.rl_sex);
        mCityTv = findViewById(R.id.tv_city);
        mCityRl = findViewById(R.id.rl_city);
        mBack = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mRightTv = findViewById(R.id.common_txt_right_text);
        mCompanyTv = findViewById(R.id.tv_company);
        mCompanyRl = findViewById(R.id.rl_company);
        mPositionTv = findViewById(R.id.tv_position);
        mPositionRl = findViewById(R.id.rl_position);
        mDleIv = findViewById(R.id.iv_dle);

        mLabelRl = findViewById(R.id.rl_label);
        mLabelTv = findViewById(R.id.tv_label);
        mNameEt = findViewById(R.id.et_name);
        mBgIv = findViewById(R.id.iv_bg);
        mBriefRl = findViewById(R.id.rl_brief);
        mBriefTv = findViewById(R.id.tv_brief);
        mIvHeader = findViewById(R.id.iv_head);
        mPortraitTv = findViewById(R.id.tv_portrait);
        mBgTv = findViewById(R.id.tv_bg);
        mAutographrl = findViewById(R.id.rl_autograph);
        mAutographTv = findViewById(R.id.tv_autograph);

        mBirthRl = findViewById(R.id.rl_birth);
        mBirthTv = findViewById(R.id.tv_birth);
    }

    @Override
    protected void init() {
        mRightTv.setText("保存");
        mRightTv.setTextColor(getResources().getColor(R.color.white));

        mTitleTv.setText("编辑基本资料");
        if (null != getIntent().getStringExtra("uid")) {
            mUid = getIntent().getStringExtra("uid");
            editPersonalData(mUid);
        }

        if (null != getIntent().getStringExtra("parentName")) {
            mParentName = getIntent().getStringExtra("parentName");
            mLabelName = getIntent().getStringExtra("labelName");
            mLabelTv.setTextColor(getResources().getColor(R.color.green_03));
            mLabelTv.setText(mParentName + "-" + mLabelName);
        }

        initData();
        openmPickerView();
        parseJson(JsonFileReader.getJson(this, "province_data.json"));
        String str = "带<font color='#F85F48'>" + getResources().getString(R.string.user_info) + "</font>为必填项";
        mDesTv.setText(Html.fromHtml(str));

        initTimePicker();
    }

    private void initTimePicker() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(1949, 0, 01);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020, 11, 31);

        mPvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mBirthTv.setText(getTime(date));
                mBirthTv.setTextColor(getResources().getColor(R.color.green_03));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(20)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .setCancelColor(getResources().getColor(R.color.green))
                .setSubmitColor(getResources().getColor(R.color.green))
                .build();
    }


    private void editPersonalData(String mUid) {// 编辑个人信息
        WebCastDbHelper.editUserInfo(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中请稍后...");
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        JSONObject data = new JSONObject(jsonobject.getString("data"));

                        mBgIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        if (!"".equals(data.getString("backgroundImage"))) {
                            loaderImagBackground(data.getString("backgroundImage"));
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ImageLoader.getInstance().displayImage(data.getString("backgroundImage"), mBgIv);
                        }

                        if (!"null".equals(data.getString("sign"))) {
                            mAutographTv.setTextColor(getResources().getColor(R.color.green_03));
                            mAutographTv.setText(data.getString("sign"));
                        }


                        //头像
                        if (!"".equals(data.getString("avatar"))) {
                            loaderImagHeader(data.getString("avatar"));
                            mPortraitTv.setVisibility(View.GONE);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ImgLoaderManager.getInstance().showImageView(data.getString("avatar"), mIvHeader);
                        }


                        mNameEt.setText(data.getString("username"));
                        mNameEt.setTextColor(getResources().getColor(R.color.green_03));
                        mSexTv.setText(data.getString("sex").equals("0") ? "保密" : data.getString("sex").equals("1") ? "男" : "女");
                        if (!"null".equals(data.getString("province"))) {
                            mCityTv.setText(data.getString("province") + "-" + data.getString("city") + "-" + data.getString("area"));
                            mDleIv.setVisibility(View.VISIBLE);
                        }

                        provinceid = TextUtils.isEmpty(data.getString("provinceId")) ? "0" : data.getString("provinceId");
                        cityid = TextUtils.isEmpty(data.getString("cityId")) ? "0" : data.getString("cityId");
                        areaid = TextUtils.isEmpty(data.getString("districtId")) ? "0" : data.getString("districtId");

                        if (!"null".equals(data.getString("introduce"))) {
                            mBriefTv.setText(data.getString("introduce"));
                            mBriefTv.setTextColor(getResources().getColor(R.color.green_03));
                        }

                        JSONObject work = new JSONObject(data.getString("workHistory"));
                        if (!"null".equals(work)) {
                            mCompanyTv.setText(work.getString("name"));
                            mPositionTv.setText(work.getString("position"));
                            workId = work.getString("id");
                            mCityTv.setTextColor(getResources().getColor(R.color.green_03));
                            mCompanyTv.setTextColor(getResources().getColor(R.color.green_03));
                            mDleIv.setVisibility(View.VISIBLE);
                        }

                        mSexTv.setTextColor(getResources().getColor(R.color.green_03));
                        mPositionTv.setTextColor(getResources().getColor(R.color.green_03));

                        JSONArray tags = new JSONArray(data.getString("property"));
                        if (tags.length() > 0) {
                            ArrayList<TagesEntity> tagesEntities = parseTagesData(tags);
                            mLabelTv.setTextColor(getResources().getColor(R.color.green_03));
                            for (TagesEntity entity : tagesEntities) {
                                mOneId = entity.getParentid();//一级标签
                                mTwoId = entity.getId();//二级标签
                                mParentName = entity.getParentName();
                                mLabelName = entity.getName();
                                mLabelTv.setText(entity.getParentName() + "-" + entity.getName());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("获取数据失败，请稍后重试!");
            }
        });
    }

    @Override
    protected void setListener() {
        mRightTv.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mCityRl.setOnClickListener(this);
        mSexRl.setOnClickListener(this);
        mCompanyRl.setOnClickListener(this);
        mPositionRl.setOnClickListener(this);
        mLabelRl.setOnClickListener(this);
        mBriefRl.setOnClickListener(this);
        mBgTv.setOnClickListener(this);
        mIvHeader.setOnClickListener(this);
        mAutographrl.setOnClickListener(this);
        mBirthRl.setOnClickListener(this);
        mDleIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                String name = mNameEt.getText().toString().trim();
                String gender = mSexTv.getText().toString().trim();
                String sex = gender.equals("保密") ? "0" : gender.equals("男") ? "1" : "2";
                String companyStr = mCompanyTv.getText().toString().trim();
                String position = mPositionTv.getText().toString().trim();
              /*  if (TextUtils.isEmpty(name)) {
                   // showDialogs();
                    return;
                }
                if (TextUtils.isEmpty(sex)) {
                   // showDialogs();
                    return;
                }
                if (TextUtils.isEmpty(companyStr)) {
                   // showDialogs();
                    return;
                }
                if (TextUtils.isEmpty(position)) {
                  //  showDialogs();
                    return;
                }*/
                finish();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
            case R.id.rl_sex:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.rl_city:
                cityShow();
                break;
            case R.id.rl_company:
                Intent intent = new Intent(EditUserInfoActivity.this, EditCompanyActivity.class);
                String company = mCompanyTv.getText().toString().trim();
                intent.putExtra("company", company);
                startActivity(intent);
                break;
            case R.id.rl_position:
                Intent intent1 = new Intent(EditUserInfoActivity.this, EditTitleActivity.class);
                String title = mPositionTv.getText().toString().trim();
                intent1.putExtra("title", title);
                startActivity(intent1);
                break;
            case R.id.rl_label:
                Intent function = new Intent(EditUserInfoActivity.this, FunctionLabelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", mUid);
                bundle.putString("parentName", mParentName);
                bundle.putString("labelName", mLabelName);
                function.putExtras(bundle);
                startActivity(function);
                break;
            case R.id.rl_brief:
                Intent briefIntent = new Intent(EditUserInfoActivity.this, BriefActivity.class);
                briefIntent.putExtra("doc", mBriefTv.getText().toString().trim());
                startActivity(briefIntent);
                break;
            case R.id.iv_head:
                choiceHead();
                break;
            case R.id.tv_bg:
                choiceBg();
                break;
            case R.id.rl_autograph:
                Intent autographIntent = new Intent(EditUserInfoActivity.this, AutographActivity.class);
                autographIntent.putExtra("doc", mAutographTv.getText().toString().trim());
                startActivity(autographIntent);
                break;
            case R.id.rl_birth:
                if (mPvTime != null) {
                    mPvTime.show();
                }
                break;
            case R.id.iv_dle:
                provinceid = "0";
                cityid = "0";
                areaid = "0";
                mDleIv.setVisibility(View.GONE);
                mCityTv.setText("");
                mCityTv.setText("请选择城市");
                mCityTv.setHintTextColor(getResources().getColor(R.color.green_99));
                break;
            default:
                break;
        }
    }

    private void save() {//保存
        name = mNameEt.getText().toString().trim();
        String gender = mSexTv.getText().toString().trim();
        String sex = gender.equals("保密") ? "0" : gender.equals("男") ? "1" : "2";
        String company = mCompanyTv.getText().toString().trim();
        String position = mPositionTv.getText().toString().trim();
        String brief = mBriefTv.getText().toString().trim();
        String city = mCityTv.getText().toString().trim();
        String sign = mAutographTv.getText().toString().trim();
        String birthday = mBirthTv.getText().toString().trim();

        WebCastDbHelper.saveUserInfo(mUid, mHeadImage, mBackgroundImage, name, sex, provinceid, cityid, areaid, company, position, mOneId, mTwoId, brief, workId, sign, birthday, mIOAuthCallBack);
    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {
            showProgressDialog("", "正在加载请稍后...");
        }

        @Override
        public void getSuccess(String successJson) {
            dismissProgressDialog();

            SharedPreferencesUtils.setParameter(EditUserInfoActivity.this, "username", name);
            try {
                JSONObject jsonobject = new JSONObject(successJson);
                JSONObject result = new JSONObject(jsonobject.getString("result"));
                if (result.getString("state").equals("0")) {
                    ToastUtils.showToast(result.getString("description"));
                    EventBus.getDefault().post(new MessageEvent(""));
                    ToastUtils.showToast(result.getString("description"));
                    EditUserInfoActivity.this.finish();
                } else {
                    ToastUtils.showToast(result.getString("description"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {
            ToastUtils.showToast("保存失败，请稍后重试!");
            dismissProgressDialog();
        }
    };

    private void choiceBg() {//背景

        PictureSelector.create(EditUserInfoActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(false)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .withAspectRatio(375, 256)
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .showCropGrid(false)
                .selectionMedia(selectList)// 是否传入已选图片
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .showCropGrid(false)
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪

                .forResult(PictureConfig.BACKGROUND_PHOTO);//结果回调onActivityResult code*/
    }

    private void choiceHead() {//头像

        PictureSelector.create(EditUserInfoActivity.this)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mHeadImage = outputImage;
                        }
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.icon_head_default)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);

                        Glide.with(EditUserInfoActivity.this)
                                .asBitmap()
                                .load(media.getCompressPath())
                                .apply(options)
                                .into(mIvHeader);
                    }
                    break;
                case PictureConfig.BACKGROUND_PHOTO:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mBackgroundImage = outputImage;
                        }
                        mBgIv.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserEvent(MessageEvent messageEvent) {
        mCompanyTv.setTextColor(getResources().getColor(R.color.green_03));
        mCompanyTv.setText(messageEvent.message);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionEvent(PositionEvent positionEvent) {
        mPositionTv.setTextColor(getResources().getColor(R.color.green_03));
        mPositionTv.setText(positionEvent.position);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLabeEvent(LabelEvent labes) {
        String labelStr = "";
        label.clear();
        for (int i = 0; i < labes.mAutoLabel.getLabels().size(); i++) {
            label.add(labes.mAutoLabel.getLabels().get(i).getText());
            labelStr += labes.mAutoLabel.getLabels().get(i).getText() + "/";
        }

        mLabelTv.setTextColor(getResources().getColor(R.color.green_03));
        mLabelTv.setText(labelStr.substring(0, labelStr.length() - 1));
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBriefEvent(BriefEvent brief) {
        mBriefTv.setTextColor(getResources().getColor(R.color.green_03));
        mBriefTv.setText(brief.content);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onAutographEvent(AutographEvent autographEvent) {//朋友圈个性签名
        mAutographTv.setTextColor(getResources().getColor(R.color.green_03));
        mAutographTv.setText(autographEvent.content);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFunctionEvent(FunctionEvent functionEvent) {//职能标签
        this.mOneId = functionEvent.mOneId;
        this.mTwoId = functionEvent.mTwoId;
        mLabelTv.setTextColor(getResources().getColor(R.color.green_03));
        mLabelTv.setText(functionEvent.mTitle + "-" + functionEvent.mContent);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDleLabel(DleLabelEvent event) {
        mLabelTv.setText("");
        mLabelTv.setHint("请选择职能标签");
        mLabelTv.setHintTextColor(getResources().getColor(R.color.green_99));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(EditUserInfoActivity.this);
    }

    private void initData() {
        mSex.add("女");
        mSex.add("男");
        mSex.add("保密");
    }

    private void openmPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = mSex.get(options2);
                mSexTv.setTextColor(getResources().getColor(R.color.green_03));
                mSexTv.setText(str);
            }
        }).setCancelColor(getResources().getColor(R.color.green)).setSubmitColor(getResources().getColor(R.color.green)).build();
        mPickerView.setNPicker(new ArrayList(), mSex, new ArrayList());
    }

    private void parseJson(String successJson) {
        ArrayList<JsonBean> jsonBean = parseData(successJson);
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            options1ItemName.add(jsonBean.get(i).getName());
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）

            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getLists().size(); c++) {//遍历该省份的所有城市

                String CityName = jsonBean.get(i).getLists().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                if (jsonBean.get(i).getLists().get(c).getLists() == null
                        || jsonBean.get(i).getLists().get(c).getLists().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < jsonBean.get(i).getLists().get(c).getLists().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getLists().get(c).getLists().get(d).getName();
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        dismissProgressDialog();

    }

    private void cityShow() {

        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);

                provinceid = options1Items.get(options1).getPickerViewId();//省id
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                areaid = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id
                mCityTv.setTextColor(getResources().getColor(R.color.green_03));
                mCityTv.setText(tx);
                mDleIv.setVisibility(View.VISIBLE);
            }
        }).setCancelColor(getResources().getColor(R.color.green)).setSubmitColor(getResources().getColor(R.color.green)).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    public ArrayList<TagesEntity> parseTagesData(JSONArray tages) {//Gson 解析
        ArrayList<TagesEntity> detail = new ArrayList<>();
        try {
            Gson gson = new Gson();
            for (int i = 0; i < tages.length(); i++) {
                TagesEntity entity = gson.fromJson(tages.optJSONObject(i).toString(), TagesEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private void loaderImagHeader(final String url) {//头像

        new ImgLoaderManager().showImageView(url, mIvHeader, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (TextUtils.isEmpty(s)) return;
                mHeadImage = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void loaderImagBackground(final String url) {//背景

        new ImgLoaderManager(R.color.green_3C).showImageView(url, mBgIv, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mBackgroundImage = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    private void showDialogs() {
        mAlertView = new AlertView("提示", "您的资料还未完善，请完善资料，\n提高吸引力，体验三分钟带来的\n价值吧！", "放弃", new String[]{"继续编辑"}, null, EditUserInfoActivity.this, AlertView.Style.Alert, EditUserInfoActivity.this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0) {
        } else if (position == -1) {
            finish();
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }
}
