package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bridgeconn.autographago.models.ResponseModel;

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

    public interface FileUnzipCallback {
        void onSuccess(File zipFile, String directoryName);
        void onFailure();
    }

    private static Handler myHandler;

    public static void unzipFile(final File zipfile, final Context context, final String languageName,
                                 final String versionCode, final String versionName, final FileUnzipCallback callback) {
        final File zipFile1 = zipfile;
        final String directory = zipFile1.getParent() + "/";
        final File zipDirectory = new File(directory);
        myHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                switch (msg.what) {
                    case 1: {
//                        Toast.makeText(context, "Zip extracted successfully", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(zipfile, directory);
                        break;
                    }
                    case 2: {
                        callback.onFailure();
                        break;
                    }

                }
                super.handleMessage(msg);
            }

        };
        Thread workthread = new Thread(new UnZip(zipfile, directory));
        workthread.start();
    }

    public static class UnZip implements Runnable {

        File archive;
        String outputDir;

        public UnZip(File ziparchive, String directory) {
            archive = ziparchive;
            outputDir = directory;
        }

        public void log(String log) {
            Log.v("unzip", log);
        }

        @SuppressWarnings("unchecked")
        public void run() {
            Message msg;
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries();
                     e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    msg = new Message();
                    msg.what = 0;
                    msg.obj = "Extracting " + entry.getName();
                    myHandler.sendMessage(msg);
                    unzipEntry(zipfile, entry, outputDir);
                }
            } catch (Exception e) {
                log("Error while extracting file " + archive);
            }
            msg = new Message();
            msg.what = 1;
            myHandler.sendMessage(msg);
        }

        @SuppressWarnings("unchecked")
        public void unzipArchive(File archive, String outputDir) {
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries();
                     e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, outputDir);
                }
            } catch (Exception e) {
                log("Error while extracting file " + archive);
            }
        }

        private void unzipEntry(ZipFile zipfile, ZipEntry entry,
                                String outputDir) throws IOException {

            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            log("Extracting: " + entry);
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

        private void createDir(File dir) {
            log("Creating dir " + dir.getName());
            if (!dir.mkdirs())
                throw new RuntimeException("Can not create dir " + dir);
        }
    }

}
