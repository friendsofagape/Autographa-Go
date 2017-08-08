package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.services.BackgroundService;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.fragments.ChapterFragment;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class ChapterNumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<ChapterModel> mChapterModels;
    private String mBookId;
    private boolean mOpenBook;
    private boolean mSelectVerse;

    public ChapterNumberViewHolder(View itemView, Fragment fragment, ArrayList<ChapterModel> chapterModels, String bookId, boolean openBook, boolean selectVerse) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_number);

        mFragment = fragment;
        mChapterModels = chapterModels;
        mBookId = bookId;
        mOpenBook = openBook;
        mSelectVerse = selectVerse;
    }

    public void onBind(final int position) {
        ChapterModel chapterModel = mChapterModels.get(position);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (chapterModel.isSelected()) {
//                mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
                mTvChapterNumber.setBackgroundColor(Color.LTGRAY);
            } else {
                mTvChapterNumber.setBackgroundColor(Color.WHITE);
//                mTvChapterNumber.setBackground(mFragment.getResources().getDrawable(R.drawable.border_right_bottom, null));
            }
//        }
        mTvChapterNumber.setText(chapterModel.getChapterNumber() + "");
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
        mTvChapterNumber.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_number: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {
                    ((ChapterFragment) mFragment).setSelected(position);
                    String bookId = mChapterModels.get(position).getChapterId().split("_")[2];
                    ((SelectChapterAndVerseActivity) mFragment.getActivity()).openVersePage(position+1,
                            bookId);
                }
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tv_number: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {

                    String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
                    String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.UDB);
                    String bookId = mChapterModels.get(position).getChapterId().split("_")[2];

                    Realm realm = Realm.getDefaultInstance();
                    String verseNumber = getVerseNumber(realm, languageCode, versionCode, bookId, position+1);
                    realm.close();

                    Intent task = new Intent(mFragment.getContext(), BackgroundService.class);
                    task.setAction(Constants.ACTION.ADD_TO_HISTORY);
                    task.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                    task.putExtra(Constants.Keys.CHAPTER_NO, position+1);
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
                        model.setChapterNumber(position+1);
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
                        intent.putExtra(Constants.Keys.CHAPTER_NO, position+1);
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