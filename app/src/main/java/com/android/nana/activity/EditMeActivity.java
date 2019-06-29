package com.android.nana.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.material.JsonBean;
import com.android.nana.photos.FullyGridLayoutManager;
import com.android.nana.photos.GridImageAdapter;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JsonFileReader;
import com.android.nana.widget.StateButton;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017/8/25.
 */

public class EditMeActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackBtn;
    private TextView mTitleTv, mRightTv;
    private ImageView mAddIv;
    private EditText mThemeEt, mIntroduceEt;

    private File mPhotoImage = null;

    private LinearLayout mAddressRl;//未选中地区
    private ImageView mRightIv, mSeleRightIv;//未选中地区
    private LinearLayout mSelellAddress;//选中地区
    private TextView mSeleAddressTv;//选中地区

    private StateButton mCreateBtn;
    private OptionsPickerView pvOptions;
    private String provinceid, cityid, areaid;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private LinearLayout mDatall, mSeleDatall;
    private ImageView mDataRightIv, mSeleDataIv;
    private TextView mDataTv;
    private TimePickerView mPvTime;

    private SwitchButton mPwdBtn;
    private String strPwd = "";
    private TextView mPwdTv;
    private UserInfo mUserInfo;
    private String id;//活动id
    private String mid;
    private String strCost = "";//群组费用
    private SwitchButton mCostBtn;
    private TextView mCostTv;
    private boolean isPwdBtn = false;//判断是否启用密码
    private boolean isCostBtn = false;//判断是否设置费用
    private String ispay = "0";//是否收费（0=否   1=是）
    private List<LocalMedia> selectList = new ArrayList<>();
    private EditMeEntity entity;

    private RecyclerView mPhotos;
    private GridImageAdapter adapter;
    private int maxSelectNum = 5;
    private TextView mNumTv;
    private boolean isImg = false;//判断是本地选择还是网络图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(EditMeActivity.this)) {
            EventBus.getDefault().register(EditMeActivity.this);
        }

        parseJson(JsonFileReader.getJson(this, "province_data.json"));
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(EditMeActivity.this, "userInfo", UserInfo.class);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_edit);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.common_btn_back);
        mTitleTv = findViewById(R.id.common_txt_title);
        mAddIv = findViewById(R.id.iv_add);
        mThemeEt = findViewById(R.id.et_theme);
        mIntroduceEt = findViewById(R.id.et_introduce);
        mRightTv = findViewById(R.id.common_txt_right_text);

        mAddressRl = findViewById(R.id.rl_address);
        mRightIv = findViewById(R.id.iv_right);
        mSelellAddress = findViewById(R.id.ll_sele);
        mSeleRightIv = findViewById(R.id.iv_sele_right);
        mSeleAddressTv = findViewById(R.id.tv_selet_address);

        mDatall = findViewById(R.id.rl_data);
        mDataTv = findViewById(R.id.tv_data);
        mDataRightIv = findViewById(R.id.iv_data_right);
        mSeleDataIv = findViewById(R.id.iv_sele_data);
        mSeleDatall = findViewById(R.id.ll_sele_data);

        mPwdTv = findViewById(R.id.tv_pwd);
        mPwdBtn = findViewById(R.id.btn_pwd);

        mCostTv = findViewById(R.id.tv_cost);
        mCostBtn = findViewById(R.id.btn_cost);

        mPhotos = findViewById(R.id.recycler);
        mNumTv = findViewById(R.id.tv_num);
    }

    @Override
    protected void init() {

        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setText("完成");
        mTitleTv.setText("编辑群组");

        id = getIntent().getStringExtra("id");
        mid = getIntent().getStringExtra("mid");
        HomeDbHelper.editActivity(id, mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        entity = parseEditData(successJson);
                        mThemeEt.setText(entity.getTitle());
                        mIntroduceEt.setText(entity.getIntroduce());

                        //地址
                        mSeleAddressTv.setTextColor(getResources().getColor(R.color.green_33));
                        mSeleAddressTv.setText(entity.getProvince() + "." + entity.getCity());
                        provinceid = entity.getProvinceId();
                        cityid = entity.getCityId();

                        mRightIv.setVisibility(View.GONE);
                        mSelellAddress.setVisibility(View.VISIBLE);
                        mSeleRightIv.setVisibility(View.VISIBLE);

                        if (entity.getPics().size() > 0) {
                            for (int i = 0; i < entity.getPics().size(); i++) {
                                LocalMedia media = new LocalMedia();
                               media.setPath(entity.getPics().get(i).getPicture());
                               selectList.add(media);
                            }
                           mNumTv.setText("封面图片(" + selectList.size() + "/5)");
                           adapter.setList(selectList);
                            adapter.notifyDataSetChanged();
                        }

                        if (null != entity.getDate()) {
                            mDataTv.setText(entity.getDate());
                            mDataRightIv.setVisibility(View.GONE);
                            mSeleDatall.setVisibility(View.VISIBLE);
                            mSeleRightIv.setVisibility(View.VISIBLE);
                        }

                        if (null != entity.getPass() && !"".equals(entity.getPass())) {
                            mPwdTv.setVisibility(View.VISIBLE);
                            mPwdTv.setText(entity.getPass());
                            strPwd = entity.getPass();
                            mPwdBtn.setChecked(true);
                            isPwdBtn = true;
                        }

                        if (null != entity.getCharge()) {
                            ispay = "1";
                            strCost = entity.getCharge();
                            mCostTv.setVisibility(View.VISIBLE);
                            mCostTv.setText(entity.getCharge() + "元");
                            mCostBtn.setChecked(true);
                            isCostBtn = true;
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
        initTimePicker();
        FullyGridLayoutManager manager = new FullyGridLayoutManager(EditMeActivity.this, 4, GridLayoutManager.VERTICAL, false);
        mPhotos.setLayoutManager(manager);
        adapter = new GridImageAdapter(EditMeActivity.this, onAddPicClickListener);
        adapter.setGridImageNum(mNumTv);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mPhotos.setAdapter(adapter);
    }

   private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(EditMeActivity.this)
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
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }

    };

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mAddIv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mAddressRl.setOnClickListener(this);
        mIntroduceEt.setOnClickListener(this);
        mDatall.setOnClickListener(this);


        mPwdBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {//密码
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    dialogShow();
                } else {
                    strPwd = "";
                    mPwdTv.setVisibility(View.GONE);
                }
            }
        });

        mCostBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {//费用
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (isChecked) {
                    costDialogShow();
                } else {
                    strCost = "";
                    ispay = "0";
                    mCostTv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void costDialogShow() {//费用

        DialogHelper.costAlert(EditMeActivity.this, "设置费用", "请设置参加群组的费用", "确定", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
                openCost(content);
            }

            @Override
            public void OnClick() {

            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                mCostBtn.setChecked(false);
            }
        });
    }

    private void openCost(String content) {//显示费用

        if (!"".equals(content) && Double.valueOf(content) >= 0.1) {
            if (isPwdBtn) {
                mPwdBtn.setChecked(false);
                mPwdTv.setVisibility(View.GONE);
                isPwdBtn = false;
            }

            isCostBtn = true;
            mCostTv.setText(content + "元");
            strCost = content;
            ispay = "1";//是否收费（0=否   1=是）
            mCostTv.setVisibility(View.VISIBLE);
        } else {
            mCostBtn.setChecked(false);
            ToastUtils.showToast("请输入有效金额");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                EditMeActivity.this.finish();
                break;
            case R.id.et_introduce:
                String introduce = mIntroduceEt.getText().toString().trim();
                Intent intent = new Intent(EditMeActivity.this, IntroduceActivity.class);
                intent.putExtra("introduce", introduce);
                startActivity(intent);
                break;
            case R.id.iv_add:
                photograph();
                break;
            case R.id.common_txt_right_text:
                submit();
                break;
            case R.id.rl_address:
                cityShow();
                break;
            case R.id.rl_data:
                if (mPvTime != null) {
                    mPvTime.show();
                }
                break;
        }
    }

    private void submit() {//编辑

        String themeStr = mThemeEt.getText().toString().trim();
        String introduceStr = mIntroduceEt.getText().toString().trim();
        String data = mDataTv.getText().toString().trim();

        if (selectList.size() < 0) {
            ToastUtils.showToast("封面图片不能为空！");
            return;
        }
        if (TextUtils.isEmpty(themeStr)) {
            ToastUtils.showToast("群组名称不能为空！");
            return;
        }

        if (TextUtils.isEmpty(introduceStr)) {
            ToastUtils.showToast("群组介绍不能为空！");
            return;
        }

        if (null == provinceid) {
            ToastUtils.showToast("地址不能为空！");
            return;
        }

        if (TextUtils.isEmpty(data)) {
            ToastUtils.showToast("日期不能为空！");
            return;
        }

        ArrayList<File> files = new ArrayList<>();
        if (selectList.size() > 0) {

            if (isImg) {
                for (int i = 0; i < selectList.size(); i++) {
                    File file = new File(selectList.get(i).getPath());
                    if (file.exists()) {
                        files.add(file);
                    } else {
                        Bitmap bitmap = BitmapUtils.getBitmap(selectList.get(i).getPath());
                        File file1 = BitmapUtils.compressImage(bitmap);
                        if (file1.exists()) {
                            files.add(file1);
                        }
                    }
                }
            } else {
                isImg = false;
                for (int i = 0; i < selectList.size(); i++) {
                    Bitmap bitmap = BitmapUtils.getBitmap(selectList.get(i).getPath());
                    File file = BitmapUtils.compressImage(bitmap);
                    if (file.exists()) {
                        files.add(file);
                    }
                }
            }
        }

        HomeDbHelper.updateActivity(mUserInfo.getId(), id, themeStr, String.valueOf(selectList.size()), files, introduceStr, provinceid, cityid, data, strPwd, ispay, strCost, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中...");
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        ToastUtils.showToast("编辑成功!");
                        EventBus.getDefault().post(new MessageEvent("update"));
                        EditMeActivity.this.finish();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }


    private void cityShow() {
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options1Items.get(options1).getPickerViewText() + " " +
                        options2Items.get(options1).get(options2) + " " +
                        options3Items.get(options1).get(options2).get(options3);

                provinceid = options1Items.get(options1).getPickerViewId();//省id
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                areaid = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id
                mSeleAddressTv.setTextColor(getResources().getColor(R.color.green_33));
                mSeleAddressTv.setText(tx);

                mRightIv.setVisibility(View.GONE);
                mSelellAddress.setVisibility(View.VISIBLE);
                mSeleRightIv.setVisibility(View.VISIBLE);
            }
        }).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }

    private void initTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2020, 11, 31);

        mPvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mDataTv.setText(getTime(date));
                mDataRightIv.setVisibility(View.GONE);
                mSeleDatall.setVisibility(View.VISIBLE);
                mSeleRightIv.setVisibility(View.VISIBLE);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }


    private void photograph() {
        PictureSelector.create(EditMeActivity.this)
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
            isImg = true;
            selectList = PictureSelector.obtainMultipleResult(data);
            mNumTv.setText("封面图片(" + selectList.size() + "/5)");
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();
        }

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onContentIntroduce(MessageEvent messageEvent) {
        if (messageEvent.message != null) {
            mIntroduceEt.setText(messageEvent.message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(EditMeActivity.this);
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
                    City_AreaList.add("");
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

    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
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

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void dialogShow() {

        DialogHelper.pwdAlert(EditMeActivity.this, "设置密码", "请设置创建的群组密码", "确定", "取消", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
                openPwd(content);
            }

            @Override
            public void OnClick() {

            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                mPwdBtn.setChecked(false);
            }
        });
    }

    private void openPwd(String content) {//显示密码
        if (content.length() == 4) {
            if (isCostBtn) {
                mCostBtn.setChecked(false);
                mCostTv.setVisibility(View.GONE);
                isCostBtn = false;
            }
            mPwdTv.setText(content);
            strPwd = content;
            mPwdTv.setVisibility(View.VISIBLE);
            isPwdBtn = true;
        } else {
            mPwdBtn.setChecked(false);
            ToastUtils.showToast("请输入4位数字密码");
        }
    }

    public EditMeEntity parseEditData(String result) {//Gson 解析
        EditMeEntity entity = new EditMeEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), EditMeEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
}
