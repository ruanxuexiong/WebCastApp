package com.android.nana.webcast;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.android.common.utils.BitmapUtils;
import com.android.nana.R;
import com.android.nana.user.ForwardActivity;
import com.android.nana.util.BitmapUtil;
import com.android.nana.util.ForAllUtils;
import com.android.nana.util.ShareUtils;
import com.android.nana.util.ToastUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

import io.rong.message.ImageMessage;

/**
 * Created by THINK on 2017/6/19.
 */

public class ShareFragment extends DialogFragment implements View.OnClickListener {

    private String mUrl, mTitle, mContent, mLogo, mThumb, mOldCard;

    private View mWeixin, mWeixinMoments, mViewSan, mCopy;
    private File mWxFile;

    public static ShareFragment newInstance(String url, String title, String content, String logo) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("title", title);
        args.putString("content", content);
        args.putString("logo", logo);

        fragment.setArguments(args);
        return fragment;
    }

    public static ShareFragment newInstance(String url, String title, String content, String logo, String thumb, String oldCard) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("title", title);
        args.putString("content", content);
        args.putString("logo", logo);
        args.putString("thumb", thumb);
        args.putString("oldCard", oldCard);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_wx_moment:
                if (ForAllUtils.isWeixinAvilible(getActivity())) {

                    if (null != getArguments().getString("thumb")) {
                        shareWx();
                        ShareUtils.shareImage(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, mWxFile);
                    } else {
                        ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, mTitle, mContent, mLogo, mUrl);
                    }
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }
                break;
            case R.id.view_wx:
                if (ForAllUtils.isWeixinAvilible(getActivity())) {
                    if (null != getArguments().getString("thumb")) {
                        shareWx();
                        ShareUtils.shareImage(getActivity(), SHARE_MEDIA.WEIXIN, mWxFile);
                    } else {
                        ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN, mTitle, mContent, mLogo, mUrl);
                    }
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }
                break;

            case R.id.view_san:
                shareSan();
                break;
            case R.id.view_copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mUrl);
                ToastUtils.showToast("复制成功");
                dismiss();
                break;
            default:
                break;
        }
    }


    private void shareWx() {
        Bitmap bitmap = BitmapUtils.getBitmap(mOldCard);
        mWxFile = BitmapUtils.getBitmapToFile(bitmap, "oldCard");
    }

    private void shareSan() {
        ImageMessage image = new ImageMessage();


        Bitmap bitmap = BitmapUtils.getBitmap(mOldCard);
        File mOldFile = BitmapUtils.getBitmapToFile(bitmap, "oldCard");
        Uri uri = Uri.fromFile(mOldFile);

        Bitmap mBitmap = BitmapUtils.getBitmap(mThumb);
        File mThumbFile = BitmapUtil.saveBitmapFile(mBitmap, "thumb");
        Uri thumb = Uri.fromFile(mThumbFile);
        image.obtain(thumb, uri, true);

        image.setLocalUri(uri);
        image.setThumUri(thumb);
        Intent intent = new Intent(getActivity(), ForwardActivity.class);
        intent.putExtra("imageMsg", image);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        //设置弹出框宽屏显示，适应屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);

        //移动弹出菜单到底部
        WindowManager.LayoutParams wlp = getDialog().getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(wlp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明

        mUrl = getArguments().getString("url");
        String title = getArguments().getString("title");
        String content = getArguments().getString("content");
        String logo = getArguments().getString("logo");


        if (null != getArguments().getString("thumb")) {
            mThumb = getArguments().getString("thumb");
        }
        if (null != getArguments().getString("oldCard")) {
            mOldCard = getArguments().getString("oldCard");
        }

        if (title != null && !title.isEmpty()) {
            mTitle = title;
        } else {
            mTitle = "哪哪APP--让联系更简单！";
        }

        if (content != null && !content.isEmpty()) {
            mContent = content;
        } else {
            mContent = "";
        }

        if (logo != null && !logo.isEmpty()) {
            mLogo = logo;
        } else {
            mLogo = "http://www.facethree.com/themes/simplebootx/Public/images/zblogo.png";
        }

        View view = inflater.inflate(R.layout.activity_share, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeixin = view.findViewById(R.id.view_wx);
        mWeixinMoments = view.findViewById(R.id.view_wx_moment);

        mViewSan = view.findViewById(R.id.view_san);
        mCopy = view.findViewById(R.id.view_copy);

        if (null != getArguments().getString("thumb")) {
            mCopy.setVisibility(View.GONE);
        }

        if (null == getArguments().getString("thumb")) {
            mViewSan.setVisibility(View.GONE);
        }
        mViewSan.setOnClickListener(this);
        mCopy.setOnClickListener(this);

        mWeixinMoments.setOnClickListener(this);
        mWeixin.setOnClickListener(this);
    }
}
