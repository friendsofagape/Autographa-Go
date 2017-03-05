package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.ChapterAdapter;
import com.bridgeconn.autographago.ui.customviews.BounceInterpolator;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChapterAdapter mAdapter;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();

    private BookModel mBookModel;
    private TextView mToolBarTitle;
    private ImageView mIvBookMark;
    private String mBookId;
    private int mBookMarkNumber;

    private LinearLayout mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        String verseNumber = intent.getStringExtra(Constants.Keys.VERSE_NO);
        int chapterNumber = intent.getIntExtra(Constants.Keys.CHAPTER_NO, 0);
        mBookId = intent.getStringExtra(Constants.Keys.BOOK_ID);

        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mIvBookMark = (ImageView) findViewById(R.id.iv_bookmark);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

        mIvBookMark.setOnClickListener(this);
        findViewById(R.id.view_highlights).setOnClickListener(this);
        findViewById(R.id.view_notes).setOnClickListener(this);
        findViewById(R.id.view_share).setOnClickListener(this);

        for (int k = 0; k < Constants.CONTAINER.getBookModelList().size(); k++) {
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

            mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.list_chapters);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChapterAdapter(this, mChapterModels, verseNumber, chapterNumber - 1, SharedPrefs.getFontSize());
        mRecyclerView.setAdapter(mAdapter);

        if (mBookMarkNumber == 1) {
            mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
        }

        mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                int chapterNum = findChapterNumber(firstVisibleItem);
                if (mBookMarkNumber > 0) {
                    if (chapterNum == mBookMarkNumber) {
                        mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                    } else {
                        mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.white));
                    }
                }
            }
        });
    }

    private int findPositionToScroll(int chapterPosition, String verseNumber) {
        int size = 0;
        for (int k=0; k<mChapterModels.size(); k++) {
            for (int i=0; i<mChapterModels.get(k).getVerseComponentsModels().size(); i++) {
                if (i==0) {
                    if (k == chapterPosition && mChapterModels.get(k).getVerseComponentsModels().get(i).getVerseNumber().equals(verseNumber)) {
                        return size;
                    }
                    size++;
                } else {
                    if (mChapterModels.get(k).getVerseComponentsModels().get(i).getVerseNumber().equals(
                            mChapterModels.get(k).getVerseComponentsModels().get(i-1).getVerseNumber())) {
                        continue;
                    } else {
                        if (k == chapterPosition && mChapterModels.get(k).getVerseComponentsModels().get(i).getVerseNumber().equals(verseNumber)) {
                            return size;
                        }
                        size++;
                    }
                }
            }
        }
        return 0;
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

        int bookPosition = 0;
        for (int k = 0; k < Constants.CONTAINER.getBookModelList().size(); k++) {
            if (Constants.CONTAINER.getBookModelList().get(k).getBookId().equals(mBookId)) {
                bookPosition = k;
                break;
            }
        }

        BookModel bookModel = new BookModel(mBookModel);

        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setHighlighted(true);

                    Constants.CONTAINER.getBookModelList().get(bookPosition).
                            getChapterModels().get(i).getVerseComponentsModels().get(j).setHighlighted(true);

                    bookModel.getChapterModels().get(i).getVerseComponentsModels().get(j).setChapterId(
                            bookModel.getChapterModels().get(i).getChapterId());
                    bookModel.getChapterModels().get(i).getVerseComponentsModels().get(j).setHighlighted(true);
                }
            }
        }

        new AutographaRepository<BookModel>().update(bookModel);

        mAdapter.notifyDataSetChanged();
    }

    private void findSelectedAndAddNote() {
        HashSet<VerseIdModel> verseIdModelHashSet = new HashSet<>();
        ArrayList<VerseIdModel> models = new ArrayList<>();
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    VerseIdModel verseIdModel = new VerseIdModel();
                    verseIdModel.setBookId(mBookId);
                    verseIdModel.setBookName(mBookModel.getBookName());
                    verseIdModel.setChapterNumber(mChapterModels.get(i).getChapterNumber());
                    verseIdModel.setVerseNumber(mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber());
                    verseIdModelHashSet.add(verseIdModel);
                }
            }
        }
        for (VerseIdModel model : verseIdModelHashSet) {
            models.add(model);
        }
        mAdapter.notifyDataSetChanged();

        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putParcelableArrayListExtra(Constants.Keys.VERSE_MODELS, models);
        startActivity(intent);
    }

    private void findSelectedAndShare() {
        StringBuilder shareBody = new StringBuilder(getString(R.string.share_body));
        shareBody.append(" ");
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

    private int findChapterNumber(int position) {
        int size = 0;
        for (int i=0; i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (j==0) {
                    if (size == position) {
                        return i+1;
                    }
                    size++;
                } else {
                    if (mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber().equals(
                            mChapterModels.get(i).getVerseComponentsModels().get(j-1).getVerseNumber())) {
                        continue;
                    } else {
                        if (size == position) {
                            return i+1;
                        }
                        size++;
                    }
                }
            }
        }
        return 0;
    }

    private String findVerseNumber(int position) {
        int size = 0;
        for (int i=0; i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (j==0) {
                    if (size == position) {
                        return mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber();
                    }
                    size++;
                } else {
                    if (mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber().equals(
                            mChapterModels.get(i).getVerseComponentsModels().get(j-1).getVerseNumber())) {
                        continue;
                    } else {
                        if (size == position) {
                            return mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber();
                        }
                        size++;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bookmark: {

                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                myAnim.setInterpolator(interpolator);
                mIvBookMark.startAnimation(myAnim);

                mIvBookMark.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

                int position = mLayoutManager.findFirstVisibleItemPosition();

                mBookMarkNumber = findChapterNumber(position);

                for (int k = 0; k < Constants.CONTAINER.getBookModelList().size(); k++) {
                    if (Constants.CONTAINER.getBookModelList().get(k).getBookId().equals(mBookId)) {
                        Constants.CONTAINER.getBookModelList().get(k).setBookmarkChapterNumber(mBookMarkNumber);
                        break;
                    }
                }
                mBookModel.setBookmarkChapterNumber(mBookMarkNumber);
                new AutographaRepository<BookModel>().update(mBookModel);

                break;
            }
            case R.id.view_highlights: {
                findSelectedAndHighlight();
                hideBottomBar();
                break;
            }
            case R.id.view_notes: {
                findSelectedAndAddNote();
                hideBottomBar();
                break;
            }
            case R.id.view_share: {
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
        mBottomBar.setVisibility(View.VISIBLE);
    }

    public void hideBottomBar() {
        mBottomBar.setVisibility(View.GONE);
    }

    /**
     * called whenever a verse is unselected. Then checked for all verses, if none is selected, then hide the bottom bar
     */
    public void checkToHideOrNot() {
        boolean hide = true;
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    hide = false;
                }
            }
        }
        if (hide) {
            hideBottomBar();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
        int chapterNum = findChapterNumber(firstVisibleItem);
        String verseNum = findVerseNumber(firstVisibleItem);

        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String version = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PrefKeys.LAST_READ_BOOK_ID, mBookId);
            jsonObject.put(Constants.PrefKeys.LAST_READ_CHAPTER, chapterNum);
            jsonObject.put(Constants.PrefKeys.LAST_READ_VERSE, verseNum);
            SharedPrefs.putString(Constants.PrefKeys.LAST_READ + "_" + languageCode + "_" + version, jsonObject.toString());
        } catch (JSONException je) {
        }
    }
}