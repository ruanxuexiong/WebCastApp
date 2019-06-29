package com.android.nana.job.position;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.LabelsSecEvent;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.material.JsonBean;
import com.android.nana.wanted.JsonFileReader;
import com.android.nana.wanted.Position;
import com.android.nana.wanted.PositionActivity;
import com.android.nana.wanted.TradeActivity;
import com.android.nana.widget.StateButton;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/9.
 */

public class AddPositionActivity extends BaseActivity implements View.OnClickListener {


    private TextView mBackTv;
    private TextView mTitleTv;
    private LinearLayout mRecruitll;
    private Position position;//职位
    private TextView mRecruitTv;
    private LinearLayout mIndustryLl;
    private TextView mIndustryTv;
    private LinearLayout mSalaryLl;
    private TextView mSalaryTv;

    private LinearLayout mAddressLl;
    private TextView mAddressTv;
    private OptionsPickerView mPickerView;
    private ArrayList<String> mSalary = new ArrayList<>();

    private OptionsPickerView pvOptions;
    private String mProvinceId, mCityId, mAreaId;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private StateButton mSubBtn;
    private TextView mDelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(AddPositionActivity.this)) {
            EventBus.getDefault().register(AddPositionActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_add_postion);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRecruitll = findViewById(R.id.ll_recruit);
        mRecruitTv = findViewById(R.id.tv_recruit);

        mIndustryLl = findViewById(R.id.ll_industry);
        mIndustryTv = findViewById(R.id.tv_industry);
        mSalaryLl = findViewById(R.id.ll_salary);
        mSalaryTv = findViewById(R.id.tv_salary);

        mAddressLl = findViewById(R.id.ll_address);
        mAddressTv = findViewById(R.id.tv_address);

        mSubBtn = findViewById(R.id.btn_add);
        mDelBtn = findViewById(R.id.tv_del);
    }

    @Override
    protected void init() {
        mTitleTv.setText("添加求职意向");
        mBackTv.setVisibility(View.VISIBLE);
        openPickerView();
        initSalaryData();
        initCityData();
    }

    private void initCityData() {
        showProgressDialog("", "加载中请稍后...");
        String successJson = JsonFileReader.getJson(this, "province_data.json");
        parseJson(successJson);
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
                   /* for (int d = 0; d < jsonBean.get(i).getLists().get(c).getLists().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getLists().get(c).getLists().get(d).getName();

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }*/
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

    private void openPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mSalaryTv.setText(mSalary.get(options2));
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mSalary, new ArrayList());
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRecruitll.setOnClickListener(this);
        mIndustryLl.setOnClickListener(this);
        mSalaryLl.setOnClickListener(this);
        mAddressLl.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_recruit:
                startActivity(new Intent(AddPositionActivity.this, PositionActivity.class));
                break;
            case R.id.ll_industry:
                startActivity(new Intent(AddPositionActivity.this, TradeActivity.class));
                break;
            case R.id.ll_salary:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.ll_address:
                openView();
                break;
            case R.id.btn_add:

                break;
            default:
                break;
        }
    }

    //期望职位
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionEvent(PositionEvent mPositionEvent) {
        position = mPositionEvent.mPosition;
        mRecruitTv.setText(position.getName());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLabelsEvent(LabelsSecEvent event) {

        if (event.sec.size() < 0) {
            mIndustryTv.setText("不限");
        } else {
            mIndustryTv.setText(event.sec.size() + "个标签");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(AddPositionActivity.this);
    }

    private void openView() {
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = options1Items.get(options1).getPickerViewText() + " " +
                        options2Items.get(options1).get(options2) /*+ "-" +
                        options3Items.get(options1).get(options2).get(options3)*/;

                mProvinceId = options1Items.get(options1).getPickerViewId();//省id
                mCityId = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                mAreaId = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id
                mAddressTv.setText(tx);
            }
        }).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
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
}
