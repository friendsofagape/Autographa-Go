package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.viewholders.HistoryItemViewHolder;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<SearchModel> mHistoryModels;

    public HistoryAdapter(Context context, ArrayList<SearchModel> historyModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHistoryModels = historyModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
//        return new HistoryItemViewHolder(mLayoutInflater.inflate(R.layout.item_history, parent, false), mContext, mHistoryModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof HistoryItemViewHolder) {
//            ((HistoryItemViewHolder) holder).onBind(position);
//        }
    }

    @Override
    public int getItemCount() {
        return mHistoryModels.size();
    }
}