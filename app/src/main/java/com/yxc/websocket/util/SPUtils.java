package com.yxc.websocket.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yxc.websocket.base.BaseApplication;


public class SPUtils {
    private static final String file_name = "sp_wbsocket";
    private static final int sp_mode = Context.MODE_PRIVATE;
    private static Context mcontext = BaseApplication.getAppContext();

    public static boolean put(String key, Object value) {
        SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
        SharedPreferences.Editor edit = preferences.edit();

        if (value instanceof String) {
            if (!TextUtils.isEmpty((CharSequence) value)) {
                edit.putString(key, (String) value);
            }
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);

        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);

        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else {
            edit.putLong(key, (Long) value);
        }

        boolean commit = edit.commit();
        return commit;
    }


    public static String getString(String key, String defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getString(key, defualt);
    }

    public static int getInt(String key, int defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getInt(key, defualt);
    }

    public static boolean getBoolean(String key, boolean defualt) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(file_name, sp_mode);
        return sharedPreferences.getBoolean(key, defualt);
    }

    public static void remove(String key) {
        SharedPreferences preferences = mcontext.getSharedPreferences(file_name, sp_mode);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(key);
        edit.commit();

    }
}
