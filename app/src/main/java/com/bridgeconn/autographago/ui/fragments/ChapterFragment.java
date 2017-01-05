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
    private NumberAdapter numberAdapter;
    private int numberOfBlocks;
    private BookModel bookModel;
    private ArrayList<ChapterModel> chapterModels = new ArrayList<>();
    private String bookId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookId = getArguments().getString(Constants.Keys.BOOK_ID);

        bookModel = getBookModel(bookId);
        if (bookModel != null) {
//            numberOfBlocks = bookModel.getChapterModels().size();
            for (ChapterModel chapterModel : bookModel.getChapterModels()) {
                chapterModels.add(chapterModel);
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

        numberAdapter = new NumberAdapter(this, chapterModels, 0, 0, bookId);
        mRecyclerView.setAdapter(numberAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setSelected(-1);
    }

    public void setSelected(int position) {
        for (int i=0; i<chapterModels.size(); i++) {
            chapterModels.get(i).setSelected(false);
        }
        if (position >= 0) {
            chapterModels.get(position).setSelected(true);
        }
        numberAdapter.notifyDataSetChanged();
    }

}