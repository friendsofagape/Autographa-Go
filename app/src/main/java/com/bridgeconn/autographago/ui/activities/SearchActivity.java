package com.bridgeconn.autographago.ui.activities;

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
import com.bridgeconn.autographago.ui.adapters.SearchAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    //    private EditText mEtSearch;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ImageView mIvClose;
    private ArrayList<SearchModel> mSearchResultModels = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_search);
//        mEtSearch = (EditText) findViewById(R.id.et_search);

        mIvClose = (ImageView) findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(this);
        mAutoCompleteTextView.setOnClickListener(this);

        mAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = mAutoCompleteTextView.getText().toString();
                    if (!text.equals("")) {
                        UtilFunctions.hideKeyboard(SearchActivity.this, mAutoCompleteTextView);

                        ArrayList<SearchHistoryModel> resultList = new AutographaRepository<SearchHistoryModel>().query(new AllSpecifications.SearchHistoryModelByText(text), new AllMappers.SearchHistoryMapper());

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
                        // clear any previous results
                        doSearch(text);
                        // show search results
                        return true;
                    }
                }
                return false;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list_results);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter(this, mSearchResultModels);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void doSearch(String searchText) {

        mSearchResultModels.clear();

        searchInBookName(searchText);
        searchInVerseText(searchText);
    }

    private void searchInBookName(String searchText) {
        if (!searchText.trim().equals("")) {
            List<BookModel> resultList = new AutographaRepository<BookModel>().query(
                    new AllSpecifications.SearchInBookName(searchText), new AllMappers.BookMapper());

            for (BookModel bookModel : resultList) {
                for (BookModel model : Constants.CONTAINER.getBookModelList()) {
                    if (model.getBookId().equals(bookModel.getBookId())) {
                        SearchModel searchModel = new SearchModel();
                        searchModel.setBookId(model.getBookId());
                        searchModel.setBookName(model.getBookName());
                        searchModel.setChapterNumber(1);
                        searchModel.setVerseNumber("1");
                        searchModel.setText(getVerseText(model.getChapterModels().get(0)));
                        mSearchResultModels.add(searchModel);
                        break;
                    }
                }
            }
        }
    }

    private String getVerseText(ChapterModel chapterModel) {
        String text = "";
        for (VerseComponentsModel verseComponentsModel : chapterModel.getVerseComponentsModels()) {
            String [] verseNumberSplit = verseComponentsModel.getVerseNumber().split("-");
            if (Integer.parseInt(verseNumberSplit[0]) > 1) {
//            if (verseComponentsModel.getVerseNumber() > 1) {
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

    private void searchInVerseText(String searchText) {

        if (!searchText.trim().equals("")) {
            List<VerseComponentsModel> resultList = new AutographaRepository<VerseComponentsModel>().query(
                    new AllSpecifications.SearchInVerseComponentsText(searchText), new AllMappers.VerseComponentsMapper());
            for (VerseComponentsModel verseComponentsModel : resultList) {
                SearchModel searchModel = new SearchModel();
                String [] splitString = verseComponentsModel.getChapterId().split("_");
                if (splitString.length > 1) {
                    searchModel.setBookId(splitString[0]);
                    searchModel.setBookName(UtilFunctions.getBookNameFromMapping(this, splitString[0]));
                    searchModel.setChapterNumber(Integer.parseInt(splitString[1]));
                }
                searchModel.setVerseNumber(verseComponentsModel.getVerseNumber());
                searchModel.setText(verseComponentsModel.getText());
                mSearchResultModels.add(searchModel);
            }
            mAdapter.notifyDataSetChanged();
        }
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

}