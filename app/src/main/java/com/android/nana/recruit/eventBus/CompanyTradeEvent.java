package com.android.nana.recruit.eventBus;

import com.android.nana.wanted.Labels;

/**
 * Created by lenovo on 2018/3/23.
 */

public class CompanyTradeEvent {

    public Labels.Sec label;

    public CompanyTradeEvent(Labels.Sec label) {
        this.label = label;
    }
}
