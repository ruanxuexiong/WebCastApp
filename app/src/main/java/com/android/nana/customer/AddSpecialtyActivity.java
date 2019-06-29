package com.android.nana.customer;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.ui.CustomWindowDialog;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.SpecialtyGridViewItemAdapter;
import com.android.nana.bean.CategoryEntity;
import com.android.nana.bean.CategoryInfoEntity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/19 0019.
 */

public class AddSpecialtyActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtRight;

    private LinearLayout mLlList,mLlLayout;
    private TextView mTxtDelete,mTxtAdd;
    private GridView mGvList;

    private SpecialtyGridViewItemAdapter mAdapter;

    private CustomWindowDialog mCwdSpecialty; // 专长

    private List<CategoryInfoEntity> mList = new ArrayList<>();

    private String mUserId;
    private String mPersonalExpertiseId;
    private JSONObject mJsonObject;
    private boolean mIsFirst;

    @Override
    protected void bindViews() {

        mUserId = (String) SharedPreferencesUtils.getParameter(AddSpecialtyActivity.this, "userId", "");
        mJsonObject = JSONUtil.getStringToJson(getIntent().getStringExtra("Json"));
        mAdapter = new SpecialtyGridViewItemAdapter(this);
        mCwdSpecialty = new CustomWindowDialog(this);
        setContentView(R.layout.add_specialty);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("个人标签");
        mTxtRight = (TextView) findViewById(R.id.common_txt_right_text);
        mTxtRight.setText("保存");

      //  mTxtType = (TextView) findViewById(R.id.add_specialty_txt_type);
     //   mIvType = (ImageView) findViewById(R.id.add_specialty_iv_type);
       // mLlType = (LinearLayout) findViewById(R.id.add_specialty_ll_type);

        mGvList = (GridView) findViewById(R.id.add_specialty_gv_list);
        mGvList.setAdapter(mAdapter);

        mLlLayout = (LinearLayout) findViewById(R.id.add_specialty_ll_layout);
        mLlList = (LinearLayout) findViewById(R.id.add_specialty_ll_list);

        mTxtAdd = (TextView) findViewById(R.id.add_specialty_txt_add);
        mTxtDelete = (TextView) findViewById(R.id.add_specialty_txt_delete);
    }

    @Override
    protected void init() {

        if (mJsonObject != null) { // 从详情进来
            mPersonalExpertiseId = JSONUtil.get(mJsonObject, "id", "");
            mTxtDelete.setVisibility(View.VISIBLE);
        } else {
            mTxtDelete.setVisibility(View.GONE);
        }

        DBModel dbModel = DBModel.get("queryCategory");
        if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {

            JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
            List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");

            initData(lists);

        } else {

            WebCastDbHelper.lists(mIOAuthCallBack);
        }

    }

    private void selector(CategoryInfoEntity info){

        String name = info.getName();

        //mTxtType.setText(name);
        //ImgLoaderManager.getInstance().showImageView(info.getPicture(), mIvType);

        if (!name.equals("其他")) {

            mAdapter.setList(info.getList());
            mAdapter.notifyDataSetChanged();

            mGvList.setVisibility(View.VISIBLE);
            mLlLayout.setVisibility(View.GONE);

        } else {

            mGvList.setVisibility(View.GONE);
            mLlLayout.setVisibility(View.VISIBLE);

            if (!mIsFirst) {
                add();
                mIsFirst = true;
            }

        }

    }

    private void initData(List<JSONObject> lists){

        mList.clear();

        for (int i = 0; i < lists.size(); i++) {
            JSONObject object = lists.get(i);
            List<JSONObject> list = JSONUtil.getList(object, "lists");

            String id = JSONUtil.get(object, "id", "");
            String name = JSONUtil.get(object, "name", "");
            String picture = JSONUtil.get(object, "picture", "");
            List<CategoryEntity> datas = new ArrayList<>();
            for (JSONObject object1 : list) {
                datas.add(new CategoryEntity(object1.optString("id"),object1.optString("name"),false));
            }
            mList.add(new CategoryInfoEntity(id,name,picture,datas));
        }

        mCwdSpecialty.initDialog(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selector((CategoryInfoEntity) parent.getItemAtPosition(position));

                mCwdSpecialty.dismiss();
            }
        },mList);

        selector(mList.get(0));

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddSpecialtyActivity.this.finish();

            }
        });

        mTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               save();

            }
        });

        mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               delete();

            }
        });

        mTxtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               add();

            }
        });

       /* mLlType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCwdSpecialty.show();

            }
        });
*/
        mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CategoryEntity en = (CategoryEntity) parent.getItemAtPosition(position);
                int index = mAdapter.getList().indexOf(en);
                en.setChecked(!en.isChecked());
                mAdapter.set(index,en);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public void save(){

        List<String> list = new ArrayList<>();
        List<String> expertises = new ArrayList<>();
        if (mGvList.getVisibility() == View.VISIBLE) {
            for (CategoryEntity categoryEntity : mAdapter.getList()) {
                if (categoryEntity.isChecked()) list.add(categoryEntity.getId());
            }
        } else {
            for (int i =0;i<mLlList.getChildCount();i++) {
                View itemView = mLlList.getChildAt(i);
//                EditText mEtName = (EditText) itemView;
                EditText mEtName = (EditText) itemView.findViewById(R.id.add_specialty_other_et_name);
                String text = mEtName.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) expertises.add(text);
            }
        }

        if (list.size() > 0){
            CustomerDbHelper.setPersonalExpertise(mUserId, mPersonalExpertiseId, list, expertises, mSaveDeleteCallBack);
        }else {
            UIHelper.showToast(AddSpecialtyActivity.this, "保存失败，标签类型不能为空！");
        }


    }

    public void add(){

        View itemView = LayoutInflater.from(this).inflate(R.layout.add_specialty_other,null);

        mLlList.addView(itemView);

    }

    public void delete(){

        DialogHelper.customAlert(this, "提示", "确定删除吗?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {

                CustomerDbHelper.deletePersonalExpert(mPersonalExpertiseId, mSaveDeleteCallBack);

            }
        }, null);

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack(){

        @Override
        public void onStartRequest() {

            UIHelper.showOnLoadingDialog(AddSpecialtyActivity.this);

        }

        @Override
        public void getSuccess(String success) {


            BaseModel mResultDetailModel = new ResultRequestModel(success);
            if (mResultDetailModel.mIsSuccess) {

                DBModel.saveOrUpdate("queryCategory",success);
                init();
            } else {

                UIHelper.showToast(AddSpecialtyActivity.this, mResultDetailModel.mMessage);
            }
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(AddSpecialtyActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };

    private IOAuthCallBack mSaveDeleteCallBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {
            UIHelper.showOnLoadingDialog(AddSpecialtyActivity.this);
        }

        @Override
        public void getSuccess(String success) {
            BaseModel mResultDetailModel = new ResultRequestModel(success);
            UIHelper.hideOnLoadingDialog();
            if (mResultDetailModel.mIsSuccess) {
                setResult(RESULT_OK);
                finish();
            } else {
                UIHelper.showToast(AddSpecialtyActivity.this, mResultDetailModel.mMessage);

            }
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(AddSpecialtyActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };
}
