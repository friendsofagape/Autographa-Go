package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.services.BackgroundService;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class VerseNumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvVerseNumber;
    private Fragment mFragment;
    private ArrayList<VerseComponentsModel> mVerseComponentsModels;
    private int mChapterNumber;
    private String mBookId;
    private boolean mOpenBook;
    private boolean mSelectVerse;

    public VerseNumberViewHolder(View itemView, Fragment fragment, ArrayList<VerseComponentsModel> verseComponentsModels, int chapterNumber, String bookId, boolean openBook, boolean selectVerse) {
        super(itemView);
        mTvVerseNumber = (TextView) itemView.findViewById(R.id.tv_number);

        mFragment = fragment;
        mChapterNumber = chapterNumber;
        mBookId = bookId;
        mVerseComponentsModels = verseComponentsModels;
        mOpenBook = openBook;
        mSelectVerse = selectVerse;
    }

    public void onBind(int position) {
        mTvVerseNumber.setText(mVerseComponentsModels.get(position).getVerseNumber() + "");
        mTvVerseNumber.setTag(position);
        mTvVerseNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_number: {
                int position = (int) v.getTag();
                String verseNumber = mVerseComponentsModels.get(position).getVerseNumber();

                Intent task = new Intent(mFragment.getContext(), BackgroundService.class);
                task.setAction(Constants.ACTION.ADD_TO_HISTORY);
                task.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                String chapterId = mVerseComponentsModels.get(position).getChapterId();
                task.putExtra(Constants.Keys.CHAPTER_NO, Integer.parseInt(chapterId.split("_")[1]));
                task.putExtra(Constants.Keys.LANGUAGE_CODE, mVerseComponentsModels.get(position).getLanguageCode());
                task.putExtra(Constants.Keys.VERSION_CODE, mVerseComponentsModels.get(position).getVersionCode());
                task.putExtra(Constants.Keys.BOOK_ID, mBookId);
                task.putExtra(Constants.Keys.TIMESTAMP, System.currentTimeMillis());

                if (mSelectVerse) {
                    if (mOpenBook) {
                        mFragment.getContext().startService(task);
                    }
                    VerseIdModel model = new VerseIdModel();
                    model.setVerseNumber(verseNumber);
                    model.setChapterNumber(((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter());
                    model.setBookId(((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedBook());
                    model.setBookName(UtilFunctions.getBookNameFromMapping(mFragment.getContext(), ((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedBook()));

                    Intent output = new Intent();
                    output.putExtra(Constants.Keys.VERSE_NOTE_MODEL, model);

                    mFragment.getActivity().setResult(RESULT_OK, output);
                    mFragment.getActivity().finish();
                } else {
                    mFragment.getContext().startService(task);

                    Intent intent = new Intent(mFragment.getContext(), BookActivity.class);
                    intent.putExtra(Constants.Keys.BOOK_ID, mBookId);
                    intent.putExtra(Constants.Keys.CHAPTER_NO, ((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter());
                    intent.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                    mFragment.startActivity(intent);

                    mFragment.getActivity().finish();
                }
                break;
            }
        }
    }
}