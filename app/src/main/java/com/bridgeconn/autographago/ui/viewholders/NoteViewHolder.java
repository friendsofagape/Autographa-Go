package com.bridgeconn.autographago.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.ui.activities.EditNoteActivity;
import com.bridgeconn.autographago.ui.activities.NotesActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View mView;
    private TextView mTvTitle;
    private TextView mTvText;
    private ImageView mDelete;
    private Context mContext;
    private ArrayList<NotesModel> mNotesModels;

    public NoteViewHolder(View itemView, Context context, ArrayList<NotesModel> notesModels) {
        super(itemView);

        mView = itemView;
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
        mView.setTag(position);
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete_note: {
                int position = (int) v.getTag();
                ((NotesActivity) mContext).refreshList(position);
                break;
            }
            case R.id.note_view: {
                int position = (int) v.getTag();
                Intent intent = new Intent(mContext, EditNoteActivity.class);
                intent.putExtra(Constants.Keys.SAVED_NOTE_TIMESTAMP, mNotesModels.get(position).getTimestamp());
                mContext.startActivity(intent);
                break;
            }
        }
    }
}