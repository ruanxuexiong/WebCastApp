package com.android.nana.webcast;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseFragmentActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.common.helper.UIHelper;
import com.android.common.models.BaseModel;
import com.android.common.models.DBModel;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.bean.CategoryEntity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.CategoryEvent;
import com.android.nana.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22 0022.
 * 全部页面
 */

public class CategoryListActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ImageButton mIbBack, mScreenBtn;
    private TextView mTxtTitle;
    private FragmentBuilder mFragmentBuilder;

    /**
     * Item宽度
     */
    private int mItemWidth = 0;

    private RadioGroup mRgList;
    private String mTitle;
    private String mCategoryId;
    private String mChildCategoryId;

    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(CategoryListActivity.this)) {
            EventBus.getDefault().register(CategoryListActivity.this);
        }
    }

    @Override
    protected void bindViews() {

        mTitle = getIntent().getStringExtra("Name");
        mCategoryId = getIntent().getStringExtra("CategoryId");
        mChildCategoryId = getIntent().getStringExtra("ChildCategoryId");
        mItemWidth = UIUtil.getInstance().getWindowsWidth(this) / 6 + 10;

        if (null != getIntent().getStringExtra("count")) {
            count = getIntent().getStringExtra("count");
        }

        setContentView(R.layout.category_list);

    }

    @Override
    protected void locationData() {

    }

    @Override
    protected void findViewById() {

        mIbBack = findViewById(R.id.common_btn_back);
        mTxtTitle = findViewById(R.id.common_txt_title);
        mTxtTitle.setText("找人导航");
        mScreenBtn = findViewById(R.id.screen_btn);
        mRgList = findViewById(R.id.category_list_rg);
        mFragmentBuilder = new FragmentBuilder(this, R.id.category_list_tab_content);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initFragments() {
        DBModel dbModel = DBModel.get("queryCategory");
        if (null != count && count.equals("count")) {
            List<CategoryEntity> mList = new ArrayList<>();

            mFragmentBuilder = new FragmentBuilder(this, R.id.category_list_tab_content);
            mList.add(new CategoryEntity("0", "全部", false));
            mFragmentBuilder.registerFragement("全部", WebCastItemFragment.newInstance("0"));

            JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
            List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");
            for (int i = 0; i < lists.size(); i++) {
                JSONObject object = lists.get(i);
                List<JSONObject> list = JSONUtil.getList(object, "lists");

                String id = JSONUtil.get(object, "id", "");
                if (id.equals(mCategoryId)) {
                    for (JSONObject object1 : list) {
                        String childId = JSONUtil.get(object1, "id", "");
                        String name = JSONUtil.get(object1, "name", "");
                        mList.add(new CategoryEntity(childId, name, false));
                        mFragmentBuilder.registerFragement(name, WebCastItemFragment.newInstance(childId, count));
                    }
                }
            }

            int index = 0;

            for (int i = 0; i < mList.size(); i++) {
                CategoryEntity en = mList.get(i);

                if (en.getId().equals(mChildCategoryId)) index = i;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LinearLayout.LayoutParams.FILL_PARENT);
                RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.radio_button_item, null);
                radioButton.setId(i);
                radioButton.setText(en.getName());
                if (i == index) {
                    radioButton.setChecked(true);
                } else {
                    radioButton.setChecked(false);
                }
                mRgList.addView(radioButton, i, params);
            }

            mFragmentBuilder.switchFragment(index);

        } else if (dbModel != null && !TextUtils.isEmpty(dbModel.Description)) {
            List<CategoryEntity> mList = new ArrayList<>();

            mFragmentBuilder = new FragmentBuilder(this, R.id.category_list_tab_content);

            mList.add(new CategoryEntity("0", "全部", false));

            mFragmentBuilder.registerFragement("全部", WebCastItemFragment.newInstance("0"));
            JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
            List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");
            for (int i = 0; i < lists.size(); i++) {
                JSONObject object = lists.get(i);
                List<JSONObject> list = JSONUtil.getList(object, "lists");
                String id = JSONUtil.get(object, "id", "");
                if (id.equals(mCategoryId)) {
                    for (JSONObject object1 : list) {
                        String childId = JSONUtil.get(object1, "id", "");
                        String name = JSONUtil.get(object1, "name", "");
                        mList.add(new CategoryEntity(childId, name, false));
                        mFragmentBuilder.registerFragement(name, WebCastItemFragment.newInstance(childId));
                    }
                }
            }

            int index = 0;

            for (int i = 0; i < mList.size(); i++) {
                CategoryEntity en = mList.get(i);

                if (en.getId().equals(mChildCategoryId)) index = i;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LinearLayout.LayoutParams.FILL_PARENT);
                RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.radio_button_item, null);
                radioButton.setId(i);

                radioButton.setText(en.getName());
                if (i == index) {
                    radioButton.setChecked(true);
                } else {
                    radioButton.setChecked(false);
                }
                mRgList.addView(radioButton, i, params);
            }

            mFragmentBuilder.switchFragment(index);

        } else {
            WebCastDbHelper.lists(mIOAuthCallBack);
        }

    }

    @Override
    protected void setListener() {
        mScreenBtn.setOnClickListener(this);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryListActivity.this.finish();
            }
        });

        mRgList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                mFragmentBuilder.switchFragment(checkedId);

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {
            UIHelper.showOnLoadingDialog(CategoryListActivity.this);
        }

        @Override
        public void getSuccess(String success) {
            BaseModel mResultDetailModel = new ResultRequestModel(success);
            if (mResultDetailModel.mIsSuccess) {
                DBModel.saveOrUpdate("queryCategory", success);

            }
            UIHelper.showToast(CategoryListActivity.this, mResultDetailModel.mMessage);
            UIHelper.hideOnLoadingDialog();
        }

        @Override
        public void getFailue(String failueJson) {

            UIHelper.showToast(CategoryListActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen_btn:
                FragmentManager fm = getSupportFragmentManager();
                CategoryDialogFragment dialog = CategoryDialogFragment.newInstance();
                dialog.show(fm, "category_dialog");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(CategoryListActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCategoryEvent(CategoryEvent event) {
        mCategoryId = event.categoryId;
        mChildCategoryId = event.childCategoryId;
        loadFragments();
    }

    private void loadFragments() {
        DBModel dbModel = DBModel.get("queryCategory");
        List<CategoryEntity> mList = new ArrayList<>();
        mFragmentBuilder = new FragmentBuilder(this, R.id.category_list_tab_content);
        mList.add(new CategoryEntity("0", "全部", false));
        mFragmentBuilder.registerFragement("全部", WebCastItemFragment.newInstance("category"));

        JSONObject jsonObject = JSONUtil.getStringToJson(dbModel.Description);
        List<JSONObject> lists = JSONUtil.getList(jsonObject, "data");
        for (int i = 0; i < lists.size(); i++) {
            JSONObject object = lists.get(i);
            List<JSONObject> list = JSONUtil.getList(object, "lists");

            String id = JSONUtil.get(object, "id", "");
            if (id.equals(mCategoryId)) {
                for (JSONObject object1 : list) {
                    String childId = JSONUtil.get(object1, "id", "");
                    String name = JSONUtil.get(object1, "name", "");
                    mList.add(new CategoryEntity(childId, name, false));
                    mFragmentBuilder.registerFragement(name, WebCastItemFragment.newInstance(childId, count));
                }
            }
        }

        int index = 0;

        for (int i = 0; i < mList.size(); i++) {
            CategoryEntity en = mList.get(i);

            if (en.getId().equals(mChildCategoryId)) index = i;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LinearLayout.LayoutParams.FILL_PARENT);
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.radio_button_item, null);
            radioButton.setId(i);
            radioButton.setText(en.getName());
            if (i == index) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            mRgList.addView(radioButton, i, params);
        }

        mFragmentBuilder.loadFragment(index);
    }
}
