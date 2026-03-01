package com.example.yinpin.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.yinpin.entity.Record;
import com.example.yinpin.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.List;

public class DBRecord {

    public static boolean add(Record s){
        ContentValues values=new ContentValues();
        values.put("username",s.getUsername());
        values.put("id",s.getId());
        values.put("name",s.getName());
        values.put("price",s.getPrice());
        values.put("address",s.getAddress());
        long i= SqliteUtils.getInstance().getWritableDatabase().insert("record",null,values);
        if(i>0){
            Log.d("","插入成功");
            return true;
        }
        Log.d("","插入失败");
        return false;
    }

    public static List<Record> getAll(String user){
        List<Record> array=new ArrayList<>();
        Cursor cursor=SqliteUtils.getInstance().getReadableDatabase().query("record",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            @SuppressLint("Range") String username=cursor.getString( cursor.getColumnIndex("username"));
            @SuppressLint("Range") String id=cursor.getString( cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name=cursor.getString( cursor.getColumnIndex("name"));
            @SuppressLint("Range") String address=cursor.getString( cursor.getColumnIndex("address"));
            @SuppressLint("Range") String price=cursor.getString( cursor.getColumnIndex("price"));
            if(user.equals(username)){
                Record u=new Record(username,id,name ,price,address);
                array.add(u);
            }
        }
        return array;
    }

    public static boolean delete(String username, String id, String price, String address) {
        long i = SqliteUtils.getInstance().getWritableDatabase().delete("record",
                "username=? and id=? and price=? and address=?",
                new String[]{username, id, price, address});
        if (i > 0) {
            Log.d("DBRecord", "删除成功");
            return true;
        }
        return false;
    }
}

