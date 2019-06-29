package com.android.nana.recruit.companyinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.material.JsonBean;
import com.android.nana.recruit.eventBus.AddressEvent;
import com.android.nana.wanted.JsonFileReader;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/27.
 */

public class WorkAddressActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRightTv;

    private Button mSubBtn;
    private EditText mAddressEt;
    private TextView mAddressTv;
    private LinearLayout mAddressLl;

    private String mProvinceId, mCityId, mAreaId;
    private String mCityName, mAreaName;
    private OptionsPickerView pvOptions;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_work_address);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mAddressLl = findViewById(R.id.ll_address);
        mSubBtn = findViewById(R.id.btn_sub);
        mAddressTv = findViewById(R.id.tv_address);
        mAddressEt = findViewById(R.id.et_address);
    }

    @Override
    protected void init() {
        mTitleTv.setText("工作地点");
        mRightTv.setText("保存");
        mRightTv.setVisibility(View.VISIBLE);
        mBackTv.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.green));
        parseJson(JsonFileReader.getJson(this, "province_data.json"));
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mSubBtn.setOnClickListener(this);
        mAddressLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
            case R.id.btn_sub:
                save();
                break;
            case R.id.ll_address:
                cityShow();
                break;
            default:
                break;
        }
    }

    private void save() {
        EventBus.getDefault().post(new AddressEvent(mProvinceId, mCityId, mAreaId, mCityName, mAreaName, mAddressEt.getText().toString()));
        this.finish();
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

                String tx = options1Items.get(options1).getPickerViewText() + " " +
                        options2Items.get(options1).get(options2) + " " +
                        options3Items.get(options1).get(options2).get(options3);

                mCityName = options2Items.get(options1).get(options2);//城市
                mAreaName = options3Items.get(options1).get(options2).get(options3);//区

                mProvinceId = options1Items.get(options1).getPickerViewId();//省id
                mCityId = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                mAreaId = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id
                mAddressTv.setText(tx);
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

}
