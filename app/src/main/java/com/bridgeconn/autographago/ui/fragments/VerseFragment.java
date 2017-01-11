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
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.adapters.NumberAdapter;
import com.bridgeconn.autographago.utils.Constants;

public class VerseFragment extends Fragment implements View.OnClickListener, SelectChapterAndVerseActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private int mNumberOfBlocks;
    private BookModel mBookModel;
    private ChapterModel mChapterModel;
    private int mChapterNumber;
    private String mBookId;

    @Override
    public void onItemClick(int number) {
        mChapterNumber = number;

        mNumberOfBlocks = getNumberOfVerses(mBookModel);

        mAdapter.changeVerseCount(mNumberOfBlocks);
        mAdapter.changeChapterNumber(mChapterNumber);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookId = getArguments().getString(Constants.Keys.BOOK_ID);
        mChapterNumber = getArguments().getInt(Constants.Keys.CHAPTER_NO);

        mBookModel = getBookModel(mBookId);

        mNumberOfBlocks = getNumberOfVerses(mBookModel);
    }

    private BookModel getBookModel(String bookId) {
        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            if (bookModel.getBookId().equals(bookId)) {
                return bookModel;
            }
        }
        return null;
    }

    private int getNumberOfVerses(BookModel bookModel) {
        ChapterModel chapterModel = bookModel.getChapterModels().get(mChapterNumber - 1);
        return chapterModel.getVerseComponentsModels().get(chapterModel.getVerseComponentsModels().size() - 1).getVerseNumber();
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

        mAdapter = new NumberAdapter(this, null, mNumberOfBlocks, mChapterNumber, mBookId);
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
    }

}
