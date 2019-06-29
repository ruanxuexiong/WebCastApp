package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.ListViewDecoration;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/3.
 */

public class NavigationActivity extends BaseActivity implements View.OnClickListener, NavigationAdapter.NavigationListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private NavigationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout mBottomll;
    private ImageView mSearchIv;
    private ArrayList<NavigationBean.OneNav> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_navigation);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mRecyclerView = findViewById(R.id.recycler_view);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mBottomll = findViewById(R.id.ll_bottom);
        mSearchIv = findViewById(R.id.iv_search);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("找人才");
        showProgressDialog("", "加载中...");
        loadData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new NavigationAdapter(NavigationActivity.this, mDataList, NavigationActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        FunctionDbHelper.navgation(new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        ImageLoader.getInstance().displayImage(data.getString("bg"), mSearchIv);
                        if (parseData(successJson).size() > 0) {
                            for (NavigationBean.OneNav item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
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

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mBottomll.setOnClickListener(this);
        mSearchIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_bottom:
                Intent intent = new Intent(NavigationActivity.this, OtherActivity.class);
                intent.putExtra("keyword", "");
                startActivity(intent);
                break;
            case R.id.iv_search:
               /* Intent search = new Intent(NavigationActivity.this, OtherActivity.class);
                search.putExtra("search","search");//标识弹出软键盘
                startActivity(search);*/
                Intent intentSearch = new Intent(NavigationActivity.this, SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
            default:
                break;
        }
    }

    private ArrayList<NavigationBean.OneNav> parseData(String result) {
        ArrayList<NavigationBean.OneNav> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray nav = new JSONArray(data.getString("nav"));
            if (nav.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < nav.length(); i++) {
                    NavigationBean.OneNav bean = gson.fromJson(nav.optJSONObject(i).toString(), NavigationBean.OneNav.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void OnItemClick(String mContent) {
        Intent intent = new Intent(NavigationActivity.this, OtherActivity.class);
        intent.putExtra("keyword", mContent);
        startActivity(intent);
    }
}
