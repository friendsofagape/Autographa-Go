package com.bridgeconn.autographago.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.services.DownloadService;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = manager.query(query);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        // DO THIS IN SERVICE

                        /// get valued from shared prefs
                        String languageName, languageCode, versionName, versionCode;
                        String value = SharedPrefs.getString(Constants.PrefKeys.DOWNLOAD_ID_ + id, null);
                        if (value != null) {
                            try {
                                JSONObject object = new JSONObject(value);
                                languageCode = object.getString(Constants.PrefKeys.LANGUAGE_CODE);
                                languageName = object.getString(Constants.PrefKeys.LANGUAGE_NAME);
                                versionName = object.getString(Constants.PrefKeys.VERSION_NAME);
                                versionCode = object.getString(Constants.PrefKeys.VERSION_CODE);

                                SharedPrefs.removeKey(Constants.PrefKeys.DOWNLOAD_ID_ + id);

                                Intent startIntent = new Intent(context, DownloadService.class);
                                startIntent.putExtra(Constants.Keys.FILE_PATH, file);
                                startIntent.putExtra(Constants.Keys.LANGUAGE_NAME, languageName);
                                startIntent.putExtra(Constants.Keys.LANGUAGE_CODE, languageCode);
                                startIntent.putExtra(Constants.Keys.VERSION_NAME, versionName);
                                startIntent.putExtra(Constants.Keys.VERSION_CODE, versionCode);

                                if (UtilFunctions.isServiceRunning(DownloadService.class.getName(), context)) {
                                 //    see how to queue
                                    Log.i(Constants.DUMMY_TAG, "service already running");
                                } else {
                                    Log.i(Constants.DUMMY_TAG, "starting serivce");
                                    startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                                    context.startService(startIntent);
                                }
                            } catch (JSONException je) {
                            }
                        }
                    } else {
                        int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                        if (message == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                            Toast.makeText(context, context.getString(R.string.insufficient_storage), Toast.LENGTH_SHORT).show();
                        }
                        // So something here on failed.
                    }
                }
            }
        }
    }
}