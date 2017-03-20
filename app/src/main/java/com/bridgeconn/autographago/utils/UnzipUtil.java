package com.bridgeconn.autographago.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipUtil {

    public static void unzipFile(final File zipfile1, Context context, final String languageName, final String languageCode,
                                 final String versionCode) {
        final String directory = zipfile1.getParent() + "/";

        File archive = zipfile1;
        String outputDir = directory;

        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory()) {
                    unzipEntry(zipfile, entry, outputDir);
                }
            }
            Log.i(Constants.LOG_TAG, "unzip success");
            archive.delete();

            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

            UtilFunctions.startParseService(context, outputDir, languageName, languageCode, versionCode);

        } catch (Exception e) {
            archive.delete();
            Log.v(Constants.LOG_TAG, "unzip :: " + "Deleting zip anyway, Error while extracting file " + archive);
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        Log.v(Constants.LOG_TAG, "unzip :: " + "Extracting: " + entry);
        BufferedInputStream inputStream = new
                BufferedInputStream(zipfile
                .getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(outputFile));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private static void createDir(File dir) {
        Log.v(Constants.LOG_TAG, "unzip :: " + "Creating dir " + dir.getName());
        if (!dir.mkdirs())
            throw new RuntimeException("Can not create dir " + dir);
    }
}