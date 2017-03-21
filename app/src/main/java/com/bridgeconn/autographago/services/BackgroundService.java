package com.bridgeconn.autographago.services;

import android.app.IntentService;
import android.content.Intent;

import com.bridgeconn.autographago.models.SearchHistoryModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super(BackgroundService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(Constants.ACTION.ADD_TO_HISTORY)) {
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
        else if (intent.getAction().equals(Constants.ACTION.UPDATE_SEARCH_HISTORY)) {
            String text = intent.getStringExtra(Constants.Keys.TEXT);
            final Realm realm = Realm.getDefaultInstance();
            ArrayList<SearchHistoryModel> resultList = querySearchHistory(realm, new AllSpecifications.SearchHistoryModelByText(text), new AllMappers.SearchHistoryMapper());

            SearchHistoryModel model = new SearchHistoryModel();
            model.setLastSearchTime(System.currentTimeMillis());
            model.setSearchText(text);

            if (resultList.size() > 0) {
                model.setSearchCount(resultList.get(0).getSearchCount() + 1);
                new AutographaRepository<SearchHistoryModel>().update(model);
            } else {
                model.setSearchCount(1);
                new AutographaRepository<SearchHistoryModel>().add(model);
            }
            realm.close();
        }
    }

    private ArrayList<SearchHistoryModel> querySearchHistory(Realm realm, Specification<SearchHistoryModel> specification, Mapper<SearchHistoryModel, SearchHistoryModel> mapper) {
        RealmResults<SearchHistoryModel> realmResults = specification.generateResults(realm);
        ArrayList<SearchHistoryModel> resultsToReturn = new ArrayList<>();
        for (SearchHistoryModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }
}