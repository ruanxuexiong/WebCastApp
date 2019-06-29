package com.android.nana.material;

import java.util.List;

/**
 * Created by THINK on 2017/6/30.
 */

public class JsonBean implements IPickerViewData {


    private String id;
    private String name;

    public List<JsonBean.lists> getLists() {
        return lists;
    }

    public void setLists(List<JsonBean.lists> lists) {
        this.lists = lists;
    }

    private List<lists> lists;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getPickerViewText() {
        return this.name;
    }

    @Override
    public String getPickerViewId() {
        return this.id;
    }

    public static class lists implements IPickerViewData {


        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String id;
        private String name;

        public List<JsonBean.lists> getLists() {
            return lists;
        }

        public void setLists(List<JsonBean.lists> lists) {
            this.lists = lists;
        }

        private List<lists> lists;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String getPickerViewText() {
            return this.name;
        }

        @Override
        public String getPickerViewId() {
            return this.id;
        }
    }
}
