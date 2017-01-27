package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class SearchItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvTitle;
    private TextView mTvText;
    private View mView;
    private Context mContext;
    private ArrayList<SearchModel> mSearchModels;

    public SearchItemViewHolder(View itemView, Context context, ArrayList<SearchModel> searchModels) {
        super(itemView);
        mView = itemView;
        mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mTvText = (TextView) itemView.findViewById(R.id.tv_text);

        mContext = context;
        mSearchModels = searchModels;

        mView.setOnClickListener(this);
    }

    public void onBind(final int position) {
        SearchModel searchModel = mSearchModels.get(position);
        mTvTitle.setText(searchModel.getBookName() + " " + searchModel.getChapterNumber() + Constants.Styling.CHAR_COLON + searchModel.getVerseNumber());
        mTvText.setText("");
        addAllContent(searchModel.getText());
        mView.setTag(position);
    }

    private void addAllContent(String textString) {
        if (textString != null) {
            if (!textString.trim().equals("")) {
                String[] splitString = textString.split(Constants.Styling.SPLIT_SPACE);
                for (int n = 0; n < splitString.length; n++) {
                    switch (splitString[n]) {
                        case Constants.Markers.MARKER_NEW_PARAGRAPH: {
//                                textViewVerse.append("\n");
                            break;
                        }
                        case Constants.Styling.MARKER_Q: {
                            mTvText.append(Constants.Styling.NEW_LINE_WITH_TAB_SPACE);
                            break;
                        }
                        default: {
                            if (splitString[n].startsWith(Constants.Styling.MARKER_Q)) {
                                String str = splitString[n];
                                int number = Integer.parseInt(str.replaceAll(Constants.Styling.REGEX_NUMBERS, ""));
                                mTvText.append(Constants.Styling.NEW_LINE);
                                for (int o = 0; o < number; o++) {
                                    mTvText.append(Constants.Styling.TAB_SPACE);
                                }
                            } else {
                                mTvText.append(splitString[n] + " ");
                            }
                            break;
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view: {
                int position = (int) v.getTag();
                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, mSearchModels.get(position).getBookId());
                intent.putExtra(Constants.Keys.CHAPTER_NO, mSearchModels.get(position).getChapterNumber());
                intent.putExtra(Constants.Keys.VERSE_NO, mSearchModels.get(position).getVerseNumber());
                mContext.startActivity(intent);
                break;
            }
        }
    }
}