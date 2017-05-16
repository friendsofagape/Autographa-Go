package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ui.viewholders.HistoryHeaderViewHolder;
import com.bridgeconn.autographago.ui.viewholders.HistoryItemViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class HistoryExpandableAdapter extends ExpandableRecyclerViewAdapter<HistoryHeaderViewHolder, HistoryItemViewHolder> {

    private Context mContext;

    public HistoryExpandableAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        mContext = context;
    }

    @Override
    public HistoryHeaderViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return new HistoryHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_header, parent, false));
    }

    @Override
    public HistoryItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new HistoryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false), mContext);
    }

    @Override
    public void onBindChildViewHolder(HistoryItemViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {
        final SearchModel model = (SearchModel) group.getItems().get(childIndex);
//         ((SearchModel) group).getItems().get(childIndex);
        holder.onBind(model, childIndex);
//        holder.setArtistName(artist.getBookName());
    }

    @Override
    public void onBindGroupViewHolder(HistoryHeaderViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setTimeTitle(group);
    }
}
