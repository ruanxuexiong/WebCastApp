package com.android.nana.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.common.base.BaseActivity;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.alertview.AlertView;
import com.android.nana.alertview.OnDismissListener;
import com.android.nana.alertview.OnItemClickListener;
import com.android.nana.customer.EditWorkActivity;
import com.android.nana.customer.EducationListActivity;
import com.android.nana.customer.IDAuthenticationActivity;
import com.android.nana.dbhelper.FunctionDbHelper;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.eventBus.DleLabelEvent;
import com.android.nana.eventBus.FunctionEvent;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.friend.WholeAlbumActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.ToastUtils;
import com.android.nana.wanted.JobActivity;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.LabelsView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINK on 2017/6/27.
 */

public class EditPersonalActivity extends BaseActivity implements View.OnClickListener, OnDismissListener, OnItemClickListener {


    private LinearLayout mWorkLL, mWorkExperienceLL, mLabelExperienceLL, mEducationLL;
    private String mUid, mjobId, status;


    ArrayList<String> label = new ArrayList<>();
    private String mParentName, mLabelName;
    private RoundedImageView mAvatar;
    private ImageView mEditIv, mGradeIv, mBackgrounIv, mIdentyIv;
    private TextView mNameTv, mEditTv, mPositionTv, mAddressTv, mInfoTv, mAddJobTv, mAddStartJobTv, mEditWorkTv, mEditEducationTv, mEditLabelTv, mMoreTv, mEducationTv, mEducationMoerTv;
    private List<JSONObject> job;
    private String cityId, salary, jobStr, proStr, mName, mUild;
    private LinearLayout mEducationMoreLl, mMoreLl;
    private View mEducationMoreView, mMoreView;

    private AlertView mAlertView;
    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mNumTv;
    private TextView mGenderTv;
    private TextView mMoneyTv;
    private String mFaceModeStr;//三分钟模式状态
    private ImageView mGenderIv;
    private LinearLayout mGroupLl;
    private String mNameStr, mLogo, mHead;
    private RelativeLayout mNavigationFriend;
    private LinearLayout mAlbumDataLL, mAlbumLL;
    private TextView mAlbumTv;
    private RelativeLayout mNavigationShare, mNavigationFollow, mNavigationMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        if (!EventBus.getDefault().isRegistered(EditPersonalActivity.this)) {
            EventBus.getDefault().register(EditPersonalActivity.this);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_edit_persional);
    }

    @Override
    protected void findViewById() {
        mInfoTv = findViewById(R.id.tv_introduce);
        mEditTv = findViewById(R.id.iv_edit);
        mNameTv = findViewById(R.id.tv_name);

        mAddressTv = findViewById(R.id.tv_address);
        mPositionTv = findViewById(R.id.tv_position);
        mBackgrounIv = findViewById(R.id.customer_detail_iv_top);
        mAvatar = findViewById(R.id.iv_head);

        mGenderIv = findViewById(R.id.iv_sex);
        mWorkLL = findViewById(R.id.ll_work);
        mWorkExperienceLL = findViewById(R.id.anchor_detail_ll_work_experience);
        mEditWorkTv = findViewById(R.id.iv_edit_work);
        mEditEducationTv = findViewById(R.id.iv_edit_education);
        mEditLabelTv = findViewById(R.id.iv_edit_label);

        mMoreTv = findViewById(R.id.tv_more);
        mMoreLl = findViewById(R.id.ll_more);
        mMoreView = findViewById(R.id.view_more);
        mEducationMoerTv = findViewById(R.id.tv_education_more);
        mEducationMoreLl = findViewById(R.id.ll_education_more);
        mEducationMoreView = findViewById(R.id.view_education_more);
        mEducationTv = findViewById(R.id.iv_edit_education);
        mLabelExperienceLL = findViewById(R.id.anchor_detail_ll_label_experience);
        mAlbumTv = findViewById(R.id.tv_album);
        mEducationLL = findViewById(R.id.anchor_detail_ll_education_experience);
        mIdentyIv = findViewById(R.id.iv_identy);

        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mNumTv = findViewById(R.id.tv_num);
        mMoneyTv = findViewById(R.id.tv_money);
        mGenderTv = findViewById(R.id.tv_gender);
        mGroupLl = findViewById(R.id.ll_group);

        mNavigationShare = findViewById(R.id.navigation_share);
        mNavigationFriend = findViewById(R.id.navigation_friend);
        mNavigationFollow = findViewById(R.id.navigation_follow);
        mNavigationMore = findViewById(R.id.navigation_more);

        mAlbumLL = findViewById(R.id.ll_album);
        mAlbumDataLL = findViewById(R.id.ll_album_data);
    }

    @Override
    protected void init() {
        mTitleTv.setText("编辑资料");
        mBackTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle.getString("thisUid")) {
            mUid = bundle.getString("thisUid");
        }
        if (null != mUid) {
            initData(mUid);
        }
    }

    private void initData(final String mUid) {

        WebCastDbHelper.getUserInfo(mUid, mUid, "2", new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject json = new JSONObject(successJson);
                    JSONObject result = new JSONObject(json.getString("result"));
                    if (result.getString("state").equals("0")) {
                        MeDataBean item = parseData(successJson);

                        mNameTv.setText(item.getUsername());
                        mNameStr = item.getUsername();
                        mBackgrounIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(item.getBackgroundImage(), mBackgrounIv);
                        if (!"".equals(item.getPosition()) && !"".equals(item.getCompany())) {
                            mPositionTv.setText(item.getPosition() + " | " + item.getCompany());
                        } else {
                            mPositionTv.setVisibility(View.GONE);
                        }
                        if (item.getStatus().equals("1")) {
                            status = item.getStatus();
                            mIdentyIv.setVisibility(View.VISIBLE);
                        } else {
                            mIdentyIv.setVisibility(View.GONE);
                            status = item.getStatus();
                        }

                        if (!"".equals(item.getAvatar())) {
                            mLogo = item.getAvatar();

                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.icon_head_default)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true);

                            Glide.with(EditPersonalActivity.this)
                                    .asBitmap()
                                    .load(item.getAvatar())
                                    .apply(options)
                                    .into(mAvatar);
                        } else {
                            mLogo = "http://www.facethree.com/themes/simplebootx/Public/images/zblogo.png";
                        }

                        if ("".equals(item.getAvatar())) {
                            mHead = "http://www.facethree.com/data/upload/share/person.png";
                        } else {
                            mHead = item.getAvatar();
                        }
                        if (!"".equals(item.getIdcard())) {
                            mNumTv.setText("ID" + item.getIdcard());
                        }

                        if (item.getSex().equals("1")) {
                            mGenderIv.setImageDrawable(getDrawable(R.drawable.icon_sex_male));
                        } else if (item.getSex().equals("2")) {
                            mGenderIv.setImageDrawable(getDrawable(R.drawable.icon_sex_female));
                        } else {
                            mGenderIv.setVisibility(View.GONE);
                        }


                        if (null != item.getProvince() && null != item.getDistrict() && null != item.getCity()) {
                            mAddressTv.setText("常居：" + item.getProvince() + "·" + item.getCity() + "·" + item.getDistrict());
                        } else {
                            mAddressTv.setVisibility(View.GONE);
                        }

                        if (!"".equals(item.getIntroduce())) {
                            mInfoTv.setText("简介：" + item.getIntroduce());
                        } else {
                            mInfoTv.setText("简介：暂无简介");
                        }

                        if (!"".equals(item.getMoney_time())) {
                            mMoneyTv.setText("收费：" + item.getCharge() + "/分钟" + "（前3分钟免费）");
                        }


                        mFaceModeStr = item.getFaceMode();//三分钟模式
                        switch (mFaceModeStr) {
                            case "1":
                                break;
                            case "2":
                                mMoneyTv.setVisibility(View.GONE);
                                mMoneyTv.setText("见面金额：由对方提出合理的见面金额");
                                break;
                            case "3":
                                mMoneyTv.setVisibility(View.GONE);//显示金额
                                break;
                            case "4":
                                mMoneyTv.setVisibility(View.GONE);//显示金额
                                break;
                            case "5"://未开启三分钟模式+非好友
                                mMoneyTv.setVisibility(View.GONE);//显示金额
                                break;
                            default:
                                break;
                        }

                        //工作经历
                        if (item.getWorkHistorys().size() > 0) {
                            mWorkLL.setVisibility(View.VISIBLE);
                            mEditWorkTv.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.setClickable(false);
                            mWorkExperienceLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.removeAllViews();
                            mMoreTv.setVisibility(View.VISIBLE);
                            mMoreLl.setVisibility(View.VISIBLE);
                            mMoreView.setVisibility(View.VISIBLE);
                            for (int i = 0; i < item.getWorkHistorys().size(); i++) {
                                final MeDataBean.WorkHistorys mItem = item.getWorkHistorys().get(i);
                                View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.work_experience_item, null);
                                LinearLayout content = mView.findViewById(R.id.content);
                                TextView mTitle = mView.findViewById(R.id.tv_title);
                                TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                TextView mTimeTv = mView.findViewById(R.id.tv_time);

                                new ImgLoaderManager(R.drawable.icon_work).showImageView(mItem.getPicture(), mPicture);
                                mTitle.setText(mItem.getName());
                                mCompanyTv.setText(mItem.getPosition());
                                if ("".equals(mItem.getBeginTime())) {
                                    mTimeTv.setTextColor(getResources().getColor(R.color.gules));
                                    mTimeTv.setText("起止日期和详细描述待完善");
                                } else {
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());
                                }
                                mWorkExperienceLL.addView(mView);

                              /*  mMoreTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(EditPersonalActivity.this, WorkExperienceActivity.class);
                                        intent.putExtra("IsHideEdit", true);
                                        intent.putExtra("userId", mUid);
                                        startActivity(intent);
                                    }
                                });*/

                                content.setOnClickListener(new View.OnClickListener() {//点击某个item进详情
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(EditPersonalActivity.this, AddWorkActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("item", mItem);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });

                            }

                        } else {
                            mMoreLl.setVisibility(View.GONE);
                            mMoreTv.setVisibility(View.GONE);
                            mMoreView.setVisibility(View.GONE);
                            mEditWorkTv.setVisibility(View.GONE);

                            mWorkExperienceLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.setClickable(true);
                            mWorkExperienceLL.removeAllViews();
                            View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.item_work_experience, null);
                            LinearLayout mContentLl = mView.findViewById(R.id.ll_content);
                            mContentLl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(EditPersonalActivity.this, AddWorkActivity.class);
                                    intent.putExtra("add", "add");
                                    startActivity(intent);
                                }
                            });
                            mWorkExperienceLL.addView(mView);
                        }

                        //教育经历
                        if (item.getEducationExperiences().size() > 0) {
                            mEducationTv.setVisibility(View.VISIBLE);
                            mEducationLL.setClickable(false);
                            mEducationLL.removeAllViews();
                            if (item.getEducationExperiences().size() > 2) {
                                mEducationMoerTv.setVisibility(View.VISIBLE);
                                mEducationMoreLl.setVisibility(View.VISIBLE);
                                mEducationMoreView.setVisibility(View.VISIBLE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {

                                    final MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                    LinearLayout content = mView.findViewById(R.id.content);

                                    new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);

                                    mEducationMoreLl.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(EditPersonalActivity.this, EditEducationActivity.class);
                                            intent.putExtra("add", "add");
                                            startActivity(intent);
                                        }
                                    });
                                    content.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent intent = new Intent(EditPersonalActivity.this, EditEducationActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("item", mItem);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mEducationMoerTv.setVisibility(View.VISIBLE);
                                mEducationMoreLl.setVisibility(View.VISIBLE);
                                mEducationMoreView.setVisibility(View.GONE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {
                                    final MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);
                                    LinearLayout content = mView.findViewById(R.id.content);
                                    new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);
                                    content.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(EditPersonalActivity.this, EditEducationActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("item", mItem);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                mEducationMoreLl.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(EditPersonalActivity.this, EditEducationActivity.class);
                                        intent.putExtra("add", "add");
                                        startActivity(intent);
                                    }
                                });


                            }
                        } else {
                            mEducationTv.setVisibility(View.GONE);
                            mEditEducationTv.setVisibility(View.GONE);
                            mEducationMoerTv.setVisibility(View.GONE);
                            mEducationMoreLl.setVisibility(View.GONE);
                            mEducationMoreView.setVisibility(View.GONE);

                            mEducationLL.setVisibility(View.VISIBLE);
                            mEducationLL.setClickable(true);
                            mEducationLL.removeAllViews();

                            View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.item_work_experience, null);
                            LinearLayout mContentLl = mView.findViewById(R.id.ll_content);
                            TextView mTextTv = mView.findViewById(R.id.tv_add_start);
                            mTextTv.setText("更好的展示你的专业知识");
                            mContentLl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(EditPersonalActivity.this, EditEducationActivity.class);
                                    intent.putExtra("add", "add");
                                    startActivity(intent);
                                }
                            });
                            mEducationLL.addView(mView);
                        }


                        //标签
                        if (null != item.getProperty() && item.getProperty().size() > 0) {
                            label.clear();
                            mLabelExperienceLL.setVisibility(View.VISIBLE);
                            mLabelExperienceLL.removeAllViews();
                            mLabelExperienceLL.setClickable(false);
                            mEditLabelTv.setVisibility(View.VISIBLE);

                            View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.item_tages, null);
                            LabelsView mLabelsView = mView.findViewById(R.id.lv_name);
                            mLabelsView.setSelectType(LabelsView.SelectType.NONE);//标签不可选

                            for (int i = 0; i < item.getProperty().size(); i++) {
                                MeDataBean.Property mItem = item.getProperty().get(i);
                                mParentName = mItem.getParentName();
                                mLabelName = mItem.getName();
                                label.add(mItem.getParentName() + "-" + mItem.getName());
                            }
                            mLabelsView.setLabels(label);
                            mLabelExperienceLL.addView(mView);
                        } else {
                            mEditLabelTv.setVisibility(View.GONE);
                        }

                        if (item.getPictures().size() > 0) {
                            mAlbumTv.setText(item.getUsername() + "的动态");
                            mAlbumDataLL.removeAllViews();
                            for (int i = 0; i < item.getPictures().size(); i++) {
                                View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.item_images, null);
                                ImageView image = mView.findViewById(R.id.iv_img);
                                ImgLoaderManager.getInstance().showImageView(item.getPictures().get(i).toString(), image);
                                mAlbumDataLL.addView(mView);
                            }
                        } else {
                            mAlbumDataLL.setVisibility(View.GONE);
                            mAlbumTv.setVisibility(View.GONE);
                        }

                        addGroupImage(Double.valueOf(item.getStars()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFailue(String failueJson) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void setListener() {

        mEducationLL.setOnClickListener(this);
        mLabelExperienceLL.setOnClickListener(this);
        mWorkExperienceLL.setOnClickListener(this);
        mEditTv.setOnClickListener(this);

        mEditEducationTv.setOnClickListener(this);
        mEditLabelTv.setOnClickListener(this);
        mEditWorkTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);

        mNavigationFriend.setOnClickListener(this);
        mNavigationShare.setOnClickListener(this);
        mNavigationFollow.setOnClickListener(this);
        mNavigationMore.setOnClickListener(this);
        mAlbumLL.setOnClickListener(this);

        //添加工作经历
        mMoreLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_edit_job:
                Intent jobIntent = new Intent(new Intent(EditPersonalActivity.this, JobActivity.class));
                jobIntent.putExtra("mUid", mUid);
                jobIntent.putExtra("edit", "edit");
                jobIntent.putExtra("jobid", mjobId);

                jobIntent.putExtra("cityId", cityId);
                jobIntent.putExtra("salary", salary);
                jobIntent.putExtra("jobStr", jobStr);
                jobIntent.putExtra("proStr", proStr);


                startActivity(jobIntent);
                break;

            case R.id.iv_toolbar_back:
                finish();
                break;

            case R.id.anchor_detail_ll_work_experience:
                intent.setClass(this, AddWorkActivity.class);
                intent.putExtra("add", "add");
                startActivity(intent);
                break;
            case R.id.anchor_detail_ll_education_experience:
                intent.setClass(this, EditEducationActivity.class);
                intent.putExtra("add", "add");
                startActivity(intent);
                break;
            case R.id.anchor_detail_ll_label_experience:

                /*Intent intent1 = new Intent(EditPersonalActivity.this, AddLabelActivity.class);
                intent1.putExtra("uid", mUid);
                startActivity(intent1);*/
                Intent intent1 = new Intent(EditPersonalActivity.this, FunctionLabelActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("uid", mUid);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.iv_edit:
                Intent intentUser = new Intent(EditPersonalActivity.this, EditUserInfoActivity.class);
                Bundle bundleUser = new Bundle();
                bundleUser.putString("uid", mUid);

                bundleUser.putString("parentName", mParentName);
                bundleUser.putString("labelName", mLabelName);
                intentUser.putExtras(bundleUser);
                startActivity(intentUser);
                break;
            case R.id.iv_edit_work:
                startActivity(new Intent(this, EditWorkActivity.class));
                break;
            case R.id.iv_edit_education:
                startActivity(new Intent(this, EducationListActivity.class));
                //startActivity(new Intent(EditPersonalActivity.this, EducationActivity.class));
                break;
            case R.id.iv_edit_label:
              /*  Intent intent4 = new Intent(EditPersonalActivity.this, AddLabelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", mUid);
                bundle.putStringArrayList("labes", label);
                intent4.putExtras(bundle);
                startActivity(intent4);*/
                Intent intent4 = new Intent(EditPersonalActivity.this, FunctionLabelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", mUid);
                bundle.putString("parentName", mParentName);
                bundle.putString("labelName", mLabelName);
                intent4.putExtras(bundle);
                startActivity(intent4);
                break;
            case R.id.navigation_friend:
                ToastUtils.showToast("自己不能添加自己为好友！");
                break;
            case R.id.navigation_follow:
                ToastUtils.showToast("自己不能关注自己！");
                break;
            case R.id.navigation_share:
                share();
                break;
            case R.id.navigation_more:
                bottomMenu();
                break;
            case R.id.ll_album:
                intent.setClass(EditPersonalActivity.this, WholeAlbumActivity.class);
                intent.putExtra("uid", mUid);
                startActivity(intent);
                break;
            case R.id.ll_more:
                intent.setClass(this, AddWorkActivity.class);
                intent.putExtra("add", "add");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("举报");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("拉黑");
        menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                Intent intent = new Intent(EditPersonalActivity.this, ReportActivity.class);
                intent.putExtra("uid", mUid);
                intent.putExtra("thisId", mUid);
                startActivity(intent);
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                ToastUtils.showToast("自己不能拉黑自己");
                return;

            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
    }

    private void share() {//分享
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User/index?uid=" + mUid + "&thisUid=" + mUid, mNameStr, mInfoTv.getText().toString(), mLogo);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onStatusEvent(MessageEvent event) {
        if (!status.equals("1")) {
            //showDialogs();
        }
    }


    private void showDialogs() {
        mAlertView = new AlertView("提示", "您还不是认证用户，请提交资\n料申请认证！", "取消", new String[]{"马上认证"}, null, EditPersonalActivity.this, AlertView.Style.Alert, EditPersonalActivity.this).setCancelable(true).setOnDismissListener(this);
        mAlertView.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(EditPersonalActivity.this);
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (position == 0) {
            startActivity(new Intent(EditPersonalActivity.this, IDAuthenticationActivity.class));
        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDleLabel(DleLabelEvent event) {
        mEditLabelTv.setVisibility(View.GONE);
        mLabelExperienceLL.setVisibility(View.VISIBLE);
        mLabelExperienceLL.removeAllViews();
        mLabelExperienceLL.setClickable(true);
        View mView = LayoutInflater.from(EditPersonalActivity.this).inflate(R.layout.item_label, null);
        mLabelExperienceLL.addView(mView);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEditPersonalFunctionEvent(FunctionEvent functionEvent) {//职能标签
        FunctionDbHelper.updateProfession(mUid, functionEvent.mOneId, functionEvent.mTwoId, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {
            }

            @Override
            public void getSuccess(String successJson) {
                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        initData(mUid);
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

    public MeDataBean parseData(String result) {//相册
        MeDataBean entity = new MeDataBean();
        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONObject data = new JSONObject(jsonobject.getString("data"));
            Gson gson = new Gson();
            entity = gson.fromJson(data.toString(), MeDataBean.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    //添加评论星
    private void addGroupImage(double size) {
        mGroupLl.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));  //设置图片宽高
            imageView.setImageResource(R.drawable.icon_new_comment); //图片资源
            mGroupLl.addView(imageView); //动态添加图片
        }
    }

}
