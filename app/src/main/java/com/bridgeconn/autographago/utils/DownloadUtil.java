package com.bridgeconn.autographago.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bridgeconn.autographago.models.ResponseModel;

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

public class DownloadUtil {

    public interface FileDownloadCallback {
        void onSuccess(ResponseModel model);
        void onFailure();
    }

    private Retrofit retrofit;

    public DownloadUtil() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Constants.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.client(httpClient.build()).build();
    }

    public void downloadJson(final String fileUrl, final FileDownloadCallback callback) {
        final ApiInterface downloadService = retrofit.create(ApiInterface.class);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseModel> call = downloadService.downloadJsonResponse(fileUrl);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        Log.i(Constants.DUMMY_TAG, "message = " + response.message());
                        if (response.isSuccessful()) {
                            Log.d(Constants.DUMMY_TAG, "server contacted and has file");

                            if (response.body() != null) {
                                Log.i(Constants.DUMMY_TAG, "found languages");
                                callback.onSuccess(response.body());
                            } else {
                                callback.onFailure();
                            }
                        } else {
                            Log.d(Constants.DUMMY_TAG, "server contact failed");

                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.e(Constants.DUMMY_TAG, "error");

                        callback.onFailure();
                    }
                });
                return null;
            }
        }.execute();
    }

    public void downloadFile(final String fileUrl, final ImageView mProgress, final Context context) {

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
                Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(fileUrl);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(Constants.DUMMY_TAG, "message = " + response.message());
                        if (response.isSuccessful()) {
                            Log.d(Constants.DUMMY_TAG, "server contacted and has file");

                            try {
                                File root = Environment.getExternalStorageDirectory();

                                Random generator = new Random();
                                int n = 10000;
                                n = generator.nextInt(n);
                                String fname = Constants.USFM_ZIP_FILE_NAME;

                                String sDBName = fname;
                                if (root.canWrite()) {
                                    String backupDBPath = Constants.STORAGE_DIRECTORY + sDBName;
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

                                            Log.d(Constants.DUMMY_TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                                        }
                                        Log.i(Constants.DUMMY_TAG, "file downloaded successfully at " + futureStudioIconFile.getAbsolutePath());

                                        outputStream.flush();

                                        UnzipUtil.unzipFile(new File(futureStudioIconFile.getAbsolutePath()), context);
                                        return ;
                                    } catch (IOException e) {
                                        Log.e(Constants.DUMMY_TAG, e.toString());
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
                                    // TODO ask for permission to write to storage
                                    Log.e(Constants.DUMMY_TAG, "sd cannot write");
                                }
                            } catch (IOException e) {
                                Log.e(Constants.DUMMY_TAG, e.toString());
                                return;
                            }
                        } else {
                            Log.d(Constants.DUMMY_TAG, "server contact failed");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(Constants.DUMMY_TAG, "error");
                    }
                });
                return null;
            }
        }.execute();
    }

}
