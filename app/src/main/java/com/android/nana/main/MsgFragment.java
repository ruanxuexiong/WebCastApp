package com.android.nana.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.PopWindow.CustomPopWindow;
import com.android.nana.R;
import com.android.nana.adapter.ConversationListAdapterEx;
import com.android.nana.addialog.AdConstant;
import com.android.nana.addialog.AdInfo;
import com.android.nana.addialog.AdManager;
import com.android.nana.addialog.DepthPageTransformer;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.MessageArrayEvent;
import com.android.nana.eventBus.UpdateMessageEvent;
import com.android.nana.eventBus.UpdateWantMessageEvent;
import com.android.nana.eventBus.WantEvent;
import com.android.nana.eventBus.WhoSeeEvent;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.friend.AddNewFriendActivity;
import com.android.nana.listener.MainListener;
import com.android.nana.mail.SelectFriendsActivity;
import com.android.nana.material.EditUserInfoActivity;
import com.android.nana.material.WantBean;
import com.android.nana.partner.PartnerFragment;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.webcast.SearchActivity;
import com.android.nana.widget.StateButton;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import net.soulwolf.widget.speedyselector.widget.SelectorTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/12/1.
 */

public class MsgFragment extends BaseRequestFragment implements View.OnClickListener {

    private TextView mTitleTv;
    private LinearLayout mSearchRl;
    private ConversationListFragment mConversationListFragment = null;

    private FragmentTransaction mFragmetTrans;
    private FragmentManager mFragmentManager;
    private TextView mRightTv;

    private LinearLayout mWholl, mWantll, mRrecordll;
    private SelectorTextView mtWhoNumTv, mWantNumTv;
    private String mid;//当前用户id;
    public String whoseeComment;//谁要见我num
    public String wantComment;//我要见谁num

    private Dialog mPerfectDialog;
    private View mPerfectView;
    private TextView mPerfectContentTv;
    private StateButton mPerfectBtn;
    private ImageButton mCloseBtn;
    private String mUserId;
    private CustomPopWindow mCustomPopWindow;
    //分享内容
    private String mShareUrl, mMessage, mShareTitle, mSareDesc, mShareLogo;

    private TextView mConsultMeTv;//我要咨询谁
    private TextView mMeConsultTv;//谁要咨询我
    private TextView mRecordTv;//咨询记录

    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<WantBean> wantBeen = new ArrayList<>();
    private ArrayList<WantBean> mWantBeen = new ArrayList<>();

    private TextView mCloseTv;
    private TextView mSendTv;
    private LinearLayout mContentLl;
    private TextView mFindTextTv;
    private ImageView mLogoIv;
    private HtmlBean bean;

    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(MsgFragment.this)) {
            EventBus.getDefault().register(MsgFragment.this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_msg, container, false);

        mFragmentManager = getActivity().getSupportFragmentManager();
        mFragmetTrans = mFragmentManager.beginTransaction();
        // mConversationListFragment = initConversationList();
        mFragmetTrans.add(R.id.view_layout, initConversationList()).commit();

        mTitleTv = view.findViewById(R.id.tv_title);
        mSearchRl = view.findViewById(R.id.rl_search);
        mRightTv = view.findViewById(R.id.toolbar_right_2);

        mWholl = view.findViewById(R.id.ll_who);
        mWantll = view.findViewById(R.id.ll_want);
        mRrecordll = view.findViewById(R.id.ll_record);

        mtWhoNumTv = view.findViewById(R.id.tv_who_num);
        mWantNumTv = view.findViewById(R.id.tv_want_num);

        mPerfectView = LayoutInflater.from(getActivity()).inflate(R.layout.home_perfect_dialog, null);
        mPerfectBtn = mPerfectView.findViewById(R.id.btn_perfect);
        mPerfectContentTv = mPerfectView.findViewById(R.id.tv_content);
        mCloseBtn = mPerfectView.findViewById(R.id.close_iv);
        mPerfectDialog = new AlertDialog.Builder(getActivity()).create();
        mPerfectDialog.setCanceledOnTouchOutside(false);

        mConsultMeTv = view.findViewById(R.id.tv_consult_me);
        mMeConsultTv = view.findViewById(R.id.tv_me_consult);
        mRecordTv = view.findViewById(R.id.tv_record_num);

        //弹窗广告
        mCloseTv = view.findViewById(R.id.tv_close);
        mContentLl = view.findViewById(R.id.ll_content);
        mSendTv = view.findViewById(R.id.btn_send);
        mFindTextTv = view.findViewById(R.id.tv_find_text);
        mLogoIv = view.findViewById(R.id.iv_img);
       /* String msg = (String) SharedPreferencesUtils.getParameter(getActivity(), "msg", "");
        if ("3".equals(msg)) {
            mContentLl.setVisibility(View.VISIBLE);
        }*/
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        initData();
        initHtml5();


        mTitleTv.setText("消息");
        mRightTv.setVisibility(View.VISIBLE);
        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.icon_add);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mRightTv.setCompoundDrawables(drawable, null, null, null);

        mUserId = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        getMeetingNum(mUserId);
        //加载分享信息
        loadShare(mUserId);

        mSearchRl.setOnClickListener(this);
        mRightTv.setOnClickListener(this);
        mLogoIv.setOnClickListener(this);
        return view;
    }


    private void loadShare(String mUserId) {
        HomeDbHelper.shenFriend(mUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mShareUrl = data.getString("shareUrl");
                        mMessage = data.getString("message");
                        mShareTitle = data.getString("shareTitle");
                        mSareDesc = data.getString("shareDesc");
                        mShareLogo = data.getString("shareLogo");
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

    public void getMeetingNum(String mUserId) {

        HomeDbHelper.meetingNum(mUserId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        whoseeComment = data.getString("seeMineRecordNum");//谁要咨询我
                        wantComment = data.getString("seeOtherRecordNum");//我要咨询谁
                        if (Integer.valueOf(whoseeComment) > 0) {
                            mMeConsultTv.setText(whoseeComment);
                            mMeConsultTv.setVisibility(View.VISIBLE);
                        } else {
                            mMeConsultTv.setVisibility(View.GONE);
                        }

                        if (Integer.valueOf(wantComment) > 0) {
                            mConsultMeTv.setText(wantComment);
                            mConsultMeTv.setVisibility(View.VISIBLE);
                        } else {
                            mConsultMeTv.setVisibility(View.GONE);
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    protected void findViewById() {

    }

    private void initHtml5() {
        CustomerDbHelper.AdvisoryHtmlIndexUrl(mid, new IOAuthCallBack() {
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
                        ImageLoader.getInstance().displayImage(bean.getLogo(), mLogoIv);
                        mFindTextTv.setText(bean.getText());
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

    private void initData() {
        HomeDbHelper.adDialog(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (data.getString("isopen").equals("1")) {//判断是否打开
                            AdManager adManager = new AdManager(getActivity(), parseData(successJson));
                            adManager.setOverScreen(true).setPageTransformer(new DepthPageTransformer());
                            adManager.showAdDialog(AdConstant.ANIM_DOWN_TO_UP);
                            adManager.setOnImageClickListener(new AdManager.OnImageClickListener() {
                                @Override
                                public void onImageClick(View view, AdInfo onAdInfo) {
                                //    Intent intent = new Intent(getContext(), CommonActivity.class);
                              //      intent.putExtra("title", onAdInfo.getTitle());
                                 //   intent.putExtra("url", onAdInfo.getUrl());
                                  //  startActivity(intent);
                                    Intent intent2=new Intent(getActivity(),MeLocationActivity.class);
                                    startActivity(intent2);
                                }
                            });
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

    @Override
    protected void init() {



    }

    private ConversationListFragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));

            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter("nana", "nana")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                    .build();
            listFragment.setUri(uri);

            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }


    @Override
    protected void setListener() {
        mSearchRl.setOnClickListener(this);


        mWholl.setOnClickListener(this);
        mWantll.setOnClickListener(this);
        mRrecordll.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);

        mCloseTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_img:

                break;

            case R.id.rl_search:
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
            case R.id.toolbar_right_2:
                onAction2Click();
                break;
            case R.id.ll_who:
                Intent whoSee = new Intent(getContext(), WhoSeeActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putParcelableArrayList("msg", messages);
                whoSee.putExtras(bundle1);
                startActivity(whoSee);
                break;
            case R.id.ll_want:
                Intent intent = new Intent(getContext(), WantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("msg", messages);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.ll_record:
                Intent record = new Intent(getContext(), RecordActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putParcelableArrayList("msg", messages);
                record.putExtras(bundle2);
                startActivity(record);
                break;
            case R.id.close_iv:
                mPerfectDialog.dismiss();
                break;
            case R.id.tv_close:
                mContentLl.setVisibility(View.GONE);
                break;
            case R.id.btn_send:
                Intent web = new Intent(getActivity(), BaiduWebViewActivity.class);
                web.putExtra("knowledge", bean.getFaceThreeKnowledge());
                web.putExtra("findText", bean.getText());
                web.putExtra("mid", mid);
                startActivity(web);
                break;
            default:
                break;
        }
    }


    private void onAction2Click() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_layout, null);

        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .setFocusable(true)
                .setBgDarkAlpha(0.7f)
                .enableBackgroundDark(true)
                .setOutsideTouchable(true)
                .create();
        mCustomPopWindow.showAsDropDown(mRightTv, 0, 10);
        handleLogic(contentView);
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                switch (v.getId()) {
                    case R.id.tv_launch:
                        Intent intent = new Intent(new Intent(getContext(), SelectFriendsActivity.class));
                        intent.putExtra("createGroup", true);
                        getContext().startActivity(intent);
                        break;
                    case R.id.tv_share:
                        share();
                        break;
                    case R.id.tv_add_friend:
                        Intent intent1 = new Intent(new Intent(getContext(), AddNewFriendActivity.class));
                        startActivity(intent1);
                        break;
                    default:
                        break;
                }
            }
        };
        contentView.findViewById(R.id.tv_launch).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_share).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_add_friend).setOnClickListener(listener);
    }

    private void share() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PartnerFragment dialog = PartnerFragment.newInstance(mUserId, mShareTitle, mShareLogo, mSareDesc, mMessage, mShareUrl);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        CustomerDbHelper.checkIsAlert(mid, mIOCheckIsAlert);//当前用户id身份证是否认证


        MainListener.getInstance().mOnMessageListener = new MainListener.OnMessageListener() {
            @Override
            public void refersh(Message message) {
                if (message.getReceivedStatus().isRead()) {
                    messages.clear();
                    messages.add(message);
                    zxToMe(mid);
                    zxToOther(mid);
                } else {
                    messages.add(message);
                    zxToMe(mid);
                    zxToOther(mid);
                }
            }
        };


        MainListener.getInstance().mOnMessageRefreshListener = new MainListener.OnMessageRefreshListener() {
            @Override
            public void refersh() {
                getMeetingNum(mid);
            }

            @Override
            public void clear() {
            }
        };
    }

    private void zxToOther(String mid) {//我要质询谁

        CustomerDbHelper.zxToOther(mid, 0, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseToOtherData(successJson).size() > 0) {
                            for (WantBean item : parseToOtherData(successJson)) {
                                if (!mWantBeen.contains(item)) {
                                    mWantBeen.add(item);
                                }
                            }

                            for (int i = 0; i < messages.size(); i++) {
                                for (int j = 0; j < mWantBeen.size(); j++) {
                                    if (mWantBeen.get(j).getTalkId().equals(messages.get(i).getTargetId())) {

                                        if (messages.get(i).getReceivedStatus().isRead()) {
                                            int num = messages.size() - 1;
                                            if (num == 0) {
                                                mtWhoNumTv.setVisibility(View.GONE);
                                            } else {
                                                mtWhoNumTv.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            mWantNumTv.setVisibility(View.VISIBLE);
                                            mWantNumTv.setText("");
                                        }
                                    }
                                }
                            }
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

    private void zxToMe(String mid) {//谁要咨询我

        CustomerDbHelper.zxToMe(mid, 0, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (parseWhoData(successJson).size() > 0) {
                            for (WantBean item : parseWhoData(successJson)) {
                                if (!wantBeen.contains(item)) {
                                    wantBeen.add(item);
                                }
                            }


                            for (int i = 0; i < messages.size(); i++) {
                                for (int j = 0; j < wantBeen.size(); j++) {
                                    if (wantBeen.get(j).getTalkId().equals(messages.get(i).getTargetId())) {//谁要咨询我
                                        if (messages.get(i).getReceivedStatus().isRead()) {
                                            int num = messages.size() - 1;
                                            if (num == 0) {
                                                mtWhoNumTv.setVisibility(View.GONE);
                                            } else {
                                                mtWhoNumTv.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            mtWhoNumTv.setVisibility(View.VISIBLE);
                                            mtWhoNumTv.setText("");
                                        }
                                    }
                                }
                            }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MsgFragment.this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateMsg(WhoSeeEvent whoSeeEvent) {//谁要见我msg
        getMeetingNum(mUserId);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMsg(MessageArrayEvent message) {//咨询记录红点
        if (message.messages.size() == 0) {
            mRecordTv.setVisibility(View.GONE);
        } else if (message.messages.size() > 0) {
            mRecordTv.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateWantMsg(WantEvent wantEvent) {//我要见谁msg
        getMeetingNum(mUserId);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateMessage(UpdateMessageEvent event) {//更新谁要咨询我红点
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getTargetId().equals(event.message.getTargetId())) {
                messages.get(i).getReceivedStatus().setRead();
                messages.get(i).setReceivedStatus(messages.get(i).getReceivedStatus());
                messages.remove(messages.get(i));
            }
        }
        if (messages.size() == 0) {
            mtWhoNumTv.setVisibility(View.GONE);
        } else {
            zxToMe(mid);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateWantMessage(UpdateWantMessageEvent event) {//更新我要咨询谁红点
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getTargetId().equals(event.message.getTargetId())) {
                messages.get(i).getReceivedStatus().setRead();
                messages.get(i).setReceivedStatus(messages.get(i).getReceivedStatus());
                messages.remove(messages.get(i));
            }
        }

        if (messages.size() == 0) {
            mWantNumTv.setVisibility(View.GONE);
        } else {
            zxToOther(mid);
        }
    }


    private IOAuthCallBack mIOCheckIsAlert = new IOAuthCallBack() {
        @Override
        public void onStartRequest() {

        }

        @Override
        public void getSuccess(String successJson) {

            try {
                JSONObject jsonObject = new JSONObject(successJson);
                JSONObject result = new JSONObject(jsonObject.getString("result"));
                if (result.getString("state").equals("0")) {
                    isAlert();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getFailue(String failueJson) {

        }
    };


    //当前用户是否认证

    private void isAlert() {
        CustomerDbHelper.checkUser(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("-1")) {//未完善资料
                        perfect(result.getString("description"));
                    } else if (result.getString("state").equals("-2")) {//未认证
                        certified(result.getString("description"));
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

    private void perfect(String description) {//未完善
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUser = new Intent(getActivity(), EditUserInfoActivity.class);
                Bundle bundleUser = new Bundle();
                bundleUser.putString("uid", mid);
                intentUser.putExtras(bundleUser);
                startActivity(intentUser);
            }
        });
    }

    private void certified(String description) {//未认证
        mPerfectContentTv.setText(description);
        mPerfectDialog.show();
        mPerfectDialog.getWindow().setContentView(mPerfectView);
        mPerfectBtn.setText("马上认证");
        mPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IDAuthenticationActivity.class));
            }
        });
    }

    private ArrayList<AdInfo> parseData(String result) {//弹窗广告

        ArrayList<AdInfo> adInfo = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));

            JSONArray array = new JSONArray(data.getString("advertise"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                AdInfo entity = gson.fromJson(array.optJSONObject(i).toString(), AdInfo.class);
                adInfo.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adInfo;
    }

    public ArrayList<WantBean> parseWhoData(String result) {//谁要咨询我
        ArrayList<WantBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray jsonArray = new JSONArray(data.getString("zxToMe"));
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                WantBean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), WantBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<WantBean> parseToOtherData(String result) {//我要咨询谁
        ArrayList<WantBean> detail = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray jsonArray = new JSONArray(data.getString("zxToOther"));
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                WantBean entity = gson.fromJson(jsonArray.optJSONObject(i).toString(), WantBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
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
