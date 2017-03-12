package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.HighlightActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class HighlightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private ImageView mIvDelete;
    private Context mContext;
    private ArrayList<VerseIdModel> mHighlightModels;

    public HighlightViewHolder(View itemView, Context context, ArrayList<VerseIdModel> highlightModels) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
        mIvDelete = (ImageView) itemView.findViewById(R.id.delete);
        mContext = context;
        mHighlightModels = highlightModels;
    }

    public void onBind(int position) {
        VerseIdModel model = mHighlightModels.get(position);
        mTvBookName.setText(model.getBookName() + " " +
                model.getChapterNumber() + Constants.Styling.CHAR_COLON +
                model.getVerseNumber());
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

                VerseIdModel model = mHighlightModels.get(position);

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, model.getBookId());
                intent.putExtra(Constants.Keys.CHAPTER_NO, model.getChapterNumber());
                intent.putExtra(Constants.Keys.VERSE_NO, model.getVerseNumber());
                mContext.startActivity(intent);
                break;
            }
            case R.id.delete: {
                int position = (int) v.getTag();
                ((HighlightActivity) mContext).refreshHighlightList(position);
                break;
            }
        }
    }
}