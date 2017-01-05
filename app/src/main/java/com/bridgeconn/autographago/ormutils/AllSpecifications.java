package com.bridgeconn.autographago.ormutils;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AllSpecifications {

    public static class AllLanguages implements Specification<LanguageModel> {
        @Override
        public RealmResults<LanguageModel> generateResults(Realm realm) {
            return realm.where(LanguageModel.class).findAllSorted("languageCode");
        }
    }

    public static class AllVersions implements Specification<VersionModel> {
        @Override
        public RealmResults<VersionModel> generateResults(Realm realm) {
            return realm.where(VersionModel.class).findAll();
        }
    }

    public static class AllBooks implements Specification<BookModel> {
        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            return realm.where(BookModel.class).findAll();
        }
    }

    public static class AllChapters implements Specification<ChapterModel> {
        @Override
        public RealmResults<ChapterModel> generateResults(Realm realm) {
            return realm.where(ChapterModel.class).findAll();
        }
    }

    public static class AllVerseComponents implements Specification<VerseComponentsModel> {
        @Override
        public RealmResults<VerseComponentsModel> generateResults(Realm realm) {
            return realm.where(VerseComponentsModel.class).findAll();
        }
    }

    public static class BookModelById implements Specification<BookModel> {
        private String bookId;

        public BookModelById(String bookId) {
            this.bookId = bookId;
        }

        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            RealmQuery<BookModel> query = realm.where(BookModel.class);
            query = query.equalTo("bookId", bookId);
            return query.findAll();
        }
    }

}