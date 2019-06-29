package com.android.nana.bean;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/8/11.
 */

public class YearEntity {

    private String year;

    public ArrayList<YearEntity.lists> getLists() {
        return lists;
    }

    public void setLists(ArrayList<YearEntity.lists> lists) {
        this.lists = lists;
    }

    private ArrayList<YearEntity.lists> lists;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static class lists {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        private String month;
    }


}
