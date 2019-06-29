package com.android.nana.downmenu;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/4.
 */

public class ProvinceBean {

    private ArrayList<Area> area;

    public ArrayList<Area> getArea() {
        return area;
    }

    public void setArea(ArrayList<Area> area) {
        this.area = area;
    }

    public class Area {
        private String id;
        private String parentId;
        private String name;
        private String type;
        private ArrayList<City> lists;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ArrayList<City> getLists() {
            return lists;
        }

        public void setLists(ArrayList<City> lists) {
            this.lists = lists;
        }


        public class City {
            private String id;
            private String parentId;
            private String name;
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
