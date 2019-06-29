package com.android.nana.widget;

import com.android.nana.bean.FriendsBookEntity;

import java.util.Comparator;

/**
 * Created by lenovo on 2017/9/11.
 */

public class PinyinComparatorFriend implements Comparator<FriendsBookEntity> {
    @Override
    public int compare(FriendsBookEntity o1, FriendsBookEntity o2) {
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
