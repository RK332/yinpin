package com.example.yinpin.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class SqliteUtils extends SQLiteOpenHelper {

    private static final String TAG = "SqliteUtils";
    private static final String OLD_DB_NAME = "flower_shop.db";
    private static final String NEW_DB_NAME = "YinPin.db";

    public SqliteUtils() {
        // 数据库名修改为与项目名一致的 YinPin.db
        super(AppUtils.getApplication(), NEW_DB_NAME, null, 6);
    }


    /**
     * 创建并获取单例
     */
    public static SqliteUtils getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 如有需要，将旧数据库 flower_shop.db 中的数据迁移到新数据库 YinPin.db
     * 仅在本地存在旧数据库文件时执行一次，迁移成功后会删除旧库文件
     */
    public static void migrateOldDatabaseIfNeeded() {
        File oldDbFile = AppUtils.getApplication().getDatabasePath(OLD_DB_NAME);
        File newDbFile = AppUtils.getApplication().getDatabasePath(NEW_DB_NAME);

        // 没有旧库，直接返回
        if (oldDbFile == null || !oldDbFile.exists()) {
            return;
        }

        Log.d(TAG, "检测到旧数据库文件，开始迁移: " + oldDbFile.getAbsolutePath());

        SQLiteDatabase oldDb = null;
        SQLiteDatabase newDb = null;

        try {
            // 确保新库和表结构已经创建
            getInstance().getWritableDatabase();

            // 打开旧库（只读）和新库（可写）
            oldDb = SQLiteDatabase.openDatabase(oldDbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
            newDb = getInstance().getWritableDatabase();

            newDb.beginTransaction();

            // 迁移 user 表
            copyTable(oldDb, newDb, "user", new String[]{"name", "psw", "address"});
            // 迁移 stuff 表（如果你不想迁移商品，可以注释掉这一行）
            copyTable(oldDb, newDb, "stuff", new String[]{"id", "name", "title", "kind", "price"});
            // 迁移 record 表
            copyTable(oldDb, newDb, "record", new String[]{"username", "id", "name", "price", "address"});
            // 迁移 cart 表
            copyTable(oldDb, newDb, "cart", new String[]{"id", "username"});

            newDb.setTransactionSuccessful();
            Log.d(TAG, "数据库迁移完成，准备删除旧数据库文件");
        } catch (Exception e) {
            Log.e(TAG, "迁移旧数据库失败", e);
        } finally {
            if (newDb != null) {
                try {
                    newDb.endTransaction();
                } catch (Exception ignored) {}
            }
            if (oldDb != null && oldDb.isOpen()) {
                oldDb.close();
            }
            if (newDb != null && newDb.isOpen()) {
                newDb.close();
            }
        }

        // 如果迁移过程中没有抛异常，则尝试删除旧数据库文件
        if (oldDbFile.exists()) {
            boolean deleted = oldDbFile.delete();
            Log.d(TAG, "旧数据库文件删除结果: " + deleted);
        }
    }

    /**
     * 从旧库中将某张表的数据复制到新库
     */
    private static void copyTable(SQLiteDatabase oldDb, SQLiteDatabase newDb, String tableName, String[] columns) {
        // 检查旧库中是否存在该表
        Cursor checkCursor = null;
        try {
            checkCursor = oldDb.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{tableName}
            );
            if (checkCursor == null || !checkCursor.moveToFirst()) {
                Log.w(TAG, "旧库中不存在表，跳过迁移: " + tableName);
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "检查旧库表失败，跳过: " + tableName, e);
            return;
        } finally {
            if (checkCursor != null) {
                checkCursor.close();
            }
        }

        Cursor cursor = null;
        try {
            cursor = oldDb.query(tableName, columns, null, null, null, null, null);
            int count = 0;
            while (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                for (String column : columns) {
                    int index = cursor.getColumnIndex(column);
                    if (index >= 0) {
                        values.put(column, cursor.getString(index));
                    }
                }
                long result = newDb.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (result != -1) {
                    count++;
                }
            }
            Log.d(TAG, "表 " + tableName + " 迁移完成，成功插入条数: " + count);
        } catch (Exception e) {
            Log.e(TAG, "迁移表失败: " + tableName, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(name text primary key,psw text not null,address text not null)");
        db.execSQL("create table stuff(id INTEGER primary key AUTOINCREMENT,name text not null,title text not null,kind text not null,price text not null)");
        db.execSQL("create table record(username text not null,id text not null,name text not null,price text not null,address text not null)");
        db.execSQL("create table cart(id text not null,username text not null,quantity INTEGER DEFAULT 1,PRIMARY KEY(id,username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级到版本5时，强制清除所有商品数据并重建表，确保删除所有旧的数据
        if (oldVersion < 5) {
            // 删除旧的商品表
            db.execSQL("DROP TABLE IF EXISTS stuff");
            // 重新创建商品表
            db.execSQL("create table stuff(id INTEGER primary key AUTOINCREMENT,name text not null,title text not null,kind text not null,price text not null)");
        }
        // 升级到版本6时，为购物车表添加数量字段
        if (oldVersion < 6) {
            try {
                // 尝试添加quantity列（如果已存在会失败，但不影响）
                db.execSQL("ALTER TABLE cart ADD COLUMN quantity INTEGER DEFAULT 1");
            } catch (Exception e) {
                Log.e(TAG, "添加quantity列失败，可能已存在", e);
            }
        }
        // 如果是全新安装，onCreate会创建所有表
    }

    private static final class InstanceHolder {
        /**
         * 单例
         */
        static final SqliteUtils instance = new SqliteUtils();
    }

}
