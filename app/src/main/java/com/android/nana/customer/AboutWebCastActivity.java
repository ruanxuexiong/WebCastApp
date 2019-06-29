package com.android.nana.customer;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.StringHelper;
import com.android.nana.R;
import com.android.nana.ui.UITableView;
import com.android.nana.webview.JumpWebViewActivity;
import com.tencent.bugly.beta.Beta;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class AboutWebCastActivity extends BaseActivity {

    private TextView mIbBack;
    private TextView mTxtTitle, mTxtVersionName;

    private String mVersionName;

    @Override
    protected void bindViews() {

        mVersionName = StringHelper.getVersionName(this);
        setContentView(R.layout.about_webcast);

    }

    @Override
    protected void findViewById() {

        mIbBack =  findViewById(R.id.iv_toolbar_back);
        mTxtTitle =  findViewById(R.id.tv_title);
        mTxtTitle.setText("关于哪哪");
        mIbBack.setVisibility(View.VISIBLE);
        mTxtVersionName =  findViewById(R.id.about_webcast_txt_version);
        mTxtVersionName.setText("哪哪" + mVersionName);

        UITableView table = findViewById(R.id.about_webcast_ll_tableview);
        table.addBasicItem(0, "功能介绍", "");
        table.addBasicItem(0, "系统通知", "");
        table.addBasicItem(0, "我要反馈", "");
        table.addBasicItem(0, "投诉", "");
        table.addBasicItem(0, "更新版本", mVersionName);
        table.setOnUITableClickLister(new UITableView.UITableClickLister() {
            @Override
            public void onClick(int index) {

                switch (index) {
                    case 0: // 功能介绍
                        startJumpActivity(JumpWebViewActivity.class, "功能介绍", "6");
                        break;
                    case 1: // 系统通知
                        startJumpActivity(JumpWebViewActivity.class, "系统通知", "7");
                        break;
                    case 2: // 我要反馈
                        startJumpActivity(FeedbackComplaintsActivity.class, "我要反馈", "");
                        break;
                    case 3: // 投诉
                        startJumpActivity(JumpWebViewActivity.class, "投诉", "8");
                        break;
                    case 4: // 更新版本
                        //  CustomerDbHelper.queryVersion(mIOAuthCallBack);
                        Beta.checkUpgrade();
                        break;
                }
            }
        });
        table.builder();

    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(mBackPullListener);

    }

    private void startJumpActivity(Class<?> clx, String title, String termId) {

        Intent intent = new Intent(AboutWebCastActivity.this, clx);
        intent.putExtra("Title", title);
        intent.putExtra("TermId", termId);
        startActivity(intent);
    }

}
