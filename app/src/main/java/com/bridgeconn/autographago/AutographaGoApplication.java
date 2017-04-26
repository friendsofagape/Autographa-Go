package com.bridgeconn.autographago;

import android.app.Application;

import com.bridgeconn.autographago.utils.SharedPrefs;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class AutographaGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
                        RealmSchema schema = realm.getSchema();
                        if (oldVersion == 1) {
                            RealmObjectSchema intSchema = schema.create("RealmInteger")
                                    .addField("value", int.class);

                            // change "int bookmarkChapterNumber" to array of integers
                            schema.get("BookModel")
                                    .addRealmListField("bookmarksList", intSchema)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            if (obj.getInt("bookmarkChapterNumber") > 0) {
                                                DynamicRealmObject intValue = realm.createObject("RealmInteger");
                                                intValue.setInt("value", obj.getInt("bookmarkChapterNumber"));
                                                obj.getList("bookmarksList").add(intValue);
                                            }
                                        }
                                    })
                                    .removeField("bookmarkChapterNumber");
                        }
                    }
                })
                .assetFile("default.realm")
                .schemaVersion(2) // TODO increment schema version here
                .build();
        Realm.setDefaultConfiguration(config);

        SharedPrefs.init(this);
    }
}