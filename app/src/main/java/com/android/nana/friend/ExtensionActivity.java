package com.android.nana.friend;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.nana.R;
import com.android.nana.eventBus.ExtensionEvent;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2019/1/16.
 */

public class ExtensionActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv, mRightTv;
    private TextView mBack;
    private EditText mTextEt;
    private LinearLayout mDownloadLl, mSearchLl, mDynamicSearchLl, mDynamicArticleLl;
    private CheckBox mDownloadBox, mSearchBox, mDynamicBox, mArticleBox;
    private String mType = "1";//默认下载

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_extension);
    }

    @Override
    protected void findViewById() {
        mTextEt = findViewById(R.id.et_text);
        mBack = findViewById(R.id.iv_toolbar_back);
        mTitleTv = findViewById(R.id.tv_title);
        mRightTv = findViewById(R.id.toolbar_right_2);
        mDownloadLl = findViewById(R.id.ll_download);
        mSearchLl = findViewById(R.id.ll_search);
        mDownloadBox = findViewById(R.id.checkBox_download);
        mSearchBox = findViewById(R.id.checkBox_share);

        mDynamicBox = findViewById(R.id.checkBox_dynamic);
        mDynamicSearchLl = findViewById(R.id.ll_dynamic_search);
        mDynamicArticleLl = findViewById(R.id.ll_dynamic_article);
        mArticleBox = findViewById(R.id.checkBox_article);
    }

    @Override
    protected void init() {
        mTitleTv.setText("推广链接");
        mRightTv.setText("保存");
        mBack.setVisibility(View.VISIBLE);
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setVisibility(View.VISIBLE);
        Utils.showSoftInputFromWindow(ExtensionActivity.this, mTextEt);
        if (null != getIntent().getStringExtra("extension") && !"".equals(getIntent().getStringExtra("extension"))) {
            mTextEt.setText(getIntent().getStringExtra("extension"));
        }
    }

    @Override
    protected void setListener() {
        mRightTv.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mDownloadLl.setOnClickListener(this);
        mSearchLl.setOnClickListener(this);
        mDynamicSearchLl.setOnClickListener(this);
        mDynamicArticleLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_2:
                String mTextStr = mTextEt.getText().toString().trim();
                if (TextUtils.isEmpty(mTextStr)) {
                    ToastUtils.showToast("请填写推广地址!");
                    return;
                }
                EventBus.getDefault().post(new ExtensionEvent(mTextStr, mType));
                this.finish();
                break;
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_download:
                mDownloadBox.setChecked(true);
                mSearchBox.setChecked(false);
                mSearchLl.setBackground(null);
                mDynamicBox.setChecked(false);
                mDynamicSearchLl.setBackground(null);
                mArticleBox.setChecked(false);
                mDynamicArticleLl.setBackground(null);
                mDownloadLl.setBackgroundResource(R.drawable.icon_selection);
                mType = "1";
                break;
            case R.id.ll_search:
                mSearchBox.setChecked(true);
                mDownloadBox.setChecked(false);
                mDownloadLl.setBackground(null);
                mDynamicBox.setChecked(false);
                mDynamicSearchLl.setBackground(null);
                mArticleBox.setChecked(false);
                mDynamicArticleLl.setBackground(null);
                mSearchLl.setBackgroundResource(R.drawable.icon_selection);
                mType = "2";
                break;
            case R.id.ll_dynamic_search:
                mDynamicBox.setChecked(true);
                mDownloadBox.setChecked(false);
                mSearchBox.setChecked(false);
                mDownloadLl.setBackground(null);
                mSearchLl.setBackground(null);

                mArticleBox.setChecked(false);
                mDynamicArticleLl.setBackground(null);
                mDynamicSearchLl.setBackgroundResource(R.drawable.icon_selection);
                mType = "3";
                break;
            case R.id.ll_dynamic_article:
                mArticleBox.setChecked(true);
                mDynamicArticleLl.setBackgroundResource(R.drawable.icon_selection);
                mDownloadBox.setChecked(false);
                mSearchBox.setChecked(false);
                mDownloadLl.setBackground(null);
                mSearchLl.setBackground(null);
                mDynamicBox.setChecked(false);
                mDynamicSearchLl.setBackground(null);
                mType = "4";
                break;
            default:
                break;
        }
    }


}
