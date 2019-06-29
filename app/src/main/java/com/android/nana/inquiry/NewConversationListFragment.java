package com.android.nana.inquiry;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.nana.R;
import com.android.nana.user.CustomMessageListAdapter;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Message;

/**
 * Created by lenovo on 2018/4/11.
 */

public class NewConversationListFragment extends ConversationFragment {


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.rc_extension).setVisibility(View.GONE);
    }

    @Override
    public void onResendItemClick(Message message) {
        super.onResendItemClick(message);
    }




    @Override
    public void onSendToggleClick(View v, String text) {
        super.onSendToggleClick(v, text);
    }

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return new CustomMessageListAdapter(context);
    }

}
