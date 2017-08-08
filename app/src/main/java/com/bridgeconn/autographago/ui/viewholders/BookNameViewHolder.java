package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.services.BackgroundService;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.fragments.BookFragment;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class BookNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<BookIdModel> mBookModelArrayList;
    private boolean mOpenBook;
    private boolean mSelectVerse;

    public BookNameViewHolder(View itemView, Fragment fragment, ArrayList<BookIdModel> bookModelArrayList, boolean openBook, boolean selectVerse) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);

        mFragment = fragment;
        mBookModelArrayList = bookModelArrayList;
        mOpenBook = openBook;
        mSelectVerse = selectVerse;
    }

    public void onBind(final int position) {
        BookIdModel bookModel = mBookModelArrayList.get(position);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (bookModel.isSelected()) {
//                mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
                mTvChapterNumber.setBackgroundColor(Color.LTGRAY);
            } else {
                mTvChapterNumber.setBackgroundColor(Color.WHITE);
            }
//        }
        mTvChapterNumber.setText(bookModel.getBookName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
        mTvChapterNumber.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {
                    ((BookFragment) mFragment).setSelected(position);
                    ((SelectChapterAndVerseActivity) mFragment.getActivity()).openChapterPage(position, mBookModelArrayList.get(position).getBookId());
                }
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {

                    String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
                    String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.UDB);
                    String bookId = mBookModelArrayList.get(position).getBookId();

                    Realm realm = Realm.getDefaultInstance();
                    String verseNumber = getVerseNumber(realm, languageCode, versionCode, bookId, 1);
                    realm.close();

                    Intent task = new Intent(mFragment.getContext(), BackgroundService.class);
                    task.setAction(Constants.ACTION.ADD_TO_HISTORY);
                    task.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                    task.putExtra(Constants.Keys.CHAPTER_NO, 1);
                    task.putExtra(Constants.Keys.LANGUAGE_CODE, languageCode);
                    task.putExtra(Constants.Keys.VERSION_CODE, versionCode);
                    task.putExtra(Constants.Keys.BOOK_ID, bookId);
                    task.putExtra(Constants.Keys.TIMESTAMP, System.currentTimeMillis());

                    if (mSelectVerse) {
                        if (mOpenBook) {
                            mFragment.getContext().startService(task);
                        }
                        VerseIdModel model = new VerseIdModel();
                        model.setVerseNumber(verseNumber);
                        model.setChapterNumber(1);
                        model.setBookId(bookId);
                        model.setBookName(UtilFunctions.getBookNameFromMapping(mFragment.getContext(), bookId));
                        Intent output = new Intent();
                        output.putExtra(Constants.Keys.VERSE_NOTE_MODEL, model);
                        mFragment.getActivity().setResult(RESULT_OK, output);
                        mFragment.getActivity().finish();
                    } else {
                        mFragment.getContext().startService(task);

                        Intent intent = new Intent(mFragment.getContext(), BookActivity.class);
                        intent.putExtra(Constants.Keys.BOOK_ID, bookId);
                        intent.putExtra(Constants.Keys.CHAPTER_NO, 1);
                        intent.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                        mFragment.startActivity(intent);
                        mFragment.getActivity().finish();
                    }
                }
                return true;
            }
        }
        return false;
    }

    private String getVerseNumber(Realm realm, String languageCode, String versionCOde, String bookId, int chapterNum) {
        RealmResults<BookModel> realmResults = new AllSpecifications.BookModelById(languageCode, versionCOde, bookId).generateResults(realm);
        if (realmResults.size() > 0) {
            for (ChapterModel chapterModel : realmResults.get(0).getChapterModels()) {
                if (chapterModel.getChapterNumber() == chapterNum) {
                    if (chapterModel.getVerseComponentsModels().size() > 0) {
                        String verseNumber = chapterModel.getVerseComponentsModels().get(0).getVerseNumber();
                        return verseNumber;
                    }
                }
            }
        }
        return "1";
    }
}