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
import com.bridgeconn.autographago.ui.adapters.NumberAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class ChapterFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private int mNumberOfBlocks;
    private BookModel mBookModel;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();
    private String mBookId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookId = getArguments().getString(Constants.Keys.BOOK_ID);

        mBookModel = getBookModel(mBookId);
        if (mBookModel != null) {
//            mNumberOfBlocks = mBookModel.getChapterModels().size();
            for (ChapterModel chapterModel : mBookModel.getChapterModels()) {
                mChapterModels.add(chapterModel);
            }
        }
    }

    private BookModel getBookModel(String bookId) {
        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            if (bookModel.getBookId().equals(bookId)) {
                return bookModel;
            }
        }
        return null;
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

        mAdapter = new NumberAdapter(this, mChapterModels, 0, 0, mBookId);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setSelected(-1);
    }

    public void setSelected(int position) {
        for (int i = 0; i< mChapterModels.size(); i++) {
            mChapterModels.get(i).setSelected(false);
        }
        if (position >= 0) {
            mChapterModels.get(position).setSelected(true);
        }
        mAdapter.notifyDataSetChanged();
    }

}