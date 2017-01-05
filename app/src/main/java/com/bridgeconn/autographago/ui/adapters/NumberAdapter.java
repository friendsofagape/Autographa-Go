package com.bridgeconn.autographago.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ui.viewholders.ChapterNumberViewHolder;
import com.bridgeconn.autographago.ui.viewholders.VerseNumberViewHolder;

import java.util.ArrayList;

public class NumberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Fragment mFragment;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ChapterModel> mChapterModels;
    private int mVerseCount;
    private int mChapterNumber;
    private String mBookId;

    private interface ViewTypes {
        int CHAPTER = 0;
        int VERSE = 1;
    }

    public NumberAdapter(Fragment fragment, ArrayList<ChapterModel> chapterModels, int verseCount, int chapterNumber, String bookId) {
        mFragment = fragment;
        mLayoutInflater = LayoutInflater.from(mFragment.getContext());
        mChapterModels = chapterModels;
        mVerseCount = verseCount;
        mChapterNumber = chapterNumber;
        mBookId = bookId;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChapterModels != null) {
            return ViewTypes.CHAPTER;
        }
        return ViewTypes.VERSE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ViewTypes.CHAPTER) {
            viewHolder = new ChapterNumberViewHolder(mLayoutInflater.inflate(
                    R.layout.item_grid_chapter_verse, parent, false), mFragment, mChapterModels);
        } else if (viewType == ViewTypes.VERSE) {
            viewHolder = new VerseNumberViewHolder(mLayoutInflater.inflate(
                    R.layout.item_grid_chapter_verse, parent, false), mFragment, mVerseCount, mChapterNumber, mBookId, null);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterNumberViewHolder) {
            ((ChapterNumberViewHolder) holder).onBind(position);
        } else if (holder instanceof VerseNumberViewHolder) {
            ((VerseNumberViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mChapterModels == null) {
            return mVerseCount;
        }
        return mChapterModels.size();
    }

    public void changeVerseCount(int count) {
        mVerseCount = count;
    }

    public void changeChapterNumber(int number) {
        mChapterNumber = number;
    }

}
