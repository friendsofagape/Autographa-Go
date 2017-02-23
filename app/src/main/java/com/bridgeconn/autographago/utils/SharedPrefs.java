package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static SharedPreferences sharedPrefs;

    private final static String FONT_SIZE = "FONT_SIZE";

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    public static Constants.FontSize getFontSize() {
        return Constants.FontSize.valueOf(sharedPrefs.getString(FONT_SIZE, Constants.FontSize.Medium.name()));
    }

    public static void setFontSize(Constants.FontSize style) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(FONT_SIZE, style.name());
        editor.commit();
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

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defValue) {
        return sharedPrefs.getInt(key, defValue);
    }

    public static void putIntInstant(String key, int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}