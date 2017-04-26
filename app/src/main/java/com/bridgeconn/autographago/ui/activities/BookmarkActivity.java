package com.bridgeconn.autographago.ui.activities;

import android.os.AsyncTask;
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
import com.bridgeconn.autographago.models.RealmInteger;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.BookmarkAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class BookmarkActivity extends AppCompatActivity {

    private TextView mToolBarTitle;
    private RecyclerView mRecyclerViewBookmarks;
    private BookmarkAdapter mBookmarkAdapter;
    private ArrayList<BookIdModel> mBookmarkModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        getSupportActionBar().setTitle("");
        mToolBarTitle.setText(getResources().getString(R.string.bookmarks));

        mRecyclerViewBookmarks = (RecyclerView) findViewById(R.id.list_menu);

        mRecyclerViewBookmarks.setHasFixedSize(true);
        mRecyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(this));
        mBookmarkAdapter = new BookmarkAdapter(this, mBookmarkModels);
        mRecyclerViewBookmarks.setAdapter(mBookmarkAdapter);

        getBookmarks();
    }

    private void getBookmarks() {
        mBookmarkModels.clear();
        for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
            for (Integer integer : bookIdModel.getBookmarksList()) {
//            if (bookIdModel.getBookmarkChapterNumber() > 0) {
                BookIdModel bookIdModel1 = new BookIdModel();
                bookIdModel1.setBookmarkChapterNumber(integer);
                bookIdModel1.setBookNumber(bookIdModel.getBookNumber());
                bookIdModel1.setLanguageCode(bookIdModel.getLanguageCode());
                bookIdModel1.setBookName(bookIdModel.getBookName());
                bookIdModel1.setBookId(bookIdModel.getBookId());
                bookIdModel1.setSection(bookIdModel.getSection());
                bookIdModel1.setVersionCode(bookIdModel.getVersionCode());
                mBookmarkModels.add(bookIdModel1);
            }
        }
        mBookmarkAdapter.notifyDataSetChanged();
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
        int bookMarkNumber = mBookmarkModels.get(position).getBookmarkChapterNumber();
//        mBookmarkModels.get(position).setBookmarkChapterNumber(0);
        for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
            if (bookIdModel.getBookId().equals(mBookmarkModels.get(position).getBookId())) {
                bookIdModel.setBookmarkChapterNumber(0);
                int pos = bookIdModel.getBookmarksList().indexOf(bookMarkNumber);
                bookIdModel.getBookmarksList().remove(pos);
            }
        }
        String bookId = mBookmarkModels.get(position).getBookId();
        mBookmarkModels.remove(position);
        mBookmarkAdapter.notifyItemRemoved(position);
        mBookmarkAdapter.notifyItemRangeChanged(position, mBookmarkModels.size(), null);

        final Realm realm = Realm.getDefaultInstance();
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<BookModel> resultList = query(realm, new AllSpecifications.BookModelById(languageCode, versionCode, bookId), new AllMappers.BookMapper());
        if (resultList.size() > 0) {
            BookModel bModel = resultList.get(0);
            new AutographaRepository<BookModel>().updateBookWithBookMark(realm, bModel, bookMarkNumber, false);
        }
        realm.close();
    }

    public ArrayList<BookModel> query(Realm realm, Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }
}