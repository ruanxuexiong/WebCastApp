package com.android.nana.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.OverrideEditText;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/13.
 */

public class FriendSearchActivity extends BaseActivity implements View.OnClickListener, FriendSearchAdapter.FriendSearchListener {

    private ImageView mCloseIv;
    private OverrideEditText mSearchEt;
    private String keyword, mid;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<FriendSearchEntity.Moments> mDataList = new ArrayList<>();
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private FriendSearchAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mid = (String) SharedPreferencesUtils.getParameter(FriendSearchActivity.this, "userId", "");
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_friend_search);
    }

    @Override
    protected void findViewById() {
        mCloseIv = findViewById(R.id.iv_close);
        mSearchEt = findViewById(R.id.et_search);
        mListView = findViewById(R.id.view_recycler);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mAdapter = new FriendSearchAdapter(this, mDataList, this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        mCloseIv.setOnClickListener(this);
        mSearchEt.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                mSearchEt.setText("");
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
                    if (!"".equals(keyword)) {
                        mDataList.clear();
                        mMultipleStatusView.loading();
                        mAdapter.setKeyword(keyword);
                        query(keyword);
                    } else {
                        ToastUtils.showToast("请输入搜索内容！");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void query(String keyword) {//搜索

        FriendDbHelper.searchMoments(mid, keyword, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                mMultipleStatusView.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseData(successJson).size() > 0) {
                            mMultipleStatusView.dismiss();
                            for (FriendSearchEntity.Moments item : parseData(successJson)) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultipleStatusView.empty();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMultipleStatusView.dismiss();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                this.finish();
                break;
            default:
                break;
        }
    }

    private ArrayList<FriendSearchEntity.Moments> parseData(String result) {
        ArrayList<FriendSearchEntity.Moments> item = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray user = new JSONArray(data.getString("moments"));
            if (user.length() > 0) {
                Gson gson = new Gson();
                for (int i = 0; i < user.length(); i++) {
                    FriendSearchEntity.Moments bean = gson.fromJson(user.optJSONObject(i).toString(), FriendSearchEntity.Moments.class);
                    item.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public void onPlayView(String path) {//播放视频
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        PictureSelector.create(FriendSearchActivity.this).externalPictureVideo(path);
    }

    @Override
    public void onItemClick(View view) {
        FriendSearchEntity.Moments item = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(FriendSearchActivity.this, AlbumDetailsActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("mid", mid);
        startActivity(intent);
    }
}
