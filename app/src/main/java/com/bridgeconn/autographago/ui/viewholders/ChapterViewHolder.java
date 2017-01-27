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

    public ChapterViewHolder(View itemView, Activity context, ArrayList<ChapterModel> chapterModels) {
        super(itemView);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.list_chapters);
        mContext = context;
        mChapterModels = chapterModels;
    }

    public void onBind(int position) {
//        ChapterModel chapterModel = mChapterModels.get(position);
//        ArrayList<VerseComponentsModel> verseComponentsModels = new ArrayList<>();
//        for (VerseComponentsModel model : chapterModel.getVerseComponentsModels()) {
//            if (verseComponentsModels.size() > 0) {
//                if (model.getVerseNumber() == verseComponentsModels.get(verseComponentsModels.size() - 1).getVerseNumber()) {
//                    continue;
//                }
//            }
//            verseComponentsModels.add(model);
//        }
        VerseAdapter adapter = new VerseAdapter(mContext, mChapterModels.get(position));//, verseComponentsModels);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
    }
}