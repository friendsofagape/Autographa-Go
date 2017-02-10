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

    public VerseAdapter(Activity context, ChapterModel chapterModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mChapterModel = chapterModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VerseViewHolder(mLayoutInflater.inflate(R.layout.item_verse, parent, false), mContext, mChapterModel);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VerseViewHolder) {
            ((VerseViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        for (int i=0; i<mChapterModel.getVerseComponentsModels().size(); i++) {
            if (i==0) {
                size++;
            } else {
                if (mChapterModel.getVerseComponentsModels().get(i).getVerseNumber().equals(
                        mChapterModel.getVerseComponentsModels().get(i-1).getVerseNumber())) {
                    continue;
                } else {
                    size++;
                }
            }
        }
        return size;
//        return mChapterModel.getVerseComponentsModels().get(mChapterModel.getVerseComponentsModels().size() - 1).getVerseNumber();
    }

}