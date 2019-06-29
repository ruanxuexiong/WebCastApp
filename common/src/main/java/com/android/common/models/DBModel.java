package com.android.common.models;

import android.text.TextUtils;
import android.util.Log;

import com.android.common.BaseApplication;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class DBModel {

    public int id;

    @Column(column = "Key")
    public String Key;

    @Column(column = "Description")
    public String Description;

    public static void saveOrUpdate(String name, String value) {
        try {
            DBModel model = BaseApplication.getInstance().mDbUtils
                    .findFirst(Selector.from(DBModel.class).where("Key", "=", name));
            if (model == null) {
                model = new DBModel();
            }

            model.Key = name;
            model.Description = value;
            BaseApplication.getInstance().mDbUtils.saveOrUpdate(model);

        } catch (DbException e) {

        } catch (Exception e) {
            Log.i("errohahhahahhahahhahahr", e.getMessage());
        }
    }

    public static DBModel get(String name) {

        if (TextUtils.isEmpty(name)) return null;

        try {
            DBModel model = BaseApplication.getInstance().mDbUtils
                    .findFirst(Selector.from(DBModel.class).where("Key", "=", name));

            return model;

        } catch (DbException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }


}
