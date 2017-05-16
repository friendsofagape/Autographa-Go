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
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.NotesStyleModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.customviews.FlowLayout;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.HashSet;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditor;
    private EditText mEtTitle;
    private TextView mSave;
    private FlowLayout mButtonLayout;
    private ImageView mAddVerse;
    private HashSet<VerseIdModel> mVerseList = new HashSet<>();
    private long mTimeStamp = 0;
    private String languageCode, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_note);

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
        getSupportActionBar().setTitle(getResources().getString(R.string.notes));

        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEditor = (EditText) findViewById(R.id.editor);
        mSave = (TextView) findViewById(R.id.iv_save);
        mButtonLayout = (FlowLayout) findViewById(R.id.button_layout);
        mAddVerse = (ImageView) findViewById(R.id.iv_add_verse);

        mSave.setOnClickListener(this);
        mAddVerse.setOnClickListener(this);

        mEditor.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(R.string.bold);
                menu.add(R.string.italics);
                menu.add(R.string.underline);
                menu.add(R.string.bullets);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
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
                    case Constants.TextEditor.BULLETS: {
                        mEditor.getText().insert(findMin(), "\n    \u2022");
//                        mEditor.append("\\n\\u2022", findMin(), findMin() + 8);
                        break;
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        Intent intent = getIntent();
        NotesModel notesModel = intent.getParcelableExtra(Constants.Keys.NOTES_MODEL);
        if (notesModel != null) {
            mEtTitle.setText(notesModel.getTitle());
            mEditor.setText(notesModel.getText());
            for (NotesStyleModel styleModel : notesModel.getNotesStyleModels()) {
                Spannable spannable = new SpannableString(mEditor.getText());
                switch (styleModel.getStyle()) {
                    case Constants.TextEditorStyles.BOLD: {
                        spannable.setSpan(new StyleSpan(Typeface.BOLD), styleModel.getStart(), styleModel.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                    case Constants.TextEditorStyles.ITALICS: {
                        spannable.setSpan(new StyleSpan(Typeface.ITALIC), styleModel.getStart(), styleModel.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                    case Constants.TextEditorStyles.UNDERLINE: {
                        spannable.setSpan(new UnderlineSpan(), styleModel.getStart(), styleModel.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
                mEditor.setText(spannable);
            }
            mTimeStamp = notesModel.getTimestamp();
            for (final VerseIdModel model : notesModel.getVerseIds()) {
                mVerseList.add(model);
                addNoteButton(model, false);
            }
        }
//        else {
            ArrayList<VerseIdModel> models = intent.getParcelableArrayListExtra(Constants.Keys.VERSE_MODELS);
            if (models != null) {
                for (final VerseIdModel model : models) {
                    mVerseList.add(model);
                    addNoteButton(model, false);
                }
            }
//        }
    }

    private void addNoteButton(final VerseIdModel model, final boolean removeFromList) {
        final View view = LayoutInflater.from(this).inflate(R.layout.button_verse, mButtonLayout, false);
        TextView button = (TextView) view.findViewById(R.id.button);
        button.setText(model.getBookName() + " " +
                model.getChapterNumber() + Constants.Styling.CHAR_COLON +
                model.getVerseNumber());
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
                if (removeFromList) {
                    mVerseList.remove(model);
                }
                mButtonLayout.removeView(view);
            }
        });
        mButtonLayout.addView(view);
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
                if (Constants.CONTAINER_BOOKS_LIST.size() > 0) {
                    Intent intent = new Intent(this, SelectChapterAndVerseActivity.class);
                    intent.putExtra(Constants.Keys.SELECT_VERSE_FOR_NOTE, true);
                    intent.putExtra(Constants.Keys.OPEN_BOOK, false);
                    intent.putExtra(Constants.Keys.BOOK_ID, Constants.CONTAINER_BOOKS_LIST.get(0).getBookId());
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
                    final VerseIdModel model = data.getParcelableExtra(Constants.Keys.VERSE_NOTE_MODEL);
                    if (mVerseList.add(model)) {
                        addNoteButton(model, true);
                    }
                }
                break;
            }
        }
    }

    private void saveToDB() {
        NotesModel notesModel = new NotesModel();
        notesModel.setTitle(mEtTitle.getText().toString());
        notesModel.setText(mEditor.getText().toString());

        StyleSpan [] styleSpans = mEditor.getText().getSpans(0, mEditor.getText().length(), StyleSpan.class);
        UnderlineSpan [] underlineSpans = mEditor.getText().getSpans(0, mEditor.getText().length(), UnderlineSpan.class);

        for (StyleSpan span : styleSpans) {
            NotesStyleModel notesStyleModel = new NotesStyleModel();
            if (span.getStyle() == Typeface.BOLD) {
                notesStyleModel.setStyle(Constants.TextEditorStyles.BOLD);
                notesStyleModel.setStart(mEditor.getText().getSpanStart(span));
                notesStyleModel.setEnd(mEditor.getText().getSpanEnd(span));
            } else if (span.getStyle() == Typeface.ITALIC) {
                notesStyleModel.setStyle(Constants.TextEditorStyles.ITALICS);
                notesStyleModel.setStart(mEditor.getText().getSpanStart(span));
                notesStyleModel.setEnd(mEditor.getText().getSpanEnd(span));
            }
            notesModel.getNotesStyleModels().add(notesStyleModel);
        }
        for (UnderlineSpan span : underlineSpans) {
            NotesStyleModel notesStyleModel = new NotesStyleModel();
            notesStyleModel.setStyle(Constants.TextEditorStyles.UNDERLINE);
            notesStyleModel.setStart(mEditor.getText().getSpanStart(span));
            notesStyleModel.setEnd(mEditor.getText().getSpanEnd(span));
            notesModel.getNotesStyleModels().add(notesStyleModel);
        }

        notesModel.setLanguageCode(languageCode);
        notesModel.setVersionCode(versionCode);
        for (VerseIdModel model : mVerseList) {
            notesModel.getVerseIds().add(model);
        }
        if (mTimeStamp > 0) {
            // previous note
            notesModel.setTimestamp(mTimeStamp);
            new AutographaRepository<NotesModel>().update(notesModel);
        } else {
            notesModel.setTimestamp(System.currentTimeMillis());
            new AutographaRepository<NotesModel>().add(notesModel);
        }
        Toast.makeText(EditNoteActivity.this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

}