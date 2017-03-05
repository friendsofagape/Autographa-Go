package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.MenuActivity;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

public class HighlightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private ImageView mIvDelete;
    private Context mContext;
    private ArrayList<VerseComponentsModel> mHighlightModels;

    public HighlightViewHolder(View itemView, Context context, ArrayList<VerseComponentsModel> highlightModels) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
        mIvDelete = (ImageView) itemView.findViewById(R.id.delete);
        mContext = context;
        mHighlightModels = highlightModels;
    }

    public void onBind(int position) {
        VerseComponentsModel model = mHighlightModels.get(position);
        String [] splitString = model.getChapterId().split("_");
        if (splitString.length < 2) {
            return;
        }
        mTvBookName.setText(UtilFunctions.getBookNameFromMapping(mContext, splitString[0]) + " " +
                splitString[1] + Constants.Styling.CHAR_COLON +
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

                VerseComponentsModel model = mHighlightModels.get(position);
                String [] splitString = model.getChapterId().split("_");

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, splitString[0]);
                intent.putExtra(Constants.Keys.CHAPTER_NO, Integer.parseInt(splitString[1]));
                intent.putExtra(Constants.Keys.VERSE_NO, model.getVerseNumber());
                mContext.startActivity(intent);
                break;
            }
            case R.id.delete: {
                int position = (int) v.getTag();
                ((MenuActivity) mContext).refreshHighlightList(position);
                break;
            }
        }
    }
}