package com.android.nana.bean;

import java.util.List;

public class NoticeEntity {

    /**
     * data : {"notices":[{"id":"8","userId":"27694","type":"0","status":"0"},{"id":"7","userId":"27694","type":"1","status":"0"},{"id":"6","userId":"27694","type":"2","status":"1"}]}
     * result : {"state":0,"description":"成功"}
     */

    private DataBean data;
    private ResultBean result;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class DataBean {
        private List<NoticesBean> notices;

        public List<NoticesBean> getNotices() {
            return notices;
        }

        public void setNotices(List<NoticesBean> notices) {
            this.notices = notices;
        }

        public static class NoticesBean {
            /**
             * id : 8
             * userId : 27694
             * type : 0
             * status : 0
             */

            private String id;
            private String userId;
            private String type;
            private String status;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }

    public static class ResultBean {
        /**
         * state : 0
         * description : 成功
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
}
