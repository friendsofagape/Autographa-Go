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
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.BookAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mNotesView = (ImageView) findViewById(R.id.iv_notes);
        mMenuView = (ImageView) findViewById(R.id.iv_menu);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mHistoryView = (ImageView) findViewById(R.id.iv_history);
        mSettingsView = (ImageView) findViewById(R.id.iv_settings);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_books);
        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookAdapter(this, mBookModelArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mSpinner.setOnItemSelectedListener(this);
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

    private void getBooksByLanguageAndVersion(String language, String version) {
        new AutographaRepository<BookModel>().addToContainer();

        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            mBookModelArrayList.add(bookModel);
        }

//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

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
                startActivity(settingIntent);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}