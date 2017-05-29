package com.bridgeconn.autographago.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.services.ParseService;
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

                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        // DO THIS IN SERVICE
                        String value = SharedPrefs.getString(Constants.PrefKeys.DOWNLOAD_ID_ + id, null);
                        if (value != null) {
                            try {
                                JSONObject object = new JSONObject(value);
                                long time = object.getLong(Constants.PrefKeys.TIMESTAMP);

                                UtilFunctions.startUnzipService(time, context);
                            } catch (JSONException e) {
                            }
                        }
                    } else {
                        int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                        if (message == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                            Toast.makeText(context, context.getString(R.string.insufficient_storage), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }
}