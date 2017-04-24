package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class BookItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private View mView;
    private Context mContext;
    private ArrayList<BookIdModel> mBookModels;
    private TextView mTvHeader;

    public BookItemViewHolder(View itemView, Context context, ArrayList<BookIdModel> bookModels) {
        super(itemView);
        mView = itemView;
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
        mTvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        mContext = context;
        mBookModels = bookModels;
    }

    public void onBind(final int position) {
        BookIdModel bookModel = mBookModels.get(position);
        mTvBookName.setText(bookModel.getBookName());
        mView.setTag(position);
        mView.setOnClickListener(this);
        if (bookModel.getBookNumber() > 40) {
            mTvHeader.setText("New Testament");
        } else {
            mTvHeader.setText("Old Testament");
        }
//        if (position == 0 || position == 39) {
//            mTvHeader.setVisibility(View.VISIBLE);
//        } else {
            mTvHeader.setVisibility(View.GONE);
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_item: {
                int position = (int) v.getTag();
                Intent intent = new Intent(mContext, SelectChapterAndVerseActivity.class);
                intent.putExtra(Constants.Keys.SELECT_VERSE_FOR_NOTE, false);
                intent.putExtra(Constants.Keys.OPEN_BOOK, true);
                intent.putExtra(Constants.Keys.BOOK_ID, mBookModels.get(position).getBookId());
                mContext.startActivity(intent);
                break;
            }
        }
    }
}
