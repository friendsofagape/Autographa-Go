package com.bridgeconn.autographago;

import android.app.Application;

import com.bridgeconn.autographago.utils.SharedPrefs;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class AutographaGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                    }
                })
                .assetFile("default.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);

        SharedPrefs.init(this);
    }
}