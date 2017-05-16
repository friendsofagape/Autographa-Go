package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class HistoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View mView;
    private TextView mTvBookName, mTvTime;
    private Context mContext;
    private ArrayList<SearchModel> mHistoryModels;

    public HistoryItemViewHolder(View itemView, Context context, ArrayList<SearchModel> historyModels) {
        super(itemView);
        mView = itemView;
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_text);
        mTvTime = (TextView) itemView.findViewById(R.id.time);

        mContext = context;
        mHistoryModels = historyModels;
    }

    public void onBind(final int position) {
        SearchModel searchModel = mHistoryModels.get(position);
        mTvBookName.setText(searchModel.getBookName() + "  " +
                searchModel.getChapterNumber() + Constants.Styling.CHAR_COLON + searchModel.getVerseNumber());
        mTvTime.setText(DateUtils.getRelativeTimeSpanString(searchModel.getTimeStamp(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
        mView.setTag(position);
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_history_view: {
                int position = (int) v.getTag();

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, mHistoryModels.get(position).getBookId());
                intent.putExtra(Constants.Keys.CHAPTER_NO, mHistoryModels.get(position).getChapterNumber());
                intent.putExtra(Constants.Keys.VERSE_NO, mHistoryModels.get(position).getVerseNumber());
                mContext.startActivity(intent);

                break;
            }
        }
    }
}
