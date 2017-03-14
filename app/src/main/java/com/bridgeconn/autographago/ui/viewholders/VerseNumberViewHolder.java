package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
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
                if (mSelectVerse) {
                    if (mOpenBook) {
                        SearchModel model = new SearchModel();
                        model.setVerseNumber(verseNumber);
                        model.setChapterNumber(((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter());
                        model.setLanguageCode(mVerseComponentsModels.get(position).getLanguageCode());
                        model.setVersionCode(mVerseComponentsModels.get(position).getVersionCode());
                        model.setBookId(mBookId);
                        model.setBookName(UtilFunctions.getBookNameFromMapping(mFragment.getContext(), mBookId));
                        model.setSection(UtilFunctions.getBookSectionFromMapping(mFragment.getContext(), mBookId));
                        model.setTimeStamp(System.currentTimeMillis());
                        model.setSearchId(mBookId + "_" + model.getChapterNumber() + "_" + verseNumber);
                        new AutographaRepository<SearchModel>().add(model);
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
                    SearchModel model = new SearchModel();
                    model.setVerseNumber(verseNumber);
                    model.setChapterNumber(((SelectChapterAndVerseActivity) mFragment.getActivity()).getSelectedChapter());
                    model.setLanguageCode(mVerseComponentsModels.get(position).getLanguageCode());
                    model.setVersionCode(mVerseComponentsModels.get(position).getVersionCode());
                    model.setBookId(mBookId);
                    model.setBookName(UtilFunctions.getBookNameFromMapping(mFragment.getContext(), mBookId));
                    model.setSection(UtilFunctions.getBookSectionFromMapping(mFragment.getContext(), mBookId));
                    model.setTimeStamp(System.currentTimeMillis());
                    model.setSearchId(mBookId + "_" + model.getChapterNumber() + "_" + verseNumber);
                    new AutographaRepository<SearchModel>().add(model);

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