package com.android.nana.user.Audio;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.android.nana.R;
import com.android.nana.eventBus.AudioPluginEvent;

import org.greenrobot.eventbus.EventBus;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by lenovo on 2018/11/22.
 */

public class AudioPlugin  implements IPluginModule {

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.recognizer_voice_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_plugin_recognize);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {

        EventBus.getDefault().post(new AudioPluginEvent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
