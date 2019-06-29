package com.android.nana.friend;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.webcast.SearchActivity;

/**
 * Created by lenovo on 2019/1/7.
 */

public class AddNewFriendActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBackTv, mTitleTv;
    private LinearLayout mSearchLl, mContactLl;
    private TextView mUidTv;


    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_add_new_friend);
    }

    @Override
    protected void findViewById() {
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mSearchLl = findViewById(R.id.ll_search);
        mContactLl = findViewById(R.id.ll_contact);
        mUidTv = findViewById(R.id.tv_id);
    }

    @Override
    protected void init() {
        mTitleTv.setText("添加朋友");
        mBackTv.setVisibility(View.VISIBLE);
        mUidTv.setText("我的哪哪ID:"+ SharedPreferencesUtils.getParameter(AddNewFriendActivity.this,"idcard",""));
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mSearchLl.setOnClickListener(this);
        mContactLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_search:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                intentSearch.putExtra("state", "0");
                startActivity(intentSearch);
                break;
            case R.id.ll_contact:
                if (requestMail(AddNewFriendActivity.this)) {
                    Intent intent = new Intent(this, PhoneContactActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this, PhoneContactActivity.class);
                    startActivity(intent);
                }
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

}
