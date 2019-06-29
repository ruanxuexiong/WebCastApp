package com.android.nana.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseRequestFragment;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.WebCastApplication;
import com.android.nana.activity.MainCreateActivity;
import com.android.nana.card.CardMainActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.dbhelper.FriendDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.friend.FriendActivity;
import com.android.nana.job.MainJobActivity;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/8/29.
 */

public class FindFragment extends BaseRequestFragment implements View.OnClickListener {

    private RelativeLayout mNavigationRl, mGroupRl, mFriendRl, mBaiduRl;
    private TextView mTitleTv;
    private TextView mCountTv;
    private RoundImageView mAvatarIv;
    private RelativeLayout mAvatarrl;
    private ImageView mImgIv;//红点
    private RelativeLayout mAvatarRl;
    private RelativeLayout mCardRl;
    private RelativeLayout mRecruitll;

    private String mid;
    private HtmlBean bean;
    private ImageView mLogoIv;
    private RelativeLayout mSanRl;
    private TextView mFansTitleTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(FindFragment.this)) {
            EventBus.getDefault().register(FindFragment.this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    protected void findViewById() {
        mGroupRl = (RelativeLayout) findViewById(R.id.rl_group);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mFriendRl = (RelativeLayout) findViewById(R.id.rl_friend);
        mNavigationRl = (RelativeLayout) findViewById(R.id.rl_navigation);

        mCountTv = (TextView) findViewById(R.id.tv_count);
        mImgIv = (ImageView) findViewById(R.id.iv_avatar_img);
        mAvatarrl = (RelativeLayout) findViewById(R.id.avatar_rl);
        mAvatarIv = (RoundImageView) findViewById(R.id.iv_avatar);
        mBaiduRl = (RelativeLayout) findViewById(R.id.rl_baidu);
        mAvatarRl = (RelativeLayout) findViewById(R.id.avatar_rl);
        mCardRl = (RelativeLayout) findViewById(R.id.rl_card);

        mRecruitll = (RelativeLayout) findViewById(R.id.rl_recruit);

        mLogoIv = (ImageView) findViewById(R.id.iv_logo);
        mSanRl = (RelativeLayout) findViewById(R.id.rl_san);
        mFansTitleTv = (TextView) findViewById(R.id.tv_fans_title);
    }

    @Override
    protected void init() {
        mTitleTv.setText("发现");
        mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
        initWeb(mid);
    }

    private void initWeb(String mid) {
        CustomerDbHelper.AdvisoryHtmlUrl(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        bean = parseHtmlData(successJson);
                        ImageLoader.getInstance().displayImage(bean.getLogo(), mLogoIv);
                        mFansTitleTv.setText(bean.getFindText());
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


    @Override
    protected void setListener() {
        mGroupRl.setOnClickListener(this);
        mNavigationRl.setOnClickListener(this);
        mFriendRl.setOnClickListener(this);
        mBaiduRl.setOnClickListener(this);
        mCardRl.setOnClickListener(this);
        mRecruitll.setOnClickListener(this);
        mSanRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rl_group:
                startActivity(new Intent(getActivity(), MainCreateActivity.class));
                break;
            case R.id.rl_friend://朋友圈
                startActivity(new Intent(getActivity(), FriendActivity.class));
                break;
            case R.id.rl_navigation:
              /*  Intent intent = new Intent(getActivity(), CategoryListActivity.class);
                intent.putExtra("Name", "全部");
                intent.putExtra("CategoryId", "1");
                intent.putExtra("ChildCategoryId", "0");
                startActivity(intent);*/
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_baidu:
                startActivity(new Intent(getContext(), BaiduWebViewActivity.class));
                break;
            case R.id.rl_card:
                startActivity(new Intent(getContext(), CardMainActivity.class));
                break;
            case R.id.rl_recruit:
                startActivity(new Intent(getContext(), MainJobActivity.class));
                break;
            case R.id.rl_san:
                Intent web = new Intent(getActivity(), BaiduWebViewActivity.class);
                web.putExtra("knowledge", bean.getFaceThreeKnowledge());
                web.putExtra("findText", bean.getFindText());
                web.putExtra("mid", mid);
                startActivity(web);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(FindFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpMsgdate(MessageEvent msg) {

        if ("FindMsg".equals(msg.message)) {//朋友圈评论显示
            String mid = (String) SharedPreferencesUtils.getParameter(getActivity(), "userId", "");
            initData(mid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

         initData(mid);
    }

    private void initData(String mid) {

        FriendDbHelper.newMoments(mid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    if (result.getString("state").equals("0")) {
                        JSONObject msg = new JSONObject(data.getString("message"));
                        if (Integer.valueOf(msg.getString("count")) > 0) {
                            mCountTv.setVisibility(View.VISIBLE);
                            mAvatarrl.setVisibility(View.VISIBLE);
                            mCountTv.setText(msg.getString("count"));
                            mAvatarIv.setVisibility(View.VISIBLE);

                            ImgLoaderManager.getInstance().showImageView(msg.getString("avatar"), mAvatarIv);
                        } else if (!"".equals(SharedPreferencesUtils.getParameter(getActivity(), "avatar", ""))) {
                            String avatar = (String) SharedPreferencesUtils.getParameter(getActivity(), "avatar", "");
                            mAvatarRl.setVisibility(View.VISIBLE);
                            mImgIv.setVisibility(View.VISIBLE);
                            mAvatarIv.setVisibility(View.VISIBLE);
                            ImgLoaderManager.getInstance().showImageView(avatar, mAvatarIv);
                        } else {
                            mCountTv.setVisibility(View.GONE);
                            mAvatarrl.setVisibility(View.GONE);
                        }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {//fragment 可见时操作
            if (!"".equals(SharedPreferencesUtils.getParameter(getActivity(), "avatar", ""))) {
                String avatar = (String) SharedPreferencesUtils.getParameter(getActivity(), "avatar", "");
                mAvatarRl.setVisibility(View.VISIBLE);
                mImgIv.setVisibility(View.VISIBLE);
                ImgLoaderManager.getInstance().showImageView(avatar, mAvatarIv);
            }
        } else {//不可见时操作

        }
    }

    public HtmlBean parseHtmlData(String result) {//Gson 解析
        HtmlBean detail = new HtmlBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            detail = gson.fromJson(data.toString(), HtmlBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
