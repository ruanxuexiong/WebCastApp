package com.android.nana.user;

import java.util.List;

import io.rong.callkit.AudioPlugin;
import io.rong.callkit.VideoPlugin;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by lenovo on 2017/12/6.
 */

public class MyExtensionModule extends DefaultExtensionModule {

    @Override

    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {

        List<IPluginModule> pluginModules = super.getPluginModules(conversationType);

        com.android.nana.user.Audio.AudioPlugin recognizePlugin = new com.android.nana.user.Audio.AudioPlugin();
        pluginModules.add(recognizePlugin);

        for (int i = 0; i < pluginModules.size(); i++) {
            if (pluginModules.get(i) instanceof AudioPlugin) {
                pluginModules.remove(i);
                break;
            }
        }

        for (int x = 0; x < pluginModules.size(); x++) {
            if (pluginModules.get(x) instanceof VideoPlugin) {
                pluginModules.remove(x);
                break;
            }
        }

        return pluginModules;
    }


    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }
}
