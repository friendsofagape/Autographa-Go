package com.bridgeconn.autographago.ormutils;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface Specification<T extends RealmObject> {
    RealmResults<T> generateResults(Realm realm);
}
