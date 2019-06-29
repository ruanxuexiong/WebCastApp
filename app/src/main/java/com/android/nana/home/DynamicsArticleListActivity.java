package com.android.nana.home;

import android.annotation.SuppressLint;

import com.android.common.base.BaseJsonAdapter;
import com.android.common.base.BaseListViewActivity;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
@SuppressLint("ValidFragment")
public class DynamicsArticleListActivity extends BaseListViewActivity {
    @Override
    protected BaseJsonAdapter getBaseJsonAdapter() {
        return null;
    }

    @Override
    protected void initList() {

    }

    @Override
    protected void bindViews() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void setListener() {

    }
/*

    private ImageButton mIbBack;
    private TextView mTxtTitle;

    private ArticleAdapter mAdapter;
    private String mEndTime;
    private String mUserId;
    private String mThisUserId;
    private String userName;

    @Override
    protected ArticleAdapter getBaseJsonAdapter() {
        mAdapter = new ArticleAdapter(this,this);
        return mAdapter;
    }

    @Override
    protected void initList() {

        if (mPageIndex != 1 && mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0){
            mEndTime = JSONUtil.get(mAdapter.getList().get(mAdapter.getCount()-1), "addTime","");
        } else {
            mEndTime = "";
        }
      //  CustomerDbHelper.queryUserArticle(mPageIndex, mPageSize, mUserId, mThisUserId, mEndTime, mIOAuthCallBack);

    }

    @Override
    protected void bindViews() {

        mThisUserId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        mUserId = getIntent().getStringExtra("UserId");
        userName = getIntent().getStringExtra("userName");
        setContentView(R.layout.dynamics_article_list);
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText(userName+"的相册");

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DynamicsArticleListActivity.this.finish();

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject object = (JSONObject) parent.getItemAtPosition(position);

                Intent intent = new Intent(DynamicsArticleListActivity.this, ArticleDetailActivity.class);
                intent.putExtra("userArticleId", JSONUtil.get(object,"id",""));
                intent.putExtra("userId", JSONUtil.get(object,"userId",""));
                startActivity(intent);

            }
        });

    }

    @Override
    public void onShareClick() {

    }

    @Override
    public void onCollectionClick(String uid, String thisID) {

    }

    @Override
    public void onDiggClick(String uid, String mArticleId) {

    }
*/

}
