package com.android.nana.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseScrollViewActivity;
import com.android.common.models.DBModel;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.WebCastItemAdapter;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.webcast.CategoryListActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/3/23 0023.
 */

public class AllCategoryListActivity extends BaseScrollViewActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private View mVWebCast;
    private LinearLayout mLlList;
    private int num = 1;

    @Override
    protected void bindViews() {

        setContentView(R.layout.all_category_list);

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("找人导航");

        mVWebCast = LayoutInflater.from(AllCategoryListActivity.this).inflate(R.layout.common_linearlayout, null);
        mLlList = (LinearLayout) mVWebCast.findViewById(R.id.common_ll_layout);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllCategoryListActivity.this.finish();

            }
        });

    }

    @Override
    protected void initData() {

        WebCastDbHelper.lists(mIOAuthCallBack);
    }

    @Override
    public void populateData(String success) {

        DBModel.saveOrUpdate("queryCategory", success);

        JSONObject jsonObject = JSONUtil.getStringToJson(success);
        List<JSONObject> mList = JSONUtil.getList(jsonObject, "data");

        if (num == 1) {
            num++;
            for (int i = 0; i < mList.size(); i++) {
                JSONObject object = mList.get(i);
                View mView = LayoutInflater.from(AllCategoryListActivity.this).inflate(R.layout.webcast_item, null);
                ImageView mIvPicture = (ImageView) mView.findViewById(R.id.webcast_item_iv_picture);
                TextView mTxtAllCategory = (TextView) mView.findViewById(R.id.webcast_item_txt_all_category);
                TextView mTxtName = (TextView) mView.findViewById(R.id.webcast_item_txt_name);
                GridView mGvList = (GridView) mView.findViewById(R.id.webcast_item_gv_list);

                WebCastItemAdapter adapter = new WebCastItemAdapter(AllCategoryListActivity.this);
                mGvList.setAdapter(adapter);

                final String Id = JSONUtil.get(object, "id", "");
                final String name = JSONUtil.get(object, "name", "");
                mTxtName.setText(name);
                ImgLoaderManager.getInstance().showImageView(JSONUtil.get(object, "picture", ""), mIvPicture);

                List<JSONObject> list = JSONUtil.getList(object, "lists");
                adapter.setList(list);
                adapter.notifyDataSetChanged();

                // 全部
                mTxtAllCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(AllCategoryListActivity.this, CategoryListActivity.class);
                        intent.putExtra("Name", name);
                        intent.putExtra("CategoryId", Id);
                        intent.putExtra("ChildCategoryId", "0");
                        startActivity(intent);

                    }
                });

                // 子分类
                mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        JSONObject object = (JSONObject) parent.getItemAtPosition(position);
                        Intent intent = new Intent(AllCategoryListActivity.this, CategoryListActivity.class);
                        intent.putExtra("Name", name);
                        intent.putExtra("CategoryId", Id);
                        intent.putExtra("ChildCategoryId", JSONUtil.get(object, "id", "0"));
                        startActivity(intent);

                    }
                });

                mLlList.addView(mView);
            }
        }
    }

    @Override
    protected String dbModelName() {

        return "WebCast";
    }

    @Override
    protected View addViewToLayout() {

        return mVWebCast;
    }

}
