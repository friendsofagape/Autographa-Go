package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.ui.viewholders.BookItemViewHolder;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<BookIdModel> mBookModels;

    public BookAdapter(Context context, ArrayList<BookIdModel> bookModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mBookModels = bookModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookItemViewHolder(mLayoutInflater.inflate(R.layout.item_book, parent, false), mContext, mBookModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookItemViewHolder) {
            ((BookItemViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mBookModels.size();
    }

}
