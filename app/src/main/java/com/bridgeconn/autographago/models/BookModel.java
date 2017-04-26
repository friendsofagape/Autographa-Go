package com.bridgeconn.autographago.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class BookModel extends RealmObject {

    private String bookId;
    @PrimaryKey
    private String bookPrimaryId;
    private String languageCode;
    private String versionCode;
    private String bookName;
    private RealmList<ChapterModel> chapterModels = new RealmList<>();
    @Ignore
    private boolean selected;
//    private int bookmarkChapterNumber;
    private RealmList<RealmInteger> bookmarksList = new RealmList<>();
    private String section;
    private int bookNumber;

    public BookModel(BookModel model) {
        bookId = model.getBookId();
        bookPrimaryId = model.getBookPrimaryId();
        languageCode = model.getLanguageCode();
        versionCode = model.getVersionCode();
        bookName = model.getBookName();
        chapterModels = model.getChapterModels();
        selected = model.isSelected();
//        bookmarkChapterNumber = model.getBookmarkChapterNumber();
        bookmarksList = model.getBookmarksList();
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

    public String getBookPrimaryId() {
        return bookPrimaryId;
    }

    public void setBookPrimaryId(String bookPrimaryId) {
        this.bookPrimaryId = bookPrimaryId;
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

//    public int getBookmarkChapterNumber() {
//        return bookmarkChapterNumber;
//    }
//
//    public void setBookmarkChapterNumber(int bookmarkChapterNumber) {
//        this.bookmarkChapterNumber = bookmarkChapterNumber;
//    }

    public RealmList<RealmInteger> getBookmarksList() {
        return bookmarksList;
    }

    public void setBookmarksList(RealmList<RealmInteger> bookmarksList) {
        this.bookmarksList = bookmarksList;
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
