package com.android.nana.downmenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.nana.R;
import com.android.nana.bean.ProvinceEntity;
import com.android.nana.eventBus.FunctionEvent;
import com.android.nana.eventBus.PositionEvent;
import com.android.nana.eventBus.SchoolEvent;
import com.android.nana.eventBus.SearchEvent;
import com.android.nana.eventBus.SeniorSearchEvent;
import com.android.nana.material.FunctionLabelActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.wanted.JsonFileReader;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.OverrideEditText;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/4.
 * 高级搜索
 */

public class SeniorSearchActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mCloseIv;
    private OverrideEditText mSearchEt;
    private RelativeLayout mSexRl, mPositionRl, mRegionRl, mLabelRl, mSchoolRl;
    private TextView mSexTv, mPositionTv, mRegionTv, mLabelTv, mSchoolTv;
    private LinearLayout mSearchBtn;

    private String mUid;
    private String mOneId = "";//一级ID
    private String mTwoId = "";//二级ID
    private String keyword;
    private OptionsPickerView mPickerView;
    private ArrayList<String> mSex = new ArrayList<>();
    private OptionsPickerView pvOptions;
    private String cityid = "";
    private String mProvinceid = "";
    private String mGender = "-1";//选择性别 -1保密1男2女
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private ArrayList<ProvinceEntity.Area> options1Items = new ArrayList<>();
    private ArrayList<String> options1ItemName = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SeniorSearchActivity.this)) {
            EventBus.getDefault().register(SeniorSearchActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_senior_search);
    }

    @Override
    protected void findViewById() {
        mCloseIv = findViewById(R.id.iv_close);
        mSearchEt = findViewById(R.id.et_search);

        mSexRl = findViewById(R.id.rl_sex);
        mPositionRl = findViewById(R.id.rl_position);
        mRegionRl = findViewById(R.id.rl_region);
        mLabelRl = findViewById(R.id.rl_label);
        mSchoolRl = findViewById(R.id.rl_school);

        mSexTv = findViewById(R.id.tv_sex);
        mPositionTv = findViewById(R.id.tv_position);
        mRegionTv = findViewById(R.id.tv_region);
        mLabelTv = findViewById(R.id.tv_label);
        mSchoolTv = findViewById(R.id.tv_school);
        mSearchBtn = findViewById(R.id.ll_bottom);
    }

    @Override
    protected void init() {

        initData();
        openmPickerView();
        mUid = (String) SharedPreferencesUtils.getParameter(SeniorSearchActivity.this, "userId", "");
        parseJson(JsonFileReader.getJson(this, "city.json"));
    }

    @Override
    protected void setListener() {
        mCloseIv.setOnClickListener(this);
        mSearchEt.setOnClickListener(this);
        mSexRl.setOnClickListener(this);

        mPositionRl.setOnClickListener(this);
        mRegionRl.setOnClickListener(this);
        mLabelRl.setOnClickListener(this);
        mSchoolRl.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                this.finish();
                break;
            case R.id.rl_sex:
                if (mPickerView != null) {
                    mPickerView.show();
                }
                break;
            case R.id.rl_position:
                Intent intent = new Intent(SeniorSearchActivity.this, PositionNameActivity.class);
                intent.putExtra("position", mPositionTv.getText().toString().trim());
                startActivity(intent);
                break;
            case R.id.rl_region:
                cityShow();
                break;
            case R.id.rl_label:
                Intent function = new Intent(SeniorSearchActivity.this, FunctionLabelActivity.class);
                function.putExtra("uid", mUid);
                startActivity(function);
                break;
            case R.id.rl_school:
                Intent schoolIntent = new Intent(SeniorSearchActivity.this, SchoolActivity.class);
                schoolIntent.putExtra("school", mSchoolTv.getText().toString());
                startActivity(schoolIntent);
                break;
            case R.id.ll_bottom:
                String position = mPositionTv.getText().toString().trim();//职位
                String label = mLabelTv.getText().toString().trim();//标签
                String school = mSchoolTv.getText().toString().trim();//学校
                String region = mRegionTv.getText().toString().trim();//地区
                keyword = mSearchEt.getText().toString().trim();//搜索

                if (!TextUtils.isEmpty(mGender) || !"".equals(cityid) || !"".equals(position) || !"".equals(label) || !"".equals(school) || !"".equals(region)) {
                    EventBus.getDefault().post(new SeniorSearchEvent(mGender,position,mProvinceid,cityid,region,mOneId,mTwoId,label,school));
                    SeniorSearchActivity.this.finish();

                } else if (!TextUtils.isEmpty(keyword)) {//搜索框搜索
                    EventBus.getDefault().post(new SearchEvent(keyword));
                    SeniorSearchActivity.this.finish();
                } else {
                    DialogHelper.customAlert(SeniorSearchActivity.this, "提示", "至少输入一个查询条件", new DialogHelper.OnAlertConfirmClick() {
                        @Override
                        public void OnClick(String content) {

                        }

                        @Override
                        public void OnClick() {

                        }
                    }, null);
                }
                break;
            case R.id.et_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(SeniorSearchActivity.this);
    }

    //职位名称
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPositionNameEvent(PositionEvent event) {
        mPositionTv.setText(event.position);
        mPositionTv.setTextColor(getResources().getColor(R.color.green));
    }

    //职能标签
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFunctionEvent(FunctionEvent functionEvent) {//职能标签
        this.mOneId = functionEvent.mOneId;
        this.mTwoId = functionEvent.mTwoId;
        mLabelTv.setTextColor(getResources().getColor(R.color.green));
        mLabelTv.setText(functionEvent.mTitle + "-" + functionEvent.mContent);
    }

    //毕业学校
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSchoolNameEvent(SchoolEvent event) {
        mSchoolTv.setText(event.name);
        mSchoolTv.setTextColor(getResources().getColor(R.color.green));
    }


    private void initData() {
        mSex.add("女");
        mSex.add("男");
        mSex.add("不限");
    }

    private void openmPickerView() {
        mPickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String str = mSex.get(options2);//0不限-1男--2女
                switch (str) {
                    case "不限":
                        mGender = "-1";
                        break;
                    case "男":
                        mGender = "1";
                        break;
                    case "女":
                        mGender = "2";
                        break;
                    default:
                        break;
                }
                mSexTv.setTextColor(getResources().getColor(R.color.green));
                mSexTv.setText(str);
            }
        }).setCancelColor(getResources().getColor(R.color.green)).setSubmitColor(getResources().getColor(R.color.green)).build();
        mPickerView.setNPicker(new ArrayList(), mSex, new ArrayList());
    }

    private void cityShow() {
        pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String mProvince = options1Items.get(options1).getName();
                String tx = options2Items.get(options1).get(options2);

                mProvinceid = options1Items.get(options1).getId();//省
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id

                mRegionTv.setText(mProvince + " " + tx);
                mRegionTv.setTextColor(getResources().getColor(R.color.green));
            }
        }).setCancelColor(getResources().getColor(R.color.green)).setSubmitColor(getResources().getColor(R.color.green)).build();

        pvOptions.setPicker(options1ItemName, options2Items, options3Items);
        pvOptions.show();
    }

    private void parseJson(String successJson) {
        ArrayList<ProvinceEntity.Area> jsonBean = parseData(successJson);
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            options1ItemName.add(jsonBean.get(i).getName());
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）

            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getLists().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getLists().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                City_AreaList.add("");
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            options2Items.add(CityList);

            options3Items.add(Province_AreaList);
        }

        dismissProgressDialog();

    }

    public ArrayList<ProvinceEntity.Area> parseData(String result) {//城市 解析
        ArrayList<ProvinceEntity.Area> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray area = new JSONArray(data.getString("area"));
            Gson gson = new Gson();
            for (int i = 0; i < area.length(); i++) {
                ProvinceEntity.Area entity = gson.fromJson(area.optJSONObject(i).toString(), ProvinceEntity.Area.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
