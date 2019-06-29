package com.android.nana.webcast;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.OthreEntity;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.downmenu.DropMenuAdapter;
import com.android.nana.downmenu.FilterUrl;
import com.android.nana.downmenu.OthreAdapter;
import com.android.nana.eventBus.SearchDeleteEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.main.OtherActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.search.RecordSQLiteOpenHelper;
import com.android.nana.search.SearchListview;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.OverrideEditText;
import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.google.gson.Gson;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.nana.R.id.et_search;

/**
 * Created by THINK on 2017/7/27.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, OnFilterDoneListener, DropDownMenu.DropDownMenuSearchListener, OthreAdapter.OthreListener {

    private OverrideEditText mSearchEt;
    private ImageView mCancelIv;
    private String mUserId;
    private String mSearchContent;
    private MultipleStatusView mMultipleStatusView;

    //搜索历史
    private SearchListview listView;
    private LinearLayout mSearchLl;
    private TextView mClearTv;

    private BaseAdapter adapter;
    public static SQLiteDatabase db;
    public static RecordSQLiteOpenHelper helper;
    private AlertView mAlertView;
    private DropDownMenu mDropDownMenu;
    private String queryName = "";
    private DropMenuAdapter menuAdapter;

    private String mCityId = "";
    private String mProvinceId = "";//省份ID
    private String mIndustryId = "";//行业ID
    private String mParPropertyId = "";//二级行业ID
    private int page = 1;
    private OthreAdapter mAdapter;
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private ArrayList<OthreEntity.User> mDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SearchActivity.this)) {
            EventBus.getDefault().register(SearchActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void findViewById() {
        mSearchEt = findViewById(et_search);
        mCancelIv = findViewById(R.id.iv_close);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        listView = findViewById(R.id.listView);
        mSearchLl = findViewById(R.id.ll_listview);
        mClearTv = findViewById(R.id.tv_clear);
        mDropDownMenu = findViewById(R.id.dropDownMenu);

        mRecyclerView = findViewById(R.id.view_recycler);
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);

        initFilterDropDownView();
    }


    private void initFilterDropDownView() {
        String[] titleList = new String[]{"全部城市", "全部行业", "高级搜索"};
        menuAdapter = new DropMenuAdapter(this, titleList, this);
        mDropDownMenu.setMenuAdapter(menuAdapter, SearchActivity.this);
    }

    @Override
    protected void init() {
        if (NetWorkUtils.isNetworkConnected(SearchActivity.this)) {
            mUserId = BaseApplication.getInstance().getCustomerId(this);
        } else {
            mMultipleStatusView.noNetwork();
        }

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new OthreAdapter(SearchActivity.this, mDataList, queryName, this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_A1);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataList.clear();
        initData();
    }

    private void initData() {//显示搜索历史数据
        helper = new RecordSQLiteOpenHelper(this);
        queryData("");

        if (listView.getCount() > 0) {
            mSearchLl.setVisibility(View.VISIBLE);
            mClearTv.setVisibility(View.VISIBLE);
        } else {
            mSearchLl.setVisibility(View.GONE);
            mClearTv.setVisibility(View.GONE);
        }

        mClearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(mSearchEt);
                deleteDialogs();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //获取到用户点击列表里的文字,并自动填充到搜索框内
                TextView textView = view.findViewById(android.R.id.text1);
                mSearchContent = textView.getText().toString();
                mSearchLl.setVisibility(View.GONE);

                closeKeyboard(mSearchEt);
                mMultipleStatusView.loading();
                mAdapter.setKeyword(mSearchContent);
                isLoading = true;
                loadData(mSearchContent, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
            }
        });
    }

    private void loadData(String keyword, String cityId, String mProvinceId, final String mIndustryId, String mParPropertyId, int page) {//搜索内容
        FunctionDbHelper.searchUserbyKeyword(keyword, mUserId, cityId, mProvinceId, mIndustryId, mParPropertyId, page, new IOAuthCallBack() {
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
                                mMultipleStatusView.searchEmpty();
                                ToastUtils.showToast("暂无更多数据");
                                swipeRefreshLayout.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
                                mAdapter.isLoading(true);
                                isLoading = true;
                                return;
                            } else {
                                mMultipleStatusView.searchEmpty();
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


    //查询是否同一数据
    private String queryName(String tempName) {
        //模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc limit 5 ", null);
        while (cursor.moveToNext()) {
            queryName = cursor.getString(cursor.getColumnIndex("name"));
        }
        return queryName;
    }

    /*模糊查询数据 并显示在ListView列表上*/
    private void queryData(String tempName) {

        //模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc limit 5 ", null);

        // 创建adapter适配器对象,装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(this, R.layout.simple_list_item_text, cursor, new String[]{"name"},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void deleteData() {//清空历史记录
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    /*插入数据*/
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }


    private void deleteDialogs() {//设置见面金额
        mAlertView = new AlertView("提示", "确定清除搜索历史？", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, this).setCancelable(true);
        mAlertView.show();
    }

    @Override
    protected void setListener() {
        mCancelIv.setOnClickListener(this);

        SearchActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

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
                    closeKeyboard(mSearchEt);
                    mSearchContent = mSearchEt.getText().toString().trim();
                    if (!"".equals(mSearchContent) && !mSearchContent.equals(queryName(mSearchContent))) {
                        insertData(mSearchContent);
                    }
                    mSearchLl.setVisibility(View.GONE);
                    mMultipleStatusView.loading();
                    isLoading = true;
                    mAdapter.setKeyword(mSearchContent);
                    mDataList.clear();
                    loadData(mSearchContent, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                    return true;
                }
                return false;
            }
        });

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
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadData(queryName, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        page = 1;
                        isLoading = false;
                        if (mCityId.equals("") || mIndustryId.equals("")) {
                            mDataList.clear();
                            loadData(queryName, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                        } else if (!"".equals(queryName)) {
                            mDataList.clear();
                            loadData(queryName, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                        } else {
                            loadData(queryName, mCityId, mProvinceId, mIndustryId, mParPropertyId, page);
                        }
                    }
                }, 2000);
            }
        });

    }


    public ArrayList<OthreEntity.User> parseData(String result) {//Gson 解析
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                SearchActivity.this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(Object o, int position) {//清空搜索记录
        if (position == 0) {
            //清空数据库
            deleteData();
            queryData("");
            mSearchLl.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SearchActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDelete(SearchDeleteEvent searchEvent) {//清空搜索历史
        deleteData();
    }

    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {

        if (null != FilterUrl.instance().mProvinceId) {
            mCityId = FilterUrl.instance().mCityId;//城市ID
            mProvinceId = FilterUrl.instance().mProvinceId;//省份
        } else if (null != FilterUrl.instance().mProfessionId) {
            mIndustryId = FilterUrl.instance().mProfessionId;//行业ID
            mParPropertyId = FilterUrl.instance().mNextId;//行业二级ID
        }
        if (position != 3) {
            mDropDownMenu.setPositionIndicatorText(FilterUrl.instance().position, FilterUrl.instance().positionTitle);
        }
        mDropDownMenu.close();
        delayKeyboard(mSearchEt);
    }


    @Override
    public void onClickCity() {//点击城市
        closeKeyboard(mSearchEt);
    }

    @Override
    public void onSeniorClick() {
        mDropDownMenu.closeMenu();

        Intent intent = new Intent(SearchActivity.this, OtherActivity.class);
        intent.putExtra("search", "search");
        startActivity(intent);
    }

    @Override
    public void onItemClick(OthreEntity.User entity) {
        Intent intent = new Intent(SearchActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", entity.getId());
        startActivity(intent);
    }

    /**
     * 弹出软键盘
     */
    public void openKeyboard(View view) {
        // 获取焦点
        mSearchEt.setFocusable(true);
        mSearchEt.setFocusableInTouchMode(true);
        mSearchEt.requestFocus();
        // 弹出软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearchEt, 0);
    }

    //延迟弹出软件盘
    public void delayKeyboard(View view) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) mSearchEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mSearchEt, 0);
            }
        }, 200);
    }

    /**
     * 关闭软键盘
     */
    public void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

}
