package com.android.nana.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.ForwardAdapter;
import com.android.nana.adapter.GroupAdapter;
import com.android.nana.adapter.LatelyAdapter;
import com.android.nana.bean.ForwardEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.ForwardEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.widget.OverrideEditText;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by lenovo on 2017/10/14.
 * 转发
 */

public class ForwardActivity extends BaseActivity implements View.OnClickListener, LatelyAdapter.LateInterface, ForwardAdapter.ForwardListener, GroupAdapter.GroupsInterface {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight1Tv;

    private LinearLayout mGroupLl, mFriendsLl;
    private Message message;
    private ImageMessage imageMessage;
    private OverrideEditText mSearchEt;
    private Conversation mConversation;
    private List<Conversation> conversationList;
    private ListView mListView;
    private LatelyAdapter mAdapter;
    private int num = 0;//记录选中条数
    private String mid;
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private Map<String, String> hashMap = new HashMap<>();
    private String keyword;
    private ArrayList<String> mArrayUid = new ArrayList<>();//存储选中的用户id

    private ListView mFriendsLv;
    private ListView mGroupsLv;
    private GroupAdapter mGroupAdapter;
    private LinearLayout mDisplayLl, mSearchLl;
    private ForwardAdapter mFriendsAdapter;

    private LinearLayout mFrindsLvLl, mGroupLvLl;
    private ArrayList<ForwardEntity.Friends> friends = new ArrayList<>();
    ArrayList<ForwardEntity.Groups> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mid = (String) SharedPreferencesUtils.getParameter(ForwardActivity.this, "userId", "");

        if (null != getIntent().getParcelableExtra("imageMsg")) {
            imageMessage = getIntent().getParcelableExtra("imageMsg");
        }

        if (null != getIntent().getParcelableExtra("msg")) {
            message = getIntent().getParcelableExtra("msg");
        }

        if (!EventBus.getDefault().isRegistered(ForwardActivity.this)) {
            EventBus.getDefault().register(ForwardActivity.this);
        }
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchEt.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                mSearchEt.setText("");
                mAdapter.notifyDataSetChanged();
            }
        });

        mSearchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEt.setCursorVisible(true);
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
                    /**
                     * 防止用户选择最佳聊天在搜索
                     */
                    num = 0;
                    mRight1Tv.setText("确定");
                    query(keyword);
                    return true;
                }
                return false;
            }
        });

    }

    private void query(String keyword) {//搜索
        HomeDbHelper.searchPushFriend(mid, keyword, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mDisplayLl.setVisibility(View.GONE);
                        mSearchLl.setVisibility(View.VISIBLE);//显示搜索view

                        friends = parseFriendsData(successJson);
                        groups = parseGroupsData(successJson);
                        if (friends.size() > 0) {
                            mFrindsLvLl.setVisibility(View.VISIBLE);
                            mFriendsAdapter = new ForwardAdapter(ForwardActivity.this, friends, ForwardActivity.this);
                            mFriendsLv.setAdapter(mFriendsAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mFrindsLvLl.setVisibility(View.GONE);
                        }

                        if (groups.size() > 0) {
                            mGroupLvLl.setVisibility(View.VISIBLE);
                            mGroupAdapter = new GroupAdapter(ForwardActivity.this, groups, ForwardActivity.this);
                            mGroupsLv.setAdapter(mGroupAdapter);
                            mGroupAdapter.notifyDataSetChanged();
                        } else {
                            mGroupLvLl.setVisibility(View.GONE);
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
    protected void bindViews() {
        setContentView(R.layout.activity_forward);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight1Tv = findViewById(R.id.toolbar_right_2);

        mGroupLl = findViewById(R.id.ll_group);
        mFriendsLl = findViewById(R.id.ll_friends);
        mSearchEt = findViewById(R.id.et_search);
        mListView = findViewById(R.id.list_view);

        mFriendsLv = findViewById(R.id.lv_friends);
        mGroupsLv = findViewById(R.id.lv_group);
        mDisplayLl = findViewById(R.id.ll_display);
        mSearchLl = findViewById(R.id.ll_search);

        mFrindsLvLl = findViewById(R.id.ll_lv_friends);
        mGroupLvLl = findViewById(R.id.ll_lv_group);

    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("发送给");
        mRight1Tv.setVisibility(View.VISIBLE);
        mRight1Tv.setText("确定");
        mRight1Tv.setTextColor(getResources().getColor(R.color.white));
        initData();
    }

    private void initData() {

        if (RongIM.getInstance() != null) {
            conversationList = RongIM.getInstance().getRongIMClient().getConversationList();
            if (conversationList != null && conversationList.size() > 0) {
                mAdapter = new LatelyAdapter(ForwardActivity.this, conversationList, true, ForwardActivity.this);
                mAdapter.notifyDataSetChanged();
            }
        }
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight1Tv.setOnClickListener(this);

        mGroupLl.setOnClickListener(this);
        mFriendsLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                finish();
                break;
            case R.id.toolbar_right_2:

                addUsers();
                break;
            case R.id.ll_friends:
                Intent intent = new Intent(ForwardActivity.this, SelectForwardActivity.class);
                intent.putExtra("msg", message);
                intent.putExtra("imageMsg", imageMessage);
                startActivity(intent);
                break;
            case R.id.ll_group:
                Intent intent1 = new Intent(ForwardActivity.this, SelectGroupActivity.class);
                intent1.putExtra("msg", message);
                intent1.putExtra("imageMsg", imageMessage);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(ForwardActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinish(ForwardEvent forwardEvent) {
        this.finish();
    }

    @Override
    public void onItemClick(View view, CheckBox checkBox, int position) {
        mConversation = conversationList.get((Integer) view.getTag());

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            conversationList.get(position).setTop(false);
            num--;
            if (num == 0) {
                mRight1Tv.setText("确定");
            } else {
                mRight1Tv.setText("确定(" + num + ")");
            }
            if (hashMap.size() > 0) {
                hashMap.remove(mConversation.getTargetId());
            }
        } else {
            hashMap.put(mConversation.getTargetId(), mConversation.getConversationType().getName());
            checkBox.setChecked(true);
            conversationList.get(position).setTop(true);
            num++;
            mRight1Tv.setText("确定(" + num + ")");
        }
    }

    private void addUsers() {
        if (hashMap.size() > 0) {
            Iterator iter = hashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (entry.getValue().equals("group")) {
                    RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, entry.getKey().toString(), message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            Log.d("=====转发失败===", errorCode.getMessage());
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            dismissProgressDialog();
                            ForwardActivity.this.finish();
                        }
                    });
                } else {

                    RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, entry.getKey().toString(), message.getContent(), null, "Forward", new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                            Log.e("发送失败===", errorCode.getMessage());
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            ForwardActivity.this.finish();
                        }
                    });
                }
            }
        } else if (mArrayUid.size() > 0) {

            for (int i = 0; i < mArrayUid.size(); i++) {
                if (mArrayUid.get(i).contains("groups")) {
                    String str = mArrayUid.get(i).substring(6, mArrayUid.get(i).length());

                    RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, str, message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            Log.d("=====转发失败===", errorCode.getMessage());
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            dismissProgressDialog();
                            ForwardActivity.this.finish();
                        }
                    });
                } else {

                    RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, mArrayUid.get(i), message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                            Log.e("发送失败===", errorCode.getMessage());
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            ForwardActivity.this.finish();
                        }
                    });
                }
            }
        } else {
            this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchEt.setCursorVisible(false);
    }

    public ArrayList<ForwardEntity.Friends> parseFriendsData(String result) throws JSONException {//好友 解析
        ArrayList<ForwardEntity.Friends> detail = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(result);
        JSONObject data = new JSONObject(jsonobject.getString("data"));
        JSONArray friends = new JSONArray(data.getString("friends"));

        if (friends.length() > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < friends.length(); i++) {
                ForwardEntity.Friends item = gson.fromJson(friends.optJSONObject(i).toString(), ForwardEntity.Friends.class);
                detail.add(item);
            }
        }
        return detail;
    }

    public ArrayList<ForwardEntity.Groups> parseGroupsData(String result) throws JSONException {//群组 解析
        ArrayList<ForwardEntity.Groups> detail = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(result);
        JSONObject data = new JSONObject(jsonobject.getString("data"));
        JSONArray groups = new JSONArray(data.getString("groups"));

        if (groups.length() > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < groups.length(); i++) {
                ForwardEntity.Groups item = gson.fromJson(groups.optJSONObject(i).toString(), ForwardEntity.Groups.class);
                detail.add(item);
            }
        }
        return detail;
    }

    @Override
    public void onContentClick(View view, CheckBox checkBox, int position) {
        ForwardEntity.Friends mItem = friends.get((Integer) view.getTag());
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            friends.get(position).setChcked(false);
            num--;
            if (num == 0) {
                mRight1Tv.setText("确定");
            } else {
                mRight1Tv.setText("确定(" + num + ")");
            }
            if (mArrayUid.size() > 0) {
                mArrayUid.remove(mItem.getId());
            }
        } else {
            mArrayUid.add(mItem.getId());
            checkBox.setChecked(true);
            friends.get(position).setChcked(true);
            num++;
            mRight1Tv.setText("确定(" + num + ")");
        }
    }

    @Override
    public void onClick(View view, CheckBox checkBox, int position) {
        ForwardEntity.Groups mItem = groups.get((Integer) view.getTag());
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            groups.get(position).setChcked(false);
            num--;
            if (num == 0) {
                mRight1Tv.setText("确定");
            } else {
                mRight1Tv.setText("确定(" + num + ")");
            }
            if (mArrayUid.size() > 0) {
                mArrayUid.remove(mItem.getGroupId());
            }
        } else {
            mArrayUid.add("groups" + mItem.getGroupId());
            checkBox.setChecked(true);
            groups.get(position).setChcked(true);
            num++;
            mRight1Tv.setText("确定(" + num + ")");
        }
    }


}
