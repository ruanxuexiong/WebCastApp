package com.android.nana.mail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.FriendsBookAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.GroupEvent;
import com.android.nana.eventBus.SelectEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.HorizontalListView;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinComparatorFriend;
import com.android.nana.widget.SideBar;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lenovo on 2017/9/18.
 * 创建群选择好友
 */

public class SelectFriendsActivity extends BaseActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, FriendsBookAdapter.SortListener {
    EditText friends_seach;
    private List<String> listimg=new ArrayList<>();
    Seachadapter seachadapter;
    private HorizontalListView Hlistview;
    private String mid;
    private TextView mTitleTv;
    private TextView mBackTv;

    private MultipleStatusView mMultipleStatusView;
    private CharacterParser mParser;
    private PinyinComparatorFriend mPinyin;
    private FriendsBookAdapter mAdapter;
    private ListView mListView;
    private SideBar mSideBar;
    private TextView mDialogTv;
    private ArrayList<FriendsBookEntity> mFriendsData3 = new ArrayList<>();
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();
    private ArrayList<FriendsBookEntity> mFriendsData2 = new ArrayList<>();
    private TextView mRight2;
    private int num = 0;//记录选中条数
    private ArrayList<String> mArrayUid = new ArrayList<>();//存储选中的用户id

    private boolean isAddGroupMember;
    private boolean isDeleteGroupMember;
    private String sGroupId;
    private String mGroupUids = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mid = (String) SharedPreferencesUtils.getParameter(SelectFriendsActivity.this, "userId", "");

        isAddGroupMember = getIntent().getBooleanExtra("isAddGroupMember", false);
        isDeleteGroupMember = getIntent().getBooleanExtra("isDeleteGroupMember", false);

        if (isAddGroupMember) {
            mTitleTv.setText("邀请联系人");
            if (NetWorkUtils.isNetworkConnected(SelectFriendsActivity.this)) {
                showProgressDialog("", "加载中...");
                sGroupId = getIntent().getStringExtra("GroupId");
                loadAddData();
            } else {
                mMultipleStatusView.noNetwork();
            }
        } else if (isDeleteGroupMember) {
            mTitleTv.setText("删除成员");

            if (NetWorkUtils.isNetworkConnected(SelectFriendsActivity.this)) {
                showProgressDialog("", "加载中...");
                sGroupId = getIntent().getStringExtra("GroupId");
                loadDetData();
            } else {
                mMultipleStatusView.noNetwork();
            }
        } else {
            mTitleTv.setText("选择联系人");
            if (NetWorkUtils.isNetworkConnected(SelectFriendsActivity.this)) {
                showProgressDialog("", "加载中...");
                loadData();
            } else {
                mMultipleStatusView.noNetwork();
            }
        }

        if (!EventBus.getDefault().isRegistered(SelectFriendsActivity.this)) {
            EventBus.getDefault().register(SelectFriendsActivity.this);
        }
    }

    private void loadDetData() {//删除成员列表
        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();

        HomeDbHelper.quitMember(sGroupId, "", new IOAuthCallBack() {
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
                        mFriendsData = parseDelData(successJson);
                        if (mFriendsData.size() > 0) {
                            Collections.sort(mFriendsData, mPinyin);
                            mAdapter = new FriendsBookAdapter(SelectFriendsActivity.this, mFriendsData, SelectFriendsActivity.this, true);
                            mListView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultipleStatusView.empty();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });

    }

    private void loadAddData() {//添加邀请联系人接口

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();

        HomeDbHelper.selectMember(mid, sGroupId, "", new IOAuthCallBack() {
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
                        mFriendsData = parseDelData(successJson);
                        if (mFriendsData.size() > 0) {
                            Collections.sort(mFriendsData, mPinyin);
                            mAdapter = new FriendsBookAdapter(SelectFriendsActivity.this, mFriendsData, SelectFriendsActivity.this, true);
                            mListView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultipleStatusView.empty();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_select_friends);
    }

    @Override
    protected void findViewById() {
        friends_seach=findViewById(R.id.friends_seach);
        Hlistview=findViewById(R.id.Hlistview);
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mListView = findViewById(R.id.lv_follow);
        mSideBar = findViewById(R.id.sidrbar);
        mDialogTv = findViewById(R.id.tv_dialog);

        mRight2 = findViewById(R.id.toolbar_right_2);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        friends_seach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()==0){
                    mFriendsData=mFriendsData3;
                    mAdapter=new FriendsBookAdapter(SelectFriendsActivity.this,mFriendsData,SelectFriendsActivity.this,true);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        friends_seach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ii, KeyEvent keyEvent) {
                /*判断是否是“搜索”键*/

                if(ii == EditorInfo.IME_ACTION_SEARCH){



                    String str=friends_seach.getText().toString();


                    if (str.length()==0){

                    }
                    else {

                        if (mFriendsData2.size()!=0){
                            mFriendsData2.clear();
                            mFriendsData = mFriendsData3;

                        }
                        else {
                            mFriendsData2.clear();
                            mFriendsData3=mFriendsData;
                        }

                        /*隐藏软键盘*/
                        InputMethodManager imm = (InputMethodManager) textView
                                .getContext().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);

                        // showToast("哈哈哈哈");
                        for (int i=0;i<mFriendsData.size();i++){
                            if (mFriendsData.get(i).getUsername().contains(str)){
                                mFriendsData2.add(mFriendsData.get(i));
                            }
                        }
                        //   mAdapter.clear();
                        mFriendsData=mFriendsData2;
                        mAdapter=new FriendsBookAdapter(SelectFriendsActivity.this,mFriendsData,SelectFriendsActivity.this,true);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                    }


                    //mFriendsData=mFriendsData3;
                    //   mFriendsData.clear();
                    //mFriendsData=mFriendsData2;
                    //mAdapter.clear();
                    //mAdapter.notifyDataSetChanged();


                    return true;
                }

                return false;
            }
        });
        seachadapter=new Seachadapter();
        Hlistview.setAdapter(seachadapter);
        Hlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                for (int i=0;i<mFriendsData.size();i++){
                    if (mFriendsData.get(i).getAvatar().equals(listimg.get(position))){

                        mFriendsData.get(i).setChcked(false);
                        num--;
                        if (num == 0) {
                            mRight2.setText("确定");
                        } else {
                            mRight2.setText("确定(" + num + ")");
                        }

                        if (mArrayUid.size() > 0) {
                            mArrayUid.remove(mFriendsData.get(i).getId());
                        }
                        listimg.remove(position);
                        seachadapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }



            }
        });

        mBackTv.setVisibility(View.VISIBLE);
        mSideBar.setTextView(mDialogTv);
        mRight2.setVisibility(View.VISIBLE);
        mRight2.setCompoundDrawables(null, null, null, null);
        mRight2.setTextColor(getResources().getColor(R.color.white));
        mRight2.setText("确定");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadData() {

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();

        HomeDbHelper.friendsBook(mid, new IOAuthCallBack() {
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
                        mFriendsData = parseData(successJson);
                        mFriendsData3=parseData(successJson);
                        Collections.sort(mFriendsData, mPinyin);
                        mAdapter = new FriendsBookAdapter(SelectFriendsActivity.this, mFriendsData, SelectFriendsActivity.this, true);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }

            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                if (isDeleteGroupMember) {
                    delUsers();
                } else if (isAddGroupMember) {
                    addGroupUsers();
                } else {
                    addUsers();
                }
                break;
        }
    }

    private void addGroupUsers() {//添加联系人到群组

        for (int i = 0; i < mArrayUid.size(); i++) {
            if (mArrayUid.size() > 1) {
                mGroupUids += mArrayUid.get(i) + ",";
            } else {
                mGroupUids = mArrayUid.get(i);
            }
        }
        if (!"".equals(mGroupUids)) {


            HomeDbHelper.joinGroup(mGroupUids, sGroupId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ToastUtils.showToast("添加成功");
                            SelectFriendsActivity.this.finish();
                            EventBus.getDefault().post(new GroupEvent());
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
            ToastUtils.showToast("至少选择一位好友");
        }
    }

    private void delUsers() {//移除成员
        for (int i = 0; i < mArrayUid.size(); i++) {
            if (mArrayUid.size() > 1) {
                mGroupUids += mArrayUid.get(i) + ",";
            } else {
                mGroupUids = mArrayUid.get(i);
            }
        }

        if (!"".equals(mGroupUids)) {
            HomeDbHelper.quitGroup(mid, sGroupId, mGroupUids, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {

                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ToastUtils.showToast("移除成功");
                            SelectFriendsActivity.this.finish();
                            EventBus.getDefault().post(new GroupEvent());
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
            ToastUtils.showToast("至少选择一位好友");
        }
    }

    private void addUsers() {
        if (mArrayUid.size() > 0) {
            Intent intent = new Intent(SelectFriendsActivity.this, CreateGroupActivity.class);
            intent.putStringArrayListExtra("mUids", mArrayUid);
            intent.putExtra("mid", mid);
            startActivity(intent);
        } else {
            ToastUtils.showToast("至少选择一位好友");
        }
    }


    private ArrayList<FriendsBookEntity> parseDelData(String result) {//删除群成员
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

    private ArrayList<FriendsBookEntity> parseData(String result) {//普通选择
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
            Log.e("fannan", e.getMessage());
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
           // showToast("减");
            for (int i=0;i<listimg.size();i++){
                if (listimg.get(i).equals(mFriendsData.get(position).getAvatar())){
                    listimg.remove(i);
                    seachadapter.notifyDataSetChanged();
                }
            }
          //  listimg.add(mFriendsData.get(position).getAvatar());
           // listimg.remove(entity.getId());

            checkBox.setChecked(false);
            mFriendsData.get(position).setChcked(false);
            num--;
            if (num == 0) {
                mRight2.setText("确定");
            } else {
                mRight2.setText("确定(" + num + ")");
            }

            if (mArrayUid.size() > 0) {
                mArrayUid.remove(entity.getId());
            }
        } else {
        //    showToast("加");
            listimg.add(mFriendsData.get(position).getAvatar());
               seachadapter.notifyDataSetChanged();
            mArrayUid.add(entity.getId());
            checkBox.setChecked(true);
            mFriendsData.get(position).setChcked(true);
            num++;
            mRight2.setText("确定(" + num + ")");
        }
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
        EventBus.getDefault().unregister(SelectFriendsActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onFinish(SelectEvent selectEvent) {
        this.finish();
    }


    public class Seachadapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (listimg==null){
                return 0;
            }
            else {
                return listimg.size();
            }

        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view =LayoutInflater.from(SelectFriendsActivity.this).inflate(R.layout.seach_list_,null);
            ImageView heard_img=view.findViewById(R.id.heard_img);
            Glide.with(SelectFriendsActivity.this).load(listimg.get(i).toString()).into(heard_img);
            return view;
        }
    }


}
