package com.bridgeconn.autographago.ormutils;

import com.bridgeconn.autographago.models.BookModel;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.NotesModel;
import com.bridgeconn.autographago.models.SearchHistoryModel;
import com.bridgeconn.autographago.models.VerseComponentsModel;
import com.bridgeconn.autographago.models.VersionModel;

public class AllMappers {

    public static class LanguageMapper implements Mapper<LanguageModel, LanguageModel> {
        @Override
        public LanguageModel map(LanguageModel languageModel) {
            return new LanguageModel(languageModel);
        }
    }

    public static class VersionMapper implements Mapper<VersionModel, VersionModel> {
        @Override
        public VersionModel map(VersionModel versionModel) {
            return new VersionModel(versionModel);
        }
    }

    public static class BookMapper implements Mapper<BookModel, BookModel> {
        @Override
        public BookModel map(BookModel bookModel) {
            return new BookModel(bookModel);
        }
    }

    public static class ChapterMapper implements Mapper<ChapterModel, ChapterModel> {
        @Override
        public ChapterModel map(ChapterModel chapterModel) {
            return new ChapterModel(chapterModel);
        }
    }

    public static class VerseComponentsMapper implements Mapper<VerseComponentsModel, VerseComponentsModel> {
        @Override
        public VerseComponentsModel map(VerseComponentsModel verseComponentsModel) {
            return new VerseComponentsModel(verseComponentsModel);
        }
    }

    public static class SearchHistoryMapper implements Mapper<SearchHistoryModel, SearchHistoryModel> {
        @Override
        public SearchHistoryModel map(SearchHistoryModel searchHistoryModel) {
            return new SearchHistoryModel(searchHistoryModel);
        }
    }

    public static class NotesMapper implements Mapper<NotesModel, NotesModel> {
        @Override
        public NotesModel map(NotesModel notesModel) {
            return new NotesModel(notesModel);
        }
    }

}