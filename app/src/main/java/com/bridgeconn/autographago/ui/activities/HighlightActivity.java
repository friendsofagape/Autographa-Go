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
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.HighlightAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HighlightActivity extends AppCompatActivity {

    private TextView mToolBarTitle;
    private RecyclerView mRecyclerViewHighlights;
    private HighlightAdapter mHighlightAdapter;
    private  ArrayList<VerseIdModel> mHighlightModels = new ArrayList<>();
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

        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        getSupportActionBar().setTitle("");
        mToolBarTitle.setText(getResources().getString(R.string.highlights));

        mRecyclerViewHighlights = (RecyclerView) findViewById(R.id.list_menu);

        mRecyclerViewHighlights.setHasFixedSize(true);
        mRecyclerViewHighlights.setLayoutManager(new LinearLayoutManager(this));
        mHighlightAdapter = new HighlightAdapter(this, mHighlightModels);
        mRecyclerViewHighlights.setAdapter(mHighlightAdapter);

        getHighlights();
    }

    private void getHighlights() {
        List<BookModel> bookModels = query(
                new AllSpecifications.BooksByLanguageAndVersion(languageCode, versionCode), new AllMappers.BookMapper());
        HashSet<VerseIdModel> verseIdModelHashSet = new HashSet<>();
        for (BookModel bookModel : bookModels) {
            for (ChapterModel chapterModel : bookModel.getChapterModels()) {
                for (VerseComponentsModel verseComponentsModel : chapterModel.getVerseComponentsModels()) {
                    if (verseComponentsModel.isHighlighted()) {
                        VerseIdModel verseIdModel = new VerseIdModel();
                        verseIdModel.setBookId(bookModel.getBookId());
                        verseIdModel.setBookName(bookModel.getBookName());
                        verseIdModel.setChapterNumber(chapterModel.getChapterNumber());
                        verseIdModel.setVerseNumber(verseComponentsModel.getVerseNumber());
                        verseIdModelHashSet.add(verseIdModel);
                    }
                }
            }
        }
        for (VerseIdModel model : verseIdModelHashSet) {
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

    public void refreshHighlightList(int position) {

        VerseIdModel model = mHighlightModels.get(position);
        String bookId = model.getBookId();
        String chapterId = model.getBookId() + "_" + model.getChapterNumber();
        String verseNumber = model.getVerseNumber();

        mHighlightModels.remove(position);
        mHighlightAdapter.notifyItemRemoved(position);
        mHighlightAdapter.notifyItemRangeChanged(position, mHighlightModels.size(), null);

        BookModel bookModel = null;
        bookModel = getBookModel(bookId);

        if (bookModel != null) {
            for (ChapterModel cModel : bookModel.getChapterModels()) {
                if (cModel.getChapterId().equals(languageCode + "_" + versionCode + "_" + chapterId)) {
                    for (VerseComponentsModel vModel : cModel.getVerseComponentsModels()) {
                        if (vModel.getVerseNumber().equals(verseNumber)) {
                            vModel.setHighlighted(false);
                        }
                    }
                    break;
                }
            }
            new AutographaRepository<BookModel>().update(bookModel);
        }
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
            bookModel.setBookPrimaryId(bModel.getBookPrimaryId());
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