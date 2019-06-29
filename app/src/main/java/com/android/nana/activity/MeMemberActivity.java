package com.android.nana.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.MeAdapter;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.DetailsEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.model.AppointmentModel;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.ListViewDecoration;
import com.google.gson.Gson;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by lenovo on 2017/10/12.
 * <p>
 * 自己创建的群组查看成员
 */

public class MeMemberActivity extends BaseActivity implements View.OnClickListener, MeAdapter.DetailsListener, OnItemClickListener {

    private String id, mid;
    private AppointmentModel mAppointmentModel;
    //  private DetailsAdapter mAdapter;
    private DetailsEntity entity;
    private MultipleStatusView mMultiplpView;
    private AppCompatTextView mBackTv, mTitleTv;
    private ArrayList<DetailsEntity.Users> mDataList = new ArrayList<>();
    private SwipeMenuRecyclerView mRecyclerView;
    private UserInfo mUserInfo;
    private MeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            showProgressDialog("", "正在加载...");
            loadData(id, mid);
        }
    }

    private void loadData(String id, String mid) {

        HomeDbHelper.activity(id, mid, new IOAuthCallBack() {
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
                        entity = parseData(successJson);
                        mTitleTv.setText("群组成员(" + entity.getNum() + ")");
                        if (entity.getUsers().size() > 0) {
                            for (DetailsEntity.Users item : entity.getUsers()) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                            mMultiplpView.dismiss();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mMultiplpView.empty();
                        }
                        dismissProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_member);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mRecyclerView = findViewById(R.id.recycler_view);
        mMultiplpView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mAppointmentModel = new AppointmentModel(MeMemberActivity.this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.addItemDecoration(new ListViewDecoration());

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
        mAdapter = new MeAdapter(MeMemberActivity.this, mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
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
                DetailsEntity.Users entity = mDataList.get(adapterPosition);
                if (mid.equals(entity.getId())) {
                    ToastUtils.showToast("自己不能移除自己创建的群组！");
                } else {
                    dialogDeleteUser(entity, adapterPosition);
                }
            }
        }
    };


    private void dialogDeleteUser(final DetailsEntity.Users entity, final int position) {
        DialogHelper.customAlert(MeMemberActivity.this, "提示", "确定移除该成员?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                deleteUsers(entity, position);
            }
        }, null);
    }

    private void deleteUsers(DetailsEntity.Users entity, final int position) {//移除用户
        HomeDbHelper.removeUser(mid, entity.getLinkId(), new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中...");
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mDataList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        loadData(id, mid);
                        EventBus.getDefault().post(new MessageEvent("updateData"));//更新其他数据
                        dismissProgressDialog();
                    } else {
                        dismissProgressDialog();
                        ToastUtils.showToast(result.getString("description"));
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    public DetailsEntity parseData(String result) {//Gson 解析
        DetailsEntity entity = new DetailsEntity();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), DetailsEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(Object o, int position) {

    }

    @Override
    public void onMakeClick(DetailsEntity.Users mItem) {
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(MeMemberActivity.this, "userInfo", UserInfo.class);

        mAppointmentModel.init(mUserInfo.getId(), mItem.getId(), mUserInfo.getPayPassword());
        mAppointmentModel.doDialog("", mItem.getUsername());
    }

    @Override
    public void onContentClick(DetailsEntity.Users mItem) {
        Intent intent = new Intent(MeMemberActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mItem.getId());
        startActivity(intent);
    }

    @Override
    public void onCallClick(DetailsEntity.Users mItem) {

        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", "PRIVATE");
        intent.putExtra("targetId", mItem.getId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(MeMemberActivity.this.getPackageName());
        startActivity(intent);
    }
}
