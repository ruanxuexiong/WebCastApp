package com.android.nana.customer;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.BaseApplication;
import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.SharedPreferencesUtils;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.ShareFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class ArticleDetailActivity extends BaseActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private TextView mTxtRight;
    private ImageView mDetailIv, mShareIv;


    private TextView mTxtContent, mTxtGood, mTxtCollection;
    private ImageView mIvShare;

    private String mUserArticleId;
    private String mUserId;
    private String mThisUserId;
    private ResultRequestModel mResultDetailModel;
    private TransitionSingleHelper mTransiTion;
    private String mPath, mUserName, info, logo, userid;

    @Override
    protected void bindViews() {

        mUserArticleId = getIntent().getStringExtra("userArticleId");
        mThisUserId = (String) SharedPreferencesUtils.getParameter(ArticleDetailActivity.this, "userId", "");

        if (null != getIntent().getStringExtra("userName") || null != getIntent().getStringExtra("userNicename")) {
            mUserName = getIntent().getStringExtra("userName");
            info = getIntent().getStringExtra("info");
            logo = getIntent().getStringExtra("logo");
            userid = getIntent().getStringExtra("mUserId");
        }

        setContentView(R.layout.article_detail);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mUserId = BaseApplication.getInstance().getCustomerId(this);
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtRight = (TextView) findViewById(R.id.common_txt_right_text);

        if (null != mUserName) {
            mTxtTitle.setText(mUserName + "的相册");
        } else {
            mTxtTitle.setText("我的相册");
        }
        mTxtRight.setText("删除");

        mTxtContent = (TextView) findViewById(R.id.article_detail_txt_content);
        mTxtCollection = (TextView) findViewById(R.id.article_detail_txt_collection);
        mTxtGood = (TextView) findViewById(R.id.article_detail_txt_good);
        mIvShare = (ImageView) findViewById(R.id.article_share);
        mDetailIv = (ImageView) findViewById(R.id.iv_detail_bg);
        mTransiTion = new TransitionManager(ArticleDetailActivity.this).getSingle();

        mDetailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransiTion.startViewerActivity(v, mPath);
            }
        });

    }

    @Override
    protected void init() {

        CustomerDbHelper.queryUserArticleInfo(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                mResultDetailModel = new ResultRequestModel(successJson);

                if (mResultDetailModel.mIsSuccess) {

                    // mTxtTitle.setText(JSONUtil.get(mResultDetailModel.mJsonData, "title", ""));
                    mTxtContent.setText(JSONUtil.get(mResultDetailModel.mJsonData, "content", ""));
                    mTxtGood.setText(JSONUtil.get(mResultDetailModel.mJsonData, "laudCount", ""));
                    try {
                        JSONArray jsonArray = new JSONArray(JSONUtil.get(mResultDetailModel.mJsonData, "userArticlePictures", ""));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mPath = jsonObject.getString("path");
                            ImgLoaderManager.getInstance().showImageView(jsonObject.getString("path"), mDetailIv);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (JSONUtil.get(mResultDetailModel.mJsonData, "userId", "").equals(mThisUserId)) {
                        mTxtRight.setVisibility(View.VISIBLE);
                        mTxtCollection.setVisibility(View.GONE);
                    } else {
                        mTxtCollection.setVisibility(View.VISIBLE);
                        mTxtRight.setVisibility(View.GONE);
                    }
                    if (JSONUtil.get(mResultDetailModel.mJsonData, "collectioned", 0) == 1) {
                        mTxtCollection.setText("已收藏");
                        mTxtCollection.setTextColor(getResources().getColor(R.color.white));
                        mTxtCollection.setBackgroundResource(R.drawable.bule_full_bg_shape);
                    } else {
                        mTxtCollection.setText("收藏");
                        mTxtCollection.setTextColor(getResources().getColor(R.color.main_blue));
                        mTxtCollection.setBackgroundResource(R.drawable.grey_bg_shape);
                    }
                    if (JSONUtil.get(mResultDetailModel.mJsonData, "laudResult", 0) == 1) {
                        mTxtGood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_praise_pre, 0, 0, 0);
                    } else {
                        mTxtGood.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_praise_nor, 0, 0, 0);
                    }

                } else {

                    UIHelper.showToast(ArticleDetailActivity.this, mResultDetailModel.mMessage);
                }
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {

            }
        });

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArticleDetailActivity.this.finish();

            }
        });

        mTxtRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkLogins()) return;

                delete();

            }
        });

        mTxtGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkLogins()) return;

                if (mResultDetailModel == null) return;

                if (JSONUtil.get(mResultDetailModel.mJsonData, "laudResult", 0) == 1) {

                    cancelGood();

                } else {

                    good();

                }

            }
        });

        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != userid) {
                    FragmentManager fm = getSupportFragmentManager();
                    ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User?uid=" + userid + "?thisUid=" + mThisUserId, mUserName, info, logo);
                    dialog.show(fm, "fragment_bottom_dialog");
                } else {
                    String mUserName = (String) SharedPreferencesUtils.getParameter(ArticleDetailActivity.this, "thisUserName", "");
                    String mInfo = (String) SharedPreferencesUtils.getParameter(ArticleDetailActivity.this, "introduce", "");
                    String mLogo = (String) SharedPreferencesUtils.getParameter(ArticleDetailActivity.this, "logo", "");
                    if (!"".equals(mUserName) && !"".equals(mInfo) && !"".equals(mLogo)) {
                        FragmentManager fm = getSupportFragmentManager();
                        ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User?uid=" + mThisUserId + "?thisUid=" + mThisUserId, mUserName, mInfo, mLogo);
                        dialog.show(fm, "fragment_bottom_dialog");
                    }else {
                        ToastUtils.showToast("请检查资料是否完整，编辑资料后重试！");
                    }
                }

            }
        });

        mTxtCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkLogins()) return;

                String text = mTxtCollection.getText().toString();
                if (text.equals("收藏")) {

                    collection();

                } else {

                    cancelCollection();

                }

            }
        });

    }

    private void good() {

        CustomerDbHelper.laudUserArticle(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(ArticleDetailActivity.this, "正在处理，请等待...");

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                if (mResultDetailModel.mIsSuccess) {
                    init();
                } else {

                    UIHelper.showToast(ArticleDetailActivity.this, mResultDetailModel.mMessage);
                }
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {
                UIHelper.showToast(ArticleDetailActivity.this, "获取数据失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();
            }
        });

    }

    private void cancelGood() {

        CustomerDbHelper.cancelLaudUserArticle(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(ArticleDetailActivity.this, "正在处理，请等待...");

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
                if (mResultDetailModel.mIsSuccess) {
                    init();
                } else {

                    UIHelper.showToast(ArticleDetailActivity.this, mResultDetailModel.mMessage);
                }
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {
                UIHelper.showToast(ArticleDetailActivity.this, "获取数据失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();
            }
        });

    }

    private void delete() {

        CustomerDbHelper.deleteUserArticle(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(ArticleDetailActivity.this, "正在处理，请等待...");

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);

                if (mResultDetailModel.mIsSuccess) {
                    setResult(RESULT_OK);
                    finish();
                } else {

                    UIHelper.showToast(ArticleDetailActivity.this, mResultDetailModel.mMessage);
                }
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {
                UIHelper.showToast(ArticleDetailActivity.this, "获取数据失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();
            }
        });

    }

    private void collection() {

        CustomerDbHelper.collectionUserArticle(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(ArticleDetailActivity.this, "正在处理，请等待...");

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);

                if (mResultDetailModel.mIsSuccess) {

                    mTxtCollection.setText("已收藏");
                    mTxtCollection.setTextColor(getResources().getColor(R.color.white));
                    mTxtCollection.setBackgroundResource(R.drawable.bule_full_bg_shape);

                }
                UIHelper.showToast(ArticleDetailActivity.this, "该照片您已收藏");
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {
                UIHelper.showToast(ArticleDetailActivity.this, "获取数据失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();
            }
        });

    }

    private void cancelCollection() {

        CustomerDbHelper.cancelCollectionUserArticle(mUserId, mUserArticleId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

                UIHelper.showOnLoadingDialog(ArticleDetailActivity.this, "正在处理，请等待...");

            }

            @Override
            public void getSuccess(String successJson) {

                ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);

                if (mResultDetailModel.mIsSuccess) {

                    mTxtCollection.setText("收藏");
                    mTxtCollection.setTextColor(getResources().getColor(R.color.main_blue));
                    mTxtCollection.setBackgroundResource(R.drawable.grey_bg_shape);

                }
                UIHelper.showToast(ArticleDetailActivity.this, mResultDetailModel.mMessage);
                UIHelper.hideOnLoadingDialog();

            }

            @Override
            public void getFailue(String failueJson) {
                UIHelper.showToast(ArticleDetailActivity.this, "获取数据失败，请稍后重试");
                UIHelper.hideOnLoadingDialog();
            }
        });

    }

    private boolean checkLogins() {

        if (!BaseApplication.getInstance().checkLogin(this)) {
            startActivity(new Intent(this, com.android.nana.auth.WelcomeActivity.class));
            return false;
        }
        return true;
    }

}
