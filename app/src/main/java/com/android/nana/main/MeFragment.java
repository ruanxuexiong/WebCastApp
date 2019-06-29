package com.android.nana.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.helper.DialogHelper;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.customer.SystemSetupActivity;
import com.android.nana.customer.myincome.WalletActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.PushExtraEvent;
import com.android.nana.find.NewFindActivity;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.friend.CollectionActvity;
import com.android.nana.friend.MeAlbumActivity;
import com.android.nana.friend.MeCollectionActivity;
import com.android.nana.friend.WholeAlbumActivity;
import com.android.nana.identity.identity_homeActivity;
import com.android.nana.inquiry.SetMoneyActivity;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.MyDataActivity;
import com.android.nana.partner.PartnerBaseActivity;
import com.android.nana.pattern.SanPatternSetActivity;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.suke.widget.SwitchButton;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by lenovo on 2017/11/16.
 */

public class MeFragment extends BaseRequestFragment implements View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    private RoundedImageView mRivHead;
    private ImageView mIvIdenty;
    private TextView mTxtName, mTxtInfo, mTitleTv;
    private TextView mNumTv;

    private String uid, state;
    private String money;//见面金额
    private FrameLayout mHeadfl;
    private TextView mStateTv;
    private LinearLayout mWalletLl, mSanLl, mIdentityll, mAlbumLl, mPlanLl, mSetLl, mCollectionLl, mSwitchll, mSupplierLl, mShoppingLl;
    private SwitchButton mSwitchBtn;
    private String openFace;
    private String isRecommend;
    //开启模式
    private String mType = "0";//是否设置收费金额
    private LinearLayout mSetMoneyLl;
    private TextView mMoneyTv;
    private TextView mCountTv;
    private HtmlBean bean;
    private LinearLayout mLocationLl;
    private LinearLayout mCoopLl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MeFragment.this)) {
            EventBus.getDefault().register(MeFragment.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me_new;
    }

    @Override
    protected void findViewById() {
        mNumTv = (TextView) findViewById(R.id.tv_me_num);
        mRivHead = (RoundedImageView) findViewById(R.id.customer_iv_head);
        mIvIdenty = (ImageView) findViewById(R.id.customer_iv_identy);
        mTxtName = (TextView) findViewById(R.id.customer_txt_name);
        mTxtInfo = (TextView) findViewById(R.id.customer_txt_info);

        mAlbumLl = (LinearLayout) findViewById(R.id.ll_album);
        mPlanLl = (LinearLayout) findViewById(R.id.ll_plan);
        mSetLl = (LinearLayout) findViewById(R.id.ll_set);
        mSanLl = (LinearLayout) findViewById(R.id.ll_san);
        mTitleTv = (TextView) findViewById(R.id.tv_title);

        mStateTv = (TextView) findViewById(R.id.state);
        mHeadfl = (FrameLayout) findViewById(R.id.view_head);
        mWalletLl = (LinearLayout) findViewById(R.id.ll_wallet);
        mShoppingLl = (LinearLayout) findViewById(R.id.ll_shopping);
        mIdentityll = (LinearLayout) findViewById(R.id.ll_identity);
        mCollectionLl = (LinearLayout) findViewById(R.id.ll_collection);

        mSwitchll = (LinearLayout) findViewById(R.id.ll_switch);
        mSwitchBtn = (SwitchButton) findViewById(R.id.switch_button);
        mSetMoneyLl = (LinearLayout) findViewById(R.id.ll_set_money);
        mMoneyTv = (TextView) findViewById(R.id.tv_money_text);
        mCountTv = (TextView) findViewById(R.id.tv_count);
        mSupplierLl = (LinearLayout) findViewById(R.id.ll_supplier);

        mLocationLl = (LinearLayout) findViewById(R.id.ll_location);
        mCoopLl = (LinearLayout) findViewById(R.id.ll_coop);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我");
    }

    @Override
    protected void setListener() {
        mWalletLl.setOnClickListener(this);
        mSanLl.setOnClickListener(this);
        mAlbumLl.setOnClickListener(this);
        mPlanLl.setOnClickListener(this);
        mSetLl.setOnClickListener(this);
        mRivHead.setOnClickListener(this);
        mHeadfl.setOnClickListener(this);
        mIdentityll.setOnClickListener(this);

        mCollectionLl.setOnClickListener(this);
        mSwitchBtn.setOnCheckedChangeListener(this);
        mSetMoneyLl.setOnClickListener(this);
        mSupplierLl.setOnClickListener(this);
        mLocationLl.setOnClickListener(this);
        mCoopLl.setOnClickListener(this);
        mShoppingLl.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        uid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        initData();
        initHtml5();
    }

    private void initData() {
        WebCastDbHelper.getUserInfo(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject userInfo = new JSONObject(jsonObject.getString("data"));

                    if ("null".equals(userInfo.getString("username"))) {
                        mTxtName.setText("姓名");
                    } else {
                        mTxtName.setText(userInfo.getString("username"));
                    }

                    if (!"".equals(userInfo.getString("idcard"))) {
                        mNumTv.setText("ID:" + userInfo.getString("idcard"));
                    }

                    if ("".equals(userInfo.getString("company")) || "".equals(userInfo.getString("position"))) {
                        mTxtInfo.setText("公司名");
                    } else if (!"".equals(userInfo.getString("company")) && !"".equals(userInfo.getString("position"))) {
                        mTxtInfo.setText(userInfo.getString("position") + " | " + userInfo.getString("company"));
                    } else if (!"".equals(userInfo.getString("company"))) {
                        mTxtInfo.setText(userInfo.getString("company"));
                    } else if (!"".equals(userInfo.getString("position"))) {
                        mTxtInfo.setText(userInfo.getString("position"));
                    }

                    if (MeFragment.this.isAdded()) {//fragment 是否添加到activity
                        Drawable drawable = getResources().getDrawable(R.drawable.icon_head_default);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mRivHead.setImageDrawable(drawable);
                    }

                    if (!"".equals(userInfo.getString("avatar"))) {
                      /*  RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.icon_head_default)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);

                        Glide.with(getActivity())
                                .asBitmap()
                                .load(userInfo.getString("avatar"))
                                .apply(options)
                                .into(mRivHead);*/
                        ImgLoaderManager.getInstance().showImageView(userInfo.getString("avatar"), mRivHead);
                    }


                    state = userInfo.getString("status"); // 3待审核  1审核通过  2审核未通过
                    if (!TextUtils.isEmpty(state) && state.equals("1")) {
                        mIvIdenty.setVisibility(View.VISIBLE);
                        mIvIdenty.setBackgroundResource(R.drawable.icon_authen);
//                        mStateTv.setText("个人认证通过");
                        mStateTv.setVisibility(View.GONE);
                    } else if (!TextUtils.isEmpty(state) && state.equals("4")) {
                        mIvIdenty.setVisibility(View.VISIBLE);
                        mIvIdenty.setBackgroundResource(R.mipmap.user_vip);
//                        mStateTv.setText("企业认证通过");

                    } else if (!TextUtils.isEmpty(state) && state.equals("3")) {
                        mIvIdenty.setVisibility(View.GONE);
                        mStateTv.setVisibility(View.VISIBLE);
                        mStateTv.setText("审核中");
                    } else {
                   //     Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_identity_state);
                //        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                //        mStateTv.setCompoundDrawables(drawable, null, null, null);
                //        mStateTv.setTextColor(getResources().getColor(R.color.bg_light_red));
//                        mStateTv.setText("未认证");
                        mIvIdenty.setVisibility(View.GONE);
                    }
                    money = userInfo.getString("money");
                    openFace = userInfo.getString("openFace");
                    isRecommend = userInfo.getString("isRecommend");//是否显示合伙人计划 1=显示   -1=不显示

                    if (null != isRecommend && isRecommend.equals("1")) {
                        mPlanLl.setVisibility(View.VISIBLE);
                    }
                    if (openFace.equals("1")) {
                        mSwitchll.setVisibility(View.GONE);
                        mSanLl.setVisibility(View.GONE);
                        JSONObject openDetail = new JSONObject(userInfo.getString("openDetail"));
                        mType = openDetail.getString("type");
                        money = openDetail.getString("money");
                        mMoneyTv.setVisibility(View.VISIBLE);
                        mMoneyTv.setText(money + "/分钟");
                    } else {
                        mSanLl.setVisibility(View.GONE);
                        mSwitchBtn.setChecked(false);
                        mSwitchll.setVisibility(View.GONE);
                        mMoneyTv.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });


        FriendDbHelper.newMoments(uid, new IOAuthCallBack() {
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
                        JSONObject msg = new JSONObject(data.getString("message"));
                        if (Integer.valueOf(msg.getString("count")) > 0) {
                            // mCountTv.setVisibility(View.VISIBLE);
                            mCountTv.setText(msg.getString("count"));
                        } else {
                            mCountTv.setVisibility(View.GONE);
                            //清空MainActivity 发现角标
                            EventBus.getDefault().post(new PushExtraEvent("null", ""));
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLogout(MessageEvent messageEvent) {
        if (messageEvent.message.equals("logout")) {
            MeFragment.this.getActivity().finish();
            startActivity(new Intent(MeFragment.this.getActivity(), com.android.nana.auth.WelcomeActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(MeFragment.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_wallet:
                //   startActivity(new Intent(getActivity(), MyIncomeActivity.class));
                Intent intent2 = new Intent(getActivity(), WalletActivity.class);
                intent2.putExtra("state", state);
                startActivity(intent2);
                break;
            case R.id.ll_san:
                Intent intentPattern = new Intent(getContext(), SanPatternSetActivity.class);
                intentPattern.putExtra("uid", uid);
                intentPattern.putExtra("openFace", openFace);
                intentPattern.putExtra("mType", mType);
                intentPattern.putExtra("money", money);
                startActivity(intentPattern);
                break;
            case R.id.ll_album:
                Intent album = new Intent(getActivity(), MeAlbumActivity.class);
                album.putExtra("uid", uid);
                startActivity(album);
                break;
            case R.id.ll_plan:
                //邀请奖励
                Intent intentPartner = new Intent(getActivity(), PartnerBaseActivity.class);
                intentPartner.putExtra("mid", uid);
                startActivity(intentPartner);
                break;
            case R.id.ll_set:
                Intent intentSystem = new Intent(getActivity(), SystemSetupActivity.class);
                intentSystem.putExtra("mid", uid);
                startActivity(intentSystem);

                break;
            case R.id.customer_iv_head:
                Intent intent = new Intent(getActivity(), EditDataActivity.class);
                intent.putExtra("UserId", uid);
                startActivity(intent);
                break;
            case R.id.view_head:
                /*Intent intentMe = new Intent(getActivity(), MeDataActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("thisUid", uid);
                intentMe.putExtras(bundle);
                startActivity(intentMe);*/
                Intent intent1 = new Intent(getActivity(), MyDataActivity.class);
                intent1.putExtra("thisUid", uid);
                startActivity(intent1);
                break;
            case R.id.ll_collection://收藏
                Intent collection = new Intent(getActivity(), MeCollectionActivity.class);
                collection.putExtra("uid", uid);
                startActivity(collection);
                break;
            case R.id.ll_identity:
                if ("3".equals(state)) {
                    ToastUtils.showToast("正在审核中，预计需要1-3个工作日");
                } else if ("1".equals(state)||"4".equals(state)) {
                    Intent mIntent = new Intent(getActivity(), AuditedActivity.class);
                    mIntent.putExtra("state", state);
                    startActivity(mIntent);
                } else if ("2".equals(state)) {
                    //   startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
                    startActivity(new Intent(getActivity(), identity_homeActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), identity_homeActivity.class));
                    //     startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
                }
                break;
            case R.id.ll_set_money:
                if (state.equals("1")) {
                    Intent intentSetMoney = new Intent(getActivity(), SetMoneyActivity.class);
                    intentSetMoney.putExtra("mid", uid);
                    intentSetMoney.putExtra("money", money);
                    startActivity(intentSetMoney);
                } else {
                    showIncomeDialogs();
                }
                break;
            case R.id.ll_supplier:
                Intent web = new Intent(getActivity(), BaiduWebViewActivity.class);
                web.putExtra("knowledge", bean.getFaceThreeKnowledge());
                web.putExtra("findText", bean.getText());
                web.putExtra("mid", uid);
                startActivity(web);
                break;
            case R.id.ll_location:
                Intent mLocation = new Intent(getActivity(), MeLocationActivity.class);
                startActivity(mLocation);
                break;
            case R.id.ll_coop:
                Intent coopIntent = new Intent(getActivity(), CoopActivity.class);
                startActivity(coopIntent);
                break;
            case R.id.ll_shopping:
                Intent intentUrl = new Intent(getActivity(), CommonActivity.class);
                intentUrl.putExtra("title", "有赞精选");
                intentUrl.putExtra("url", "http://j.youzan.com/MEtvo9");
                getActivity().startActivity(intentUrl);
                break;
            default:
                break;
        }
    }

    private void showIncomeDialogs() {//身份证未认证提示
        DialogHelper.customMeAlert(getActivity(), "提示", "你的身份未认证，需要认证\n身份之后才能修改见面金额", new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {
                startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
            }
        }, new DialogHelper.OnAlertConfirmClick() {
            @Override
            public void OnClick(String content) {
            }

            @Override
            public void OnClick() {
                mSwitchBtn.setChecked(false);
            }
        });
    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked) {
            if (state.equals("1")) {
             /*   Intent intent = new Intent(getContext(), SanPatternSetActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("money", money);
                intent.putExtra("openFace", openFace);
                startActivity(intent);*/
                Intent intentSetMoney = new Intent(getActivity(), SetMoneyActivity.class);
                intentSetMoney.putExtra("mid", uid);
                intentSetMoney.putExtra("money", money);
                startActivity(intentSetMoney);
            } else {
                showIncomeDialogs();
            }
        }
    }

    private void initHtml5() {
        CustomerDbHelper.AdvisoryHtmlIndexUrl(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        bean = parseHtmlData(successJson);
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


    public HtmlBean parseHtmlData(String result) {//Gson 解析
        HtmlBean detail = new HtmlBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), HtmlBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        }
    }
}
