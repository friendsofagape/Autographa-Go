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

                            /*
                            // add columns to version model
                            RealmObjectSchema versionSchema = schema.get("VersionModel");

                            // Combine 'firstName' and 'lastName' in a new field called 'fullName'
                            versionSchema
                                    .addField("source", String.class)
                                    .addField("license", String.class)
                                    .addField("year", int.class)
                            .transform(new RealmObjectSchema.Function() {
                                @Override
                                public void apply(DynamicRealmObject obj) {
                                    obj.set("source", "Unfolding Word");
                                    obj.set("license", "CCSA");
                                    obj.set("year", 2017);
                                }
                            });
                            */
//                        }
//                        if (oldVersion == 2) {
                            // add columns to version model
                            RealmObjectSchema versionSchema = schema.get("VersionModel");

                            // Combine 'firstName' and 'lastName' in a new field called 'fullName'
                            versionSchema
                                    .addField("source", String.class)
                                    .addField("license", String.class)
                                    .addField("year", int.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("source", "Unfolding Word");
                                            obj.set("license", "CCSA");
                                            obj.set("year", 2017);
                                        }
                                    });
                        }
                    }
                })
                .assetFile("default.realm")
                .schemaVersion(3) // TODO increment schema version here
                .build();
        Realm.setDefaultConfiguration(config);

        SharedPrefs.init(this);
    }
}