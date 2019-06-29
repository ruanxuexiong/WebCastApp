package com.android.nana.partner;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.utils.IOAuthCallBack;
import com.android.nana.R;
import com.android.nana.dbhelper.WebCastDbHelper;
import com.android.nana.util.ForAllUtils;
import com.android.nana.util.ShareUtils;
import com.android.nana.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by THINK on 2017/7/6.
 */

public class PartnerFragment extends DialogFragment implements View.OnClickListener {

    private View mMesg, mCode, mWeixin, mWeixinMoments;
    private Dialog mDialog;
    private View mView;
    private TextView mCloseTv;
    private String mUid, mUserName, mAvatar, mIntroduce, mMsg, mUrl;
    private ImageView mCodeIv;

    public static PartnerFragment newInstance(String mid, String mUserName, String mAvatar, String mIntroduce, String mMsg, String mUrl) {
        PartnerFragment fragment = new PartnerFragment();
        Bundle args = new Bundle();
        args.putString("mid", mid);
        args.putString("mUserName", mUserName);
        args.putString("mAvatar", mAvatar);
        args.putString("mIntroduce", mIntroduce);
        args.putString("mMsg", mMsg);
        args.putString("mUrl", mUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public static PartnerFragment newInstance(String mid, String mMsg, String mUrl) {
        PartnerFragment fragment = new PartnerFragment();
        Bundle args = new Bundle();
        args.putString("mid", mid);
        args.putString("mMsg", mMsg);
        args.putString("mUrl", mUrl);
        fragment.setArguments(args);
        return fragment;
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

        mUid = getArguments().getString("mid");
        mUserName = getArguments().getString("mUserName");
        mAvatar = getArguments().getString("mAvatar");
        mIntroduce = getArguments().getString("mIntroduce");
        mMsg = getArguments().getString("mMsg");
        mUrl = getArguments().getString("mUrl");

        View view = inflater.inflate(R.layout.activity_partner_share, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeixin = view.findViewById(R.id.view_wx);
        mWeixinMoments = view.findViewById(R.id.view_wx_moment);

        mMesg = view.findViewById(R.id.view_msg);
        mCode = view.findViewById(R.id.view_qr_code);

        mMesg.setOnClickListener(this);
        mCode.setOnClickListener(this);
        mWeixinMoments.setOnClickListener(this);
        mWeixin.setOnClickListener(this);

        mView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_qr_dialog, null);
        mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setCanceledOnTouchOutside(false);

        mCloseTv = mView.findViewById(R.id.tv_btn);
        mCloseTv.setOnClickListener(this);

        mCodeIv = mView.findViewById(R.id.qr_iv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_wx:

                if (ForAllUtils.isWeixinAvilible(getActivity())) {
                    ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN, mUserName, mIntroduce, mAvatar, mUrl);
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }
                break;
            case R.id.view_wx_moment:
                if (ForAllUtils.isWeixinAvilible(getActivity())) {
                    ShareUtils.shareSingle(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE, mUserName, mIntroduce, mAvatar, mUrl);
                } else {
                    ToastUtils.showToast("分享失败，请安装微信客户端！");
                }
                break;
            case R.id.view_msg:
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:"));
                sendIntent.putExtra("sms_body", mMsg);
                getActivity().startActivity(sendIntent);
                break;
            case R.id.view_qr_code:
                loadData(mUid);
                break;
            case R.id.tv_btn:
                mDialog.dismiss();
                break;
        }
    }

    private void loadData(String mUid) {
        WebCastDbHelper.getQR(mUid, new IOAuthCallBack() {
            @Override
            public void onStartRequest() {

            }

            @Override
            public void getSuccess(String successJson) {

                try {
                    JSONObject jsonObject = new JSONObject(successJson);
                    JSONObject object = new JSONObject(jsonObject.getString("data"));
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    if (result.getString("state").equals("0")) {
                        ImageLoader.getInstance().displayImage(object.getString("qr"), mCodeIv);
                        mDialog.show();
                        mDialog.getWindow().setContentView(mView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mDialog.show();
                mDialog.getWindow().setContentView(mView);
            }

            @Override
            public void getFailue(String failueJson) {

            }
        });
    }
}
