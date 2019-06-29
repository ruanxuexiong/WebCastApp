package com.android.nana.user;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.nana.R;
import com.android.nana.eventBus.SendVideoMsgEvent;
import com.android.nana.eventBus.VideoMessageItemEvent;
import com.android.nana.user.weight.VideoPlayerController;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xiao.nicevideoplayer.NiceVideoPlayer;

import org.greenrobot.eventbus.EventBus;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/11/27.
 */

@ProviderTag(messageContent = VideoMessage.class)
public class VideoMessageItemProvider extends IContainerItemProvider.MessageProvider<VideoMessage> {


    @Override
    public void bindView(View view, int i, final VideoMessage videoMessage, final UIMessage uiMessage) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        VideoPlayerController controller = new VideoPlayerController(viewHolder.mContext);
        viewHolder.mController = controller;
        viewHolder.mVideoPlayer.setController(controller);
        WindowManager manager = (WindowManager) viewHolder.mContext.getSystemService(Context.WINDOW_SERVICE);
        double width = manager.getDefaultDisplay().getWidth();

        double number = width;
        double total =  Double.valueOf(number) / 375.00;
        double videoWidth = 237 * Double.valueOf(total);


        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) { //自己发送

            ViewGroup.LayoutParams params = viewHolder.mVideoLl.getLayoutParams();

            params.width = (int) videoWidth;
            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(videoMessage.getRatio())));

            viewHolder.mVideoLl.setLayoutParams(params);

            if (null != videoMessage.getVideoImageUrl()) {
                Glide.with(viewHolder.mContext).load(videoMessage.getVideoImageUrl()).into(viewHolder.mController.imageView());
            }
            if (null != videoMessage.getVideoUrl()) {
                viewHolder.mVideoPlayer.continueFromLastPosition(false);
                viewHolder.mVideoPlayer.getTcpSpeed();
                viewHolder.mVideoPlayer.setUp(videoMessage.getVideoUrl(), null);
                viewHolder.mVideoPlayer.start();
                viewHolder.mController.mCenterStart.setVisibility(View.GONE);
            }

        } else {//对方发送

            viewHolder.mVideoPlayer.setController(controller);

            ViewGroup.LayoutParams params = viewHolder.mVideoLl.getLayoutParams();
            params.width = (int) videoWidth;
            params.height = Integer.valueOf((int) (videoWidth / Double.valueOf(videoMessage.getRatio())));

            viewHolder.mVideoLl.setLayoutParams(params);

            if (null != videoMessage.getVideoImageUrl()) {
                Picasso.with(viewHolder.mContext) //设置context
                        .load(videoMessage.getVideoImageUrl()) //图片url地址
                        .placeholder(R.drawable.img_df) //加载时显示的图片
                        .fit() //自动按照图片尺寸进行压缩
                        .tag("image") //图片tag，便于控制图片加载和暂停加载
                        .into(viewHolder.mController.imageView());
            }

            if (null != videoMessage.getVideoUrl()) {
                viewHolder.mVideoPlayer.continueFromLastPosition(false);
                viewHolder.mVideoPlayer.getTcpSpeed();
                viewHolder.mVideoPlayer.setUp(videoMessage.getVideoUrl(), null);
                viewHolder.mVideoPlayer.start();
                viewHolder.mController.mCenterStart.setVisibility(View.GONE);
            }
        }

        viewHolder.mController.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new VideoMessageItemEvent(uiMessage.getMessageId()));
                return true;
            }
        });


        viewHolder.mController.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SendVideoMsgEvent(viewHolder, videoMessage, uiMessage));
            }
        });

        viewHolder.mController.mVoiceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable.ConstantState drawableCs = viewHolder.mContext.getResources().getDrawable(R.drawable.icon_voice_open).getConstantState();
                if (viewHolder.mController.mVoiceIv.getDrawable().getConstantState().equals(drawableCs)) {
                    viewHolder.mVideoPlayer.setVolume(0);
                    viewHolder.mController.mVoiceIv.setImageResource(R.drawable.icon_voice_close);
                } else {
                    viewHolder.mVideoPlayer.setVolume(50);
                    viewHolder.mController.mVoiceIv.setImageResource(R.drawable.icon_voice_open);
                }
            }
        });

    }

    @Override
    public Spannable getContentSummary(VideoMessage videoMessage) {
        return new SpannableString("[视频消息]");
    }

    @Override
    public void onItemClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        EventBus.getDefault().post(new SendVideoMsgEvent(viewHolder, videoMessage, uiMessage));
    }
/*
    @Override
    public void onItemLongClick(View view, int position, VideoMessage content, UIMessage message) {
     //   super.onItemLongClick(view, position, content, message);

    }*/

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_video_message, null);
        ViewHolder holder = new ViewHolder();
        holder.mVideoPlayer = view.findViewById(R.id.nice_video_player);
        holder.mVideoLl = view.findViewById(R.id.ll_video);
        holder.mContext = context;
        view.setTag(holder);
        return view;
    }


    public class ViewHolder {
        Context mContext;
        VideoPlayerController mController;
        NiceVideoPlayer mVideoPlayer;
        FrameLayout mVideoLl;
    }
}
