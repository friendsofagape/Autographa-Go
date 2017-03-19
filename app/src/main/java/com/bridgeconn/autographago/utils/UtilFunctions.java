package com.bridgeconn.autographago.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bridgeconn.autographago.services.ParseService;

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

    public static void queueArchivesForUnzipping(Context context) {
        traverseForExist(new File (Environment.getExternalStorageDirectory() + Constants.STORAGE_DIRECTORY), context);
    }

    private static void traverseForExist (File dir, Context context) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    traverseForExist(file, context);
                } else {
                    if (file.getAbsolutePath().endsWith(".zip")){
                        // here unzip the archive
                        String filePath = file.getAbsolutePath();
                        filePath = filePath.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
                        filePath = filePath.replaceAll(Constants.STORAGE_DIRECTORY, "");
                        filePath = filePath.replaceAll(Constants.USFM_ZIP_FILE_NAME, "");
                        filePath = filePath.replaceAll("/", "");
                        try {
                            long timeStamp = Long.parseLong(filePath);
                            startUnzipService(timeStamp, context);
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        }
    }

    public static void queueDirectoriesForParsing(Context context) {
        traverseForTimeStamp(context, new File (Environment.getExternalStorageDirectory() + Constants.STORAGE_DIRECTORY));
    }

    private static void traverseForTimeStamp(Context context, File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    // here start parsing service
                    String filePath = file.getAbsolutePath();
                    filePath = filePath.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
                    filePath = filePath.replaceAll(Constants.STORAGE_DIRECTORY, "");
                    filePath = filePath.replaceAll("/", "");
                    try {
                        long timeStamp = Long.parseLong(filePath);
                        String value = SharedPrefs.getString(Constants.PrefKeys.TIMESTAMP_ + timeStamp, null);
                        if (value != null) {
                            JSONObject jsonObject = new JSONObject(value);
                            String languageCode = jsonObject.getString(Constants.PrefKeys.LANGUAGE_CODE);
                            String languageName = jsonObject.getString(Constants.PrefKeys.LANGUAGE_NAME);
                            String versionCode = jsonObject.getString(Constants.PrefKeys.VERSION_CODE);
                            startParseService(context, file.getAbsolutePath(), languageName, languageCode, versionCode);
                        }
                    } catch (NumberFormatException | JSONException e) {
                    }
                }
            }
        }
    }

    public static void startUnzipService(long time, Context context) {
        /// get valued from shared prefs
        try {
            String value = SharedPrefs.getString(Constants.PrefKeys.TIMESTAMP_ + time, null);
            if (value != null) {
                JSONObject jsonObject = new JSONObject(value);
                String languageCode = jsonObject.getString(Constants.PrefKeys.LANGUAGE_CODE);
                String languageName = jsonObject.getString(Constants.PrefKeys.LANGUAGE_NAME);
                String versionName = jsonObject.getString(Constants.PrefKeys.VERSION_NAME);
                String versionCode = jsonObject.getString(Constants.PrefKeys.VERSION_CODE);

                String file = Environment.getExternalStorageDirectory() + Constants.STORAGE_DIRECTORY + time + "/" + Constants.USFM_ZIP_FILE_NAME;
//              SharedPrefs.removeKey(Constants.PrefKeys.DOWNLOAD_ID_ + id);

                Intent startIntent = new Intent(context, ParseService.class);
                startIntent.putExtra(Constants.Keys.FILE_PATH, file);
                startIntent.putExtra(Constants.Keys.LANGUAGE_NAME, languageName);
                startIntent.putExtra(Constants.Keys.LANGUAGE_CODE, languageCode);
                startIntent.putExtra(Constants.Keys.VERSION_NAME, versionName);
                startIntent.putExtra(Constants.Keys.VERSION_CODE, versionCode);
                startIntent.setAction(Constants.ACTION.START_UNZIP);
                context.startService(startIntent);
            }
        } catch (JSONException je) {
        }
    }

    public static void startParseService(Context context, String directoryPath, String languageName, String languageCode, String versionCode) {
        Intent startParse = new Intent(context, ParseService.class);
        startParse.setAction(Constants.ACTION.START_PARSE);
        startParse.putExtra(Constants.Keys.FILE_PATH, directoryPath);
        startParse.putExtra(Constants.Keys.LANGUAGE_NAME, languageName);
        startParse.putExtra(Constants.Keys.LANGUAGE_CODE, languageCode);
        startParse.putExtra(Constants.Keys.VERSION_CODE, versionCode);
        context.startService(startParse);
    }
}