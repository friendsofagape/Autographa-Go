package com.bridgeconn.autographago.ormutils;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseIdModel;
import com.bridgeconn.autographago.models.VersionModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;

public interface Repository<T extends RealmObject> {
    void add(T item);

    void add(T item, RepositoryRequestListener listener);

    @UiThread
    void add(Iterable<T> items, RepositoryRequestListener listener);

    @WorkerThread
    void add(Iterable<T> items);

    void update(T item);

    void updateLanguageWithVersion(Realm realm, LanguageModel languageModel, VersionModel versionModel);

    void updateLanguageWithBook(Realm realm, LanguageModel languageModel, int position, BookModel bookModel);

    void updateBookWithHighlights(Realm realm, ArrayList<LanguageModel> languageModels, ArrayList<VerseIdModel> verseIdModels);

    void remove(Specification<T> specification);

    ArrayList<T> query(Specification<T> specification, Mapper<T, T> mapper);

    int count(Specification<T> specification);

    void addToNewContainer(String language, String version);
}