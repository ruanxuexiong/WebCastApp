package com.android.nana.widget;

import com.android.nana.bean.SortEntity;

import java.util.Comparator;

/**
 * Created by lenovo on 2017/8/30.
 */

public class PinyinComparator implements Comparator<SortEntity> {

    public int compare(SortEntity o1, SortEntity o2) {
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
