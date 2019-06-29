package com.android.nana.bean;

public class ArticleByTopic {

    /**
     * data : {"tag_info":{"tag_id":"1","tag_name":" PHP"},"user":{"username":"even","avatar":"http://qiniu.nanapal.com/7100_5cc002c57c1e5.jpg"},"article":{"content":"@jb哈哈：51和@twitter@在研究用# PHP#的#正则表达式#过   滤话题和","id":"3885"},"tag_count":"3","view_count":"540"}
     * result : {"state":0,"description":""}
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
        /**
         * tag_info : {"tag_id":"1","tag_name":" PHP"}
         * user : {"username":"even","avatar":"http://qiniu.nanapal.com/7100_5cc002c57c1e5.jpg"}
         * article : {"content":"@jb哈哈：51和@twitter@在研究用# PHP#的#正则表达式#过   滤话题和","id":"3885"}
         * tag_count : 3
         * view_count : 540
         */

        private TagInfoBean tag_info;
        private UserBean user;
        private ArticleBean article;
        private String tag_count;
        private String view_count;

        public TagInfoBean getTag_info() {
            return tag_info;
        }

        public void setTag_info(TagInfoBean tag_info) {
            this.tag_info = tag_info;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public ArticleBean getArticle() {
            return article;
        }

        public void setArticle(ArticleBean article) {
            this.article = article;
        }

        public String getTag_count() {
            return tag_count;
        }

        public void setTag_count(String tag_count) {
            this.tag_count = tag_count;
        }

        public String getView_count() {
            return view_count;
        }

        public void setView_count(String view_count) {
            this.view_count = view_count;
        }

        public static class TagInfoBean {
            /**
             * tag_id : 1
             * tag_name :  PHP
             */

            private String tag_id;
            private String tag_name;

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }
        }

        public static class UserBean {
            /**
             * username : even
             * avatar : http://qiniu.nanapal.com/7100_5cc002c57c1e5.jpg
             */

            private String username;
            private String avatar;

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

        public static class ArticleBean {
            /**
             * content : @jb哈哈：51和@twitter@在研究用# PHP#的#正则表达式#过   滤话题和
             * id : 3885
             */

            private String content;
            private String id;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

    public static class ResultBean {
        /**
         * state : 0
         * description :
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
