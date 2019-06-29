package com.android.nana.material;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.android.nana.customer.EditWorkActivity;
import com.android.nana.customer.EducationActivity;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.friend.WholeAlbumActivity;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.ratingbar.RatingBar;
import com.android.nana.transition.TransitionManager;
import com.android.nana.transition.TransitionSingleHelper;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.ToastUtils;
import com.android.nana.webcast.ShareFragment;
import com.android.nana.widget.LabelsView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/4/19.
 */

public class MeDataActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTv;
    private TextView mBackTv;
    private TextView mAction2;
    private String mid, status, mHead;
    private String mFaceModeStr;//三分钟模式状态
    private RoundedImageView mAvatar;
    private TextView mNumTv;
    private ImageView mGenderIv;
    private TextView mMoneyTv;
    private ArrayList<String> label = new ArrayList<>();
    private LinearLayout mWorkLL, mWorkExperienceLL, mLabelExperienceLL, mEducationLL,mEducation,mTagsLl;
    private ImageView mBackgrounIv, mIdentyIv;
    private TextView mNameTv, mPositionTv, mAddressTv, mInfoTv, mMoreTv, mEducationMoerTv;
    private TextView mAlbumTv;
    private TransitionSingleHelper mTSHelper;
    private LinearLayout mAlbumDataLL, mAlbumLL;
    private RelativeLayout mNavigationFriend;
    private String mNameStr, mLogo;
    private RatingBar mRatingBar;
    private RelativeLayout mNavigationShare, mNavigationFollow, mNavigationMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle.getString("thisUid")) {
            mid = bundle.getString("thisUid");
            initData(mid);
        }
    }

    @Override
    protected void bindViews() {
        setContentView(R.layout.activity_me_deta);
    }

    @Override
    protected void findViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        mBackTv = findViewById(R.id.iv_toolbar_back);
        mAction2 = findViewById(R.id.toolbar_right_2);
        mInfoTv = findViewById(R.id.tv_introduce);
        mNameTv = findViewById(R.id.tv_name);
        mAlbumTv = findViewById(R.id.tv_album);
        mAddressTv = findViewById(R.id.tv_address);
        mPositionTv = findViewById(R.id.tv_position);
        mBackgrounIv = findViewById(R.id.customer_detail_iv_top);
        mAvatar = findViewById(R.id.iv_head);

        mAlbumLL = findViewById(R.id.ll_album);
        mAlbumDataLL = findViewById(R.id.ll_album_data);
        mWorkLL = findViewById(R.id.ll_work);
        mWorkExperienceLL = findViewById(R.id.anchor_detail_ll_work_experience);

        mMoreTv = findViewById(R.id.tv_more);
        mEducationMoerTv = findViewById(R.id.tv_education_more);
        mLabelExperienceLL = findViewById(R.id.anchor_detail_ll_label_experience);
        mEducationLL = findViewById(R.id.anchor_detail_ll_education_experience);
        mEducation = findViewById(R.id.ll_education);
        mIdentyIv = findViewById(R.id.iv_identy);
        mNumTv = findViewById(R.id.tv_num);
        mMoneyTv = findViewById(R.id.tv_money);
        mGenderIv = findViewById(R.id.iv_sex);
        mRatingBar = findViewById(R.id.ratingBar);
        mNavigationShare = findViewById(R.id.navigation_share);
        mNavigationFriend = findViewById(R.id.navigation_friend);
        mNavigationFollow = findViewById(R.id.navigation_follow);
        mNavigationMore = findViewById(R.id.navigation_more);
        mTagsLl = findViewById(R.id.ll_tags);
    }

    @Override
    protected void init() {
        mTitleTv.setText("我的资料");
        mBackTv.setVisibility(View.VISIBLE);
        mAction2.setVisibility(View.VISIBLE);

        mTSHelper = new TransitionManager(MeDataActivity.this).getSingle();
        Drawable drawable = getResources().getDrawable(R.drawable.icon_me_edit);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mAction2.setCompoundDrawables(drawable, null, null, null);
        mAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction2Click();
            }
        });

    }

    private void onAction2Click() {
        Intent intent = new Intent(this, EditPersonalActivity.class);
        intent.putExtra("thisUid", mid);
        startActivity(intent);
    }

    @Override
    protected void setListener() {
        mBackTv.setOnClickListener(this);
        mAlbumLL.setOnClickListener(this);
        mAvatar.setOnClickListener(this);

        mNavigationFriend.setOnClickListener(this);
        mNavigationShare.setOnClickListener(this);
        mNavigationFollow.setOnClickListener(this);
        mNavigationMore.setOnClickListener(this);
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

                        if ("1".equals(item.getStatus())) {
                            status = item.getStatus();
                            mIdentyIv.setVisibility(View.VISIBLE);
                        } else {
                            mIdentyIv.setVisibility(View.GONE);
                            status = item.getStatus();
                        }

                        if (!"".equals(item.getAvatar())) {
                            mLogo = item.getAvatar();
                            ImgLoaderManager.getInstance().showImageView(item.getAvatar(), mAvatar);
                        } else {
                            mLogo = "http://www.facethree.com/themes/simplebootx/Public/images/zblogo.png";
                        }

                        if ("".equals(item.getAvatar())) {
                            mHead = "http://www.facethree.com/data/upload/share/person.png";
                        } else {
                            mHead = item.getAvatar();
                        }
                        if (!"".equals(item.getIdcard())) {
                            mNumTv.setText("ID:"+item.getIdcard());
                        }

                        if ("1".equals(item.getSex())) {
                            mGenderIv.setImageDrawable(getDrawable(R.drawable.icon_sex_male));
                        } else if ("2".equals(item.getSex())) {
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
                                mMoneyTv.setVisibility(View.VISIBLE);
                                mMoneyTv.setText("见面金额：由对方提出合理的见面金额");
                                break;
                            case "3":
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            case "4":
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            case "5"://未开启三分钟模式+非好友
                                mMoneyTv.setVisibility(View.VISIBLE);//显示金额
                                break;
                            default:
                                break;
                        }

                        if (item.getWorkHistorys().size() > 0) {
                            mWorkLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.setClickable(false);
                            mWorkExperienceLL.setVisibility(View.VISIBLE);
                            mWorkExperienceLL.removeAllViews();
                            mMoreTv.setVisibility(View.VISIBLE);
                            for (int i = 0; i < item.getWorkHistorys().size(); i++) {
                                MeDataBean.WorkHistorys mItem = item.getWorkHistorys().get(i);
                                View mView = LayoutInflater.from(MeDataActivity.this).inflate(R.layout.work_experience_item, null);
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

                                mMoreTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MeDataActivity.this, EditWorkActivity.class);
                                        /*intent.putExtra("add", true);
                                        intent.putExtra("userId", mUid);*/
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            mWorkLL.setVisibility(View.GONE);
                            mMoreTv.setVisibility(View.GONE);
                        }

                        //教育经历
                        if (item.getEducationExperiences().size() > 0) {
                            mEducationLL.setClickable(false);
                            mEducationLL.removeAllViews();
                            if (item.getEducationExperiences().size() > 2) {
                                mEducationMoerTv.setVisibility(View.VISIBLE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {
                                    MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(MeDataActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);

                                    new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);

                                    mEducationMoerTv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(MeDataActivity.this, EducationActivity.class);
                                            intent.putExtra("IsHideEdit", true);
                                            intent.putExtra("userId", mUid);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                mEducationMoerTv.setVisibility(View.GONE);
                                for (int i = 0; i < item.getEducationExperiences().size(); i++) {
                                    MeDataBean.EducationExperiences mItem = item.getEducationExperiences().get(i);
                                    View mView = LayoutInflater.from(MeDataActivity.this).inflate(R.layout.work_experience_item, null);
                                    TextView mTitle = mView.findViewById(R.id.tv_title);
                                    TextView mCompanyTv = mView.findViewById(R.id.tv_company);
                                    ImageView mPicture = mView.findViewById(R.id.iv_picture);
                                    TextView mTimeTv = mView.findViewById(R.id.tv_time);

                                    new ImgLoaderManager(R.drawable.icon_education).showImageView(mItem.getPicture(), mPicture);
                                    mTitle.setText(mItem.getName());
                                    mCompanyTv.setText(mItem.getMajor());
                                    mTimeTv.setText(mItem.getBeginTime() + "-" + mItem.getEndTime());

                                    mEducationLL.addView(mView);
                                }
                            }
                        } else {
                            mEducation.setVisibility(View.GONE);
                            mEducationMoerTv.setVisibility(View.GONE);
                        }

                        //标签

                        if (null != item.getProperty() && item.getProperty().size() > 0) {
                            label.clear();
                            mLabelExperienceLL.setVisibility(View.VISIBLE);
                            mLabelExperienceLL.removeAllViews();
                            mLabelExperienceLL.setClickable(false);

                            View mView = LayoutInflater.from(MeDataActivity.this).inflate(R.layout.item_tages, null);
                            LabelsView mLabelsView = mView.findViewById(R.id.lv_name);
                            mLabelsView.setSelectType(LabelsView.SelectType.NONE);//标签不可选

                            for (int i = 0; i < item.getProperty().size(); i++) {
                                MeDataBean.Property mItem = item.getProperty().get(i);
                                label.add(mItem.getParentName() + "-" + mItem.getName());
                            }
                            mLabelsView.setLabels(label);
                            mLabelExperienceLL.addView(mView);
                        }else {
                            mTagsLl.setVisibility(View.GONE);
                        }


                        if (item.getPictures().size() > 0) {
                            mAlbumTv.setText(item.getUsername() + "的动态");
                            mAlbumDataLL.removeAllViews();
                            for (int i = 0; i < item.getPictures().size(); i++) {
                                View mView = LayoutInflater.from(MeDataActivity.this).inflate(R.layout.item_images, null);
                                ImageView image = mView.findViewById(R.id.iv_img);
                                ImgLoaderManager.getInstance().showImageView(item.getPictures().get(i).toString(), image);
                                mAlbumDataLL.addView(mView);
                            }
                        } else {
                            mAlbumDataLL.setVisibility(View.GONE);
                            mAlbumTv.setVisibility(View.GONE);
                        }

                        addGroupImage(Float.valueOf(item.getStars()));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_toolbar_back:
                this.finish();
                break;
            case R.id.ll_album:
                Intent intent = new Intent(MeDataActivity.this, WholeAlbumActivity.class);
                intent.putExtra("uid", mid);
                startActivity(intent);
                break;
            case R.id.iv_head:
                mTSHelper.startViewerActivity(mAvatar, mHead);
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
                Intent intent = new Intent(MeDataActivity.this, ReportActivity.class);
                intent.putExtra("uid", mid);
                intent.putExtra("thisId", mid);
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
        ShareFragment dialog = ShareFragment.newInstance("http://www.facethree.com/W3g/User/index?uid=" + mid + "&thisUid=" + mid, mNameStr, mInfoTv.getText().toString(), mLogo);
        dialog.show(fm, "fragment_bottom_dialog");
    }

    public MeDataBean parseData(String result) {

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
    private void addGroupImage(Float size) {
        mRatingBar.setmClickable(false);
        mRatingBar.setStarEmptyDrawable(getResources().getDrawable(R.drawable.icon_star_empty));
        //设置填充的星星
        mRatingBar.setStarFillDrawable(getResources().getDrawable(R.drawable.icon_new_comment));
        //设置半颗星
        mRatingBar.setStarHalfDrawable(getResources().getDrawable(R.drawable.icon_star_half));
        //设置显示的星星个数
        mRatingBar.setStar(size);
    }
}
