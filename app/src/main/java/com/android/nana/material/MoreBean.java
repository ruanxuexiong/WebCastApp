package com.android.nana.material;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/2.
 * 更多标签
 */

public class MoreBean {

    private ArrayList<Profession> profession;

    public ArrayList<Profession> getProfession() {
        return profession;
    }

    public void setProfession(ArrayList<Profession> profession) {
        this.profession = profession;
    }

    public class Profession {
        private String id;
        private String name;
        private String listorder;
        private String parentid;

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

        public String getListorder() {
            return listorder;
        }

        public void setListorder(String listorder) {
            this.listorder = listorder;
        }

        public String getParentid() {
            return parentid;
        }

        public void setParentid(String parentid) {
            this.parentid = parentid;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        private String parentName;
    }
}
