package com.android.nana.eventBus;

import com.amap.api.services.help.Tip;

/**
 * Created by lenovo on 2018/12/3.
 */

public class TipEvent {

    public Tip tip;
    public TipEvent(Tip tip){
        this.tip = tip;
    }
}
