package com.bridgeconn.autographago.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.RealmInteger;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.adapters.NumberAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChapterFragment extends Fragment implements SelectChapterAndVerseActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private BookModel mBookModel;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();
    private String mBookId;
    private boolean mOpenBook, mSelectVerse;

    @Override
    public void onItemClick(int number, String bookId) {
        mBookId = bookId;
        mBookModel = getBookModel(mBookId);
        if (mBookModel != null) {
            mChapterModels.clear();
            for (ChapterModel chapterModel : mBookModel.getChapterModels()) {
                mChapterModels.add(chapterModel);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookId = getArguments().getString(Constants.Keys.BOOK_ID);
        mOpenBook = getArguments().getBoolean(Constants.Keys.OPEN_BOOK);
        mSelectVerse = getArguments().getBoolean(Constants.Keys.SELECT_VERSE_FOR_NOTE);
        int chapterNum = getArguments().getInt(Constants.Keys.CHAPTER_NO);

        mBookModel = getBookModel(mBookId);
        if (mBookModel != null) {
            for (int i = 0; i < mBookModel.getChapterModels().size(); i++) {
                mChapterModels.add(mBookModel.getChapterModels().get(i));
            }

            if (mChapterModels.size() > 0) {
                setSelected(0);
                    for (int i = 0; i < mChapterModels.size(); i++) {
                        if (mChapterModels.get(i).getChapterNumber() == chapterNum) {
                            setSelected(i);
                            break;
                        }
                    }
            }
        }
    }

    private BookModel getBookModel(String bookId) {
        final Realm realm = Realm.getDefaultInstance();
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<BookModel> resultList = query(realm, new AllSpecifications.BookModelById(languageCode, versionCode, bookId), new AllMappers.BookMapper());
        if (resultList.size() > 0) {
            BookModel bModel = resultList.get(0);
            BookModel bookModel = new BookModel();
            for (RealmInteger realmInteger : bModel.getBookmarksList()) {
                bookModel.getBookmarksList().add(realmInteger);
            }
//            bookModel.setBookmarkChapterNumber(bModel.getBookmarkChapterNumber());
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
                bookModel.getChapterModels().add(chapterModel);
            }
            realm.close();
            return bookModel;
        }
        realm.close();
        return null;
    }

    public ArrayList<BookModel> query(Realm realm, Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chapter_verse, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_numbers);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new NumberAdapter(this, mChapterModels, null, null, 0, mBookId, mOpenBook, mSelectVerse);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void setSelected(int position) {
        for (int i = 0; i< mChapterModels.size(); i++) {
            mChapterModels.get(i).setSelected(false);
        }
        if (position >= 0) {
            mChapterModels.get(position).setSelected(true);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public int getSelectedChapterNumber() {
        for (int i = 0; i< mChapterModels.size(); i++) {
            if (mChapterModels.get(i).isSelected()) {
                return mChapterModels.get(i).getChapterNumber();
            }
        }
        return 0;
    }

}