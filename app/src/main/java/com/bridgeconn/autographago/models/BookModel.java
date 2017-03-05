package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class BookModel extends RealmObject {

    @PrimaryKey
    private String bookId;
    private String languageCode;
    private String versionCode;
    private String bookName;
    private RealmList<ChapterModel> chapterModels = new RealmList<>();
    @Ignore
    private boolean selected;
    private int bookmarkChapterNumber;
    private String section;
    private int bookNumber;

    public BookModel(BookModel model) {
        bookId = model.getBookId();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
        bookName = model.getBookName();
        chapterModels = model.getChapterModels();
        selected = model.isSelected();
        bookmarkChapterNumber = model.getBookmarkChapterNumber();
        section = model.getSection();
        bookNumber = model.getBookNumber();
    }

    public BookModel() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getBookmarkChapterNumber() {
        return bookmarkChapterNumber;
    }

    public void setBookmarkChapterNumber(int bookmarkChapterNumber) {
        this.bookmarkChapterNumber = bookmarkChapterNumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }
}
