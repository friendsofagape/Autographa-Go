package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ResponseModel;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.DownloadUtil;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvDownload;
    private ImageView mProgress;
    private LinearLayout mInflateLayout;

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
        mInflateLayout = (LinearLayout) findViewById(R.id.inflate_layout);

        mTvDownload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_bible: {

                getAvailableLanguages();

                break;
            }
        }
    }

    private void showDialog() {

    }

    private void getAvailableLanguages() {

        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(Constants.META_DATA_FILE_NAME, new DownloadUtil.FileDownloadCallback() {

            @Override
            public void onSuccess(ResponseModel model) {
                if (model.getLanguagesAvailable() != null) {
                    for (final String language : model.getLanguagesAvailable()) {
                        final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.button_verse, mInflateLayout, false);
                        TextView button = (TextView) view.findViewById(R.id.button);
                        button.setText(language);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getAvailableListOfVersions(language);
                            }
                        });
                        mInflateLayout.addView(view);
                    }
                }
            }

            @Override
            public void onFailure() {
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    private void getAvailableListOfVersions(final String language) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + Constants.META_DATA_FILE_NAME, new DownloadUtil.FileDownloadCallback() {

            @Override
            public void onSuccess(ResponseModel model) {
                if (model.getListOfVersionsAvailable() != null) {
                    for (final String version : model.getListOfVersionsAvailable()) {
                        final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.button_verse, mInflateLayout, false);
                        TextView button = (TextView) view.findViewById(R.id.button);
                        button.setText(version);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getMetaData(language, version);
                            }
                        });
                        mInflateLayout.addView(view);
                    }
                }
            }

            @Override
            public void onFailure() {
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    private void getMetaData(final String language, final String version) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + version + "/" + Constants.META_DATA_FILE_NAME,
                new DownloadUtil.FileDownloadCallback() {

            @Override
            public void onSuccess(ResponseModel model) {
                if (model.getMetaData() != null) {
                    TextView tv = new TextView(SettingsActivity.this);
                    tv.setText(model.getMetaData().getSource() + " :: " +
                            model.getMetaData().getLanguage() + " :: " +
                            model.getMetaData().getLicense() + " :: "  +
                            model.getMetaData().getYear() + " :: " +
                            model.getMetaData().getVersion()
                    );
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadUtil downloadUtil = new DownloadUtil();
                            downloadUtil.downloadFile(language + "/" + version + "/" + Constants.USFM_ZIP_FILE_NAME, mProgress);
                        }
                    });
                    mInflateLayout.addView(tv);
                }
            }

            @Override
            public void onFailure() {
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

}