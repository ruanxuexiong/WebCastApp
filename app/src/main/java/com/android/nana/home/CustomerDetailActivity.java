package com.android.nana.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.helper.UIHelper;
import com.android.common.models.ResultRequestModel;
import com.android.common.utils.IOAuthCallBack;
import com.android.common.utils.JSONUtil;
import com.android.nana.R;
import com.android.nana.customer.EducationActivity;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.customer.SpecialtyActivity;
import com.android.nana.customer.UserInfoActivity;
import com.android.nana.dbhelper.CustomerDbHelper;
import com.android.nana.ui.RoundImageView;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * 主播详情
 *
 * @author Administrator
 */
public class CustomerDetailActivity extends BaseActivity {

    private ImageButton mIbBack;
    private RoundImageView mRivHead;
    private ImageView mIvIdenty;

    private TextView mTxtWorkExperMore, mTxtEducationMore, mTxtSpecialtyMore;
    private TextView mTxtName, mTxtInfo, mTxtTitle, mTxtAddress, mTxtAuthentication, mTxtGender;

    // 个人经历，工作经历，教育经历，主播专长
    private ImageView mIvEdit1, mIvEdit2, mIvEdit3, mIvEdit4;

    private LinearLayout mLlWorkExperience, mLlEducationExperience, mLlSpecialty;

    private String mUserId;

    @Override
    protected void bindViews() {

        mUserId = (String) SharedPreferencesUtils.getParameter(this, "userId", "");
        setContentView(R.layout.customer_detail);
    }

    @Override
    protected void findViewById() {

        mIbBack = (ImageButton) findViewById(R.id.customer_detail_btn_back);
        mRivHead = (RoundImageView) findViewById(R.id.customer_detail_iv_head);
        mIvIdenty = (ImageView) findViewById(R.id.customer_detail_iv_identy);

        mTxtAuthentication = (TextView) findViewById(R.id.customer_detail_txt_authentication);

        mTxtName = (TextView) findViewById(R.id.customer_detail_txt_name);
        mTxtGender = (TextView) findViewById(R.id.customer_detail_txt_gender);
        mTxtInfo = (TextView) findViewById(R.id.customer_detail_txt_info);
        mTxtTitle = (TextView) findViewById(R.id.customer_detail_txt_title);
        mTxtAddress = (TextView) findViewById(R.id.customer_detail_txt_address);

        mIvEdit1 = (ImageView) findViewById(R.id.customer_detail_iv_edit1);
        mIvEdit2 = (ImageView) findViewById(R.id.customer_detail_iv_edit2);
        mIvEdit3 = (ImageView) findViewById(R.id.customer_detail_iv_edit3);
        mIvEdit4 = (ImageView) findViewById(R.id.customer_detail_iv_edit4);

        mTxtWorkExperMore = (TextView) findViewById(R.id.customer_detail_txt_work_exper_more);
        mTxtEducationMore = (TextView) findViewById(R.id.customer_detail_txt_education_more);
        mTxtSpecialtyMore = (TextView) findViewById(R.id.customer_detail_txt_specialty_more);

        // 工作经历
        mLlWorkExperience = (LinearLayout) findViewById(R.id.customer_detail_ll_work_experience);
        // 教育经历
        mLlEducationExperience = (LinearLayout) findViewById(R.id.customer_detail_ll_education_experience);
        // 主播专长
        mLlSpecialty = (LinearLayout) findViewById(R.id.customer_detail_ll_specialty);

        Drawable drawable = getResources().getDrawable(R.drawable.icon_app_logo);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mRivHead.setImageDrawable(drawable);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();

        CustomerDbHelper.queryUserInfo(mUserId, mIOAuthCallBack);

    }

    @Override
    protected void setListener() {

        mIbBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                CustomerDetailActivity.this.finish();

            }
        });

        // 身份认证
        mTxtAuthentication.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, IDAuthenticationActivity.class));

            }
        });

        // 个人经历
        mIvEdit1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, UserInfoActivity.class));

            }
        });

        // 工作经历
        mIvEdit2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

               // startActivity(new Intent(CustomerDetailActivity.this, WorkExperienceActivity.class));

            }
        });
        mTxtWorkExperMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

            //    startActivity(new Intent(CustomerDetailActivity.this, WorkExperienceActivity.class));

            }
        });

        // 教育经历
        mIvEdit3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, EducationActivity.class));

            }
        });
        mTxtEducationMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, EducationActivity.class));

            }
        });

        // 主播专长
        mIvEdit4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, SpecialtyActivity.class));

            }
        });
        mTxtSpecialtyMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(CustomerDetailActivity.this, SpecialtyActivity.class));

            }
        });

    }

    private IOAuthCallBack mIOAuthCallBack = new IOAuthCallBack() {

        @Override
        public void onStartRequest() {

            //  UIHelper.showOnLoadingDialog(CustomerDetailActivity.this, "正在加载，请等待...");

        }

        @Override
        public void getSuccess(String successJson) {

            ResultRequestModel mResultDetailModel = new ResultRequestModel(successJson);
            if (mResultDetailModel.mIsSuccess) {

                JSONObject object = mResultDetailModel.mJsonData;

                // 头像

                Log.e("返回值===", JSONUtil.get(object, "avatar", ""));
                if (!"http://119.23.216.45".equals(JSONUtil.get(object, "avatar", ""))) {
                    new ImgLoaderManager(R.drawable.icon_default_header).showImageView(JSONUtil.get(object, "avatar", ""), mRivHead);
                    SharedPreferencesUtils.setParameter(CustomerDetailActivity.this, "logo", JSONUtil.get(object, "avatar", ""));
                }

                // 认证身份
                String state = JSONUtil.get(object, "status", "");
                if (state.equals("1")) {
                    mIvIdenty.setVisibility(View.VISIBLE);
                } else {
                    mIvIdenty.setVisibility(View.GONE);
                }

                // 信息
                //  mTxtName.setText(JSONUtil.get(object, "user_nicename", ""));

                if (!"null".equals(JSONUtil.get(object, "username", "")) && !"null".equals(JSONUtil.get(object, "user_nicename", ""))) {
                    mTxtName.setText(JSONUtil.get(object, "username", "") + "(" + JSONUtil.get(object, "user_nicename", "") + ")");
                } else if (!"null".equals(JSONUtil.get(object, "username", ""))) {
                    mTxtName.setText(JSONUtil.get(object, "username", ""));
                } else if (!"null".equals(JSONUtil.get(object, "user_nicename", ""))) {
                    mTxtName.setText("(" + JSONUtil.get(object, "user_nicename", "") + ")");
                }
                SharedPreferencesUtils.setParameter(CustomerDetailActivity.this, "thisUserName", JSONUtil.get(object, "username", ""));
                String gender = JSONUtil.get(object, "sex", "");
                if (gender.equals("2")) {
                    mTxtGender.setTextColor(getResources().getColor(R.color.red));
                    mTxtGender.setVisibility(View.VISIBLE);
                } else if (gender.equals("1")) {
                    mTxtGender.setTextColor(getResources().getColor(R.color.bule_42));
                    mTxtGender.setVisibility(View.VISIBLE);
                } else if (gender.equals("0")) {
                    mTxtGender.setVisibility(View.GONE);
                }
                mTxtGender.setText(gender.equals("0") ? "保密" : gender.equals("1") ? "男" : "女");
                String introduce = JSONUtil.get(object, "introduce", "");
                if (!TextUtils.isEmpty(introduce)) {
                    mTxtInfo.setText("个人介绍：" + introduce);
                    SharedPreferencesUtils.setParameter(CustomerDetailActivity.this, "introduce", mTxtInfo.getText().toString());
                    mTxtInfo.setVisibility(View.VISIBLE);
                } else {
                    mTxtInfo.setVisibility(View.GONE);
                }
                mTxtTitle.setText(JSONUtil.get(object, "title", ""));

                String province = JSONUtil.get(object, "province", "");
                String city = JSONUtil.get(object, "city", "");
                String district = JSONUtil.get(object, "district", "");
                if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(district)) {
                    mTxtAddress.setText(province + "," + city + "," + district);
                    mTxtAddress.setVisibility(View.VISIBLE);
                } else {
                    mTxtAddress.setVisibility(View.GONE);
                }

                // 工作经历
                mLlWorkExperience.removeAllViews();
                List<JSONObject> workHistorys = JSONUtil.getList(object, "workHistorys");
          /*      for (int i = 0; i < workHistorys.size(); i++) {
                    JSONObject workHistory = workHistorys.get(i);

                    View mItem = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.work_experience_item, null);
                    ImageView mIvPicture = (ImageView) mItem.findViewById(R.id.we_item_iv_picture);
                    TextView mTxtName = (TextView) mItem.findViewById(R.id.we_item_txt_name);
                    TextView mTxtInfo = (TextView) mItem.findViewById(R.id.we_item_txt_info);
                    TextView mTxtDesc = (TextView) mItem.findViewById(R.id.we_item_txt_desc);
                    TextView mTxtText = (TextView) mItem.findViewById(R.id.we_item_txt_text);
                    mTxtText.setVisibility(View.GONE);

                    Drawable drawable = getResources().getDrawable(R.drawable.dynamic_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mIvPicture.setImageDrawable(drawable);

                    Log.e("picture===", JSONUtil.get(workHistory, "picture", ""));
                    if (!"http://119.23.216.45".equals(JSONUtil.get(workHistory, "picture", "").toString().trim())) {
                        ImgLoaderManager.getInstance().showImageView(JSONUtil.get(workHistory, "picture", ""), mIvPicture);
                    }
                    mTxtName.setText(JSONUtil.get(workHistory, "name", ""));
                    mTxtInfo.setText(JSONUtil.get(workHistory, "position", "") + " | " + JSONUtil.get(workHistory, "introduce", ""));
                    mTxtDesc.setText(JSONUtil.get(workHistory, "beginTime", "") + " - " + JSONUtil.get(workHistory, "endTime", ""));

                    mItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {


                        }
                    });
                    mLlWorkExperience.addView(mItem);
                }*/

                if (workHistorys.size() == 0) mTxtWorkExperMore.setVisibility(View.GONE);

                // 教育经历
                mLlEducationExperience.removeAllViews();
                List<JSONObject> educationExperiences = JSONUtil.getList(object, "educationExperiences");
          /*      for (int i = 0; i < educationExperiences.size(); i++) {
                    JSONObject educationExperience = educationExperiences.get(i);

                    View mItem = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.work_experience_item, null);
                    ImageView mIvPicture = (ImageView) mItem.findViewById(R.id.we_item_iv_picture);
                    TextView mTxtName = (TextView) mItem.findViewById(R.id.we_item_txt_name);
                    TextView mTxtDesc = (TextView) mItem.findViewById(R.id.we_item_txt_desc);
                    TextView mTxtInfo = (TextView) mItem.findViewById(R.id.we_item_txt_info);
                    TextView mTxtText = (TextView) mItem.findViewById(R.id.we_item_txt_text);
                    mTxtText.setVisibility(View.GONE);

                    Drawable drawable = getResources().getDrawable(R.drawable.dynamic_bg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mIvPicture.setImageDrawable(drawable);

                    if (!"http://119.23.216.45".equals(JSONUtil.get(educationExperience, "picture", ""))) {
                        ImgLoaderManager.getInstance().showImageView(JSONUtil.get(educationExperience, "picture", ""), mIvPicture);
                    }

                    mTxtName.setText(JSONUtil.get(educationExperience, "name", ""));
                    mTxtInfo.setText(JSONUtil.get(educationExperience, "major", ""));
                    mTxtDesc.setText(JSONUtil.get(educationExperience, "beginTime", "") + " - " + JSONUtil.get(educationExperience, "endTime", ""));

                    mItem.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {


                        }
                    });
                    mLlEducationExperience.addView(mItem);
                }*/

                if (educationExperiences.size() == 0) mTxtEducationMore.setVisibility(View.GONE);

                // 主播专长
                mLlSpecialty.removeAllViews();
                List<JSONObject> personalExpertises = JSONUtil.getList(object, "personalExpertises");
                if (null != personalExpertises) {
                    for (int i = 0; i < personalExpertises.size(); i++) {
                        JSONObject personalExpertise = personalExpertises.get(i);
                        View mItem = LayoutInflater.from(CustomerDetailActivity.this).inflate(R.layout.specialty_item, null);
                        ImageView mIvPicture = (ImageView) mItem.findViewById(R.id.specialty_item_iv_picture1);
                        TextView mTxtName = (TextView) mItem.findViewById(R.id.specialty_item_txt_name);

                        JSONObject parentColumn = JSONUtil.getJsonObject(personalExpertise, "parentColumn");
                        String picture = JSONUtil.get(parentColumn, "picture", "");
                        ImgLoaderManager.getInstance().showImageView(picture, mIvPicture);

                        StringBuffer content = new StringBuffer();
                        content.append(JSONUtil.get(parentColumn, "name", "") + " - ");
                        List<JSONObject> columns = JSONUtil.getList(personalExpertise, "columns");
                        if (null != columns) {
                            for (int k = 0; k < columns.size(); k++) {
                                String name = JSONUtil.get(columns.get(k), "name", "");
                                content.append(name);
                                if (k != columns.size() - 1) content.append("、");
                            }
                        }
                        mTxtName.setText(content.toString());

                        mItem.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {


                            }
                        });
                        mLlSpecialty.addView(mItem);
                    }
                }

                if (personalExpertises.size() == 0) mTxtSpecialtyMore.setVisibility(View.GONE);

            } else {

                UIHelper.showToast(CustomerDetailActivity.this, mResultDetailModel.mMessage);
            }
            UIHelper.hideOnLoadingDialog();

        }

        @Override
        public void getFailue(String failueJson) {
            UIHelper.showToast(CustomerDetailActivity.this, "获取数据失败，请稍后重试");
            UIHelper.hideOnLoadingDialog();

        }
    };

}
