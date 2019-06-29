package com.android.nana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.MeInStartAdapter;
import com.android.nana.adapter.MeStartAdapter;
import com.android.nana.bean.MeStartEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.util.SharedPreferencesUtils;
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

public class MeFragment extends BaseFragment implements MeStartAdapter.MeStartListener, MeInStartAdapter.MeInStartListener {

    private ImageButton mBackBtn;
    private TextView mTitleTv;

    private RelativeLayout mMeRl, mMeInRl;
    private ImageView mRightIv, mRightInIv;
    private boolean isMeView = true;
    private boolean isMeInView = true;
    private RecyclerView mMeRecyclerView, mMeInRecyclerView;
    private LinearLayout mMeListll, mMeInListll;
    private UserInfo mUserInfo;
    private MeStartAdapter mAdapter;
    private MeInStartAdapter mAdapterIn;
    private ArrayList<MeStartEntity> mDataList = new ArrayList<>();
    private ArrayList<MeStartEntity> mDataInList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MeFragment.this)) {
            EventBus.getDefault().register(MeFragment.this);
        }
    }

    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    protected void initData() {
        mTitleTv.setText("我的群组");
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(getActivity(), "userInfo", UserInfo.class);
        loadData(mUserInfo.getId());//我发起的活动
        loadInData(mUserInfo.getId());
    }

    private void loadInData(String id) {
        mDataInList.clear();
        HomeDbHelper.followActivities(id, "1", 1, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (MeStartEntity item : parseData(successJson)) {
                                if (!mDataInList.contains(item)) {
                                    mDataInList.add(item);
                                }
                            }
                        }
                        mAdapterIn.notifyDataSetChanged();
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

    private void loadData(String mid) {//加载数据

        mDataList.clear();
        HomeDbHelper.startActivities(mid, "1", 1, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            for (MeStartEntity item : parseData(successJson)) {
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
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.me_fragment;
    }

    @Override
    public void initView() {
        mBackBtn = mContentView.findViewById(R.id.common_btn_back);
        mTitleTv = mContentView.findViewById(R.id.common_txt_title);

        mMeListll = mContentView.findViewById(R.id.ll_me_list);
        mMeInListll = mContentView.findViewById(R.id.ll_me_in_list);

        mMeRl = mContentView.findViewById(R.id.rl_me);
        mMeInRl = mContentView.findViewById(R.id.rl_me_in);

        //   mRightIv = mContentView.findViewById(R.id.iv_right);
        // mRightInIv = mContentView.findViewById(R.id.iv_in_right);

        mMeRecyclerView = mContentView.findViewById(R.id.me_list_view);
        mMeInRecyclerView = mContentView.findViewById(R.id.me_list_in_view);

        mMeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        mMeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MeStartAdapter(getActivity(), mDataList, this);
        mMeRecyclerView.setAdapter(mAdapter);


        mMeInRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        mMeInRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapterIn = new MeInStartAdapter(getActivity(), mDataInList, this);
        mMeInRecyclerView.setAdapter(mAdapterIn);
    }

    @Override
    public void bindEvent() {
        mBackBtn.setOnClickListener(this);
        mMeInRl.setOnClickListener(this);
        mMeRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_btn_back:
                getActivity().finish();
                break;
            case R.id.rl_me:
                Intent intent1 = new Intent(getActivity(), MeLaunchActivity.class);
                intent1.putExtra("mid", mUserInfo.getId());
                startActivity(intent1);
                break;
            case R.id.rl_me_in://我参加的

                Intent intent = new Intent(getActivity(), MeInActivity.class);
                intent.putExtra("mid", mUserInfo.getId());
                startActivity(intent);
                break;
        }
    }

    public ArrayList<MeStartEntity> parseData(String result) {//Gson 解析
        ArrayList<MeStartEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                MeStartEntity entity = gson.fromJson(data.optJSONObject(i).toString(), MeStartEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void onItemClick(String id) {
        Intent intent = new Intent(getActivity(), MeActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mid", mUserInfo.getId());
        startActivity(intent);

      /*  Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mid",  mUserInfo.getId());
        startActivity(intent);*/
    }

    @Override
    public void onMeItemClick(String id) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("mid", mUserInfo.getId());
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpIndate(MessageEvent messageEvent) {
       /*if (messageEvent.message.equals("updateData")) {
            loadInData(mUserInfo.getId());//我参加的活动
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MeFragment.this);
    }
}
