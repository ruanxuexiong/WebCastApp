package com.android.nana.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseListViewActivity;
import com.android.nana.R;
import com.android.nana.adapter.VisitorRecordAdapter;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.material.EditDataActivity;
import com.android.nana.util.UIUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class SearchListActivity extends BaseListViewActivity {

    private EditText mEditSearch;
    private TextView mTxtCancel;

    private String mUserId;
    private String mEndTime;

    private String mSearchContent;

    private VisitorRecordAdapter mAdapter;

    private InputMethodManager mInputMethodManager; // 隐藏软键盘

    @Override
    protected VisitorRecordAdapter getBaseJsonAdapter() {
        mAdapter = new VisitorRecordAdapter(this);
        return mAdapter;
    }

    @Override
    protected void initList() {

        mPageIndex = 1;
        if (!TextUtils.isEmpty(mSearchContent)) {

            CustomerDbHelper.queryUserLists(this, mPageIndex, mPageSize, mUserId, mSearchContent, mIOAuthCallBack);
        }
    }

    @Override
    protected void bindViews() {
        mUserId = BaseApplication.getInstance().getCustomerId(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.search_list);
    }

    @Override
    protected void findViewById() {

        mEditSearch = (EditText) findViewById(R.id.search_list_edit_search);
        mEditSearch.setHint("输入姓名、三分钟号码、公司等");
        mTxtCancel = (TextView) findViewById(R.id.search_list_txt_cancel);
    }


    @Override
    protected void initOther() {
        super.initOther();
        hideRefersh();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.grey_e6)));
        mListView.setDividerHeight(UIUtil.getInstance().dip2px(this, 1));
    }

    @Override
    protected void setListener() {

        mTxtCancel.setOnClickListener(mBackPullListener);
        mEditSearch.setFocusable(true);//获取焦点
        mEditSearch.setFocusableInTouchMode(true);
        mEditSearch.requestFocus();

        SearchListActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // 搜索内容
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }

                    mSearchContent = mEditSearch.getText().toString().trim();

                    autoRefresh();
                    return true;
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject obj = (JSONObject) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchListActivity.this, EditDataActivity.class);
                intent.putExtra("UserId", obj.optString("id"));
                startActivity(intent);

            }
        });

    }
}
