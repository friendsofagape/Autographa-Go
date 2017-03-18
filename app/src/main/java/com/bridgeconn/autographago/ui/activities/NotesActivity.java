package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.NotesAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvNewNote;
    private RecyclerView mRecyclerView;
    private NotesAdapter mAdapter;
    private ArrayList<NotesModel> mNotesModels = new ArrayList<>();
    private String languageCode, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notes);

        UtilFunctions.applyReadingMode();

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mIvNewNote = (ImageView) findViewById(R.id.iv_new_note);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_notes);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new NotesAdapter(this, mNotesModels);
        mRecyclerView.setAdapter(mAdapter);

        mIvNewNote.setOnClickListener(this);
    }

    private void getNotesFromDB() {
        final Realm realm = Realm.getDefaultInstance();
        mNotesModels.clear();
        ArrayList<NotesModel> models = query(realm, new AllSpecifications.AllNotes(languageCode, versionCode), new AllMappers.NotesMapper());
        for (NotesModel model : models) {
            NotesModel notesModel = new NotesModel();
            notesModel.setTitle(model.getTitle());
            notesModel.setText(model.getText());
            notesModel.setLanguageCode(model.getLanguageCode());
            notesModel.setVersionCode(model.getVersionCode());
            for (VerseIdModel verseIdModel : model.getVerseIds()) {
                VerseIdModel vIdModel = new VerseIdModel();
                vIdModel.setChapterNumber(verseIdModel.getChapterNumber());
                vIdModel.setVerseNumber(verseIdModel.getVerseNumber());
                vIdModel.setBookId(verseIdModel.getBookId());
                vIdModel.setBookName(verseIdModel.getBookName());
                notesModel.getVerseIds().add(vIdModel);
            }
            notesModel.setTimestamp(model.getTimestamp());
            mNotesModels.add(notesModel);
        }
        realm.close();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<NotesModel> query(Realm realm, Specification<NotesModel> specification, Mapper<NotesModel, NotesModel> mapper) {
        RealmResults<NotesModel> realmResults = specification.generateResults(realm);

        ArrayList<NotesModel> resultsToReturn = new ArrayList<>();

        for (NotesModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }

        return resultsToReturn;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNotesFromDB();
    }

    public void refreshList(int position) {
        new AutographaRepository<NotesModel>().remove(new AllSpecifications.NotesById(mNotesModels.get(position).getTimestamp(), languageCode, versionCode));
        mNotesModels.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mNotesModels.size(), null);
    }
}