package com.android.nana.user.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.android.nana.R;
import com.android.nana.eventBus.CustomVideoEvent;

import org.greenrobot.eventbus.EventBus;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by lenovo on 2018/11/26.
 */

public class CustomVideoPlugin implements IPluginModule {

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.custom_vide_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_custom_video_plugin_recognize);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        EventBus.getDefault().post(new CustomVideoEvent());
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
