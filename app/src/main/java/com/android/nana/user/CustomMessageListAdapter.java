package com.android.nana.user;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.android.nana.R;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/10/10.
 */

public class CustomMessageListAdapter extends MessageListAdapter {


    private Context mContext;

    public CustomMessageListAdapter(Context context) {

        super(context);

        mContext = context;

    }


    @Override

    protected void bindView(View v, int position, UIMessage data) {

        super.bindView(v, position, data);

        ViewHolder holder = (ViewHolder) v.getTag();
        TextView message = holder.contentView.findViewById(android.R.id.text1);

        if (message == null) {
            return;
        } else if (data.getMessageDirection() == Message.MessageDirection.SEND) {
            message.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            message.setTextColor(ContextCompat.getColor(mContext, R.color.green_33));
        }

    }

}
