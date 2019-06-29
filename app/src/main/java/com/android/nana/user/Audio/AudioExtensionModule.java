package com.android.nana.user.Audio;

import com.android.nana.user.video.CustomVideoPlugin;

import java.util.ArrayList;
import java.util.List;

import io.rong.callkit.VideoPlugin;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.DefaultLocationPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;

/**
 * Created by lenovo on 2018/11/22.
 */

public class AudioExtensionModule extends DefaultExtensionModule {

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules = new ArrayList<>();

        IPluginModule image = new ImagePlugin();
        IPluginModule videoPlugin = new CustomVideoPlugin();
        IPluginModule audio = new io.rong.callkit.AudioPlugin();
        IPluginModule video = new VideoPlugin();
        IPluginModule recognizePlugin = new com.android.nana.user.Audio.AudioPlugin();
        IPluginModule file = new FilePlugin();
        IPluginModule longitude = new DefaultLocationPlugin();


        pluginModules.add(image);
        pluginModules.add(videoPlugin);
        pluginModules.add(audio);
        pluginModules.add(video);
        pluginModules.add(recognizePlugin);
        pluginModules.add(file);
        pluginModules.add(longitude);
        return pluginModules;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }

}
