package com.android.nana.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.models.DBModel;
import com.android.common.ui.CustomWindowDialog;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.PurposeGridViewItemAdapter;
import com.android.nana.bean.PurposeEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class FilterActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private TextView mTxtAgeMin, mTxtAgeMax, mTxtProvince;
    private CustomWindowDialog mCwdAge; // 年龄
    // private GridView mGvList; // 注册目的

    private boolean mIsAgeMan;
    private PurposeGridViewItemAdapter mGridViewAdapter;

    private List<PurposeEntity> mPurposeList;

    // 筛选
    private String mBeginAge = "0";
    private String mEndAge = "0";
    private String mPurposeId = "0";
    private String mProvinceId = "0";
    private String mGender = "0";
    private String mOrderByType = "comprehensive";

    @Override
    protected void bindViews() {

        mCwdAge = new CustomWindowDialog(this);
        mGridViewAdapter = new PurposeGridViewItemAdapter(this);

        // 城市
        //initRegions();

        // 年龄
        String[] ageStr = new String[45];
        for (int i = 0; i < 45; i++) ageStr[i] = String.valueOf(16 + i);

        mCwdAge.initDialog(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mIsAgeMan) {
                    mTxtAgeMax.setText((String) parent.getItemAtPosition(position));
                    mBeginAge = (String) parent.getItemAtPosition(position);
                } else {
                    mTxtAgeMin.setText((String) parent.getItemAtPosition(position));
                    mEndAge = (String) parent.getItemAtPosition(position);
                }

                mCwdAge.dismiss();
            }
        }, ageStr);

        // 注册目的
        mPurposeList = new ArrayList<>();
        DBModel dbModel = DBModel.get("queryPurpose");
        if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {
            JSONObject object = JSONUtil.getStringToJson(dbModel.Description);
            JSONArray array = JSONUtil.getArray(object, "data");
            for (int i = 0; i < array.length(); i++) {
                mPurposeList.add(new PurposeEntity(array.optJSONObject(i).optString("id"), array.optJSONObject(i).optString("name")));
            }
        }

        setContentView(R.layout.filter);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("筛选");

       /* mTxtAgeMin = (TextView) findViewById(R.id.webcast_selector_txt_age_min);
        mTxtAgeMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAgeMan = false;
                mCwdAge.show();
            }
        });
        mTxtAgeMax = (TextView) findViewById(R.id.webcast_selector_txt_age_max);
        mTxtAgeMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsAgeMan = true;
                mCwdAge.show();
            }
        });
        final RadioGroup mRgSex = (RadioGroup) findViewById(R.id.webcast_selector_rg_sex);
        mRgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.webcast_selector_rb_man: // 男
                        mGender = "1";
                        break;
                    case R.id.webcast_selector_rb_woman: // 女
                        mGender = "2";
                        break;
                    case R.id.webcast_selector_rb_gender: // 不限
                        mGender = "0";
                        break;
                }
            }
        });*/
      /*  mGvList = (GridView) findViewById(R.id.webcast_selector_gv_list);
        mGvList.setAdapter(mGridViewAdapter);
        mGridViewAdapter.setList(mPurposeList);
        mGridViewAdapter.notifyDataSetChanged();
        mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PurposeEntity en = (PurposeEntity) parent.getItemAtPosition(position);
                mPurposeId = en.getId();
                mGridViewAdapter.setChecked(en);
                mGridViewAdapter.notifyDataSetChanged();
            }
        });*/

        // mTxtProvince = (TextView) findViewById(R.id.webcast_selector_txt_city);

      /*  TextView mTxtReset = (TextView) findViewById(R.id.webcast_selector_txt_reset);
        mTxtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOrderByType = "comprehensive";

                mTxtAgeMin.setText("18");
                mBeginAge = "0";
                mTxtAgeMax.setText("30");
                mEndAge = "0";

                for (int i = 0; i < mRgSex.getChildCount(); i++) {
                    ((RadioButton) mRgSex.getChildAt(i)).setChecked(false);
                }
                mGender = "0";

                // mTxtProvince.setText("重庆");
                mProvinceId = "0";

                mGridViewAdapter.clear();
                mGridViewAdapter.notifyDataSetChanged();
                mPurposeId = "0";

            }
        });*/
     /*   TextView mTxtCommit = (TextView) findViewById(R.id.webcast_selector_txt_commit);
        mTxtCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetWorkUtils.isNetworkConnected(getApplicationContext())) {
                    if (!BaseApplication.getInstance().checkLogin(getApplication())) {
                        getApplication().startActivity(new Intent(getApplication(), com.android.webcast.auth.WelcomeActivity.class));
                        return;
                    }

                    Intent intent = new Intent(FilterActivity.this, AnchorListActivity.class);
                    intent.putExtra("BeginAge", mBeginAge);
                    intent.putExtra("EndAge", mEndAge);
                    intent.putExtra("Gender", mGender);
                    intent.putExtra("PurposeId", mPurposeId);
                    intent.putExtra("ProvinceId", mProvinceId);
                    intent.putExtra("OrderByType", mOrderByType);
                    startActivity(intent);
                } else {
                    UIHelper.showToast(getApplicationContext(), "当前网络不可用，请稍后重试！");
                }


            }
        });*/

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

    }


}
