package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.ChapterAdapter;
import com.bridgeconn.autographago.ui.customviews.BounceInterpolator;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmResults;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChapterAdapter mAdapter;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();

    private BookModel mBookModel;
    private TextView mToolBarTitle;
    private LinearLayout mBookmarkHolder;
    private ImageView mIvBookMark;
    private String mBookId;
    private int mBookMarkNumber;

    private LinearLayout mBottomBar;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mBookId = getIntent().getStringExtra(Constants.Keys.BOOK_ID);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mIvBookMark = (ImageView) findViewById(R.id.iv_bookmark);
        mBookmarkHolder = (LinearLayout) findViewById(R.id.bookmark_holder);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);

        mBookmarkHolder.setOnClickListener(this);
        mToolBarTitle.setOnClickListener(this);
        findViewById(R.id.view_highlights).setOnClickListener(this);
        findViewById(R.id.view_notes).setOnClickListener(this);
        findViewById(R.id.view_share).setOnClickListener(this);

        getSupportActionBar().setTitle("");
        mToolBarTitle.setText(UtilFunctions.getBookNameFromMapping(this, mBookId));

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
                mToolBarTitle.setOnClickListener(null);
            }

            @Override
            protected Void doInBackground(Void... params) {
                mBookModel = getBookModel(mBookId);

                if (mBookModel != null) {
                    for (ChapterModel model : mBookModel.getChapterModels()) {
                        mChapterModels.add(model);
                    }
                    mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressBar.setVisibility(View.GONE);

                mToolBarTitle.setOnClickListener(BookActivity.this);
                if (mBookMarkNumber == 1) {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                }

                String verseNumber = getIntent().getStringExtra(Constants.Keys.VERSE_NO);
                int chapterNumber = getIntent().getIntExtra(Constants.Keys.CHAPTER_NO, 0);

                mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));
            }
        }.execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.list_chapters);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChapterAdapter(this, mChapterModels, SharedPrefs.getFontSize());
        mRecyclerView.setAdapter(mAdapter);

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

    private BookModel getBookModel(String bookId) {
        final Realm realm = Realm.getDefaultInstance();
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<BookModel> resultList = query(realm, new AllSpecifications.BookModelById(languageCode, versionCode, bookId), new AllMappers.BookMapper());
        if (resultList.size() > 0) {
            BookModel bModel = resultList.get(0);
            BookModel bookModel = new BookModel();
            bookModel.setBookmarkChapterNumber(bModel.getBookmarkChapterNumber());
            bookModel.setBookNumber(bModel.getBookNumber());
            bookModel.setSection(bModel.getSection());
            bookModel.setVersionCode(bModel.getVersionCode());
            bookModel.setLanguageCode(bModel.getLanguageCode());
            bookModel.setBookPrimaryId(bModel.getBookPrimaryId());
            bookModel.setBookId(bModel.getBookId());
            bookModel.setBookName(bModel.getBookName());
            for (ChapterModel cModel : bModel.getChapterModels()) {
                ChapterModel chapterModel = new ChapterModel();
                chapterModel.setChapterNumber(cModel.getChapterNumber());
                chapterModel.setLanguageCode(cModel.getLanguageCode());
                chapterModel.setVersionCode(cModel.getVersionCode());
                chapterModel.setChapterId(cModel.getChapterId());
                chapterModel.setNumberOfVerses(cModel.getNumberOfVerses());
                for (VerseComponentsModel vModel : cModel.getVerseComponentsModels()) {
                    VerseComponentsModel verseComponentsModel = new VerseComponentsModel();
                    verseComponentsModel.setChapterId(vModel.getChapterId());
                    verseComponentsModel.setHighlighted(vModel.isHighlighted());
                    verseComponentsModel.setVersionCode(vModel.getVersionCode());
                    verseComponentsModel.setLanguageCode(vModel.getLanguageCode());
                    verseComponentsModel.setText(vModel.getText());
                    verseComponentsModel.setVerseNumber(vModel.getVerseNumber());
                    verseComponentsModel.setType(vModel.getType());
                    chapterModel.getVerseComponentsModels().add(verseComponentsModel);
                }
                bookModel.getChapterModels().add(chapterModel);
            }
            realm.close();
            return bookModel;
        }
        realm.close();
        return null;
    }

    public ArrayList<BookModel> query(Realm realm, Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
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
        // TODO see how to do this in background

        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setHighlighted(true);

                    mBookModel.getChapterModels().get(i).getVerseComponentsModels().get(j).setHighlighted(true);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        new AsyncTask<BookModel, Void, Void>() {
            @Override
            protected Void doInBackground(BookModel... params) {
                new AutographaRepository<BookModel>().update(params[0]);
                return null;
            }
        }.execute(mBookModel);
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
                    shareBody.append(UtilFunctions.getPlainVerseText(verseComponentsModel.getText()));
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
            size = size + mChapterModels.get(i).getNumberOfVerses();
            if (position > size - 1) {
                continue;
            } else { // pos <= num
                return mChapterModels.get(i).getChapterNumber();
            }
        }
        return 1;
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
            case R.id.bookmark_holder: {

//                BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
//                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
//                myAnim.setInterpolator(interpolator);
//                mIvBookMark.startAnimation(myAnim);

                mIvBookMark.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));

                int position = mLayoutManager.findFirstVisibleItemPosition();

                new AsyncTask<Integer, Void, Void>(){
                    @Override
                    protected Void doInBackground(Integer... params) {
                        mBookMarkNumber = findChapterNumber(params[0]);

                        for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
                            if (bookIdModel.getBookId().equals(mBookId)) {
                                bookIdModel.setBookmarkChapterNumber(mBookMarkNumber);
                                break;
                            }
                        }
                        mBookModel.setBookmarkChapterNumber(mBookMarkNumber);
                        new AutographaRepository<BookModel>().update(mBookModel);

                        return null;
                    }
                }.execute(position);
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
            case R.id.toolbar_title: {
                Intent intent = new Intent(this, SelectChapterAndVerseActivity.class);
                intent.putExtra(Constants.Keys.SELECT_VERSE_FOR_NOTE, true);
                intent.putExtra(Constants.Keys.OPEN_BOOK, true);
                intent.putExtra(Constants.Keys.BOOK_ID, Constants.CONTAINER_BOOKS_LIST.get(0).getBookId());
                startActivityForResult(intent, Constants.RequestCodes.CHANGE_BOOK);
                break;
            }
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.RequestCodes.CHANGE_BOOK: {
                if (resultCode == RESULT_OK) {
                    VerseIdModel model = data.getParcelableExtra(Constants.Keys.VERSE_NOTE_MODEL);

                    String verseNumber = model.getVerseNumber();
                    int chapterNumber = model.getChapterNumber();
                    mBookId = model.getBookId();

                    mBookModel = getBookModel(mBookId);

                    if (mBookModel != null) {
                        mToolBarTitle.setText(mBookModel.getBookName());

                        mChapterModels.clear();
                        for (ChapterModel chapterModel : mBookModel.getChapterModels()) {
                            mChapterModels.add(chapterModel);
                        }
                        mAdapter.notifyDataSetChanged();
                        mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
                    }

                    if (mBookMarkNumber == 1) {
                        mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                    }

                    mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));
                }
                break;
            }
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