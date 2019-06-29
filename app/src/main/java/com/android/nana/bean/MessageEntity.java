package com.android.nana.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
@Table(name = "MessageEntity")
public class MessageEntity {

        @Id
        private int id;

        @Column(column = "json")
        private String json;

        public MessageEntity() {

        }

        public MessageEntity(String json) {
            super();
            this.json = json;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

}
