package com.android.nana.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.adapter.MeAdapter;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.DetailsEntity;
import com.android.nana.bean.UserInfo;
import com.android.nana.card.LocalImageHolderView;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.material.EditDataActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.DrawableTextView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

import static com.android.nana.R.id.collapsingToolbar;

/**
 * Created by lenovo on 2017/8/23.
 */

public class MeActivity extends BaseActivity implements View.OnClickListener, MeAdapter.DetailsListener, OnItemClickListener {

    private ImageView mAvatarIv;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private TextView mDetailsTv;
    private ArrayList<DetailsEntity.Users> mDataList = new ArrayList<>();
    private TextView mNameTv, mLaunchNameTv, mNumTv, mAddressTv, mDateTv;
    private UserInfo mUserInfo;
    private String id, mid;
    private DetailsEntity entity;
    private LinearLayout mPwdLL;
    private TextView mPwdTv;
    private AppBarLayout mAppBarLayout;
    private ImageView mBbackIv, mRightIv;
    private AlertView mAlertView;
    private Toolbar mToolbar;

    private SwipeMenuRecyclerView mRecyclerView;
    private String data;
    private String shareUrl;
    private String strId, mLogo;//活动id
    private String provinceId, cityId, mPwd, strCost;
    private TextView mCostTv;
    private DrawableTextView mChatBtnTv;
    private DrawableTextView mSeeBtnTv;

    private ConvenientBanner mBanner;
    private TransitionSingleHelper mTransiTion;
    private RoundImageView mHheadIv;
    private ImageView mDentyIv;
    private TextView mPositionTv;
    private LinearLayout mUserLl;
    private ArrayList<String> localImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(MeActivity.this)) {
            EventBus.getDefault().register(MeActivity.this);
        }

        mTransiTion = new TransitionManager(this).getSingle();

        showProgressDialog("", "加载中...");
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
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


                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        entity = parseData(successJson);

                        localImages.clear();
                        for (int i = 0; i < entity.getPics().size(); i++) {
                            localImages.add(entity.getPics().get(i).getPicture());
                        }

                        mBanner.setPages(
                                new CBViewHolderCreator<LocalImageHolderView>() {
                                    @Override
                                    public LocalImageHolderView createHolder() {
                                        return new LocalImageHolderView();
                                    }
                                }, localImages)
                                .startTurning(4000)
                                .setPageIndicator(new int[]{R.drawable.icon_slide_no, R.drawable.icon_slide_yes}).setOnItemClickListener(new com.bigkoo.convenientbanner.listener.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                doPicture(position);
                            }
                        });


                        mNameTv.setText(entity.getTitle());
                        mNumTv.setText(entity.getNum() + "人已参加");
                        mLaunchNameTv.setText(entity.getFounder().getUname());
                        mDateTv.setText(entity.getMonth() + " " + entity.getDd());
                        mAddressTv.setText(entity.getProvince() + "·" + entity.getCity());
                        mDetailsTv.setText(entity.getIntroduce());

                        if (entity.getIspay().equals("1")) {
                            mCostTv.setVisibility(View.VISIBLE);
                            strCost = entity.getCharge();
                            mCostTv.setText("费用:" + entity.getCharge() + "元");
                        }

                        if (entity.getFounder().getStatus().equals("1")) {
                            mDentyIv.setVisibility(View.VISIBLE);
                        }
                        ImageLoader.getInstance().displayImage(entity.getFounder().getAvatar(), mHheadIv);

                        if (!"".equals(entity.getFounder().getCompany()) && !"".equals(entity.getFounder().getPosition()) && null != entity.getFounder().getCompany() && null != entity.getFounder().getPosition()) {
                            mPositionTv.setText(entity.getFounder().getPosition() + " | " + entity.getFounder().getCompany());
                        } else if (null != entity.getFounder().getCompany() && !"".equals(entity.getFounder().getCompany())) {
                            mPositionTv.setText(entity.getFounder().getCompany());
                        } else if (!"".equals(entity.getFounder().getPosition()) && null != entity.getFounder().getPosition()) {
                            mPositionTv.setText(entity.getFounder().getPosition());
                        }

                        data = Utils.timet(entity.getDate()); //时间戳转日期
                        provinceId = entity.getProvinceId();//省份id
                        cityId = entity.getCityId();//城市id
                        shareUrl = entity.getShareUrl();//分享
                        strId = entity.getId();//活动id

                        mLogo = entity.getPicture();//图片
                        ImgLoaderManager.getInstance().showImageView(entity.getFounder().getAvatar(), mAvatarIv);

                        if (!"".equals(entity.getPass())) {
                            mPwdTv.setText("群组密码：" + entity.getPass());
                            mPwd = entity.getPass();
                            mPwdLL.setVisibility(View.VISIBLE);
                        } else {
                            mPwd = null;
                            mPwdLL.setVisibility(View.GONE);
                        }

                        mCollapsingToolbar.setTitle(entity.getTitle());
                        mCollapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER);
                        mCollapsingToolbar.setExpandedTitleColor(Color.parseColor("#00ffffff"));//设置还没收缩时状态下字体颜色
                        mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));//设置收缩后Toolbar上字体的

                        if (entity.getUsers().size() > 0) {
                            mDataList.clear();
                            for (DetailsEntity.Users item : entity.getUsers()) {
                                if (!mDataList.contains(item)) {
                                    mDataList.add(item);
                                }
                            }
                        }

                    }
                    dismissProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgressDialog();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试!");
            }
        });
    }


    private void doPicture(int position) {//点击轮播图
        mTransiTion.startViewerActivity(mBanner, localImages.get(position));
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_new_me);
    }

    @Override
    protected void findViewById() {
        mCollapsingToolbar = findViewById(collapsingToolbar);

        mNameTv = findViewById(R.id.tv_name);
        mNumTv = findViewById(R.id.tv_num);
        mDateTv = findViewById(R.id.tv_date);
        mAddressTv = findViewById(R.id.tv_address);
        mAvatarIv = findViewById(R.id.customer_iv_head);

        mDetailsTv = findViewById(R.id.tv_activity_details);
        mLaunchNameTv = findViewById(R.id.tv_launch_name);

        mPwdLL = findViewById(R.id.ll_pwd);

        mPwdTv = findViewById(R.id.tv_pwd);

        mAppBarLayout = findViewById(R.id.appBarLayout);
        mBbackIv = findViewById(R.id.iv_back);
        mRightIv = findViewById(R.id.tv_right);

        mToolbar = findViewById(R.id.view_toolbar);
        mCostTv = findViewById(R.id.tv_cost);

        mChatBtnTv = findViewById(R.id.btn_chat);
        mSeeBtnTv = findViewById(R.id.tv_see);
        mBanner = findViewById(R.id.banner_group);
        mDentyIv = findViewById(R.id.iv_identy);
        mHheadIv = findViewById(R.id.customer_iv_head);
        mPositionTv = findViewById(R.id.tv_position);
        mUserLl = findViewById(R.id.ll_user);

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


    @Override
    protected void init() {
    }

    @Override
    protected void setListener() {
        mBbackIv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("STATE", state.name());
                if (state == State.EXPANDED) {

                    //展开状态
                    Log.e("展开状态", "");
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.view_toolbar_hide));
                    mBbackIv.setImageResource(R.drawable.ic_back);
                    mRightIv.setImageResource(R.drawable.icon_more_unselect);
                    mRightIv.setVisibility(View.VISIBLE);
                } else if (state == State.COLLAPSED) {
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.view_toolbar));
                    mBbackIv.setImageResource(R.drawable.ic_back);
                    mRightIv.setImageResource(R.drawable.icon_more_unselect);

                } else {
                }
            }
        });
        mChatBtnTv.setOnClickListener(this);
        mSeeBtnTv.setOnClickListener(this);
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
            case R.id.iv_back:
                MeActivity.this.finish();
                break;
            case R.id.tv_right:
                bottomMenu();
                break;
            case R.id.btn_chat:
                RongIM.getInstance().startGroupChat(MeActivity.this, entity.getGroupId(), entity.getTitle());
                break;
            case R.id.tv_see:
                Intent intent = new Intent(MeActivity.this, MeMemberActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("mid", mid);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("编辑");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("解散群组");
        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setText("分享");
        menuItem3.setStyle(MenuItem.MenuItemStyle.COMMON);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                //编辑群组
                Intent intent = new Intent(MeActivity.this, EditMeActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("mid", mid);
                startActivity(intent);
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                showDialogs();
            }
        });


        menuItem3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem3) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                share();
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem3);
        menuItemList.add(menuItem2);

        bottomMenuFragment.setMenuItems(menuItemList);

        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void share() {//分享
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance(shareUrl + "activityId=" + strId + "&mid=" + mid, mNameTv.getText().toString(), mDetailsTv.getText().toString(), mLogo);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    private void showDialogs() {
        mAlertView = new AlertView("提示", "确定解散群组吗？", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, this).setCancelable(true);
        mAlertView.show();
    }

    @Override
    public void onMakeClick(DetailsEntity.Users mItem) {
        mUserInfo = (UserInfo) SharedPreferencesUtils.getObject(MeActivity.this, "userInfo", UserInfo.class);
    }

    @Override
    public void onContentClick(DetailsEntity.Users mItem) {
        Intent intent = new Intent(MeActivity.this, EditDataActivity.class);
        intent.putExtra("UserId", mItem.getId());
        startActivity(intent);
    }


    @Override
    public void onCallClick(DetailsEntity.Users mItem) {
        String mid = (String) SharedPreferencesUtils.getParameter(MeActivity.this, "userId", "");


     /*   if (isTalking) {
            ToastUtils.showToast("通话中，请稍后重试...");
        } else {
            HomeDbHelper.appointFriendsMeeting(mItem.getId(), mid, "", new IOAuthCallBack() {
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
                            onDialVideo(data.getString("id"), data.getString("thisUid"), data.getString("thisname"), data.getString("userId"), data.getString("username"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getFailue(String failueJson) {

                }
            });
        }*/
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0) {
            delete();
        }
    }

    private void delete() {//删除群组
        HomeDbHelper.deleteGroup(id, mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
                showProgressDialog("", "加载中...");
            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonobject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonobject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("解散成功!");
                        dismissProgressDialog();
                        MeActivity.this.finish();
                        EventBus.getDefault().post(new MessageEvent("updateData"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
                ToastUtils.showToast("请求失败，请稍后重试！");
            }
        });
    }

    private void dialogDeleteUser(final DetailsEntity.Users entity, final int position) {
        DialogHelper.customAlert(MeActivity.this, "提示", "确定移除该成员?", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {

            }

            @Override
            public void OnClick() {
                deleteUsers(entity, position);
            }
        }, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MeActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(MessageEvent messageEvent) {
        if (messageEvent.message != null) {
            loadData(id, mid);
        }
    }

}
