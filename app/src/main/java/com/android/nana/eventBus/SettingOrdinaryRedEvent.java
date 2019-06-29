package com.android.nana.eventBus;

/**
 * Created by lenovo on 2019/1/3.
 */

public class SettingOrdinaryRedEvent {

    public String advertising;
    public String adv_type;
    public String adv_url;

    public SettingOrdinaryRedEvent(String advertising,String adv_type,String adv_url){
        this.advertising = advertising;
        this.adv_type = adv_type;
        this.adv_url = adv_url;
    }
}
