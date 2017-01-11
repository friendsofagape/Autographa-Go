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
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.BookmarkModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.ChapterAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {

    //    private Button unzipButton;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChapterAdapter mAdapter;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();

    private BookModel mBookModel;
    private TextView mToolBarTitle;
    private ImageView mIvBookMark;
    private String mBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        int verseNumber = intent.getIntExtra(Constants.Keys.VERSE_NO, 0);
        int chapterNumber = intent.getIntExtra(Constants.Keys.CHAPTER_NO, 0);
        mBookId = intent.getStringExtra(Constants.Keys.BOOK_ID);

        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mIvBookMark = (ImageView) findViewById(R.id.iv_bookmark);
        mIvBookMark.setOnClickListener(this);

        for (int k=0; k<Constants.CONTAINER.getBookModelList().size(); k++) {
            BookModel bookModel = Constants.CONTAINER.getBookModelList().get(k);
            if (bookModel.getBookId().equals(mBookId)) {
                mBookModel = bookModel;
                break;
            }
        }

        if (mBookModel != null) {
            getSupportActionBar().setTitle("");
            mToolBarTitle.setText(mBookModel.getBookName());

            for (ChapterModel model : mBookModel.getChapterModels()) {
                mChapterModels.add(model);
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.list_chapters);

        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChapterAdapter(this, mChapterModels);
        mRecyclerView.setAdapter(mAdapter);

//        if (chapterNumber > 1) {
//            mRecyclerView.smoothScrollToPosition(chapterNumber - 1);
//            mRecyclerView.smoothScrollBy(0, 10);
//        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bookmark: {
                BookmarkModel model = new BookmarkModel();
                int position = mLayoutManager.findFirstVisibleItemPosition();
                model.setChapterId(mBookId + "_" + (position + 1));
                new AutographaRepository<BookmarkModel>().add(model);
                break;
            }
        }
//        if(Intent.ACTION_VIEW.equals(intent.getAction())){
//            String filePath = intent.getData().getPath();
//
//            USFMParser usfmParser = new USFMParser();
//            Toast.makeText(getApplicationContext(), "path= " + filePath, Toast.LENGTH_SHORT).show();
//            usfmParser.parseUSFMFile(this, filePath, false);
//        }

//        unzipButton = (Button) findViewById(R.id.bt_unzip);
//        unzipButton.setOnClickListener(this);

//        switch (v.getId()) {
//            case R.id.bt_unzip: {
//                String filePath = Environment.getExternalStorageDirectory()+"/Download/zipfolder.zip";
//                UnzipUtil.unzipFile(new File(filePath), getApplicationContext());
//                break;
//            }
//        }
    }

}