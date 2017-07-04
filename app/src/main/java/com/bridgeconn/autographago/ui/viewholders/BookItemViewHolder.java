package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
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
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class BookItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private TextView mTvBookName;
    private View mView;
    private Context mContext;
    private ArrayList<BookIdModel> mBookModels;
    private TextView mTvHeader;

    public BookItemViewHolder(View itemView, Context context, ArrayList<BookIdModel> bookModels) {
        super(itemView);
        mView = itemView;
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
        mTvHeader = (TextView) itemView.findViewById(R.id.tv_header);
        mContext = context;
        mBookModels = bookModels;
    }

    public void onBind(final int position) {
        BookIdModel bookModel = mBookModels.get(position);
        mTvBookName.setText(bookModel.getBookName());
        mView.setTag(position);
        mView.setOnClickListener(this);
        mView.setOnLongClickListener(this);
//        if (position == 0 || position == 39) {
//            mTvHeader.setVisibility(View.VISIBLE);
//        } else {
            mTvHeader.setVisibility(View.GONE);
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_item: {
                int position = (int) v.getTag();
                Intent intent = new Intent(mContext, SelectChapterAndVerseActivity.class);
                intent.putExtra(Constants.Keys.SELECT_VERSE_FOR_NOTE, false);
                intent.putExtra(Constants.Keys.OPEN_BOOK, true);
                intent.putExtra(Constants.Keys.BOOK_ID, mBookModels.get(position).getBookId());
                mContext.startActivity(intent);
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.root_item: {
                int position = (int) v.getTag();

                String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
                String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.UDB);
                String bookId = mBookModels.get(position).getBookId();

                Realm realm = Realm.getDefaultInstance();
                String verseNumber = getVerseNumber(realm, languageCode, versionCode, bookId, 1);
                realm.close();

                Intent task = new Intent(mContext, BackgroundService.class);
                task.setAction(Constants.ACTION.ADD_TO_HISTORY);
                task.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                task.putExtra(Constants.Keys.CHAPTER_NO, 1);
                task.putExtra(Constants.Keys.LANGUAGE_CODE, languageCode);
                task.putExtra(Constants.Keys.VERSION_CODE, versionCode);
                task.putExtra(Constants.Keys.BOOK_ID, bookId);
                task.putExtra(Constants.Keys.TIMESTAMP, System.currentTimeMillis());
                mContext.startService(task);

                Intent intent = new Intent(mContext, BookActivity.class);
                intent.putExtra(Constants.Keys.BOOK_ID, bookId);
                intent.putExtra(Constants.Keys.CHAPTER_NO, 1);
                intent.putExtra(Constants.Keys.VERSE_NO, verseNumber);
                mContext.startActivity(intent);

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
