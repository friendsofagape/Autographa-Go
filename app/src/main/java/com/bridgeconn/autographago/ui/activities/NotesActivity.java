package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvNewNote;
    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private ArrayList<NotesModel> mNotesModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getNotesFromDB();

        mIvNewNote = (ImageView) findViewById(R.id.iv_new_note);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_notes);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new NotesAdapter(this, mNotesModels);
        mRecyclerView.setAdapter(mAdapter);

        mIvNewNote.setOnClickListener(this);
    }

    private void getNotesFromDB() {
        mNotesModels.clear();
        ArrayList<NotesModel> models = new AutographaRepository<NotesModel>().query(new AllSpecifications.AllNotes(), new AllMappers.NotesMapper());
        for (NotesModel model : models) {
            mNotesModels.add(model);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_new_note: {
                Intent intent = new Intent(this, EditNoteActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNotesFromDB();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void refreshList(int position) {
        new AutographaRepository<NotesModel>().remove(new AllSpecifications.NotesById(mNotesModels.get(position).getTimestamp()));
        mNotesModels.remove(position);
//        mAdapter.notifyDataSetChanged();
        mAdapter.notifyItemRemoved(position);
        // TODO fix this, item deleted but not from display
    }
}