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

public class VerseFragment extends Fragment implements View.OnClickListener, SelectChapterAndVerseActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private BookModel mBookModel;
    private String mBookId;
    private boolean mOpenBook;
    private ArrayList<VerseComponentsModel> mVerseComponentsModels = new ArrayList<>();
    private int mNumberOfBlocks;
    private int mChapterNumber;
    private Realm realm;

    @Override
    public void onItemClick(int number, String bookId) {
        mChapterNumber = number;

        mBookModel = getBookModel(bookId);
        if (mBookModel == null) {
            return;
        }
        mNumberOfBlocks = getNumberOfVerses(mBookModel);

        ChapterModel chapterModel = mBookModel.getChapterModels().get(mChapterNumber - 1);
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

//        mAdapter.changeVerseCount(mNumberOfBlocks);
//        mAdapter.changeChapterNumber(mChapterNumber);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        mBookId = getArguments().getString(Constants.Keys.BOOK_ID);
        mChapterNumber = getArguments().getInt(Constants.Keys.CHAPTER_NO);
        mOpenBook = getArguments().getBoolean(Constants.Keys.OPEN_BOOK);

        mBookModel = getBookModel(mBookId);
        if (mBookModel == null) {
            return;
        }
        if (mBookModel.getChapterModels().size() == 0) {
            return;
        }
        mNumberOfBlocks = getNumberOfVerses(mBookModel);

        ChapterModel chapterModel = mBookModel.getChapterModels().get(mChapterNumber - 1);
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

    private int getNumberOfVerses(BookModel bookModel) {
        // TODO fix java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
        ChapterModel chapterModel = bookModel.getChapterModels().get(mChapterNumber - 1);

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
        return size;
//        return chapterModel.getVerseComponentsModels().get(chapterModel.getVerseComponentsModels().size() - 1).getVerseNumber();
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

        mAdapter = new NumberAdapter(this, null, null, mVerseComponentsModels, mChapterNumber, mBookId, mOpenBook);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
