package com.bridgeconn.autographago.ormutils;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;

import io.realm.RealmObject;

public interface Repository<T extends RealmObject> {
    void add(T item);

    void add(T item, RepositoryRequestListener listener);

    @UiThread
    void add(Iterable<T> items, RepositoryRequestListener listener);

    @WorkerThread
    void add(Iterable<T> items);

    void update(T item);

    void remove(Specification<T> specification);

    ArrayList<T> query(Specification<T> specification, Mapper<T, T> mapper);

    int count(Specification<T> specification);

    void addToNewContainer(String language, String version);
}