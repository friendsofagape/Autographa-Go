package com.bridgeconn.autographago.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class UtilFunctions {

    public static void applyReadingMode() {
        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            }
            case Night: {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            }
        }
    }

    public static void pullDB(String sDBName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "data/"+"com.bridgeconn.autographago"+"/files/"+ sDBName;
                String backupDBPath = Constants.STORAGE_DIRECTORY + sDBName;
                File dir = new File(sd,backupDBPath.replace(sDBName,""));
                if(dir.mkdir()) {

                }
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            } else {
                Log.e(Constants.DUMMY_TAG, "sd cannot write");
            }
        } catch (Exception e) {
            Log.e(Constants.DUMMY_TAG, e.toString());
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = activity.getCurrentFocus();
        }
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = activity.getCurrentFocus();
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static String getBookNameFromMapping(Context context, String bookId) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("mappings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject idObject = jsonObject.getJSONObject("id_name_map");
            JSONObject valueObject = idObject.getJSONObject(bookId);
            return valueObject.getString("book_name");
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public static int getBookNumberFromMapping(Context context, String bookId) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("mappings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject idObject = jsonObject.getJSONObject("id_name_map");
            JSONObject valueObject = idObject.getJSONObject(bookId);
            return valueObject.getInt("number");
        } catch (JSONException je) {
            je.printStackTrace();
            return -1;
        }
    }

    public static String getBookSectionFromMapping(Context context, String bookId) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("mappings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject idObject = jsonObject.getJSONObject("id_name_map");
            JSONObject valueObject = idObject.getJSONObject(bookId);
            return valueObject.getString("section");
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static String getVersionNameFromCode(String versionCode) {
        switch (versionCode) {
            case Constants.VersionCodes.ULB: {
                return Constants.VersionNames.ULB;
            }
            case Constants.VersionCodes.UDB: {
                return Constants.VersionNames.UDB;
            }
        }
        return Constants.VersionNames.ULB;
    }

    public static String getPlainVerseText(String verseText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("");
        if (verseText != null) {
            if (!verseText.trim().equals("")) {
                String[] splitString = verseText.split(Constants.Styling.SPLIT_SPACE);
                for (int n = 0; n < splitString.length; n++) {
                    switch (splitString[n]) {
                        case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                            break;
                        }
                        case Constants.Styling.MARKER_Q: {
                            spannableStringBuilder.append(Constants.Styling.NEW_LINE_WITH_TAB_SPACE);
                            break;
                        }
                        default: {
                            if (splitString[n].startsWith(Constants.Styling.MARKER_Q)) {
                                String str = splitString[n];
                                String intString = str.replaceAll(Constants.Styling.REGEX_NUMBERS, "");
                                int number;
                                if (intString.equals("")) {
                                    number = 1;
                                } else {
                                    number = Integer.parseInt(intString);
                                }
                                spannableStringBuilder.append(Constants.Styling.NEW_LINE);
                                for (int o = 0; o < number; o++) {
                                    spannableStringBuilder.append(Constants.Styling.TAB_SPACE);
                                }
                            } else if (splitString[n].startsWith(Constants.Styling.REGEX_ESCAPE)) {
                            } else {
                                spannableStringBuilder.append(splitString[n] + " ");
                            }
                            break;
                        }
                    }
                }
            }
        }
        return spannableStringBuilder.toString();
    }

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}