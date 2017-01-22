package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.ui.viewholders.NoteViewHolder;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<NotesModel> mNotesModels;

    public NotesAdapter(Context context, ArrayList<NotesModel> notesModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mNotesModels = notesModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteViewHolder(mLayoutInflater.inflate(R.layout.item_note, parent, false), mContext, mNotesModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoteViewHolder) {
            ((NoteViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mNotesModels.size();
    }

}
