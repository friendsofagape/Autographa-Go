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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.BookmarkModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.ChapterAdapter;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;

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

    private LinearLayout mBottomBar;

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
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

        mIvBookMark.setOnClickListener(this);
        findViewById(R.id.tv_highlight).setOnClickListener(this);
        findViewById(R.id.tv_notes).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);

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

    private void findSelectedAndHighlight() {
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setHighlighted(true);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void findSelectedAndAddNote() {
        HashSet<SearchModel> searchModelSet = new HashSet<>();
        ArrayList<SearchModel> models = new ArrayList<>();
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    SearchModel searchModel = new SearchModel();
                    searchModel.setBookId(mBookId);
                    searchModel.setBookName(mBookModel.getBookName());
                    searchModel.setChapterNumber(mChapterModels.get(i).getChapterNumber());
                    searchModel.setVerseNumber(mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber());
                    models.add(searchModel);
                    searchModelSet.add(searchModel);
                }
            }
        }
        mAdapter.notifyDataSetChanged();

        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putParcelableArrayListExtra(Constants.Keys.VERSE_MODELS, models);
        intent.putExtra(Constants.Keys.VERSE_SET, searchModelSet);
        startActivity(intent);
    }

    private void findSelectedAndShare() {
        StringBuilder shareBody = new StringBuilder(getString(R.string.share_body));
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    VerseComponentsModel verseComponentsModel = mChapterModels.get(i).getVerseComponentsModels().get(j);
                    if (verseComponentsModel.getText() != null){
                        if (!verseComponentsModel.getText().trim().equals("")) {
                            String[] splitString = verseComponentsModel.getText().split(Constants.Styling.SPLIT_SPACE);
                            for (int n = 0; n < splitString.length; n++) {
                                switch (splitString[n]) {
                                    case Constants.Markers.MARKER_NEW_PARAGRAPH: {
                                        shareBody.append(Constants.Styling.NEW_LINE);
                                        break;
                                    }
                                    case Constants.Styling.MARKER_Q: {
                                        shareBody.append(Constants.Styling.NEW_LINE_WITH_TAB_SPACE);
                                        break;
                                    }
                                    default: {
                                        if (splitString[n].startsWith(Constants.Styling.MARKER_Q)) {
                                            String str = splitString[n];
                                            int number = Integer.parseInt(str.replaceAll(Constants.Styling.REGEX_NUMBERS, ""));
                                            shareBody.append(Constants.Styling.NEW_LINE);
                                            for (int o = 0; o < number; o++) {
                                                shareBody.append(Constants.Styling.TAB_SPACE);
                                            }
                                        } else if (splitString[n].startsWith(Constants.Styling.REGEX_ESCAPE)) {
                                            break;
                                        } else {
                                            shareBody.append(splitString[n] + " ");
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(Constants.SHARE_TEXT_TYPE);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

        mAdapter.notifyDataSetChanged();
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
            case R.id.tv_highlight: {
                findSelectedAndHighlight();
                hideBottomBar();
                break;
            }
            case R.id.tv_notes: {
                findSelectedAndAddNote();
                hideBottomBar();
                break;
            }
            case R.id.tv_share: {
                findSelectedAndShare();
                hideBottomBar();
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

    public void showBottomBar() {
        // TODO when bar visible add dummy item to end of chapters to show last text
        mBottomBar.setVisibility(View.VISIBLE);
    }

    public void hideBottomBar() {
        // TODO when bar hide, remove dummy item from end of chapter
        mBottomBar.setVisibility(View.GONE);
    }

    // TODO clear this - how do we store highlights in db, or map notes in db
    // TODO for this, wither each verse component will be new line
    // TODO as user can select any text
    // TODO or we store position of text along with each component, and there can be multiple positionings for one component
}