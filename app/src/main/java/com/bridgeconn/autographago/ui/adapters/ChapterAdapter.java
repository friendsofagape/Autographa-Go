package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ui.viewholders.ChapterViewHolder;

import java.util.ArrayList;

public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ChapterModel> mChapterModels;

    public ChapterAdapter(Context context, ArrayList<ChapterModel> chapterModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mChapterModels = chapterModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChapterViewHolder(mLayoutInflater.inflate(R.layout.item_chapter, parent, false), mContext, mChapterModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterViewHolder) {
            ((ChapterViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mChapterModels.size();
    }

}