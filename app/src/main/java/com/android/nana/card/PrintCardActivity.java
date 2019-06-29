package com.android.nana.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.adapter.SendCardAdapter;
import com.android.nana.card.bean.CardList;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.PickupEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/2/1.
 */

public class PrintCardActivity extends BaseActivity implements View.OnClickListener, SendCardAdapter.SendCardListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private RecyclerView mRecyclerView;
    private SendCardAdapter mAdapter;
    private StateButton mPrintBtn;
    private String mid;
    private String mCardId;
    private ArrayList<CardList.Cards> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(PrintCardActivity.this)) {
            EventBus.getDefault().register(PrintCardActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_print_card);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mPrintBtn = findViewById(R.id.btn_print);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("免费打印名片");
        mBackTv.setVisibility(View.VISIBLE);
        mid = (String) SharedPreferencesUtils.getParameter(PrintCardActivity.this, "userId", "");
        showProgressDialog("", "加载中...");
        loadData(mid);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new SendCardAdapter(PrintCardActivity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData(String mid) {
        mDataList.clear();
        CardDbHelper.cardList(mid, "1", new IOAuthCallBack() {
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
                        if (parseData(successJson).size() > 0) {
                            for (CardList.Cards item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
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
            }
        });
    }


    public ArrayList<CardList.Cards> parseData(String result) {//Gson 解析
        ArrayList<CardList.Cards> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonobject.getString("data"));
            JSONArray data = new JSONArray(jsonObject1.getString("cards"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                CardList.Cards entity = gson.fromJson(data.optJSONObject(i).toString(), CardList.Cards.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    protected void setListener() {
        mPrintBtn.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_print:
                if (null != mCardId) {
                    Intent intent = new Intent(PrintCardActivity.this, PickupActivity.class);
                    intent.putExtra("cardId", mCardId);
                    intent.putExtra("mid", mid);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("至少选择一张名片！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckBoxItemClick(CardList.Cards item) {
        mCardId = item.getId();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(PrintCardActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinish(PickupEvent event) {//更新名片列表
        PrintCardActivity.this.finish();
    }
}
