package com.bridgeconn.autographago.ui.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.ResponseModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.DownloadDialogAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.DownloadUtil;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UnzipUtil;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private TextView mTvDownload;
    private ImageView mDayMode, mNightMode;
    private LinearLayout mInflateLayout;
    private AppCompatSeekBar mSeekBarTextSize;
    private Constants.ReadingMode mReadingMode;
    private Constants.FontSize mFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        UtilFunctions.applyReadingMode();

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
        mDayMode = (ImageView) findViewById(R.id.iv_day_mode);
        mNightMode = (ImageView) findViewById(R.id.iv_night_mode);
        mInflateLayout = (LinearLayout) findViewById(R.id.inflate_layout);
        mSeekBarTextSize = (AppCompatSeekBar) findViewById(R.id.seekbar_text_size);

        mTvDownload.setOnClickListener(this);
        mDayMode.setOnClickListener(this);
        mNightMode.setOnClickListener(this);
        findViewById(R.id.tv_about_us).setOnClickListener(this);

        mFontSize = SharedPrefs.getFontSize();
        mReadingMode = SharedPrefs.getReadingMode();

        mSeekBarTextSize.setMax(4);
        mSeekBarTextSize.setOnSeekBarChangeListener(this);
        mSeekBarTextSize.setProgress(getFontSizeInt(SharedPrefs.getFontSize()));

        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                mDayMode.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                mNightMode.setColorFilter(ContextCompat.getColor(this, R.color.black_40));
                break;
            }
            case Night: {
                mDayMode.setColorFilter(ContextCompat.getColor(this, R.color.black_40));
                mNightMode.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                break;
            }
        }

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mFontSize = getFontSizeEnum(progress);
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
            case R.id.iv_day_mode: {
                mReadingMode = Constants.ReadingMode.Day;
                mDayMode.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                mNightMode.setColorFilter(ContextCompat.getColor(this, R.color.black_40));
                break;
            }
            case R.id.iv_night_mode: {
                mReadingMode = Constants.ReadingMode.Night;
                mDayMode.setColorFilter(ContextCompat.getColor(this, R.color.black_40));
                mNightMode.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                break;
            }
            case R.id.tv_about_us: {
                Intent intent = new Intent(this, AboutPageActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void showLanguageDialog(List<String> languages) {
        ArrayList<LanguageModel> languageModels = new AutographaRepository<LanguageModel>().query(new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (Iterator<String> iterator = languages.iterator(); iterator.hasNext(); ) {
            String lan = iterator.next();
            for (LanguageModel languageModel : languageModels) {
                if (languageModel.getLanguageName().equals(lan)) {
                    if (languageModel.getVersionModels().size() == 2) {
                        iterator.remove();
                    }
                }
            }
        }

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

        ArrayList<LanguageModel> languageModels = new AutographaRepository<LanguageModel>().query(new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (Iterator<String> iterator = versions.iterator(); iterator.hasNext(); ) {
            String ver = iterator.next();
            for (LanguageModel languageModel : languageModels) {
                if (languageModel.getLanguageName().equals(language)) {
                    for (VersionModel versionModel : languageModel.getVersionModels()) {
                        if (versionModel.getVersionCode().equals(ver)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
    }

    private long lastDownload=-1L;
    private long enqueue;
    private DownloadManager downloadManager;

    String filePath = "";

    public void startDownload(String url) {
        Uri uri=Uri.parse(url);

        File root = Environment.getExternalStorageDirectory();

        if (root.canWrite()) {
            String backupDBPath = Constants.STORAGE_DIRECTORY + Constants.USFM_ZIP_FILE_NAME;
            File dir = new File(root, Constants.STORAGE_DIRECTORY);
            if (dir.mkdir()) {
            }
            File f = new File (root, backupDBPath);
            filePath = f.getAbsolutePath();
        }

        lastDownload = downloadManager.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Downloading Bible")
                .setDescription("Downloading...")
                .setDestinationInExternalPublicDir(Constants.STORAGE_DIRECTORY, Constants.USFM_ZIP_FILE_NAME)
        );
    }

    private String language, versionCode, versionName;

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(final Context ctxt, Intent intent) {

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            DownloadManager manager = (DownloadManager) ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = manager.query(query);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        // So something here on success
                        startUnzipping(ctxt);
                    } else {
                        int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                        if (message == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                            Toast.makeText(ctxt, getString(R.string.insufficient_storage), Toast.LENGTH_SHORT).show();
                        }
                        // So something here on failed.
                    }
                }
            }
        }
    };

    private void startUnzipping(final Context context) {
        UnzipUtil.unzipFile(new File(filePath),
                context, language, versionCode, versionName,
                new UnzipUtil.FileUnzipCallback() {

                    @Override
                    public void onSuccess(final File zipFile, String directoryName) {

                        new AsyncTask<Void, Long, Void>() {

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                Log.i(Constants.DUMMY_TAG, "Parsing DONE");
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
                                            success = usfmParser.parseUSFMFile(context,
                                                    file.getAbsolutePath(),
                                                    false,
                                                    language,
                                                    versionCode,
                                                    versionName);

                                            if (success) {
                                                // delete that file
                                                file.delete();
                                            }
                                        }
                                    }
                                    Log.i(Constants.DUMMY_TAG, "DONE......");
                                    // TODO refresh lish oof books on main screen
                                }
                                zipDirectory.delete();

                                return null;
                            }
                        }.execute();
                    }

                    @Override
                    public void onFailure() {
                        Log.i(Constants.DUMMY_TAG, "Unzip Error");
                    }
                });
    }

    private static String downloadUrl;

    public void getMetaData(final String language, final String version) {
        // TODO make this a foreground service with notification
        final DownloadUtil downloadUtil = new DownloadUtil();
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

                                    downloadUrl = Constants.API_BASE_URL + language + "/" + version + "/" + Constants.USFM_ZIP_FILE_NAME;

                                    if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                Constants.RequestCodes.PERMISSION_STORAGE);
                                        return;
                                    }
                                    startDownload(downloadUrl);

//                                    Intent startIntent = new Intent(SettingsActivity.this, FileDownloadService.class);
//                                    startIntent.putExtra(Constants.Keys.LANGUAGE, language);
//                                    startIntent.putExtra(Constants.Keys.VERSION, version);
//                                    startIntent.putExtra(Constants.Keys.VERSION_NAME, model.getMetaData().getVersionName());
//                                    startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//                                    startService(startIntent);
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
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.RequestCodes.PERMISSION_STORAGE: {
                if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    startDownload(downloadUrl);

                } else {
                    String positiveButton;
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        positiveButton = getString(R.string.take_me_to_settings).toUpperCase();
                    } else {
                        positiveButton = getString(R.string.try_again).toUpperCase();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.DialogThemeLight);
                    builder.setMessage(getString(R.string.storage_permission_message));
                    builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + SettingsActivity.this.getPackageName()));
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivityForResult(myAppSettings, Constants.RequestCodes.APP_SETTINGS_STORAGE);
                            } else {
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                            }
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.APP_SETTINGS_STORAGE: {

                if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.RequestCodes.PERMISSION_STORAGE);
                    return;
                }

                startDownload(downloadUrl);

                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveToSharedPrefs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveToSharedPrefs() {
        Intent output = new Intent();
        if (SharedPrefs.getFontSize().equals(mFontSize)) {
            output.putExtra(Constants.Keys.TEXT_SIZE_CHANGED, false);
        } else {
            SharedPrefs.putFontSize(mFontSize);
            output.putExtra(Constants.Keys.TEXT_SIZE_CHANGED, true);
        }
        if (SharedPrefs.getReadingMode().equals(mReadingMode)) {
            output.putExtra(Constants.Keys.READING_MODE_CHANGE, false);
        } else {
            SharedPrefs.putReadingMode(mReadingMode);
            output.putExtra(Constants.Keys.READING_MODE_CHANGE, true);
        }
        setResult(RESULT_OK, output);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveToSharedPrefs();
    }

}