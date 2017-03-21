package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ui.viewholders.HighlightViewHolder;

import java.util.ArrayList;

public class HighlightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<VerseIdModel> mHighlightModels;

    public HighlightAdapter(Context context, ArrayList<VerseIdModel> highlightModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHighlightModels = highlightModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HighlightViewHolder(mLayoutInflater.inflate(R.layout.item_bookmark, parent, false), mContext, mHighlightModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HighlightViewHolder) {
            ((HighlightViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mHighlightModels.size();
    }
}