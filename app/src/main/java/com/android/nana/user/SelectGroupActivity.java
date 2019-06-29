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
import com.android.nana.adapter.GroupsAdapter;
import com.android.nana.bean.GroupsEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.ForwardEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by lenovo on 2017/10/16.
 */

public class SelectGroupActivity extends BaseActivity implements View.OnClickListener, GroupsAdapter.GroupsInterface {

    private TextView mBackTv;
    private TextView mTitleTv;
    private TextView mRight1Tv;
    private String mid;

    private ImageMessage imageMsg;
    private Message message;
    private ListView mListView;
    private MultipleStatusView mMultipleStatusView;
    private GroupsAdapter mAdapter;
    private int num = 0;//记录选中条数
    private ArrayList<String> mArrayUid = new ArrayList<>();//存储选中的用户id
    private ArrayList<GroupsEntity> mDataList = new ArrayList<>();

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
        setContentView(R.layout.activity_select_group);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRight1Tv = findViewById(R.id.toolbar_right_2);
        mListView = findViewById(R.id.lv_group);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mRight1Tv.setText("确定");
        mRight1Tv.setVisibility(View.VISIBLE);
        mTitleTv.setText("选择群聊");
        mRight1Tv.setTextColor(getResources().getColor(R.color.white));

        if (NetWorkUtils.isNetworkConnected(SelectGroupActivity.this)) {
            showProgressDialog("", "加载中...");
            mid = (String) SharedPreferencesUtils.getParameter(SelectGroupActivity.this, "userId", "");
            loadData(mid);
        } else {
            mMultipleStatusView.noNetwork();
        }
        mAdapter = new GroupsAdapter(SelectGroupActivity.this, mDataList, this, true);
        mListView.setAdapter(mAdapter);
    }


    private void loadData(String mid) {
        HomeDbHelper.getGroupList(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mDataList.clear();
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ArrayList<GroupsEntity> entities = parseData(successJson);
                        if (entities.size() > 0) {
                            for (GroupsEntity item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    dismissProgressDialog();
                                    mDataList.add(item);
                                }
                            }
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            dismissProgressDialog();
                            mMultipleStatusView.empty();
                        }
                    } else if (result.getString("state").equals("-1")) {
                        mMultipleStatusView.empty();
                        dismissProgressDialog();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight1Tv.setOnClickListener(this);
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

    private void addUsers() {
        showProgressDialog("", "加载中....");
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
                            SelectGroupActivity.this.finish();
                        }

                        @Override
                        public void onSuccess(Message message) {
                            SelectGroupActivity.this.finish();
                            ToastUtils.showToast("发送成功！");
                            EventBus.getDefault().post(new ForwardEvent());//发送通知关闭转发页面
                        }

                        @Override
                        public void onProgress(Message message, int i) {

                        }
                    });
                }else {

                    RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP, mArrayUid.get(i), message.getContent(), null, null, new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                            Log.d("=====转发失败===", errorCode.getMessage());
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            dismissProgressDialog();
                            SelectGroupActivity.this.finish();
                            EventBus.getDefault().post(new ForwardEvent());//发送通知关闭转发页面
                        }
                    });
                }
            }
        } else {
            this.finish();
        }
    }

    public ArrayList<GroupsEntity> parseData(String result) throws JSONException {//Gson 解析
        ArrayList<GroupsEntity> detail = new ArrayList<>();

        JSONObject jsonobject = new JSONObject(result);
        JSONObject data = new JSONObject(jsonobject.getString("data"));
        JSONArray groups = new JSONArray(data.getString("groups"));
        Gson gson = new Gson();
        for (int i = 0; i < groups.length(); i++) {
            GroupsEntity entity = gson.fromJson(groups.optJSONObject(i).toString(), GroupsEntity.class);
            detail.add(entity);
        }

        return detail;
    }

    @Override
    public void onItemClick(View view, CheckBox checkBox, int position) {
        GroupsEntity entity = mDataList.get((Integer) view.getTag());

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            mDataList.get(position).setChcked(false);
            num--;
            if (num == 0) {
                mRight1Tv.setText("确定");
            } else {
                mRight1Tv.setText("确定(" + num + ")");
            }

            if (mArrayUid.size() > 0) {
                mArrayUid.remove(entity.getGroupId());
            }
        } else {
            mDataList.get(position).setChcked(true);
            mArrayUid.add(entity.getGroupId());
            checkBox.setChecked(true);
            num++;
            mRight1Tv.setText("确定(" + num + ")");
        }
    }
}
