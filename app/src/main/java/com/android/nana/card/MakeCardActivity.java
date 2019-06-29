package com.android.nana.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.card.adapter.MakeCardAdapter;
import com.android.nana.card.bean.CardList;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.CardEvent;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/1/16.
 */

public class MakeCardActivity extends BaseActivity implements View.OnClickListener, MakeCardAdapter.MakeCardListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private ImageView mAddCardIv;
    private ImageView mRightIv;
    private ImageView mLeftIv;
    private TextView mTextTv;
    private String hasCard;//是否已有名片
    private boolean isAddCard = true;
    private RecyclerView mRecyclerView;
    private MakeCardAdapter mAdapter;
    private String mid;
    private TransitionSingleHelper mTransiTion;
    private ArrayList<CardList.Cards> mDataList = new ArrayList<>();
    List<LocalMedia> selectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MakeCardActivity.this)) {
            EventBus.getDefault().register(MakeCardActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_make_card);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAddCardIv = findViewById(R.id.iv_add_card);
        mRightIv = findViewById(R.id.iv_right);
        mLeftIv = findViewById(R.id.iv_left);
        mTextTv = findViewById(R.id.tv_text);
        mRecyclerView = findViewById(R.id.recycler);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发名片");
        mTransiTion = new TransitionManager(MakeCardActivity.this).getSingle();
        mid = (String) SharedPreferencesUtils.getParameter(MakeCardActivity.this, "userId", "");
        mBackTv.setVisibility(View.VISIBLE);
        if (null != getIntent().getStringExtra("hasCard")) {
            hasCard = getIntent().getStringExtra("hasCard");
            if (hasCard.equals("-1")) {
                mTextTv.setVisibility(View.VISIBLE);
            } else {
                loadData(mid);
            }
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new MakeCardAdapter(MakeCardActivity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
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
                        } else {
                            mTextTv.setVisibility(View.VISIBLE);
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
        mAddCardIv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mLeftIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.iv_add_card:
                if (mDataList.size() == 5) {
                    mLeftIv.setVisibility(View.GONE);
                    mRightIv.setVisibility(View.GONE);
                    ToastUtils.showToast("最多5张名片，请编辑或删除一张名片");
                    return;
                }
                if (isAddCard) {//显示
                    isAddCard = false;
                    mLeftIv.setVisibility(View.VISIBLE);
                    mRightIv.setVisibility(View.VISIBLE);
                    mAddCardIv.setImageResource(R.drawable.icon_close_card);
                } else {//隐藏
                    isAddCard = true;
                    mLeftIv.setVisibility(View.GONE);
                    mRightIv.setVisibility(View.GONE);
                    mAddCardIv.setImageResource(R.drawable.icon_add_card);
                }
                break;
            case R.id.iv_right:
                Intent intent = new Intent(MakeCardActivity.this, EditCardActivity.class);
                intent.putExtra("hasCard", hasCard);
                intent.putExtra("addCard", "addCard");
                startActivity(intent);
                break;
            case R.id.iv_left:
                photo();
                break;
            default:
                break;
        }
    }

    private void photo() {
        selectList.clear();
       PictureSelector.create(MakeCardActivity.this)
                .openCamera(PictureMimeType.ofImage())
                .enableCrop(true)
                .showCropFrame(false)
                .showCropGrid(false)
                .selectionMedia(selectList)
                .freeStyleCropEnabled(false)
                .enableCrop(true)// 是否裁剪
                .showCropGrid(false)
                .selectionMedia(selectList)// 是否传入已选图片
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .withAspectRatio(400, 209)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .rotateEnabled(true)
                .scaleEnabled(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        Intent intent = new Intent(MakeCardActivity.this, TheCardActivity.class);
                        intent.putExtra("info","info");
                        intent.putExtra("path", media.getCompressPath());
                        startActivity(intent);
                    }
                    break;
            }
        }
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
    public void onEditItemClick(CardList.Cards item) {

        if (item.getTemplatestl().equals("0")) {
            Intent intent = new Intent(MakeCardActivity.this, TheCardActivity.class);
            intent.putExtra("cardId", item.getId());
            intent.putExtra("uid", item.getUid());
            intent.putExtra("name", item.getName());
            intent.putExtra("position", item.getPosition());
            intent.putExtra("company", item.getCompany());
            intent.putExtra("card", item.getCard());
            startActivity(intent);
        } else {
            Intent intent = new Intent(MakeCardActivity.this, EditCardActivity.class);
            intent.putExtra("cardId", item.getId());
            intent.putExtra("uid", item.getUid());
            intent.putExtra("name", item.getName());
            intent.putExtra("position", item.getPosition());
            intent.putExtra("company", item.getCompany());
            intent.putExtra("phone", item.getPhone());
            intent.putExtra("mobile", item.getMobile());
            intent.putExtra("fax", item.getFax());
            intent.putExtra("email", item.getEmail());
            intent.putExtra("address", item.getAddress());
            intent.putExtra("postal", item.getPostal());
            intent.putExtra("templatestl", item.getTemplatestl());
            intent.putExtra("card", item.getCard());
            intent.putExtra("logo", item.getLogo());
            intent.putExtra("ctime", item.getCtime());
            intent.putExtra("send", item.getSend());
            intent.putExtra("date", item.getDate());
            intent.putExtra("hasCard", hasCard);
            startActivity(intent);
        }

    }

    @Override
    public void onCardItemClick(View view ,CardList.Cards item) {
        mTransiTion.startViewerActivity(view, item.getCard());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new CardEvent());
        EventBus.getDefault().unregister(MakeCardActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdataData(MessageEvent messageEvent) {//更新名片列表
        mTextTv.setVisibility(View.GONE);
        loadData(mid);
    }



}
