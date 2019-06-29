package com.android.nana.card;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.adapter.SearchCardAdapter;
import com.android.nana.card.bean.SearchCardBean;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.TheCardEvent;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.OverrideEditText;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/2/2.
 */

public class SearchCardActivity extends BaseActivity implements View.OnClickListener,SearchCardAdapter.SearchCardListener {

    private ImageView mCloseIv;
    private OverrideEditText mSearchEt;
    private String keyword;
    private String mid;
    private SearchCardAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TransitionSingleHelper mTransiTion;
    private ArrayList<SearchCardBean.Cards> mDataList = new ArrayList<>();
    private InputMethodManager mInputMethodManager; // 隐藏软键盘

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(SearchCardActivity.this)) {
            EventBus.getDefault().register(SearchCardActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_search_card);
    }

    @Override
    protected void findViewById() {
        mCloseIv = findViewById(R.id.iv_close);
        mSearchEt = findViewById(R.id.et_search);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void init() {
        mid = getIntent().getStringExtra("mid");
        mTransiTion = new TransitionManager(SearchCardActivity.this).getSingle();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new SearchCardAdapter(SearchCardActivity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
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
                        mDataList.clear();
                        loadData(keyword);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void loadData(String keyword) {//搜索
        CardDbHelper.searchCard(mid, keyword, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (SearchCardBean.Cards item : parseData(successJson)) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                SearchCardActivity.this.finish();
                break;
            default:
                break;
        }
    }


    public ArrayList<SearchCardBean.Cards> parseData(String result) {//Gson 解析
        ArrayList<SearchCardBean.Cards> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("cards"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                SearchCardBean.Cards entity = gson.fromJson(data.optJSONObject(i).toString(), SearchCardBean.Cards.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onEditItemClick(SearchCardBean.Cards item) {
        Intent intent = new Intent(SearchCardActivity.this, TheCardActivity.class);
        intent.putExtra("cardId", item.getId());
        intent.putExtra("uid", item.getUid());
        intent.putExtra("name", item.getName());
        intent.putExtra("position", item.getPosition());
        intent.putExtra("company", item.getCompany());
        intent.putExtra("card", item.getCard());
        intent.putExtra("type", "2");
        startActivity(intent);
    }

    @Override
    public void onCardItemClick(View view, SearchCardBean.Cards item) {
        mTransiTion.startViewerActivity(view, item.getCard());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SearchCardActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdataData(TheCardEvent event) {//更新名片列表
          SearchCardActivity.this.finish();
    }
}
