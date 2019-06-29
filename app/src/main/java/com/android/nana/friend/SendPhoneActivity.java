package com.android.nana.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.bean.PhoneEntity;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.loading.MultipleStatusView;
import com.android.nana.material.EditDataActivity;
import com.android.nana.user.AddFriendActivity;
import com.android.nana.util.ListDataSave;
import com.android.nana.util.PhoneUtils;
import com.android.nana.widget.OverrideEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2019/1/8.
 */

public class SendPhoneActivity extends BaseActivity implements View.OnClickListener, SendPhoneAdapter.SendPhoneListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private OverrideEditText mSearchEt;
    private SendPhoneAdapter mAdapter;
    private String keyword;
    private ListView mListView;
    private String mUserId;
    private String mThisTel;//本机号码
    private MultipleStatusView mMultipleStatusView;
    private InputMethodManager mInputMethodManager; // 隐藏软键盘
    private List<PhoneEntity> mDataList = new ArrayList<>();
    private ArrayList<PhoneEntity> mListSearchData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_send_phone);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mSearchEt = findViewById(R.id.et_search);
        mListView = findViewById(R.id.lv_follow);
        mMultipleStatusView = findViewById(R.id.multiple_status_view);
    }

    @Override
    protected void init() {
        mTitleTv.setText("通讯录搜索");
        mBackTv.setVisibility(View.VISIBLE);
        mUserId = BaseApplication.getInstance().getCustomerId(this);
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

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query(s.toString());
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

                    query(keyword);
                    return true;
                }
                return false;
            }
        });
        ListDataSave dataSave = new ListDataSave(SendPhoneActivity.this, "phone");
        mDataList = dataSave.getDataList("phoneContact");
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mThisTel = tm.getLine1Number();//手机号码

        mAdapter = new SendPhoneAdapter(SendPhoneActivity.this, mListSearchData, SendPhoneActivity.this, mThisTel);
        mListView.setAdapter(mAdapter);
    }

    //搜索
    private void query(String keyword) {
        mMultipleStatusView.dismiss();
        if (!TextUtils.isEmpty(keyword)) {
            String key = keyword.toString().trim();
            if (!TextUtils.isEmpty(key)) {
                if (mListSearchData != null && mListSearchData.size() > 0) {
                    mListSearchData.clear();
                }
                for (PhoneEntity bean : mDataList) {
                    if (PhoneUtils.isNumeric(key) || PhoneUtils.isContainChinese(key)) {
                        if (bean.getMobile().contains(key) || bean.getUsername().contains(key)) {
                            mListSearchData.add(bean);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mListSearchData.clear();
            mAdapter.notifyDataSetChanged();
            mMultipleStatusView.noEmpty();
        }
    }


    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchEt.setCursorVisible(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        final PhoneEntity entity = mListSearchData.get((Integer) view.getTag());
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


                                String msg = jsonObject.getString("data");
                                FragmentManager fm = getSupportFragmentManager();
                                PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
                                dialog.show(fm, "dialog");
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
        PhoneEntity entity = mListSearchData.get((Integer) view.getTag());
        Intent intent = new Intent(SendPhoneActivity.this, AddFriendActivity.class);
        intent.putExtra("uid", mUserId);
        intent.putExtra("userName", entity.getUsername());
        intent.putExtra("info", entity.getIntroduce());
        intent.putExtra("logo", entity.getAvatar());
        intent.putExtra("thisUserId", entity.getId());
        intent.putExtra("status", entity.getStatus());
        startActivity(intent);
    }

    @Override
    public void onSendMsgClick(View view) {
        final PhoneEntity entity = mListSearchData.get((Integer) view.getTag());

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

                        String msg = jsonObject.getString("data");
                        FragmentManager fm = getSupportFragmentManager();
                        PhoneContactFragment dialog = PhoneContactFragment.newInstance(entity.getUsername(), msg, entity.getMobile());
                        dialog.show(fm, "dialog");
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
