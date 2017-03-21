package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.viewholders.SearchItemViewHolder;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<SearchModel> mSearchModels;

    public SearchAdapter(Context context, ArrayList<SearchModel> searchModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mSearchModels = searchModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchItemViewHolder(mLayoutInflater.inflate(R.layout.item_search, parent, false), mContext, mSearchModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchItemViewHolder) {
            ((SearchItemViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchModels.size();
    }

}