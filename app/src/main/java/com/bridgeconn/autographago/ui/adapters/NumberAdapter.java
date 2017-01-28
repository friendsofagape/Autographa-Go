package com.bridgeconn.autographago.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.viewholders.BookNameViewHolder;
import com.bridgeconn.autographago.ui.viewholders.ChapterNumberViewHolder;
import com.bridgeconn.autographago.ui.viewholders.VerseNumberViewHolder;

import java.util.ArrayList;

public class NumberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Fragment mFragment;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ChapterModel> mChapterModels;
    private ArrayList<BookModel> mBookModelArrayList;
    private ArrayList<VerseComponentsModel> mVerseComponentsModels;
    private int mChapterNumber;
    private String mBookId;
    private boolean mOpenBook;

    private interface ViewTypes {
        int BOOK = 0;
        int CHAPTER = 1;
        int VERSE = 2;
    }

    public NumberAdapter(Fragment fragment, ArrayList<ChapterModel> chapterModels, ArrayList<BookModel> bookModelArrayList, ArrayList<VerseComponentsModel> verseComponentsModels, int chapterNumber, String bookId, boolean openBook) {
        mFragment = fragment;
        mLayoutInflater = LayoutInflater.from(mFragment.getContext());
        mChapterModels = chapterModels;
        mBookModelArrayList = bookModelArrayList;
        mVerseComponentsModels = verseComponentsModels;
        mChapterNumber = chapterNumber;
        mBookId = bookId;
        mOpenBook = openBook;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChapterModels != null) {
            return ViewTypes.CHAPTER;
        }
        if (mBookModelArrayList != null) {
            return ViewTypes.BOOK;
        }
        return ViewTypes.VERSE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ViewTypes.BOOK) {
            viewHolder = new BookNameViewHolder(mLayoutInflater.inflate(
                    R.layout.item_book_fragment, parent, false), mFragment, mBookModelArrayList);
        } else if (viewType == ViewTypes.CHAPTER) {
            viewHolder = new ChapterNumberViewHolder(mLayoutInflater.inflate(
                    R.layout.item_grid_chapter_verse, parent, false), mFragment, mChapterModels, mBookId);
        } else if (viewType == ViewTypes.VERSE) {
            viewHolder = new VerseNumberViewHolder(mLayoutInflater.inflate(
                    R.layout.item_grid_chapter_verse, parent, false), mFragment, mVerseComponentsModels, mChapterNumber, mBookId, mOpenBook);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookNameViewHolder) {
            ((BookNameViewHolder) holder).onBind(position);
        } else if (holder instanceof ChapterNumberViewHolder) {
            ((ChapterNumberViewHolder) holder).onBind(position);
        } else if (holder instanceof VerseNumberViewHolder) {
            ((VerseNumberViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mChapterModels != null) {
            return mChapterModels.size();
        }
        if (mBookModelArrayList != null) {
            return mBookModelArrayList.size();
        }
        return mVerseComponentsModels.size();
    }

    public void changeVerseCount(int count) {
//        mVerseCount = count;
    }

    public void changeChapterNumber(int number) {
        mChapterNumber = number;
    }

    public void changeBookId(String bookId) {
        mBookId = bookId;
        ((SelectChapterAndVerseActivity) mFragment.getActivity()).reattachFragment();
    }

}