package com.bridgeconn.autographago.utils;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

import static com.bridgeconn.autographago.utils.Constants.EXPORT_REALM_FILE_NAME;
import static com.bridgeconn.autographago.utils.Constants.EXPORT_REALM_PATH;
import static com.bridgeconn.autographago.utils.Constants.IMPORT_REALM_FILE_NAME;

public class BackupRestoreUtil {

    public static void backup() {
        Realm realm = Realm.getDefaultInstance();
        try {
            // create a backup file
            File exportRealmFile;
            exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile);
            Log.d(Constants.LOG_TAG, "Data backup done");
        } catch (Exception e) { // IOException
            e.printStackTrace();
        }
        realm.close();
    }

    public static void restore(Activity activity) {
        String restoreFilePath = EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;

        copyBundledRealmFile(activity, restoreFilePath, IMPORT_REALM_FILE_NAME);
        Log.d(Constants.LOG_TAG, "Data restore is done");
    }

    private static String copyBundledRealmFile(Activity activity, String oldFilePath, String outFileName) {
        try {
            File file = new File(activity.getApplicationContext().getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
