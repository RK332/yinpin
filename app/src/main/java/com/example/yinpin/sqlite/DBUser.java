package com.example.yinpin.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.yinpin.entity.User;
import com.example.yinpin.utils.SqliteUtils;

public class DBUser {

    public static boolean add(User u) {
        ContentValues values = new ContentValues();
        values.put("name", u.getName());
        values.put("psw", u.getPsw());
        values.put("address", u.getAddress());
        long i = SqliteUtils.getInstance().getWritableDatabase().insert("user", null, values);
        if (i > 0) {
            Log.d("", "插入成功");
            return true;
        }
        return false;
    }

    public static boolean change(User u) {
        ContentValues values = new ContentValues();
        values.put("address", u.getAddress());
        long i = SqliteUtils.getInstance().getWritableDatabase().update("user", values, "name=?", new String[]{u.getName()});
        if (i > 0) {
            Log.d("", "修改成功");
            return true;
        }
        return false;
    }

    public static boolean check(String n, String p) {
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query("user", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String psw = cursor.getString(cursor.getColumnIndex("psw"));
            if (n.equals(name) && p.equals(psw)) {
                return true;
            }
        }
        return false;
    }

    public static User get(String n) {
        User u = new User();
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query("user", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String psw = cursor.getString(cursor.getColumnIndex("psw"));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
            if (n.equals(name)) {
                u = new User(name, psw, address);
                break;
            }
        }
        return u;
    }
}
