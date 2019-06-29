package com.android.nana.mail;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.FriendsBookAdapter;
import com.android.nana.adapter.GroupsAdapter;
import com.android.nana.bean.GroupsEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.NetWorkUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.widget.NestedListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;

/**
 * Created by lenovo on 2017/9/18.
 */

public class GroupListActivity extends BaseActivity implements View.OnClickListener, GroupsAdapter.GroupsInterface {
    boolean flag=true;
    private TextView mTitleTv;
    private TextView mBackTv;
    private String mid;
    private EditText friends_seach;
    private NestedListView mListView;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<GroupsEntity> mDataList = new ArrayList<>();
    private ArrayList<GroupsEntity> mDataList2 = new ArrayList<>();
    private ArrayList<GroupsEntity> mDataList3 = new ArrayList<>();
    private GroupsAdapter mAdapter;
    private TextView mGroupSizeTv;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_group_list);
    }

    @Override
    protected void findViewById() {
        friends_seach=findViewById(R.id.friends_seach);
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mListView = findViewById(R.id.content_view);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我的群聊");
        mBackTv.setVisibility(View.VISIBLE);
        view = LayoutInflater.from(GroupListActivity.this).inflate(R.layout.followlist_footer, null);

        mAdapter = new GroupsAdapter(GroupListActivity.this, mDataList, this, false);
        mListView.addFooterView(view);
        mListView.setAdapter(mAdapter);
        friends_seach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()==0){
                    mDataList=mDataList3;
                    mAdapter=new GroupsAdapter(GroupListActivity.this,mDataList,GroupListActivity.this,false);
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
                        mDataList = mDataList3;
                       mDataList2.clear();

                        for (int i=0;i<mDataList.size();i++){
                            if (mDataList.get(i).getName().contains(str)){
                                mDataList2.add(mDataList.get(i));
                            }
                        }
                        mDataList=mDataList2;
                        mAdapter=new GroupsAdapter(GroupListActivity.this,mDataList,GroupListActivity.this,false);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }




                    return true;
                }

                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (NetWorkUtils.isNetworkConnected(GroupListActivity.this)) {

            if (flag){
                showProgressDialog("", "加载中...");
                mid = (String) SharedPreferencesUtils.getParameter(GroupListActivity.this, "userId", "");
                loadData(mid);
                flag=false;
            }
            else {

            }

        } else {
            mMultipleStatusView.noNetwork();
        }
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
                            mDataList3=mDataList;
                            mMultipleStatusView.dismiss();
                            mAdapter.notifyDataSetChanged();


                            mGroupSizeTv = view.findViewById(R.id.tv_num);
                            mGroupSizeTv.setText(entities.size() + "个群聊");

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
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
    public void onItemClick(View view, CheckBox mCheckBox, int position) {
        GroupsEntity entity = mDataList.get((Integer) view.getTag());
        RongIM.getInstance().startGroupChat(GroupListActivity.this, "group_" + entity.getGroupId(), entity.getName());

    }
}
