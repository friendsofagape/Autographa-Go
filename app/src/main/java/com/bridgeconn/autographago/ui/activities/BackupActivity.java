package com.bridgeconn.autographago.ui.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.AutographaGoBackup;
import com.bridgeconn.autographago.ui.adapters.BackupAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;
import com.bridgeconn.autographago.utils.backup.Backup;
import com.bridgeconn.autographago.utils.backup.GoogleDriveBackup;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

public class BackupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "drive_backup";

    private Backup backup;
    private GoogleApiClient mGoogleApiClient;
    private TextView folderTextView;
    private Realm realm;
    private RecyclerView backupRecyclerView;
    private BackupAdapter mAdapter;

    // TODO set up weekly alarm for backup at 4.00 am

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_drive);
        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        realm = Realm.getDefaultInstance();

        backup = new GoogleDriveBackup();
        backup.init(this);
        backup.start();
        mGoogleApiClient = backup.getClient();

        Button backupButton = (Button) findViewById(R.id.activity_backup_drive_button_backup);
        folderTextView = (TextView) findViewById(R.id.activity_backup_drive_textview_folder);
        backupRecyclerView = (RecyclerView) findViewById(R.id.activity_backup_drive_recyclerview_restore);

        backupRecyclerView.setHasFixedSize(true);
        backupRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backupButton.setOnClickListener(this);
        findViewById(R.id.recent_backups).setOnClickListener(this);

        getBackupsFromDrive();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_backup_drive_button_backup: {
                uploadToDrive();
                break;
            }
            case R.id.recent_backups: {
                getBackupsFromDrive();
                break;
            }
        }
    }

    private void reinitializeGoogleApiClient(boolean getBackup, boolean download, boolean upload, DriveFile file) {
        Log.i("mytag", "call reiniitailize");
        if (mGoogleApiClient != null) {
            Log.i("mytag", "reinitializing, isconnected=" + mGoogleApiClient.isConnected());
            mGoogleApiClient.reconnect();
        } else {
            backup.init(this);
            backup.start();
            mGoogleApiClient = backup.getClient();
        }
    }

    private void getBackupsFromDrive() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            final Activity activity = this;
            SortOrder sortOrder = new SortOrder.Builder()
                    .addSortDescending(SortableField.MODIFIED_DATE).build();
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, Constants.EXPORT_REALM_FILE_NAME))
                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
                    .setSortOrder(sortOrder)
                    .build();
            Drive.DriveApi.getAppFolder(mGoogleApiClient)
                    .queryChildren(mGoogleApiClient, query)
                    .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                        private ArrayList<AutographaGoBackup> backupsArray = new ArrayList<>();

                        @Override
                        public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                            MetadataBuffer buffer = result.getMetadataBuffer();
                            int size = buffer.getCount();
                            for (int i = 0; i < size; i++) {
                                Metadata metadata = buffer.get(i);
                                DriveId driveId = metadata.getDriveId();
                                Date modifiedDate = metadata.getModifiedDate();
                                long backupSize = metadata.getFileSize();
                                backupsArray.add(new AutographaGoBackup(driveId, modifiedDate, backupSize));
                            }
                            mAdapter = new BackupAdapter(BackupActivity.this, backupsArray);
                            backupRecyclerView.setAdapter(mAdapter);
                        }
                    });
        } else {
            showConnectionError();
            reinitializeGoogleApiClient(true, false, false, null);
        }
    }

    private void showConnectionError() {
        Toast.makeText(getApplicationContext(), getString(R.string.client_not_connected_to_server), Toast.LENGTH_SHORT).show();
    }

    public void downloadFromDrive(DriveFile file) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                showErrorDialog();
                                return;
                            }

                            // DriveContents object contains pointers
                            // to the actual byte stream
                            DriveContents contents = result.getDriveContents();
                            InputStream input = contents.getInputStream();

                            try {
                                File file = new File(realm.getPath());
                                OutputStream output = new FileOutputStream(file);
                                try {
                                    try {
                                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                        int read;

                                        while ((read = input.read(buffer)) != -1) {
                                            output.write(buffer, 0, read);
                                        }
                                        output.flush();
                                    } finally {
                                        safeCloseClosable(input);
                                    }
                                } catch (Exception e) {
                                    reportToFirebase(e, "Error downloading backup from drive");
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                reportToFirebase(e, "Error downloading backup from drive, file not found");
                                e.printStackTrace();
                            } finally {
                                safeCloseClosable(input);
                            }

                            Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_message_restart, Toast.LENGTH_LONG).show();

                            // Reboot app
                            Intent mStartActivity = new Intent(getApplicationContext(), HomeActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
        } else {
            showConnectionError();
            reinitializeGoogleApiClient(false, true, false, file);
        }
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            reportToFirebase(e, "Error downloading backup from drive, IO Exception");
            e.printStackTrace();
        }
    }

    private void uploadToDrive() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                showErrorDialog();
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();

                                    FileInputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(new File(realm.getPath()));
                                    } catch (FileNotFoundException e) {
                                        reportToFirebase(e, "Error uploading backup from drive, file not found");
                                        showErrorDialog();
                                        e.printStackTrace();
                                    }

                                    byte[] buf = new byte[1024];
                                    int bytesRead;
                                    try {
                                        if (inputStream != null) {
                                            while ((bytesRead = inputStream.read(buf)) > 0) {
                                                outputStream.write(buf, 0, bytesRead);
                                            }
                                        }
                                    } catch (IOException e) {

                                        showErrorDialog();
                                        e.printStackTrace();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(Constants.EXPORT_REALM_FILE_NAME)
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in app folder
                                    Drive.DriveApi
                                            .getAppFolder(mGoogleApiClient)
                                            .createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.d(TAG, "Error while trying to create the file");
                                                        showErrorDialog();
                                                        finish();
                                                        return;
                                                    }
                                                    showSuccessDialog();
                                                    finish();
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        } else {
            showConnectionError();
            reinitializeGoogleApiClient(false, false, true, null);
        }
    }

    private void showSuccessDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_success, Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.activity_backup_drive_failed, Toast.LENGTH_SHORT).show();
    }

    private void reportToFirebase(Exception e, String message) {
        FirebaseCrash.log(message);
        FirebaseCrash.report(e);
    }

    public void disconnectClient() {
        backup.stop();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
