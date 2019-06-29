package com.android.nana.material;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.YearEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.MajorEvent;
import com.android.nana.eventBus.SchoolEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
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
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/8/1.
 */

public class EditEducationActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight2Tv;
    private File mPictureFile;
    private ImageView mPictureIv;
    private LinearLayout  mMajorRl;
    private LinearLayout mSchoolRl;
    private TextView mSchoolTv, mMajorTv;
    private RelativeLayout mEducationRl;
    private TextView mEducationTv;
    private RelativeLayout mStartTimeRl, mEndTimeRl;
    private TextView mStartTimeTv, mEndTimeTv;
    private EditText mDocEt;
    private TextView mNumTv;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String education;
    private int value;
    private OptionsPickerView mPickerView;
    private ArrayList<String> mSalary = new ArrayList<>();//学历
    private long mStartTime, mEndTime;
    private SimpleDateFormat sdf;

    private String mEducationExperienceId;
    private MeDataBean.EducationExperiences mItem;
    private List<String> mYear = new ArrayList<>();
    private OptionsPickerView mTimePicker, mEndTimePicker;
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private LinearLayout mDeleteLl;
    private StateButton mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(EditEducationActivity.this)) {
            EventBus.getDefault().register(EditEducationActivity.this);
        }
        initData();
        openmPickerView();

        WebCastDbHelper.getYear(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                ArrayList<YearEntity> mItem = parseData(successJson);
                for (int i = 0; i < mItem.size(); i++) {
                    mYear.add(mItem.get(i).getYear());
                    if (mItem.get(i).getYear().equals("至今")) {
                    } else {
                        options1ItemName.add(mItem.get(i).getYear());
                    }
                    ArrayList<String> mMonth = new ArrayList<>();
                    ArrayList<ArrayList<String>> mProvince = new ArrayList<>();
                    for (int c = 0; c < mItem.get(i).getLists().size(); c++) {
                        mMonth.add(mItem.get(i).getLists().get(c).getMonth());
                        ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                        City_AreaList.add("");
                        mProvince.add(City_AreaList);
                    }
                    options2Items.add(mMonth);
                    options3Items.add(mProvince);
                }

                pickerView();
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_education);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mPictureIv = findViewById(R.id.iv_picture);

        mSchoolRl = findViewById(R.id.rl_school);
        mSchoolTv = findViewById(R.id.tv_school);
        mMajorRl = findViewById(R.id.rl_major);
        mMajorTv = findViewById(R.id.tv_major);
        mEducationRl = findViewById(R.id.rl_education);
        mEducationTv = findViewById(R.id.tv_education);
        mStartTimeTv = findViewById(R.id.tv_start_time);
        mStartTimeRl = findViewById(R.id.rl_start_time);
        mEndTimeTv = findViewById(R.id.tv_end_time);
        mEndTimeRl = findViewById(R.id.rl_end_time);
        mDocEt = findViewById(R.id.et_doc);
        mNumTv = findViewById(R.id.tv_num);
        mDeleteLl = findViewById(R.id.ll_delete);
        mDeleteBtn = findViewById(R.id.btn_delete);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mRight2Tv.setVisibility(View.VISIBLE);
        if ("add".equals(getIntent().getStringExtra("add"))) {
            mTitleTv.setText("添加教育经历");
        } else {
            mTitleTv.setText("编辑教育经历");
            Bundle bundle = getIntent().getExtras();
            mItem = (MeDataBean.EducationExperiences) bundle.getSerializable("item");

            mEducationExperienceId = mItem.getId();
            mSchoolTv.setText(mItem.getName());
            mMajorTv.setText(mItem.getMajor());
            mStartTimeTv.setText(mItem.getBeginTime());
            mEndTimeTv.setText(mItem.getEndTime());
            mDocEt.setText(mItem.getExperience());
            mDeleteLl.setVisibility(View.VISIBLE);
            mEducationTv.setText(mItem.getQualification());
            if (null != mItem.getPicture()) {
                loadPicture(mItem.getPicture());
                ImgLoaderManager.getInstance().showImageView(mItem.getPicture(), mPictureIv);
            }

        }

        mRight2Tv.setText("保存");
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));

        mDocEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNumTv.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        SpannableString ss = new SpannableString("填写教育经历...");//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDocEt.setHint(new SpannedString(ss));
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mPictureIv.setOnClickListener(this);
        mSchoolRl.setOnClickListener(this);
        mMajorRl.setOnClickListener(this);
        mEducationRl.setOnClickListener(this);
        mStartTimeRl.setOnClickListener(this);
        mEndTimeRl.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
            case R.id.iv_picture:
                choice();
                break;
            case R.id.rl_school:
                intent.setClass(EditEducationActivity.this, SchoolActivity.class);
                intent.putExtra("school", mSchoolTv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.rl_major:
                intent.setClass(EditEducationActivity.this, MajorActivity.class);
                intent.putExtra("major", mMajorTv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.rl_education:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.rl_start_time:
                if (isSHowKeyboard(EditEducationActivity.this, view)) {
                }
                if (mTimePicker != null) {
                    mTimePicker.show();
                }
                break;
            case R.id.rl_end_time:

                if (isSHowKeyboard(EditEducationActivity.this, view)) {
                }
                if (mEndTimePicker != null) {
                    mEndTimePicker.show();
                }
                break;
            case R.id.btn_delete:
                delete();
                break;
            default:
                break;
        }
    }

    private void delete() {
        DialogHelper.customAlert(this, "提示", "确定删除吗?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

                CustomerDbHelper.deleteEducationExperience(mEducationExperienceId, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {
                        showProgressDialog("", "加载中...");
                    }

                    @Override
                    public void getSuccess(String successJson) {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            if (result.getString("state").equals("0")) {
                                ToastUtils.showToast("删除成功！");
                                EditEducationActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {
                        UIHelper.showToast(EditEducationActivity.this, "获取数据失败，请稍后重试");
                        dismissProgressDialog();
                    }
                });

            }
        }, null);
    }

    private void save() {
        String userId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        String name = mSchoolTv.getText().toString().trim();
        String job = mMajorTv.getText().toString().trim();
        String beginTime = mStartTimeTv.getText().toString();
        String endTime = mEndTimeTv.getText().toString();
        String doc = mDocEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showToast("学校名称不能空为");
            return;
        }

        if (TextUtils.isEmpty(job)) {
            showToast("所学专业不能为空");
            return;
        }

        if (TextUtils.isEmpty(beginTime)) {
            showToast("入学年份不能为空");
            return;
        }

        if (TextUtils.isEmpty(endTime)) {
            showToast("结束年份不能为空");
            return;
        }

        mStartTime = getStringToDate(beginTime);
        mEndTime = getStringToDate(endTime);
        if (mStartTime < mEndTime) {
            CustomerDbHelper.setEducationExperience(userId, name, job, beginTime, endTime, mPictureFile, mEducationExperienceId, value, doc, mIOAuthCallBack);
        } else {
            showToast("入学年份不能大于结束年份");
        }
    }

    private void choice() {
      PictureSelector.create(EditEducationActivity.this)
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
                            mPictureFile = outputImage;
                        }
                        mPictureIv.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(EditEducationActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSchoolEvent(SchoolEvent event) {//学校名称
        mSchoolTv.setText(event.name);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMajorEvent(MajorEvent event) {//专业名称
        mMajorTv.setText(event.name);
    }

    private void initData() {
        mSalary.add("中专以下");
        mSalary.add("高中");
        mSalary.add("大专");
        mSalary.add("本科");
        mSalary.add("硕士");
        mSalary.add("博士");
    }

    private void openmPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                education = mSalary.get(options2);
                switch (education) {
                    case "中专以下":
                        value = 1;
                        break;
                    case "高中":
                        value = 2;
                        break;
                    case "大专":
                        value = 3;
                        break;
                    case "本科":
                        value = 4;
                        break;
                    case "硕士":
                        value = 5;
                        break;
                    case "博士":
                        value = 6;
                        break;
                    default:
                        break;
                }

                mEducationTv.setText(education);
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mSalary, new ArrayList());
    }

    private ArrayList<YearEntity> parseData(String result) {//Gson 解析
        ArrayList<YearEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                YearEntity entity = gson.fromJson(data.optJSONObject(i).toString(), YearEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public static boolean isSHowKeyboard(Context context, View v) {//判断是否弹出软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            imm.showSoftInput(v, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }

    private void pickerView() {
        mTimePicker = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = options1ItemName.get(options1);
                String moneth = options2Items.get(options1).get(options2);
                mStartTimeTv.setText(year + moneth);
            }
        }).build();
        mTimePicker.setPicker(options1ItemName, options2Items, options3Items);


        mEndTimePicker = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = mYear.get(options1);
                String moneth = options2Items.get(options1).get(options2);

                if ("至今".equals(year)) {
                    mEndTimeTv.setText("至今");
                } else {
                    mEndTimeTv.setText(year + moneth);
                }
            }
        }).build();
        mEndTimePicker.setPicker(mYear, options2Items, options3Items);
    }

    public long getStringToDate(String time) {
        sdf = new SimpleDateFormat("yyyy年MM月");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {
            showProgressDialog("", "加载中...");
        }

        @Override
        public void getSuccess(String successJson) {

            dismissProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(successJson);

                JSONObject result = new JSONObject(jsonObject.getString("result"));
                if (result.getString("state").equals("0")) {
                    ToastUtils.showToast("添加成功");
                    EditEducationActivity.this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {

        }
    };


    private void loadPicture(final String url) {//背景

        new ImgLoaderManager(R.color.green_3C).showImageView(url, mPictureIv, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mPictureFile = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

}
