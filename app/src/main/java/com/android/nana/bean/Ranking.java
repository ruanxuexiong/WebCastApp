package com.android.nana.bean;

import java.util.List;

public class Ranking {

    /**
     * data : [{"from_uid":"27694","earnings":"62","total":"62","index":1,"username":"阮学雄","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5cac053134a68.jpg"},{"from_uid":"17672","earnings":"23","total":"23","index":2,"username":"世间事","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5caf5b7789db6.jpg"},{"from_uid":"7031","earnings":"21","total":"21","index":3,"username":"梁俊杰","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5c9c97a16d8fa.jpg"},{"from_uid":"7100","earnings":"5","total":"8","index":4,"username":"even","avatar":"http://qiniu.nanapal.com/7100_5cc002c57c1e5.jpg"},{"from_uid":"8126","earnings":"8","total":"8","index":5,"username":"May","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5c9b129e421c7.jpg"},{"from_uid":"8657","earnings":"7","total":"7","index":6,"username":"南大王","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5c9201cd09299.jpg"},{"from_uid":"26853","earnings":"2","total":"2","index":7,"username":"MMGG","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5ca039e222da4.jpg"},{"from_uid":"7118","earnings":"2","total":"2","index":8,"username":"特色美食","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5c887d562dfd7.jpg"},{"from_uid":"7418","earnings":"2","total":"2","index":9,"username":"Joker","avatar":"http://119.23.13.73/zbnew/data/upload/avatar/5c875b885b4f3.jpg"},{"from_uid":"7115","earnings":"2","total":"2","index":10,"username":"FT11157","avatar":"http://119.23.13.73/data/upload/share/person.png"}]
     * result : {"state":0,"description":"ok"}
     */

    private ResultBean result;
    private List<DataBean> data;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class ResultBean {
        /**
         * state : 0
         * description : ok
         */

        private int state;
        private String description;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class DataBean {
        /**
         * from_uid : 27694
         * earnings : 62
         * total : 62
         * index : 1
         * username : 阮学雄
         * avatar : http://119.23.13.73/zbnew/data/upload/avatar/5cac053134a68.jpg
         */

        private String from_uid;
        private String earnings;
        private String total;
        private int index;
        private String username;
        private String avatar;

        public String getFrom_uid() {
            return from_uid;
        }

        public void setFrom_uid(String from_uid) {
            this.from_uid = from_uid;
        }

        public String getEarnings() {
            return earnings;
        }

        public void setEarnings(String earnings) {
            this.earnings = earnings;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
