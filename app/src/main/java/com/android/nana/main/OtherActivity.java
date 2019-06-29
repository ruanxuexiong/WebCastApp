package com.android.nana.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.OthreEntity;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.downmenu.DropMenuAdapter;
import com.android.nana.downmenu.FilterUrl;
import com.android.nana.downmenu.OthreAdapter;
import com.android.nana.downmenu.SeniorSearchActivity;
import com.android.nana.eventBus.SearchEvent;
import com.android.nana.eventBus.SeniorSearchEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.OverrideEditText;
import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.nana.R.id.dropDownMenu;

/**
 * Created by lenovo on 2018/1/3.
 */

public class OtherActivity extends BaseActivity implements View.OnClickListener, OnFilterDoneListener, DropDownMenu.DropDownMenuSearchListener, OthreAdapter.OthreListener {

    private ImageView mCloseIv;
    private OverrideEditText mSearchEt;
    private DropDownMenu mDropDownMenu;
    private RecyclerView mRecyclerView;
    private String keyword = "";
    private String mid = "";
    private String mCityId = "";
    private String mProvinceId = "";//省份ID
    private String mIndustryId = "";//行业ID
    private String mParPropertyId = "";//二级行业ID
    private DropMenuAdapter menuAdapter;
    private int page = 1;
    private MultipleStatusView mMultipleStatusView;
    private OthreAdapter mAdapter;
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private ArrayList<OthreEntity.User> mDataList = new ArrayList<>();

    //高级搜索
    private String mGender = "-1";
    private String mPostName;
    private String mSchoolName;
    private SearchEvent event;

    private boolean isSenior = false;//是否是高级搜索

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(OtherActivity.this)) {
            EventBus.getDefault().register(OtherActivity.this);
        }

        if (null != getIntent().getStringExtra("search")) {
            startActivity(new Intent(OtherActivity.this, SeniorSearchActivity.class));
        }
    }

    @Override
    protected void bindViews() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_other);
    }

    @Override
    protected void findViewById() {
        mCloseIv = findViewById(R.id.iv_close);
        mSearchEt = findViewById(R.id.et_search);
        mDropDownMenu = findViewById(dropDownMenu);
        mRecyclerView = findViewById(R.id.view_recycler);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
    }

    @Override
    protected void init() {
        mid = (String) SharedPreferencesUtils.getParameter(OtherActivity.this, "userId", "");
        initFilterDropDownView();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new OthreAdapter(OtherActivity.this, mDataList, keyword, this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_A1);

        if (null != getIntent().getStringExtra("search")) {
            mSearchEt.setCursorVisible(true);
            mSearchEt.requestFocus();
            mMultipleStatusView.empty();
            OtherActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        if (null != getIntent().getStringExtra("keyword")) {
            keyword = getIntent().getStringExtra("keyword");
            mSearchEt.setText(keyword);
            mInputMethodManager.hideSoftInputFromWindow(mSearchEt.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mMultipleStatusView.loading();
            mAdapter.setKeyword(keyword);
            loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        page = 1;
                        isLoading = false;
                        mDataList.clear();
                        if (isSenior) {
                            searchLoadData(mGender, mPostName, mCityId, mProvinceId, mSchoolName, mIndustryId, mParPropertyId, page);
                        } else {
                            loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                        }

                    }
                }, 2000);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        //上拉加载
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }

                    if (isSenior) {//高级搜索
                        if (!isLoading) {
                            isLoading = true;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    page++;
                                    searchLoadData(mGender, mPostName, mCityId, mProvinceId, mSchoolName, mIndustryId, mParPropertyId, page);
                                    isLoading = false;
                                }
                            }, 1000);
                        }
                    } else {
                        if (!isLoading) {
                            isLoading = true;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    page++;
                                    loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                                    isLoading = false;
                                }
                            }, 1000);
                        }
                    }
                }
            }
        });
    }

    private void loadData(String keyword, String cityId, String mProvinceId, final String mIndustryId, String mParPropertyId, int page) {//搜索内容
        FunctionDbHelper.searchUserbyKeyword(keyword, mid, cityId, mProvinceId, mIndustryId, mParPropertyId, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            mMultipleStatusView.dismiss();
                            for (OthreEntity.User item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.isLoading(false);
                            swipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                            if (mDataList.size() < 9) {
                                mAdapter.isLoading(true);
                            }
                        } else {
                            if (!isLoading) {
                                ToastUtils.showToast("暂无更多数据");
                                swipeRefreshLayout.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
                                mAdapter.isLoading(true);
                                isLoading = true;
                                return;
                            } else {
                                mAdapter.isLoading(true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                mMultipleStatusView.dismiss();
            }
        });
    }


    //高级搜索
    private void searchLoadData(String mGender, String mPostName, String cityId, String provinceId, String school, String propertyId, String parPropertyId, int page) {//搜索内容
        FunctionDbHelper.advanceSearch(mid, mGender, mPostName, cityId, provinceId, school, propertyId, parPropertyId, page, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            mMultipleStatusView.dismiss();
                            for (OthreEntity.User item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.isLoading(false);
                            swipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                            if (mDataList.size() < 9) {
                                mAdapter.isLoading(true);
                            }
                        } else {
                            if (!isLoading) {
                                ToastUtils.showToast("暂无更多数据");
                                swipeRefreshLayout.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
                                mAdapter.isLoading(true);
                                isLoading = true;
                                return;
                            } else {
                                mAdapter.isLoading(true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    private void initFilterDropDownView() {
        String[] titleList = new String[]{"全部城市", "全部行业", "高级搜索"};
        menuAdapter = new DropMenuAdapter(this, titleList, this);
        mDropDownMenu.setMenuAdapter(menuAdapter, OtherActivity.this);
    }

    @Override
    protected void setListener() {
        mCloseIv.setOnClickListener(this);
        mSearchEt.setOnClickListener(this);
        mSearchEt.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                mSearchEt.setText("");
            }
        });

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    keyword = mSearchEt.getText().toString().trim();
                    if (!"".equals(keyword)) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(true);
                            }
                        });
                        page = 1;
                        isLoading = false;
                        mDataList.clear();
                        mAdapter.setKeyword(keyword);
                        loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                this.finish();
                break;
            case R.id.et_search:

                break;
            default:
                break;
        }
    }

    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {

        if (null != FilterUrl.instance().mProvinceId) {
            mCityId = FilterUrl.instance().mCityId;//城市ID
            mProvinceId = FilterUrl.instance().mProvinceId;//省份
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            page = 1;
            isLoading = false;
            mDataList.clear();
            mAdapter.setKeyword(keyword);
            loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
        } else if (null != FilterUrl.instance().mProfessionId) {
            mIndustryId = FilterUrl.instance().mProfessionId;//行业ID
            mParPropertyId = FilterUrl.instance().mNextId;//行业二级ID
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            page = 1;
            isLoading = false;
            mDataList.clear();
            mAdapter.setKeyword(keyword);
            loadData(keyword, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
        }

        if (position != 3) {
            mDropDownMenu.setPositionIndicatorText(FilterUrl.instance().position, FilterUrl.instance().positionTitle);
        }
        mDropDownMenu.close();
        closeKeyboard(mSearchEt);
    }


    /**
     * 关闭软键盘
     */
    public void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClickCity() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(OtherActivity.this);
        FilterUrl.instance().clear();
    }

    @Override
    public void onSeniorClick() {//点击高级搜索
        mDropDownMenu.closeMenu();
        startActivity(new Intent(OtherActivity.this, SeniorSearchActivity.class));
    }

    private ArrayList<OthreEntity.User> parseData(String result) {
        ArrayList<OthreEntity.User> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray user = new JSONArray(data.getString("user"));
            if (user.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < user.length(); i++) {
                    OthreEntity.User bean = gson.fromJson(user.optJSONObject(i).toString(), OthreEntity.User.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }


    @Override
    public void onItemClick(OthreEntity.User entity) {
        Intent intent = new Intent(OtherActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", entity.getId());
        startActivity(intent);
    }

    //条件搜索回调
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSeniorSearchEvent(SeniorSearchEvent event) {

        mGender = event.mGender;//性别
        mPostName = event.mPostName;//职位名称
        mSchoolName = event.mSchoolName;//学校名称

        page = 1;
        mDataList.clear();
        isSenior = true;

        if (!"".equals(event.mCityId)) {//城市ID不为空
            mDropDownMenu.setPositionIndicatorText(0, event.mRegionName);
        } else {
            mDropDownMenu.setPositionIndicatorText(0, "全部城市");
        }

        if (!"".equals(event.mFunctionOneId)) {
            mDropDownMenu.setPositionIndicatorText(1, event.mFunctionName);
        } else {
            mDropDownMenu.setPositionIndicatorText(1, "全部行业");
        }

        if (!"".equals(mPostName) && !"".equals(mSchoolName)) {
            mSearchEt.setText(mPostName + "+" + mSchoolName);
            keyword = mSearchEt.getText().toString().trim();
            mAdapter.setKeyword(keyword);
        } else if (!"".equals(mPostName)) {
            keyword = mPostName;
            mSearchEt.setText(mPostName);
            mAdapter.setKeyword(keyword);
        } else if (!"".equals(mSchoolName)) {
            keyword = mSchoolName;
            mAdapter.setKeyword(keyword);
            mSearchEt.setText(mSchoolName);
        }

        searchLoadData(mGender, mPostName, event.mCityId, event.mProvinceId, event.mSchoolName, event.mFunctionOneId, event.mFunctionTwoId, page);
    }

    //高级搜索回调
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSearchEvent(SearchEvent event) {

        page = 1;
        isLoading = false;
        mDataList.clear();
        keyword = event.search;
        mAdapter.setKeyword(event.search);
        mSearchEt.setText(event.search);
        isSenior = true;
        mCityId = "";
        mProvinceId = "";
        mIndustryId = "";
        mParPropertyId = "";
        for (int i = 0; i < 1; i++) {
            if (i == 0) {
                mDropDownMenu.setPositionIndicatorText(0, "全部城市");
            } else {
                mDropDownMenu.setPositionIndicatorText(1, "全部行业");
            }
        }

        loadData(event.search, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
        mDropDownMenu.closeMenu();
    }

}
