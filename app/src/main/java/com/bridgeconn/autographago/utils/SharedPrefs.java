package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static SharedPreferences sharedPrefs;

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

    public static void putStringInstant(String key, String value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
}