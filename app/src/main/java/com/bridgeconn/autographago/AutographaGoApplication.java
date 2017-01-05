package com.bridgeconn.autographago;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AutographaGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}