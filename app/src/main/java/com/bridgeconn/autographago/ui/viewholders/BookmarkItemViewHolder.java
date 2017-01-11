package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookmarkModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

public class BookmarkItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private Context mContext;
    private ArrayList<BookmarkModel> mBookmarkModels;

    public BookmarkItemViewHolder(View itemView, Context context, ArrayList<BookmarkModel> bookmarkModels) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);

        mContext = context;
        mBookmarkModels = bookmarkModels;
    }

    public void onBind(final int position) {
        BookmarkModel model = mBookmarkModels.get(position);
        String chapterId = model.getChapterId();
        String [] splitString = chapterId.split("_");
        mTvBookName.setText(UtilFunctions.getBookNameFromMapping(mContext, splitString[0]) + "    " + splitString[1]);
        mTvBookName.setCompoundDrawables(null, null, null, null);
        mTvBookName.setAllCaps(false);
        mTvBookName.setOnClickListener(this);
        mTvBookName.setTag(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                BookmarkModel model = mBookmarkModels.get(position);
                String chapterId = model.getChapterId();
                String [] splitString = chapterId.split("_");

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, splitString[0]);
                intent.putExtra(Constants.Keys.CHAPTER_NO, Integer.parseInt(splitString[1]));
                intent.putExtra(Constants.Keys.VERSE_NO, 1);
                mContext.startActivity(intent);
                break;
            }
        }
    }
}
