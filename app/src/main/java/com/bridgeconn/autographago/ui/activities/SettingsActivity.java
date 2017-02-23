package com.bridgeconn.autographago.ui.activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ResponseModel;
import com.bridgeconn.autographago.ui.adapters.DownloadDialogAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.DownloadUtil;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UnzipUtil;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.io.File;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private TextView mTvDownload;
    private ProgressBar mProgress;
    private LinearLayout mInflateLayout;
    private AppCompatSeekBar mSeekBarTextSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
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
        mSeekBarTextSize = (AppCompatSeekBar) findViewById(R.id.seekbar_text_size);

        mTvDownload.setOnClickListener(this);

        mSeekBarTextSize.setMax(4);
        mSeekBarTextSize.setOnSeekBarChangeListener(this);
        mSeekBarTextSize.setProgress(getFontSizeInt(SharedPrefs.getFontSize()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            SharedPrefs.setFontSize(getFontSizeEnum(progress));
        }
    }

    private int getFontSizeInt(Constants.FontSize fontSize) {
        switch (fontSize) {
            case XSmall: {
                return 0;
            }
            case Small: {
                return 1;
            }
            case Medium: {
                return 2;
            }
            case Large: {
                return 3;
            }
            case XLarge: {
                return 4;
            }
        }
        return 3;
    }

    private Constants.FontSize getFontSizeEnum(int progress) {
        switch (progress) {
            case 0: {
                return Constants.FontSize.XSmall;
            }
            case 1: {
                return Constants.FontSize.Small;
            }
            case 2: {
                return Constants.FontSize.Medium;
            }
            case 3: {
                return Constants.FontSize.Large;
            }
            case 4: {
                return Constants.FontSize.XLarge;
            }
        }
        return Constants.FontSize.Medium;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
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

        builder.setTitle(getString(R.string.select_version));

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
                showNetworkToast();
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    private void showNetworkToast() {
        Toast.makeText(SettingsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
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
                showNetworkToast();
                Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
            }
        });
    }

    public void getMetaData(final String language, final String version) {
        // TODO make this a foreground service with notification
        DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + version + "/" + Constants.META_DATA_FILE_NAME,
                new DownloadUtil.FileDownloadCallback() {

                    @Override
                    public void onSuccess(final ResponseModel model) {
                        if (model.getMetaData() != null) {
                            TextView tv = new TextView(SettingsActivity.this);
                            tv.setText(model.getMetaData().getSource() + " :: " +
                                    model.getMetaData().getLanguageName() + " :: " +
                                    model.getMetaData().getLicense() + " :: "  +
                                    model.getMetaData().getYear() + " :: " +
                                    model.getMetaData().getVersionName()
                            );
                            Button button = new Button(SettingsActivity.this);
                            button.setText("DOWNLOAD");
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO see why progress bar is hanging
                                    mProgress.setVisibility(View.VISIBLE);
                                    DownloadUtil downloadUtil = new DownloadUtil();
                                    downloadUtil.downloadFile(
                                            language + "/" + version + "/" + Constants.USFM_ZIP_FILE_NAME,
                                            SettingsActivity.this,
                                            language,
                                            version,
                                            model.getMetaData().getVersionName(),
                                            new UnzipUtil.FileUnzipCallback() {

                                                @Override
                                                public void onSuccess(final File zipFile, String directoryName) {

                                                    new AsyncTask<Void, Long, Void>() {

                                                        @Override
                                                        protected void onPostExecute(Void aVoid) {
                                                            super.onPostExecute(aVoid);

                                                            mProgress.setVisibility(View.GONE);
                                                            Toast.makeText(SettingsActivity.this, "Parsing DONE", Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        protected Void doInBackground(Void... voids) {
                                                            final File zipFile1 = zipFile;
                                                            String directory = zipFile1.getParent() + "/";
                                                            final File zipDirectory = new File(directory);

                                                            zipFile1.delete();
                                                            if (zipDirectory.exists()) {
                                                                File[] files = zipDirectory.listFiles();
                                                                for (int i = 0; i < files.length; ++i) {
                                                                    File file = files[i];
                                                                    if (file.isDirectory() || !file.getPath().endsWith(".usfm")) {
                                                                        continue;
                                                                    } else {
                                                                        boolean success;
                                                                        USFMParser usfmParser = new USFMParser();
                                                                        success = usfmParser.parseUSFMFile(SettingsActivity.this,
                                                                                file.getAbsolutePath(),
                                                                                false,
                                                                                language,
                                                                                version,
                                                                                model.getMetaData().getVersionName());

                                                                        if (success) {
                                                                            // delete that file
                                                                            file.delete();
                                                                        }
                                                                    }
                                                                }
                                                                Log.i(Constants.DUMMY_TAG, "DONE......");
                                                                // TODO refresh lish oof books on main screen
                                                            }

                                                            return null;
                                                        }
                                                    }.execute();
                                                }

                                                @Override
                                                public void onFailure() {
                                                    Toast.makeText(SettingsActivity.this, "Unzip Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                            mInflateLayout.addView(tv);
                            mInflateLayout.addView(button);
                        }
                    }

                    @Override
                    public void onFailure() {
                        showNetworkToast();
                        Log.i(Constants.DUMMY_TAG, "NO DATA FOUND");
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}