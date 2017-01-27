package com.bridgeconn.autographago.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ui.viewholders.ChapterViewHolder;
import com.bridgeconn.autographago.ui.viewholders.VerseViewHolder;

import java.util.ArrayList;

public class VerseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private ChapterModel mChapterModel;
//    private ArrayList<VerseComponentsModel> mVerseComponentsModels;

    public VerseAdapter(Activity context, ChapterModel chapterModel) {//}, ArrayList<VerseComponentsModel> verseComponentsModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mChapterModel = chapterModel;
//        mVerseComponentsModels = verseComponentsModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VerseViewHolder(mLayoutInflater.inflate(R.layout.item_verse, parent, false), mContext, mChapterModel);//, mVerseComponentsModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VerseViewHolder) {
            ((VerseViewHolder) holder).onBind(position + 1);
        }
    }

    @Override
    public int getItemCount() {
//        return mVerseComponentsModels.size();
        return mChapterModel.getVerseComponentsModels().get(mChapterModel.getVerseComponentsModels().size() - 1).getVerseNumber();
    }

}