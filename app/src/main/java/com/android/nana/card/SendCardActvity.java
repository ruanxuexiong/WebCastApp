package com.android.nana.card;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.adapter.SendCardAdapter;
import com.android.nana.card.bean.CardList;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.ListViewDecoration;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/25.
 */

public class SendCardActvity extends BaseActivity implements View.OnClickListener, SendCardAdapter.SendCardListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private RecyclerView mRecyclerView;
    private SendCardAdapter mAdapter;
    private StateButton mSendBtn;
    private String mid;
    private String mShareUrl, mShareTitle, mShareContent, mSharLogo, mSharePic,mThumb,mOldCard;
    private ArrayList<CardList.Cards> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_send_card);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mSendBtn = findViewById(R.id.btn_send);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发名片");
        mBackTv.setVisibility(View.VISIBLE);
        mid = (String) SharedPreferencesUtils.getParameter(SendCardActvity.this, "userId", "");
        loadData(mid);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new SendCardAdapter(SendCardActvity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void setListener() {
        mSendBtn.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.btn_send:
                share();
                break;
            default:
                break;
        }
    }

    private void loadData(String mid) {//是否有名片
        mDataList.clear();
        CardDbHelper.cardList(mid, "1", new IOAuthCallBack() {
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
                            for (CardList.Cards item : parseData(successJson)) {
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
    public void onCheckBoxItemClick(CardList.Cards item) {//选择需要发送的名片
        mShareUrl = item.getCardURL();
        mShareTitle = item.getShareTitle();
        mShareContent = item.getShareContent();
        mSharLogo = item.getSharLogo();
        mThumb = item.getThumb();
        mOldCard = item.getOldCard();
    }


    private void share() {//分享

        if (TextUtils.isEmpty(mShareUrl)) {
            ToastUtils.showToast("请选用要分享的名片");
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(mShareUrl, mShareTitle, mShareContent, mSharLogo,mThumb,mOldCard);
        dialog.show(fm, "fragment_bottom_dialog");
    }
}
