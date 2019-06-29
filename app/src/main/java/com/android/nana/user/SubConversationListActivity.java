package com.android.nana.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.adapter.SubConversationListAdapterEx;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.SubConversationListFragment;

/**
 * Created by lenovo on 2017/9/8.
 * 聚合会话列表
 */

public class SubConversationListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SubConversationListFragment fragment = new SubConversationListFragment();
        fragment.setAdapter(new SubConversationListAdapterEx(RongContext.getInstance()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();

        Intent intent = getIntent();
        if (intent.getData() == null) {
            return;
        }
        //聚合会话参数
        String type = intent.getData().getQueryParameter("type");

        if (type == null)
            return;

        if (type.equals("group")) {
            setTitle("群组");
        } else if (type.equals("private")) {
            setTitle("我的私人会话");
        } else if (type.equals("discussion")) {
            setTitle("我的讨论组");
        } else if (type.equals("system")) {
            setTitle("系统消息");
        } else {
            setTitle("聊天");
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_rong);
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

    }
}
