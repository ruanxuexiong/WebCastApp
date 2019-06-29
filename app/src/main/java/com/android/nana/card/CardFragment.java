package com.android.nana.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.card.bean.CardIndexBean;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.CardEvent;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.StateButton;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/16.
 */

public class CardFragment extends BaseFragment implements OnItemClickListener {

    private ConvenientBanner mBanner;
    private StateButton mEditCardBtn;
    private ArrayList<String> localImages = new ArrayList<>();

    private String mid;
    private String mHasCard, mIsPrint;//是否有名片
    private StateButton mSendCardBtn;
    private StateButton mMakeCardBtn;
    private TransitionSingleHelper mTransiTion;
    private ArrayList<CardIndexBean.Cards> mDataList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(CardFragment.this)) {
            EventBus.getDefault().register(CardFragment.this);
        }
        mTransiTion = new TransitionManager(getActivity()).getSingle();
    }

    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }


    @Override
    protected void initData() {
        showProgressDialog("", "加载中...");
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        loadData(mid);
    }

    private void loadData(String mid) {
        localImages.clear();
        CardDbHelper.index(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mHasCard = data.getString("hasCard");
                        mIsPrint = data.getString("isPrint");//isPrint（1=是  -1=否）
                        if (parseData(successJson).size() > 0) {
                            mDataList = parseData(successJson);
                            for (int i = 0; i < mDataList.size(); i++) {
                                localImages.add(mDataList.get(i).getCard());
                            }
                            if (localImages.size() == 1) {
                                dismissProgressDialog();
                                mBanner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
                                            @Override
                                            public LocalImageHolderView createHolder() {
                                                return new LocalImageHolderView();
                                            }
                                        }, localImages)
                                        .setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int position) {
                                                doPicture(position);
                                            }
                                        });
                            } else {
                                dismissProgressDialog();
                                mBanner.setPages(
                                        new CBViewHolderCreator<LocalImageHolderView>() {
                                            @Override
                                            public LocalImageHolderView createHolder() {
                                                return new LocalImageHolderView();
                                            }
                                        }, localImages)
                                        .setPageIndicator(new int[]{R.drawable.icon_slide_no, R.drawable.icon_slide_yes})
                                        .setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(int position) {
                                                doPicture(position);
                                            }
                                        });
                            }
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

    private void doPicture(int position) {
        mTransiTion.startViewerActivity(mBanner, localImages.get(position));
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_card;
    }

    @Override
    public void initView() {
        mMakeCardBtn = mContentView.findViewById(R.id.btn_make_card);
        mSendCardBtn = mContentView.findViewById(R.id.btn_send_card);
        mBanner = mContentView.findViewById(R.id.banner_card);
        mEditCardBtn = mContentView.findViewById(R.id.btn_edit_card);
    }

    @Override
    public void bindEvent() {
        mMakeCardBtn.setOnClickListener(this);
        mEditCardBtn.setOnClickListener(this);
        mSendCardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_card:
                Intent intent = new Intent(getActivity(), MakeCardActivity.class);
                intent.putExtra("hasCard", mHasCard);
                startActivity(intent);
                break;
            case R.id.btn_send_card:
                if (mHasCard.equals("-1")) {
                    ToastUtils.showToast("请先制作您的第一张名片");
                } else {
                    startActivity(new Intent(getActivity(), SendCardActvity.class));
                }
                break;
            case R.id.btn_make_card:
                if (mHasCard.equals("-1")) {
                    ToastUtils.showToast("请先制作您的第一张名片");
                } else if (mIsPrint.equals("-1")) {
                    ToastUtils.showToast("抱歉，每月只能免费打印一次名片");
                } else {
                    startActivity(new Intent(getActivity(), PrintCardActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(CardFragment.this);
        mBanner.stopTurning();
    }

    @Override
    public void onItemClick(int position) {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdataData(CardEvent event) {//更新名片
        loadData(mid);
    }


    public ArrayList<CardIndexBean.Cards> parseData(String result) {
        ArrayList<CardIndexBean.Cards> moment = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));

            JSONArray array = new JSONArray(data.getString("cards"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                CardIndexBean.Cards entity = gson.fromJson(array.optJSONObject(i).toString(), CardIndexBean.Cards.class);
                moment.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moment;
    }


}
