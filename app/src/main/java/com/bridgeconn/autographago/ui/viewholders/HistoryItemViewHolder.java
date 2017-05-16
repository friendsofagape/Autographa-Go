package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.utils.Constants;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class HistoryItemViewHolder extends ChildViewHolder {

    private View mView;
    private TextView mTvBookName;//, mTvTime;
    private Context mContext;
//    private ArrayList<SearchModel> mHistoryModels;

    public HistoryItemViewHolder(View itemView, Context context) {//}, ArrayList<SearchModel> historyModels) {
        super(itemView);
        mView = itemView;
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_text);
        mContext = context;
//        mHistoryModels = historyModels;
    }

    public void onBind(final SearchModel searchModel, final int position) {
//        SearchModel searchModel = mHistoryModels.get(position);
        mTvBookName.setText(searchModel.getBookName() + "  " +
                searchModel.getChapterNumber() + Constants.Styling.CHAR_COLON + searchModel.getVerseNumber());
//        mView.setTag(position);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, searchModel.getBookId());
                intent.putExtra(Constants.Keys.CHAPTER_NO, searchModel.getChapterNumber());
                intent.putExtra(Constants.Keys.VERSE_NO, searchModel.getVerseNumber());
                mContext.startActivity(intent);
            }
        });
    }
}
