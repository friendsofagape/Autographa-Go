package com.bridgeconn.autographago.ui.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.NotesModel;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

//    private RichEditor mEditor;
//    private TextView mPreview;

    private EditText mEditor;
    private EditText mEtTitle;
    private ImageView mTitleDone;
    private TextView mSave;

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
        getSupportActionBar().setTitle("Notes");

        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEditor = (EditText) findViewById(R.id.editor);
        mTitleDone = (ImageView) findViewById(R.id.done);
        mSave = (TextView) findViewById(R.id.iv_save);

        mSave.setOnClickListener(this);
        mTitleDone.setOnClickListener(this);

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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_save: {
                // check title then save
                if (mEtTitle.isEnabled()) {
                    Toast.makeText(this, getString(R.string.title_empty), Toast.LENGTH_SHORT).show();
                    break;
                }
                saveToDB();
                finish();
                break;
            }
            case R.id.done: {
                // check title and then done
                String titleText = mEtTitle.getText().toString();
                if (TextUtils.isEmpty(titleText) || TextUtils.getTrimmedLength(titleText) == 0) {
                    Toast.makeText(this, getString(R.string.title_empty), Toast.LENGTH_SHORT).show();
                    break;
                }
                mTitleDone.setVisibility(View.GONE);
                mEtTitle.setEnabled(false);
                break;
            }
        }
    }

    private void saveToDB() {
        NotesModel notesModel = new NotesModel();
        notesModel.setTitle(mEtTitle.getText().toString());
        notesModel.setText(mEditor.getText().toString());

//        new AutographaRepository<NotesModel>().add(notesModel);
    }

}