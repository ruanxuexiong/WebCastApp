package com.android.nana.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/19 0019.
 */

public class CategoryInfoEntity {

    private String id;
    private String name;
    private String picture;
    private List<CategoryEntity> list;

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<CategoryEntity> getList() {
        return list;
    }

    public void setList(List<CategoryEntity> list) {
        this.list = list;
    }

    public CategoryInfoEntity(String id, String name, String picture, List<CategoryEntity> list) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.list = list;
    }

    @Override
    public String toString() {
        return name;
    }
}
