package com.bridgeconn.autographago.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.bridgeconn.autographago.models.ResponseModel;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadUtil {

    public interface JsonDownloadCallback {
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

    public void downloadJson(final String fileUrl, final JsonDownloadCallback callback) {
        final ApiInterface downloadService = retrofit.create(ApiInterface.class);

        new AsyncTask<Void, Void, Void>() {
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
}
