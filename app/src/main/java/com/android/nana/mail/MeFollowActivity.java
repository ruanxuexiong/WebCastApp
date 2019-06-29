package com.android.nana.mail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.SortAdapter;
import com.android.nana.bean.SortEntity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinComparator;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by lenovo on 2017/9/9.
 */

public class MeFollowActivity extends BaseActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, SortAdapter.SortListener {


    private String mid;
    private TextView mTitleTv, mBackTv, mDialogTv;

    private ListView mListView;
    private SideBar mSideBar;
    private SortAdapter mAdapter;
    private LinearLayout mSearchRl;

    private CharacterParser mParser;
    private PinyinComparator mPinyin;
    private ArrayList<SortEntity> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("mid")) {
            mid = getIntent().getStringExtra("mid");
            showProgressDialog("", "加载中...");
            initData();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.me_follow_activity);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mListView = findViewById(R.id.lv_follow);
        mSideBar = findViewById(R.id.sidrbar);
        mDialogTv = findViewById(R.id.tv_dialog);
        mSearchRl = findViewById(R.id.rl_search);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我的关注");
        mBackTv.setVisibility(View.VISIBLE);
        mSideBar.setTextView(mDialogTv);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSearchRl.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);

    /*    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtils.showToast("删除");
                return false;
            }
        });*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.rl_search:
                Intent intentSearch = new Intent(MeFollowActivity.this, SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
        }
    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                ToastUtils.showToast("点击了");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void initData() {//初始化数据
        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinComparator();
        HomeDbHelper.followers(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        dismissProgressDialog();
                        mDataList = parseData(successJson);
                        Collections.sort(mDataList, mPinyin);
                        mAdapter = new SortAdapter(MeFollowActivity.this, mDataList, MeFollowActivity.this);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        View view = LayoutInflater.from(MeFollowActivity.this).inflate(R.layout.followlist_footer,null);
                        TextView mNumTv = view.findViewById(R.id.tv_num);

                        if (mDataList.size()>0){
                            mNumTv.setText("共关注"+mDataList.size()+"位");
                            mListView.addFooterView(view);
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


    private ArrayList<SortEntity> parseData(String result) {//Gson 解析
        ArrayList<SortEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                SortEntity entity = gson.fromJson(data.optJSONObject(i).toString(), SortEntity.class);

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
    public void onContentClick(View view) {
        SortEntity entity = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(MeFollowActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", entity.getUserId());
        startActivity(intent);
    }
}
