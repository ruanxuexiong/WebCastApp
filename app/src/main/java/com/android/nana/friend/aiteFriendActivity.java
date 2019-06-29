package com.android.nana.friend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.FriendsBookAdapter;
import com.android.nana.adapter.ServiceBookAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.bean.OthreEntity;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.AddFriendEvent;
import com.android.nana.main.MailFragment;
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

public class aiteFriendActivity extends AppCompatActivity implements  View.OnClickListener, SideBar.OnTouchingLetterChangedListener, FriendsBookAdapter.SortListener, ServiceBookAdapter.ServiceListener{
    private String state="";
    private AppCompatTextView iv_toolbar_back;
    private SideBar mSideBar;
    private String mid="";
    private ArrayList<FriendsBookEntity> mFriendsData = new ArrayList<>();
    private ArrayList<FriendsBookEntity> mServiceData = new ArrayList<>();
  //  private TextView   mFansNumTv;
    private String mFansNum = "";
    private CharacterParser mParser;
    private LinearLayout mListViewLayout;
    private FriendsBookAdapter mAdapter;
    private ServiceBookAdapter mServiceAdapter;
    private PinyinComparatorFriend mPinyin;
    private ListView mListView;
    private EditText friends_seach;
    private boolean isNumTrue = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aite_friend);
        iv_toolbar_back=findViewById(R.id.iv_toolbar_back);
        mListView = (ListView) findViewById(R.id.lv_follow);
        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mListViewLayout = findViewById(R.id.layout_list_view);
        friends_seach=findViewById(R.id.friends_seach);
        //mNumTv = findViewById(R.id.tv_msg_num);
       // mFansNumTv = findViewById(R.id.tv_fans_num);

        mSideBar.setOnTouchingLetterChangedListener(this);
        iv_toolbar_back.setVisibility(View.VISIBLE);
        iv_toolbar_back.setText("取消");
        iv_toolbar_back.setTextColor(0xffffff);
        iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    private void init(){
        Intent intent=getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("state"))){
            state= intent.getStringExtra("state");
        }

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparatorFriend();
        mid = (String) SharedPreferencesUtils.getParameter(aiteFriendActivity.this, "userId", "");
        friends_seach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    if (editable.length()==0){
                        getdata();
                    }
            }
        });
        getdata();

    }

    private void getdata(){
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
//                        if (mServiceData.size() > 0) {
//                            mListViewLayout.removeAllViews();
//                            View mView = LayoutInflater.from(aiteFriendActivity.this).inflate(R.layout.mail_list_view, null);
//                            ListView mListView = mView.findViewById(R.id.lv_follow);
//                            mServiceAdapter = new ServiceBookAdapter(aiteFriendActivity.this, mServiceData, aiteFriendActivity.this);
//                            mListView.setAdapter(mServiceAdapter);
//                            setListViewHeightBasedOnChildren(mListView);
//                            mListViewLayout.addView(mView);
//                            mServiceAdapter.notifyDataSetChanged();
//                        }

                       // if (mFriendsData.size() > 0) {
                            Collections.sort(mFriendsData, mPinyin);
                            mAdapter = new FriendsBookAdapter(aiteFriendActivity.this, mFriendsData, aiteFriendActivity.this);
                            mAdapter.notifyDataSetChanged();
                     //   }
                        mListView.setAdapter(mAdapter);


//                        String num = result.getString("description");
//                        if (!num.equals("0")) {
//                            mNumTv.setVisibility(View.VISIBLE);
//                            mNumTv.setText(num);
//                            isNumTrue = true;
//                            EventBus.getDefault().post(new AddFriendEvent());//更新通讯录数量
//                        } else {
//                            mNumTv.setVisibility(View.GONE);
//                            if (isNumTrue) {
//                                EventBus.getDefault().post(new AddFriendEvent());//更新通讯录数量
//                            }
//                        }
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


    private ArrayList<FriendsBookEntity> parseServiceData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray friends = new JSONArray(data.getString("user"));

            Gson gson = new Gson();
            for (int i = 0; i < friends.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(friends.optJSONObject(i).toString(), FriendsBookEntity.class);

                String pinyin = mParser.getSelling(entity.getUsername());
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

    private ArrayList<FriendsBookEntity> parseData(String result) {//Gson 解析
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray friends = new JSONArray(data.getString("friends"));

            if (Integer.valueOf(data.getString("follownum")).intValue() > 0) {
              //  mFansNumTv.setVisibility(View.GONE);
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
        if (state.equals("1")){
            Intent intent = new Intent(aiteFriendActivity.this, VideoDynamicActivity.class);
            intent.putExtra("friendid", mFriendsData.get(position).getId()+" ");
            intent.putExtra("friendname", "@"+mFriendsData.get(position).getUsername()+" ");
            setResult(003, intent);
            finish();
        }
        else {
            Intent intent = new Intent(aiteFriendActivity.this, VideoDynamicActivity.class);
            intent.putExtra("friendid", mFriendsData.get(position).getId()+" ");
            intent.putExtra("friendname", mFriendsData.get(position).getUsername()+" ");
            setResult(003, intent);
            finish();
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
    public void onServiceItemClick(View view, int position) {

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
    public void onClick(View view) {

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }

    private void getKeyword(String keyword,String mUserId){
        FunctionDbHelper.searchUserbyKeyword(keyword, mUserId, "", "", "", "", 1, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
             //   mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mFriendsData=parseServiceData(successJson);
                        Collections.sort(mFriendsData, mPinyin);
                        mAdapter = new FriendsBookAdapter(aiteFriendActivity.this, mFriendsData, aiteFriendActivity.this);
                        mAdapter.notifyDataSetChanged();
                        //   }
                        mListView.setAdapter(mAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                //    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {
             //   mMultipleStatusView.dismiss();
            }
        });


    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(aiteFriendActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            getKeyword(friends_seach.getText().toString(),mid);

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
