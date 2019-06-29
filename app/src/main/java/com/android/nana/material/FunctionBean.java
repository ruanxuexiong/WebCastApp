package com.android.nana.material;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/12/29.
 */

public class FunctionBean {

    public class Profession {
        private String id;
        private String name;
        private String listorder;
        private String parentid;
        private ArrayList<LabelPro> next_pro;
        private String next_pro_num;

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

        public ArrayList<LabelPro> getNext_pro() {
            return next_pro;
        }

        public void setNext_pro(ArrayList<LabelPro> next_pro) {
            this.next_pro = next_pro;
        }

        public String getNext_pro_num() {
            return next_pro_num;
        }

        public void setNext_pro_num(String next_pro_num) {
            this.next_pro_num = next_pro_num;
        }


        public class LabelPro {
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

            public String getChoosed() {
                return choosed;
            }

            public void setChoosed(String choosed) {
                this.choosed = choosed;
            }

            private String choosed;
        }
    }
}
