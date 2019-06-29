package com.android.nana.main;

import java.util.ArrayList;

/**
 * Created by lenovo on 2018/1/3.
 */

public class NavigationBean {

    private ArrayList<OneNav> nav;

    public ArrayList<OneNav> getNav() {
        return nav;
    }

    public void setNav(ArrayList<OneNav> nav) {
        this.nav = nav;
    }

    public class OneNav {
        private String id;
        private String name;
        private String parentid;
        private String titcolor;
        private ArrayList<TwoNav> nav;

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

        public String getParentid() {
            return parentid;
        }

        public void setParentid(String parentid) {
            this.parentid = parentid;
        }

        public String getTitcolor() {
            return titcolor;
        }

        public void setTitcolor(String titcolor) {
            this.titcolor = titcolor;
        }

        public ArrayList<TwoNav> getNav() {
            return nav;
        }

        public void setNav(ArrayList<TwoNav> nav) {
            this.nav = nav;
        }


        public class TwoNav {
            private String id;
            private String name;
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

            public String getParentid() {
                return parentid;
            }

            public void setParentid(String parentid) {
                this.parentid = parentid;
            }
        }
    }
}
