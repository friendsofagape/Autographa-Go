package com.bridgeconn.autographago.ui.viewholders;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.fragments.BookFragment;

import java.util.ArrayList;

public class BookNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<BookIdModel> mBookModelArrayList;

    public BookNameViewHolder(View itemView, Fragment fragment, ArrayList<BookIdModel> bookModelArrayList) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);

        mFragment = fragment;
        mBookModelArrayList = bookModelArrayList;
    }

    public void onBind(final int position) {
        BookIdModel bookModel = mBookModelArrayList.get(position);
        if (bookModel.isSelected()) {
            mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40));
        } else {
            mTvChapterNumber.setBackgroundColor(0);
        }
        mTvChapterNumber.setText(bookModel.getBookName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {
                    ((BookFragment) mFragment).setSelected(position);
                    ((SelectChapterAndVerseActivity) mFragment.getActivity()).openChapterPage(position, mBookModelArrayList.get(position).getBookId());
                }
                break;
            }
        }
    }
}