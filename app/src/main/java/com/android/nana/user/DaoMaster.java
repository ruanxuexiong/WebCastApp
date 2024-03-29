package com.android.nana.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import io.rong.common.RLog;

/**
 * Created by lenovo on 2017/9/8.
 */

public class DaoMaster extends AbstractDaoMaster {

    public static final int SCHEMA_VERSION = 9;
    private final static String TAG = "DaoMaster";
    /**
     * Creates underlying database table using DAOs.
     */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        RLog.d(TAG, "DaoMaster createAllTables");
        FriendDao.createTable(db, ifNotExists);
        GroupsDao.createTable(db, ifNotExists);
        BlackListDao.createTable(db, ifNotExists);
        GroupMemberDao.createTable(db, ifNotExists);
    }

    /**
     * Drops underlying database table using DAOs.
     */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        RLog.d(TAG, "DaoMaster dropAllTables");
        FriendDao.dropTable(db, ifExists);
        GroupsDao.dropTable(db, ifExists);
        BlackListDao.dropTable(db, ifExists);
        GroupMemberDao.dropTable(db, ifExists);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            RLog.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     */
    public static class DevOpenHelper extends OpenHelper {
        private Context mContext;
        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
            mContext = context;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            RLog.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            SharedPreferences sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
            sp.edit().putInt("getAllUserInfoState", 0).apply();
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        RLog.d(TAG, "DaoMaster init");
        registerDaoClass(FriendDao.class);
        registerDaoClass(GroupsDao.class);
        registerDaoClass(BlackListDao.class);
        registerDaoClass(GroupMemberDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
}
