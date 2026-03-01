package com.example.yinpin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yinpin.entity.User;

public class CurrentUserUtils {

    private static final String CURRENT_USER = "CURRENT_USER";

    private final SharedPreferences sp;
    /**
     * 创建并获取单例
     */
    public static CurrentUserUtils getInstance() {
        return CurrentUserUtils.InstanceHolder.instance;
    }

    public CurrentUserUtils() {
        sp = AppUtils.getApplication().getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
    }

    public static User getCurrentUser() {
        String username = getInstance().sp.getString("username", null);
        String address =  getInstance().sp.getString("address", null);
        if (username == null) {
            return null;
        }
        User user = new User();
        user.setName(username);
        user.setAddress(address);
        return user;
    }

    public static void setCurrentUser(User user) {
        SharedPreferences.Editor editor =  getInstance().sp.edit();
        editor.putString("username", user.getName());
        editor.putString("address", user.getAddress());
        editor.apply();
    }

    private static final class InstanceHolder {
        /**
         * 单例
         */
        static final CurrentUserUtils instance = new CurrentUserUtils();
    }
}
