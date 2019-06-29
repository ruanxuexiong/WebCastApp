package com.android.nana.mail;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.HomeDbHelper;
import com.android.nana.eventBus.GroupEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.widget.OverrideEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.model.Group;

/**
 * Created by lenovo on 2017/9/20.
 */

public class UpdateGroupNameActivity extends BaseActivity implements View.OnClickListener {


    private TextView mTitleTv;
    private TextView mBackTv;
    private OverrideEditText mNameEt;
    private TextView mRight2Tv;
    private String mGroupName;
    private String mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getStringExtra("GroupId")) {
            mGroupId = getIntent().getStringExtra("GroupId");
            mGroupName = getIntent().getStringExtra("GroupName");
            mNameEt.setText(mGroupName);
            mNameEt.requestFocus();
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_update_group_name);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mRight2Tv = findViewById(R.id.toolbar_right_2);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mNameEt = findViewById(R.id.et_name);
    }

    @Override
    protected void init() {
        mBackTv.setVisibility(View.VISIBLE);
        mTitleTv.setText("群聊名称");

        mRight2Tv.setVisibility(View.VISIBLE);
        mRight2Tv.setTextColor(getResources().getColor(R.color.white));
        mRight2Tv.setText("保存");
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mRight2Tv.setOnClickListener(this);
        mNameEt.setDrawableClick(new OverrideEditText.IMyRightDrawableClick() {
            @Override
            public void rightDrawableClick() {
                mNameEt.setText("");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.toolbar_right_2:
                save();
                break;
        }
    }

    private void save() {//保存

        mGroupName = mNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(mGroupName)) {
            ToastUtils.showToast("群聊名称不能为空！");
            return;
        }
        if (mGroupName.length() == 1) {
            ToastUtils.showToast("群名称不少于2个字");
            return;
        }
        if (AndroidEmoji.isEmoji(mGroupName)) {
            if (mGroupName.length() <= 2) {
                ToastUtils.showToast("群名称不少于2个字");
                return;
            }
        }
        HomeDbHelper.updateGroup(mGroupName, mGroupId, null, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ToastUtils.showToast("更改成功");
                        Group groupInfo = new Group(mGroupId, mNameEt.getText().toString(), Uri.parse(data.getString("picture")));
                        RongIM.getInstance().refreshGroupInfoCache(groupInfo);
                        UpdateGroupNameActivity.this.finish();
                        EventBus.getDefault().post(new GroupEvent());
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
