package com.android.nana.material;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.CategoryEntity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.LabelEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.LabelsView;
import com.dpizarro.autolabel.library.AutoLabelUI;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINK on 2017/7/1.
 */

public class AddLabelActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton mBackBtn;
    private TextView mTitle, mSaveTv;
    private LabelsView mLabelsView;
    private AutoLabelUI mAutoLabel;
    private String uid, screen;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_add_label);
    }

    @Override
    protected void findViewById() {
        mBackBtn = findViewById(R.id.common_btn_back);
        mTitle = findViewById(R.id.common_txt_title);
        mLabelsView = findViewById(R.id.lv_labe_name);
        mAutoLabel = findViewById(R.id.label_view);
        mSaveTv = findViewById(R.id.common_txt_right_text);
        mAutoLabel.setBackgroundResource(R.drawable.round_corner_background);
    }

    @Override
    protected void init() {
        if (null != getIntent().getStringExtra("screen")) {
            screen = getIntent().getStringExtra("screen");
            mTitle.setText("筛选标签");
        } else {
            mTitle.setText("个人标签");
        }

        mSaveTv.setText("保存");
        Bundle bundle = this.getIntent().getExtras();

        if (null != bundle.getStringArrayList("labes")) {
            for (int i = 0; i < bundle.getStringArrayList("labes").size(); i++) {
                mAutoLabel.addLabel(bundle.getStringArrayList("labes").get(i));
            }
        }

        if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");

            WebCastDbHelper.lists(mIOAuthCallBack);
        }

        mLabelsView.setSelectType(LabelsView.SelectType.MULTI);

        mLabelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                //label是被点击的标签，labelText是标签的文字，isSelect是是否选中，position是标签的位置。
                if (isSelect) {
                    mAutoLabel.addLabel(labelText, position);
                } else {
                    mAutoLabel.removeLabel(position);
                }
            }
        });


        mAutoLabel.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {

                ArrayList<Integer> labels = mLabelsView.getSelectLabels();
                for (int i = 0; i < labels.size(); i++) {
                    if (labels.get(i).equals(position)) {//取消单个选中
                        mLabelsView.clearSingleSelect(labels.get(i));
                    }
                }
            }
        });

    }

    private void initData(List<JSONObject> lists) {
        for (int i = 0; i < lists.size(); i++) {
            JSONObject object = lists.get(i);
            List<JSONObject> list = JSONUtil.getList(object, "lists");

            String id = JSONUtil.get(object, "id", "");
            String name = JSONUtil.get(object, "name", "");
            String picture = JSONUtil.get(object, "picture", "");
            List<CategoryEntity> datas = new ArrayList<>();
            ArrayList<String> label = new ArrayList<>();
            for (JSONObject object1 : list) {
                label.add(object1.optString("name").toString());
            }
            mLabelsView.setLabels(label);
        }
    }

    @Override
    protected void setListener() {
        mBackBtn.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                AddLabelActivity.this.finish();
                break;
            case R.id.common_txt_right_text:

                if (null != screen) {
                    screenSave();
                } else {
                    save();
                }
                break;

        }
    }

    private void screenSave() {//筛选保存
        String labes = "";
        if (mAutoLabel.getLabels().size() > 0) {
            for (int i = 0; i < mAutoLabel.getLabels().size(); i++) {
                labes += mAutoLabel.getLabels().get(i).getText() + ",";
            }
        }

        if ("".equals(labes)) {
            EventBus.getDefault().post(new LabelEvent(mAutoLabel));
            AddLabelActivity.this.finish();
        } else {
            EventBus.getDefault().post(new LabelEvent(mAutoLabel));
            AddLabelActivity.this.finish();
        }

    }

    private void save() {
        String labes = "";
        for (int i = 0; i < mAutoLabel.getLabels().size(); i++) {
            labes += mAutoLabel.getLabels().get(i).getText() + ",";
        }

        WebCastDbHelper.addTags(uid, labes, "", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject jsonobject1 = new JSONObject(jsonobject.getString("result"));
                    if (jsonobject1.getString("state").equals("0")) {
                        ToastUtils.showToast("保存成功！");
                        EventBus.getDefault().post(new LabelEvent(mAutoLabel));
                        AddLabelActivity.this.finish();
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


    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {


        }

        @Override
        public void getSuccess(String success) {


            BaseModel mResultDetailModel = new ResultRequestModel(success);
            if (mResultDetailModel.mIsSuccess) {
                DBModel.saveOrUpdate("queryCategory", success);
                DBModel dbModel = DBModel.get("queryCategory");
                if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {
                    JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
                    List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");
                    initData(lists);
                }
            } else {
            }
        }

        @Override
        public void getFailue(String failueJson) {
        }
    };
}
