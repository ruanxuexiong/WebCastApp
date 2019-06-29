package com.android.nana.webview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.android.nana.R;
import com.android.nana.util.ForAllUtils;
import com.android.nana.util.ShareUtils;
import com.android.nana.util.ToastUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by THINK on 2017/7/13.
 */

public class WebFragment extends DialogFragment implements View.OnClickListener {

    private String mUrl, thisUid, mShareTitle, mShareContent, mSharePic, mShareUrl;

    private View mWeixin, mWeixinMoments;


    public static WebFragment newInstance(String thisid, String mShareTitle, String mShareContent, String mSharePic, String mShareUrl) {
        WebFragment fragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString("thisUid", thisid);
        bundle.putString("shareTitle", mShareTitle);
        bundle.putString("shareContent", mShareContent);
        bundle.putString("sharePic", mSharePic);
        bundle.putString("shareUrl", mShareUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_wx_moment:
                if (ForAllUtils.isWeixinAvilible(getActivity())) {
                    ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, mShareTitle, mShareContent, mSharePic, mShareUrl + thisUid);
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }
                break;
            case R.id.view_wx:
                if (ForAllUtils.isWeixinAvilible(getActivity())) {
                    ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN, mShareTitle, mShareContent, mSharePic, mShareUrl + thisUid);
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }

                break;
        }
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

        thisUid = getArguments().getString("thisUid");

        mShareTitle = getArguments().getString("shareTitle");
        mShareContent = getArguments().getString("shareContent");
        mSharePic = getArguments().getString("sharePic");
        mShareUrl = getArguments().getString("shareUrl");

        View view = inflater.inflate(R.layout.activity_share, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeixin = view.findViewById(R.id.view_wx);
        mWeixinMoments = view.findViewById(R.id.view_wx_moment);

        mWeixinMoments.setOnClickListener(this);
        mWeixin.setOnClickListener(this);
    }
}
