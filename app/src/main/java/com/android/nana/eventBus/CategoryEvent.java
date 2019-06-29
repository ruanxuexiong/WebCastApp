package com.android.nana.eventBus;

import com.android.common.models.DBModel;

/**
 * Created by lenovo on 2017/7/30.
 */

public class CategoryEvent {
    public String name;
    public String categoryId;
    public String childCategoryId;
    public DBModel dbModel;

    public CategoryEvent(String name, String categoryId, String childCategoryId, DBModel dbModel) {
        this.name = name;
        this.categoryId = categoryId;
        this.childCategoryId = childCategoryId;
        this.dbModel = dbModel;
    }
}
