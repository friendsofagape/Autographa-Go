package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BookModel extends RealmObject {

    @PrimaryKey
    private String bookId;
    private String bookName;
    private RealmList<ChapterModel> chapterModels = new RealmList<>();

    public BookModel(BookModel model) {
        bookId = model.getBookId();
        bookName = model.getBookName();
        chapterModels = model.getChapterModels();
    }

    public BookModel() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public RealmList<ChapterModel> getChapterModels() {
        return chapterModels;
    }

    public void setChapterModels(RealmList<ChapterModel> chapterModels) {
        this.chapterModels = chapterModels;
    }
}
