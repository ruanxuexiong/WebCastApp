package com.android.nana.eventBus;

import com.android.nana.wanted.Labels;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/22.
 */

public class LabelsSecEvent {

    public ArrayList<Labels.Sec> sec;

    public LabelsSecEvent(ArrayList<Labels.Sec> sec) {
        this.sec = sec;
    }
}
