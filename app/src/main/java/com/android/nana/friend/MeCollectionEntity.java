package com.android.nana.friend;

import java.util.ArrayList;

/**
 * Created by lenovo on 2019/3/11.
 */

public class MeCollectionEntity {



    private String month;
    private ArrayList<Article> article;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public ArrayList<Article> getArticle() {
        return article;
    }

    public void setArticle(ArrayList<Article> article) {
        this.article = article;
    }


    public class Article {

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String id;
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        private String type;
        private ArrayList<Pictures> userArticlePictures;

        public ArrayList<Pictures> getUserArticlePictures() {
            return userArticlePictures;
        }

        public void setUserArticlePictures(ArrayList<Pictures> userArticlePictures) {
            this.userArticlePictures = userArticlePictures;
        }

        public class Pictures {
            private String id;
            private String userArticleId;
            private String path;
            private String percent;
            private String width;
            private String height;

            public String getPicture_url() {
                return picture_url;
            }

            public void setPicture_url(String picture_url) {
                this.picture_url = picture_url;
            }

            private String picture_url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserArticleId() {
                return userArticleId;
            }

            public void setUserArticleId(String userArticleId) {
                this.userArticleId = userArticleId;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getPercent() {
                return percent;
            }

            public void setPercent(String percent) {
                this.percent = percent;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }
        }
    }
}
