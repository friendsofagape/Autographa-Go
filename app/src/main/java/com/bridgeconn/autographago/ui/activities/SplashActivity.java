package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.USFMParser;
import com.bridgeconn.autographago.utils.UtilFunctions;

import java.util.ArrayList;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;

public class SplashActivity extends AppCompatActivity {

    private String languageCode, versionCode;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTheme().applyStyle(SharedPrefs.getFontSize().getResId(), true);
        super.onCreate(savedInstanceState);

        UtilFunctions.applyReadingMode();

        // TODO fix memory error crash here
        realm = Realm.getDefaultInstance();

        boolean addToDB = true, startServiceUdb = true, startServiceUlb = true;
        ArrayList<LanguageModel> resultsList = query(new AllSpecifications.AllLanguages(), new AllMappers.LanguageMapper());
        for (LanguageModel languageModel : resultsList) {
            if (languageModel.getLanguageName().equals("English")) {
                for (VersionModel versionModel : languageModel.getVersionModels()) {
                    if (versionModel.getVersionCode().equals(Constants.VersionCodes.ULB)) {
                        if (versionModel.getBookModels().size() == 66) {
                            // all books of this version and languages already in db
                            startServiceUlb = false;
                            addToDB = false;
                        }
                    } else if (versionModel.getVersionCode().equals(Constants.VersionCodes.UDB)) {
                        if (versionModel.getBookModels().size() == 66) {
                            // all books of this version and languages already in db
                            startServiceUdb = false;
                        }
                    }
                }
            }
        }
        realm.close();

        int filesAdded = 0;
        if (addToDB) {
            for (int i = 0; i < Constants.UsfmFileNames.length; i++) {
                USFMParser usfmParser = new USFMParser();
                boolean added = usfmParser.parseUSFMFile(this, "english_ulb/" + Constants.UsfmFileNames[i], true, "English", "ENG", Constants.VersionCodes.ULB);
                if (added) {
                    filesAdded++;
                }
                if (i==65) {
                    startServiceUlb = false;
                }
                if (filesAdded > 5) {
                    break;
                }
            }
        }

        languageCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_LANGUAGE_CODE, "ENG");
        versionCode = SharedPrefs.getString(Constants.PrefKeys.LAST_OPEN_VERSION_CODE, Constants.VersionCodes.ULB);

        new AutographaRepository<LanguageModel>().addToNewContainer(languageCode, versionCode);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.Keys.START_SERVICE_ULB, startServiceUlb);
        intent.putExtra(Constants.Keys.START_SERVICE_UDB, startServiceUdb);
        startActivity(intent);

        finish();
    }

    private ArrayList<LanguageModel> query(Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);

        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();

        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }

    private void doRealmMigration() {
        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                // here do the required migration
            }
        };

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(2) // Must be bumped when the schema changes
                .migration(migration) // Migration to run
                .build();

        Realm.setDefaultConfiguration(config);
        // This will automatically trigger the migration if needed
    }
}