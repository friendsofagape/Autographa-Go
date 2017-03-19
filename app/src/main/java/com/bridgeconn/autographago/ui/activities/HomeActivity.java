package com.bridgeconn.autographago.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.SpinnerModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.BookAdapter;
import com.bridgeconn.autographago.ui.adapters.SpinnerAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView mToolbarTitle;
    private ImageView mContinueRead;
    private ImageView mNotesView;
    private ImageView mBookmarkView;
    private ImageView mHighlightView;
    private ImageView mSearchView;
    private ImageView mHistoryView;
    private ImageView mSettingsView;
    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private ArrayList<BookIdModel> mBookModelArrayList = new ArrayList<>();
    private AppCompatSpinner mSpinner;
    private List<SpinnerModel> categoriesList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private String languageCode, languageName, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UtilFunctions.applyReadingMode();

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        languageName = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_NAME, "English");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mContinueRead = (ImageView) findViewById(R.id.iv_continue_reading);
        mNotesView = (ImageView) findViewById(R.id.iv_notes);
        mBookmarkView = (ImageView) findViewById(R.id.iv_bookmark);
        mHighlightView = (ImageView) findViewById(R.id.iv_highlight);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mHistoryView = (ImageView) findViewById(R.id.iv_history);
        mSettingsView = (ImageView) findViewById(R.id.iv_settings);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_books);
        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookAdapter(this, mBookModelArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mSpinner.setOnItemSelectedListener(this);
        mToolbarTitle.setOnClickListener(this);
        mContinueRead.setOnClickListener(this);
        mNotesView.setOnClickListener(this);
        mBookmarkView.setOnClickListener(this);
        mHighlightView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mHistoryView.setOnClickListener(this);
        mSettingsView.setOnClickListener(this);

        spinnerAdapter = new SpinnerAdapter(this, categoriesList);
        mSpinner.setAdapter(spinnerAdapter);

        getCategories();

        SpinnerModel compareModel = new SpinnerModel();
        compareModel.setLanguageName(languageName);
        compareModel.setVersionCode(versionCode);
        compareModel.setLanguageCode(languageCode);

        int spinnerPosition = findIndex(compareModel);
        mSpinner.setSelection(spinnerPosition);

        getAllBooks();

        registerReceiver(onParsingComplete, new IntentFilter(Constants.ACTION.PARSE_COMPLETE));

        UtilFunctions.queueArchivesForUnzipping(this);
        UtilFunctions.queueDirectoriesForParsing(this);
    }

    public int getSelectedSpinnerPosition() {
        return mSpinner.getSelectedItemPosition();
    }

    public int findIndex(SpinnerModel model) {
        for (int i=0; i<categoriesList.size(); i++) {
            SpinnerModel spinnerModel = categoriesList.get(i);
            if (spinnerModel.getVersionCode().equals(model.getVersionCode()) &&
                    spinnerModel.getLanguageName().equals(model.getLanguageName()) &&
                    spinnerModel.getLanguageCode().equals(model.getLanguageCode())) {
                return i;
            }
        }
        return -1;
    }

    private void getCategories() {
        final Realm realm = Realm.getDefaultInstance();
        categoriesList.clear();
        ArrayList<LanguageModel> languageModels = query(realm, new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (LanguageModel languageModel : languageModels) {
            for (VersionModel versionModel : languageModel.getVersionModels()) {
                SpinnerModel spinnerModel = new SpinnerModel();
                spinnerModel.setLanguageCode(languageModel.getLanguageCode());
                spinnerModel.setVersionCode(versionModel.getVersionCode());
                spinnerModel.setLanguageName(languageModel.getLanguageName());
                categoriesList.add(spinnerModel);
            }
        }
        realm.close();
        spinnerAdapter.notifyDataSetChanged();
    }

    public ArrayList<LanguageModel> query(Realm realm, Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SpinnerModel spinnerModel = categoriesList.get(position);//(SpinnerModel) parent.getItemAtPosition(position);

        if (spinnerModel.getLanguageCode().equals(languageCode) && spinnerModel.getLanguageName().equals(languageName) && spinnerModel.getVersionCode().equals(versionCode)) {
            // do nothing, same element selected again
        } else {
            // save to shared prefs
            languageCode = spinnerModel.getLanguageCode();
            languageName = spinnerModel.getLanguageName();
            versionCode = spinnerModel.getVersionCode();
            SharedPrefs.putString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, languageCode);
            SharedPrefs.putString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_NAME, languageName);
            SharedPrefs.putString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, versionCode);
            new AutographaRepository<LanguageModel>().addToNewContainer(languageCode, versionCode);
            getAllBooks();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getAllBooks() {
        mBookModelArrayList.clear();
        for (BookIdModel bookModel : Constants.CONTAINER_BOOKS_LIST) {
            mBookModelArrayList.add(bookModel);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_title: {
                Intent intent = new Intent(this, AboutPageActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.iv_continue_reading: {
                String bookId = null, verse = null;
                int chapter = 1;
                String value = SharedPrefs.getString(Constants.PrefKeys.LAST_READ + "_" + languageCode + "_" + versionCode, null);
                if (value == null) {
                    break;
                }
                try {
                    JSONObject object = new JSONObject(value);
                    bookId = object.getString(Constants.PrefKeys.LAST_READ_BOOK_ID);
                    chapter = object.getInt(Constants.PrefKeys.LAST_READ_CHAPTER);
                    verse = object.getString(Constants.PrefKeys.LAST_READ_VERSE);
                } catch (JSONException je) {
                }
                if (bookId == null) {
                    break;
                }
                if (verse == null) {
                    break;
                }
                Intent readIntent = new Intent(this, BookActivity.class);
                readIntent.putExtra(Constants.Keys.BOOK_ID, bookId);
                readIntent.putExtra(Constants.Keys.CHAPTER_NO, chapter);
                readIntent.putExtra(Constants.Keys.VERSE_NO, verse);
                startActivity(readIntent);
                break;
            }
            case R.id.iv_notes: {
                Intent notesIntent = new Intent(this, NotesActivity.class);
                startActivity(notesIntent);
                break;
            }
            case R.id.iv_bookmark: {
                Intent menuIntent = new Intent(this, BookmarkActivity.class);
                startActivity(menuIntent);
                break;
            }
            case R.id.iv_highlight: {
                Intent menuIntent = new Intent(this, HighlightActivity.class);
                startActivity(menuIntent);
                break;
            }
            case R.id.iv_search: {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            }
            case R.id.iv_history: {
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            }
            case R.id.iv_settings: {
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingIntent, Constants.RequestCodes.SETTINGS);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.SETTINGS: {
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra(Constants.Keys.RECREATE_NEEDED, false)) {
                        this.recreate();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onParsingComplete);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Constants.CONTAINER_BOOKS_LIST.size() == 0) {
            // memory might be cleared, load all data again
            languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
            languageName = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_NAME, "English");
            versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
            new AutographaRepository<LanguageModel>().addToNewContainer(languageCode, versionCode);
            getAllBooks();
        }
    }

    private BroadcastReceiver onParsingComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCategories();
            if (intent.getBooleanExtra(Constants.Keys.REFRESH_CONTAINER, false)) {
                new AutographaRepository<LanguageModel>().addToNewContainer(languageCode, versionCode);
                getAllBooks();
            }
        }
    };

}