package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.activities.NotesActivity;

import java.util.ArrayList;

public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvTitle;
    private TextView mTvText;
    private ImageView mDelete;
    private Context mContext;
    private ArrayList<NotesModel> mNotesModels;

    public NoteViewHolder(View itemView, Context context, ArrayList<NotesModel> notesModels) {
        super(itemView);

        mContext = context;
        mTvTitle = (TextView) itemView.findViewById(R.id.note_title);
        mTvText = (TextView) itemView.findViewById(R.id.note_text);
        mDelete = (ImageView) itemView.findViewById(R.id.iv_delete_note);
        mNotesModels = notesModels;
    }

    public void onBind(final int position) {
        mTvTitle.setText(mNotesModels.get(position).getTitle());
        mTvText.setText(mNotesModels.get(position).getText());
        mDelete.setTag(position);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete_note: {
                int position = (int) v.getTag();
                new AutographaRepository<NotesModel>().remove(new AllSpecifications.NotesById(mNotesModels.get(position).getTimestamp()));
                ((NotesActivity) mContext).refreshList(position);
                break;
            }
        }
    }
}