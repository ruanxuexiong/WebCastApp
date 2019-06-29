package com.android.nana.friend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.adapter.PhoneAdapter;
import com.android.nana.bean.PhoneEntity;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.user.AddFriendActivity;
import com.android.nana.util.ListDataSave;
import com.android.nana.util.PhoneUtils;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.CharacterParser;
import com.android.nana.widget.PinyinPhone;
import com.android.nana.widget.SideBar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2019/1/7.
 */

public class PhoneContactActivity extends BaseActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, PhoneAdapter.PhoneListener {

    private TextView mTitleTv;
    private TextView mBackTv, mDialogTv;
    private String mUserId;
    private SideBar mSideBar;
    private ListView mListView;
    private PhoneAdapter mAdapter;
    private CharacterParser mParser;
    private PinyinPhone mPinyin;
    private LinearLayout mSearchLl;
    private MultipleStatusView mMultipleStatusView;
    private ArrayList<PhoneEntity> mDataList = new ArrayList<>();
    private String mThisTel;//本机号码
    private ListDataSave mListData;

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
        mTitleTv.setText("添加朋友");
        mBackTv.setVisibility(View.VISIBLE);
        mUserId = BaseApplication.getInstance().getCustomerId(this);
        if (requestMail(PhoneContactActivity.this)) {
          initData();
        } else {
            mMultipleStatusView.noEmpty();
        }

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mThisTel = tm.getLine1Number();//手机号码

        mParser = CharacterParser.getInstance();
        mPinyin = new PinyinPhone();
        mSideBar.setTextView(mDialogTv);

        mAdapter = new PhoneAdapter(PhoneContactActivity.this, mDataList, PhoneContactActivity.this, mThisTel);
        mListView.setAdapter(mAdapter);
        mListData = new ListDataSave(PhoneContactActivity.this, "phone");
    }
    private void initData(){
        List<HashMap<String, String>> mPhones = PhoneUtils.getAllContactInfo(PhoneContactActivity.this);
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
        if(requestCode==1006){
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
                FriendDbHelper.sendMsg(entity.getIdcard(), new IOAuthCallBack() {
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
                                    String msg = jsonObject.getJSONObject("data").getString("template");
                                    FragmentManager fm = getSupportFragmentManager();
                                    PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
                                    dialog.show(fm, "dialog");
                                }
//                                String msg = jsonObject.getString("data");
//                                FragmentManager fm = getSupportFragmentManager();
//                                PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
//                                dialog.show(fm, "dialog");
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

        }

    }

    @Override
    public void onAddFriendClick(View view) {
        PhoneEntity entity = mDataList.get((Integer) view.getTag());
        Intent intent = new Intent(PhoneContactActivity.this, AddFriendActivity.class);
        intent.putExtra("uid", entity.getId());
        intent.putExtra("userName", entity.getUsername());
        intent.putExtra("info", entity.getIntroduce());
        intent.putExtra("logo", entity.getAvatar());
        intent.putExtra("thisUserId", mUserId);
        intent.putExtra("status", entity.getStatus());
        startActivity(intent);
    }

    @Override
    public void onSendMsgClick(View view) {
        final PhoneEntity entity = mDataList.get((Integer) view.getTag());
        String idcard = (String) SharedPreferencesUtils.getParameter(PhoneContactActivity.this, "idcard", "");
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
                            String msg = jsonObject.getJSONObject("data").getString("template");
                            FragmentManager fm = getSupportFragmentManager();
                            PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
                            dialog.show(fm, "dialog");
                        }
//                        String msg = jsonObject.getString("data");
//                        FragmentManager fm = getSupportFragmentManager();
//                        PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
//                        dialog.show(fm, "dialog");
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
}
