package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtSearch;
    private ImageView mIvClose;
    private List<VerseComponentsModel> verseResults = new ArrayList<>();

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
    }

    private void doSearch(String searchText) {
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
                if (bookModel.getChapterModels().size()>0) {
                    if (bookModel.getChapterModels().get(0).getVerseComponentsModels().size()>0) {
                        verseResults.add(bookModel.getChapterModels().get(0).getVerseComponentsModels().get(0));
                    }
                }
            }
        }
    }

    private void searchInVerseText(String searchText) {
        if (!searchText.trim().equals("")) {
            List<VerseComponentsModel> resultList = new AutographaRepository<VerseComponentsModel>().query(
                    new AllSpecifications.SearchInVerseComponentsText(searchText), new AllMappers.VerseComponentsMapper());

            verseResults.addAll(resultList);
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