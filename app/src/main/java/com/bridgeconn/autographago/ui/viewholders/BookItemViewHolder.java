package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class BookItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private Context mContext;
    private ArrayList<BookModel> mBookModels;

    public BookItemViewHolder(View itemView, Context context, ArrayList<BookModel> bookModels) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);

        mContext = context;
        mBookModels = bookModels;
    }

    public void onBind(final int position) {
        BookModel bookModel = mBookModels.get(position);
        mTvBookName.setText(bookModel.getBookName());
        mTvBookName.setOnClickListener(this);
        mTvBookName.setTag(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                Intent intent = new Intent(mContext, SelectChapterAndVerseActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, mBookModels.get(position).getBookId());
                mContext.startActivity(intent);
                break;
            }
        }
    }
}
