package com.android.nana.recruit.companyinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.RecruitDbHelper;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.recruit.bean.EduEntity;
import com.android.nana.recruit.bean.ExperienceEntity;
import com.android.nana.recruit.bean.NatureEntity;
import com.android.nana.recruit.bean.SalaryEntity;
import com.android.nana.recruit.eventBus.AddressEvent;
import com.android.nana.recruit.eventBus.PositionNameEvent;
import com.android.nana.recruit.eventBus.TheEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.Position;
import com.android.nana.wanted.PositionActivity;
import com.bigkoo.pickerview.OptionsPickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/27.
 * 发布职位
 */

public class PostActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRightTv;

    private TextView mProtocolTv;
    private LinearLayout mSeeCompanyLl;
    private LinearLayout mPositionLl;
    private TextView mPositionTv;
    private Position position;//职位

    //职位名称
    private LinearLayout mPositionNameLl;
    private TextView mPositionNameTv;

    //工作地址
    private LinearLayout mAddressLl;
    private TextView mAddressTv;
    private String mProvinceId, mCityId, mAreaId;
    private String id, mid, name;//公司id,公司名
    private TextView mCompanyNameTv;
    private LinearLayout mNatureLl;
    private TextView mNatureTv;
    private LinearLayout mSalaryLl;
    private TextView mSalaryTv;
    private LinearLayout mExperienceLl;
    private TextView mExperienceTv;
    private LinearLayout mEduLl;
    private TextView mEduTv;
    private LinearLayout mTheLl;
    private TextView mTheTv;
    private ImageView mTheIv;
    private Button mSubBtn;

    //薪资
    private String[] salaryls = new String[]{"面议", "3K一下", "3K-5K", "5K-10K", "10K-20K", "20K-50K", "50K以上"};
    private String mSalaryId;
    private ArrayList<String> mSalary = new ArrayList<>();
    private ArrayList<SalaryEntity> mSalaryData = new ArrayList<>();


    //工作性质
    private OptionsPickerView mPickerView, mSarlaryView, mExperienceView, openEduView;
    private String mNatureId;
    private String[] channels = new String[]{"全职", "兼职", "实习"};
    private ArrayList<String> mNature = new ArrayList<>();
    private ArrayList<NatureEntity> mNatureData = new ArrayList<>();

    //经验
    private String[] experience = new String[]{"应届生", "1年以下", "1-3年", "3-5年", "5-10年", "10年以上"};
    private String mExperienceId;
    private ArrayList<String> mExperienceStr = new ArrayList<>();
    private ArrayList<ExperienceEntity> mExperienceData = new ArrayList<>();

    //学历
    private String[] mEduLength = new String[]{"中专以下", "高中", "大专", "本科", "硕士", "博士"};
    private String mEduId;
    private ArrayList<String> mEduStr = new ArrayList<>();
    private ArrayList<EduEntity> mEduData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(PostActivity.this)) {
            EventBus.getDefault().register(PostActivity.this);
        }

        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            name = getIntent().getStringExtra("name");
            mCompanyNameTv.setText(name);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_post);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mProtocolTv = findViewById(R.id.tv_protocol);

        mSeeCompanyLl = findViewById(R.id.ll_see_company);
        mPositionLl = findViewById(R.id.ll_position);
        mPositionTv = findViewById(R.id.tv_position);

        mPositionNameLl = findViewById(R.id.ll_position_name);
        mPositionNameTv = findViewById(R.id.tv_position_name);

        mAddressLl = findViewById(R.id.ll_address);
        mAddressTv = findViewById(R.id.tv_address);
        mCompanyNameTv = findViewById(R.id.tv_company_name);

        mNatureLl = findViewById(R.id.ll_nature);
        mNatureTv = findViewById(R.id.tv_nature);
        mSalaryLl = findViewById(R.id.ll_salary);
        mSalaryTv = findViewById(R.id.tv_salary);

        mExperienceLl = findViewById(R.id.ll_experience);
        mExperienceTv = findViewById(R.id.tv_experience);

        mEduLl = findViewById(R.id.ll_edu);
        mEduTv = findViewById(R.id.tv_edu);
        mTheLl = findViewById(R.id.ll_the);
        mTheTv = findViewById(R.id.tv_the);
        mTheIv = findViewById(R.id.iv_the);
        mSubBtn = findViewById(R.id.btn_sub);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发布职位");
        mRightTv.setText("发布");
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.white));

        initNature();
        initSalary();
        initExperience();
        initEdu();
    }


    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSeeCompanyLl.setOnClickListener(this);
        mPositionLl.setOnClickListener(this);
        mPositionNameLl.setOnClickListener(this);
        mAddressLl.setOnClickListener(this);
        mNatureLl.setOnClickListener(this);
        mSalaryLl.setOnClickListener(this);
        mExperienceLl.setOnClickListener(this);
        mEduLl.setOnClickListener(this);
        mTheLl.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_see_company:
                Intent intentSee = new Intent(this, SeeCompanyInfoActivity.class);
                intentSee.putExtra("mid", mid);
                intentSee.putExtra("id", id);
                startActivity(intentSee);
                break;
            case R.id.ll_position:
                startActivity(new Intent(PostActivity.this, PositionActivity.class));
                break;
            case R.id.ll_position_name:
                Intent intent = new Intent(PostActivity.this, PositionNameActivity.class);
                intent.putExtra("name", mPositionNameTv.getText().toString());
                startActivity(intent);
                break;
            case R.id.ll_address:
                startActivity(new Intent(this, WorkAddressActivity.class));
                break;
            case R.id.ll_nature:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.ll_salary:
                if (mSarlaryView != null) {
                    mSarlaryView.show();
                }
                break;
            case R.id.ll_experience:
                if (mExperienceView != null) {
                    mExperienceView.show();
                }
                break;
            case R.id.ll_edu:
                if (openEduView != null) {
                    openEduView.show();
                }
                break;
            case R.id.ll_the:
                Intent mTheIntent = new Intent(this, TheActivity.class);
                mTheIntent.putExtra("the", mTheTv.getText().toString());
                startActivity(mTheIntent);
                break;
            case R.id.btn_sub:
                publish();
                break;
            case R.id.toolbar_right_2:
                publish();
                break;
            default:
                break;
        }
    }

    private void publish() {

        String mPositionName = mPositionNameTv.getText().toString();
        String mAddressName = mAddressTv.getText().toString();
        String mThe = mTheTv.getText().toString();

        if (null == position) {
            ToastUtils.showToast("职位类型不能为空!");
            return;
        }
        if (TextUtils.isEmpty(mPositionName)) {
            ToastUtils.showToast("职位名称不能为空!");
            return;
        }
        if (TextUtils.isEmpty(mAddressName)) {
            ToastUtils.showToast("工作地点不能为空!");
            return;
        }
        if (null == mNatureId) {
            ToastUtils.showToast("请选择工作性质！");
            return;
        }

        if (null == mSalaryId) {
            ToastUtils.showToast("请选择薪资范围！");
            return;
        }
        if (null == mExperienceId) {
            ToastUtils.showToast("请选择经验要求！");
            return;
        }

        if (null == mEduId) {
            ToastUtils.showToast("请选择最低学历！");
            return;
        }

        if (TextUtils.isEmpty(mThe)) {
            ToastUtils.showToast("请填写职位描述！");
            return;
        }

        RecruitDbHelper.publish(mid, id, position.getId(), mPositionName, mProvinceId, mCityId, mAreaId, mAddressName, mNatureId, mSalaryId, mExperienceId, mEduId, mThe, new IOAuthCallBack() {
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
                        ToastUtils.showToast("发布成功!");
                        PostActivity.this.finish();
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
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(PostActivity.this);
    }

    private void initEdu() {
        for (int i = 0; i < mEduLength.length; i++) {
            EduEntity entity = new EduEntity();
            entity.setId(String.valueOf(i + 1));
            entity.setName(mEduLength[i]);
            mEduData.add(entity);
        }
        for (int i = 0; i < mEduData.size(); i++) {
            mEduStr.add(mEduData.get(i).getName());
        }
        openEduView();
    }

    private void openEduView() {
        openEduView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mEduId = mEduData.get(options2).getId();
                mEduTv.setText(mEduData.get(options2).getName());
            }
        }).build();
        openEduView.setNPicker(new ArrayList(), mEduStr, new ArrayList());
    }

    private void initExperience() {
        for (int i = 0; i < experience.length; i++) {
            ExperienceEntity entity = new ExperienceEntity();
            entity.setId(String.valueOf(i + 1));
            entity.setName(experience[i]);
            mExperienceData.add(entity);
        }
        for (int i = 0; i < mExperienceData.size(); i++) {
            mExperienceStr.add(mExperienceData.get(i).getName());
        }
        openExperienceView();
    }

    private void openExperienceView() {
        mExperienceView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mExperienceId = mExperienceData.get(options2).getId();
                mExperienceTv.setText(mExperienceData.get(options2).getName());
            }
        }).build();
        mExperienceView.setNPicker(new ArrayList(), mExperienceStr, new ArrayList());
    }

    private void initSalary() {//薪资
        for (int i = 0; i < salaryls.length; i++) {
            SalaryEntity entity = new SalaryEntity();
            entity.setId(String.valueOf(i + 1));
            entity.setName(salaryls[i]);
            mSalaryData.add(entity);
        }

        for (int i = 0; i < mSalaryData.size(); i++) {
            mSalary.add(mSalaryData.get(i).getName());
        }
        openSalaryView();
    }

    private void openSalaryView() {
        mSarlaryView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mSalaryId = mSalaryData.get(options2).getId();
                mSalaryTv.setText(mSalaryData.get(options2).getName());
            }
        }).build();
        mSarlaryView.setNPicker(new ArrayList(), mSalary, new ArrayList());
    }

    private void initNature() {//工作性质
        for (int i = 0; i < channels.length; i++) {
            NatureEntity entity = new NatureEntity();
            entity.setId(String.valueOf(i + 1));
            entity.setName(channels[i]);
            mNatureData.add(entity);
        }

        for (int i = 0; i < mNatureData.size(); i++) {
            mNature.add(mNatureData.get(i).getName());
        }
        openPickerView();
    }

    private void openPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mNatureId = mNatureData.get(options2).getId();
                mNatureTv.setText(mNatureData.get(options2).getName());
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mNature, new ArrayList());
    }

    //期望职位
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionEvent(PositionEvent mPositionEvent) {
        position = mPositionEvent.mPosition;
        mPositionTv.setText(position.getName());
    }

    //期望职位
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionName(PositionNameEvent event) {
        mPositionNameTv.setText(event.name);
    }

    //工作地址
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onAddress(AddressEvent event) {
        mProvinceId = event.mProvinceId;//省份
        mCityId = event.mCityId;//城市
        mAreaId = event.mAreaId;//区
        mAddressTv.setText(event.mCityName + event.mAreaName + event.mDetails);
    }

    //工作地址
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onThe(TheEvent event) {
        if (!"".equals(event.describe)) {
            mTheTv.setText(event.describe);
            mTheTv.setVisibility(View.GONE);
            mTheIv.setVisibility(View.VISIBLE);
        } else {
            mTheTv.setText("");
            mTheTv.setHint("请选择");
            mTheTv.setVisibility(View.VISIBLE);
            mTheIv.setVisibility(View.GONE);
        }
    }
}
