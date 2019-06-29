package com.android.nana.wanted;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.material.JsonBean;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.nana.util.SharedPreferencesUtils.getObject;

/**
 * Created by THINK on 2017/6/27.
 */

public class JobActivity extends BaseActivity implements View.OnClickListener {


    private OptionsPickerView mPickerView;
    private ImageButton mBackBtn;
    private ArrayList<String> mSalary = new ArrayList<>();
    private String mPositionName, mPositionId;
    private ArrayList<String> mList = new ArrayList<>();
    private TextView mTitleTv, mRightTv, mIndustryTv, mSalaryTv, mExpectTv, mAddressTv;
    private OptionsPickerView pvOptions;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();

    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String provinceid, cityid, areaid;
    private String mUid, mLowStr, mTopStr, mJobId, mCityId, mSalaryStr, mJobStr, mProStr;
    private ArrayList<String> mStrList = new ArrayList<>();


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_job);
    }

    @Override
    protected void findViewById() {
        mTitleTv =  findViewById(R.id.common_txt_title);
        mBackBtn =  findViewById(R.id.common_btn_back);
        mRightTv =  findViewById(R.id.common_txt_right_text);
        mSalaryTv =  findViewById(R.id.tv_salary);
        mExpectTv =  findViewById(R.id.tv_expect);
        mAddressTv =  findViewById(R.id.tv_address);
        mIndustryTv =  findViewById(R.id.tv_industry);
    }

    @Override
    protected void init() {

        mRightTv.setTextColor(getResources().getColor(R.color.right));
        mRightTv.setText("保存");

        if (null != getIntent().getStringExtra("edit")) {
            mJobId = getIntent().getStringExtra("jobid");
            mUid = getIntent().getStringExtra("mUid");

            mCityId = getIntent().getStringExtra("cityId");
            mSalaryStr = getIntent().getStringExtra("salary");
            mJobStr = getIntent().getStringExtra("jobStr");
            mProStr = getIntent().getStringExtra("proStr");



            mTitleTv.setText("编辑求职意向");
            loadData(mJobId);
        } else if (null != getIntent().getStringExtra("mUid")) {
            mUid = getIntent().getStringExtra("mUid");
            mTitleTv.setText("添加求职意向");
        }

        initSalaryData();
        openmPickerView();
    }

    private void loadData(String mJobId) {
        WebCastDbHelper.editJobInfo(mJobId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("data"));

                    if (result.getString("state").equals("0")) {
                        mSalaryTv.setTextColor(getResources().getColor(R.color.black_32));
                        mAddressTv.setTextColor(getResources().getColor(R.color.black_32));
                        mExpectTv.setTextColor(getResources().getColor(R.color.black_32));

                        if ("0k-0k".equals(jsonobject1.getString("salary"))) {
                            mSalaryTv.setText("面议");
                        } else if ("50k-50k".equals(jsonobject1.getString("salary"))) {
                            mSalaryTv.setText("50k以上");
                        } else {
                            mSalaryTv.setText(jsonobject1.getString("salary"));
                        }
                        cityid = mCityId;//城市id
                        provinceid = jsonobject1.getString("provinceId");
                        areaid = jsonobject1.getString("areaId");
                        mAddressTv.setText(jsonobject1.getString("province") + "-" + jsonobject1.getString("cityName") + "-" + jsonobject1.getString("area"));
                        mExpectTv.setText(jsonobject1.getString("job"));

                        if (null != jsonobject1.getString("profession")) {
                            JSONArray profession = new JSONArray(jsonobject1.getString("profession"));
                            String labelStr = "";
                            mList.clear();
                            for (int i = 0; i < profession.length(); i++) {
                                mList.add(profession.get(i).toString());
                                labelStr += profession.get(i).toString() + "/";
                            }
                            mIndustryTv.setText(labelStr.substring(0, labelStr.length() - 1));
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

    private void initSalaryData() {
        mSalary.add("面议");
        mSalary.add("2k-5k");
        mSalary.add("5k-10k");
        mSalary.add("10k-15k");
        mSalary.add("15k-25k");
        mSalary.add("25k-50k");
        mSalary.add("50k以上");
    }


    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mIndustryTv.setOnClickListener(this);
        mSalaryTv.setOnClickListener(this);
        mExpectTv.setOnClickListener(this);
        mAddressTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
            case R.id.tv_expect:
                startActivity(new Intent(JobActivity.this, PositionActivity.class));
                break;
            case R.id.tv_industry:
                startActivity(new Intent(JobActivity.this, TradeActivity.class));
                break;
            case R.id.tv_salary:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.tv_address:
                openPickerView();
                break;
            case R.id.common_txt_right_text:
                save();
                break;
        }
    }

    private void save() {
        String profession = null;
        String salary = mSalaryTv.getText().toString();
        String expect = mExpectTv.getText().toString();
        String indus = mIndustryTv.getText().toString();
        String address = mAddressTv.getText().toString();

        if (expect.equals("") || null == expect) {
            ToastUtils.showToast("期望职位不能为空!");
            return;
        } else if ("".equals(indus) || null == indus) {
            ToastUtils.showToast("期望行业不能为空!");
        } else if ("".equals(address) || null == address) {
            ToastUtils.showToast("工作地点不能为空");
            return;
        } else if (salary.equals("") || salary == null) {
            ToastUtils.showToast("期望薪资不能为空");
            return;
        } else {
            if (salary.equals("面议")) {
                salary = "0";
            } else if (salary.equals("50k以上")) {
                salary = "50";
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

            if (mList.size() > 0) {
                profession = listToString(mList, ',');
            }
        }

        if (null != mJobId) {
            if (null != mLowStr && null != mTopStr) {
                WebCastDbHelper.saveJobInfo(mUid, mJobId, profession, mPositionId, provinceid, cityid, areaid, mLowStr, mTopStr, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {
                                JobActivity.this.finish();
                                ToastUtils.showToast(jsonObject1.getString("description"));
                            } else {
                                ToastUtils.showToast(jsonObject1.getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {

                    }
                });
            } else if (null != salary && !"".equals(salary)) {
                showProgressDialog("", "加载中请稍后....");
                WebCastDbHelper.saveJobInfo(mUid, mJobId, profession, mPositionId, provinceid, cityid, areaid, salary, salary, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {
                                JobActivity.this.finish();
                                ToastUtils.showToast(jsonObject1.getString("description"));
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
        } else {
            if (null != mLowStr && null != mTopStr) {
                WebCastDbHelper.saveJobInfo(mUid, "", profession, mPositionId, provinceid, cityid, areaid, mLowStr, mTopStr, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {
                                JobActivity.this.finish();
                                ToastUtils.showToast(jsonObject1.getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {

                    }
                });
            } else if (null != salary && !"".equals(salary)) {
                showProgressDialog("", "加载中请稍后....");
                WebCastDbHelper.saveJobInfo(mUid, "", profession, mPositionId, provinceid, cityid, areaid, salary, salary, new IOAuthCallBack() {
                    @Override
                    public void onStartRequest() {

                    }

                    @Override
                    public void getSuccess(String successJson) {
                        dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(successJson);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            if (jsonObject1.getString("state").equals("0")) {
                                JobActivity.this.finish();
                                ToastUtils.showToast(jsonObject1.getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void getFailue(String failueJson) {

                        Log.e("返回值====", failueJson);
                    }
                });
            }
        }

    }

    private void openPickerView() {

        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);

                provinceid = options1Items.get(options1).getPickerViewId();//省id
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                areaid = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id

                mAddressTv.setTextColor(getResources().getColor(R.color.black_32));
                mAddressTv.setText(tx);
            }
        }).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }

    private void openmPickerView() {//期望薪资
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = mSalary.get(options2);
                mSalaryTv.setTextColor(getResources().getColor(R.color.black_32));
                mSalaryTv.setText(str);
            }
        }).build();
        mPickerView.setNPicker(new ArrayList(), mSalary, new ArrayList());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCityData();

        if (null != getObject(JobActivity.this, "Position", Position.class)) {
            Position mPosition = (Position) SharedPreferencesUtils.getObject(JobActivity.this, "Position", Position.class);
            mPositionName = mPosition.getName();
            mPositionId = mPosition.getId();
            mExpectTv.setTextColor(getResources().getColor(R.color.black_32));
            mExpectTv.setText(mPositionName);
        }

        if (null != SharedPreferencesUtils.getParameter(JobActivity.this, "labels", "") && !"".equals(SharedPreferencesUtils.getParameter(JobActivity.this, "labels", ""))) {
            String labels = (String) SharedPreferencesUtils.getParameter(JobActivity.this, "labels", "");
            mIndustryTv.setTextColor(getResources().getColor(R.color.black_32));
            mList.clear();
            String[] strs = labels.split("/");
            for (int i = 0; i < strs.length; i++) {
                mList.add(strs[i]);
            }
            mIndustryTv.setText(labels.substring(0, labels.length() - 1));
        }

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

}
