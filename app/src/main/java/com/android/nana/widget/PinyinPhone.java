package com.android.nana.widget;

import com.android.nana.bean.PhoneEntity;

import java.util.Comparator;

/**
 * Created by lenovo on 2019/1/8.
 */

public class PinyinPhone implements Comparator<PhoneEntity> {
    
    @Override
    public int compare(PhoneEntity o1, PhoneEntity o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
