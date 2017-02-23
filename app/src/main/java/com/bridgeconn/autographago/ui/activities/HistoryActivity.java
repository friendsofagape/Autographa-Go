package com.bridgeconn.autographago.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.adapters.HistoryAdapter;
import com.bridgeconn.autographago.utils.SharedPrefs;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private ArrayList<SearchModel> mHistoryModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_history);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HistoryAdapter(this, mHistoryModels);
        mRecyclerView.setAdapter(mAdapter);

        getAllHistory();
    }

    private void getAllHistory() {
        ArrayList<SearchModel> models = new AutographaRepository<SearchModel>().query(new AllSpecifications.AllHistory(), new AllMappers.HistoryMapper());
        for (SearchModel model : models) {
            mHistoryModels.add(model);
        }
        mAdapter.notifyDataSetChanged();
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

}