package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class VerseNumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvVerseNumber;
    private Fragment mFragment;
    private ArrayList<VerseComponentsModel> mVerseComponentsModels;
    private int mChapterNumber;
    private String mBookId;
    private boolean mOpenBook;

    public VerseNumberViewHolder(View itemView, Fragment fragment, ArrayList<VerseComponentsModel> verseComponentsModels, int chapterNumber, String bookId, boolean openBook) {
        super(itemView);
        mTvVerseNumber = (TextView) itemView.findViewById(R.id.tv_number);

        mFragment = fragment;
        mChapterNumber = chapterNumber;
        mBookId = bookId;
        mVerseComponentsModels = verseComponentsModels;
        mOpenBook = openBook;
    }

    public void onBind(int position) {
        mTvVerseNumber.setText(mVerseComponentsModels.get(position).getVerseNumber() + "");
        mTvVerseNumber.setTag(mVerseComponentsModels.get(position).getVerseNumber());
        mTvVerseNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_number: {
                int verseNumber = (int) v.getTag();
                if (mOpenBook) {
                    Intent intent = new Intent(mFragment.getContext(), BookActivity.class);
                    intent.putExtra(Constants.Keys.BOOK_ID, mBookId);
                    intent.putExtra(Constants.Keys.CHAPTER_NO, ((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter());
                    intent.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                    mFragment.startActivity(intent);
                } else {
                    Intent output = new Intent();
                    output.putExtra(Constants.Keys.BOOK_ID, mBookId);
                    output.putExtra(Constants.Keys.CHAPTER_NO, ((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter()+1);
                    output.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                    mFragment.getActivity().setResult(RESULT_OK, output);
                    mFragment.getActivity().finish();
                }
                break;
            }
        }
    }
}