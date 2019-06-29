package com.android.nana.material;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.LabelEvent;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.wanted.TradeActivity;
import com.android.nana.wanted.JsonFileReader;
import com.android.nana.wanted.Position;
import com.android.nana.wanted.PositionActivity;
import com.android.nana.widget.LabelsView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINK on 2017/7/25.
 */

public class ScreenActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackBtn;
    private TextView mTitleTv, mCityTv, mRightTv;
    private EditText mMinEt, mHigEt;
    private LinearLayout mMeetLL, mLabelLL, mJobLL, mPositionLL, mTradeLL, mSalaryLL;
    private String uid, state;
    private LabelsView mLabels, mPositionLabels, mTradeLabels, mSalaryLabels;
    private ArrayList<String> label = new ArrayList<>();
    private RadioButton mRadioMale, mRadioFemale;

    private OptionsPickerView pvOptions;
    private String cityid = "1", salary, mLowStr, mTopStr;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private ArrayList<String> mSalary = new ArrayList<>();//期望薪资
    private OptionsPickerView mPickerView;
    private ArrayList<String> mStrList = new ArrayList<>();

    private Position position;//职位
    private ArrayList<String> mProfessList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(ScreenActivity.this)) {
            EventBus.getDefault().register(ScreenActivity.this);
        }
        initSalaryData();
        openmPickerView();
    }


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_screen);
    }

    @Override
    protected void findViewById() {
        mBackBtn = (ImageButton) findViewById(R.id.common_btn_back);
        mTitleTv = (TextView) findViewById(R.id.common_txt_title);

        mCityTv = (TextView) findViewById(R.id.tv_city);
        mRadioMale = (RadioButton) findViewById(R.id.radio_male);
        mRadioFemale = (RadioButton) findViewById(R.id.radio_female);

        mMinEt = (EditText) findViewById(R.id.et_min);
        mHigEt = (EditText) findViewById(R.id.et_hig);
        mMeetLL = (LinearLayout) findViewById(R.id.ll_meet);

        mJobLL = (LinearLayout) findViewById(R.id.ll_job);
        mLabelLL = (LinearLayout) findViewById(R.id.ll_label);
        mRightTv = (TextView) findViewById(R.id.common_txt_right_text);

        mLabels = (LabelsView) findViewById(R.id.lv_name);//个人标签
        parseJson(JsonFileReader.getJson(this, "city_data.json"));

        mPositionLL = (LinearLayout) findViewById(R.id.ll_position);//HR模式
        mSalaryLL = (LinearLayout) findViewById(R.id.ll_salary);
        mTradeLL = (LinearLayout) findViewById(R.id.ll_trade);

        mTradeLabels = (LabelsView) findViewById(R.id.lv_trade_name);
        mSalaryLabels = (LabelsView) findViewById(R.id.lv_salary_name);
        mPositionLabels = (LabelsView) findViewById(R.id.lv_position_name);
    }

    @Override
    protected void init() {
        mTitleTv.setText("筛选");
        mRightTv.setText("确定");
        if (null != getIntent().getStringExtra("state")) {
            if (getIntent().getStringExtra("state").equals("1")) {
                mMeetLL.setVisibility(View.GONE);
                mLabelLL.setVisibility(View.GONE);
                mJobLL.setVisibility(View.VISIBLE);
                state = "1";
            }
        }

        if (null != SharedPreferencesUtils.getParameter(ScreenActivity.this, "userId", "") && !"".equals(SharedPreferencesUtils.getParameter(ScreenActivity.this, "userId", ""))) {
            uid = (String) SharedPreferencesUtils.getParameter(ScreenActivity.this, "userId", "");
        }

    }

    @Override
    protected void setListener() {
        mLabelLL.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mCityTv.setOnClickListener(this);

        mPositionLL.setOnClickListener(this);
        mSalaryLL.setOnClickListener(this);
        mTradeLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.common_txt_right_text:
                if (null != state && state.equals("1")) {
                    hrSave();
                } else {
                    save();
                }

                break;
            case R.id.tv_city:
                cityShow();
                break;
            case R.id.ll_label:
                Intent labelIntent = new Intent(ScreenActivity.this, AddLabelActivity.class);
                Bundle bundle = new Bundle();
                if (label.size() > 0) {
                    bundle.putStringArrayList("labes", label);
                }
                bundle.putString("uid", uid);
                bundle.putString("screen", "screen");
                labelIntent.putExtras(bundle);
                startActivity(labelIntent);
                break;
            case R.id.ll_position:
                Intent intentPosition = new Intent(ScreenActivity.this, PositionActivity.class);
                intentPosition.putExtra("screen", "screen");
                startActivity(intentPosition);
                break;
            case R.id.ll_salary://期望月薪
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.ll_trade://期望行业
                Intent istener = new Intent(ScreenActivity.this, TradeActivity.class);
                Bundle bundleIstener = new Bundle();
                if (mProfessList.size() > 0) {
                    bundleIstener.putStringArrayList("labes", mProfessList);
                }
                bundleIstener.putString("screen", "screen");
                istener.putExtras(bundleIstener);
                startActivity(istener);
                break;
        }
    }


    private void save() {//普通模式
        String tags = null;
        String sex = null;
        String minStr = mMinEt.getText().toString().trim();
        String higStr = mHigEt.getText().toString().trim();
        if (mRadioMale.isChecked()) {
            sex = "1";
        }
        if (mRadioFemale.isChecked()) {
            sex = "2";
        }
        if (label.size() > 0) {
            tags = listToString(label, ',');
        }

        Intent intent = new Intent(ScreenActivity.this, ScreenShowActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("save", "save");
        intent.putExtra("cityid", cityid);
        intent.putExtra("sex", sex);
        intent.putExtra("minStr", minStr);
        intent.putExtra("higStr", higStr);
        intent.putExtra("tags", tags);
        startActivity(intent);
    }

    private void hrSave() {//招聘模式下
        String jobId = "";
        if (null != position) {//期望id
            jobId = position.getId();
        }
        String tags = null;//期望行业
        String sex = null;

        if (mRadioMale.isChecked()) {
            sex = "1";
        }
        if (mRadioFemale.isChecked()) {
            sex = "2";
        }

        if (mProfessList.size() > 0) {
            tags = listToString(label, ',');
        }

        if (null != salary) {
            if (salary.equals("面议")) {
                mLowStr = "0";
                mTopStr = "0";
            } else if (salary.equals("50k以上")) {
                mLowStr = "50";
                mTopStr = "0";
            } else {
                String[] strs = salary.split("-");
                String[] strings;
                for (int i = 0; i < strs.length; i++) {
                    strings = strs[i].split("k");
                    for (int j = 0; j < strings.length; j++) {
                        mStrList.add(strings[j]);
                    }
                }

                for (int k = 0; k < mStrList.size(); k++) {
                    if (k == 0) {
                        mLowStr = mStrList.get(0);
                    } else {
                        mTopStr = mStrList.get(1);
                    }
                }
            }
        }
        Intent intent = new Intent(ScreenActivity.this, ScreenShowActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("jobId", jobId);
        intent.putExtra("tags", tags);
        intent.putExtra("sex", sex);
        intent.putExtra("cityid", cityid);
        intent.putExtra("mLowStr", mLowStr);
        intent.putExtra("mTopStr", mTopStr);
        intent.putExtra("hrSave", "hrSave");
        startActivity(intent);
    }


    private void openmPickerView() {//期望月薪
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                salary = mSalary.get(options2);
                ArrayList<String> salaryList = new ArrayList<>();
                salaryList.add(mSalary.get(options2));
                mSalaryLabels.setLabels(salaryList);
                mSalaryLabels.setVisibility(View.VISIBLE);
                mSalaryLabels.setSelectType(LabelsView.SelectType.NONE);//标签不可选
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mSalary, new ArrayList());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(ScreenActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLabeEvent(LabelEvent labes) {
        label.clear();
        if (labes.mAutoLabel.getLabels().size() > 0) {
            for (int i = 0; i < labes.mAutoLabel.getLabels().size(); i++) {
                label.add(labes.mAutoLabel.getLabels().get(i).getText());
            }
            mLabels.setLabels(label);
            mLabels.setSelectType(LabelsView.SelectType.NONE);//标签不可选
            mLabels.setVisibility(View.VISIBLE);
        } else {
            label.clear();
            mLabels.setVisibility(View.GONE);
        }
    }

    //期望职位
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionEvent(PositionEvent mPositionEvent) {
        position = mPositionEvent.mPosition;
        ArrayList<String> position = new ArrayList<>();
        position.add(mPositionEvent.mPosition.getName());
        mPositionLabels.setLabels(position);
        mPositionLabels.setVisibility(View.VISIBLE);
        mPositionLabels.setSelectType(LabelsView.SelectType.NONE);//标签不可选
    }

    //期望行业
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onIstenerLabeEvent(LabelEvent labes) {
        mProfessList.clear();
        if (labes.mAutoLabel.getLabels().size() > 0) {
            for (int i = 0; i < labes.mAutoLabel.getLabels().size(); i++) {
                mProfessList.add(labes.mAutoLabel.getLabels().get(i).getText());
            }
            mTradeLabels.setLabels(mProfessList);
            mTradeLabels.setSelectType(LabelsView.SelectType.NONE);//标签不可选
            mTradeLabels.setVisibility(View.VISIBLE);
        } else {
            mProfessList.clear();
            mTradeLabels.setVisibility(View.GONE);
        }
    }

    private void cityShow() {

        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options2Items.get(options1).get(options2);

                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id

                mCityTv.setText(tx);
            }
        }).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
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

              /*  if (jsonBean.get(i).getLists().get(c).getLists() == null
                        || jsonBean.get(i).getLists().get(c).getLists().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getLists().get(c).getLists().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getLists().get(c).getLists().get(d).getName();
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }*/
                City_AreaList.add("");
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

    private void initSalaryData() {
        mSalary.add("面议");
        mSalary.add("2k-5k");
        mSalary.add("5k-10k");
        mSalary.add("10k-15k");
        mSalary.add("15k-25k");
        mSalary.add("25k-50k");
        mSalary.add("50k以上");
    }


}
