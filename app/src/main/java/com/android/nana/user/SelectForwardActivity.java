package com.android.nana.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.SelectForwardAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.ForwardEvent;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinComparatorFriend;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by lenovo on 2017/10/14.
 * 转发选择好友
 */

public class SelectForwardActivity extends BaseActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, SelectForwardAdapter.SortListener {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight1Tv;
    private String mid;

    private SideBar mSideBar;
    private Message message;
    private ImageMessage imageMsg;
    private SelectForwardAdapter mAdapter;
    private CharacterParser mParser;
    private ListView mListView;
    private int num = 0;//记录选中条数
    private TextView mDialogTv;
    private ArrayList<String> mArrayUid = new ArrayList<>();//存储选中的用户id
    private PinyinComparatorFriend mPinyin;
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getIntent().getParcelableExtra("imageMsg")) {
            imageMsg = getIntent().getParcelableExtra("imageMsg");
        }
        if (null != getIntent().getParcelableExtra("msg")) {//融云发送的消息
            message = getIntent().getParcelableExtra("msg");
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_select_friends);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight1Tv = findViewById(R.id.toolbar_right_2);
        mListView = findViewById(R.id.lv_follow);
        mDialogTv = findViewById(R.id.tv_dialog);
        mSideBar = findViewById(R.id.sidrbar);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mRight1Tv.setText("确定");
        mTitleTv.setText("选择好友");
        mRight1Tv.setVisibility(View.VISIBLE);
        mRight1Tv.setTextColor(getResources().getColor(R.color.white));
        mid = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        mSideBar.setTextView(mDialogTv);
        initData();
    }

    private void initData() {
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
                        Collections.sort(mFriendsData, mPinyin);
                        mAdapter = new SelectForwardAdapter(SelectForwardActivity.this, mFriendsData, SelectForwardActivity.this, true);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
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
        mBackTv.setOnClickListener(this);
        mRight1Tv.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                addUsers();
                break;
            default:
                break;
        }
    }


    private ArrayList<FriendsBookEntity> parseData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray friends = new JSONArray(data.getString("friends"));

            Gson gson = new Gson();
            for (int i = 0; i < friends.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(friends.optJSONObject(i).toString(), FriendsBookEntity.class);
                entity.setUname(entity.getUname());
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
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }

    @Override
    public void onContentClick(View view, CheckBox checkBox, int position) {
        FriendsBookEntity entity = mFriendsData.get((Integer) view.getTag());

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            mFriendsData.get(position).setChcked(false);
            num--;
            if (num == 0) {
                mRight1Tv.setText("确定");
            } else {
                mRight1Tv.setText("确定(" + num + ")");
            }

            if (mArrayUid.size() > 0) {
                mArrayUid.remove(entity.getId());
            }
        } else {
            mArrayUid.add(entity.getId());
            mFriendsData.get(position).setChcked(true);
            checkBox.setChecked(true);
            num++;
            mRight1Tv.setText("确定(" + num + ")");
        }

    }


    private void addUsers() {
        if (mArrayUid.size() > 0) {
            for (int i = 0; i < mArrayUid.size(); i++) {
                ConversationActivity.tanchang = "-1";
                if (null != imageMsg) {

                    RongIM.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, mArrayUid.get(i), imageMsg, null, null, new RongIMClient.SendImageMessageCallback() {
                        @Override
                        public void onAttached(Message message) {

                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            ToastUtils.showToast("发送失败，请稍后重试！");
                            SelectForwardActivity.this.finish();
                        }

                        @Override
                        public void onSuccess(Message message) {
                            SelectForwardActivity.this.finish();
                            ToastUtils.showToast("发送成功！");
                            EventBus.getDefault().post(new ForwardEvent());//发送通知关闭转发页面
                        }

                        @Override
                        public void onProgress(Message message, int i) {

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
                            SelectForwardActivity.this.finish();
                            EventBus.getDefault().post(new ForwardEvent());//发送通知关闭转发页面
                        }
                    });
                }
            }
        } else {
            this.finish();
        }
    }
}
