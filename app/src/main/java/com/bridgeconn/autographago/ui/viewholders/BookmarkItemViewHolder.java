package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.MenuActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class BookmarkItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private ImageView mIvDelete;
    private Context mContext;
    private ArrayList<BookIdModel> mBookmarkModels;

    public BookmarkItemViewHolder(View itemView, Context context, ArrayList<BookIdModel> bookmarkModels) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
        mIvDelete = (ImageView) itemView.findViewById(R.id.delete);

        mContext = context;
        mBookmarkModels = bookmarkModels;
    }

    public void onBind(final int position) {
        BookIdModel model = mBookmarkModels.get(position);
        mTvBookName.setText(model.getBookName() + "    " + model.getBookmarkChapterNumber());
        mTvBookName.setCompoundDrawables(null, null, null, null);
        mTvBookName.setAllCaps(false);
        mTvBookName.setOnClickListener(this);
        mTvBookName.setTag(position);

        mIvDelete.setOnClickListener(this);
        mIvDelete.setTag(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                BookIdModel model = mBookmarkModels.get(position);

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, model.getBookId());
                intent.putExtra(Constants.Keys.CHAPTER_NO, model.getBookmarkChapterNumber());
                intent.putExtra(Constants.Keys.VERSE_NO, "1");
                mContext.startActivity(intent);
                break;
            }
            case R.id.delete: {
                int position = (int) v.getTag();
                ((MenuActivity) mContext).refreshBookMarkList(position);
                break;
            }
        }
    }
}
