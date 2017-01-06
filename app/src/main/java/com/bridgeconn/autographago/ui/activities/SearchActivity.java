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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.SearchAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtSearch;
    private ImageView mIvClose;
    private ArrayList<SearchModel> searchResultModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIvClose = (ImageView) findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(this);
        mEtSearch.setOnClickListener(this);

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = mEtSearch.getText().toString();
                    if (!text.equals("")) {
                        UtilFunctions.hideKeyboard(SearchActivity.this, mEtSearch);
                        // clear any previous results
                        doSearch(mEtSearch.getText().toString());
                        // show search results
                        return true;
                    }
                }
                return false;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list_results);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(this, searchResultModels);
        recyclerView.setAdapter(searchAdapter);
    }

    private void doSearch(String searchText) {

        searchResultModels.clear();

        searchInBookName(searchText);
        searchInVerseText(searchText);

        showView();
    }

    private void showView() {
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
                        searchModel.setVerseNumber(1);
                        searchModel.setText(getVerseText(model.getChapterModels().get(0)));
                        searchResultModels.add(searchModel);
                        break;
                    }
                }
            }
        }
    }

    private String getVerseText(ChapterModel chapterModel) {
        String text = "";
        for (VerseComponentsModel verseComponentsModel : chapterModel.getVerseComponentsModels()) {
            if (verseComponentsModel.getVerseNumber() > 1) {
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
                    ArrayList<BookModel> bookList = new AutographaRepository<BookModel>().query(new AllSpecifications.BookModelById(splitString[0]), new AllMappers.BookMapper());
                    if (bookList.size() > 0) {
                        searchModel.setBookName(bookList.get(0).getBookName());
                    }
                    searchModel.setChapterNumber(Integer.parseInt(splitString[1]));
                }
                searchModel.setVerseNumber(verseComponentsModel.getVerseNumber());
                searchModel.setText(verseComponentsModel.getText());
                searchResultModels.add(searchModel);
            }
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilFunctions.hideKeyboard(this, mEtSearch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilFunctions.hideKeyboard(this, mEtSearch);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                UtilFunctions.hideKeyboard(this, mEtSearch);
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
                mEtSearch.setText("");
                mEtSearch.requestFocus();
                mEtSearch.setCursorVisible(true);
                UtilFunctions.showKeyboard(SearchActivity.this, mEtSearch);
                break;
            }
            case R.id.et_search: {
                mEtSearch.setCursorVisible(true);
                break;
            }
        }
    }

}