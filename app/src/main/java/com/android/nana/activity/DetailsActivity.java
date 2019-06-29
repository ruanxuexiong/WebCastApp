package com.android.nana.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.bean.DetailsEntity;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.bean.GroupEntity;
import com.android.nana.card.LocalImageHolderView;
import com.android.nana.customer.RechargeActivity;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.material.EditDataActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.DrawableTextView;
import com.android.nana.widget.StateButton;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;

import static com.android.nana.R.id.collapsingToolbar;
import static com.android.nana.R.id.tv_see;

/**
 * Created by lenovo on 2017/8/21.
 */

public class DetailsActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener, RongIM.UserInfoProvider, RongIM.GroupInfoProvider {

    private ImageView mAvatarIv, mBlurredBg;
    private StateButton mSignBtn;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private TextView mDetailsTv;
    private ArrayList<DetailsEntity.Users> mDataList = new ArrayList<>();
    private TextView mNameTv, mLaunchNameTv, mNumTv, mAddressTv, mDateTv;
    private String id, mid;
    private DetailsEntity entity;

    private LinearLayout mPwdll;
    private TextView mPwdTv;
    private View mPwdView;
    private AppBarLayout mAppBarLayout;
    private ImageView mBbackIv;
    private ImageView mRightIv;
    private Toolbar mToolbar;
    private String isHome;//判断是否从首页跳转到活动详情

    private String data;
    private AlertView mAlertView;
    private String provinceId, cityId, mPwd, mStrCost;
    private String shareUrl;
    private String strId, mLogo;//活动id
    private DrawableTextView mChatTv, mSeeTv;
    private TextView mCostTv;
    private String join;

    /*轮播*/
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

        if (!EventBus.getDefault().isRegistered(DetailsActivity.this)) {
            EventBus.getDefault().register(DetailsActivity.this);
        }

        showProgressDialog("", "加载中...");
        if (null != getIntent().getStringExtra("id")) {
            id = getIntent().getStringExtra("id");
            mid = getIntent().getStringExtra("mid");
            loadData(id, mid);
        }

        if (null != getIntent().getStringExtra("homeActivity")) {
            isHome = getIntent().getStringExtra("homeActivity");
        }

        mTransiTion = new TransitionManager(this).getSingle();
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_details);
    }

    @Override
    protected void findViewById() {
        mCollapsingToolbar = findViewById(collapsingToolbar);

        mBbackIv = findViewById(R.id.iv_back);
        mRightIv = findViewById(R.id.tv_right);

        mNameTv = findViewById(R.id.tv_name);
        mNumTv = findViewById(R.id.tv_num);
        mDateTv = findViewById(R.id.tv_date);
        mAddressTv = findViewById(R.id.tv_address);
        mAvatarIv = findViewById(R.id.iv_avatar);
        mSignBtn = findViewById(R.id.btn_sign);


        mAppBarLayout = findViewById(R.id.appBarLayout);
        mDetailsTv = findViewById(R.id.tv_activity_details);
        mLaunchNameTv = findViewById(R.id.tv_launch_name);

        mPwdll = findViewById(R.id.ll_pwd);
        mPwdTv = findViewById(R.id.tv_pwd);
        mPwdView = findViewById(R.id.view_pwd);
        mToolbar = findViewById(R.id.view_toolbar);
        mChatTv = findViewById(R.id.btn_chat);
        mCostTv = findViewById(R.id.tv_cost);
        mSeeTv = findViewById(tv_see);

        mBanner = findViewById(R.id.banner_group);
        mDentyIv = findViewById(R.id.iv_identy);
        mHheadIv = findViewById(R.id.customer_iv_head);
        mPositionTv = findViewById(R.id.tv_position);
        mUserLl = findViewById(R.id.ll_user);
    }

    @Override
    protected void init() {
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

                        join = entity.getJoin();//是否已参加

                        if (null != entity.getFounder().getStatus() && entity.getFounder().getStatus().equals("1")) {
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

                        if (entity.getIspay().equals("1")) {//是否收费
                            mCostTv.setVisibility(View.VISIBLE);
                            mCostTv.setText("费用:" + entity.getCharge() + "元");
                            mStrCost = entity.getCharge();//费用
                        } else {
                            mStrCost = null;
                        }

                        if (entity.getOrganization().equals("0")) {
                            if (entity.getJoin().equals("1")) {
                                mSignBtn.setVisibility(View.GONE);
                                mChatTv.setVisibility(View.VISIBLE);//进入聊天界面
                                RongIM.getInstance().getRongIMClient().getUnreadCount(Conversation.ConversationType.GROUP, entity.getGroupId(), new RongIMClient.ResultCallback<Integer>() {
                                    @Override
                                    public void onSuccess(Integer integer) {
                                        if (integer > 0) {
                                            mChatTv.setText("新的消息(" + integer + ")");
                                        }
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });
                            }
                        } else if (entity.getOrganization().equals("1")) {
                            mSignBtn.setVisibility(View.GONE);
                            mChatTv.setVisibility(View.VISIBLE);
                            RongIM.getInstance().getRongIMClient().getUnreadCount(Conversation.ConversationType.GROUP, entity.getGroupId(), new RongIMClient.ResultCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer integer) {
                                    if (integer > 0) {
                                        mChatTv.setText("新的消息(" + integer + ")");
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        } else {
                            if (!"".equals(entity.getPass())) {
                                mSignBtn.setVisibility(View.GONE);
                                mPwdll.setVisibility(View.VISIBLE);
                                mPwdTv.setText("群组密码：" + entity.getPass());
                                mPwd = entity.getPass();
                                mPwdView.setVisibility(View.VISIBLE);
                            } else {
                                mPwd = null;
                                mSignBtn.setVisibility(View.GONE);
                                mPwdll.setVisibility(View.GONE);

                            }
                        }

                        data = Utils.timet(entity.getDate()); //时间戳转日期
                        provinceId = entity.getProvinceId();//省份id
                        cityId = entity.getCityId();//城市id

                        shareUrl = entity.getShareUrl();//分享
                        strId = entity.getId();//活动id
                        mLogo = entity.getPicture();//图片
                        ImgLoaderManager.getInstance().showImageView(entity.getPicture(), mAvatarIv);

                        mCollapsingToolbar.setTitle(entity.getTitle());
                        mCollapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER_HORIZONTAL);
                        mCollapsingToolbar.setExpandedTitleColor(Color.parseColor("#00ffffff"));//设置还没收缩时状态下字体颜色
                        mCollapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));//设置收缩后Toolbar上字体的

                        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                            @Override
                            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                                if (state == State.EXPANDED) {
                                    mBbackIv.setImageResource(R.drawable.ic_back);
                                    mToolbar.setBackgroundColor(getResources().getColor(R.color.view_toolbar_hide));

                                    mRightIv.setVisibility(View.VISIBLE);
                                } else if (state == State.COLLAPSED) {

                                    mBbackIv.setImageResource(R.drawable.ic_back);
                                    mToolbar.setBackgroundColor(getResources().getColor(R.color.view_toolbar));

                                    mRightIv.setVisibility(View.VISIBLE);
                                } else {
                                    Log.e("中间状态", "");
                                    //中间状态

                                }
                            }
                        });
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
                ToastUtils.showToast("请求失败，请稍后重试!");
            }
        });

    }

    private void doPicture(int position) {//点击轮播图
        mTransiTion.startViewerActivity(mBanner, localImages.get(position));
    }

    @Override
    protected void setListener() {
        mBbackIv.setOnClickListener(this);
        mSignBtn.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mChatTv.setOnClickListener(this);
        mSeeTv.setOnClickListener(this);
        mUserLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                DetailsActivity.this.finish();
                break;
            case R.id.btn_sign:
                if (entity.getIspay().equals("1")) {//是否收费 （0=否    1=是）
                    DialogHelper.agreeAlert(DetailsActivity.this, "支付确认", "请确认支付参加群组的费用" + entity.getCharge() + "元", "确定", "取消", new DialogHelper.OnAlertConfirmClick() {
                        @Override
                        public void OnClick(String content) {
                        }

                        @Override
                        public void OnClick() {//确定按钮
                            showProgressDialog("", "加载中...");
                            in("");
                        }
                    }, null);
                } else if (entity.getIsEncryte().equals("1")) {
                    DialogHelper.pwdAlert(DetailsActivity.this, "输入密码", "输入群组密码", "确定", "取消", new DialogHelper.OnAlertConfirmClick() {
                        @Override
                        public void OnClick(String content) {
                            showProgressDialog("", "加载中...");
                            in(content);
                        }

                        @Override
                        public void OnClick() {
                        }
                    }, null);
                } else {
                    showProgressDialog("", "加载中...");
                    in("");
                }
                break;
            case R.id.tv_right:
                if (entity.getOrganization().equals("1")) {//是否是自己发起的群组
                    meBottomMenu();
                } else if (join.equals("1")) {//判断是否参加该群组
                    joinBottomMenu();
                } else {
                    bottomMenu();
                }
                break;
            case R.id.btn_chat://进入聊天室
                RongIM.getInstance().startGroupChat(DetailsActivity.this, entity.getGroupId(), entity.getTitle());
                break;
            case R.id.tv_see:
                if (entity.getOrganization().equals("1")) {
                    Intent intent = new Intent(DetailsActivity.this, MeMemberActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("mid", mid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DetailsActivity.this, MemberActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("mid", mid);
                    startActivity(intent);
                }
                break;
            case R.id.ll_user:
                Intent intent = new Intent(DetailsActivity.this, EditDataActivity.class);
                intent.putExtra("UserId", entity.getFounder().getId());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void joinBottomMenu() {//是否以参加
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("分享");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("退出群组");
        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);
        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                share();
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                signOut();
            }
        });
        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("分享");
        menuItem1.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                share();
            }
        });

        menuItemList.add(menuItem1);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void signOut() {//退出群组
        HomeDbHelper.quitActivity(mid, id, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("退出成功!");
                        DetailsActivity.this.finish();
                        EventBus.getDefault().post(new MessageEvent("updateData"));
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

    private void meBottomMenu() {
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
                Intent intent = new Intent(DetailsActivity.this, EditMeActivity.class);
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

        bottomMenuFragment.show(getFragmentManager(), "MeBottomMenuFragment");
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

    private void in(String content) {//立即参加

        HomeDbHelper.joinIn(mid, entity.getCuid(), entity.getId(), content, new IOAuthCallBack() {
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
                        ToastUtils.showToast(result.getString("description"));
                        loadData(id, mid);
                        EventBus.getDefault().post(new MessageEvent("updateData"));
                    } else if (result.getString("state").equals("-3")) {
                        dismissProgressDialog();
                        new AlertDialog.Builder(DetailsActivity.this).setTitle("温馨提示").setMessage("您的余额不足，请充值后再约见！")
                                .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        Intent intent = new Intent(DetailsActivity.this, RechargeActivity.class);
                                        intent.putExtra("UserId", mid);
                                        intent.putExtra("Money", entity.getCharge());
                                        intent.putExtra("IsAnchor", true);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }

                                }).show();
                    } else {
                        ToastUtils.showToast(result.getString("description"));
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
                        DetailsActivity.this.finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DetailsActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdata(MessageEvent messageEvent) {
        if (messageEvent.message != null) {
            loadData(id, mid);
        }
    }


    @Override
    public Group getGroupInfo(String groupsId) {

        if (groupsId.contains("activity")) {
            HomeDbHelper.getActivityInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        } else {
            HomeDbHelper.getGroupsInfo(groupsId, new IOAuthCallBack() {
                @Override
                public void onStartRequest() {
                }

                @Override
                public void getSuccess(String successJson) {
                    try {
                        JSONObject jsonObject = new JSONObject(successJson);
                        JSONObject result = new JSONObject(jsonObject.getString("result"));
                        if (result.getString("state").equals("0")) {
                            ArrayList<GroupEntity> item = parseGroupData(successJson);
                            for (GroupEntity entity : item) {
                                Group groupInfo = new Group(entity.getGroupId(), entity.getName(), Uri.parse(entity.getPicture()));
                                RongIM.getInstance().refreshGroupInfoCache(groupInfo);
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
        return null;
    }

    @Override
    public io.rong.imlib.model.UserInfo getUserInfo(String s) {

        HomeDbHelper.getUserName(s, mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        for (FriendsBookEntity entity : parseNmaeData(successJson)) {
                            io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(entity.getId(), entity.getUname(), Uri.parse(entity.getAvatar()));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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

        return null;
    }

    public ArrayList<GroupEntity> parseGroupData(String result) {//Gson 解析
        ArrayList<GroupEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                GroupEntity entity = gson.fromJson(data.optJSONObject(i).toString(), GroupEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<FriendsBookEntity> parseNmaeData(String result) {//Gson 解析用户头像
        ArrayList<FriendsBookEntity> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray data = new JSONArray(jsonobject.getString("data"));
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                FriendsBookEntity entity = gson.fromJson(data.optJSONObject(i).toString(), FriendsBookEntity.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
