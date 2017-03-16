package com.bridgeconn.autographago.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(Constants.ACTION.ADD_TO_HISTORY)) {
            Log.i(Constants.DUMMY_TAG, "Received Start add to history action");

            SearchModel model = new SearchModel();
            model.setVerseNumber(intent.getStringExtra(Constants.Keys.VERSE_NO));
            model.setChapterNumber(intent.getIntExtra(Constants.Keys.CHAPTER_NO, 1));
            model.setLanguageCode(intent.getStringExtra(Constants.Keys.LANGUAGE_CODE));
            model.setVersionCode(intent.getStringExtra(Constants.Keys.VERSION_CODE));
            model.setBookId(intent.getStringExtra(Constants.Keys.BOOK_ID));
            model.setBookName(UtilFunctions.getBookNameFromMapping(BackgroundService.this, model.getBookId()));
            model.setSection(UtilFunctions.getBookSectionFromMapping(BackgroundService.this, model.getBookId()));
            model.setTimeStamp(intent.getLongExtra(Constants.Keys.TIMESTAMP, 0));
            model.setSearchId(model.getBookId() + "_" + model.getChapterNumber() + "_" + model.getVerseNumber());
            new AutographaRepository<SearchModel>().add(model);
        }
    }
}