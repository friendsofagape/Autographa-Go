package com.bridgeconn.autographago.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.BookIdModel;
import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.RealmInteger;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.ChapterAdapter;
import com.bridgeconn.autographago.ui.adapters.DownloadDialogAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChapterAdapter mAdapter;
    private ArrayList<ChapterModel> mChapterModels = new ArrayList<>();
    private String mVersionName, mLicense;
    private int mYear;

    private BookModel mBookModel;
    private TextView mToolBarTitle, mToolbarBookVersion;
    private RelativeLayout mBookmarkHolder;
    private ImageView mIvBookMark;
    private String mBookId;
    //    private int mBookMarkNumber;
    private ArrayList<Integer> mBookMarkList = new ArrayList<>();
    private ArrayList<NotesModel> mNotesModels = new ArrayList<>();

    private LinearLayout mBottomBar;
    private ProgressBar mProgressBar;
    private TextView mTvHighlight;

    private ScaleGestureDetector mScaleGestureDetector;

    // TODO BUG: when click on toolbar title, book name, then select book/chapter/verse this does not change the title of the activity

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
        mToolbarBookVersion = (TextView) findViewById(R.id.book_version);
        mIvBookMark = (ImageView) findViewById(R.id.iv_bookmark);
        mBookmarkHolder = (RelativeLayout) findViewById(R.id.bookmark_holder);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mTvHighlight = (TextView) findViewById(R.id.tv_highlight);

        mBookmarkHolder.setOnClickListener(this);
        mToolBarTitle.setOnClickListener(this);
        mToolbarBookVersion.setOnClickListener(this);
        findViewById(R.id.view_highlights).setOnClickListener(this);
        findViewById(R.id.view_notes).setOnClickListener(this);
        findViewById(R.id.view_share).setOnClickListener(this);

        getSupportActionBar().setTitle("");
        mToolBarTitle.setText(UtilFunctions.getBookNameFromMapping(this, mBookId) + " " + getIntent().getIntExtra(Constants.Keys.CHAPTER_NO, 0)
//                + " " + SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));
        );
        mToolbarBookVersion.setText(SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));

        findCopyrightData();

        mRecyclerView = (RecyclerView) findViewById(R.id.list_chapters);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChapterAdapter(this, mChapterModels, SharedPrefs.getFontSize(), mVersionName, mLicense, mYear);
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
                if (mBookMarkList.contains(chapterNum)) {
//                if (mBookMarkNumber > 0) {
//                    if (chapterNum == mBookMarkNumber) {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                } else {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.white));
                }
//                }
                mToolBarTitle.setText(UtilFunctions.getBookNameFromMapping(BookActivity.this, mBookId) + " " + chapterNum
//                        + " " + SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));
                );
                mToolbarBookVersion.setText(SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));
            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
                mToolBarTitle.setOnClickListener(null);
            }

            @Override
            protected Void doInBackground(Void... params) {
                getNotesFromDB();

                mBookModel = getBookModel(mBookId);

                if (mBookModel != null) {
                    for (ChapterModel model : mBookModel.getChapterModels()) {
                        mChapterModels.add(model);
                    }
                    for (RealmInteger realmInteger : mBookModel.getBookmarksList()) {
                        mBookMarkList.add(realmInteger.getValue());
                    }
//                    mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressBar.setVisibility(View.GONE);

                mToolBarTitle.setOnClickListener(BookActivity.this);
                if (mBookMarkList.contains(1)) {
//                if (mBookMarkNumber == 1) {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                }

                String verseNumber = getIntent().getStringExtra(Constants.Keys.VERSE_NO);
                int chapterNumber = getIntent().getIntExtra(Constants.Keys.CHAPTER_NO, 0);

                mLayoutManager.scrollToPositionWithOffset(findPositionToScroll(chapterNumber-1, verseNumber), 0);
//                mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));
            }
        }.execute();

        addGesture();
    }

    private void findCopyrightData() {
        final Realm realm = Realm.getDefaultInstance();

        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<VersionModel> results = queryVersion(realm, new AllSpecifications.VersionModelByCode(languageCode, versionCode), new AllMappers.VersionMapper());

        if (results.size() > 0) {
            mVersionName = results.get(0).getVersionName();
            mLicense = results.get(0).getLicense();
            mYear = results.get(0).getYear();
        }
        realm.close();
    }

    private void getNotesFromDB() {
        final Realm realm = Realm.getDefaultInstance();
        mNotesModels.clear();
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<NotesModel> models = query(realm, new AllSpecifications.AllNotes(languageCode, versionCode), new AllMappers.NotesMapper());
        for (NotesModel model : models) {
            for (VerseIdModel verseIdModel : model.getVerseIds()) {
                if (verseIdModel.getBookId().equals(mBookId)) {
                    NotesModel notesModel = new NotesModel();
                    notesModel.setTimestamp(model.getTimestamp());

                    VerseIdModel vIdModel = new VerseIdModel();
                    vIdModel.setChapterNumber(verseIdModel.getChapterNumber());
                    vIdModel.setVerseNumber(verseIdModel.getVerseNumber());
                    notesModel.getVerseIds().add(vIdModel);
                    mNotesModels.add(notesModel);
                }
            }
        }
        realm.close();
    }

    private ArrayList<VersionModel> queryVersion(Realm realm, Specification<VersionModel> specification, Mapper<VersionModel, VersionModel> mapper) {
        RealmResults<VersionModel> realmResults = specification.generateResults(realm);
        ArrayList<VersionModel> resultsToReturn = new ArrayList<>();
        for (VersionModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    private ArrayList<NotesModel> query(Realm realm, Specification<NotesModel> specification, Mapper<NotesModel, NotesModel> mapper) {
        RealmResults<NotesModel> realmResults = specification.generateResults(realm);
        ArrayList<NotesModel> resultsToReturn = new ArrayList<>();
        for (NotesModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    private void addGesture() {
        //set scale gesture detector
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
//                if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                    switch (SharedPrefs.getFontSize()) {
                        case XSmall: {
                            return false;
                        }
                        case Small: {
                            SharedPrefs.putFontSize(Constants.FontSize.XSmall);
                            recreate();
                            return true;
                        }
                        case Medium: {
                            SharedPrefs.putFontSize(Constants.FontSize.Small);
                            recreate();
                            return true;
                        }
                        case Large: {
                            SharedPrefs.putFontSize(Constants.FontSize.Medium);
                            recreate();
                            return true;
                        }
                        case XLarge: {
                            SharedPrefs.putFontSize(Constants.FontSize.Large);
                            recreate();
                            return true;
                        }
                    }
                } else if(detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                    switch (SharedPrefs.getFontSize()) {
                        case XSmall: {
                            SharedPrefs.putFontSize(Constants.FontSize.Small);
                            recreate();
                            return true;
                        }
                        case Small: {
                            SharedPrefs.putFontSize(Constants.FontSize.Medium);
                            recreate();
                            return true;
                        }
                        case Medium: {
                            SharedPrefs.putFontSize(Constants.FontSize.Large);
                            recreate();
                            return true;
                        }
                        case Large: {
                            SharedPrefs.putFontSize(Constants.FontSize.XLarge);
                            recreate();
                            return true;
                        }
                        case XLarge: {
                            return false;
                        }
                    }
                }
//                }
                return false;
            }
        });

        //set touch listener on recycler view
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private BookModel getBookModel(String bookId) {
        final Realm realm = Realm.getDefaultInstance();
        String languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        String versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);
        ArrayList<BookModel> resultList = queryBooks(realm, new AllSpecifications.BookModelById(languageCode, versionCode, bookId), new AllMappers.BookMapper());
        if (resultList.size() > 0) {
            BookModel bModel = resultList.get(0);
            BookModel bookModel = new BookModel();
            for (RealmInteger realmInteger : bModel.getBookmarksList()) {
                RealmInteger realmValue = new RealmInteger();
                realmValue.setValue(realmInteger.getValue());
                bookModel.getBookmarksList().add(realmValue);
            }
//            bookModel.setBookmarkChapterNumber(bModel.getBookmarkChapterNumber());
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
                    for (NotesModel notesModel : mNotesModels) {
                        if (notesModel.getVerseIds().get(0).getChapterNumber() == cModel.getChapterNumber()
                                && notesModel.getVerseIds().get(0).getVerseNumber().equals(vModel.getVerseNumber())) {
                            verseComponentsModel.getNotesTimestamps().add(notesModel.getTimestamp());
                        }
                    }
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

    public ArrayList<BookModel> queryBooks(Realm realm, Specification<BookModel> specification, Mapper<BookModel, BookModel> mapper) {
        RealmResults<BookModel> realmResults = specification.generateResults(realm);
        ArrayList<BookModel> resultsToReturn = new ArrayList<>();
        for (BookModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    public ArrayList<LanguageModel> queryLanguage(Realm realm, Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
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
        // remove highlight option also
        boolean removeHighlight = false;
        if (mTvHighlight.getText().equals(getResources().getString(R.string.remove_highlight))) {
            removeHighlight = true;
        }
        final ArrayList<VerseIdModel> verseIdModels = new ArrayList<>();

        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    mChapterModels.get(i).getVerseComponentsModels().get(j).setSelected(false);
                    VerseIdModel verseIdModel = new VerseIdModel();
                    verseIdModel.setBookId(mBookModel.getBookId());
                    verseIdModel.setBookName(mBookModel.getBookName());
                    verseIdModel.setChapterNumber(mChapterModels.get(i).getChapterNumber());
                    verseIdModel.setVerseNumber(mChapterModels.get(i).getVerseComponentsModels().get(j).getVerseNumber());
                    if (removeHighlight) {
                        mChapterModels.get(i).getVerseComponentsModels().get(j).setHighlighted(false);
                        verseIdModel.setTimeStamp(0);
//                        mBookModel.getChapterModels().get(i).getVerseComponentsModels().get(j).setHighlighted(false);
                    } else {
                        mChapterModels.get(i).getVerseComponentsModels().get(j).setHighlighted(true);
                        verseIdModel.setTimeStamp(1);
//                        mBookModel.getChapterModels().get(i).getVerseComponentsModels().get(j).setHighlighted(true);
                    }
                    verseIdModels.add(verseIdModel);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        new AsyncTask<ArrayList<VerseIdModel>, Void, Void>() {
            @Override
            protected Void doInBackground(ArrayList<VerseIdModel>... params) {
//                new AutographaRepository<BookModel>().update(params[0]);

                Realm realm = Realm.getDefaultInstance();
                ArrayList<LanguageModel> languageModels = queryLanguage(realm, new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
                new AutographaRepository<VerseComponentsModel>().updateBookWithHighlights(realm, languageModels, params[0]);
                realm.close();
                // update this in all versions and languages
                return null;
            }
        }.execute(verseIdModels);
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

//        Intent intent = new Intent(this, EditNoteActivity.class);
        Intent intent = new Intent(this, NotesActivity.class);
        int position = mLayoutManager.findFirstVisibleItemPosition();
        int chapterNumber = findChapterNumber(position);
        String verseNo = findVerseNumber(position);
        intent.putExtra(Constants.Keys.CHAPTER_NO, chapterNumber);
        intent.putExtra(Constants.Keys.VERSE_NO, verseNo);
        intent.putParcelableArrayListExtra(Constants.Keys.VERSE_MODELS, models);
        startActivityForResult(intent, Constants.RequestCodes.ADD_TO_NOTE);
    }

    private void findSelectedAndShare() {
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
                    String text = mChapterModels.get(i).getVerseComponentsModels().get(j).getText();
                    if (text == null) {
                        text = "";
                    }
                    if (models.contains(verseIdModel)) {
                        int pos = models.indexOf(verseIdModel);
                        String appendtext = models.get(pos).getText() + text;
                        models.get(pos).setText(appendtext);
                    } else {
                        verseIdModel.setText(text);
                        models.add(verseIdModel);
                    }
                }
            }
        }
        StringBuilder shareBody = new StringBuilder();
        for (VerseIdModel verseIdModel : models) {
            shareBody.append(verseIdModel.getBookName() + " " + verseIdModel.getChapterNumber() + ":" + verseIdModel.getVerseNumber() + " ");
            shareBody.append(UtilFunctions.getPlainVerseText(verseIdModel.getText()));
            shareBody.append("\n");
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

                int position = mLayoutManager.findFirstVisibleItemPosition();
                final int chapterNumber = findChapterNumber(position);
                boolean present = false;
                if (mBookMarkList.contains(chapterNumber)) {
                    present = true;
//                if (mBookMarkNumber == findChapterNumber(position)) {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.white));
                    int index1 = mBookMarkList.indexOf(chapterNumber);
                    mBookMarkList.remove(index1);

                    for (int i=0; i<mBookModel.getBookmarksList().size(); i++) {
                        if (mBookModel.getBookmarksList().get(i).getValue() == chapterNumber) {
                            mBookModel.getBookmarksList().remove(i);
                            break;
                        }
                    }
                } else {
                    mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                    mBookMarkList.add(chapterNumber);
                    RealmInteger realmInteger = new RealmInteger(chapterNumber);
                    mBookModel.getBookmarksList().add(realmInteger);
                }
                new AutographaRepository<BookModel>().add(mBookModel, null);

                for (BookIdModel bookIdModel : Constants.CONTAINER_BOOKS_LIST) {
                    if (bookIdModel.getBookId().equals(mBookId)) {
                        if (present) {
                            int index = bookIdModel.getBookmarksList().indexOf(chapterNumber);
                            bookIdModel.getBookmarksList().remove(index);
                        } else {
                            bookIdModel.getBookmarksList().add(chapterNumber);
                        }
                        break;
                    }
                }
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
            case R.id.book_version: {
                String languageName = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_NAME, "English");
                showAvailableVersionsDialog(languageName);
                break;
            }
        }
    }

    private ArrayList<LanguageModel> queryVersions(Realm realm, Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    private void showAvailableVersionsDialog(String language) {
        List<String> versions = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        ArrayList<LanguageModel> languageModels = queryVersions(realm, new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (LanguageModel languageModel : languageModels) {
            if (languageModel.getLanguageName().equalsIgnoreCase(language)) {
                for (VersionModel versionModel : languageModel.getVersionModels()) {
                    versions.add(versionModel.getVersionCode());
                }
            }
        }
        realm.close();
        if (isFinishing()) {
            return;
        }

        if (versions.size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.no_new_versions_available), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = null;
        switch (SharedPrefs.getReadingMode()) {
            case Day: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeLight);
                break;
            }
            case Night: {
                builder = new AlertDialog.Builder(this, R.style.DialogThemeDark);
                break;
            }
        }

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_languages, (ViewGroup) findViewById(android.R.id.content), false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        builder.setView(view);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int maxHeight = UtilFunctions.dpToPx(BookActivity.this, 300);

            @Override
            public void onGlobalLayout() {
                if (view.getHeight() > maxHeight)
                    view.getLayoutParams().height = maxHeight;
            }
        });

        builder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(getString(R.string.select_version));

        AlertDialog dialog = builder.create();
        dialog.show();

        DownloadDialogAdapter dialogAdapter = new DownloadDialogAdapter(this, dialog, null, versions, language);
        recyclerView.setAdapter(dialogAdapter);
    }

    public void changeLanguageVersionOfBook(String language, String version) {
        // TODO here change vesion in book
        // TODO also save last open version to shared orefs
        // TODO also update home activity about this
    }

    public void showBottomBar() {
        // here see highlight and change text of bottom bar of highlight
        int selectedCount = 0, highlightCount = 0;
        for (int i=0;  i<mChapterModels.size(); i++) {
            for (int j=0; j<mChapterModels.get(i).getVerseComponentsModels().size(); j++) {
                if (mChapterModels.get(i).getVerseComponentsModels().get(j).isSelected()) {
                    selectedCount++;
                    if (mChapterModels.get(i).getVerseComponentsModels().get(j).isHighlighted()) {
                        highlightCount++;
                    }
                }
            }
        }
        if (selectedCount == highlightCount) {
            // set text to remove highlight
            mTvHighlight.setText(getResources().getString(R.string.remove_highlight));
        } else {
            mTvHighlight.setText(getResources().getString(R.string.highlight_text));
        }

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
        } else {
            showBottomBar();
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

                    getNotesFromDB();

                    mBookModel = getBookModel(mBookId);

                    if (mBookModel != null) {
                        mToolBarTitle.setText(mBookModel.getBookName() + " " + chapterNumber
//                                + " " + SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));
                        );
                        mToolbarBookVersion.setText(SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));

                        mChapterModels.clear();
                        for (ChapterModel chapterModel : mBookModel.getChapterModels()) {
                            mChapterModels.add(chapterModel);
                        }
                        mBookMarkList.clear();
                        for (RealmInteger realmInteger : mBookModel.getBookmarksList()) {
                            mBookMarkList.add(realmInteger.getValue());
                        }
                        mAdapter.notifyDataSetChanged();
//                        mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
                    }

                    if (mBookMarkList.contains(1)) {
//                    if (mBookMarkNumber == 1) {
                        mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                    }

                    mLayoutManager.scrollToPositionWithOffset(findPositionToScroll(chapterNumber-1, verseNumber), 0);
//                    mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));
                }
                break;
            }
            case Constants.RequestCodes.ADD_TO_NOTE: {
                if (resultCode == RESULT_OK) {
                    getNotesFromDB();
                    mBookModel = getBookModel(mBookId);

                    if (mBookModel != null) {
                        mToolBarTitle.setText(mBookModel.getBookName() + " " + data.getIntExtra(Constants.Keys.CHAPTER_NO, 1)
//                                + " " + SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));
                        );
                        mToolbarBookVersion.setText(SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB));

                        mChapterModels.clear();
                        for (ChapterModel chapterModel : mBookModel.getChapterModels()) {
                            mChapterModels.add(chapterModel);
                        }
                        mBookMarkList.clear();
                        for (RealmInteger realmInteger : mBookModel.getBookmarksList()) {
                            mBookMarkList.add(realmInteger.getValue());
                        }
                        mAdapter.notifyDataSetChanged();
//                        mBookMarkNumber = mBookModel.getBookmarkChapterNumber();
                    }

                    if (mBookMarkList.contains(1)) {
//                    if (mBookMarkNumber == 1) {
                        mIvBookMark.setColorFilter(ContextCompat.getColor(BookActivity.this, R.color.colorAccent));
                    }

                    mLayoutManager.scrollToPositionWithOffset(findPositionToScroll(data.getIntExtra(Constants.Keys.CHAPTER_NO, 1)-1, data.getStringExtra(Constants.Keys.VERSE_NO)), 0);
//                    mRecyclerView.scrollToPosition(findPositionToScroll(chapterNumber-1, verseNumber));
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