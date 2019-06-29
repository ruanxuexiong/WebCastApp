package com.android.nana.eventBus;

import com.dpizarro.autolabel.library.AutoLabelUI;

/**
 * Created by THINK on 2017/7/20.
 */

public class LabelEvent {

    public AutoLabelUI mAutoLabel;

    public LabelEvent(AutoLabelUI mAutoLabel) {
        this.mAutoLabel = mAutoLabel;
    }
}
