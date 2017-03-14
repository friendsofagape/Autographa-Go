package com.bridgeconn.autographago.ui.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

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

    private long lastDownload=-1L;
    private DownloadManager downloadManager;

    private String languageName, languageCode, versionCode, versionName;

    private static String downloadUrl;

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
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mFontSize = getFontSizeEnum(progress);
            SharedPrefs.putFontSize(mFontSize);
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
                SharedPrefs.putReadingMode(mReadingMode);
                recreate();
                break;
            }
            case R.id.iv_night_mode: {
                mReadingMode = Constants.ReadingMode.Night;
                mDayMode.setColorFilter(ContextCompat.getColor(this, R.color.black_40));
                mNightMode.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
                SharedPrefs.putReadingMode(mReadingMode);
                recreate();
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
        if (isFinishing()) {
            return;
        }

        if (languages.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.no_new_languages_available), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = null;
        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeLight);
                break;
            }
            case Night: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeDark);
                break;
            }
        }

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

        if (isFinishing()) {
            return;
        }

        if (versions.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.no_new_versions_available), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = null;
        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeLight);
                break;
            }
            case Night: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeDark);
                break;
            }
        }

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
        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    public void startDownload(String url) {
        Uri uri=Uri.parse(url);

        File root = Environment.getExternalStorageDirectory();

        String dirName = Constants.STORAGE_DIRECTORY + System.currentTimeMillis() + "/";
        if (root.canWrite()) {
            File dir = new File(root, dirName);
            dir.mkdirs();
        }

        lastDownload = downloadManager.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(getResources().getString(R.string.downloading_bible))
                .setDescription(getResources().getString(R.string.downloading))
                .setDestinationInExternalPublicDir(dirName, Constants.USFM_ZIP_FILE_NAME)
        );

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PrefKeys.LANGUAGE_NAME, languageName);
            jsonObject.put(Constants.PrefKeys.LANGUAGE_CODE, languageCode);
            jsonObject.put(Constants.PrefKeys.VERSION_NAME, versionName);
            jsonObject.put(Constants.PrefKeys.VERSION_CODE, versionCode);
            SharedPrefs.putString(Constants.PrefKeys.DOWNLOAD_ID_ + lastDownload, jsonObject.toString());
        } catch (JSONException je) {
        }
    }

    public void getMetaData(final String language, final String version) {
        final DownloadUtil downloadUtil = new DownloadUtil();
        downloadUtil.downloadJson(language + "/" + version + "/" + Constants.META_DATA_FILE_NAME,
                new DownloadUtil.FileDownloadCallback() {

                    @Override
                    public void onSuccess(final ResponseModel model) {
                        if (model.getMetaData() != null) {
                            languageName = model.getMetaData().getLanguageName();
                            languageCode = model.getMetaData().getLanguageCode();
                            versionCode = model.getMetaData().getVersionCode();
                            versionName = model.getMetaData().getVersionName();

                            TextView tv = new TextView(SettingsActivity.this);
                            tv.setText(model.getMetaData().getSource() + " :: " +
                                    model.getMetaData().getLanguageName() + " :: " +
                                    model.getMetaData().getLicense() + " :: "  +
                                    model.getMetaData().getYear() + " :: " +
                                    model.getMetaData().getVersionName()
                            );
                            Button button = new Button(SettingsActivity.this);
                            button.setText(getResources().getString(R.string.download));
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
                                }
                            });
//                            mInflateLayout.addView(tv);
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
        output.putExtra(Constants.Keys.TEXT_SIZE_CHANGED, true);
        output.putExtra(Constants.Keys.READING_MODE_CHANGE, true);
        setResult(RESULT_OK, output);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveToSharedPrefs();
    }

}