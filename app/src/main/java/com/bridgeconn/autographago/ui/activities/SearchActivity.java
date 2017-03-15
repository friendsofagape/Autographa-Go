package com.bridgeconn.autographago.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.SearchHistoryModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.SearchAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, TextView.OnEditorActionListener {

    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageView mIvClose;
    private ArrayList<SearchModel> mSearchResultModels = new ArrayList<>();
    private ArrayList<SearchModel> mShowSearchResultModels = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private ProgressBar mProgressBar;
    private LinearLayout noResultsFound;
    private String languageCode, versionCode;
    private Realm realm;
    private RadioGroup sectionGroupView;
    private TextView numOfResults;
    private PerformSearch performSearchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        UtilFunctions.applyReadingMode();

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_search);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        noResultsFound = (LinearLayout) findViewById(R.id.no_results_found);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_results);
        sectionGroupView = (RadioGroup) findViewById(R.id.section_group);
        numOfResults = (TextView) findViewById(R.id.numResults);

        sectionGroupView.setOnCheckedChangeListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(this, mShowSearchResultModels);
        mRecyclerView.setAdapter(mAdapter);

        mIvClose.setOnClickListener(this);
        mAutoCompleteTextView.setOnClickListener(this);

        mAutoCompleteTextView.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = mAutoCompleteTextView.getText().toString();
            if (!text.equals("")) {
                UtilFunctions.hideKeyboard(SearchActivity.this, mAutoCompleteTextView);

                realm = Realm.getDefaultInstance();
                ArrayList<SearchHistoryModel> resultList = querySearchHistory(new AllSpecifications.SearchHistoryModelByText(text), new AllMappers.SearchHistoryMapper());

                SearchHistoryModel model = new SearchHistoryModel();
                model.setLastSearchTime(System.currentTimeMillis());
                model.setSearchText(text);

                if (resultList.size() > 0) {
                    model.setSearchCount(resultList.get(0).getSearchCount() + 1);
                    new AutographaRepository<SearchHistoryModel>().update(model);
                } else {
                    model.setSearchCount(1);
                    new AutographaRepository<SearchHistoryModel>().add(model);
                }
                realm.close();
                doSearch(text, languageCode, versionCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.section_group: {
                mShowSearchResultModels.clear();
                switch (checkedId) {
                    case R.id.allSection: {
                        mShowSearchResultModels.addAll(mSearchResultModels);
                        break;
                    }
                    case R.id.oldSection: {
                        for (SearchModel searchModel : mSearchResultModels) {
                            if (searchModel.getBookNumber() < 40) {
                                mShowSearchResultModels.add(searchModel);
                            }
                        }
                        break;
                    }
                    case R.id.newSection: {
                        for (SearchModel searchModel : mSearchResultModels) {
                            if (searchModel.getBookNumber() > 40) {
                                mShowSearchResultModels.add(searchModel);
                            }
                        }
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mShowSearchResultModels.size() == 0) {
                    noResultsFound.setVisibility(View.VISIBLE);
                } else {
                    noResultsFound.setVisibility(View.GONE);
                }
                numOfResults.setVisibility(View.VISIBLE);
                numOfResults.setText(mShowSearchResultModels.size() + " " + getResources().getString(R.string.search_results_found));
                break;
            }
        }
    }

    private void doSearch(String searchText, String languageCode, String versionCode) {
        if (performSearchTask != null) {
            performSearchTask.cancel(true);
        }
        performSearchTask = new PerformSearch();
        performSearchTask.execute(new String[] { searchText, languageCode, versionCode });
    }

    private String getVerseText(ChapterModel chapterModel) {
        String text = "";
        for (VerseComponentsModel verseComponentsModel : chapterModel.getVerseComponentsModels()) {
            String [] verseNumberSplit = verseComponentsModel.getVerseNumber().split("-");
            if (Integer.parseInt(verseNumberSplit[0]) > 1) {
                break;
            }
            switch (verseComponentsModel.getType()) {
                case Constants.MarkerTypes.VERSE: {
                    text = verseComponentsModel.getText();
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return text;
    }

    public ArrayList<SearchHistoryModel> querySearchHistory(Specification<SearchHistoryModel> specification, Mapper<SearchHistoryModel, SearchHistoryModel> mapper) {
        RealmResults<SearchHistoryModel> realmResults = specification.generateResults(realm);
        ArrayList<SearchHistoryModel> resultsToReturn = new ArrayList<>();
        for (SearchHistoryModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    public ArrayList<BookModel> queryBook(Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    public ArrayList<VerseComponentsModel> queryVerse(Specification<VerseComponentsModel> specification, Mapper<VerseComponentsModel, VerseComponentsModel> mapper) {
        RealmResults<VerseComponentsModel> realmResults = specification.generateResults(realm);
        ArrayList<VerseComponentsModel> resultsToReturn = new ArrayList<>();
        for (VerseComponentsModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilFunctions.hideKeyboard(this, mAutoCompleteTextView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilFunctions.hideKeyboard(this, mAutoCompleteTextView);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                UtilFunctions.hideKeyboard(this, mAutoCompleteTextView);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close: {
                mAutoCompleteTextView.setText("");
                mAutoCompleteTextView.requestFocus();
                mAutoCompleteTextView.setCursorVisible(true);
                UtilFunctions.showKeyboard(SearchActivity.this, mAutoCompleteTextView);
                break;
            }
            case R.id.auto_complete_search: {
                mAutoCompleteTextView.setCursorVisible(true);
                break;
            }
        }
    }

    private class PerformSearch extends AsyncTask<String, SearchModel, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            noResultsFound.setVisibility(View.GONE);
            sectionGroupView.setVisibility(View.VISIBLE);
            mShowSearchResultModels.clear();
            mSearchResultModels.clear();
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.VISIBLE);
            numOfResults.setVisibility(View.VISIBLE);
            numOfResults.setText(mShowSearchResultModels.size() + " " + getResources().getString(R.string.search_results_found));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            mProgressBar.setVisibility(View.GONE);
            mShowSearchResultModels.clear();
            mSearchResultModels.clear();
            mAdapter.notifyDataSetChanged();
            sectionGroupView.setVisibility(View.GONE);
            noResultsFound.setVisibility(View.GONE);
            numOfResults.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            realm = Realm.getDefaultInstance();
            if (!params[0].trim().equals("")) {
                List<BookModel> resultList = queryBook(
                        new AllSpecifications.SearchInBookName(params[0], params[1], params[2]), new AllMappers.BookMapper());

                for (BookModel bookModel : resultList) {
                    if (isCancelled()) {
                        realm.close();
                        return null;
                    }
                    SearchModel searchModel = new SearchModel();
                    searchModel.setBookId(bookModel.getBookId());
                    searchModel.setBookName(bookModel.getBookName());
                    searchModel.setChapterNumber(1);
                    searchModel.setVerseNumber("1");
                    searchModel.setText(getVerseText(bookModel.getChapterModels().get(0)));
                    searchModel.setSection(bookModel.getSection());
                    searchModel.setBookNumber(bookModel.getBookNumber());
                    searchModel.setLanguageCode(languageCode);
                    searchModel.setVersionCode(versionCode);
                    publishProgress(searchModel);
                }
            }

            if (!params[0].trim().equals("")) {
                List<VerseComponentsModel> resultList = queryVerse(
                        new AllSpecifications.SearchInVerseComponentsText(params[0], params[1], params[2]), new AllMappers.VerseComponentsMapper());
                for (VerseComponentsModel verseComponentsModel : resultList) {
                    if (isCancelled()) {
                        realm.close();
                        return null;
                    }
                    SearchModel searchModel = new SearchModel();
                    String [] splitString = verseComponentsModel.getChapterId().split("_");
                    if (splitString.length > 1) {
                        searchModel.setBookId(splitString[0]);
                        searchModel.setBookName(UtilFunctions.getBookNameFromMapping(SearchActivity.this, splitString[0]));
                        searchModel.setSection(UtilFunctions.getBookSectionFromMapping(SearchActivity.this, splitString[0]));
                        searchModel.setChapterNumber(Integer.parseInt(splitString[1]));
                        searchModel.setVerseNumber(verseComponentsModel.getVerseNumber());
                        searchModel.setText(verseComponentsModel.getText());
                        searchModel.setSection(UtilFunctions.getBookSectionFromMapping(SearchActivity.this, splitString[0]));
                        searchModel.setBookNumber(UtilFunctions.getBookNumberFromMapping(SearchActivity.this, splitString[0]));
                        searchModel.setLanguageCode(languageCode);
                        searchModel.setVersionCode(versionCode);
                        publishProgress(searchModel);
                    }
                }
            }
            realm.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(SearchModel... values) {
            if (isCancelled()) {
                return;
            }
            mSearchResultModels.add(values[0]);

            noResultsFound.setVisibility(View.GONE);
            switch (sectionGroupView.getCheckedRadioButtonId()) {
                case R.id.allSection: {
                    mShowSearchResultModels.add(values[0]);
                    mAdapter.notifyItemRangeInserted(mShowSearchResultModels.size()-1, 1);
                    break;
                }
                case R.id.oldSection: {
                    if (values[0].getBookNumber() < 40) {
                        mShowSearchResultModels.add(values[0]);
                        mAdapter.notifyItemRangeInserted(mShowSearchResultModels.size()-1, 1);
                    }
                    break;
                }
                case R.id.newSection: {
                    if (values[0].getBookNumber() > 40) {
                        mShowSearchResultModels.add(values[0]);
                        mAdapter.notifyItemRangeInserted(mShowSearchResultModels.size()-1, 1);
                    }
                    break;
                }
            }
            numOfResults.setText(mShowSearchResultModels.size() + " " + getResources().getString(R.string.search_results_found));
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.GONE);
            if (mShowSearchResultModels.size() == 0) {
                noResultsFound.setVisibility(View.VISIBLE);
            } else {
                noResultsFound.setVisibility(View.GONE);
            }
        }
    }

}