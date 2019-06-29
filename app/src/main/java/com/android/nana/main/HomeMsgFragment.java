package com.android.nana.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.ConversationListAdapterEx;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.bean.GroupEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MsgEvent;
import com.android.nana.mail.SelectFriendsActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.SearchActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * Created by lenovo on 2017/9/7.
 */

public class HomeMsgFragment extends BaseRequestFragment implements View.OnClickListener, RongIM.UserInfoProvider, RongIM.GroupInfoProvider {

    private TextView mTitleTv;
    private RelativeLayout mSearchRl;
    private ConversationListFragment mConversationListFragment = null;
    private Conversation.ConversationType[] mConversationsTypes = null;

    private FragmentTransaction mFragmetTrans;
    private FragmentManager mFragmentManager;
    private TextView mRightTv;
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(HomeMsgFragment.this)) {
            EventBus.getDefault().register(HomeMsgFragment.this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    protected void findViewById() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mSearchRl = (RelativeLayout) findViewById(R.id.rl_search);
        mRightTv = (TextView) findViewById(R.id.toolbar_right_2);
    }

    @Override
    protected void init() {
        mTitleTv.setText("消息");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText("发起群聊");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mFragmentManager = getActivity().getSupportFragmentManager();
        mFragmetTrans = mFragmentManager.beginTransaction();
        mConversationListFragment = initConversationList();
        mFragmetTrans.replace(R.id.view_layout, mConversationListFragment).commit();
    }

    private ConversationListFragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                    .build();
            mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                    Conversation.ConversationType.GROUP,
                    Conversation.ConversationType.PUBLIC_SERVICE,
                    Conversation.ConversationType.APP_PUBLIC_SERVICE,
                    Conversation.ConversationType.SYSTEM
            };
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }


    @Override
    protected void setListener() {
        mSearchRl.setOnClickListener(this);
        mRightTv.setOnClickListener(this);

        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupInfoProvider(this, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_search:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
            case R.id.toolbar_right_2:
                Intent intent = new Intent(new Intent(getContext(), SelectFriendsActivity.class));
                intent.putExtra("createGroup", true);
                getContext().startActivity(intent);
                break;
        }
    }


    private void loadData() {

        String userid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        HomeDbHelper.friendsBook(userid, new IOAuthCallBack() {
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
                        for (FriendsBookEntity entity : mFriendsData) {
                            UserInfo userInfo = new UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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
    public UserInfo getUserInfo(final String s) {

       /* HomeDbHelper.getUserName(s, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        for (FriendsBookEntity entity : parseNmaeData(successJson)) {
                            UserInfo userInfo = new UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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
*/
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ArrayList<FriendsBookEntity> parseData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
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
        EventBus.getDefault().unregister(HomeMsgFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpMsgDate(MsgEvent msgEvent) {
        loadData();
    }

    @Override
    public Group getGroupInfo(final String groupsId) {

        if (groupsId.contains("activity")) {
            HomeDbHelper.getActivityInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }
                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        } else {
            HomeDbHelper.getGroupsInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }
                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        return null;
    }


    public ArrayList<GroupEntity> parseGroupData(String result) {//Gson 解析
        ArrayList<GroupEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                GroupEntity entity = gson.fromJson(data.optJSONObject(i).toString(), GroupEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    public ArrayList<FriendsBookEntity> parseNmaeData(String result) {//Gson 解析用户头像
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

}
