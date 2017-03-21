package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.ui.viewholders.BookmarkItemViewHolder;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<BookIdModel> mBookmarkModels;

    public BookmarkAdapter(Context context, ArrayList<BookIdModel> bookmarkModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mBookmarkModels = bookmarkModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookmarkItemViewHolder(mLayoutInflater.inflate(R.layout.item_bookmark, parent, false), mContext, mBookmarkModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookmarkItemViewHolder) {
            ((BookmarkItemViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mBookmarkModels.size();
    }
}