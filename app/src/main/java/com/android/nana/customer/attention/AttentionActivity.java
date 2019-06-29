package com.android.nana.customer.attention;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.common.base.BaseFragmentActivity;
import com.android.common.builder.FragmentBuilder;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.ImgLoaderManager;
import com.android.nana.R;
import com.android.nana.bean.UserInfo;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class AttentionActivity extends BaseFragmentActivity {

    private ImageButton mIbBack;
    private TextView mTxtTitle;
    private RadioGroup mTabRg;
    private FragmentBuilder mFragmentBuilder;

    private RoundImageView mIvPicture;
    private ImageView mIvIdenty;
    private TextView mTxtName, mTxtInfo;
    private String mUserId;
    private String mEndTime = "";

    @Override
    protected void bindViews() {

        setContentView(R.layout.attention);

    }

    @Override
    protected void locationData() {

     /*   AttentionListener.getInstance().mOnAttentionListener = new AttentionListener.OnAttentionListener() {
            @Override
            public void result(String t, String s) {



            }
        };*/

    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.common_btn_back);
        mTxtTitle = (TextView) findViewById(R.id.common_txt_title);
        mTxtTitle.setText("关注");

        mIvPicture = (RoundImageView) findViewById(R.id.attention_iv_picture);
        mIvIdenty = (ImageView) findViewById(R.id.attention_iv_identy);
        mTxtName = (TextView) findViewById(R.id.attention_txt_name);
        mTxtInfo = (TextView) findViewById(R.id.attention_txt_info);
        mTabRg = (RadioGroup) findViewById(R.id.attention_tab_rg_menu);

        mUserId = (String) SharedPreferencesUtils.getParameter(AttentionActivity.this, "userId", "");

        CustomerDbHelper.queryAttentionUserLists(1, 1, mEndTime, mUserId, "", "", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        mTxtInfo.setText("我关注的：" + jsonObject.getString("myAttentionUserCount") + "，关注我的：" + jsonObject.getString("attentionMyUserCount"));
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
    protected void init() {

        UserInfo userInfo = (UserInfo) SharedPreferencesUtils.getObject(this, "userInfo", UserInfo.class);
        ImgLoaderManager.getInstance(R.drawable.icon_default_header).showImageView(userInfo.getAvatar(), mIvPicture);
        mTxtName.setText(userInfo.getUsername());
        String state = userInfo.getStatus(); // 0待审核  1审核通过  2审核未通过
        if (state.equals("1")) {
            mIvIdenty.setVisibility(View.VISIBLE);
        } else {
            mIvIdenty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initFragments() {

        mFragmentBuilder = new FragmentBuilder(this, R.id.attention_tab_content);
        mFragmentBuilder.registerFragement("我关注的", AttentionFragment.newInstance("MY_ATTENTION"));
        mFragmentBuilder.registerFragement("关注我的", AttentionFragment.newInstance("ATTENTION_MINE"));

        mFragmentBuilder.switchFragment(0);

    }

    @Override
    protected void setListener() {

        mTabRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.attention_tab_rb_1:
                        mFragmentBuilder.switchFragment(0);
                        break;
                    case R.id.attention_tab_rb_2:
                        mFragmentBuilder.switchFragment(1);
                        break;
                }
            }
        });

        mIbBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AttentionActivity.this.finish();

            }
        });

    }

}
