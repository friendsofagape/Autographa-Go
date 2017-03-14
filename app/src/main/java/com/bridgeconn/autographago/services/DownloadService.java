package com.bridgeconn.autographago.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.activities.HomeActivity;
import com.bridgeconn.autographago.ui.activities.SearchActivity;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UnzipUtil;

import java.io.File;
import java.util.ArrayList;

public class DownloadService extends Service {

    private static final String LOG_TAG = "ForegroundService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(Constants.DUMMY_TAG, "Received Start Foreground Intent ");

            String zipFilePath = intent.getStringExtra(Constants.Keys.FILE_PATH);
            String languageCode = intent.getStringExtra(Constants.Keys.LANGUAGE_CODE);
            String versionCode = intent.getStringExtra(Constants.Keys.VERSION_CODE);
            String languageName = intent.getStringExtra(Constants.Keys.LANGUAGE_NAME);
            String versionName = intent.getStringExtra(Constants.Keys.VERSION_NAME);

            Intent notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_file_download_white_24dp);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(languageName + " " + versionCode)
                    .setContentText(getResources().getString(R.string.parsing))
                    .setSmallIcon(R.drawable.ic_file_download_white)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);

            startUnzipping(this, languageName, languageCode, versionCode, versionName, zipFilePath);

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(Constants.DUMMY_TAG, "Received Stop Foreground Intent");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION.PARSING_COMPLETE_ACTION);
            sendBroadcast(broadcastIntent);

            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equals(Constants.ACTION.PARSE_ENG_UDB_ACTION)) {

            String languageName = intent.getStringExtra(Constants.Keys.LANGUAGE_NAME);
            String versionCode = intent.getStringExtra(Constants.Keys.VERSION_CODE);

            Intent notificationIntent = new Intent(this, HomeActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_file_download_white_24dp);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(languageName + " " + versionCode)
                    .setContentText(getResources().getString(R.string.parsing))
                    .setSmallIcon(R.drawable.ic_file_download_white)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);

            parseEngUdb(this);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.DUMMY_TAG, "In onDestroy, service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private void parseEngUdb(Context context) {
        new Parse().execute();
    }

    private class Parse extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i<Constants.UsfmFileNames.length; i++) {
                USFMParser usfmParser = new USFMParser();
                usfmParser.parseUSFMFile(DownloadService.this, "english_udb/"+Constants.UsfmFileNames[i], true, "English", "ENG", Constants.VersionCodes.UDB);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent startIntent = new Intent(DownloadService.this, DownloadService.class);
            startIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(startIntent);
        }
    }

        private void startUnzipping(final Context context, final String languageName, final String languageCode,
                                final String versionCode, final String versionName, String filePath) {

        UnzipUtil.unzipFile(new File(filePath),
                context,
                new UnzipUtil.FileUnzipCallback() {

                    @Override
                    public void onSuccess(final File zipFile, String directoryName) {

                        new AsyncTask<Void, Long, Void>() {

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                Log.i(Constants.DUMMY_TAG, "Parsing DONE");
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                final File zipFile1 = zipFile;
                                String directory = zipFile1.getParent() + "/";
                                final File zipDirectory = new File(directory);

                                zipFile1.delete();
                                if (zipDirectory.exists()) {
                                    File[] files = zipDirectory.listFiles();
                                    for (int i = 0; i < files.length; ++i) {
                                        File file = files[i];
                                        if (file.isDirectory() || !file.getPath().endsWith(".usfm")) {
                                            continue;
                                        } else {
                                            Log.i(Constants.DUMMY_TAG, "now parsing = " + file.getAbsolutePath());
                                            boolean success;
                                            USFMParser usfmParser = new USFMParser();
                                            success = usfmParser.parseUSFMFile(context,
                                                    file.getAbsolutePath(),
                                                    false,
                                                    languageName,
                                                    languageCode,
                                                    versionCode);

                                            if (success) {
                                                // delete that file
                                                file.delete();
                                            }
                                        }
                                    }
                                    Log.i(Constants.DUMMY_TAG, "DONE......");
                                }
                                deleteDirectory(zipDirectory);

                                Intent startIntent = new Intent(context, DownloadService.class);
                                startIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                                context.startService(startIntent);

                                return null;
                            }
                        }.execute();
                    }

                    @Override
                    public void onFailure() {
                        Log.i(Constants.DUMMY_TAG, "Unzip Error");
                    }
                });
    }

    private void deleteDirectory(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File child : fileOrDir.listFiles()) {
                deleteDirectory(child);
            }
        }
        fileOrDir.delete();
    }

}