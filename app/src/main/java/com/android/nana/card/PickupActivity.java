package com.android.nana.card;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.PickupEvent;
import com.android.nana.material.JsonBean;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JsonFileReader;
import com.android.nana.widget.StateButton;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/2/1.
 */

public class PickupActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;

    private EditText mNameEt;
    private EditText mPhoneEt;
    private EditText mAddressEt;
    private TextView mCityTv;
    private RelativeLayout mCityRl;
    private StateButton mSubmitBtn;
    private String mCardId;
    private String mid;

    private OptionsPickerView pvOptions;
    private String provinceid, cityid, areaid;
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
        setContentView(R.layout.activity_pickup);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);

        mNameEt = findViewById(R.id.et_name);
        mPhoneEt = findViewById(R.id.et_phone);
        mAddressEt = findViewById(R.id.et_address);
        mCityTv = findViewById(R.id.tv_city);
        mCityRl = findViewById(R.id.rl_city);
        mSubmitBtn = findViewById(R.id.btn_submit);
    }

    @Override
    protected void init() {
        parseJson(JsonFileReader.getJson(this, "province_data.json"));
        mTitleTv.setText("填写收件信息");
        mBackTv.setVisibility(View.VISIBLE);
        mCardId = getIntent().getStringExtra("cardId");
        mid = getIntent().getStringExtra("mid");
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mCityRl.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                PickupActivity.this.finish();
                break;
            case R.id.rl_city:
                cityShow();
                break;
            case R.id.btn_submit:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {
        String name = mNameEt.getText().toString();
        String phone = mPhoneEt.getText().toString();
        String address = mAddressEt.getText().toString();
        String city = mCityTv.getText().toString();

        if (TextUtils.isEmpty(name)){
            ToastUtils.showToast("收件人不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)){
            ToastUtils.showToast("联系方式不能为空");
            return;
        }

        if (TextUtils.isEmpty(city)){
            ToastUtils.showToast("所在地区不能为空");
            return;
        }
        if (TextUtils.isEmpty(address)){
            ToastUtils.showToast("请输入你的详细地址");
            return;
        }
        CardDbHelper.orderCard(mid, mCardId, name, phone, provinceid, cityid, areaid, address, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")){
                        pickup();
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

                String tx = options1Items.get(options1).getPickerViewText() + "-" +
                        options2Items.get(options1).get(options2) + "-" +
                        options3Items.get(options1).get(options2).get(options3);

                provinceid = options1Items.get(options1).getPickerViewId();//省id
                cityid = options1Items.get(options1).getLists().get(options2).getPickerViewId();//市id
                areaid = options1Items.get(options1).getLists().get(options2).getLists().get(options3).getPickerViewId();//区id
                mCityTv.setTextColor(getResources().getColor(R.color.green_03));
                mCityTv.setText(tx);
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

    private void pickup() {//删除
        DialogHelper.customAlert(PickupActivity.this, "提交成功", "我们将在近期给您打印并寄送", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                EventBus.getDefault().post(new PickupEvent());
                PickupActivity.this.finish();
            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                EventBus.getDefault().post(new PickupEvent());
                PickupActivity.this.finish();
            }
        });
    }
}
