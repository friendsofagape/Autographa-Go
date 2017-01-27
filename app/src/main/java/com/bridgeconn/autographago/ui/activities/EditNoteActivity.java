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
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.HashSet;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditor;
    private EditText mEtTitle;
    private TextView mSave;
    private LinearLayout mButtonLayout;
    private ImageView mAddVerse;
    private HashSet<SearchModel> verseList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notes");

        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEditor = (EditText) findViewById(R.id.editor);
        mSave = (TextView) findViewById(R.id.iv_save);
        mButtonLayout = (LinearLayout) findViewById(R.id.button_layout);
        mAddVerse = (ImageView) findViewById(R.id.iv_add_verse);

        Intent intent = getIntent();
        HashSet<SearchModel> modelHashSet = (HashSet<SearchModel>) intent.getSerializableExtra(Constants.Keys.VERSE_SET);
        if (modelHashSet != null) {
            for (SearchModel model : modelHashSet) {
                verseList.add(model);
                final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
                TextView button = (TextView) view.findViewById(R.id.button);
                button.setText(model.getBookName() + " " + model.getChapterNumber() + ":" + model.getVerseNumber());
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
                    case "BOLD": {
                        Spannable spannable = new SpannableString(mEditor.getText());
                        spannable.setSpan(new StyleSpan(Typeface.BOLD), findMin(), findMax(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditor.setText(spannable);
                        break;
                    }
                    case "ITALICS": {
                        Spannable spannable = new SpannableString(mEditor.getText());
                        spannable.setSpan(new StyleSpan(Typeface.ITALIC), findMin(), findMax(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEditor.setText(spannable);
                        break;
                    }
                    case "UNDERLINE": {
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
    }

    private String getLabelText() {

        return "";
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
                // TODO show verse number chooser
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
                    String bookId = data.getStringExtra(Constants.Keys.BOOK_ID);
                    if (bookId != null) {
                        // TODO get all this converted to parce and disrectl get model object
                        // TODO add this to verse list hash set
                        final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
                        TextView button = (TextView) view.findViewById(R.id.button);
                        button.setText(UtilFunctions.getBookNameFromMapping(this, bookId) + " " +
                                data.getIntExtra(Constants.Keys.CHAPTER_NO, 0) + ":" +
                                data.getIntExtra(Constants.Keys.VERSE_NO, 0));
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
                break;
            }
        }
    }

    private void saveToDB() {
        NotesModel notesModel = new NotesModel();
        notesModel.setTimestamp(System.currentTimeMillis());
        notesModel.setTitle(mEtTitle.getText().toString());
        notesModel.setText(mEditor.getText().toString());

        new AutographaRepository<NotesModel>().add(notesModel);

        Toast.makeText(EditNoteActivity.this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show();

        finish();
    }

}