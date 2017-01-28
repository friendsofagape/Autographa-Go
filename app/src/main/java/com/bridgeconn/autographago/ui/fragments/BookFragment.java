package com.bridgeconn.autographago.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.adapters.NumberAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class BookFragment extends Fragment implements SelectChapterAndVerseActivity.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private NumberAdapter mAdapter;
    private int mNumberOfBlocks;
    private BookModel mBookModel;
    private String mBookId;
    private ArrayList<BookModel> mBookModelArrayList = new ArrayList<>();
    private boolean mOpenBook;

    @Override
    public void onItemClick(int number, String bookId) {
        mBookId = bookId;
        mAdapter.changeBookId(mBookId);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOpenBook = getArguments().getBoolean(Constants.Keys.OPEN_BOOK);

        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            mBookModelArrayList.add(bookModel);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new NumberAdapter(this, null, mBookModelArrayList, null, 0, mBookId, mOpenBook);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setSelected(-1);
    }

    public void setSelected(int position) {
        for (int i = 0; i< mBookModelArrayList.size(); i++) {
            mBookModelArrayList.get(i).setSelected(false);
        }
        if (position >= 0) {
            mBookModelArrayList.get(position).setSelected(true);
        }
        mAdapter.notifyDataSetChanged();
    }

}