package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.BookmarkAdapter;
import com.bridgeconn.autographago.ui.adapters.HighlightAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private TextView mTvHighlights;
    private RecyclerView mRecyclerViewHighlights;
    private TextView mTvBookmarks;
    private RecyclerView mRecyclerViewBookmarks;

    private BookmarkAdapter mBookmarkAdapter;
    private HighlightAdapter mHighlightAdapter;

    private ArrayList<BookModel> mBookmarkModels = new ArrayList<>();
    private  ArrayList<VerseComponentsModel> mHighlightModels = new ArrayList<>();
    private String languageCode, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        UtilFunctions.applyReadingMode();

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mTvHighlights = (TextView) findViewById(R.id.tv_highlights);
        mTvBookmarks = (TextView) findViewById(R.id.tv_bookmarks);

        mRecyclerViewHighlights = (RecyclerView) findViewById(R.id.list_highlights);
        mRecyclerViewBookmarks = (RecyclerView) findViewById(R.id.list_bookmarks);

        mRecyclerViewBookmarks.setHasFixedSize(true);
        mRecyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        mBookmarkAdapter = new BookmarkAdapter(this, mBookmarkModels);
        mRecyclerViewBookmarks.setAdapter(mBookmarkAdapter);

        getBookmarks();

        mRecyclerViewHighlights.setHasFixedSize(true);
        mRecyclerViewHighlights.setLayoutManager(new LinearLayoutManager(this));
        mHighlightAdapter = new HighlightAdapter(this, mHighlightModels);
        mRecyclerViewHighlights.setAdapter(mHighlightAdapter);

        getHighlights();
    }

    private void getBookmarks() {
        List<BookModel> results = new AutographaRepository<BookModel>().query(new AllSpecifications.AllBookmarks(languageCode, versionCode), new AllMappers.BookMapper());
        for (BookModel model : results) {
            mBookmarkModels.add(model);
        }
        mBookmarkAdapter.notifyDataSetChanged();
    }

    private void getHighlights() {
        List<VerseComponentsModel> results = new AutographaRepository<VerseComponentsModel>().query(new AllSpecifications.AllHighlights(languageCode, versionCode), new AllMappers.VerseComponentsMapper());
        for (VerseComponentsModel model : results) {
            mHighlightModels.add(model);
        }
        mHighlightAdapter.notifyDataSetChanged();
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

    public void refreshBookMarkList(int position) {
        mBookmarkModels.get(position).setBookmarkChapterNumber(0);
        new AutographaRepository<BookModel>().update(mBookmarkModels.get(position));
        mBookmarkModels.remove(position);
        mBookmarkAdapter.notifyItemRemoved(position);
    }

    public void refreshHighlightList(int position) {
        mHighlightModels.get(position).setHighlighted(false);
        new AutographaRepository<VerseComponentsModel>().update(mHighlightModels.get(position));
        mHighlightModels.remove(position);
        mHighlightAdapter.notifyItemRemoved(position);
    }

}