package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.BookmarkAdapter;
import com.bridgeconn.autographago.ui.adapters.HighlightAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity {

    private TextView mTvHighlights;
    private RecyclerView mRecyclerViewHighlights;
    private TextView mTvBookmarks;
    private RecyclerView mRecyclerViewBookmarks;

    private BookmarkAdapter mBookmarkAdapter;
    private HighlightAdapter mHighlightAdapter;

    private ArrayList<BookIdModel> mBookmarkModels = new ArrayList<>();
    private  ArrayList<VerseComponentsModel> mHighlightModels = new ArrayList<>();
    private String languageCode, versionCode;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        UtilFunctions.applyReadingMode();

        realm = Realm.getDefaultInstance();

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
        mBookmarkModels.clear();
        for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
            if (bookIdModel.getBookmarkChapterNumber() > 0) {
                BookIdModel bookIdModel1 = new BookIdModel();
                bookIdModel1.setBookmarkChapterNumber(bookIdModel.getBookmarkChapterNumber());
                bookIdModel1.setBookNumber(bookIdModel.getBookNumber());
                bookIdModel1.setLanguageCode(bookIdModel.getLanguageCode());
                bookIdModel1.setBookName(bookIdModel.getBookName());
                bookIdModel1.setBookId(bookIdModel.getBookId());
                bookIdModel1.setSection(bookIdModel.getSection());
                bookIdModel1.setVersionCode(bookIdModel.getVersionCode());
                mBookmarkModels.add(bookIdModel1);
            }
        }
//        List<BookModel> results = new AutographaRepository<BookModel>().query(new AllSpecifications.AllBookmarks(languageCode, versionCode), new AllMappers.BookMapper());
//        for (BookModel model : results) {
//            mBookmarkModels.add(model);
//        }
        mBookmarkAdapter.notifyDataSetChanged();
    }

    private void getHighlights() {
        List<VerseComponentsModel> results = new AutographaRepository<VerseComponentsModel>().query(new AllSpecifications.AllHighlights(languageCode, versionCode), new AllMappers.VerseComponentsMapper());
        for (VerseComponentsModel model : results) {
            VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
            verseComponentsModel.setChapterId(model.getChapterId());
            verseComponentsModel.setType(model.getType());
            verseComponentsModel.setVersionCode(model.getVersionCode());
            verseComponentsModel.setVerseNumber(model.getVerseNumber());
            verseComponentsModel.setHighlighted(model.isHighlighted());
            verseComponentsModel.setLanguageCode(model.getLanguageCode());
            verseComponentsModel.setText(model.getText());
            mHighlightModels.add(verseComponentsModel);
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
        for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
            if (bookIdModel.getBookId().equals(mBookmarkModels.get(position).getBookId())) {
                bookIdModel.setBookmarkChapterNumber(0);
            }
        }
        BookModel bookModel = getBookModel(mBookmarkModels.get(position).getBookId());
        bookModel.setBookmarkChapterNumber(0);
        new AutographaRepository<BookModel>().update(bookModel);
        mBookmarkModels.remove(position);
        mBookmarkAdapter.notifyItemRemoved(position);
    }

    public void refreshHighlightList(int position) {

        VerseComponentsModel model = mHighlightModels.get(position);
        String [] splitString = model.getChapterId().split("_");

        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setChapterId(model.getChapterId());
        chapterModel.setChapterNumber(Integer.parseInt(splitString[1]));
        chapterModel.setVersionCode(versionCode);
        chapterModel.setLanguageCode(languageCode);

        BookModel bookModel = null;
        String bookId = splitString[0];

        bookModel = getBookModel(bookId);

        if (bookModel != null) {
            for (ChapterModel cModel : bookModel.getChapterModels()) {
                if (cModel.getChapterId().equals(model.getChapterId())) {
                    chapterModel.setVerseComponentsModels(cModel.getVerseComponentsModels());
                    int size = 0;
                    for (int i=0; i<chapterModel.getVerseComponentsModels().size(); i++) {
                        if (i==0) {
                            size++;
                        } else {
                            if (chapterModel.getVerseComponentsModels().get(i).getVerseNumber().equals(
                                    chapterModel.getVerseComponentsModels().get(i-1).getVerseNumber())) {
                                continue;
                            } else {
                                size++;
                            }
                        }
                    }
                    chapterModel.setNumberOfVerses(size);
                    for (VerseComponentsModel vModel : chapterModel.getVerseComponentsModels()) {
                        if (vModel.getVerseNumber().equals(model.getVerseNumber())) {
                            vModel.setHighlighted(false);
                        }
                    }
                    break;
                }
            }
        }

        mHighlightModels.get(position).setHighlighted(false);
        new AutographaRepository<ChapterModel>().update(chapterModel);
        mHighlightModels.remove(position);
        mHighlightAdapter.notifyItemRemoved(position);
    }

    private BookModel getBookModel(String bookId) {
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<BookModel> resultList = query(new AllSpecifications.BookModelById(languageCode, versionCode, bookId), new AllMappers.BookMapper());
        if (resultList.size() > 0) {
            BookModel bModel = resultList.get(0);
            BookModel bookModel = new BookModel();
            bookModel.setBookmarkChapterNumber(bModel.getBookmarkChapterNumber());
            bookModel.setBookNumber(bModel.getBookNumber());
            bookModel.setSection(bModel.getSection());
            bookModel.setVersionCode(bModel.getVersionCode());
            bookModel.setLanguageCode(bModel.getLanguageCode());
            bookModel.setBookId(bModel.getBookId());
            bookModel.setBookName(bModel.getBookName());
            for (ChapterModel cModel : bModel.getChapterModels()) {
                ChapterModel chapterModel = new ChapterModel();
                chapterModel.setChapterNumber(cModel.getChapterNumber());
                chapterModel.setLanguageCode(cModel.getLanguageCode());
                chapterModel.setVersionCode(cModel.getVersionCode());
                chapterModel.setChapterId(cModel.getChapterId());
                chapterModel.setNumberOfVerses(cModel.getNumberOfVerses());
                for (VerseComponentsModel vModel : cModel.getVerseComponentsModels()) {
                    VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
                    verseComponentsModel.setChapterId(vModel.getChapterId());
                    verseComponentsModel.setHighlighted(vModel.isHighlighted());
                    verseComponentsModel.setVersionCode(vModel.getVersionCode());
                    verseComponentsModel.setLanguageCode(vModel.getLanguageCode());
                    verseComponentsModel.setText(vModel.getText());
                    verseComponentsModel.setVerseNumber(vModel.getVerseNumber());
                    verseComponentsModel.setType(vModel.getType());
                    chapterModel.getVerseComponentsModels().add(verseComponentsModel);
                }
                bookModel.getChapterModels().add(chapterModel);
            }
            return bookModel;
        }
        return null;
    }

    public ArrayList<BookModel> query(Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}