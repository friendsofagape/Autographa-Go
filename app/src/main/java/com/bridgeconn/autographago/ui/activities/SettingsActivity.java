package com.bridgeconn.autographago.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.utils.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvDownload;
    private ImageView mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mTvDownload = (TextView) findViewById(R.id.download_bible);
        mProgress = (ImageView) findViewById(R.id.progress);

        mTvDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_bible: {

                downloadFile("Autographa_Repo/master/Bibles/English_ULB/01-GEN.usfm");
                downloadDirectory("https://github.com/Bridgeconn/Autographa_Repo.git");
                break;
            }
        }
    }

    static String TAG = "abcd";

    private void downloadDirectory(final String fileURL) {}

    private void downloadFile(final String fileURL) {

        String API_BASE_URL = "https://raw.githubusercontent.com/Bridgeconn/"; // "https://api.github.com/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();

        final ApiInterface downloadService = retrofit.create(ApiInterface.class);

        new AsyncTask<Void, Long, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgress.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(fileURL);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(TAG, "message = " + response.message());
                        if (response.isSuccessful()) {
                            Log.d(TAG, "server contacted and has file");

                            try {
                                File root = Environment.getExternalStorageDirectory();

                                Random generator = new Random();
                                int n = 10000;
                                n = generator.nextInt(n);
                                String fname = "File-"+ n; //+".usfm";

                                String sDBName = fname;
                                if (root.canWrite()) {
                                    String backupDBPath = "/appname-external-data-cache/" + sDBName; //"{database name}";
                                    File dir = new File(root, backupDBPath.replace(sDBName, ""));
                                    if (dir.mkdir()) {
                                    }

                                    final File futureStudioIconFile = new File (root, backupDBPath);
                                    if (futureStudioIconFile.exists ()) futureStudioIconFile.delete ();

                                    InputStream inputStream = null;
                                    OutputStream outputStream = null;

                                    try {
                                        byte[] fileReader = new byte[4096];

                                        long fileSize = response.body().contentLength();
                                        long fileSizeDownloaded = 0;

                                        inputStream = response.body().byteStream();
                                        outputStream = new FileOutputStream(futureStudioIconFile);

                                        while (true) {
                                            int read = inputStream.read(fileReader);

                                            if (read == -1) {
                                                break;
                                            }

                                            outputStream.write(fileReader, 0, read);

                                            fileSizeDownloaded += read;

                                            Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SettingsActivity.this, "file downloaded successfully at " + futureStudioIconFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        outputStream.flush();

                                        return ;
                                    } catch (IOException e) {
                                        Log.e(TAG, e.toString());
                                        return ;
                                    } finally {
                                        if (inputStream != null) {
                                            inputStream.close();
                                        }

                                        if (outputStream != null) {
                                            outputStream.close();
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "sd cannot write");
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.toString());
                                return;
                            }
                        } else {
                            Log.d(TAG, "server contact failed");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "error");
                    }
                });
                return null;
            }
        }.execute();
    }

}