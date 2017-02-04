package com.bridgeconn.autographago.ui.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ResponseModel;
import com.bridgeconn.autographago.ui.adapters.DownloadDialogAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.DownloadUtil;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvDownload;
    private ProgressBar mProgress;
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
        mProgress = (ProgressBar) findViewById(R.id.progress);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.discard_note));
        builder.setMessage(getString(R.string.discard_note_message));

        String positiveText = getString(R.string.discard);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLanguageDialog(List<String> languages) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogThemeLight);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_languages, (ViewGroup) findViewById(android.R.id.content), false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        builder.setView(view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int maxHeight = UtilFunctions.dpToPx(SettingsActivity.this, 300);

            @Override
            public void onGlobalLayout() {
                if (view.getHeight() > maxHeight)
                    view.getLayoutParams().height = maxHeight;
            }
        });

        builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(getString(R.string.select_language));

        AlertDialog dialog = builder.create();
        dialog.show();

        DownloadDialogAdapter dialogAdapter = new DownloadDialogAdapter(this, dialog, languages, null, null);
        recyclerView.setAdapter(dialogAdapter);
    }

    private void showVersionDialog(List<String> versions, String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogThemeLight);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_languages, (ViewGroup) findViewById(android.R.id.content), false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        builder.setView(view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int maxHeight = UtilFunctions.dpToPx(SettingsActivity.this, 300);

            @Override
            public void onGlobalLayout() {
                if (view.getHeight() > maxHeight)
                    view.getLayoutParams().height = maxHeight;
            }
        });

        builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(getString(R.string.select_language));

        AlertDialog dialog = builder.create();
        dialog.show();

        DownloadDialogAdapter dialogAdapter = new DownloadDialogAdapter(this, dialog, null, versions, language);
        recyclerView.setAdapter(dialogAdapter);
    }

    private void getAvailableLanguages() {

        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(Constants.META_DATA_FILE_NAME, new DownloadUtil.FileDownloadCallback() {

            @Override
            public void onSuccess(ResponseModel model) {
                if (model.getLanguagesAvailable() != null) {
                    showLanguageDialog(model.getLanguagesAvailable());
                }
            }

            @Override
            public void onFailure() {
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    public void getAvailableListOfVersions(final String language) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + Constants.META_DATA_FILE_NAME, new DownloadUtil.FileDownloadCallback() {

            @Override
            public void onSuccess(ResponseModel model) {
                if (model.getListOfVersionsAvailable() != null) {
                    showVersionDialog(model.getListOfVersionsAvailable(), language);
                }
            }

            @Override
            public void onFailure() {
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    public void getMetaData(final String language, final String version) {
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + version + "/" + Constants.META_DATA_FILE_NAME,
                new DownloadUtil.FileDownloadCallback() {

                    @Override
                    public void onSuccess(final ResponseModel model) {
                        if (model.getMetaData() != null) {
                            TextView tv = new TextView(SettingsActivity.this);
                            tv.setText(model.getMetaData().getSource() + " :: " +
                                    model.getMetaData().getLanguage() + " :: " +
                                    model.getMetaData().getLicense() + " :: "  +
                                    model.getMetaData().getYear() + " :: " +
                                    model.getMetaData().getVersion()
                            );
                            Button button = new Button(SettingsActivity.this);
                            button.setText("DOWNLOAD");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO see why progress bar not showing
                                    mProgress.setVisibility(View.VISIBLE);
                                    DownloadUtil downloadUtil = new DownloadUtil();
                                    downloadUtil.downloadFile(
                                            language + "/" + version + "/" + Constants.USFM_ZIP_FILE_NAME,
                                            mProgress,
                                            SettingsActivity.this,
                                            language,
                                            version,
                                            model.getMetaData().getVersion());
                                }
                            });
                            mInflateLayout.addView(tv);
                            mInflateLayout.addView(button);
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
                    }
                });
    }

}