package com.android.nana.friend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.adapter.PhoneAdapter;
import com.android.nana.adapter.RedPhoneAdapter;
import com.android.nana.bean.FriendsBookEntity;
import com.android.nana.bean.PhoneEntity;
import com.android.nana.customer.EditWorkActivity;
import com.android.nana.customer.EducationListActivity;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.material.ProfileActivity;
import com.android.nana.user.AddFriendActivity;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.ListDataSave;
import com.android.nana.util.PhoneUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.pay.SignUtils;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.LabelsView;
import com.android.nana.widget.PinyinPhone;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;
import com.iflytek.thirdparty.S;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 *
 */

public class RedPhoneContactActivity extends BaseActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, RedPhoneAdapter.PhoneListener {
    private String time="";
    private TextView mTitleTv;
    private TextView mBackTv, mDialogTv;
    private String mUserId;
    private SideBar mSideBar;
    private ListView mListView;
    private RedPhoneAdapter mAdapter;
    private CharacterParser mParser;
    private PinyinPhone mPinyin;
    private LinearLayout mSearchLl;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<PhoneEntity> mDataList = new ArrayList<>();
    private String mThisTel;//本机号码
    private ListDataSave mListData;
    private String templateBound = "";
    private String templateBoundNoLink = "";
    private boolean sendMsg = false;

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_phone_contact);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
        mListView = findViewById(R.id.lv_follow);
        mDialogTv = findViewById(R.id.tv_dialog);
        mSideBar = findViewById(R.id.sidrbar);
        mSearchLl = findViewById(R.id.rl_search);

    }

    @Override
    protected void init() {
        mTitleTv.setText("手机联系人");
        mBackTv.setVisibility(View.VISIBLE);
        mUserId = BaseApplication.getInstance().getCustomerId(this);
        if (requestMail(RedPhoneContactActivity.this)) {
            initData();
        } else {
            mMultipleStatusView.noEmpty();
        }

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mThisTel = tm.getLine1Number();//手机号码

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinPhone();
        mSideBar.setTextView(mDialogTv);

        mAdapter = new RedPhoneAdapter(RedPhoneContactActivity.this, mDataList, RedPhoneContactActivity.this, mThisTel);
        mListView.setAdapter(mAdapter);
        mListData = new ListDataSave(RedPhoneContactActivity.this, "phone");
        final String idcard = (String) SharedPreferencesUtils.getParameter(RedPhoneContactActivity.this, "idcard", "");
        FriendDbHelper.sendMsg(idcard, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        if (jsonObject.get("data") instanceof JSONObject) {
                            templateBound = jsonObject.getJSONObject("data").getString("templateBound");
                            templateBoundNoLink = jsonObject.getJSONObject("data").getString("templateBoundNoLink");

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

    private void initData() {
        List<HashMap<String, String>> mPhones = PhoneUtils.getAllContactInfo(RedPhoneContactActivity.this);
        StringBuilder name = new StringBuilder();
        StringBuilder phone = new StringBuilder();
        for (HashMap<String, String> entity : mPhones) {
            if (null == entity.get("name")) {

            } else {
                name.append(entity.get("name") + ",");
            }

            if (null == entity.get("phone")) {

            } else {
                phone.append(entity.get("phone") + ",");
            }
        }
        getPhone(name.toString(), phone.toString().replaceAll(" +", ""));

    }

    private void getPhone(String name, String phone) {

            FriendDbHelper.getPhoneUser(name, phone, mUserId, new IOAuthCallBack() {
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
                                for (PhoneEntity entity : parseData(successJson)) {
                                    mDataList.add(entity);
                                }
                                if (mDataList.size() > 0) {
                                    Collections.sort(mDataList, mPinyin);

                                    mListData.setDataList("phoneContact", mDataList);
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                mMultipleStatusView.noEmpty();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void getFailue(String failueJson) {

                    ToastUtils.showToast(failueJson);
                    mMultipleStatusView.error();
                }
            });

    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSearchLl.setOnClickListener(this);
        mSideBar.setOnTouchingLetterChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.rl_search:
                Intent intent = new Intent(this, SendPhoneActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public boolean requestMail(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS);
            //没有授权
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                //进行授权提示 1006为返回标识
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, 1006);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1006) {
            initData();
        }
    }

    private ArrayList<PhoneEntity> parseData(String result) {
        ArrayList<PhoneEntity> phone = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            JSONArray array = new JSONArray(data.getString("user"));
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                PhoneEntity entity = gson.fromJson(array.optJSONObject(i).toString(), PhoneEntity.class);

                String pinyin = mParser.getSelling(entity.getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }

                phone.add(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }


    @Override
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        final PhoneEntity entity = mDataList.get((Integer) view.getTag());
        if (mUserId.equals(entity.getId())) {
            Intent intent = new Intent(this, EditDataActivity.class);
            intent.putExtra("UserId", mUserId);
            startActivity(intent);
        } else {
            if (entity.getIsMember().equals("1")) {
                Intent intentEdit = new Intent(this, EditDataActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("UserId", entity.getId());
                intentEdit.putExtras(bundle);
                startActivity(intentEdit);
            } else {
                FragmentManager fm = getSupportFragmentManager();
                PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), templateBound, entity.getMobile());
                dialog.show(fm, "dialog");
            }
        }

    }

    @Override
    public void onAddFriendClick(View view) {
        PhoneEntity entity = mDataList.get((Integer) view.getTag());
        sendMsg(entity);
    }

    private void sendMsg(PhoneEntity entity) {
        toInform(entity.getId(), entity.getUsername(), templateBoundNoLink);
        // startActivity(new Intent(EditDataActivity.this, ConversationListActivity.class));
    }

    public void toInform(final String uid, final String name, final String notice_msg) {
        FriendDbHelper.toInform(uid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = jsonObject.getJSONObject("result");
                    if (result.getString("state").equals("-1")) {//state="-1"已通知過
                        ToastUtils.showToast(result.getString("description"));
                    } else {
                        showSendDialog(uid, name, notice_msg);
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

    public void showSendDialog(final String uid, final String name, String notice_msg) {
        FragmentManager fm = getSupportFragmentManager();
        final PhoneContactFragment dialog = PhoneContactFragment.newInstance(name, notice_msg, "");
        dialog.show(fm, "dialog");
        dialog.setOnClickListener(new PhoneContactFragment.OnClick() {
            @Override
            public void sendMsg(String msg) {
                TextMessage textMessage = TextMessage.obtain(msg);
                RongIM.getInstance().sendMessage(Conversation.ConversationType.PRIVATE, uid, textMessage, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        sendMsg = true;
                        startChat(uid, name);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void dismiss() {
                if (sendMsg) {


                    String mid = (String) SharedPreferencesUtils.getParameter(RedPhoneContactActivity.this, "userId", "");
                    time=getTime();
                    Map<String,String> map=new HashMap<>();
                    map.put("userId",mid);
                    map.put("toUid",uid);
                    map.put("time",time);
                  String sign= SignUtils.signSort(map);
                    FriendDbHelper.getInformResult(mid, uid,sign,time, new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {

                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });
                    sendMsg = false;
                }
            }
        });
    }

    public void startChat(final String uid, final String name) {

        showProgressDialog("", "加载中请稍后...");
        String mid = (String) SharedPreferencesUtils.getParameter(RedPhoneContactActivity.this, "userId", "");
        HomeDbHelper.isFriend(mid, uid, new IOAuthCallBack() {
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
                        if (data.getString("friend").equals("2")) {//非好友状态
                            JSONObject mTanchuang = new JSONObject(data.getString("tanchuang"));
                            String isTanchang = mTanchuang.getString("isTanchang");//（1=弹窗   2=不弹窗）
                            String mMoney = mTanchuang.getString("money");
                            String mEnough = mTanchuang.getString("enough");//余额是否足够：enough（1=是  -1=否）
                            RongIM.getInstance().startPrivateChat(RedPhoneContactActivity.this, uid + "=" + isTanchang + "=" + mMoney + "=" + mEnough + "=", name);
                        } else {
                            RongIM.getInstance().startPrivateChat(RedPhoneContactActivity.this, uid, name);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
                return;
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });

    }

    @Override
    public void onSendMsgClick(View view) {
        PhoneEntity entity = mDataList.get((Integer) view.getTag());
        FragmentManager fm = getSupportFragmentManager();
        PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), templateBound, entity.getMobile());
        dialog.show(fm, "dialog");
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;
        String str = String.valueOf(time);
        return str;
    }
}
