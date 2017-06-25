package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.customviews.TabLayoutHelper;
import com.bridgeconn.autographago.ui.fragments.LanguageFragment;
import com.bridgeconn.autographago.ui.fragments.VersionFragment;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectLanguageAndVersionActivity extends AppCompatActivity {

    private ArrayList<String> Tabs = new ArrayList<>();
    private ViewPagerAdapter mAdapter;
    private TabLayoutHelper mTabLayoutHelper;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private boolean mSelectBook;
    private ArrayList<LanguageModel> languageModelArrayList = new ArrayList<>();
    private ArrayList<VersionModel> versionModelArrayList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(String languageCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_language_and_version);

        UtilFunctions.applyReadingMode();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        mSelectBook = intent.getBooleanExtra(Constants.Keys.SELECT_BOOK, false);

        Tabs.add(Constants.LanguageTabs.LANGUAGE);
        Tabs.add(Constants.LanguageTabs.VERSION);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Tabs, mSelectBook);
        mViewPager.setAdapter(mAdapter);

        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, mViewPager);
        mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);

        getLanguagesFromDB();
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
    public void onPause() {
        super.onPause();
        if (mViewPager.getAdapter() == null) {
            return;
        }
        if (mTabLayoutHelper != null) {
            mTabLayoutHelper.release();
            mTabLayoutHelper = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewPager.getAdapter() == null) {
            return;
        }
        if (mTabLayoutHelper == null) {
            mTabLayoutHelper = new TabLayoutHelper(mTabLayout, mViewPager);
            mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewPager.getAdapter() == null) {
            return;
        }
        if (mTabLayoutHelper != null) {
            mTabLayoutHelper.release();
            mTabLayoutHelper = null;
        }
    }

    static LanguageFragment languageFragment;
    static VersionFragment versionFragment;

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TabsList;
        private boolean mSelectBook;

        public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> tabs, boolean selectBook) {
            super(fragmentManager);
            TabsList = tabs;
            mSelectBook = selectBook;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.Keys.SELECT_BOOK, mSelectBook);
            if (getPageTitle(position).equals(Constants.LanguageTabs.LANGUAGE)) {
                languageFragment = new LanguageFragment();
                languageFragment.setArguments(bundle);
                return languageFragment;
            } else {
                versionFragment = new VersionFragment();
                versionFragment.setArguments(bundle);
                return versionFragment;
            }
        }

        @Override
        public int getCount() {
            return TabsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TabsList.get(position);
        }

    }

    public void openVersionPage(String languageCode) {
        mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition() + 1).select();
        versionFragment.onItemClick(languageCode);
    }

    public void reattachFragment() {
        mAdapter.notifyDataSetChanged();
    }

    public String getSelectedLanguageCode() {
        return languageFragment.getSelectedLanguageCode();
    }

    public String getSelectedLanguageName() {
        return languageFragment.getSelectedLanguageName();
    }

    private void getLanguagesFromDB() {
        final Realm realm = Realm.getDefaultInstance();
        ArrayList<LanguageModel> resultList = queryLanguages(realm, new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (LanguageModel languageModel : resultList) {
            LanguageModel lModel = new LanguageModel();
            lModel.setLanguageCode(languageModel.getLanguageCode());
            lModel.setLanguageName(languageModel.getLanguageName());
            for (VersionModel versionModel : languageModel.getVersionModels()) {
                VersionModel vModel = new VersionModel();
                vModel.setLanguageCode(versionModel.getLanguageCode());
                vModel.setVersionCode(versionModel.getVersionCode());
                vModel.setVersionId(versionModel.getVersionId());
                vModel.setVersionName(versionModel.getVersionName());
                lModel.getVersionModels().add(vModel);
            }
            languageModelArrayList.add(lModel);
        }
        realm.close();
    }

    private ArrayList<LanguageModel> queryLanguages(Realm realm, Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    public ArrayList<LanguageModel> getLanguageModelArrayList() {
        return languageModelArrayList;
    }

    public ArrayList<VersionModel> getVersionModelArrayList() {
        return versionModelArrayList;
    }

    public void setVersionModelArrayList(String languageCode) {
        for (LanguageModel languageModel : languageModelArrayList) {
            if (languageModel.getLanguageCode().equalsIgnoreCase(languageCode)) {
                this.versionModelArrayList.clear();
                for (VersionModel vModel : languageModel.getVersionModels()) {
                    VersionModel versionModel = new VersionModel();
                    versionModel.setLanguageCode(vModel.getLanguageCode());
                    versionModel.setVersionCode(vModel.getVersionCode());
                    versionModel.setVersionId(vModel.getVersionId());
                    versionModel.setVersionName(vModel.getVersionName());
                    this.versionModelArrayList.add(versionModel);
                }
                break;
            }
        }
    }
}