package com.android.nana.card;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.common.utils.BitmapUtils;
import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.activity.BaseFragment;
import com.android.nana.dbhelper.CardDbHelper;
import com.android.nana.eventBus.MessageEvent;
import com.android.nana.eventBus.Modular3EditFragmentEvent;
import com.android.nana.eventBus.Modular3FragmentEvent;
import com.android.nana.menu.BottomMenuFragment;
import com.android.nana.menu.MenuItem;
import com.android.nana.menu.MenuItemOnClickListener;
import com.android.nana.util.ImgLoaderManager;
import com.android.nana.util.ToastUtils;
import com.android.nana.util.Utils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lenovo on 2018/1/18.
 */

public class Modular3Fragment extends BaseFragment {

    private ImageView mAddLogoIv;
    private LinearLayout mImageContent;
    private LinearLayout mImagell;
    private String mHhasCard, mAddCard;


    private TextView mNameTv;
    private TextView mPositionTv;
    private TextView mPhoneTv;
    private TextView mCompanyTv;
    private TextView mAddressTv;
    private TextView mMobileTv;
    private TextView mFaxTv;
    private TextView mMailTv;
    private TextView mCodeTv;
    private ImageView mLogoIv;

    private String mCardId;
    private File mLogoImage = null;
    private boolean isBottom = true;
    private boolean isLogoNull = false;//是否添加logo图
   private List<LocalMedia> selectList = new ArrayList<>();

    public static Modular3Fragment newInstance(String mHasCard, String mAddCard) {
        Modular3Fragment fragment = new Modular3Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("mHasCard", mHasCard);
        bundle.putString("mAddCard", mAddCard);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Modular3Fragment newInstance(String mHasCard, String mAddCard, String mCardId, String name, String post, String company, String phone, String mobile, String fax, String mail, String address, String code, String logo) {
        Modular3Fragment fragment = new Modular3Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("mHasCard", mHasCard);
        bundle.putString("mAddCard", mAddCard);
        bundle.putString("mCardId", mCardId);
        bundle.putString("name", name);
        bundle.putString("post", post);
        bundle.putString("company", company);
        bundle.putString("phone", phone);
        bundle.putString("mobile", mobile);
        bundle.putString("fax", fax);
        bundle.putString("mail", mail);
        bundle.putString("address", address);
        bundle.putString("code", code);
        bundle.putString("logo", logo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(Modular3Fragment.this)) {
            EventBus.getDefault().register(Modular3Fragment.this);
        }
    }

    @Override
    protected void initData() {
        Bundle args = getArguments();
        if (null != args) {
            mHhasCard = args.getString("mHasCard");
            if (null != args.getString("name")) {
                mImagell.setVisibility(View.GONE);
                mImageContent.setVisibility(View.VISIBLE);

                mCardId = args.getString("mCardId");
                mNameTv.setText(args.getString("name"));
                mCompanyTv.setText(args.getString("company"));
                mPositionTv.setText(args.getString("post"));
                mPhoneTv.setText("手机：" + args.getString("phone"));
                mMobileTv.setText("电话：" + args.getString("mobile"));
                mAddressTv.setText("地址：" + args.getString("address"));
                mMailTv.setText("邮箱：" + args.getString("mail"));
                mFaxTv.setText("传真：" + args.getString("fax"));
                mCodeTv.setText("邮编：" + args.getString("code"));

                if ("".equals(args.getString("logo"))) {
                    isLogoNull = true;
                    mLogoIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_mode3_logo));
                } else {
                    new ImgLoaderManager(R.drawable.icon_default).showImageView(args.getString("logo"), mLogoIv, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                            if (TextUtils.isEmpty(s)) return;

                            mLogoImage = BitmapUtils.getBitmapToFile(bitmap, s.substring(s.lastIndexOf("/") + 1));

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });
                }
            } else if (null != args.getString("mAddCard")) {//mAddCard 是否从点击名片按钮进来
                mImageContent.setVisibility(View.GONE);
                mImagell.setVisibility(View.VISIBLE);
            } else {
                mImagell.setVisibility(View.GONE);
                mImageContent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_modular3;
    }

    @Override
    public void initView() {
        mAddLogoIv = mContentView.findViewById(R.id.iv_add_card);
        mImagell = mContentView.findViewById(R.id.ll_img_null);
        mImageContent = mContentView.findViewById(R.id.ll_img_content);

        mNameTv = mContentView.findViewById(R.id.tv_name);
        mPositionTv = mContentView.findViewById(R.id.tv_position);
        mPhoneTv = mContentView.findViewById(R.id.tv_phone);
        mCompanyTv = mContentView.findViewById(R.id.tv_company);
        mAddressTv = mContentView.findViewById(R.id.tv_address);
        mMobileTv = mContentView.findViewById(R.id.tv_mobile);
        mFaxTv = mContentView.findViewById(R.id.tv_fax);
        mMailTv = mContentView.findViewById(R.id.tv_mail);
        mCodeTv = mContentView.findViewById(R.id.tv_code);
        mLogoIv = mContentView.findViewById(R.id.iv_logo);
    }

    @Override
    public void bindEvent() {
        mLogoIv.setOnClickListener(this);
        mAddLogoIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_card:
                if (isBottom) {
                    choiceLogo();
                } else {
                    bottomMenu();
                }
                break;
            case R.id.iv_logo:
                bottomMenu();
                break;
            default:
                break;
        }
    }

    private void choiceLogo() {//头像

        isBottom = false;

        PictureSelector.create(Modular3Fragment.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(false)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .showCropGrid(false)
                .selectionMedia(selectList)// 是否传入已选图片
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .compress(true)//是否压缩
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        File outputImage = new File(media.getCompressPath());
                        if (outputImage.exists()) {
                            mLogoImage = outputImage;
                        }
                        isLogoNull = false;
                        mLogoIv.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                        mAddLogoIv.setImageURI(Uri.fromFile(new File(media.getCompressPath())));
                    }
                    break;
            }
        } else {
            isBottom = true;
        }
    }

    private void bottomMenu() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("从手机相册选择");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("删除");
        menuItem2.setStyle(MenuItem.MenuItemStyle.STRESS);

        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                choiceLogo();
            }
        });

        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                isBottom = true;
                mLogoImage = null;
                isLogoNull = true;
                mAddLogoIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_mode3_logo));
                mLogoIv.setImageDrawable(getResources().getDrawable(R.drawable.icon_mode3_logo));
            }
        });

        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getActivity().getFragmentManager(), "BottomMenuFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(Modular3Fragment.this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEdit3Card(Modular3EditFragmentEvent event) {

        showProgressDialog("", "加载中...");

        mImagell.setVisibility(View.GONE);
        mImageContent.setVisibility(View.VISIBLE);

        mNameTv.setText(event.name);
        mCompanyTv.setText(event.company);
        mPositionTv.setText(event.post);

        if ("".equals(event.phone)) {
            mPhoneTv.setVisibility(View.GONE);
        } else {
            mPhoneTv.setText("手机：" + event.phone);
        }

        if ("".equals(event.mobile)) {
            mMobileTv.setVisibility(View.GONE);
        } else {
            mMobileTv.setText("电话：" + event.mobile);
        }

        if ("".equals(event.address)) {
            mAddressTv.setVisibility(View.GONE);
        } else {
            mAddressTv.setText("地址：" + event.address);
        }

        if ("".equals(event.mail)) {
            mMailTv.setVisibility(View.GONE);
        } else {
            mMailTv.setText("邮箱：" + event.mail);
        }

        if ("".equals(event.fax)) {
            mFaxTv.setVisibility(View.GONE);
        } else {
            mFaxTv.setText("传真：" + event.fax);
        }

        if ("".equals(event.code)) {
            mCodeTv.setVisibility(View.GONE);
        } else {
            mCodeTv.setText("邮编：" + event.code);
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 2;
        bundle.putString("mid", event.mid);
        bundle.putString("name", event.name);
        bundle.putString("company", event.company);
        bundle.putString("post", event.post);
        bundle.putString("phone", event.phone);
        bundle.putString("mobile", event.mobile);
        bundle.putString("address", event.address);
        bundle.putString("mail", event.mail);
        bundle.putString("fax", event.fax);
        bundle.putString("code", event.code);
        msg.setData(bundle);
        uiHandler.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpModular3data(Modular3FragmentEvent event) {
        showProgressDialog("", "加载中...");

        mImagell.setVisibility(View.GONE);
        mImageContent.setVisibility(View.VISIBLE);

        mNameTv.setText(event.name);
        mCompanyTv.setText(event.company);
        mPositionTv.setText(event.post);

        if ("".equals(event.phone)) {
            mPhoneTv.setVisibility(View.GONE);
        } else {
            mPhoneTv.setText("手机：" + event.phone);
        }

        if ("".equals(event.mobile)) {
            mMobileTv.setVisibility(View.GONE);
        } else {
            mMobileTv.setText("电话：" + event.mobile);
        }

        if ("".equals(event.address)) {
            mAddressTv.setVisibility(View.GONE);
        } else {
            mAddressTv.setText("地址：" + event.address);
        }

        if ("".equals(event.mail)) {
            mMailTv.setVisibility(View.GONE);
        } else {
            mMailTv.setText("邮箱：" + event.mail);
        }

        if ("".equals(event.fax)) {
            mFaxTv.setVisibility(View.GONE);
        } else {
            mFaxTv.setText("传真：" + event.fax);
        }

        if ("".equals(event.code)) {
            mCodeTv.setVisibility(View.GONE);
        } else {
            mCodeTv.setText("邮编：" + event.code);
        }


        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 1;
        bundle.putString("mid", event.mid);
        bundle.putString("name", event.name);
        bundle.putString("company", event.company);
        bundle.putString("post", event.post);
        bundle.putString("phone", event.phone);
        bundle.putString("mobile", event.mobile);
        bundle.putString("address", event.address);
        bundle.putString("mail", event.mail);
        bundle.putString("fax", event.fax);
        bundle.putString("code", event.code);
        msg.setData(bundle);
        uiHandler.sendMessage(msg);
    }


    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isLogoNull) {
                        isLogoNull = false;
                        mLogoIv.setVisibility(View.GONE);
                    }
                    String mPath = Utils.saveImage(getActivity(), mImageContent);
                    File file = new File(mPath);
                    Bundle bundle = msg.getData();

                    CardDbHelper.addCard("1", bundle.getString("mid"), "", bundle.getString("name"), bundle.getString("post"), bundle.getString("company"), bundle.getString("phone"), file, mLogoImage, bundle.getString("mobile"), bundle.getString("fax"), bundle.getString("mail"), bundle.getString("code"), "3", bundle.getString("address"), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject result = new JSONObject(jsonObject.getString("result"));
                                if (result.getString("state").equals("0")) {
                                    ToastUtils.showToast("添加成功!");
                                    EventBus.getDefault().post(new MessageEvent(""));//通知更新名片列表
                                    getActivity().finish();
                                } else {
                                    ToastUtils.showToast(result.getString("description"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });
                    break;
                case 2:
                    if (isLogoNull) {
                        isLogoNull = false;
                        mLogoIv.setVisibility(View.GONE);
                    }
                    String path = Utils.saveImage(getActivity(), mImageContent);
                    File mFile = new File(path);
                    Bundle mBundle = msg.getData();

                    CardDbHelper.addCard("1", mBundle.getString("mid"), mCardId, mBundle.getString("name"), mBundle.getString("post"), mBundle.getString("company"), mBundle.getString("phone"), mFile, mLogoImage, mBundle.getString("mobile"), mBundle.getString("fax"), mBundle.getString("mail"), mBundle.getString("code"), "3", mBundle.getString("address"), new IOAuthCallBack() {
                        @Override
                        public void onStartRequest() {

                        }

                        @Override
                        public void getSuccess(String successJson) {
                            dismissProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(successJson);
                                JSONObject result = new JSONObject(jsonObject.getString("result"));
                                if (result.getString("state").equals("0")) {
                                    ToastUtils.showToast("编辑成功!");
                                    EventBus.getDefault().post(new MessageEvent(""));//通知更新名片列表
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void getFailue(String failueJson) {

                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };
}
