package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ui.adapters.BookAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView mToolbarTitle;
    private ImageView mContinueRead;
    private ImageView mNotesView;
    private ImageView mMenuView;
    private ImageView mSearchView;
    private ImageView mHistoryView;
    private ImageView mSettingsView;
    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private ArrayList<BookModel> mBookModelArrayList = new ArrayList<>();
    private AppCompatSpinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mContinueRead = (ImageView) findViewById(R.id.iv_continue_reading);
        mNotesView = (ImageView) findViewById(R.id.iv_notes);
        mMenuView = (ImageView) findViewById(R.id.iv_menu);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mHistoryView = (ImageView) findViewById(R.id.iv_history);
        mSettingsView = (ImageView) findViewById(R.id.iv_settings);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_books);
        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookAdapter(this, mBookModelArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mSpinner.setOnItemSelectedListener(this);
        mToolbarTitle.setOnClickListener(this);
        mContinueRead.setOnClickListener(this);
        mNotesView.setOnClickListener(this);
        mMenuView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mHistoryView.setOnClickListener(this);
        mSettingsView.setOnClickListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("English ULB");
//        categories.add("English UDB");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);

        getAllBooks();
    }

//    private void getBooksByLanguageAndVersion(String language, String version) {
//        new AutographaRepository<BookModel>().addToContainer();
//
//        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
//            mBookModelArrayList.add(bookModel);
//        }
//
////        mAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        String[] arr = item.split(" ");
//        getBooksByLanguageAndVersion(arr[0], arr[1]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getAllBooks() {

        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            mBookModelArrayList.add(bookModel);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_title: {
                Intent intent = new Intent(this, AboutPageActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.iv_continue_reading: {
                String bookId = SharedPrefs.getString(Constants.PrefKeys.LAST_READ_BOOK_ID, null);
                int chapter = SharedPrefs.getInt(Constants.PrefKeys.LAST_READ_CHAPTER, 1);
                String verse = SharedPrefs.getString(Constants.PrefKeys.LAST_READ_VERSE, null);
                if (bookId == null) {
                    break;
                }
                if (verse == null) {
                    break;
                }
                Intent readIntent = new Intent(this, BookActivity.class);
                readIntent.putExtra(Constants.Keys.BOOK_ID, bookId);
                readIntent.putExtra(Constants.Keys.CHAPTER_NO, chapter);
                readIntent.putExtra(Constants.Keys.VERSE_NO, verse);
                startActivity(readIntent);
                break;
            }
            case R.id.iv_notes: {
                Intent notesIntent = new Intent(this, NotesActivity.class);
                startActivity(notesIntent);
                break;
            }
            case R.id.iv_menu: {
                Intent menuIntent = new Intent(this, MenuActivity.class);
                startActivity(menuIntent);
                break;
            }
            case R.id.iv_search: {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                break;
            }
            case R.id.iv_history: {
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            }
            case R.id.iv_settings: {
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingIntent, Constants.RequestCodes.SETTINGS);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.SETTINGS: {
                if (resultCode == RESULT_OK) {
                    boolean readingMode, fontSize;
                    readingMode = data.getBooleanExtra(Constants.Keys.TEXT_SIZE_CHANGED, false);
                    fontSize = data.getBooleanExtra(Constants.Keys.READING_MODE_CHANGE, false);
                    if (readingMode || fontSize) {
                        this.recreate();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}