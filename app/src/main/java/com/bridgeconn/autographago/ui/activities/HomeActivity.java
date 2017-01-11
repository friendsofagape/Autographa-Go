package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.BookAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mNotesView;
    private ImageView mMenuView;
    private ImageView mSearchView;
    private ImageView mSettingsView;
    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private ArrayList<BookModel> mBookModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String [] fileNames = {"28-HOS.usfm", "65-3JN.usfm", "36-ZEP.usfm"};

        for (int i=0; i<fileNames.length; i++) {
            ArrayList<BookModel> bookModels = new AutographaRepository<BookModel>().query(new AllSpecifications.BookModelById(fileNames[i]), new AllMappers.BookMapper());
            if (bookModels.size() > 0) {
                // already in db
            } else {
                // add to db
                USFMParser usfmParser = new USFMParser();
                usfmParser.parseUSFMFile(this, fileNames[i], true);
            }
        }

        mNotesView = (ImageView) findViewById(R.id.iv_notes);
        mMenuView = (ImageView) findViewById(R.id.iv_menu);
        mSearchView = (ImageView) findViewById(R.id.iv_search);
        mSettingsView = (ImageView) findViewById(R.id.iv_settings);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_books);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookAdapter(this, mBookModelArrayList);
        mRecyclerView.setAdapter(mAdapter);

        mNotesView.setOnClickListener(this);
        mMenuView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mSettingsView.setOnClickListener(this);

        getAllBooks();
    }

    private void getAllBooks() {

        new AutographaRepository<BookModel>().addToContainer();

        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            mBookModelArrayList.add(bookModel);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_notes: {
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
            case R.id.iv_settings: {
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}