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
import com.bridgeconn.autographago.models.VerseComponentsModel;
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

public class VerseFragment extends Fragment implements SelectChapterAndVerseActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private BookModel mBookModel;
    private String mBookId;
    private boolean mOpenBook, mSelectVerse;
    private ArrayList<VerseComponentsModel> mVerseComponentsModels = new ArrayList<>();
    private int mChapterNumber;

    @Override
    public void onItemClick(int number, String bookId) {
        mChapterNumber = number;

        mBookModel = getBookModel(bookId, mChapterNumber-1);
        if (mBookModel == null) {
            return;
        }
        if (mBookModel.getChapterModels().size() == 0) {
            return;
        }

        ChapterModel chapterModel = mBookModel.getChapterModels().get(0);
        mVerseComponentsModels.clear();
        for (VerseComponentsModel model : chapterModel.getVerseComponentsModels()) {
            if (mVerseComponentsModels.size() > 0) {
                if (model.getVerseNumber().equals(mVerseComponentsModels.get(mVerseComponentsModels.size() - 1).getVerseNumber())) {
                    continue;
                } else {
                    mVerseComponentsModels.add(model);
                }
            } else {
                mVerseComponentsModels.add(model);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookId = getArguments().getString(Constants.Keys.BOOK_ID);
        mChapterNumber = getArguments().getInt(Constants.Keys.CHAPTER_NO);
        mOpenBook = getArguments().getBoolean(Constants.Keys.OPEN_BOOK);
        mSelectVerse = getArguments().getBoolean(Constants.Keys.SELECT_VERSE_FOR_NOTE);

        mBookModel = getBookModel(mBookId, mChapterNumber-1);
        if (mBookModel == null) {
            return;
        }
        if (mBookModel.getChapterModels().size() == 0) {
            return;
        }

        ChapterModel chapterModel = mBookModel.getChapterModels().get(0);
        mVerseComponentsModels.clear();
        for (VerseComponentsModel model : chapterModel.getVerseComponentsModels()) {
            if (mVerseComponentsModels.size() > 0) {
                if (model.getVerseNumber().equals(mVerseComponentsModels.get(mVerseComponentsModels.size() - 1).getVerseNumber())) {
                    continue;
                } else {
                    mVerseComponentsModels.add(model);
                }
            } else {
                mVerseComponentsModels.add(model);
            }
        }
    }

    private BookModel getBookModel(String bookId, int chapterPosition) {
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
            ChapterModel cModel = bModel.getChapterModels().get(chapterPosition);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new NumberAdapter(this, null, null, mVerseComponentsModels, mChapterNumber, mBookId, mOpenBook, mSelectVerse);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
