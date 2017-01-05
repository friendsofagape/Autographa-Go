package com.bridgeconn.autographago.ui.activities;

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

    private ImageView notesView;
    private ImageView menuView;
    private ImageView searchView;
    private ImageView settingsView;
    private RecyclerView booksRecyclerView;
    private BookAdapter bookAdapter;
    private ArrayList<BookModel> bookModelArrayList = new ArrayList<>();

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

        notesView = (ImageView) findViewById(R.id.item1);
        menuView = (ImageView) findViewById(R.id.item2);
        searchView = (ImageView) findViewById(R.id.item3);
        settingsView = (ImageView) findViewById(R.id.item4);
        booksRecyclerView = (RecyclerView) findViewById(R.id.list_books);

        booksRecyclerView.setHasFixedSize(false);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(this, bookModelArrayList);
        booksRecyclerView.setAdapter(bookAdapter);

        notesView.setOnClickListener(this);
        menuView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        settingsView.setOnClickListener(this);

        getAllBooks();
    }

    private void getAllBooks() {

        new AutographaRepository<BookModel>().addToContainer();

        for (BookModel bookModel : Constants.CONTAINER.getBookModelList()) {
            bookModelArrayList.add(bookModel);
        }
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item1: {
                break;
            }
            case R.id.item2: {
                break;
            }
            case R.id.item3: {
                break;
            }
            case R.id.item4: {
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}