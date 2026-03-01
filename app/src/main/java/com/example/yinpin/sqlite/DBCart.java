package com.example.yinpin.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.yinpin.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBCart {

    /**
     * 添加商品到购物车，如果已存在则数量+1
     */
    public static boolean add(String id, String username) {
        try {
            // 先检查是否已存在
            int currentQuantity = getQuantity(id, username);
            
            if (currentQuantity > 0) {
                // 已存在，数量+1
                boolean result = updateQuantity(id, username, currentQuantity + 1);
                Log.d("DBCart", "更新商品数量: " + id + ", 新数量: " + (currentQuantity + 1) + ", 结果: " + result);
                return result;
            } else {
                // 不存在，新增
                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("username", username);
                values.put("quantity", 1);
                long i = SqliteUtils.getInstance().getWritableDatabase().insert("cart", null, values);
                if (i > 0) {
                    Log.d("DBCart", "插入成功，商品ID: " + id);
                    return true;
                } else {
                    Log.e("DBCart", "插入失败，商品ID: " + id);
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("DBCart", "添加商品到购物车失败", e);
            return false;
        }
    }

    /**
     * 获取购物车中某商品的数量
     */
    public static int getQuantity(String id, String username) {
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "cart",
                new String[]{"quantity"},
                "id=? and username=?",
                new String[]{id, username},
                null, null, null
        );
        
        int quantity = 0;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") 
            int quantityIndex = cursor.getColumnIndex("quantity");
            if (quantityIndex >= 0) {
                quantity = cursor.getInt(quantityIndex);
            } else {
                // 如果没有quantity列，返回1（兼容旧数据）
                quantity = 1;
            }
        }
        cursor.close();
        return quantity;
    }

    /**
     * 更新购物车中某商品的数量
     */
    public static boolean updateQuantity(String id, String username, int quantity) {
        if (quantity <= 0) {
            // 数量为0或负数，直接删除
            return del(id, username);
        }
        
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        int i = SqliteUtils.getInstance().getWritableDatabase().update(
                "cart",
                values,
                "id=? and username=?",
                new String[]{id, username}
        );
        if (i > 0) {
            Log.d("DBCart", "更新数量成功");
            return true;
        }
        return false;
    }

    public static boolean del(String id, String username) {
        long i = SqliteUtils.getInstance().getWritableDatabase().delete("cart", "id=? and username=?", new String[]{id, username});
        if (i > 0) {
            Log.d("DBCart", "删除成功");
            return true;
        }
        return false;
    }

    /**
     * 获取用户购物车中的所有商品ID（不重复）
     */
    public static ArrayList<String> getLikesTitle(String username) {
        ArrayList<String> array = new ArrayList<>();

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query("cart", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("username"));
            if (name.equals(username)) {
                array.add(id);
            }
        }
        cursor.close();
        return array;
    }

    /**
     * 获取用户购物车中的所有商品ID及其数量
     */
    public static Map<String, Integer> getCartWithQuantity(String username) {
        Map<String, Integer> cartMap = new HashMap<>();

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "cart",
                new String[]{"id", "quantity"},
                "username=?",
                new String[]{username},
                null, null, null
        );
        
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") 
            int quantityIndex = cursor.getColumnIndex("quantity");
            int quantity = 1; // 默认数量为1
            if (quantityIndex >= 0) {
                quantity = cursor.getInt(quantityIndex);
            }
            cartMap.put(id, quantity);
        }
        cursor.close();
        return cartMap;
    }
}
