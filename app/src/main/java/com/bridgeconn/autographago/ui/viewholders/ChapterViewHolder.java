package com.bridgeconn.autographago.ui.viewholders;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ui.adapters.VerseAdapter;

import java.util.ArrayList;

public class ChapterViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView mRecyclerView;
    private Activity mContext;
    private ArrayList<ChapterModel> mChapterModels;
    private String mVerseNumber;
    private int mChapterPosition;

    public ChapterViewHolder(View itemView, Activity context, ArrayList<ChapterModel> chapterModels, String verseNumber, int chapterPosition) {
        super(itemView);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.list_chapters);
        mContext = context;
        mChapterModels = chapterModels;
        mVerseNumber = verseNumber;
        mChapterPosition = chapterPosition;
    }

    public void onBind(int position) {
        VerseAdapter adapter = new VerseAdapter(mContext, mChapterModels.get(position));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        // TODO fix this, this isn't working
        // TODO if this fixes, then also check for versenumber here
//        if (mChapterPosition == position) {
//            if (mVerseNumber > 1) {
//                mRecyclerView.smoothScrollToPosition(mVerseNumber - 1);
//      //          mRecyclerView.scrollToPosition(mVerseNumber - 1);
//            }
//        }
    }
}