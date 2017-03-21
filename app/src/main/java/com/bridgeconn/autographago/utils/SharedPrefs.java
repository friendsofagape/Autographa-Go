package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static SharedPreferences sharedPrefs;

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    public static Constants.FontSize getFontSize() {
        return Constants.FontSize.valueOf(sharedPrefs.getString(Constants.PrefKeys.FONT_SIZE, Constants.FontSize.Medium.name()));
    }

    public static void putFontSize(Constants.FontSize style) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.PrefKeys.FONT_SIZE, style.name());
        editor.commit();
    }

    public static Constants.ReadingMode getReadingMode() {
        return Constants.ReadingMode.valueOf(sharedPrefs.getString(Constants.PrefKeys.READING_MODE, Constants.ReadingMode.Day.name()));
    }

    public static void putReadingMode(Constants.ReadingMode mode) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.PrefKeys.READING_MODE, mode.name());
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

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sharedPrefs.getBoolean(key, defValue);
    }

    public static void putBooleanInstant(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void removeKey(String key) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(key);
        editor.apply();
    }
}