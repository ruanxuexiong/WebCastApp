package com.android.nana.find.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.find.web.CommonActivity;
import com.android.nana.user.weight.VideoPlayerController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

/**
 * Created by lenovo on 2018/11/6.
 */

public class PointFailedFragment extends DialogFragment implements View.OnClickListener  {

    private String  mAdvertising, mAdvType, mAdvUrl;
    private TextView mMoneyTv,hongbao_text;
    private ImageView mCloseIv;
    private NiceVideoPlayer mPlayer;
    private LinearLayout mPlanLl,mBgImgLl;
    private ImageView mDefIv,hongbao_xq;

    public static PointFailedFragment newInstance(String advertising, String advType, String advUrl) {
        PointFailedFragment fragment = new PointFailedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("advertising", advertising);
        bundle.putString("advType", advType);
        bundle.putString("advUrl", advUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdvertising = getArguments().getString("advertising");
        mAdvType = getArguments().getString("advType");
        mAdvUrl = getArguments().getString("advUrl");
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_open_failed, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCloseIv = view.findViewById(R.id.iv_close);
        mDefIv = view.findViewById(R.id.iv_def);
        mPlayer = view.findViewById(R.id.video_player);
        mPlanLl = view.findViewById(R.id.ll_plan);
        hongbao_text=view.findViewById(R.id.hongbao_text);
        hongbao_xq=view.findViewById(R.id.hongbao_xq);
        if (null!=mAdvType&&!mAdvType.equals("")){
            hongbao_xq.setVisibility(View.VISIBLE);
        }
        else {
            hongbao_xq.setVisibility(View.GONE);
        }
        if (mAdvType.equals("2")) {//视频
            mDefIv.setVisibility(View.GONE);
            mPlanLl.setVisibility(View.VISIBLE);

            VideoPlayerController controller = new VideoPlayerController(getActivity());
            final VideoPlayerController mController = controller;
            mPlayer.setController(mController);
            mPlayer.continueFromLastPosition(false);
            mPlayer.getTcpSpeed();
            mPlayer.setUp(mAdvertising, null);
            mPlayer.start();
            mController.mCenterStart.setVisibility(View.GONE);

            mController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable.ConstantState drawableCs = getActivity().getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                    if (mController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                        mPlayer.setVolume(0);
                        mController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                    } else {
                        mPlayer.setVolume(50);
                        mController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                    }
                }
            });
        } else {
            mDefIv.setVisibility(View.VISIBLE);
            mPlanLl.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.icon_red_def)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(getActivity())
                    .asBitmap()
                    .load(mAdvertising)
                    .apply(options)
                    .into(mDefIv);
        }

        mCloseIv.setOnClickListener(this);
        mDefIv.setOnClickListener(this);
        mPlanLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                mPlayer.releasePlayer();
                this.dismiss();
                break;
            case R.id.iv_def:
                if (null != mAdvUrl && !"".equals(mAdvUrl)) {
                    Intent mImage = new Intent(getContext(), CommonActivity.class);
                    mImage.putExtra("title", "哪哪");
                    mImage.putExtra("url", mAdvUrl);
                    getContext().startActivity(mImage);
                }
                break;
            case R.id.ll_plan:
                if (null != mAdvUrl && !"".equals(mAdvUrl)) {
                    Intent intent = new Intent(getContext(), CommonActivity.class);
                    intent.putExtra("title", "哪哪");
                    intent.putExtra("url", mAdvUrl);
                    getContext().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
