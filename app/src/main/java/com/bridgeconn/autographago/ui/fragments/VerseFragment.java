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
    private NumberAdapter numberAdapter;
    private int numberOfBlocks;
    private BookModel bookModel;
    private ChapterModel chapterModel;
    private int chapterNumber;
    private String bookId;

    @Override
    public void onItemClick(int number) {
        chapterNumber = number;

        numberOfBlocks = getNumberOfVerses(bookModel);

        numberAdapter.changeVerseCount(numberOfBlocks);
        numberAdapter.changeChapterNumber(chapterNumber);
        numberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookId = getArguments().getString(Constants.Keys.BOOK_ID);
        chapterNumber = getArguments().getInt(Constants.Keys.CHAPTER_NO);

        bookModel = getBookModel(bookId);

        numberOfBlocks = getNumberOfVerses(bookModel);
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
        ChapterModel chapterModel = bookModel.getChapterModels().get(chapterNumber - 1);
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

        numberAdapter = new NumberAdapter(this, null, numberOfBlocks, chapterNumber, bookId);
        mRecyclerView.setAdapter(numberAdapter);

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
