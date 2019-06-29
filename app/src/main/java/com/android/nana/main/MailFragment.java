package com.android.nana.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.FollowListActivity;
import com.android.nana.adapter.FriendsBookAdapter;
import com.android.nana.adapter.ServiceBookAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.AddFriendEvent;
import com.android.nana.eventBus.MailEvent;
import com.android.nana.friend.AddNewFriendActivity;
import com.android.nana.mail.GroupListActivity;
import com.android.nana.mail.MeFollowActivity;
import com.android.nana.mail.NewFriendActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinComparatorFriend;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lenovo on 2017/8/29.
 */

public class MailFragment extends BaseRequestFragment implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, FriendsBookAdapter.SortListener, ServiceBookAdapter.ServiceListener {

    private TextView mTitleTv, mDialogTv;
    private ListView mListView;
    private SideBar mSideBar;
    private FriendsBookAdapter mAdapter;
    private ServiceBookAdapter mServiceAdapter;
    private LinearLayout mSearchRl;
    private LinearLayout mListViewLayout;

    private CharacterParser mParser;
    private PinyinComparatorFriend mPinyin;
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();
    private ArrayList<FriendsBookEntity> mServiceData = new ArrayList<>();
    private String mid;
    private TextView mHeaderGroupTv, mFollowTv, mNumTv, mFansNumTv;
    private LinearLayout mHeaderFansLl;
    private LinearLayout mFriendLl;
    private String sNum;//新的朋友通知
    private boolean isNumTrue = false;
    private static int ON_TOP = 1;
    private String mFansNum = "";
    private TextView mAction2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MailFragment.this)) {
            EventBus.getDefault().register(MailFragment.this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mail;
    }

    @Override
    protected void findViewById() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mListView = (ListView) findViewById(R.id.lv_follow);
        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mDialogTv = (TextView) findViewById(R.id.tv_dialog);
        mSearchRl = (LinearLayout) findViewById(R.id.rl_search);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mail_header, null);
        mListViewLayout = view.findViewById(R.id.layout_list_view);
        mHeaderFansLl = view.findViewById(R.id.ll_fans);
        mHeaderGroupTv = view.findViewById(R.id.tv_group);
        mFollowTv = view.findViewById(R.id.tv_follow);
        mFriendLl = view.findViewById(R.id.ll_friend);
        mNumTv = view.findViewById(R.id.tv_msg_num);
        mFansNumTv = view.findViewById(R.id.tv_fans_num);
        setOnClickListener(mHeaderFansLl, mHeaderGroupTv, mFollowTv, mFriendLl);
        mListView.addHeaderView(view);

        mAction2 = (TextView) findViewById(R.id.toolbar_right_2);
    }


    @Override
    public void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    private void setOnClickListener(LinearLayout mHeaderFansll, TextView mHeaderGroupTv, TextView mHeaderFollowTv, LinearLayout mHeaderFriendLl) {//首页点击事件
        mHeaderFansll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowListActivity.class);
                intent.putExtra("fansNum", mFansNum);
                startActivity(intent);
            }
        });
        mHeaderGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(new Intent(getActivity(), GroupListActivity.class));
                startActivity(intent);
            }
        });

        mHeaderFollowTv.setOnClickListener(new View.OnClickListener() {//我关注的
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MeFollowActivity.class);
                intent.putExtra("mid", mid);
                startActivity(intent);
            }
        });
        mHeaderFriendLl.setOnClickListener(new View.OnClickListener() {//新的朋友
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                intent.putExtra("mid", mid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void init() {
        mTitleTv.setText("通讯录");
        mSideBar.setTextView(mDialogTv);

        mAction2.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_mail_add_friend);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction2Click();
            }
        });
    }

    private void onAction2Click() {
        Intent intent = new Intent(getActivity(), AddNewFriendActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        initData();
    }

    private void initData() {//初始化数据
        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();

        HomeDbHelper.friendsBook(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mFriendsData = parseData(successJson);
                        mServiceData = parseServiceData(successJson);
                        if (mServiceData.size() > 0) {
                            mListViewLayout.removeAllViews();
                            View mView = LayoutInflater.from(getActivity()).inflate(R.layout.mail_list_view, null);
                            ListView mListView = mView.findViewById(R.id.lv_follow);
                            mServiceAdapter = new ServiceBookAdapter(getContext(), mServiceData, MailFragment.this);
                            mListView.setAdapter(mServiceAdapter);
                            setListViewHeightBasedOnChildren(mListView);
                            mListViewLayout.addView(mView);
                            mServiceAdapter.notifyDataSetChanged();
                        }

                        if (mFriendsData.size() > 0) {
                            Collections.sort(mFriendsData, mPinyin);
                            mAdapter = new FriendsBookAdapter(getContext(), mFriendsData, MailFragment.this);
                            mAdapter.notifyDataSetChanged();
                        }
                        mListView.setAdapter(mAdapter);


                        String num = result.getString("description");
                        if (!num.equals("0")) {
                            mNumTv.setVisibility(View.VISIBLE);
                            mNumTv.setText(num);
                            isNumTrue = true;
                            EventBus.getDefault().post(new AddFriendEvent());//更新通讯录数量
                        } else {
                            mNumTv.setVisibility(View.GONE);
                            if (isNumTrue) {
                                EventBus.getDefault().post(new AddFriendEvent());//更新通讯录数量
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }

    @Override
    protected void setListener() {
        mSearchRl.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {//拼音滑动
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }

    private ArrayList<FriendsBookEntity> parseServiceData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray friends = new JSONArray(data.getString("service"));

            Gson gson = new Gson();
            for (int i = 0; i < friends.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(friends.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private ArrayList<FriendsBookEntity> parseData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray friends = new JSONArray(data.getString("friends"));

            if (Integer.valueOf(data.getString("follownum")).intValue() > 0) {
                mFansNumTv.setVisibility(View.GONE);
                mFansNum = data.getString("follownum");
            }
            Gson gson = new Gson();
            for (int i = 0; i < friends.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(friends.optJSONObject(i).toString(), FriendsBookEntity.class);

                String pinyin = mParser.getSelling(entity.getUname());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }

                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    public void onContentClick(View view, CheckBox checkBox, int position) {
        FriendsBookEntity entity = mFriendsData.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", entity.getId());
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, CheckBox checkBox, int position) {

    }

    @Override
    public void onCallClick(View view) {

    }

    @Override
    public void onVideoClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MailFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdate(MailEvent mailEvent) {

        //   mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        //  initData();

    }

    @Override
    public void onServiceItemClick(View view, int position) {
        FriendsBookEntity entity = mServiceData.get((Integer) view.getTag());
        Intent intent = new Intent(getActivity(), EditDataActivity.class);
        intent.putExtra("UserId", entity.getId());
        startActivity(intent);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {

        } else {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }

}
