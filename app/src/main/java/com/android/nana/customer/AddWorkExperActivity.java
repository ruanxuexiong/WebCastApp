package com.android.nana.customer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.builder.PhotoBuilder;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.ui.CustomWindowDialog;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.YearEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class AddWorkExperActivity extends BaseActivity implements View.OnClickListener {

    private SimpleDateFormat sdf;
    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtRight;

    private ImageView mIvImage, mIvImagePhotograph;
    private EditText mTxtName, mTxtJob, mTxtIntroduce;
    private TextView mTxtEntry, mTxtQuit, mTxtDelete;
    private CustomWindowDialog mCwdHeader; // 头像
    private PhotoBuilder mHeaderPhotoBuilder;

    private File mHeadImage;
    private JSONObject mJsonObject;
    private String mWorkHistoryId;
    private long mStartTime, mEndTime;
    private List<String> mYear = new ArrayList<>();
    private OptionsPickerView mTimePicker, mEndTimePicker;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                openmPickerView();
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
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


    private void openmPickerView() {
        mTimePicker = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = options1ItemName.get(options1);
                String moneth = options2Items.get(options1).get(options2);
                mTxtEntry.setText(year + moneth);
            }
        }).build();
        mTimePicker.setPicker(options1ItemName, options2Items, options3Items);


        mEndTimePicker = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String year = mYear.get(options1);
                String moneth = options2Items.get(options1).get(options2);

                if ("至今".equals(year)) {
                    mTxtQuit.setText("至今");
                } else {
                    mTxtQuit.setText(year + moneth);
                }
            }
        }).build();
        mEndTimePicker.setPicker(mYear, options2Items, options3Items);
    }


    @Override
    protected void bindViews() {

        mJsonObject = JSONUtil.getStringToJson(getIntent().getStringExtra("Json"));

        mCwdHeader = new CustomWindowDialog(this);
        mHeaderPhotoBuilder = new PhotoBuilder(this);
        setContentView(R.layout.add_work_exper);
    }

    @Override
    protected void findViewById() {

        mIbBack = findViewById(R.id.common_btn_back);
        mTxtTitle = findViewById(R.id.common_txt_title);
        mTxtTitle.setText("工作经历");
        mTxtRight = findViewById(R.id.common_txt_right_text);
        mTxtRight.setText("保存");

        mIvImage = findViewById(R.id.add_work_exper_iv_image);
        mIvImagePhotograph = findViewById(R.id.add_work_exper_iv_image_bg);
        mTxtName = findViewById(R.id.add_work_exper_edit_company);
        mTxtJob = findViewById(R.id.add_work_exper_edit_job);
        mTxtIntroduce = findViewById(R.id.add_work_exper_edit_introduce);

        mTxtEntry = findViewById(R.id.add_work_exper_txt_entry);
        mTxtQuit = findViewById(R.id.add_work_exper_txt_quit);
        mTxtDelete = findViewById(R.id.add_work_exper_txt_delete);
    }

    @Override
    protected void locationData() {
        super.locationData();

        mCwdHeader.initDialog(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = (String) parent.getItemAtPosition(position);
                switch (name) {
                    case "拍照":
                        mHeaderPhotoBuilder.doCameraPhoto();
                        break;
                    case "相册":
                        mHeaderPhotoBuilder.doLocalPhoto();
                        break;
                }

                mCwdHeader.dismiss();
            }
        }, "拍照", "相册");
    }

    @Override
    protected void init() {

        if (mJsonObject != null) {

            mWorkHistoryId = JSONUtil.get(mJsonObject, "id", "");
            mTxtName.setText(JSONUtil.get(mJsonObject, "name", ""));
            mTxtJob.setText(JSONUtil.get(mJsonObject, "position", ""));
            mTxtIntroduce.setText(JSONUtil.get(mJsonObject, "introduce", ""));
            mTxtEntry.setText(JSONUtil.get(mJsonObject, "beginTime", ""));
            mTxtQuit.setText(JSONUtil.get(mJsonObject, "endTime", ""));
            mIvImagePhotograph.setVisibility(View.GONE);
            mTxtDelete.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(JSONUtil.get(mJsonObject, "picture", ""), mIvImage);
        } else {
            mTxtDelete.setVisibility(View.GONE);
        }

    }

    private static final int CHOOSE_PHOTO = 2;
    private static final int APPLY_PERMISSION = 3;

    @Override
    protected void setListener() {

        mTxtEntry.setOnClickListener(this);
        mTxtQuit.setOnClickListener(this);

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddWorkExperActivity.this.finish();

            }
        });

        mTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();

            }
        });

        // 头像
        mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera();
            }
        });

        // 删除公司
        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete();

            }
        });

    }

    private void camera() {//相册
        PictureSelector.create(AddWorkExperActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(false)// 是否可预览图片
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

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void save() {

        String userId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        String name = mTxtName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showToast("公司名称不能为空");
            return;
        }
        String job = mTxtJob.getText().toString().trim();
        if (TextUtils.isEmpty(job)) {
            showToast("职位不为能空");
            return;
        }
        String beginTime = mTxtEntry.getText().toString();
        if (TextUtils.isEmpty(beginTime)) {
            showToast("入职时间不能为空");
            return;
        }
        String endTime = mTxtQuit.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            showToast("离职时间不能为空");
            return;
        }
        String introduce = mTxtIntroduce.getText().toString().trim();

        mStartTime = getStringToDate(beginTime);
        mEndTime = getStringToDate(endTime);

        if (mStartTime < mEndTime) {
            CustomerDbHelper.setUserWorkHistory(userId, name, job, introduce, beginTime, endTime, mHeadImage, mWorkHistoryId, mIOAuthCallBack);
        } else {
            showToast("入职时间不能大于离职时间");
        }
    }

    private void delete() {

        DialogHelper.customAlert(this, "提示", "确定删除吗?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

                CustomerDbHelper.deleteWorkHistory(mWorkHistoryId, mIOAuthCallBack);

            }
        }, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    mIvImagePhotograph.setVisibility(View.GONE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() > 0) {
                        for (LocalMedia media : selectList) {
                            File outputImage = new File(media.getCompressPath());
                            if (outputImage.exists()) {
                                mHeadImage = outputImage;
                                mIvImage.setImageURI(Uri.fromFile(outputImage));
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }


    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(AddWorkExperActivity.this, "正在处理，请等待...");
        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);

            UIHelper.hideOnLoadingDialog();
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                finish();
            } else {
                UIHelper.showToast(AddWorkExperActivity.this, mResultDetailModel.mMessage);

            }

        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(AddWorkExperActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case APPLY_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_work_exper_txt_entry:

                if (isSHowKeyboard(AddWorkExperActivity.this, v)) {
                }

                if (mTimePicker != null) {
                    mTimePicker.show();
                }

                break;
            case R.id.add_work_exper_txt_quit:

                if (isSHowKeyboard(AddWorkExperActivity.this, v)) {
                }

                if (mEndTimePicker != null) {
                    mEndTimePicker.show();
                }
                break;
        }
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

    public String getTime() {

        long time = System.currentTimeMillis();

        String str = String.valueOf(time);

        return str;

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
}
