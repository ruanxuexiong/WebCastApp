package com.android.nana.recruit.companyinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.photos.FullyGridLayoutManager;
import com.android.nana.photos.GridImageAdapter;
import com.android.nana.recruit.bean.FinanceEntity;
import com.android.nana.recruit.bean.ScaleEntity;
import com.android.nana.recruit.eventBus.CompanyNameEvent;
import com.android.nana.recruit.eventBus.CompanyTheEvent;
import com.android.nana.recruit.eventBus.CompanyTradeEvent;
import com.android.nana.recruit.eventBus.CompanyWebEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/3/23.
 * 公司信息
 */

public class CompanyInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private Button mSubBtn;

    private File mPhotoImage = null;
    private RoundedImageView mLogoIv;

    private RecyclerView mPhotos;
    private GridImageAdapter adapter;
    private int maxSelectNum = 5;
   private List<LocalMedia> selectList = new ArrayList<>();

    private TextView mCompanyNameTv;
    private RelativeLayout mCompanyNameRl;
    private TextView mCompanyTradeTv;
    private String professoinId;//行业ID
    private RelativeLayout mCompanyTradeRl;
    private TextView mCompanyScaleTv;
    private RelativeLayout mCompanyScaleRl;

    private OptionsPickerView mPickerView, mFinancePv;

    private RelativeLayout mFinanceRl;
    private TextView mFinanceTv;

    private RelativeLayout mCompanyWebRl;
    private TextView mCompanyWebTv;
    private RelativeLayout mTheRl;
    private TextView mTheTv;
    private ImageView mTheIv;

    //公司规模
    private String mScaleId;
    private ArrayList<ScaleEntity> mScaleData = new ArrayList<>();
    private ArrayList<String> mScale = new ArrayList<>();
    private String mid;

    //融资阶段
    private String mFinanceId;
    private ArrayList<FinanceEntity> mFinanceData = new ArrayList<>();
    private ArrayList<String> mFinance = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(CompanyInfoActivity.this)) {
            EventBus.getDefault().register(CompanyInfoActivity.this);
        }
        mid = (String) SharedPreferencesUtils.getParameter(CompanyInfoActivity.this, "userId", "");
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_company_info);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mSubBtn = findViewById(R.id.btn_sub);
        mPhotos = findViewById(R.id.recycler);
        mLogoIv = findViewById(R.id.iv_company_logo);

        mCompanyNameTv = findViewById(R.id.tv_company_name);
        mCompanyNameRl = findViewById(R.id.rl_company_name);
        mCompanyTradeTv = findViewById(R.id.tv_trade);
        mCompanyTradeRl = findViewById(R.id.rl_trade);

        mCompanyScaleTv = findViewById(R.id.tv_scale);
        mCompanyScaleRl = findViewById(R.id.rl_scale);
        mFinanceRl = findViewById(R.id.rl_finance);
        mFinanceTv = findViewById(R.id.tv_finance);

        mCompanyWebRl = findViewById(R.id.rl_company_web);
        mCompanyWebTv = findViewById(R.id.tv_company_web);

        mTheRl = findViewById(R.id.rl_the);
        mTheTv = findViewById(R.id.tv_the);
        mTheIv = findViewById(R.id.iv_the);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("公司主页");

        FullyGridLayoutManager manager = new FullyGridLayoutManager(CompanyInfoActivity.this, 4, GridLayoutManager.VERTICAL, false);
        mPhotos.setLayoutManager(manager);
        adapter = new GridImageAdapter(CompanyInfoActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mPhotos.setAdapter(adapter);

        openPickerView();
        initScaleData();
        openFinanceView();
        initFinanceData();
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mLogoIv.setOnClickListener(this);

        mCompanyScaleRl.setOnClickListener(this);
        mCompanyNameRl.setOnClickListener(this);
        mCompanyTradeRl.setOnClickListener(this);
        mFinanceRl.setOnClickListener(this);
        mCompanyWebRl.setOnClickListener(this);
        mTheRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.iv_company_logo:
                photograph();
                break;
            case R.id.rl_company_name:
                Intent intent = new Intent(this, CompanyNameActivity.class);
                intent.putExtra("name", mCompanyNameTv.getText().toString());
                startActivity(intent);
                break;
            case R.id.rl_trade:
                startActivity(new Intent(this, CompanyTradeActivity.class));
                break;
            case R.id.rl_scale:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.rl_finance:
                if (mFinancePv != null) {
                    mFinancePv.show();
                }
                break;
            case R.id.rl_company_web:
                Intent webName = new Intent(this, CompanyWebActivity.class);
                webName.putExtra("webName", mCompanyWebTv.getText().toString());
                startActivity(webName);
                break;
            case R.id.rl_the:
                Intent theIntent = new Intent(this, CompanyTheActivity.class);
                theIntent.putExtra("the", mTheTv.getText().toString());
                startActivity(theIntent);
                break;
            case R.id.btn_sub:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {//保存公司主页信息
        String name = mCompanyNameTv.getText().toString();
        String the = mTheTv.getText().toString();
        String web = mCompanyWebTv.getText().toString();

        if (null == mPhotoImage) {
            ToastUtils.showToast("公司Logo不能为空！");
            return;
        }
      /*  if (selectList.size() <= 0) {
            ToastUtils.showToast("公司照片不能为空！");
            return;
        }*/
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("公司名不能为空！");
            return;
        }
        if (TextUtils.isEmpty(professoinId)) {
            ToastUtils.showToast("请选择公司行业！");
            return;
        }

        if (TextUtils.isEmpty(mScaleId)) {
            ToastUtils.showToast("请选择公司规模！");
            return;
        }
        if (TextUtils.isEmpty(mFinanceId)) {
            ToastUtils.showToast("请选择公司融资阶段！");
            return;
        }
        if (TextUtils.isEmpty(the)) {
            ToastUtils.showToast("公司简介不能为空！");
            return;
        }

        ArrayList<File> files = new ArrayList<>();
      /*  if (selectList.size() > 0) {
            for (final LocalMedia media : selectList) {
                File file = new File(media.getPath());
                if (file.exists()) {
                    files.add(file);
                }
            }
        }*/

     /*   RecruitDbHelper.UpdateCompany(mid, professoinId, mScaleId, mFinanceId, web, the, name, mPhotoImage, files, String.valueOf(selectList.size()), "", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中...");
            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("state"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("添加成功！");
                        CompanyInfoActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectList = PictureSelector.obtainMultipleResult(data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                }
                break;
            case PictureConfig.CHOOSE_REQUEST_LOGO:
                if (resultCode == RESULT_OK) {
                    File outputImage = new File(selectList.get(0).getCompressPath());
                    if (outputImage.exists()) {
                        mPhotoImage = outputImage;
                    }
                    mLogoIv.setImageURI(Uri.fromFile(outputImage));
                }
                break;
            default:
                break;
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(CompanyInfoActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
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
                    .recordVideoSecond(10)
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code*//*
        }
    };

    private void photograph() {
       PictureSelector.create(CompanyInfoActivity.this)
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
                .forResult(PictureConfig.CHOOSE_REQUEST_LOGO);//结果回调onActivityResult code
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(CompanyInfoActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCompanyName(CompanyNameEvent event) {//更新朋友圈消息
        mCompanyNameTv.setText(event.name);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCompanyTradeName(CompanyTradeEvent event) {
        professoinId = event.label.getId();//行业ID
        mCompanyTradeTv.setText(event.label.getName());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCompanyWebName(CompanyWebEvent event) {//公司网站
        mCompanyWebTv.setText(event.name);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCompanyThe(CompanyTheEvent event) {//公司简介
        mTheTv.setText(event.theName);
        if (!"".equals(mTheTv.getText().toString())) {
            mTheIv.setVisibility(View.VISIBLE);
        } else {
            mTheIv.setVisibility(View.GONE);
        }
    }

    private void openPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mScaleId = mScaleData.get(options2).getId();
                mCompanyScaleTv.setText(mScaleData.get(options2).getNum());
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mScale, new ArrayList());
    }

    private void initScaleData() {

        RecruitDbHelper.Companyscale(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseScaleData(successJson).size() > 0) {
                            mScaleData = parseScaleData(successJson);
                            for (int i = 0; i < mScaleData.size(); i++) {
                                mScale.add(mScaleData.get(i).getNum());
                            }
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
    }

    private void openFinanceView() {
        mFinancePv = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mFinanceId = mFinanceData.get(options2).getId();
                mFinanceTv.setText(mFinanceData.get(options2).getFinance());
            }
        }).build();
        mFinancePv.setNPicker(new ArrayList(), mFinance, new ArrayList());
    }

    private void initFinanceData() {
        RecruitDbHelper.CompanyFinace(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseFinanceData(successJson).size() > 0) {
                            mFinanceData = parseFinanceData(successJson);
                            for (int i = 0; i < mFinanceData.size(); i++) {
                                mFinance.add(mFinanceData.get(i).getFinance());
                            }
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
    }

    private ArrayList<ScaleEntity> parseScaleData(String result) {//Gson 解析公司规模
        ArrayList<ScaleEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ScaleEntity entity = gson.fromJson(data.optJSONObject(i).toString(), ScaleEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private ArrayList<FinanceEntity> parseFinanceData(String result) {//Gson 解析融资阶段
        ArrayList<FinanceEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FinanceEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FinanceEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
