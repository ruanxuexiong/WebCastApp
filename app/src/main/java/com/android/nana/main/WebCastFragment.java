package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseScrollViewFragment;
import com.android.common.models.DBModel;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.WebCastItemAdapter;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.HomePageEvent;
import com.android.nana.listener.WebCastListener;
import com.android.nana.material.ScreenActivity;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.webcast.CategoryListActivity;
import com.android.nana.webcast.SearchActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

public class WebCastFragment extends BaseScrollViewFragment {

    private TextView mTxtCountry, mTxtSearch, mTxtFilter;

    private View mVWebCast;
    private LinearLayout mLlList;
    private String state = "-1";

    private View mView;
    private ImageView mIvPicture;
    private TextView mTxtAllCategory;
    private TextView mTxtName;
    private GridView mGvList;
    private WebCastItemAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(WebCastFragment.this)) {
            EventBus.getDefault().register(WebCastFragment.this);
        }
    }


    @Override
    protected int getLayoutId() {

        return R.layout.webcast_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void findViewById() {

      //  mTxtCountry = (TextView) findViewById(R.id.home_layout_txt_country);
        mTxtSearch = (TextView) findViewById(R.id.home_layout_txt_search);
        mTxtFilter = (TextView) findViewById(R.id.home_layout_txt_filter);

        mVWebCast = LayoutInflater.from(getActivity()).inflate(R.layout.common_linearlayout, null);
        mLlList = (LinearLayout) mVWebCast.findViewById(R.id.common_ll_layout);

        mView = LayoutInflater.from(getActivity()).inflate(R.layout.webcast_item, null);
        mIvPicture = (ImageView) mView.findViewById(R.id.webcast_item_iv_picture);
        mTxtAllCategory = (TextView) mView.findViewById(R.id.webcast_item_txt_all_category);
        mTxtName = (TextView) mView.findViewById(R.id.webcast_item_txt_name);
        mGvList = (GridView) mView.findViewById(R.id.webcast_item_gv_list);
        adapter = new WebCastItemAdapter(getActivity());

    }

    @Override
    protected void setListener() {

     /*   mTxtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), SelectedCountryActivity.class));

            }
        });*/

        mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", state);
                startActivity(intentSearch);
            }
        });

        mTxtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentScreen = new Intent(getActivity(), ScreenActivity.class);
                intentScreen.putExtra("state", state);
                startActivity(intentScreen);

            }
        });

    }


    @Override
    protected void initData() {

        WebCastDbHelper.lists(mIOAuthCallBack);

        WebCastListener.getInstance().mOnWebCastListener = new WebCastListener.OnWebCastListener() {
            @Override
            public void result(String text) {

                mTxtCountry.setText(text);
            }
        };


    }

    @Override
    public void populateData(String success) {
        DBModel.saveOrUpdate("queryCategory", success);
        JSONObject jsonObject = JSONUtil.getStringToJson(success);
        List<JSONObject> mList = JSONUtil.getList(jsonObject, "data");

        if (mList.size() > 0) {
            mLlList.removeAllViews();
            for (int i = 0; i < mList.size(); i++) {
                JSONObject object = mList.get(i);
                mGvList.setAdapter(adapter);
                final String Id = JSONUtil.get(object, "id", "");
                final String name = JSONUtil.get(object, "name", "");
                mTxtName.setText(name);//从服务器获取ico
                ImgLoaderManager.getInstance().showImageView(JSONUtil.get(object, "picture", ""), mIvPicture);

                List<JSONObject> list = JSONUtil.getList(object, "lists");
                adapter.setList(list);
                adapter.notifyDataSetChanged();

                // 全部
                mTxtAllCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CategoryListActivity.class);
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
                        Intent intent = new Intent(getActivity(), CategoryListActivity.class);
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onHomePageEvent(HomePageEvent homePageEvent) {

        if (homePageEvent.message.equals("hr")) {
            state = "1";
        } else {
            state = "-1";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(WebCastFragment.this);
    }

}
