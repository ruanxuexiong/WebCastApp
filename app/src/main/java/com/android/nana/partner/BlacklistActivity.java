package com.android.nana.partner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.BlackAdapter;
import com.android.nana.bean.BlackEntity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.material.EditDataActivity;
import com.android.nana.widget.ListViewDecoration;
import com.google.gson.Gson;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by THINK on 2017/7/6.
 */

public class BlacklistActivity extends BaseActivity implements View.OnClickListener, BlackAdapter.OnItemClickListener {

    private ImageButton mBack;
    private TextView mTitleTv;
    private SwipeMenuRecyclerView mMenuRecyclerView;
    private String uid;
    private ArrayList<BlackEntity> mDataList = new ArrayList<>();
    private BlackAdapter mAdapter;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_black_list);
    }

    @Override
    protected void findViewById() {
        mBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTitleTv = (TextView) findViewById(R.id.common_txt_title);

        mMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        mMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
    }

    @Override
    protected void init() {
        mTitleTv.setText("黑名单");
        if (null != getIntent().getStringExtra("uid")) {
            uid = getIntent().getStringExtra("uid");
            initData(uid);
        }
    }

    private void initData(String uid) {
        WebCastDbHelper.getBackList(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mDataList = parseData(successJson);
                        mAdapter = new BlackAdapter(BlacklistActivity.this, mDataList);
                        mAdapter.setOnItemClickListener(BlacklistActivity.this);
                        mMenuRecyclerView.setAdapter(mAdapter);
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
    protected void setListener() {
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_btn_back:
                finish();
                break;
        }
    }

    public ArrayList<BlackEntity> parseData(String result) {//Gson 解析
        ArrayList<BlackEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                BlackEntity entity = gson.fromJson(data.optJSONObject(i).toString(), BlackEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getBaseContext())
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {

        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。

                BlackEntity entity = mDataList.get(adapterPosition);
                deleteBlack(entity, adapterPosition);

            }
        }
    };

    private void deleteBlack(BlackEntity entity, final int position) {//删除
        WebCastDbHelper.deleteBackList(entity.getId(), uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {

                        removeFromBlacklist(uid,position);
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

    private void removeFromBlacklist(String uid ,final int position) {
        RongIM.getInstance().removeFromBlacklist(uid, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                mDataList.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    @Override
    public void onItemClick(BlackEntity entity) {
        Intent intent = new Intent(BlacklistActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", entity.getUserInfo().getId());
        startActivity(intent);
    }
}
