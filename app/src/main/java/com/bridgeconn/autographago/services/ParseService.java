package com.bridgeconn.autographago.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UnzipUtil;

import java.io.File;

public class ParseService extends IntentService {

    private NotificationManager notificationManager;

    public ParseService() {
        super(ParseService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(Constants.ACTION.START_PARSE)) {

            String zipFilePath = intent.getStringExtra(Constants.Keys.FILE_PATH);
            String languageCode = intent.getStringExtra(Constants.Keys.LANGUAGE_CODE);
            String versionCode = intent.getStringExtra(Constants.Keys.VERSION_CODE);
            String languageName = intent.getStringExtra(Constants.Keys.LANGUAGE_NAME);
            String versionName = intent.getStringExtra(Constants.Keys.VERSION_NAME);

            generateNotification(languageName, versionCode);

            startParsing(zipFilePath, languageName, languageCode, versionCode, versionName);
        }
        else if (intent.getAction().equals(Constants.ACTION.START_UNZIP)) {

            String zipFilePath = intent.getStringExtra(Constants.Keys.FILE_PATH);
            String languageCode = intent.getStringExtra(Constants.Keys.LANGUAGE_CODE);
            String versionCode = intent.getStringExtra(Constants.Keys.VERSION_CODE);
            String languageName = intent.getStringExtra(Constants.Keys.LANGUAGE_NAME);
            String versionName = intent.getStringExtra(Constants.Keys.VERSION_NAME);

            generateNotification(languageName, versionCode);

            startUnzipping(this, languageName, languageCode, versionCode, versionName, zipFilePath);
        }
    }

    private void generateNotification(String languageName, String versionCode) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_file_download_white_24dp);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(languageName + " " + versionCode)
                .setContentText(getResources().getString(R.string.downloading_bible))
                .setSmallIcon(R.drawable.ic_file_download_white)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(null)
                .setOngoing(true)
                .build();

        notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }

    private void startUnzipping(final Context context, final String languageName, final String languageCode,
                                final String versionCode, final String versionName, String filePath) {

        UnzipUtil.unzipFile(new File(filePath), context, languageName, languageCode, versionCode, versionName);
    }

    private void deleteDirectory(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File child : fileOrDir.listFiles()) {
                deleteDirectory(child);
            }
        }
        fileOrDir.delete();
    }

    private void startParsing(final String directory,
                              final String languageName, final String languageCode, final String versionCode, final String versionName) {
        final File zipDirectory = new File(directory);

        if (zipDirectory.exists()) {
            File[] files = zipDirectory.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory() || !file.getPath().endsWith(".usfm")) {
                    continue;
                } else {
                    if (file.getPath().endsWith(".usfm")) {
                        boolean success;
                        USFMParser usfmParser = new USFMParser();
                        usfmParser.parseUSFMFile(ParseService.this,
                                file.getAbsolutePath(),
                                false,
                                languageName,
                                languageCode,
                                versionCode, versionName);

                        // delete that file
                        file.delete();
                    }
                }
            }
            deleteDirectory(zipDirectory);
        }
        notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

        Log.i(Constants.LOG_TAG, "Parsing complete");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION.PARSE_COMPLETE);
        broadcastIntent.putExtra(Constants.Keys.LANGUAGE_NAME, languageName);
        broadcastIntent.putExtra(Constants.Keys.VERSION_CODE, versionCode);
        String languageCodeSharedPrefs = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCodeSharedPrefs = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        if (languageCodeSharedPrefs.equals(languageCode) && versionCodeSharedPrefs.equals(versionCode)) {
            broadcastIntent.putExtra(Constants.Keys.REFRESH_CONTAINER, true);
        } else {
            broadcastIntent.putExtra(Constants.Keys.REFRESH_CONTAINER, false);
        }
        sendBroadcast(broadcastIntent);
    }

}