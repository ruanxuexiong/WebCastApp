package com.android.nana.user;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.eventBus.SendMsgEvent;
import com.android.nana.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;


/**
 * Created by lenovo on 2018/9/18.
 */
@ProviderTag(messageContent = VoiceMessage.class)
public class VoiceMessageItemProvider extends IContainerItemProvider.MessageProvider<VoiceMessage> {

    private ViewHolder viewHolder;

    @Override
    public void bindView(View v, int i, VoiceMessage voiceMessage, UIMessage message) {
        final ViewHolder viewHolder = (ViewHolder) v.getTag();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
            viewHolder.mItemLL.setVisibility(View.VISIBLE);
            viewHolder.mReceiveItemLL.setVisibility(View.GONE);
            try {
                if (null != voiceMessage.getExtra()) {
                    JSONObject jsonObject = new JSONObject(voiceMessage.getExtra());
                    if ("".equals(jsonObject.getString("MessageText"))) {
                        viewHolder.mMsg.setVisibility(View.GONE);
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams1.setMargins(0, 0, 0, 0);
                        Drawable drawable = viewHolder.mContext.getResources().getDrawable(R.drawable.rc_ic_bubble_right_file);
                        viewHolder.mAudioLl.setBackground(drawable);
                        viewHolder.mAudioLl.setLayoutParams(layoutParams1);
                        viewHolder.mMeTimeTv.setText(voiceMessage.getDuration() + "\"");
                    } else {
                        viewHolder.mMsg.setVisibility(View.VISIBLE);
                        viewHolder.mMsg.setText(jsonObject.getString("MessageText"));
                        viewHolder.mMeTimeTv.setText(voiceMessage.getDuration() + "\"");
                    }
                } else {
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams2.setMargins(0, 0, 0, 0);
                    viewHolder.mMsg.setVisibility(View.GONE);
                    Drawable drawable = viewHolder.mContext.getResources().getDrawable(R.drawable.rc_ic_bubble_right_file);
                    viewHolder.mAudioLl.setBackground(drawable);
                    viewHolder.mAudioLl.setLayoutParams(layoutParams2);
                    viewHolder.mMeTimeTv.setText(voiceMessage.getDuration() + "\"");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            viewHolder.mReceiveItemLL.setVisibility(View.VISIBLE);
            viewHolder.mItemLL.setVisibility(View.GONE);
            String time = null;
            if (voiceMessage.getDuration() > 1000) {
                time = Utils.timeParse(voiceMessage.getDuration());
            }


            try {
                if (null != voiceMessage.getExtra()) {
                    JSONObject jsonObject = new JSONObject(voiceMessage.getExtra());
                    if ("".equals(jsonObject.getString("MessageText"))) {
                        viewHolder.mReceiveMsg.setVisibility(View.GONE);
                        Drawable drawable = viewHolder.mContext.getResources().getDrawable(R.drawable.rc_ic_bubble_left);
                        viewHolder.mPayLl.setBackground(drawable);
                        viewHolder.mPayLl.setLayoutParams(layoutParams);
                        if (voiceMessage.getDuration() > 1000) {
                            viewHolder.mTimeTv.setText(time + "\"");
                        } else {
                            viewHolder.mTimeTv.setText(voiceMessage.getDuration() + "\"");
                        }
                    } else {
                        viewHolder.mReceiveMsg.setVisibility(View.VISIBLE);
                        viewHolder.mReceiveMsg.setText(jsonObject.getString("MessageText"));
                        if (voiceMessage.getDuration() > 1000) {
                            viewHolder.mTimeTv.setText(time + "\"");
                        } else {
                            viewHolder.mTimeTv.setText(voiceMessage.getDuration() + "\"");
                        }
                    }
                } else {
                    viewHolder.mReceiveMsg.setVisibility(View.GONE);
                    Drawable drawable = viewHolder.mContext.getResources().getDrawable(R.drawable.rc_ic_bubble_left);
                    viewHolder.mPayLl.setBackground(drawable);
                    viewHolder.mPayLl.setLayoutParams(layoutParams);
                    if (voiceMessage.getDuration() > 1000) {
                        viewHolder.mTimeTv.setText(time + "\"");
                    } else {
                        viewHolder.mTimeTv.setText(voiceMessage.getDuration() + "\"");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Spannable getContentSummary(VoiceMessage customizeMessage) {
        return new SpannableString("[语音消息]");
    }

    @Override
    public void onItemClick(View view, int i, final VoiceMessage voiceMessage, final UIMessage uiMessage) {
        viewHolder = (ViewHolder) view.getTag();
        EventBus.getDefault().post(new SendMsgEvent(view, voiceMessage, uiMessage, viewHolder));
    }


    public class ViewHolder {
        TextView mMsg;
        LinearLayout mItemLL, mAudioLl;
        ImageView mPlayIv;
        SeekBar mSeekBar;
        Context mContext;
        TextView mMeTimeTv;

        TextView mReceiveMsg;
        LinearLayout mReceiveItemLL, mPayLl;
        ImageView mReceivePlayIv;
        SeekBar mReceiveSeekBar;
        TextView mTimeTv;
    }


    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_message, null);
        ViewHolder holder = new ViewHolder();
        holder.mContext = context;
        holder.mMsg = view.findViewById(R.id.tv_msg);
        holder.mItemLL = view.findViewById(R.id.ll_item);
        holder.mPlayIv = view.findViewById(R.id.iv_play);
        holder.mSeekBar = view.findViewById(R.id.seekBar);
        holder.mAudioLl = view.findViewById(R.id.ll_audio);
        holder.mMeTimeTv = view.findViewById(R.id.tv_me_time);

        holder.mReceiveMsg = view.findViewById(R.id.tv_receive_msg);
        holder.mReceiveItemLL = view.findViewById(R.id.ll_receive_item);//对方发送
        holder.mReceivePlayIv = view.findViewById(R.id.iv_receive_play);
        holder.mReceiveSeekBar = view.findViewById(R.id.receive_seekBar);
        holder.mTimeTv = view.findViewById(R.id.tv_time);
        holder.mPayLl = view.findViewById(R.id.ll_pay);
        view.setTag(holder);
        return view;
    }

}
