package com.android.nana.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.ui.pull.PullToRefreshLayout;
import com.android.common.ui.pull.pullableview.PullableListView;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.CoreAdapter;
import com.android.nana.bean.CoreEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/19.
 */

public class HotFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener, CoreAdapter.CoreListener {

    private ImageButton mBackBtn;
    private TextView mTitleTv;
    private IconCenterEditText icet_search;
    private String keyword = "";

    private boolean mPullToRefreshType; // 判断是下拉，还是上拉加载数据
    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private PullToRefreshLayout mPullToRefreshFinish;
    private PullToRefreshLayout mPullToLoadmore;
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private int page = 1;

    private CoreAdapter mAdapter;
    private UserInfo mUserInfo;
    private String mid;
    private ArrayList<CoreEntity> mDataList = new ArrayList<>();

    public static HotFragment newInstance() {
        HotFragment fragment = new HotFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(HotFragment.this)) {
            EventBus.getDefault().register(HotFragment.this);
        }
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
    }

    @Override
    protected void initData() {
        mTitleTv.setText("活动群组");
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(getContext(), "userInfo", UserInfo.class);
        showProgressDialog("", "加载中...");
        loadData(keyword, page);
    }

    private void loadData(String keyword, int page) {//加载数据


        HomeDbHelper.popular(keyword, page, mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                if (!mPullToRefreshType && null != mPullToRefreshFinish) {
                    mPullToRefreshFinish.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mDataList.clear();
                } else if (null != mPullToLoadmore) {
                    mPullToLoadmore.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (CoreEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            dismissProgressDialog();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast("暂无数据");
                        }
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试!");
            }
        });
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.hot_fragment;
    }

    @Override
    public void initView() {
        mBackBtn = mContentView.findViewById(R.id.common_btn_back);
        mTitleTv = mContentView.findViewById(R.id.common_txt_title);
        icet_search = mContentView.findViewById(R.id.icet_search);

        mListView = mContentView.findViewById(R.id.content_view);
        mLayout = mContentView.findViewById(R.id.refresh_view);

        mAdapter = new CoreAdapter(getActivity(), mDataList, this);
        mListView.setAdapter(mAdapter);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void bindEvent() {
        mBackBtn.setOnClickListener(this);
        mLayout.setOnRefreshListener(this);

        icet_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icet_search.setCursorVisible(true);
            }
        });

        icet_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    keyword = icet_search.getText().toString().trim();

                    mLayout.autoRefresh();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                getActivity().finish();
                break;
        }
    }


    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = false;
        mPullToRefreshFinish = pullToRefreshLayout;
        page = 1;

        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(keyword, page);
            }
        }, 500);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshType = true;
        mPullToLoadmore = pullToRefreshLayout;
        page++;
        new Handler().postDelayed(new Runnable() { // 延迟0.5秒，让体验更好
            @Override
            public void run() {
                loadData(keyword, page);
            }
        }, 500);
    }

    public ArrayList<CoreEntity> parseData(String result) {//Gson 解析
        ArrayList<CoreEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                CoreEntity entity = gson.fromJson(data.optJSONObject(i).toString(), CoreEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onItemClick(View view) {
        CoreEntity entity = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", entity.getId());
        intent.putExtra("mid", mUserInfo.getId());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(MessageEvent messageEvent) {
        if (messageEvent.message.equals("updateData")) {
            mLayout.autoRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(HotFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        icet_search.setCursorVisible(false);
    }
}
