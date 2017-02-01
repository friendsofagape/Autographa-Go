package com.bridgeconn.autographago.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;
    private EditText mEditor;
    private EditText mEtTitle;
    private TextView mSave;
    private LinearLayout mButtonLayout;
    private ImageView mAddVerse;
    private HashSet<SearchModel> mVerseList = new HashSet<>();
    private long mTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_notes));

        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEditor = (EditText) findViewById(R.id.editor);
        mSave = (TextView) findViewById(R.id.iv_save);
        mButtonLayout = (LinearLayout) findViewById(R.id.button_layout);
        mAddVerse = (ImageView) findViewById(R.id.iv_add_verse);

        mSave.setOnClickListener(this);
        mAddVerse.setOnClickListener(this);

        mEditor.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.add(R.string.bold);
                menu.add(R.string.italics);
                menu.add(R.string.underline);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getTitle().toString()) {
                    case Constants.TextEditor.BOLD: {
                        Spannable spannable = new SpannableString(mEditor.getText());
                        spannable.setSpan(new StyleSpan(Typeface.BOLD), findMin(), findMax(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditor.setText(spannable);
                        break;
                    }
                    case Constants.TextEditor.ITALICS: {
                        Spannable spannable = new SpannableString(mEditor.getText());
                        spannable.setSpan(new StyleSpan(Typeface.ITALIC), findMin(), findMax(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditor.setText(spannable);
                        break;
                    }
                    case Constants.TextEditor.UNDERLINE: {
                        Spannable spannable = new SpannableString(mEditor.getText());
                        spannable.setSpan(new UnderlineSpan(), findMin(), findMax(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditor.setText(spannable);
                        break;
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        // TODO new note or edit note

        Intent intent = getIntent();
        long savedNoteTimeStamp = intent.getLongExtra(Constants.Keys.SAVED_NOTE_TIMESTAMP, 0);
        if (savedNoteTimeStamp > 0) {
            List<NotesModel> notesModels = query(new AllSpecifications.NotesById(savedNoteTimeStamp), new AllMappers.NotesMapper());
            for (NotesModel notesModel : notesModels) {
                mEtTitle.setText(notesModel.getTitle());
                mEditor.setText(notesModel.getText());
                mTimeStamp = notesModel.getTimestamp();

                for (final SearchModel model : notesModel.getVerseIds()) {
                    mVerseList.add(model);
                    final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
                    TextView button = (TextView) view.findViewById(R.id.button);
                    button.setText(model.getBookName() + " " + model.getChapterNumber() + Constants.Styling.CHAR_COLON + model.getVerseNumber());
                    ImageView remove = (ImageView) view.findViewById(R.id.iv_remove);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EditNoteActivity.this, BookActivity.class);
                            intent.putExtra(Constants.Keys.BOOK_ID, model.getBookId());
                            intent.putExtra(Constants.Keys.CHAPTER_NO,model.getChapterNumber());
                            intent.putExtra(Constants.Keys.VERSE_NO, model.getVerseNumber());
                            startActivity(intent);
                        }
                    });
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mButtonLayout.removeView(view);
                        }
                    });
                    mButtonLayout.addView(view);
                }

                break;
            }
        } else {
            ArrayList<SearchModel> models = intent.getParcelableArrayListExtra(Constants.Keys.VERSE_MODELS);
            if (models != null) {
                for (SearchModel model : models) {
                    mVerseList.add(model);
                    final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
                    TextView button = (TextView) view.findViewById(R.id.button);
                    button.setText(model.getBookName() + " " + model.getChapterNumber() + Constants.Styling.CHAR_COLON + model.getVerseNumber());
                    ImageView remove = (ImageView) view.findViewById(R.id.iv_remove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mButtonLayout.removeView(view);
                        }
                    });
                    mButtonLayout.addView(view);
                }
            }
        }
    }

    public ArrayList<NotesModel> query(Specification<NotesModel> specification, Mapper<NotesModel, NotesModel> mapper) {
        RealmResults<NotesModel> realmResults = specification.generateResults(realm);

        ArrayList<NotesModel> resultsToReturn = new ArrayList<>();

        for (NotesModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }

        return resultsToReturn;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    private int findMin() {
        int min = 0;
        if (mEditor.isFocused()) {
            int selStart = mEditor.getSelectionStart();
            int selEnd = mEditor.getSelectionEnd();
            min = Math.max(0, Math.min(selStart, selEnd));
        }
        return min;
    }

    private int findMax() {
        int max = mEditor.getText().length();
        if (mEditor.isFocused()) {
            int selStart = mEditor.getSelectionStart();
            int selEnd = mEditor.getSelectionEnd();
            max = Math.max(0, Math.max(selStart, selEnd));
        }
        return max;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO show a dialog, sure you want to discard
                showDiscardDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showDiscardDialog();
    }

    private void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
        builder.setTitle(getString(R.string.discard_note));
        builder.setMessage(getString(R.string.discard_note_message));

        String positiveText = getString(R.string.discard);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_save: {
                // check title then save
                String titleText = mEtTitle.getText().toString();
                if (TextUtils.isEmpty(titleText) || TextUtils.getTrimmedLength(titleText) == 0) {
                    Toast.makeText(this, getString(R.string.title_empty), Toast.LENGTH_SHORT).show();
                    break;
                }
                saveToDB();
                break;
            }
            case R.id.iv_add_verse: {
                if (Constants.CONTAINER.getBookModelList().size() > 0) {
                    Intent intent = new Intent(this, SelectChapterAndVerseActivity.class);
                    intent.putExtra(Constants.Keys.SELECT_VERSE_FOR_NOTE, true);
                    intent.putExtra(Constants.Keys.BOOK_ID, Constants.CONTAINER.getBookModelList().get(0).getBookId());
                    startActivityForResult(intent, Constants.RequestCodes.EDIT_NOTES);
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.EDIT_NOTES: {
                if (resultCode == RESULT_OK) {
                    // TODO here book name is always first one
                    final SearchModel model = data.getParcelableExtra(Constants.Keys.VERSE_NOTE_MODEL);
                    mVerseList.add(model);
                    final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
                    TextView button = (TextView) view.findViewById(R.id.button);
                    button.setText(model.getBookName() + " " +
                            model.getChapterNumber() + Constants.Styling.CHAR_COLON +
                            model.getVerseNumber());
                    ImageView remove = (ImageView) view.findViewById(R.id.iv_remove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mVerseList.remove(model);
                            mButtonLayout.removeView(view);
                        }
                    });
                    mButtonLayout.addView(view);
                }
                break;
            }
        }
    }

    private void saveToDB() {
        NotesModel notesModel = new NotesModel();
        notesModel.setTitle(mEtTitle.getText().toString());
        notesModel.setText(mEditor.getText().toString());
        RealmList<SearchModel> list = new RealmList<>();
        for (SearchModel model : mVerseList) {
            list.add(model);
        }
        notesModel.setVerseIds(list);
        if (mTimeStamp > 0) {
            // previous note
            notesModel.setTimestamp(mTimeStamp);
            new AutographaRepository<NotesModel>().update(notesModel);
        } else {
            notesModel.setTimestamp(System.currentTimeMillis());
            new AutographaRepository<NotesModel>().add(notesModel);
        }
        Toast.makeText(EditNoteActivity.this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        // TODO call notify item added to notes list in notes activity
        finish();
    }

}