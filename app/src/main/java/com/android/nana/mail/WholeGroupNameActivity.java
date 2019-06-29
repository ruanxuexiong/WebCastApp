package com.android.nana.mail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.FriendsBookAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinComparatorFriend;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lenovo on 2017/9/20.
 */

public class WholeGroupNameActivity extends BaseActivity implements View.OnClickListener,SideBar.OnTouchingLetterChangedListener, FriendsBookAdapter.SortListener {


    private TextView mTitleTv;
    private TextView mBackTv;
    private String sGroupId;

    private CharacterParser mParser;
    private PinyinComparatorFriend mPinyin;
    private FriendsBookAdapter mAdapter;
    private ListView mListView;
    private SideBar mSideBar;
    private TextView mDialogTv;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();
    private boolean isTalking = false;
    private String mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("GroupId")) {
            sGroupId = getIntent().getStringExtra("GroupId");
            if (NetWorkUtils.isNetworkConnected(WholeGroupNameActivity.this)) {
                showProgressDialog("", "加载中...");
                loadData();
            } else {
                mMultipleStatusView.noNetwork();
            }
        }
        mid = (String) SharedPreferencesUtils.getParameter(WholeGroupNameActivity.this, "userId", "");
    }

    private void loadData() {

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();

        HomeDbHelper.memberList(sGroupId, "", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        mTitleTv.setText("群成员(" + data.getString("num") + ")");
                        mFriendsData = parseData(successJson);
                        if (mFriendsData.size() > 0) {
                            Collections.sort(mFriendsData, mPinyin);
                            mAdapter = new FriendsBookAdapter(WholeGroupNameActivity.this, mFriendsData, WholeGroupNameActivity.this);
                            mListView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultipleStatusView.empty();
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
        setContentView(R.layout.activity_whole_group_name);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mListView = findViewById(R.id.lv_follow);
        mSideBar = findViewById(R.id.sidrbar);
        mDialogTv = findViewById(R.id.tv_dialog);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mSideBar.setTextView(mDialogTv);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
        }
    }

    private ArrayList<FriendsBookEntity> parseData(String result) {//删除群成员
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray member = new JSONArray(data.getString("member"));
            Gson gson = new Gson();
            for (int i = 0; i < member.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(member.optJSONObject(i).toString(), FriendsBookEntity.class);

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
    public void onContentClick(View view, CheckBox checkBox, int position) {
        FriendsBookEntity entity = mFriendsData.get((Integer) view.getTag());
        Intent intent = new Intent(this, EditDataActivity.class);
        intent.putExtra("UserId", entity.getId());
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, CheckBox checkBox, int position) {

    }

    @Override
    public void onCallClick(View view) {//直呼

        FriendsBookEntity item = mFriendsData.get((Integer) view.getTag());

        if (isTalking) {
            ToastUtils.showToast("通话中，请稍后重试...");
        } else {
            HomeDbHelper.appointFriendsMeeting(item.getId(), mid, "", new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        JSONObject data = new JSONObject(jsonObject.getString("data"));
                        if (result.getString("state").equals("0")) {
                           // onDialVideo(data.getString("id"), data.getString("thisUid"), data.getString("thisname"), data.getString("userId"), data.getString("username"));
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
    }

    @Override
    public void onVideoClick(View view) {

        FriendsBookEntity item = mFriendsData.get((Integer) view.getTag());
        AppointmentModel mAppointmentModel = new AppointmentModel(WholeGroupNameActivity.this);
        mAppointmentModel.init(mid, item.getId(), "");
        mAppointmentModel.doDialog("", item.getUname());
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }
}
