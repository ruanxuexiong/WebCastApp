package com.android.nana.bean;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by lenovo on 2018/9/20.
 */

public class SendMsgViewHolder {
    private TextView mMsg;
    private LinearLayout mItemLL;
    private ImageView mPlayIv;
    private SeekBar mSeekBar;

    private TextView mReceiveMsg;
    private LinearLayout mReceiveItemLL;
    private ImageView mReceivePlayIv;
    private SeekBar mReceiveSeekBar;

    public TextView getmMsg() {
        return mMsg;
    }

    public void setmMsg(TextView mMsg) {
        this.mMsg = mMsg;
    }

    public LinearLayout getmItemLL() {
        return mItemLL;
    }

    public void setmItemLL(LinearLayout mItemLL) {
        this.mItemLL = mItemLL;
    }

    public ImageView getmPlayIv() {
        return mPlayIv;
    }

    public void setmPlayIv(ImageView mPlayIv) {
        this.mPlayIv = mPlayIv;
    }

    public SeekBar getmSeekBar() {
        return mSeekBar;
    }

    public void setmSeekBar(SeekBar mSeekBar) {
        this.mSeekBar = mSeekBar;
    }

    public TextView getmReceiveMsg() {
        return mReceiveMsg;
    }

    public void setmReceiveMsg(TextView mReceiveMsg) {
        this.mReceiveMsg = mReceiveMsg;
    }

    public LinearLayout getmReceiveItemLL() {
        return mReceiveItemLL;
    }

    public void setmReceiveItemLL(LinearLayout mReceiveItemLL) {
        this.mReceiveItemLL = mReceiveItemLL;
    }

    public ImageView getmReceivePlayIv() {
        return mReceivePlayIv;
    }

    public void setmReceivePlayIv(ImageView mReceivePlayIv) {
        this.mReceivePlayIv = mReceivePlayIv;
    }

    public SeekBar getmReceiveSeekBar() {
        return mReceiveSeekBar;
    }

    public void setmReceiveSeekBar(SeekBar mReceiveSeekBar) {
        this.mReceiveSeekBar = mReceiveSeekBar;
    }


}
