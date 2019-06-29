package com.android.nana.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nana.R;
import com.android.nana.mail.SealSearchConversationResult;
import com.android.nana.ui.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2017/9/13.
 */

public class ChattingRecordsAdapter extends BaseAdapter {
    private List<Message> mAdapterMessages;
    private Context mContext;
    private SealSearchConversationResult mResult;
    private String mFilterString;

    public ChattingRecordsAdapter(Context context, List<Message> mAdapterMessages, SealSearchConversationResult mResult, String mFilterString) {
        this.mAdapterMessages = mAdapterMessages;
        this.mContext = context;
        this.mResult = mResult;
        this.mFilterString = mFilterString;
    }

    @Override
    public int getCount() {
        if (mAdapterMessages != null) {
            return mAdapterMessages.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mAdapterMessages == null)
            return null;

        if (position >= mAdapterMessages.size())
            return null;

        return mAdapterMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChattingRecordsViewHolder viewHolder;
        Message message = (Message) getItem(position);
        if (convertView == null) {
            viewHolder = new ChattingRecordsViewHolder();
            convertView = View.inflate(mContext, R.layout.item_filter_chatting_records_list, null);
            viewHolder.portraitImageView = (RoundImageView) convertView.findViewById(R.id.item_iv_record_image);
            viewHolder.chatDetailLinearLayout = (LinearLayout) convertView.findViewById(R.id.item_ll_chatting_records_detail);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_tv_chat_name);
            viewHolder.chatRecordsDetailTextView = (TextView) convertView.findViewById(R.id.item_tv_chatting_records_detail);
            viewHolder.chatRecordsDateTextView = (TextView) convertView.findViewById(R.id.item_tv_chatting_records_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChattingRecordsViewHolder) convertView.getTag();
        }

        String id = mResult.getId();
        String title = mResult.getTitle();
        ImageLoader.getInstance().displayImage(mResult.getPortraitUri(), viewHolder.portraitImageView);
        viewHolder.nameTextView.setText(title);
        viewHolder.chatRecordsDetailTextView.setText(com.android.nana.user.CharacterParser.getInstance().getColoredChattingRecord(mFilterString, message.getContent()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date(message.getSentTime()));
        String formatDate = date.replace("-", "/");
        viewHolder.chatRecordsDateTextView.setText(formatDate);
        return convertView;
    }

    class ChattingRecordsViewHolder {
        RoundImageView portraitImageView;
        LinearLayout chatDetailLinearLayout;
        TextView nameTextView;
        TextView chatRecordsDetailTextView;
        TextView chatRecordsDateTextView;
    }
}
