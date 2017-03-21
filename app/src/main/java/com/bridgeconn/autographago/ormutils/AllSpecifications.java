package com.bridgeconn.autographago.ormutils;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.SearchHistoryModel;
import com.bridgeconn.autographago.models.SearchModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class AllSpecifications {

    public static class AllLanguages implements Specification<LanguageModel> {
        @Override
        public RealmResults<LanguageModel> generateResults(Realm realm) {
            return realm.where(LanguageModel.class).findAllSorted("languageCode");
        }
    }

    public static class LanguageModelByCode implements Specification<LanguageModel> {
        private String code;

        public LanguageModelByCode(String code) {
            this.code = code;
        }

        @Override
        public RealmResults<LanguageModel> generateResults(Realm realm) {
            RealmQuery<LanguageModel> query = realm.where(LanguageModel.class);
            query = query.equalTo("languageCode", code);
            return query.findAll();
        }
    }

    public static class VersionModelByCode implements Specification<VersionModel> {
        private String languageCode, versionCode;

        public VersionModelByCode(String language, String version) {
            this.languageCode = language;
            this.versionCode = version;
        }

        @Override
        public RealmResults<VersionModel> generateResults(Realm realm) {
            RealmQuery<VersionModel> query = realm.where(VersionModel.class);
            query = query.equalTo("languageCode", languageCode);
            query = query.equalTo("versionCode", versionCode);
            return query.findAll();
        }
    }

    public static class BooksByLanguageAndVersion implements Specification<BookModel> {
        private String language, version;

        public BooksByLanguageAndVersion(String language, String version) {
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            RealmQuery<BookModel> query = realm.where(BookModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            return query.findAll().sort("bookNumber");
        }
    }

    public static class AllBooks implements Specification<BookModel> {
        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            return realm.where(BookModel.class).findAll().sort("bookNumber");
        }
    }

    public static class BookModelById implements Specification<BookModel> {
        private String bookId, language, version;

        public BookModelById(String language, String version, String bookId) {
            this.bookId = bookId;
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            RealmQuery<BookModel> query = realm.where(BookModel.class);
            query = query.equalTo("bookPrimaryId", language+"_"+version+"_"+bookId);
            return query.findAll();
        }
    }

    public static class SearchInBookName implements Specification<BookModel> {
        private String text, language, version;

        public SearchInBookName(String text, String language, String version) {
            this.text = text;
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            RealmQuery<BookModel> query = realm.where(BookModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            query = query.contains("bookName", text, Case.INSENSITIVE);
            return query.findAll().sort("bookNumber");
        }
    }

    public static class SearchInVerseComponentsText implements Specification<VerseComponentsModel> {
        private String text, language, version;

        public SearchInVerseComponentsText(String text, String language, String version) {
            this.text = text;
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<VerseComponentsModel> generateResults(Realm realm) {
            RealmQuery<VerseComponentsModel> query = realm.where(VerseComponentsModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            query = query.contains("text", text, Case.INSENSITIVE);
            return query.findAll().distinct("chapterId", "verseNumber");
        }
    }

    public static class AllSearchHistories implements Specification<SearchHistoryModel> {
        @Override
        public RealmResults<SearchHistoryModel> generateResults(Realm realm) {
            return realm.where(SearchHistoryModel.class).findAll();
        }
    }

    public static class SearchHistoryModelByText implements Specification<SearchHistoryModel> {
        private String text;

        public SearchHistoryModelByText(String text) {
            this.text = text;
        }

        @Override
        public RealmResults<SearchHistoryModel> generateResults(Realm realm) {
            RealmQuery<SearchHistoryModel> query = realm.where(SearchHistoryModel.class);
            query = query.equalTo("searchText", text, Case.INSENSITIVE);
            return query.findAllSorted("searchCount", Sort.DESCENDING);
        }
    }

    public static class AllBookmarks implements Specification<BookModel> {
        private String language, version;

        public AllBookmarks(String language, String version) {
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<BookModel> generateResults(Realm realm) {
            RealmQuery<BookModel> query = realm.where(BookModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            query = query.greaterThan("bookmarkChapterNumber", 0);
            return query.findAll();
        }
    }

    public static class AllNotes implements Specification<NotesModel> {
        private String language, version;

        public AllNotes(String language, String version) {
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<NotesModel> generateResults(Realm realm) {
            RealmQuery<NotesModel> query = realm.where(NotesModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            return query.findAll().sort("timestamp", Sort.DESCENDING);
        }
    }

    public static class NotesById implements Specification<NotesModel> {
        private long id;
        private String language, version;

        public NotesById(long id, String language, String version) {
            this.id = id;
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<NotesModel> generateResults(Realm realm) {
            RealmQuery<NotesModel> query = realm.where(NotesModel.class);
            query = query.equalTo("timestamp", id);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            return query.findAll();
        }
    }

    public static class AllHistory implements Specification<SearchModel> {
        private String language, version;

        public AllHistory(String language, String version) {
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<SearchModel> generateResults(Realm realm) {
            RealmQuery<SearchModel> query = realm.where(SearchModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            return query.findAll().sort("timeStamp", Sort.DESCENDING);
        }
    }

    public static class AllHighlights implements Specification<VerseComponentsModel> {
        private String language, version;

        public AllHighlights(String language, String version) {
            this.language = language;
            this.version = version;
        }

        @Override
        public RealmResults<VerseComponentsModel> generateResults(Realm realm) {
            RealmQuery<VerseComponentsModel> query = realm.where(VerseComponentsModel.class);
            query = query.equalTo("languageCode", language);
            query = query.equalTo("versionCode", version);
            query = query.equalTo("highlighted", true);
            return query.findAll().distinct("chapterId", "verseNumber");
        }
    }
}