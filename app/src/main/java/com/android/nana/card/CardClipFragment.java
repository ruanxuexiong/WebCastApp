package com.android.nana.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.card.adapter.MakeCardAdapter;
import com.android.nana.card.bean.CardList;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.TheCardEvent;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.SharedPreferencesUtils;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by lenovo on 2018/1/16.
 */

public class CardClipFragment extends BaseFragment implements MakeCardAdapter.MakeCardListener {

    private RelativeLayout mPhotoRl;
    private RelativeLayout mAlbumRl;
    private String mid;
    private MakeCardAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TransitionSingleHelper mTransiTion;
    private LinearLayout mSearchll;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<CardList.Cards> mDataList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(CardClipFragment.this)) {
            EventBus.getDefault().register(CardClipFragment.this);
        }
    }

    public static CardClipFragment newInstance() {
        CardClipFragment fragment = new CardClipFragment();
        return fragment;
    }

    @Override
    protected void initData() {
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        showProgressDialog("", "加载中...");
        loadData(mid);

        mTransiTion = new TransitionManager(getActivity()).getSingle();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 布局管理器。
        mAdapter = new MakeCardAdapter(getActivity(), mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData(String mid) {
        mDataList.clear();
        CardDbHelper.cardList(mid, "2", new IOAuthCallBack() {
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
                        } else {
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

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_card_clip;
    }

    @Override
    public void initView() {
        mPhotoRl = mContentView.findViewById(R.id.navigation_photo);
        mAlbumRl = mContentView.findViewById(R.id.navigation_album);
        mRecyclerView = mContentView.findViewById(R.id.recycler_view);
        mSearchll = mContentView.findViewById(R.id.ll_search);
    }

    @Override
    public void bindEvent() {
        mPhotoRl.setOnClickListener(this);
        mAlbumRl.setOnClickListener(this);
        mSearchll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigation_photo:
                photo();
                break;
            case R.id.navigation_album:
                album();
                break;
            case R.id.ll_search:
                Intent intent = new Intent(getActivity(), SearchCardActivity.class);
                intent.putExtra("mid",mid);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void album() {
        PictureSelector.create(CardClipFragment.this)
                .openGallery(PictureMimeType.ofImage())
                .enableCrop(true)
                .maxSelectNum(1)// 最大图片选择数量 int
                .isCamera(false)
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

    private void photo() {
        PictureSelector.create(CardClipFragment.this)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        Intent intent = new Intent(getContext(), TheCardActivity.class);
                        intent.putExtra("type", "2");
                        intent.putExtra("create", "create");
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(CardClipFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdataData(TheCardEvent event) {//更新名片列表
        loadData(mid);
    }

    @Override
    public void onEditItemClick(CardList.Cards item) {

        Intent intent = new Intent(getActivity(), TheCardActivity.class);
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
    public void onCardItemClick(View view, CardList.Cards item) {
        mTransiTion.startViewerActivity(view, item.getCard());
    }
}
