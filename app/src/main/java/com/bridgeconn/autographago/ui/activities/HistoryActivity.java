package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ExpandableHistoryModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.adapters.HistoryExpandableAdapter;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    //    private HistoryAdapter mAdapter;
    private HistoryExpandableAdapter mAdapter;

    //    private ArrayList<SearchModel> mHistoryModels = new ArrayList<>();
    private List<ExpandableHistoryModel> mHistoryModels = new ArrayList<>();
    private String languageCode, versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UtilFunctions.applyReadingMode();

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getAllHistory();

        mRecyclerView = (RecyclerView) findViewById(R.id.list_history);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mAdapter = new HistoryAdapter(this, mHistoryModels);
//        mAdapter = new HistoryExpandableAdapter(this, mHistoryModels);
//        mRecyclerView.setAdapter(mAdapter);


        if (mAdapter == null) {
            if (mHistoryModels.size() > 0) {
                mAdapter = new HistoryExpandableAdapter(this, mHistoryModels);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.onGroupClick(0);
//                mAdapter.notifyDataSetChanged();
            }
        }


        findViewById(R.id.clear_history).setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null)
            mAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mAdapter != null)
            mAdapter.onRestoreInstanceState(savedInstanceState);
    }

    private void getAllHistory() {
        mHistoryModels.clear();
        ArrayList<SearchModel> todayModels = new ArrayList<>();
        ArrayList<SearchModel> yesterdayModels = new ArrayList<>();
        ArrayList<SearchModel> oneWeekModels = new ArrayList<>();
        ArrayList<SearchModel> oneMonthModels = new ArrayList<>();
        ArrayList<SearchModel> twoMonthsModels = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        Calendar historyTime = Calendar.getInstance();
        Calendar yester = Calendar.getInstance(); // today
        yester.add(Calendar.DAY_OF_YEAR, -1); // yesterday
        long weekMillis = 1000 * 60 * 60 * 24 * 7;
        long oneMonthMillis = weekMillis * 30;
//        long twoMonthsMillis = oneMonthMillis * 2 ;

        final Realm realm = Realm.getDefaultInstance();
        ArrayList<SearchModel> models = query(realm, new AllSpecifications.AllHistory(languageCode, versionCode), new AllMappers.HistoryMapper());
        for (SearchModel model : models) {
            SearchModel searchModel = new SearchModel();
            searchModel.setBookName(model.getBookName());
            searchModel.setBookId(model.getBookId());
            searchModel.setChapterNumber(model.getChapterNumber());
            searchModel.setVerseNumber(model.getVerseNumber());
            searchModel.setTimeStamp(model.getTimeStamp());

            historyTime.setTimeInMillis(searchModel.getTimeStamp());

            if (DateUtils.isToday(searchModel.getTimeStamp())) {
                todayModels.add(searchModel);
            } else if (yester.get(Calendar.YEAR) == historyTime.get(Calendar.YEAR)
                    && yester.get(Calendar.DAY_OF_YEAR) == historyTime.get(Calendar.DAY_OF_YEAR)) {
                yesterdayModels.add(searchModel);
            } else if (now.getTimeInMillis() - searchModel.getTimeStamp() <= weekMillis) {
                oneWeekModels.add(searchModel);
            } else if (now.getTimeInMillis() - searchModel.getTimeStamp() <= oneMonthMillis) {
                oneMonthModels.add(searchModel);
            } else {//if (now.getTimeInMillis() - searchModel.getTimeStamp() <= twoMonthsMillis) {
                twoMonthsModels.add(searchModel);
            }
        }
        realm.close();

        if (todayModels.size() > 0)
            mHistoryModels.add(new ExpandableHistoryModel(getResources().getString(R.string.today), todayModels));
        if (yesterdayModels.size() > 0)
            mHistoryModels.add(new ExpandableHistoryModel(getResources().getString(R.string.yesterday), yesterdayModels));
        if (oneWeekModels.size() > 0)
            mHistoryModels.add(new ExpandableHistoryModel(getResources().getString(R.string.one_week_ago), oneWeekModels));
        if (oneMonthModels.size() > 0)
            mHistoryModels.add(new ExpandableHistoryModel(getResources().getString(R.string.one_month_ago), oneMonthModels));
        if (twoMonthsModels.size() > 0)
            mHistoryModels.add(new ExpandableHistoryModel(getResources().getString(R.string.two_months_ago), twoMonthsModels));
    }

    public ArrayList<SearchModel> query(Realm realm, Specification<SearchModel> specification, Mapper<SearchModel, SearchModel> mapper) {
        RealmResults<SearchModel> realmResults = specification.generateResults(realm);
        ArrayList<SearchModel> resultsToReturn = new ArrayList<>();
        for (SearchModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
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
            case R.id.clear_history: {
                new AutographaRepository<SearchModel>().remove(new AllSpecifications.AllHistory(languageCode, versionCode));
                mHistoryModels.clear();
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
                break;
            }
        }
    }
}