package com.bridgeconn.autographago.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.USFMParser;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);

        String [] fileNames = {Constants.USFMFiles.HOSEA, Constants.USFMFiles._3_JOHN, Constants.USFMFiles.ZEPHANIAH};

        for (int i=0; i<fileNames.length; i++) {
            ArrayList<BookModel> bookModels = new AutographaRepository<BookModel>().query(new AllSpecifications.BookModelById(fileNames[i]), new AllMappers.BookMapper());
            if (bookModels.size() > 0) {
                // already in db
            } else {
                // add to db
                USFMParser usfmParser = new USFMParser();
                usfmParser.parseUSFMFile(this, fileNames[i], true, "English", "ULB", "Unlocked Literal Bible");
            }
        }

        new AutographaRepository<BookModel>().addToContainer();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        finish();

    }
}